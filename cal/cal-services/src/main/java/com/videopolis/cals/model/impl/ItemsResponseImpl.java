package com.videopolis.cals.model.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.videopolis.calm.model.CalmObject;
import com.videopolis.cals.model.ItemsResponse;

/**
 * Default implementation of {@link ItemsResponse}
 *
 * @author julien
 *
 */
public class ItemsResponseImpl implements ItemsResponse {

    /** Serialization UID */
    private static final long serialVersionUID = 1L;
    private List<? extends CalmObject> items;
    private Date lastUpdate;

    /**
     * Default constructor
     */
    public ItemsResponseImpl() {
	items = new ArrayList<CalmObject>();
    }

    @Override
    public List<? extends CalmObject> getItems() {
	return items;
    }

    /**
     * Adds a {@link CalmObject} to this response
     *
     * @param item
     *            item to add to this response
     */
    @SuppressWarnings("unchecked")
    public void addItem(final CalmObject item) {
	// The eclipse compiler does not support items.add(item) while javac
	// does. Workaround is to explicitly cast our collection to something
	// Eclipse consider valid. => Removing resulting (wrong) unchecked
	// warning
	((Collection<CalmObject>) items).add(item);
    }

    /**
     * Defines all the items that this response will return
     *
     * @param items
     *            a collection of {@link CalmObject} to put in this response
     */
    public void setItems(final List<? extends CalmObject> items) {
	this.items = items;
    }

    @Override
    public Date getLastUpdateTimestamp() {
	return lastUpdate;
    }

    @Override
    public void setLastUpdateTimestamp(final Date lastUpdateTimestamp) {
	lastUpdate = lastUpdateTimestamp;
    }
}
