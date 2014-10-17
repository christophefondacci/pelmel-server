package com.nextep.proto.services;

import com.nextep.proto.exceptions.GenericWebappException;
import com.nextep.users.model.User;
import com.videopolis.calm.model.CalmObject;

/**
 * This service provides common methods to manage and store descriptions of any
 * element.
 * 
 * @author cfondacci
 * 
 */
public interface DescriptionsManagementService {

	boolean updateDescriptions(User author, CalmObject parent,
			String[] descriptionLanguageCodes, String[] descriptionItemKeys,
			String[] descriptions, String[] descriptionSourceIds)
			throws GenericWebappException;

	boolean updateDescriptions(User author, String descFieldCode,
			CalmObject parent, String[] descriptionLanguageCodes,
			String[] descriptionItemKeys, String[] descriptions,
			String[] descriptionSourceIds) throws GenericWebappException;

	boolean updateSingleDescription(User author, CalmObject parent,
			String[] descriptionLanguageCodes, String[] descriptionItemKeys,
			String[] descriptions, String[] descriptionSourceIds)
			throws GenericWebappException;
}
