package com.nextep.proto.action.model;

import com.nextep.proto.blocks.PropertiesEditionSupport;

/**
 * An interface that should be implemented by any action offering to edit /
 * update an element's properties.
 * 
 * @author cfondacci
 * 
 */
public interface PropertiesEditionAware {

	/**
	 * Provides the edition support
	 * 
	 * @return the {@link PropertiesEditionSupport}
	 */
	PropertiesEditionSupport getPropertiesEditionSupport();

	/**
	 * Defines the edition support
	 * 
	 * @param propertiesEditionSupport
	 *            the {@link PropertiesEditionSupport} to use
	 */
	void setPropertiesEditionSupport(
			PropertiesEditionSupport propertiesEditionSupport);
}
