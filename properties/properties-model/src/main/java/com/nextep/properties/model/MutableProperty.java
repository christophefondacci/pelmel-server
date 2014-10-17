package com.nextep.properties.model;

import com.videopolis.calm.model.ItemKey;

public interface MutableProperty extends Property {

	void setCode(String propertyCode);

	void setLabel(String propertyLabel);

	void setValue(String propertyValue);

	void setParentItemKey(ItemKey parentItemKey);
}
