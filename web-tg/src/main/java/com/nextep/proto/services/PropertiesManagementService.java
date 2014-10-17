package com.nextep.proto.services;

import java.util.List;
import java.util.Locale;

import com.nextep.proto.exceptions.GenericWebappException;
import com.nextep.users.model.User;
import com.videopolis.calm.model.CalmObject;

public interface PropertiesManagementService {

	List<String> getAvailablePropertyCodesFor(String calType);

	String getPropertyLabel(String propertyCode, Locale l);

	boolean updateProperties(User author, CalmObject parent, Locale locale,
			String[] propertyKeys, String[] propertyCodes,
			String[] propertyValues) throws GenericWebappException;
}
