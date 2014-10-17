package com.nextep.proto.blocks;

import java.util.List;
import java.util.Locale;

import com.nextep.tags.model.Tag;
import com.videopolis.calm.model.CalmObject;

/**
 * Interface for {@link Tag} support
 * 
 * @author cfondacci
 * 
 */
public interface TagSupport {

	/**
	 * Initializes the Tag support for the specified locale
	 * 
	 * @param locale
	 *            locale to use for translations
	 * @param availableTags
	 *            an optional list of available tags to use with pages that
	 *            prompts tagging features to the user
	 */
	void initialize(Locale locale, List<Tag> availableTags);

	/**
	 * Retrieves the tag's translation
	 * 
	 * @param tag
	 *            the {@link Tag} to translate.
	 * 
	 * @return the translation of the given tag
	 */
	String getTagTranslation(Tag tag);

	/**
	 * Retrieves the URL of the tag icon
	 * 
	 * @param tag
	 *            the {@link Tag} to get the URL for
	 * @return the URL of the icon for the given {@link Tag}
	 */
	String getTagIconUrl(Tag tag);

	/**
	 * Lists all tags available for selection
	 * 
	 * @return the list of available {@link Tag}
	 */
	List<Tag> getAvailableTags();

	/**
	 * Provides the tags of the specified element
	 * 
	 * @param o
	 *            the object to get tags for
	 * @return list of tags
	 */
	List<? extends Tag> getTags(CalmObject o);

}
