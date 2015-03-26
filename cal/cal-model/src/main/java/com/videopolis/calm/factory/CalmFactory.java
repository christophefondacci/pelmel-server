package com.videopolis.calm.factory;

import com.videopolis.calm.exception.CalException;
import com.videopolis.calm.helper.Assert;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.calm.model.impl.ExpirableItemKeyImpl;
import com.videopolis.calm.model.impl.ItemKeyImpl;

/**
 * A factory used to create instances of {@link ItemKey}
 *
 * @author julien
 *
 */
public final class CalmFactory {
	/** Size of the type section of an {@link ItemKey} */
	public static final int KEY_TYPE_LENGTH = 4;
	/** Maximum size of the id part of an {@link ItemKey} */
	public static final int KEY_ID_MAX_LENGTH = 60;

	private CalmFactory() {
	}

	/**
	 * Creates a new {@link ItemKey} instance from a type / id pair.
	 *
	 * @param itemType
	 *            type of the item
	 * @param itemId
	 *            identifier of the typed item
	 * @return a new {@link ItemKey} instance
	 */
	public static ItemKey createKey(final String itemType, final Long itemId)
			throws CalException {
		Assert.notNull(itemId);
		return createKey(itemType, itemId.toString());
	}

	/**
	 * Creates a new {@link ItemKey} instance from a type / id pair.
	 *
	 * @param itemType
	 *            type of the item
	 * @param itemId
	 *            identifier of the typed item
	 * @return a new {@link ItemKey} instance
	 */
	public static ItemKey createKey(final String itemType, final String itemId)
			throws CalException {
		Assert.notNull(itemId);
		Assert.notNull(itemType);
		Assert.equals(itemType.length(), KEY_TYPE_LENGTH, "Item key type ["
				+ itemType + "] do not comply with rule: item type must be "
				+ KEY_TYPE_LENGTH + " characters");
		Assert.lessThan(itemId.length(), KEY_ID_MAX_LENGTH,
				"Item key id must be encoded on less than " + KEY_ID_MAX_LENGTH
						+ " characters");
		if (itemType.equals(ExpirableItemKeyImpl.CAL_TYPE)) {
			return new ExpirableItemKeyImpl(itemId);
		} else {
			final ItemKey key = new ItemKeyImpl();
			key.setType(itemType);
			key.setId(itemId);
			return key;
		}
	}

	/**
	 * Constructs an item key from its string representation.
	 *
	 * @param key
	 *            string representation of an {@link ItemKey}
	 * @return the corresponding key
	 * @throws CalException
	 *             when the provided key string is not parsable as an item key
	 */
	public static ItemKey parseKey(final String key) throws CalException {
		Assert.notNull(key);
		Assert.moreThan(key.length(), KEY_TYPE_LENGTH,
				"An item key must contain a type and an identifier, this key seems too short");
		final String type = key.substring(0, 4);
		final String id = key.substring(4);
		return createKey(type, id);
	}
}
