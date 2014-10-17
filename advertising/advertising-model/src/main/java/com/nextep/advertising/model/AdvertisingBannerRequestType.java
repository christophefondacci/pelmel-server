package com.nextep.advertising.model;

import com.videopolis.calm.model.RequestType;

/**
 * A specific request type for advertising banners providing access to detailed
 * banner info
 * 
 * @author cfondacci
 * 
 */
public interface AdvertisingBannerRequestType extends RequestType {

	/**
	 * The search type restriction (or <code>null</code> for none)
	 * 
	 * @return the search type
	 */
	String getSearchType();
}
