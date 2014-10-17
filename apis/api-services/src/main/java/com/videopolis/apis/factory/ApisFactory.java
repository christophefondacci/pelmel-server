package com.videopolis.apis.factory;

import com.videopolis.apis.helper.ApisRegistry;
import com.videopolis.apis.model.ApisRequest;
import com.videopolis.apis.model.impl.ApisCompositeRequestImpl;
import com.videopolis.apis.model.impl.ApisRequestImpl;
import com.videopolis.calm.model.CalmObject;

/**
 * This factory is the entry-point for clients who wants to perform a search
 * request through the API services.<br>
 *
 * This is the only way to create a new {@link ApisRequest} instance which
 * describes the request to perform.
 *
 * @author Christophe Fondacci
 */
public final class ApisFactory {

    private ApisFactory() {
    }

    /**
     * Creates a new {@link ApisRequest} which will return items of the given
     * type.
     *
     * @param itemType
     *            type of the items to retrieve
     * @return a new {@link ApisRequest} implementation
     */
    public static ApisRequest createRequest(
	    final Class<? extends CalmObject> itemType) {
	final String type = ApisRegistry.getTypeFromModel(itemType);
	return new ApisRequestImpl(type);
    }

    /**
     * Creates a new {@link ApisRequest} which will return items of the given
     * type name.
     *
     * @param type
     *            type name of the items to retrieve (e.g. HOTL)
     * @return a new {@link ApisRequest} implementation
     */
    public static ApisRequest createRequest(final String type) {
	return new ApisRequestImpl(type);
    }

    /**
     * Creates a new composite request which does not need any single root
     * element, thus allowing to query various CAL types at the root level of
     * the response.
     *
     * @return a composite {@link ApisRequest}
     */
    public static ApisRequest createCompositeRequest() {
	return new ApisCompositeRequestImpl();
    }
}
