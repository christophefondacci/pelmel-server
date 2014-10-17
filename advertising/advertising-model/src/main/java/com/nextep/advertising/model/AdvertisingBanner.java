package com.nextep.advertising.model;

import java.util.Date;

import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;

/**
 * This interface represents a banner for advertising
 * 
 * @author cfondacci
 * 
 */
public interface AdvertisingBanner extends CalmObject {

	String CAL_ID = "BANR";

	/**
	 * The date when this banner should start to be displayable
	 * 
	 * @return the start validity date
	 */
	Date getStartValidity();

	/**
	 * The date when this banner should stop to be displayable
	 * 
	 * @return the end validity date
	 */
	Date getEndValidity();

	/**
	 * The source HTML code of the banner
	 * 
	 * @return the HTML code to display in the banner section
	 */
	String getBannerHTMLCode();

	/**
	 * The type of banner for proper inclusion.
	 * 
	 * @return the {@link BannerType}
	 */
	BannerType getBannerType();

	/**
	 * The {@link ItemKey} of the owner of the banner (the user who uploaded it)
	 * 
	 * @return the {@link ItemKey} of the purchaser of this banner
	 */
	ItemKey getOwnerItemKey();

	/**
	 * Provides the topmost geographic item key for which this banner should be
	 * shown.
	 * 
	 * @return the {@link ItemKey} of the topmost geographical item
	 */
	ItemKey getTopGeographicItemKey();

	/**
	 * The number of times this banner has been displayed
	 * 
	 * @return the number of time the banner has been displayed
	 */
	int getDisplayCount();

	/**
	 * The number of times a user clicked on the banner
	 * 
	 * @return the number of clicks
	 */
	int getClickCount();

	/**
	 * Any search type restriction
	 * 
	 * @return applicable search type
	 */
	String getSearchType();

	/**
	 * The locale of the banner
	 * 
	 * @return the banner's locale
	 */
	String getLocale();

}
