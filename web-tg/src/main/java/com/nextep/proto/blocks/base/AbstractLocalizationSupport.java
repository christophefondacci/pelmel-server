package com.nextep.proto.blocks.base;

import java.util.List;
import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.MessageSource;

import com.nextep.geo.model.Admin;
import com.nextep.geo.model.AlternateName;
import com.nextep.geo.model.City;
import com.nextep.geo.model.Country;
import com.nextep.geo.model.GeographicItem;
import com.nextep.geo.model.Place;
import com.nextep.proto.blocks.LocalizationSupport;
import com.nextep.proto.helpers.DisplayHelper;
import com.nextep.proto.model.SearchType;
import com.nextep.proto.services.UrlService;
import com.videopolis.apis.model.FacetInformation;
import com.videopolis.calm.model.CalmObject;

public abstract class AbstractLocalizationSupport implements
		LocalizationSupport {
	private static final Log LOGGER = LogFactory
			.getLog(AbstractLocalizationSupport.class);

	private UrlService urlService;
	private MessageSource messageSource;

	private SearchType searchType;
	private FacetInformation facetInfo;
	private GeographicItem continent;
	private Country country;
	private Admin adm1;
	private Admin adm2;
	private City city;
	private CalmObject currentItem;
	private Locale locale;
	private String placeType;

	@Override
	public void initialize(SearchType searchType, UrlService urlService,
			Locale locale, GeographicItem currentItem,
			FacetInformation facetInfo) {
		this.searchType = searchType;
		this.urlService = urlService;
		this.locale = locale;
		this.facetInfo = facetInfo;
		this.currentItem = currentItem;
		if (currentItem instanceof Place) {
			final Place p = (Place) currentItem;
			city = p.getCity();
			adm2 = city.getAdm2();
			adm1 = city.getAdm1();
			country = city.getCountry();
			continent = country.getContinent();
			placeType = p.getPlaceType();
		} else if (currentItem instanceof City) {
			city = (City) currentItem;
			adm2 = city.getAdm2();
			adm1 = city.getAdm1();
			country = city.getCountry();
			continent = country.getContinent();
		} else if (currentItem instanceof Admin) {
			final Admin admin = (Admin) currentItem;
			if (admin.getAdm1() != null) {
				adm2 = admin;
				adm1 = admin.getAdm1();
			} else {
				adm1 = admin;
			}
			country = admin.getCountry();
			continent = country.getContinent();
		} else if (currentItem instanceof Country) {
			country = (Country) currentItem;
			continent = country.getContinent();
		} else {
			continent = currentItem;
		}
	}

	@Override
	public GeographicItem getContinent() {
		return continent;
	}

	@Override
	public Country getCountry() {
		return country;
	}

	@Override
	public Admin getAdm1() {
		return adm1;
	}

	@Override
	public Admin getAdm2() {
		return adm2;
	}

	@Override
	public City getCity() {
		return city;
	}

	@Override
	public CalmObject getCurrentItem() {
		return currentItem;
	}

	protected UrlService getUrlService() {
		return urlService;
	}

	protected FacetInformation getFacetInfo() {
		return facetInfo;
	}

	@Override
	public String getName(GeographicItem item) {
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("Extracting name of " + item.getKey().toString()
					+ " - " + item.getName() + " [locale=" + locale + "] :");
			final List<? extends AlternateName> alternates = item
					.get(AlternateName.class);
			for (AlternateName name : alternates) {
				LOGGER.trace("  - " + name.getLanguage() + " -> "
						+ name.getAlternameName());
			}
		}
		return DisplayHelper.getName(item, locale);
	}

	@Override
	public SearchType getSearchType() {
		return searchType;
	}

	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	protected MessageSource getMessageSource() {
		return messageSource;
	}

	protected Locale getLocale() {
		return locale;
	}
}
