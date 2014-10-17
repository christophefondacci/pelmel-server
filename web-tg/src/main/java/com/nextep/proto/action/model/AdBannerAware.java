package com.nextep.proto.action.model;

import com.nextep.proto.blocks.AdBannerSupport;

/**
 * This interface describes any element which supports installation of the
 * advertising banner module.
 * 
 * @author cfondacci
 * 
 */
public interface AdBannerAware {

	/**
	 * Provides the support for advertising banners
	 * 
	 * @return the {@link AdBannerSupport} implementation installed
	 */
	AdBannerSupport getAdBannerSupport();

	/**
	 * Installs the support for advertising banners
	 * 
	 * @param adSupport
	 *            the {@link AdBannerSupport} implementation to install
	 */
	void setAdBannerSupport(AdBannerSupport adSupport);
}
