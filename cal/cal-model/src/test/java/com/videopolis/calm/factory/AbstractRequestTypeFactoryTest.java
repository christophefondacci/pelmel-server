package com.videopolis.calm.factory;

import java.io.Serializable;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;

import com.videopolis.calm.factory.base.AbstractRequestTypeFactory;
import com.videopolis.calm.model.RequestType;

public class AbstractRequestTypeFactoryTest {

    /** Good set of parameters */
    private static final Serializable[] PARAMETERS = { "String", 42, true,
	    "Another String", 12.5d, Locale.CANADA_FRENCH };

    /** Invalid types set of parameters */
    private static final Serializable[] BAD_PARAMETERS = { 42, true,
	    "Another String", 12.5d, Locale.CANADA_FRENCH, "String" };

    /** Invalid parameter count */
    private static final Serializable[] BAD_PARAMETER_COUNT = { 42, true,
	    "Another String", 12.5d, Locale.CANADA_FRENCH };

    /** Parameter types */
    private static final Class<?>[] PARAMETER_TYPES = { String.class,
	    Integer.class, Boolean.class, String.class, Double.class,
	    Locale.class };

    private RequestTypeFactory factory;

    @Before
    public void setUp() {
	factory = new AbstractRequestTypeFactory() {

	    private static final long serialVersionUID = 2764956399508622241L;

	    @Override
	    public RequestType createRequestType(
		    final Serializable... parameters) {
		checkParameters(parameters, PARAMETER_TYPES);
		return null;
	    }

	    @Override
	    public RequestType createRequestType(final String type,
		    final Serializable... parameters) {
		checkParameters(parameters, PARAMETER_TYPES);
		return null;
	    }
	};
    }

    @Test
    public void testCheckParameters() {
	// Only make sure no exception is thrown
	factory.createRequestType(PARAMETERS);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFailCountCheckParameters() {
	// Only make sure an exception is thrown
	factory.createRequestType(BAD_PARAMETER_COUNT);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFailTypeCheckParameters() {
	// Only make sure an exception is thrown
	factory.createRequestType(BAD_PARAMETERS);
    }
}
