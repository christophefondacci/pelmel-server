package com.videopolis.calm.factory;

import org.junit.Assert;
import org.junit.Test;

import com.videopolis.calm.exception.CalException;
import com.videopolis.calm.model.ItemKey;

public class CalmFactoryTest {

    private static final String ITEM_TYPE = "TYPE";
    private static final long LONG_ITEM_ID = 42;
    private static final String LONG_ITEM_ID_AS_STRING = "42";
    private static final String STRING_ITEM_ID = "String";
    private static final String ITEM_KEY = "TYPEString";

    @Test
    public void testCreateLongKey() throws CalException {
	final ItemKey key = CalmFactory.createKey(ITEM_TYPE, LONG_ITEM_ID);
	Assert.assertNotNull(key);
	Assert.assertEquals(ITEM_TYPE, key.getType());
	Assert.assertEquals(LONG_ITEM_ID, key.getNumericId());
	Assert.assertEquals(LONG_ITEM_ID_AS_STRING, key.getId());
    }

    @Test
    public void testCreateStringKey() throws CalException {
	final ItemKey key = CalmFactory.createKey(ITEM_TYPE, STRING_ITEM_ID);
	Assert.assertNotNull(key);
	Assert.assertEquals(ITEM_TYPE, key.getType());
	Assert.assertEquals(STRING_ITEM_ID, key.getId());
    }

    @Test
    public void testParseKey() throws CalException {
	final ItemKey key = CalmFactory.parseKey(ITEM_KEY);
	Assert.assertNotNull(key);
	Assert.assertEquals(ITEM_TYPE, key.getType());
	Assert.assertEquals(STRING_ITEM_ID, key.getId());
    }
}
