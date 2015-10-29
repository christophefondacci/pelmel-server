package com.videopolis.smaug.model;

/**
 * An extension of {@link SearchWindow} for search response. It provides a count
 * of all the items found in the results.
 * 
 * @author julien
 * 
 */
public interface SearchWindowResponse extends SearchWindow {

	/**
	 * Returns the count of all the items found in the result
	 * 
	 * @return Item count
	 */
	int getItemCount();

}
