package com.nextep.cal.util.helpers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import com.videopolis.calm.exception.CalException;
import com.videopolis.calm.factory.CalmFactory;
import com.videopolis.calm.helper.Assert;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.calm.model.impl.ExpirableItemKeyImpl;

public final class CalHelper {

	public static final String MULTI_CAL_TYPE = "MULT";
	private static final String MULTI_KEY_SEPARATOR = ";";

	private CalHelper() {
	}

	public static ItemKey getItemKeyFromId(String calType, long id) {
		if (id == 0) {
			return null;
		} else {
			try {
				return CalmFactory.createKey(calType, id);
			} catch (CalException e) {
				throw new RuntimeException(e);
			}
		}
	}

	/**
	 * Creates a multi item key allowing to create a key from several (at least
	 * 2) item keys.
	 * 
	 * @param itemKeys
	 *            {@link ItemKey} to compile in this multi key
	 * @return the multi key that can be deserialized by calling
	 * @throws CalException
	 *             whenever the creation of a multi key cannot be accomplished
	 */
	public static ItemKey createMultiKey(ItemKey... itemKeys)
			throws CalException {
		Assert.moreThan(itemKeys.length, 1,
				"You need at least 2 keys to create a multi cal key");
		final StringBuilder buf = new StringBuilder();
		String separator = "";
		for (ItemKey itemKey : itemKeys) {
			buf.append(separator);
			buf.append(itemKey.toString());
			separator = MULTI_KEY_SEPARATOR;
		}
		return CalmFactory.createKey(MULTI_CAL_TYPE, buf.toString());
	}

	/**
	 * Rebuilds the list of item keys packed into a mutli key.
	 * 
	 * @param multiKey
	 *            the multi {@link ItemKey}
	 * @return the list of contained keys
	 * @throws CalException
	 *             whenever we experienced problem parsing the contained key
	 */
	public static List<ItemKey> extractKeysFromMultiKey(ItemKey multiKey)
			throws CalException {
		if (!MULTI_CAL_TYPE.equals(multiKey.getType())) {
			throw new CalException("A multi key is required to extract keys");
		}
		final String keys = multiKey.getId();
		// Splitting the key via our separator
		final String[] keyTable = keys.split(Matcher
				.quoteReplacement(MULTI_KEY_SEPARATOR));
		final List<ItemKey> itemKeys = new ArrayList<ItemKey>();
		// Parsing every split key
		for (String key : keyTable) {
			final ItemKey itemKey = CalmFactory.parseKey(key);
			// Augmenting our list
			itemKeys.add(itemKey);
		}
		return itemKeys;
	}

	/**
	 * Informs whether the specified key is a multi key
	 * 
	 * @param itemKey
	 *            the {@link ItemKey} to check
	 * @return <code>true</code> if multi key, else <code>false</code>
	 */
	public static boolean isMultiKey(ItemKey itemKey) {
		return MULTI_CAL_TYPE.equals(itemKey.getType());
	}

	/**
	 * Computes the page count from results per page and total items count.
	 * Useful for setup of paginated items response.
	 * 
	 * @param resultsPerPage
	 *            number of results on one page
	 * @param itemsCount
	 *            total number of results found
	 * @return the number of pages it represents
	 */
	public static int getPageCount(int resultsPerPage, int itemsCount) {
		int pages = itemsCount / resultsPerPage;
		int pagesMod = itemsCount % resultsPerPage;
		return pages + (pagesMod > 0 ? 1 : 0);
	}

	/**
	 * Converts a list of {@link ItemKey} into a list of plain string
	 * representation of these item keys.
	 * 
	 * @param itemKeys
	 *            the list of {@link ItemKey} to convert
	 * @return a list of string representation of the same item keys
	 */
	public static Collection<String> unwrapItemKeys(Collection<ItemKey> itemKeys) {
		final List<String> keysStr = new ArrayList<String>();
		for (ItemKey itemKey : itemKeys) {
			if (itemKey != null) {
				keysStr.add(itemKey.toString());
			}
		}
		return keysStr;
	}

	/**
	 * Converts a list of {@link ItemKey} into a list of their ID by stripping
	 * their domain prefix
	 * 
	 * @param itemKeys
	 *            the list of {@link ItemKey} to convert
	 * @return a list of internal ID specific to the domain provider
	 */
	public static Collection<Long> unwrapItemKeyIds(Collection<ItemKey> itemKeys) {
		final List<Long> keyIds = new ArrayList<Long>();
		for (ItemKey itemKey : itemKeys) {
			if (itemKey != null) {
				keyIds.add(itemKey.getNumericId());
			}
		}
		return keyIds;
	}

	public static List<ItemKey> wrapItemKeys(List<String> itemKeysStr)
			throws CalException {
		final List<ItemKey> keys = new ArrayList<ItemKey>();
		for (String itemKeyStr : itemKeysStr) {
			if (itemKeysStr != null) {
				keys.add(CalmFactory.parseKey(itemKeyStr));
			}
		}
		return keys;
	}

	public static String getKeyType(ItemKey itemKey) {
		String type = null;
		if (itemKey != null) {
			type = itemKey.getType();
			if (itemKey instanceof ExpirableItemKeyImpl) {
				type = ((ExpirableItemKeyImpl) itemKey).getBaseItemKey()
						.getType();
			}
		}
		return type;
	}

	public static <T extends CalmObject> Map<String, T> buildItemKeyMap(
			List<? extends T> objects) {
		final Map<String, T> users = new HashMap<String, T>();
		for (T o : objects) {
			users.put(o.getKey().toString(), o);
		}
		return users;
	}
}
