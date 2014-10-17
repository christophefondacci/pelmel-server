package com.videopolis.smaug.model.impl;

import org.apache.solr.client.solrj.beans.Field;

import com.videopolis.calm.factory.CalmFactory;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.smaug.model.MockTypes;
import com.videopolis.smaug.model.SearchItem;

/**
 * <p>
 * A mock implementation of {@link SearchItem} for Hotels.
 * </p>
 * <p>
 * This implementation is designed to be used as a bean with solrj
 * </p>
 * 
 * @author julien
 * 
 */
public class MockHotelSearchItemImpl implements SearchItem {

    /** The hotel id */
    @Field
    private String id;

    /** The distance of the hotel from the search point */
    @Field("geo_distance")
    private String distance;

    private ItemKey key = null;

    @Override
    public Number getInfo(String type) {
	// Return the distance, whatever the type is
	return Double.valueOf(distance);
    }

    @Override
    public ItemKey getKey() {
	if (key == null) {
	    key = CalmFactory.createKey(MockTypes.HOTEL, id);
	}
	return key;
    }

}
