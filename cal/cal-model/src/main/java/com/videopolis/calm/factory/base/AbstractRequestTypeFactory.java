package com.videopolis.calm.factory.base;

import java.io.Serializable;

import com.videopolis.calm.factory.RequestTypeFactory;
import com.videopolis.calm.model.RequestType;

/**
 * <p>
 * A base implementation of {@link RequestTypeFactory}
 * </p>
 * <p>
 * This class provides type checking method which helps implementors checking
 * the validity of the parameters passed to the {@code createRequestType()}
 * method
 * </p>
 *
 * @author julien
 *
 */
public abstract class AbstractRequestTypeFactory implements RequestTypeFactory {

    private static final long serialVersionUID = -3349799957018836238L;

    /**
     * <p>
     * Checks the provided parameters types.
     * </p>
     * <p>
     * This method will throw an {@link IllegalArgumentException} in the
     * following conditions:
     * <ul>
     * <li>The number of parameters is not correct</li>
     * <li>The type of at least one parameter is not correct</li>
     * </ul>
     * </p>
     *
     * @param parameters
     *            The provided parameters
     * @param expected
     *            The expected classes of the parameters.
     */
    protected final void checkParameters(final Serializable[] parameters,
	    final Class<?>... expected) {

	// Check parameter count
	if (parameters.length != expected.length) {
	    throw new IllegalArgumentException("Expected " + expected.length
		    + " paremeters, but " + parameters.length + " found.");
	}

	// Check parameter types
	for (int i = 0; i < parameters.length; i++) {
	    if (!expected[i].isAssignableFrom(parameters[i].getClass())) {
		throw new IllegalArgumentException("Parameter " + i
			+ ": Expected " + expected[i].getCanonicalName()
			+ " but got "
			+ parameters[i].getClass().getCanonicalName());
	    }
	}
    }

    @Override
    public RequestType createRequestType(final Serializable... parameters) {
	return createRequestType(null, parameters);
    }
}
