package com.videopolis.apis.helper;

import java.util.Collection;

import com.videopolis.apis.exception.ApisException;

/**
 * A utility class performing assertions and raising ApisException upon
 * failures.
 * 
 * @author Christophe Fondacci
 * 
 */
public final class Assert {
    private Assert() {
    }

    /**
     * Asserts that the 2 specified objects are equals and will raise an
     * {@link ApisException} if not.
     * 
     * @param o1
     *            first object
     * @param o2
     *            second object
     * @param message
     *            message of the raised exception when 2 objects differs
     * @throws ApisException
     *             when the assertion fails
     */
    public static void isEqual(final Object o1, final Object o2, String message)
	    throws ApisException {
	if ((o1 == null && o2 != null) || !o1.equals(o2)) {
	    throw new ApisException(message
		    + " [Reason : Assertion equality failed]");
	}
    }

    /**
     * Asserts that the object is not null and throws a {@link ApisException}
     * when the specified input object is <code>null</code>.
     * 
     * @param o
     *            object to check against nullity
     * @param message
     *            message to set in the exception when the assertion fails
     */
    public static final void notNull(final Object o, final String message)
	    throws ApisException {
	if (o == null) {
	    throw new ApisException(message
		    + " [Reason: Assertion failed - Object must not be null]");
	}
    }

    /**
     * Asserts that the object is null and throws a {@link ApisException} when
     * the specified input object is not <code>null</code>.
     * 
     * @param o
     *            object to check against nullity
     * @param message
     *            message to set in the exception when the assertion fails
     */
    public static final void isNull(final Object o, final String message)
	    throws ApisException {
	if (o != null) {
	    throw new ApisException(message
		    + " [Reason: Assertion failed - Object must be null]");
	}
    }

    /**
     * Asserts that the specified collection contains zero or one element<br>
     * 
     * @param collection
     *            collection to check against unique content
     * @param message
     *            message to set in the exception when the assertion fails
     */
    public static final void uniqueElement(final Collection<?> collection,
	    final String message) throws ApisException {
	Assert.notNull(collection, message);
	if (collection.size() > 1) {
	    throw new ApisException(message
		    + " [Reason: Assertion failed - The specified "
		    + "collection must contain at most 1 element, found "
		    + collection.size() + "]");
	}
    }

    /**
     * Asserts that the specified array contains one and exactly one element.<br>
     * 
     * @param collection
     *            collection to check against unique content
     * @param message
     *            message to set in the exception when the assertion fails
     */
    public static final <T> void uniqueElement(final String message,
	    final T... elements) throws ApisException {
	Assert.notNull(elements, message);
	if (elements.length != 1) {
	    throw new ApisException(
		    message
			    + " [Reason : Assertion failed - The specified array must contain 1 and only 1 element]");
	}
    }

    /**
     * Asserts that the specified object instance is compatible with the
     * specified class.
     * 
     * @param o
     *            object instance to check
     * @param classToCheck
     *            class to be compliant with
     * @param message
     *            message to set in the exception when the assertion fails
     * @throws ApisException
     *             when the assertion fails
     */
    public static final <T> void instanceOf(final Object o,
	    final Class<T> classToCheck, final String message)
	    throws ApisException {
	Assert.notNull(o, message);
	if (!classToCheck.isAssignableFrom(o.getClass())) {
	    throw new ApisException(
		    message
			    + " [Reason : Assertion failed - Framework was expected an element of type "
			    + classToCheck.getName() + ", but was "
			    + o.getClass().getName() + "]");
	}
    }

}
