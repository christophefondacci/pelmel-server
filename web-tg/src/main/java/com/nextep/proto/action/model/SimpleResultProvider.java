package com.nextep.proto.action.model;

/**
 * The interface for simple static page with title and description
 * 
 * @author cfondacci
 *
 */
public interface SimpleResultProvider {

	/**
	 * The title of the page
	 * 
	 * @return the title of the page
	 */
	String getPageTitle();

	/**
	 * The text of the page
	 * 
	 * @return the text of the page
	 */
	String getPageText();

}
