package com.nextep.advertising.model;

import java.util.Date;

import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.calm.model.Localized;

/**
 * This interface represents a banner for advertising
 * 
 * @author cfondacci
 * 
 */
public interface AdvertisingBanner extends CalmObject, Localized {

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

	/**
	 * Radius of this banner. Banner is eligible to be displayed for someone who
	 * is located at less than <code>radius</code> miles of the banner lat/lng.
	 * 
	 * @return the banner's radius
	 */
	Double getRadius();

	/**
	 * The {@link ItemKey} of the element to which this banner points. A click
	 * on the banner would bring the user to this item key. A banner should have
	 * targetItemKey OR targetUrl, never both.
	 * 
	 * @return the banner's target {@link ItemKey} or <code>null</code> if
	 *         link-based banner
	 */
	ItemKey getTargetItemKey();

	/**
	 * The target URL to which this banner points when a user clicks on it.
	 * 
	 * @return an external URL to which this banner points
	 */
	String getTargetUrl();

	/**
	 * The target number of displays for this banner. Beyond this number, the
	 * banner will stop to be displayed.
	 * 
	 * @return the total number of displays for this banner.
	 */
	long getTargetDisplayCount();

	/**
	 * Provides the status of this banner
	 * 
	 * @return the banner status as a {@link BannerStatus}
	 */
	BannerStatus getStatus();

	/**
	 * Provides the transaction ID of the in-app purchase at the origin of this
	 * banner
	 * 
	 * @return the payment transaction ID
	 */
	String getTransactionId();
}
