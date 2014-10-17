package com.nextep.descriptions.dao;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.nextep.cal.util.model.CalDao;
import com.nextep.descriptions.model.Description;
import com.videopolis.calm.model.ItemKey;

/**
 * A small extension to CAL dao providing descriptions-specific features and
 * optimizations.
 * 
 * @author cfondacci
 * 
 */
public interface DescriptionDao extends CalDao<Description> {

	Map<ItemKey, List<Description>> getDescriptionsFor(List<ItemKey> itemKeys,
			Locale locale, boolean oneDescPerKey);

	/**
	 * Clears every description attached to the given parent key
	 * 
	 * @param parentKey
	 */
	void clearDescriptions(ItemKey parentKey, ItemKey... descriptionKeys);

	/**
	 * Lists all descriptions sorted by length.
	 * 
	 * @param ascending
	 *            order of the sort by length
	 * @param maxValues
	 *            maximum number of returned descriptions
	 * @return a list of corresponding descriptions
	 */
	List<Description> listDescriptionsByLength(boolean ascending,
			int maxValues, int startOffset);
}
