package com.nextep.proto.blocks;

import java.util.List;
import java.util.Locale;

import com.nextep.proto.services.UrlService;
import com.videopolis.calm.model.CalmObject;

/**
 * This interface defines the connectors that need to be implemented to support
 * favorites.
 * 
 * @author cfondacci
 * 
 */
public interface FavoritesSupport {

	/**
	 * Initilizes the favorite block
	 * 
	 * @param urlService
	 *            the contextual {@link UrlService}
	 * @param locale
	 *            the {@link Locale} to use for translations
	 * @param parent
	 *            parent {@link CalmObject} for which favorites are displayed
	 * @param favorites
	 *            the list of favorite elements to display in this block
	 */
	void initilialize(UrlService urlService, Locale locale, CalmObject parent,
			List<? extends CalmObject> favorites);

	/**
	 * A setter provided for injecting custom object in the support to provide
	 * additional, non anticipated information.
	 * 
	 * @param metadata
	 *            custom, implementation-specific information
	 */
	void setMetadata(Object metadata);

	/**
	 * Lists all categories for the favorites block. This method should return
	 * at list 1 category for favorites to be displayed. If no category returned
	 * then no favorite will be displayed.
	 * 
	 * @return the list of favorite cateogries.
	 */
	List<Object> getCategories();

	/**
	 * Provides the title of a given category
	 * 
	 * @param category
	 *            the category to get a title for
	 * @return the category title
	 */
	String getCategoryTitle(Object category);

	/**
	 * Provides the CSS-style to use for this category. Should return null for
	 * no-specific category style
	 * 
	 * @param category
	 *            the category to get the CSS style for
	 * @return the CSS style or <code>null</code> if no specific style is
	 *         required
	 */
	String getCategoryStyle(Object category);

	/**
	 * Proivdes the title of the whole favorite box
	 * 
	 * @return the title of the favorite box
	 */
	String getTitle();

	/**
	 * Lists all favorite elements that should be displayed.
	 * 
	 * @return the list of favorite elements as {@link CalmObject}
	 */
	List<? extends CalmObject> getFavorites(String category);

	/**
	 * Provides the name of a single favorite element
	 * 
	 * @param favoriteObj
	 *            the {@link CalmObject} to get the name for
	 * @return the name to display
	 */
	String getFavoriteName(CalmObject favoriteObj);

	/**
	 * A prefix text to add immediately before the name of the favorite and
	 * which is not wrapped by the link.
	 * 
	 * @param favoriteObj
	 *            the favorite {@link CalmObject} to get prefix for
	 * @return the name prefix
	 */
	String getFavoriteNamePrefix(CalmObject favoriteObj);

	/**
	 * Provides the image of a favorite element to display.
	 * 
	 * @param favoriteObj
	 *            the {@link CalmObject} to get the image for
	 * @return the favorite image URL or <code>null</code> if none
	 */
	String getFavoriteImageUrl(CalmObject favoriteObj);

	/**
	 * Provides the link to add on this favorite element
	 * 
	 * @param favoriteObj
	 *            the {@link CalmObject} to get the link of
	 * @return the link to add when displaying this favorite element
	 */
	String getFavoriteLinkUrl(CalmObject favoriteObj);
}
