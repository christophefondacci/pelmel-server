package com.nextep.advertising.model;

import java.util.Date;

import com.videopolis.calm.model.ItemKey;

/**
 * The mutable interface providing accessors to modify banner information
 * 
 * @author cfondacci
 * 
 */
public interface MutableAdvertisingBanner extends AdvertisingBanner {

	/**
	 * Sets the date when the banner should start to be displayed
	 * 
	 * @param startValidity
	 */
	void setStartValidity(Date startValidity);

	/**
	 * Sets the date when the banner should no longer be displayed
	 * 
	 * @param endValidity
	 */
	void setEndValidity(Date endValidity);

	/**
	 * Sets the banner HTML code
	 * 
	 * @param html
	 *            the source code of the banner
	 */
	void setBannerHTMLCode(String html);

	/**
	 * Defines the type of this banner
	 * 
	 * @param bannerType
	 *            the {@link BannerType} of this banner
	 */
	void setBannerType(BannerType bannerType);

	/**
	 * Defines the owner of this banner
	 * 
	 * @param ownerItemKey
	 *            the {@link ItemKey} of the banner's admin
	 */
	void setOwnerItemKey(ItemKey ownerItemKey);

	/**
	 * Defines the number of times this banner has been displayed
	 * 
	 * @param displayCount
	 *            the number of times this banner has been displayed
	 */
	void setDisplayCount(int displayCount);

	/**
	 * Defines the number of times the banner has been clicked
	 * 
	 * @param clickCount
	 *            number of clicks
	 */
	void setClickCount(int clickCount);

	/**
	 * Defines the unique ID of the top-most geographical item where this banner
	 * should be displayed.
	 * 
	 * @param itemKey
	 *            the {@link ItemKey} of the geographical region where the
	 *            banner should be displayed
	 */
	void setTopGeographicItemKey(ItemKey itemKey);

	/**
	 * Defines the search type for which this banner applies
	 * 
	 * @param searchType
	 *            the new search type
	 */
	void setSearchType(String searchType);

	/**
	 * Defines the locale where this banner should be displayed
	 * 
	 * @param locale
	 */
	void setLocale(String locale);

	/**
	 * Defines the latitude of the center of the display range of this banner
	 * 
	 * @param latitude
	 *            the banner's display center latitude
	 */
	void setLatitude(double latitude);

	/**
	 * Defines the longitude of the center of the display range of this banner
	 * 
	 * @param longitude
	 *            the banner's display center latitude
	 */
	void setLongitude(double longitude);

	/**
	 * Defines the radius of this banner (the range where this banner will be
	 * displayed around the lat/lng)
	 * 
	 * @param radius
	 *            the display radius
	 */
	void setRadius(double radius);

	/**
	 * Defines the target item as the landing "page" of this banner
	 * 
	 * @param targetItemKey
	 */
	void setTargetItemKey(ItemKey targetItemKey);

	/**
	 * Defines the target landing URL of that banner. Only one of targetItemKey
	 * / targetUrl should be defined.
	 * 
	 * @param url
	 *            the target landing URL (for external banner)
	 */
	void setTargetUrl(String url);

	/**
	 * Defines the maximum number of impressions to display, above this number
	 * the banner will not be displayeD.
	 * 
	 * @param targetDisplayCount
	 *            the total number of impressions.
	 */
	void setTargetDisplayCount(long targetDisplayCount);
}
