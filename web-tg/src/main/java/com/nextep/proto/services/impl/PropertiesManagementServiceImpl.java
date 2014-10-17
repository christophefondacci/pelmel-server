package com.nextep.proto.services.impl;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.context.MessageSource;

import com.nextep.cal.util.services.CalPersistenceService;
import com.nextep.geo.model.Place;
import com.nextep.properties.model.MutableProperty;
import com.nextep.properties.model.Property;
import com.nextep.proto.exceptions.GenericWebappException;
import com.nextep.proto.services.PropertiesManagementService;
import com.nextep.proto.services.PuffService;
import com.nextep.proto.spring.ContextHolder;
import com.nextep.users.model.User;
import com.videopolis.calm.exception.CalException;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;

public class PropertiesManagementServiceImpl implements
		PropertiesManagementService {

	private static final String KEY_PROPERTY_LABEL_PREFIX = "property.label.";
	private final Map<String, List<String>> codesTypeMap;
	private MessageSource messageSource;
	private CalPersistenceService propertiesService;
	private PuffService puffService;

	public PropertiesManagementServiceImpl() {
		codesTypeMap = new HashMap<String, List<String>>();
		codesTypeMap.put(Place.CAL_TYPE,
				Arrays.asList("website", "openingHours", "phone"));
		codesTypeMap.put(User.CAL_TYPE, Arrays.asList("trips", "dick", "sport",
				"hobby", "love", "lookfor", "position"));
	}

	@Override
	public List<String> getAvailablePropertyCodesFor(String calType) {
		final List<String> codes = codesTypeMap.get(calType);
		if (codes != null) {
			return codes;
		} else {
			return Collections.emptyList();
		}
	}

	@Override
	public String getPropertyLabel(String propertyCode, Locale l) {
		return messageSource.getMessage(KEY_PROPERTY_LABEL_PREFIX
				+ propertyCode, null, l);
	}

	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	@Override
	public boolean updateProperties(User author, CalmObject parent,
			Locale locale, String[] propertyKeys, String[] propertyCodes,
			String[] propertyValues) throws GenericWebappException {
		final List<? extends Property> properties = parent.get(Property.class);
		// Hashing properties by their key for easy lookup
		final Map<String, Property> propertiesKeyMap = new HashMap<String, Property>();
		for (Property p : properties) {
			propertiesKeyMap.put(p.getKey().toString(), p);
		}
		// Checking that we got same number of arguments in each array
		if (propertyKeys != null && propertyCodes != null
				&& propertyKeys.length != propertyCodes.length
				|| propertyKeys.length != propertyValues.length) {
			throw new GenericWebappException(
					"Cannot update properties: incorrect number of arguments - keys:"
							+ propertyKeys.length + " vs codes:"
							+ propertyCodes.length + " vs values:"
							+ propertyValues.length);
		}
		// Processing
		boolean propertiesChanged = false;
		final ItemKey parentKey = parent.getKey();
		// Clearing properties
		try {
			propertiesService.setItemFor(parentKey);
		} catch (CalException e) {
			throw new GenericWebappException(
					"Unable to update user properties: " + e.getMessage(), e);
		}
		ContextHolder.toggleWrite();
		for (int i = 0; i < propertyCodes.length; i++) {
			final String key = propertyKeys[i];
			final String code = propertyCodes[i];
			final String value = propertyValues[i];
			if (!"0".equals(code)) {
				MutableProperty property = null;
				boolean hasChanged = false;
				if (key != null && !"0".equals(key)) {
					property = (MutableProperty) propertiesKeyMap.get(key);
					hasChanged = !property.getCode().equals(code);
				} else {
					property = (MutableProperty) propertiesService
							.createTransientObject();
				}
				hasChanged |= puffService.log(parentKey, code,
						hasChanged ? null : property.getValue(), value, null,
						author);
				propertiesChanged |= hasChanged;
				property.setCode(code);
				property.setValue(value);
				property.setParentItemKey(parentKey);
				propertiesService.saveItem(property);
			}
		}
		return propertiesChanged;
	}

	public void setPropertiesService(CalPersistenceService propertiesService) {
		this.propertiesService = propertiesService;
	}

	public void setPuffService(PuffService puffService) {
		this.puffService = puffService;
	}
}
