package com.nextep.proto.blocks;

import java.util.List;
import java.util.Locale;

import com.nextep.proto.model.SearchType;
import com.videopolis.calm.model.CalmObject;

/**
 * Support for header and meta information
 * 
 * @author cfondacci
 * 
 */
public interface HeaderSupport {

	/**
	 * Initializes this support
	 * 
	 * @param locale
	 *            current {@link Locale}
	 * @param element
	 *            root element of the page
	 * @param searchSupport
	 *            the {@link SearchSupport} (optional)
	 * @param searchType
	 *            the {@link SearchType} (optional)
	 */
	void initialize(Locale locale, CalmObject element,
			SearchSupport searchSupport, SearchType searchType);

	CalmObject getElement();

	/**
	 * Tags for robot, like INDEX,FOLLOW
	 * 
	 * @return the robots tags
	 */
	String getRobotsTags();

	/**
	 * Provides the META description information
	 * 
	 * @return the META SEO description of the page
	 */
	String getDescription();

	/**
	 * Provides the page title
	 * 
	 * @return
	 */
	String getTitle();

	/**
	 * Provides the URL of a thumb for this page, if available. It will
	 * typically be used with facebook to present the page on likes
	 * 
	 * @return the thumb URL
	 */
	String getThumbUrl();

	/**
	 * The type of elements displayed in the page
	 * 
	 * @return the type of elements in the page
	 */
	String getFacebookType();

	/**
	 * Provides the page language
	 * 
	 * @return the page language
	 */
	String getLanguage();

	/**
	 * Provides every available languages
	 * 
	 * @return the list of available and supported languages
	 */
	List<String> getAvailableLanguages();

	/**
	 * Provides the page canonical URL
	 * 
	 * @return the canonical URL
	 */
	String getCanonical();

	/**
	 * Provides the alternate URL in the given language
	 * 
	 * @param language
	 *            language to get alternate for
	 * @return the alternative URL
	 */
	String getAlternate(String language);

	/**
	 * Provides the name of the search type
	 * 
	 * @return the {@link SearchType}'s name
	 */
	String getSearchType();

	/**
	 * Provides the page CSS-style that customizes the colors
	 * 
	 * @return the CSS page style
	 */
	String getPageStyle();

	/**
	 * Provides the label of the type of items displayed on the page (singular)
	 * 
	 * @return
	 */
	String getPageTypeLabel();

}
