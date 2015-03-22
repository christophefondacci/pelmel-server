package com.nextep.json.model;

/**
 * Common interface for JSON object with a description
 * 
 * @author cfondacci
 *
 */
public interface IJsonDescripted {
	void setDescriptionKey(String descriptionKey);

	String getDescriptionKey();

	void setDescriptionLanguage(String descriptionLanguage);

	String getDescriptionLanguage();

	void setDescription(String description);

	String getDescription();
}
