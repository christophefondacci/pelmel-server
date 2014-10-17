package com.nextep.proto.blocks;

import java.util.List;
import java.util.Locale;

import com.nextep.properties.model.Property;
import com.videopolis.calm.model.CalmObject;

/**
 * Provides unified support for editing the properties of an object.
 * 
 * @author cfondacci
 * 
 */
public interface PropertiesEditionSupport {

	/**
	 * Initializes the support by providing the parent object which owns the
	 * properties to edit. Note that generic implementation will propose to add
	 * properties even when there is no property in the parent object. It is the
	 * responsibility of the {@link PropertiesEditionSupport} provider to ensure
	 * that it make sense to add / remove / edit properties on the parent
	 * object.
	 * 
	 * @param locale
	 *            the {@link Locale} to use for translations
	 * @param parent
	 *            the {@link CalmObject} containing the properties to edit
	 * @param calType
	 *            the CAL object type that identifies the set of selectable
	 *            properties, used when the parent object is null
	 */
	void initialize(Locale locale, CalmObject parent, String calType);

	/**
	 * Provides the list of properties of the parent, in the order they will be
	 * displayed.
	 * 
	 * @return a list of properties that can be edited
	 */
	List<? extends Property> getProperties();

	/**
	 * Provides the list of available properties
	 * 
	 * @return the list of available properties type, by their code
	 */
	List<String> getAvailablePropertyCodes();

	/**
	 * Provides the label of the specified property code
	 * 
	 * @param propertyCode
	 *            the property code to get the label for
	 * @return the property's user-friendly label
	 */
	String getPropertyLabel(String propertyCode);
}
