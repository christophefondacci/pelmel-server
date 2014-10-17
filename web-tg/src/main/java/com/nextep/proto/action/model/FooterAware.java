package com.nextep.proto.action.model;

import com.nextep.proto.blocks.PopularSupport;

/**
 * Interfacxe defining actions that could fully support footer. It basically
 * provides a secondary popular support for bottom right part of the footer
 * 
 * @author cfondacci
 * 
 */
public interface FooterAware extends PopularAware {

	/**
	 * Provides the support for the right footer part
	 * 
	 * @return the secondary {@link PopularSupport}
	 */
	PopularSupport getSecondaryPopularSupport();

	/**
	 * Installs the support for the right footer part
	 * 
	 * @param popularSupport
	 *            the secondary {@link PopularSupport} implementation that will
	 *            provide right elements
	 */
	void setSecondaryPopularSupport(PopularSupport popularSupport);
}
