package com.nextep.proto.blocks.impl;

import java.util.List;
import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;

import com.nextep.descriptions.model.Description;
import com.nextep.events.model.Event;
import com.nextep.geo.model.Admin;
import com.nextep.geo.model.City;
import com.nextep.geo.model.Continent;
import com.nextep.geo.model.GeographicItem;
import com.nextep.geo.model.Place;
import com.nextep.media.model.Media;
import com.nextep.proto.blocks.HeaderSupport;
import com.nextep.proto.blocks.SearchSupport;
import com.nextep.proto.helpers.DisplayHelper;
import com.nextep.proto.helpers.GeoHelper;
import com.nextep.proto.helpers.LocalizationHelper;
import com.nextep.proto.helpers.MediaHelper;
import com.nextep.proto.helpers.SeoHelper;
import com.nextep.proto.model.Constants;
import com.nextep.proto.model.SearchType;
import com.nextep.proto.services.UrlService;
import com.videopolis.calm.exception.CalException;
import com.videopolis.calm.model.CalmObject;

public class HeaderOverviewSupportImpl implements HeaderSupport {

	private static final Log LOGGER = LogFactory
			.getLog(HeaderOverviewSupportImpl.class);

	private static final String KEY_FACEBOOK_TYPE_PREFIX = "facebook.placeType.";
	private static final String KEY_DESCRIPTION = "header.description.overview";
	private static final String KEY_TYPE_PREFIX = "activity.objType.";
	private static final String KEY_PLACE_TYPE_PREFIX = "place.type.";
	private static final String KEY_TITLE_PREFIX = "header.title.overview";
	private static final String KEY_ITEM_TYPE = "item.type.";

	// Injected information
	private MessageSource messageSource;
	private UrlService urlService;
	private String domainName;

	// Dynamic internal variables
	private Locale locale;
	private CalmObject obj;
	private SearchSupport searchSupport;
	private SearchType searchType;
	private String type;
	private String name;
	private String localization;

	@Override
	public void initialize(Locale locale, CalmObject element,
			SearchSupport searchSupport, SearchType searchType) {
		this.locale = locale;
		this.obj = element;
		this.searchSupport = searchSupport;
		this.searchType = searchType;
		buildValues();
	}

	@Override
	public CalmObject getElement() {
		return obj;
	}

	private void buildValues() {
		type = "";
		// Extracting type of object
		String calType = obj == null ? City.CAL_ID : obj.getKey().getType();
		if (!Place.CAL_TYPE.equals(calType)) {
			// Every geographical element is bound to a city activity
			if (obj instanceof GeographicItem) {
				calType = City.CAL_ID;
			}
			type = messageSource.getMessage(KEY_TYPE_PREFIX + calType, null,
					locale);
		} else {
			type = messageSource.getMessage(KEY_PLACE_TYPE_PREFIX
					+ ((Place) obj).getPlaceType(), null, locale);
		}
		// Building name
		name = DisplayHelper.getName(obj);
		// Building localization of element
		localization = "";
		try {
			City city = null;
			if (obj instanceof Place) {
				city = ((Place) obj).getCity();
			} else {
				// Here the object is not a place so we first try the most
				// precise localization, which is a place
				try {
					final Place place = obj == null ? null : obj
							.getUnique(Place.class);
					// If we find it, we initialize localization with it
					if (place != null) {
						localization = GeoHelper
								.buildShortPlaceLocalizationString(place);
						return;
					}
				} catch (CalException e) {
					LOGGER.error("More than one place when trying to extract localization for "
							+ obj.getKey() + ": " + e.getMessage());
				}
				// Otherwise we try to get a city localization
				city = obj == null ? null : obj.getUnique(City.class);
			}
			if (city != null) {
				localization = GeoHelper.buildShortLocalizationString(city);
			}
		} catch (CalException e) {
			LOGGER.error(
					"Unable to extract city for header meta : "
							+ e.getMessage(), e);
		}
	}

	@Override
	public String getRobotsTags() {
		if (SeoHelper.isSeoBlocked()) {
			return "NOINDEX,NOFOLLOW";
		} else if (obj instanceof Place) {
			// If place is offline the page is no longer indexed
			final Place p = (Place) obj;
			if (!p.isIndexed()) {
				return "NOINDEX,NOFOLLOW";
			} else if (!p.isOnline()) {
				return "NOINDEX,FOLLOW";
			}
		}
		return "INDEX,FOLLOW";
	}

