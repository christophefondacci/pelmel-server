package com.nextep.proto.action.model;

import com.nextep.proto.services.DescriptionsManagementService;

/**
 * This interface specifies methods that an action MUST implement when it
 * provides support for update of descriptions of an element. The provided
 * methods are bound to the form fields name from the origin page which allows
 * user modifications to descriptions through the description edition component.
 * 
 * @author cfondacci
 * 
 */
public interface DescriptionsUpdateAware {

	void setDescriptionLanguageCode(String[] languages);

	String[] getDescriptionLanguageCode();

	void setDescriptionKey(String[] descriptionKeys);

	String[] getDescriptionKey();

	void setDescription(String[] descriptions);

	String[] getDescription();

	void setDescriptionsManagementService(
			DescriptionsManagementService descriptionManagementService);

	String[] getDescriptionSourceId();

	void setDescriptionSourceId(String[] descriptionSourceId);
}
