package com.nextep.proto.blocks.impl;

import java.util.List;
import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;

import com.nextep.descriptions.model.Description;
import com.nextep.geo.model.City;
import com.nextep.geo.model.GeographicItem;
import com.nextep.media.model.Media;
import com.nextep.proto.blocks.HeaderSearchSupport;
import com.nextep.proto.blocks.SearchSupport;
import com.nextep.proto.helpers.DisplayHelper;
import com.nextep.proto.helpers.GeoHelper;
import com.nextep.proto.helpers.LocalizationHelper;
import com.nextep.proto.helpers.MediaHelper;
import com.nextep.proto.helpers.SearchHelper;
import com.nextep.proto.helpers.SeoHelper;
import com.nextep.proto.model.Constants;
import com.nextep.proto.model.SearchType;
import com.nextep.proto.services.UrlService;
import com.videopolis.apis.model.FacetInformation;
import com.videopolis.calm.exception.CalException;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.smaug.common.model.Facet;
import com.videopolis.smaug.common.model.FacetCategory;
import com.videopolis.smaug.model.FacetCount;

public class HeaderSearchSupportImpl implements HeaderSearchSupport {

	private static final Log LOGGER = LogFactory
			.getLog(HeaderSearchSupportImpl.class);

	private static final String KEY_FACEBOOK_TYPE_PREFIX = "facebook.search.objType.";
	private static final int THRESHOLD_CANONICAL_MIN_LOCAL_DESCS = 4;
	private static final int THRESHOLD_MAX_FACETS_FOR_CANONICAL = 1;

	private static final String KEY_TITLE_SEARCH_PREFIX = "header.title.search.";
	private static final String KEY_ITEM_TYPE = "item.type.";
	private static final String KEY_DESCRIPTION = "header.description.search";

	private MessageSource messageSource;
	private UrlService urlService;
	private String domainName;
	private boolean nearbySearch;

	private Locale locale;
	private CalmObject obj;
	private SearchSupport searchSupport;
	private SearchType searchType;
	private String description;

	@Override
	public void initialize(Locale locale, CalmObject element,
			SearchSupport searchSupport, SearchType searchType) {
		this.locale = locale;
		this.obj = element;
		this.searchSupport = searchSupport;
		this.searchType = searchType;
	}

	@Override
	public CalmObject getElement() {
		return obj;
	}

	@Override
	public String getRobotsTags() {
		if (SeoHelper.isSeoBlocked()) {
			return "NOINDEX,NOFOLLOW";
		} else if (searchSupport != null) {
			final FacetInformation facetInfo = searchSupport
					.getFacetInformation();

			// If current parent object is larger than a city, we need to
			// consider GEO facetting. When a region or country has only 1
			// element in it, it should not be indexed as it would create
			// duplicate contents
			final String calType = obj.getKey().getType();
			if (!City.CAL_ID.equals(calType)) {
				String robotsMeta = "NOINDEX,NOFOLLOW";
				// Getting the GEO sub facetting category
				final FacetCategory facetCategory = SearchHelper
						.getGeoSubFacettingCategory(obj.getKey());
				// If less than 2 pages, we don't index
				if (searchSupport.getPagesList().size() > 2) {

					final List<FacetCount> facets = facetInfo
							.getFacetCounts(facetCategory);

					// We start to index geo zones when it has at least 4
					// distinct children
					if (facets.size() > 3) {
						robotsMeta = "INDEX,FOLLOW";
					}
				}

				return robotsMeta;
			} else {

				// Nearby searches are un-indexed until Google indexation
				// is fixed
				if (nearbySearch) {
					return "NOINDEX,NOFOLLOW";
				}
				final int page = searchSupport.getCurrentPage();

				int facetCount = 0;
				for (Facet f : facetInfo.getFacetFilters()) {
					if (!f.getFacetCategory()
							.getCategoryCode()
							.equals(SearchHelper.getPlaceTypeCategory()
									.getCategoryCode())) {
						facetCount++;
					}
				}
				if (facetCount < THRESHOLD_MAX_FACETS_FOR_CANONICAL) {
					// We index only if enough results
					if (searchSupport.getResultsCount() >= Constants.MIN_RESULTS_FOR_SEARCH_INDEX) {
						return "INDEX,FOLLOW";
					} else {
						return "NOINDEX,NOFOLLOW";
					}
				} else if (facetCount == THRESHOLD_MAX_FACETS_FOR_CANONICAL) {
					return "INDEX,NOFOLLOW";
				} else {
					return "NOINDEX,NOFOLLOW";
				}
			}
		}
		return "INDEX,FOLLOW";
	}

