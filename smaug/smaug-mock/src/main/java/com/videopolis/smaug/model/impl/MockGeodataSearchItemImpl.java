package com.videopolis.smaug.model.impl;

import com.tripvisit.model.bean.geodata.Geodata;
import com.videopolis.calm.factory.CalmFactory;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.smaug.model.MockTypes;
import com.videopolis.smaug.model.SearchItem;

/**
 * <p>
 * A mock implementation of {@link SearchItem} for Geodata.
 * </p>
 * <p>
 * This implementation is designed to wrap a {@link Geodata} object
 * </p>
 * 
 * @author julien
 * 
 */
public class MockGeodataSearchItemImpl implements SearchItem {

    /** Wrapped geodata */
    private final Geodata geodata;

    /** This item's key */
    private ItemKey key = null;

    public MockGeodataSearchItemImpl(Geodata geodata) {
	this.geodata = geodata;
    }

    @Override
    public Number getInfo(String type) {
	// Return the distance whatever type is
	return geodata.getDistance();
    }

    @Override
    public ItemKey getKey() {
	if (key == null) {
	    key = CalmFactory.createKey(MockTypes.POI, geodata.getToken());
	}
	return key;
    }

}