	@Override
	public String getDescription() {
		final Description d = getDescriptionFor(locale.getLanguage());
		if (d != null) {
			return messageSource.getMessage(KEY_DESCRIPTION, new Object[] {
					type, name, localization, " - " + d.getDescription() },
					locale);
		} else if (searchSupport != null) {
			return searchSupport.getSearchTitle();
		} else {
			return messageSource.getMessage(KEY_DESCRIPTION, new Object[] {
					type, name, localization, "" }, locale);
		}
	}

	@Override
	public String getTitle() {
		String title = messageSource.getMessage(KEY_TITLE_PREFIX, new Object[] {
				type, name, localization }, locale);
		return DisplayHelper.titleCased(title);
	}

	@Override
	public String getLanguage() {
		if (LocalizationHelper.isLanguageSupported(locale)) {
			return locale.getLanguage();
		}
		return "en";
	}

	@Override
	public List<String> getAvailableLanguages() {
		return LocalizationHelper.getSupportedLanguages();
	}

	@Override
	public String getCanonical() {
		final String currentLang = locale.getLanguage();
		String canonicalLanguage = null;
		// Looking for at least one description in current language
		final Description d = getDescriptionFor(currentLang);
		// If it exists, we will have the canonical in this language
		if (d != null) {
			canonicalLanguage = currentLang;
		}
		if (canonicalLanguage == null) {
			canonicalLanguage = getAlternateCanonicalLanguage();
		}
		return getAlternate(canonicalLanguage);
	}

	@Override
	public String getAlternate(String language) {
		final Locale locale = new Locale(language);
		String url = null;
		if ((obj instanceof City) || (obj instanceof Admin)
				|| (obj instanceof City) || (obj instanceof Continent)) {
			url = urlService.buildSearchUrl(locale,
					DisplayHelper.getDefaultAjaxContainer(), obj, searchType,
					null, 0);
		} else {
			url = urlService.getOverviewUrl(locale,
					DisplayHelper.getDefaultAjaxContainer(), obj);
		}
		return LocalizationHelper.buildUrl(locale, domainName, url);
	}

	private Description getDescriptionFor(String language) {
		// Looking for the first description in the specified language
		for (Description d : obj.get(Description.class)) {
			if (d.getLocale() != null) {
				final String descLanguage = d.getLocale().getLanguage();
				if (descLanguage.equals(language)) {
					return d;
				}
			}
		}
		return null;
	}

	private String getAlternateCanonicalLanguage() {
		final List<? extends Description> descs = obj.get(Description.class);
		if (descs != null && !descs.isEmpty()) {
			final Locale descLocale = descs.get(0).getLocale();
			if (descLocale != null
					&& LocalizationHelper.isLanguageSupported(descLocale)) {
				return descLocale.getLanguage();
			}
		}
		return Constants.DEFAULT_LANGUAGE;
	}

	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	public void setUrlService(UrlService urlService) {
		this.urlService = urlService;
	}

	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}

	@Override
	public String getFacebookType() {
		if (obj instanceof Place) {
			final Place p = (Place) obj;
			try {
				return messageSource.getMessage(
						KEY_FACEBOOK_TYPE_PREFIX + p.getPlaceType(), null,
						locale);
			} catch (NoSuchMessageException e) {
				// No problem, we don't have a definition, returning default
				// place type
				return "landmark";
			}
		} else if (Event.CAL_ID.equals(obj.getKey().getType())) {
			return "activity";
		} else
			return null;
	}

	@Override
	public String getThumbUrl() {
		final Media m = MediaHelper.getSingleMedia(obj);
		if (m != null) {
			return MediaHelper.getImageUrl(m.getThumbUrl());
		}
		return null;
	}

	@Override
	public String getSearchType() {
		return searchType.name();
	}

	@Override
	public String getPageStyle() {
		// Extracting the current place type through facetting information
		final String placeType = searchType != null ? searchType.getSubtype()
				: null;
		// if we got it, we customize the page with it, otherwise we fallback on
		// root object style
		if (placeType != null) {
			return placeType;
		} else if (searchType != null) {
			return searchType.name().toLowerCase();
		} else {
			return GeoHelper.getPageStyle(obj);
		}
	}

	@Override
	public String getPageTypeLabel() {
		return messageSource.getMessage(KEY_ITEM_TYPE + getPageStyle(), null,
				locale);
	}
}
