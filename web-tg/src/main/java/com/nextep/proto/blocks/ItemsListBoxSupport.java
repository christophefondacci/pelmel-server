package com.nextep.proto.blocks;

import java.util.List;
import java.util.Locale;

import com.nextep.proto.services.UrlService;
import com.videopolis.calm.model.CalmObject;

/**
 * Interface to implement to support a box that lists item with a title, a
 * description and an icon.
 * 
 * @author cfondacci
 * 
 */
public interface ItemsListBoxSupport {

	void initialize(UrlService urlService, Locale locale, CalmObject parent,
			List<? extends CalmObject> items);

	/**
	 * Global box title
	 * 
	 * @return the title of this box
	 */
	String getBoxTitle();

	/**
	 * Returns the list of items of this box
	 * 
	 * @return items to display
	 */
	List<? extends CalmObject> getItems();

	/**
	 * Returns the title of this element in the list
	 * 
	 * @param item
	 *            the {@link CalmObject} to get the title of
	 * @return the item's title
	 */
	String getItemTitle(CalmObject item);

	/**
	 * Gets a prefix to display immediatly before the item title, without being
	 * encapsulated by the title link (a tag)
	 * 
	 * @param item
	 *            item to get the title prefix for
	 * @return the title prefix or null / empty string if not applicable
	 */
	String getItemTitlePrefix(CalmObject item);

	/**
	 * Returns the description of this element in the list
	 * 
	 * @param item
	 *            the {@link CalmObject} to get the description of
	 * @return the item's description
	 */
	String getItemDescription(CalmObject item);

	/**
	 * The URL of the image for this element
	 * 
	 * @param item
	 *            the {@link CalmObject} to get the icon URL of
	 * @return the icon's URL of this item
	 */
	String getItemIconUrl(CalmObject item);

	/**
	 * The URL of the item
	 * 
	 * @param item
	 *            the {@link CalmObject} to get the link of
	 * @return the item's link
	 */
	String getItemUrl(CalmObject item);

	String getActionIconUrl();

	String getActionUrl();

	String getActionText();

	/**
	 * Provides the language HTML attribute (lang="en") to specify for the item.
	 * If nothing specific to declare, leave null
	 * 
	 * @param item
	 *            the {@link CalmObject} representing the item to generate a
	 *            language attribute for
	 * @return the language attribute or null / empty if nothing to declare
	 */
	String getLangAttribute(CalmObject item);
}
