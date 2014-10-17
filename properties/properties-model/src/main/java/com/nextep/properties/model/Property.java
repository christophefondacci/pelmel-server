package com.nextep.properties.model;

import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;

/**
 * A property is a piece of information regarding a given element expressed as a
 * key / value, or as label / value pair.
 * 
 * @author cfondacci
 * 
 */
public interface Property extends CalmObject {

	String CAL_TYPE = "PROP";

	/**
	 * Provides the unique identifier of the element described by this property
	 * 
	 * @return the {@link ItemKey} of the property's owner
	 */
	ItemKey getParentItemKey();

	/**
	 * The property code, might be null for custom properties (in which case a
	 * label would be provided)
	 * 
	 * @return the property's unique code
	 */
	String getCode();

	/**
	 * The label of this property, might be null for generic properties
	 * providing a code
	 * 
	 * @return the property's label
	 */
	String getLabel();

	/**
	 * The value of the property
	 * 
	 * @return the value expressed as a generic string
	 */
	String getValue();

}
