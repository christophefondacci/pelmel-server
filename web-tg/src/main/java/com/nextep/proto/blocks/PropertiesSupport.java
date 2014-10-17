package com.nextep.proto.blocks;

import java.util.Collection;
import java.util.List;
import java.util.Locale;

import com.nextep.properties.model.Property;

public interface PropertiesSupport {

	void initialize(Locale l, Collection<? extends Property> properties,
			boolean sort);

	List<Property> getProperties();

	String getPropertyLabel(Property p);

	String getPropertyValue(Property p);
}