	@Override
	public String getDescription() {
		if (description == null) {
			// Preparing info for generation of description
			final int count = searchSupport.getResultsCount();
			final String title = getTitle();

			// Appending search results as a comma separated list
			final StringBuilder buf = new StringBuilder();
			String sep = "";
			for (CalmObject object : searchSupport.getSearchResults()) {
				buf.append(sep);
				buf.append(DisplayHelper.getName(object));
				sep = ", ";
			}
			description = messageSource
					.getMessage(
							KEY_DESCRIPTION,
							new Object[] { String.valueOf(count), title,
									buf.toString() }, locale);
		}
		return description;

	}

	@Override
	public String getTitle() {
		// Building localization of element
		String localization = "";
		try {
			if (obj instanceof GeographicItem) {
				localization = GeoHelper.buildShortLocalizationString(obj,
						DisplayHelper.getName(obj));
			} else {
				final City city = obj.getUnique(City.class);
				if (city != null) {
					localization = GeoHelper.buildShortLocalizationString(city);
				}
			}
		} catch (CalException e) {
			LOGGER.error(
					"Unable to extract city for header meta : "
							+ e.getMessage(), e);
		}
		return messageSource.getMessage(
				KEY_TITLE_SEARCH_PREFIX + searchType.name(),
				new Object[] { localization }, locale);

	}

	@Override
	public String getLanguage() {
		if (LocalizationHelper.isLanguageSupported(locale)) {
			return locale.getLanguage();
		}
		return Constants.DEFAULT_LANGUAGE;
	}

	@Override
	public String getCanonical() {
		String language = Constants.DEFAULT_LANGUAGE;
		int localDescCount = getLocalDescriptionsCount(locale.getLanguage());
		if (localDescCount >= THRESHOLD_CANONICAL_MIN_LOCAL_DESCS) {
			language = locale.getLanguage();
		} else {
			for (String lang : LocalizationHelper.getSupportedLanguages()) {
				if (!locale.getLanguage().equals(lang)) {
					localDescCount = getLocalDescriptionsCount(lang);
					if (localDescCount >= THRESHOLD_CANONICAL_MIN_LOCAL_DESCS) {
						language = lang;
						break;
					}
				}
			}
		}
		final FacetInformation facetInfo = searchSupport.getFacetInformation();
		boolean facettedCanonical = (facetInfo.getFacetFilters().size() <= THRESHOLD_MAX_FACETS_FOR_CANONICAL);
		return getAlternate(language, facettedCanonical);
	}

	@Override
	public String getAlternate(String language) {
		return getAlternate(language, true);
	}

	private String getAlternate(String language, boolean facetted) {
		String url = null;
		final Locale locale = new Locale(language);
		final FacetInformation facetInfo = facetted ? searchSupport
				.getFacetInformation() : null;
		url = urlService.buildSearchUrl(locale,
				DisplayHelper.getDefaultAjaxContainer(), obj, searchType,
				facetInfo, 0);
		if (url != null) {
			return LocalizationHelper.buildUrl(language, domainName, url);
		}
		return null;
	}

	@Override
	public List<String> getAvailableLanguages() {
		return LocalizationHelper.getSupportedLanguages();
	}

	private int getLocalDescriptionsCount(String language) {
		int count = 0;
		// For every child of our root geographic point
		for (CalmObject child : obj.get(CalmObject.class)) {
			// We iterate over descriptions
			final List<? extends Description> descs = child
					.get(Description.class);
			if (!descs.isEmpty()) {
				final Locale descLocale = descs.get(0).getLocale();
				if (descLocale != null
						&& descLocale.getLanguage().equals(language)) {
					count++;
				}
			}
		}
		return count;
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
		try {
			return messageSource.getMessage(KEY_FACEBOOK_TYPE_PREFIX
					+ obj.getKey().getType(), null, locale);
		} catch (NoSuchMessageException e) {
			// Returning default facebook type for localities
			return "landmark";
		}
	}

	@Override
	public String getThumbUrl() {
		// Trying to get image even though we know we will not
		// have images for localities (future support)
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
		final String placeType = searchType.getSubtype();
		// if we got it, we customize the page with it, otherwise we fallback on
		// root object style
		if (placeType != null) {
			return placeType;
		} else {
			return searchType.name().toLowerCase();
		}
	}

	@Override
	public String getPageTypeLabel() {
		return messageSource.getMessage(KEY_ITEM_TYPE + getPageStyle(), null,
				locale);
	}

	@Override
	public void setNearbySearch(boolean isNearbySearch) {
		this.nearbySearch = isNearbySearch;
	}

	@Override
	public boolean isNearbySearch() {
		return nearbySearch;
	}

}
