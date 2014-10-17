package com.nextep.proto.blocks;

import java.util.Locale;

import com.nextep.geo.model.Admin;
import com.nextep.geo.model.City;
import com.nextep.geo.model.Country;
import com.nextep.geo.model.GeographicItem;
import com.nextep.proto.model.SearchType;
import com.nextep.proto.services.UrlService;
import com.videopolis.apis.model.FacetInformation;
import com.videopolis.calm.model.CalmObject;

/**
 * Support for localization support and geo-breadcrumb features.
 * 
 * @author cfondacci
 * 
 */
public interface LocalizationSupport {

	/**
	 * Initializes the localization from the given geographic element
	 * 
	 * @param currentItem
	 *            the {@link GeographicItem}
	 * @param facetInfo
	 *            the current facetting information
	 */
	void initialize(SearchType searchType, UrlService urlService,
			Locale locale, GeographicItem currentItem,
			FacetInformation facetInfo);

	/**
	 * Provides the continent
	 * 
	 * @return the continent
	 */
	GeographicItem getContinent();

	/**
	 * Provides the country
	 * 
	 * @return the country
	 */
	Country getCountry();

	/**
	 * Provides the ADM1
	 * 
	 * @return the ADM1
	 */
	Admin getAdm1();

	/**
	 * Provides the ADM2
	 * 
	 * @return the ADM2
	 */
	Admin getAdm2();

	/**
	 * Provides the city
	 * 
	 * @return the current city
	 */
	City getCity();

	/**
	 * Provides the current item.
	 * 
	 * @return the current item
	 */
	CalmObject getCurrentItem();

	String getAjaxSearchUrl(GeographicItem item);

	String getSearchUrlForType(String type);

	String getName(GeographicItem item);

	String getSearchCalType();

	SearchType getSearchType();

	String getSearchTypeLabel();

}
