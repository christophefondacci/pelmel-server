package com.nextep.geo.model.impl;

import com.videopolis.calm.model.RequestType;

public class RequestTypeWithAlternates implements RequestType {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1245111512195866084L;

	private RequestTypeWithAlternates() {
	}

	public static RequestTypeWithAlternates WITH_ALTERNATES = new RequestTypeWithAlternates();
}
