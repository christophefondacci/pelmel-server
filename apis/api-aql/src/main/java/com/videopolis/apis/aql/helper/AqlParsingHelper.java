package com.videopolis.apis.aql.helper;

import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.videopolis.apis.aql.exception.QueryParsingException;
import com.videopolis.apis.helper.ApisRegistry;
import com.videopolis.calm.model.CalmObject;

/**
 * A set of utility methods used when parsing AQL queries
 * 
 * @author julien
 * 
 */
public final class AqlParsingHelper {

    /** The length (in characters) of an item type */
    private static final int ITEM_TYPE_LENGTH = 4;

    private static final Log LOGGER = LogFactory.getLog(AqlParsingHelper.class);

    private AqlParsingHelper() {
    }

    /**
     * Given a type, returns the corresponding {@link CalmObject} class. The
     * type is represented as a String an may be (and will be checked in this
     * order):
     * <ol>
     * <li>The fully qualified class name (e.g
     * {@code com.videopolis.hotel.model.Hotel})</li>
     * <li>And item type ({@code HOTL})</li>
     * <li>The unqualified class name ({@code Hotel})</li>
     * </ol>
     * 
     * @param type
     *            The type to parse
     * @return The corresponding class
     * @throws QueryParsingException
     *             If the class can't be found using the given type.
     */
    public static Class<? extends CalmObject> getCalmObjectClass(String type) {
	// Check whether the type is a fully qualified class name
	if (type.lastIndexOf('.') > 0) {
	    LOGGER.debug("Using provided fully qualified class name for "
		    + type);
	    return getCalmObjectClassFromClassName(type);
	}

	// Check if this is a type name
	Class<? extends CalmObject> typeClass = ApisRegistry
		.getModelFromType(type);

	if (typeClass == null) {
	    // So it should be an unqualified class name
	    final Set<Class<? extends CalmObject>> modelClasses = ApisRegistry
		    .getDeclaredModelClasses();
	    for (final Class<? extends CalmObject> modelClass : modelClasses) {
		final String canonicalName = modelClass.getCanonicalName();
		if (canonicalName.substring(canonicalName.lastIndexOf('.') + 1)
			.equals(type)) {
		    typeClass = modelClass;
		}
	    }
	}

	if (typeClass == null) {
	    throw new QueryParsingException(
		    "Unable to find any matching CalmObject class for type "
			    + type);
	}

	LOGGER.debug("Found class " + typeClass.getCanonicalName()
		+ " for type " + type);
	return typeClass;
    }

    /**
     * Given a fully qualified class name, the corresponding {@link Class}
     * object, if the class is a subclass of {@link CalmObject}. Otherwise, an
     * exception is thrown.
     * 
     * @param className
     *            The class name
     * @return The {@link Class} object
     * @throws QueryParsingException
     *             If the class cannot be found, or if it's not a subclass of
     *             {@link CalmObject}
     */
    @SuppressWarnings("unchecked")
    private static Class<? extends CalmObject> getCalmObjectClassFromClassName(
	    String className) {
	try {
	    final Class<?> typeClass = Class.forName(className);
	    if (!CalmObject.class.isAssignableFrom(typeClass)) {
		// This is not an usable class...
		throw new QueryParsingException("Class " + className
			+ " is not an implementation of "
			+ typeClass.getCanonicalName());
	    }

	    return (Class<? extends CalmObject>) typeClass;
	} catch (ClassNotFoundException e) {
	    throw new QueryParsingException(e.getMessage(), e);
	}
    }

    /**
     * Given a full item key, returns the item type part
     * 
     * @param key
     *            Key
     * @return Item type
     */
    public static String extractItemType(String key) {
	return key.substring(0, ITEM_TYPE_LENGTH);
    }

    /**
     * Given a full item key, returns the item id part
     * 
     * @param key
     *            Key
     * @return Item ID
     */
    public static String extractItemId(String key) {
	return key.substring(ITEM_TYPE_LENGTH);
    }
}
