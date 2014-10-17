package com.videopolis.calm.helper;

import java.util.Collection;

import com.videopolis.calm.exception.CalException;

/**
 * A helper class providing helper methods for assertion tests upon the CAL
 * model. Please base any CAL assertion on this class as it will automatically
 * raise the apropriate CalException when implemented and supported.
 * 
 * @author Christophe Fondacci
 * 
 */
public final class Assert {

    private Assert() {
    }

    /**
     * Asserts that the object is not null and throws a {@link CalException}
     * when the specified input object is <code>null</code> with a default
     * failure message.<br>
     * Although not deprecated we adivse you to use
     * {@link Assert#notNull(Object, String)} method instead which will allow
     * you to define a more detailed failure message
     * 
     * @param o
     *            object to check against nullity
     * 
     * @throws CalException
     *             whenever this test fails
     */
    public static void notNull(Object o) throws CalException {
	notNull(o, "Object must not be null");
    }

    /**
     * Asserts that the object is not null and throws a {@link CalException}
     * when the specified input object is <code>null</code> with the given
     * failure message.
     * 
     * @param o
     *            object to check against nullity
     * @param failureMessage
     *            message to raise in the exception whenever the test fails
     * @throws CalException
     *             whenever this test fails
     */
    public static void notNull(Object o, String failureMessage)
	    throws CalException {
	if (o == null) {
	    throw new CalException(failureMessage);
	}
    }

    /**
     * Asserts that the specified collection contains one and exactly one
     * element.<br>
     * 
     * @param collection
     *            collection to check against unique content
     * @throws CalException
     *             whenever this test fails
     */
    public static void uniqueElement(Collection<?> collection)
	    throws CalException {
	Assert.notNull(collection);
	if (collection.size() != 1) {
	    throw new CalException(
		    "The specified collection must contain 1 and only 1 element");
	}
    }

    /**
     * Asserts that the specified array contains one and exactly one element.<br>
     * 
     * @param collection
     *            collection to check against unique content
     * @throws CalException
     *             whenever this test fails
     */
    public static <T> void uniqueElement(T... elements) throws CalException {
	Assert.notNull(elements);
	if (elements.length != 1) {
	    throw new CalException(
		    "The specified array must contain 1 and only 1 element");
	}
    }

    /**
     * Asserts that the actual integer value is strictly <u>superior</u> to the
     * <code>lowerBound</code> parameter.
     * 
     * @param actual
     *            actual value to check
     * @param lowerBound
     *            lower bound of the value to check
     * @param message
     *            message to raise when this test fails
     * @throws CalException
     *             whenever this test fails
     */
    public static void moreThan(int actual, int lowerBound, String message)
	    throws CalException {
	if (actual <= lowerBound) {
	    throw new CalException(message);
	}
    }

    /**
     * Asserts that the actual integer value is strictly <u>inferior</u> to the
     * <code>upperBound</code> parameter.
     * 
     * @param actual
     *            actual value to check
     * @param lowerBound
     *            lower bound of the value to check
     * @param message
     *            message to raise when this test fails
     * @throws CalException
     *             whenever this test fails
     */
    public static void lessThan(int actual, int upperBound, String message)
	    throws CalException {
	if (actual >= upperBound) {
	    throw new CalException(message);
	}
    }

    /**
     * Asserts that the provided object has a class which is either the same or
     * an extension of the provided class to check. The test will fail if the
     * provided object instance is <code>null</code>.
     * 
     * @param <T>
     *            Class type parameter
     * @param o
     *            object instance to check
     * @param classToCheck
     *            class to be compliant with
     * @throws CalException
     *             whenever this test fails
     */
    public static <T> void instanceOf(Object o, Class<T> classToCheck)
	    throws CalException {
	Assert.notNull(o);
	if (!classToCheck.isAssignableFrom(o.getClass())) {
	    throw new CalException("Framework was expected an element of type "
		    + classToCheck.getName() + ", but was "
		    + o.getClass().getName());
	}
    }

    /**
     * Asserts that the 2 parameters are equal and raises a {@link CalException}
     * if not.
     * 
     * @param actual
     *            actual value to check
     * @param expected
     *            expected value
     * @param message
     *            message to throw if this assertion fails
     * @throws CalException
     *             whenever this assertion fails
     */
    public static void equals(int actual, int expected, String message)
	    throws CalException {
	if (actual != expected) {
	    throw new CalException(message + " [expected " + expected
		    + " but was " + actual + "]");
	}
    }
}
