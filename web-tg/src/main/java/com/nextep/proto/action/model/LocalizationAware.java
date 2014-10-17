package com.nextep.proto.action.model;

import com.nextep.proto.blocks.LocalizationSupport;

/**
 * Interface that actions should implement when they provide support for
 * localization.
 * 
 * @author cfondacci
 * 
 */
public interface LocalizationAware {

	/**
	 * Access to the {@link LocalizationSupport}
	 * 
	 * @return the {@link LocalizationSupport}
	 */
	LocalizationSupport getLocalizationSupport();

	/**
	 * Injects the localization support implementation
	 * 
	 * @param localizationSupport
	 *            the {@link LocalizationSupport} instance
	 */
	void setLocalizationSupport(LocalizationSupport localizationSupport);
}
