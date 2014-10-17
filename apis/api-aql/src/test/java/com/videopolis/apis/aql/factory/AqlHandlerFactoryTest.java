package com.videopolis.apis.aql.factory;

import org.junit.Assert;
import org.junit.Test;

public class AqlHandlerFactoryTest {

    @Test
    public void testCreateQueryRewriterAqlHandler() {
	Assert.assertNotNull(AqlHandlerFactory.createQueryRewriterAqlHandler());
    }

    @Test
    public void testCreateApisRequestBuilderAqlHandler() {
	Assert.assertNotNull(AqlHandlerFactory
		.createApisRequestBuilderAqlHandler());
    }

    @Test
    public void testCreateMultiAqlHandler() {
	Assert.assertNotNull(AqlHandlerFactory
		.createMultiAqlHandler(AqlHandlerFactory
			.createApisRequestBuilderAqlHandler()));
    }
}
