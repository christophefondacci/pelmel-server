package com.nextep.proto.blocks.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;

import com.nextep.properties.model.Property;
import com.nextep.proto.blocks.PropertiesSupport;
import com.nextep.proto.model.Constants;

public class PropertiesSupportImpl implements PropertiesSupport {

	private static final Log LOGGER = LogFactory
			.getLog(PropertiesSupportImpl.class);

	private static final String TRANSLATION_PROP_CODE_PREFIX = "property.label.";
	private Locale locale;
	private List<Property> properties;
	private MessageSource messageSource;

	@Override
	public void initialize(Locale l, Collection<? extends Property> properties,
			boolean sort) {
		this.locale = l;
		this.properties = new ArrayList<Property>();
		for (Property p : properties) {
			// Filtering out opening hours (TODO: remove this prop from
			// database)
			if (!Constants.PROPERTY_OPENING_HOUR.equals(p.getCode())) {
				this.properties.add(p);
			}
		}
		if (sort) {
			Collections.sort(this.properties, new Comparator<Property>() {
				@Override
				public int compare(Property o1, Property o2) {
					return getPropertyLabel(o1).compareTo(getPropertyLabel(o2));
				}
			});
		}
	}

	@Override
	public List<Property> getProperties() {
		return properties;
	}

	@Override
	public String getPropertyLabel(Property p) {
		if (p.getCode() != null) {
			try {
				final String label = messageSource.getMessage(
						TRANSLATION_PROP_CODE_PREFIX + p.getCode(), null,
						locale);
				return label;
			} catch (NoSuchMessageException e) {
				LOGGER.warn("Missing translation of property code "
						+ p.getCode());
			}
		}
		return p.getLabel() != null ? p.getLabel() : "";
	}

	@Override
	public String getPropertyValue(Property p) {
		return p.getValue();
	}

	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}
}
