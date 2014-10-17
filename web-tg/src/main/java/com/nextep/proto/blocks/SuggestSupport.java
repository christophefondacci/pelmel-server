package com.nextep.proto.blocks;


/**
 * Support for pages providing profile edition
 * 
 * @author cfondacci
 * 
 */
public interface SuggestSupport {

	/**
	 * Retrieves the suggest URL
	 * 
	 * @return the Suggest URL for city selection
	 */
	String getSuggestUrl();
}
