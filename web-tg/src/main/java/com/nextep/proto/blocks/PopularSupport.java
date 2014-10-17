package com.nextep.proto.blocks;

import java.util.List;
import java.util.Locale;

import com.nextep.proto.model.SearchType;
import com.nextep.proto.services.UrlService;
import com.videopolis.calm.model.CalmObject;

public interface PopularSupport {

	/**
	 * Initializes a popular support component
	 * 
	 * @param searchType
	 *            the current {@link SearchType}
	 * @param locale
	 *            locale to use for text translations
	 * @param urlService
	 *            {@link UrlService} to use for URL generation
	 * @param parent
	 *            optional parent element for title generation
	 * @param popularElements
	 *            list of popular elements to display
	 * @param countObject
	 *            a custom object holding popular info
	 */
	void initialize(SearchType searchType, Locale locale,
			UrlService urlService, CalmObject parent,
			List<? extends CalmObject> popularElements, Object countObject);

	/**
	 * Retrieves the list of popular elements
	 * 
	 * @return the list of popular elements in the order they should be
	 *         displayed
	 */
	List<? extends CalmObject> getPopularElements();

	/**
	 * Retrieves the name of the popular element
	 * 
	 * @param element
	 *            the popular {@link CalmObject} to display
	 * @return the name of the popular element
	 */
	String getName(CalmObject element);

	/**
	 * An optional count to put next to the popular element.
	 * 
	 * @param element
	 *            the {@link CalmObject} to get the count for
	 * @return the element count or 0 if non applicable
	 */
	int getCount(CalmObject element);

	/**
	 * Retrieves the URL that the popular element should link to
	 * 
	 * @param element
	 *            the popular {@link CalmObject} element
	 * @return the URL
	 */
	String getUrl(CalmObject element);

	/**
	 * Retrieves the URL of the icon to display with this popular element
	 * 
	 * @param element
	 *            the popular {@link CalmObject} element
	 * @return the URL of the icon of the specified element
	 */
	String getIconUrl(CalmObject element);

	/**
	 * Retrieves the title of the popular box component
	 * 
	 * @return the box title
	 */
	String getTitle();
}
