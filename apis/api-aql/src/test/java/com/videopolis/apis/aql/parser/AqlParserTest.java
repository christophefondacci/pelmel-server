package com.videopolis.apis.aql.parser;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Test;

import com.videopolis.apis.aql.exception.AqlException;
import com.videopolis.apis.aql.factory.AqlHandlerFactory;
import com.videopolis.apis.aql.handler.StringAqlHandler;
import com.videopolis.apis.aql.helper.AqlHelper;
import com.videopolis.apis.helper.ApisRegistry;
import com.videopolis.cals.service.impl.TestServiceA;
import com.videopolis.cals.service.impl.TestServiceB;
import com.videopolis.cals.service.impl.TestServiceC;

public class AqlParserTest {

    private static final Log LOGGER = LogFactory.getLog(AqlParserTest.class);

    private static final String TEST_QUERY = "get TestModelA unique key 1234 "
	    + "with ITestModelB, (nearest CCCC radius 10 page 0 size 10 "
	    + "with com.videopolis.calm.model.impl.TestModelA, BBBB)";

    private static final String TEST_CANONICAL_QUERY = "get com.videopolis.calm.model.impl.TestModelA unique key 1234 "
	    + "with (com.videopolis.calm.model.ITestModelB), "
	    + "(nearest com.videopolis.calm.model.impl.TestModelC radius 10.0 page 0 size 10 "
	    + "with (com.videopolis.calm.model.impl.TestModelA), "
	    + "(com.videopolis.calm.model.ITestModelB))";

    @Test
    public void testAqlParser() throws AqlException {

	// Register types
	ApisRegistry.registerCalService(new TestServiceA());
	ApisRegistry.registerCalService(new TestServiceB());
	ApisRegistry.registerCalService(new TestServiceC());

	LOGGER.info("Initial query: " + TEST_QUERY);
	StringAqlHandler handler = AqlHandlerFactory
		.createQueryRewriterAqlHandler();
	AqlHelper.parse(TEST_QUERY, handler);
	LOGGER.info("Canonical query: " + handler.getResult());

	Assert.assertNotNull(handler.getResult());
	Assert.assertEquals(TEST_CANONICAL_QUERY, handler.getResult());

	// Re evaluate canonical query
	handler = AqlHandlerFactory.createQueryRewriterAqlHandler();
	AqlHelper.parse(TEST_CANONICAL_QUERY, handler);
	LOGGER.info("Canonical query reparsed: " + handler.getResult());
	Assert.assertNotNull(handler.getResult());
	Assert.assertEquals(TEST_CANONICAL_QUERY, handler.getResult());
    }
}
