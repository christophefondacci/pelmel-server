package com.videopolis.cals.model.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.cals.model.MultiKeyItemsResponse;

/**
 * Default implementation of {@link MultiKeyItemsResponse}
 *
 * @author julien
 *
 */
public class MultiKeyItemsResponseImpl implements MultiKeyItemsResponse {

    /** Serialization unique ID */
    private static final long serialVersionUID = 1L;
    private final List<CalmObject> allItems;
    private final Map<ItemKey, List<? extends CalmObject>> keyedItemsMap;
    private Date lastUpdateTimestamp = null;

    /**
     * Default constructor
     */
    public MultiKeyItemsResponseImpl() {
	allItems = new ArrayList<CalmObject>();
	keyedItemsMap = new LinkedHashMap<ItemKey, List<? extends CalmObject>>();
    }

    @Override
    public List<? extends CalmObject> getItemsFor(final ItemKey foreignKey) {
	final List<? extends CalmObject> items = keyedItemsMap.get(foreignKey);
	if (items == null) {
	    return Collections.emptyList();
	}
	return items;
    }

    @Override
    public List<? extends CalmObject> getItems() {
	return allItems;
    }

    /**
     * Sets the items related to an item
     *
     * @param foreignKey
     *            Item key
     * @param items
     *            Items related to the item whose key is {@code foreignKey}
     */
    public void setItemsFor(final ItemKey foreignKey,
	    final List<? extends CalmObject> items) {
	if (items != null) {
	    allItems.addAll(items);
	    keyedItemsMap.put(foreignKey, items);
	}
    }

    @Override
    public Date getLastUpdateTimestamp() {
	return lastUpdateTimestamp;
    }

    @Override
    public void setLastUpdateTimestamp(final Date lastUpdateTimestamp) {
	this.lastUpdateTimestamp = lastUpdateTimestamp;
    }

}
