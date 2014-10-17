package com.nextep.proto.action.model;

import com.nextep.proto.blocks.PopularSupport;

/**
 * Defines an action supporting popular elements in its header bar.
 * 
 * @author cfondacci
 * 
 */
public interface HeaderPopularAware {

	/**
	 * Exposes the installed support
	 * 
	 * @return the {@link PopularSupport} implementation installed
	 */
	PopularSupport getHeaderPopularSupport();

	/**
	 * Intalls the popular support in the action
	 * 
	 * @param headerPopularSupport
	 *            the {@link PopularSupport} implementation to install
	 */
	void setHeaderPopularSupport(PopularSupport headerPopularSupport);
}
