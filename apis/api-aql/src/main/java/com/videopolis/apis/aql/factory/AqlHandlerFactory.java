package com.videopolis.apis.aql.factory;

import com.videopolis.apis.aql.handler.ApisRequestAqlHandler;
import com.videopolis.apis.aql.handler.AqlHandler;
import com.videopolis.apis.aql.handler.StringAqlHandler;
import com.videopolis.apis.aql.handler.impl.ApisRequestBuilderAqlHandler;
import com.videopolis.apis.aql.handler.impl.MultiAqlHandler;
import com.videopolis.apis.aql.handler.impl.QueryRewriterAqlHandler;
import com.videopolis.apis.model.ApisRequest;

/**
 * A factory which builds {@link AqlHandler} instances
 * 
 * @author julien
 * 
 */
public final class AqlHandlerFactory {

    private AqlHandlerFactory() {
    }

    /**
     * Returns an handler which rewrites AQL query to canonical AQL form.
     * 
     * @return Handler
     */
    public static StringAqlHandler createQueryRewriterAqlHandler() {
	return new QueryRewriterAqlHandler();
    }

    /**
     * Returns an handler which creates an {@link ApisRequest}.
     * 
     * @return Handler
     */
    public static ApisRequestAqlHandler createApisRequestBuilderAqlHandler() {
	return new ApisRequestBuilderAqlHandler();
    }

    /**
     * Returns an handler which dispatches events to multiple handlers.
     * 
     * @param aqlHandlers
     *            Handlers
     * @return Handler
     */
    public static AqlHandler createMultiAqlHandler(AqlHandler... aqlHandlers) {
	return new MultiAqlHandler(aqlHandlers);
    }
}
