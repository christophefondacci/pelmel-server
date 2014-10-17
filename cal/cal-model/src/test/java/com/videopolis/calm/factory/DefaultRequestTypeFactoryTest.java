package com.videopolis.calm.factory;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Locale;

import org.junit.Assert;
import org.junit.Test;

import com.videopolis.calm.factory.impl.DefaultRequestTypeFactory;
import com.videopolis.calm.model.RequestType;
import com.videopolis.calm.model.impl.DefaultRequestType;

public class DefaultRequestTypeFactoryTest {

    private static final Serializable[] PARAMETERS = { "String", 42, true,
	    "Another String", 12.5d, Locale.CANADA_FRENCH };

    @Test
    public void testCreateRequestType() {
	final DefaultRequestTypeFactory factory = new DefaultRequestTypeFactory();
	final RequestType requestType = factory.createRequestType(PARAMETERS);
	Assert.assertNotNull(requestType);
	Assert.assertTrue("Invalid request type class",
		requestType instanceof DefaultRequestType);
	Assert.assertTrue(
		"Inavlid request type contents",
		Arrays.equals(PARAMETERS,
			((DefaultRequestType) requestType).getParameters()));
    }
}
