package com.nextep.proto.action.model;

import com.nextep.proto.blocks.ChildSupport;

/**
 * Represents an element which provides a connector to add children
 * 
 * @author cfondacci
 * 
 */
public interface Childable {

	/**
	 * Provides the {@link ChildSupport}
	 * 
	 * @return the {@link ChildSupport} implementation
	 */
	ChildSupport getChildSupport();

	/**
	 * Installs the child support.
	 * 
	 * @param childSupport
	 *            the {@link ChildSupport} implementation to install
	 */
	void setChildSupport(ChildSupport childSupport);
}
