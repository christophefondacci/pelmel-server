package com.nextep.proto.action.model;

import com.nextep.proto.blocks.QuickInfoSupport;

/**
 * Interface implemented by actions which provides Quick-info features
 * 
 * @author cfondacci
 * 
 */
public interface QuickInfoAware {

	/**
	 * Installs the support for Quick-info features to the action
	 * 
	 * @param quickInfoSupport
	 *            the {@link QuickInfoSupport} implementation to install
	 */
	void setQuickInfoSupport(QuickInfoSupport quickInfoSupport);

	/**
	 * Provides the delegate that supports Quick info
	 * 
	 * @return the {@link QuickInfoSupport} implementation
	 */
	QuickInfoSupport getQuickInfoSupport();
}
