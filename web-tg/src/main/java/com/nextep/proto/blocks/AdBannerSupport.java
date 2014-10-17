package com.nextep.proto.blocks;

import java.util.List;

import com.nextep.advertising.model.AdvertisingBanner;

/**
 * Provides support for advertising banners
 * 
 * @author cfondacci
 * 
 */
public interface AdBannerSupport {

	void initialize(HeaderSupport headerSupport,
			List<? extends AdvertisingBanner> banners);

	/**
	 * Get a square (typically 200x200) ad banner
	 * 
	 * @return the HTML of the banner
	 */
	String getSquareBannerHtml();

	/**
	 * Get a horizontal leader board banner
	 * 
	 * @return the HTML of this banner
	 */
	String getHorizontalBannerHtml();

	/**
	 * Gets the vertical sky-scrapper banner
	 * 
	 * @return the HTML of this banner
	 */
	String getVerticalBannerHtml();

	/**
	 * Gets the HTML code for a special location
	 * 
	 * @param code
	 *            the internal code of the banner to retrieve
	 * @return the HTML of this banner
	 */
	String getGenericBanner(String code);
}
