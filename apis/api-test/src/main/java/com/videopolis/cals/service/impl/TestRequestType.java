package com.videopolis.cals.service.impl;

import com.videopolis.calm.model.RequestType;

/**
 * A test interface for a {@link RequestType}
 * 
 * @author Christophe Fondacci
 * 
 */
public interface TestRequestType extends RequestType {

    /**
     * CODE ONLY request type
     */
    final RequestType CODE_ONLY = new RequestType() {
	private static final long serialVersionUID = -8546175913116407916L;
    };
    /**
     * LOCALE ONLY request type
     */
    final RequestType LOCALE_ONLY = new RequestType() {
	private static final long serialVersionUID = 3166862852716875468L;
    };
}
