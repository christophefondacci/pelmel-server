package com.videopolis.calm.model.impl;

import com.videopolis.calm.exception.CalException;
import com.videopolis.calm.factory.CalmFactory;
import com.videopolis.calm.model.ItemKey;

/**
 * An {@link ItemKey} that wraps an existing item key with an expiration date
 * 
 * @author cfondacci
 *
 */
public class ExpirableItemKeyImpl extends ItemKeyImpl {
	private static final long serialVersionUID = 1L;
	public static final String CAL_TYPE = "COMP";

	/**
	 * Builds a new item key by combining a base item key with an expiration
	 * timestamp
	 * 
	 * @param baseItemKey
	 *            the base {@link ItemKey}
	 * @param timestamp
	 *            the expiration time
	 */
	public ExpirableItemKeyImpl(ItemKey baseItemKey, long timestamp) {
		super();
		setType(CAL_TYPE);
		setId(baseItemKey.toString() + "-" + String.valueOf(timestamp));
	}

	public ExpirableItemKeyImpl(String id) {
		setType(CAL_TYPE);
		setId(id);
	}

	public ItemKey getBaseItemKey() {
		final String[] compounds = getId().split("-");
		try {
			return CalmFactory.parseKey(compounds[0]);
		} catch (CalException e) {
			throw new RuntimeException(
					"Unable to extract base itemKey from expirable item key: "
							+ e.getMessage(), e);
		}
	}

	public long getExpirationTime() {
		final String[] compounds = getId().split("-");
		return Long.valueOf(compounds[1]);
	}
}
