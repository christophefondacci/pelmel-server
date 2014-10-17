package com.videopolis.smaug.model;

import java.util.Locale;

import com.videopolis.calm.model.Keyed;

/**
 * A search result item which can have highlighted results.
 * 
 * @author julien
 * @author Shoun Ichida
 * 
 */
public interface SearchItem extends Keyed, Comparable<SearchItem> {

	/** DISTANCE string value */
	public static final String DISTANCE = "distance";
	/** GEO_DISTANCE string value */
	public static final String GEO_DISTANCE = "geo_distance";
	/** RANKING string value */
	public static final String RANKING = "ranking";
	/** SCORE string value */
	public static final String SCORE = "score";

	/**
	 * Returns a statistical information about this item
	 * 
	 * @param type
	 *            Type of desired information
	 * @return The value of the information
	 */
	Number getInfo(String type);

	/**
	 * This method returns the matched result from the text search
	 * 
	 * @return The matched result
	 */
	String getMatchedText();

	/**
	 * This method returns the matched locale for the test search
	 * 
	 * @return The matched locale
	 */
	Locale getMatchedLocale();
}
