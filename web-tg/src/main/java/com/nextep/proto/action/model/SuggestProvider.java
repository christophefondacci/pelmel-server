package com.nextep.proto.action.model;

/**
 * Interface implemented by actions providing suggests proposals
 * 
 * @author cfondacci
 * 
 */
public interface SuggestProvider {

	/**
	 * Provides the JSON string containing proposals
	 * 
	 * @return the JSON proposals
	 */
	String getSuggestionsAsJSON();
}
