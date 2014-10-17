package com.nextep.proto.blocks.impl;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import com.nextep.properties.model.Property;
import com.nextep.proto.blocks.PropertiesEditionSupport;
import com.nextep.proto.services.PropertiesManagementService;
import com.videopolis.calm.model.CalmObject;

public class PropertiesEditionSupportImpl implements PropertiesEditionSupport {

	private PropertiesManagementService propertiesService;

	private Locale locale;
	private CalmObject parent;
	private String calType;

	@Override
	public void initialize(Locale locale, CalmObject parent, String calType) {
		this.locale = locale;
		this.parent = parent;
		this.calType = calType;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<? extends Property> getProperties() {
		return parent == null ? Collections.EMPTY_LIST : parent
				.get(Property.class);
	}

	@Override
	public List<String> getAvailablePropertyCodes() {
		final List<String> codes = propertiesService
				.getAvailablePropertyCodesFor(calType);
		// Sorting by translation
		Collections.sort(codes, new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				return getPropertyLabel(o1).compareTo(getPropertyLabel(o2));
			}
		});
		return codes;
	}

	@Override
	public String getPropertyLabel(String propertyCode) {
		return propertiesService.getPropertyLabel(propertyCode, locale);
	}

	public void setPropertiesService(
			PropertiesManagementService propertiesService) {
		this.propertiesService = propertiesService;
	}
}
