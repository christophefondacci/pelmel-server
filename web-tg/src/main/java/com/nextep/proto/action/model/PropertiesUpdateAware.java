package com.nextep.proto.action.model;

import com.nextep.proto.services.PropertiesManagementService;

/**
 * This interface specifies the method that an action must implement if it
 * provides support of properties update. Methods are bound to the names of the
 * input fields of the properties edition component.
 * 
 * @author cfondacci
 * 
 */
public interface PropertiesUpdateAware {

	/**
	 * The array of all property codes to update
	 * 
	 * @param propertyCode
	 *            the property codes array
	 */
	void setPropertyCode(String[] propertyCode);

	/**
	 * Provides the array of all property codes from the source page form
	 * 
	 * @return the array of property codes
	 */
	String[] getPropertyCode();

	/**
	 * The array of all properties values to update
	 * 
	 * @param propertyValue
	 *            the array of property values
	 */
	void setPropertyValue(String[] propertyValue);

	String[] getPropertyValue();

	void setPropertyKey(String[] propertyKey);

	String[] getPropertyKey();

	void setPropertiesManagementService(PropertiesManagementService propertiesService);
}