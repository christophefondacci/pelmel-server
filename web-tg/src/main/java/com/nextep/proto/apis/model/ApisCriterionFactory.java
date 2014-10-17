package com.nextep.proto.apis.model;

import com.videopolis.apis.exception.ApisException;
import com.videopolis.apis.model.ApisCriterion;
import com.videopolis.calm.model.ItemKey;

public interface ApisCriterionFactory {

	/**
	 * Creates the APIS criterion for the specified {@link ItemKey}
	 * 
	 * @param itemKey
	 *            {@link ItemKey} of the element to display
	 * @return the {@link ApisCriterion}
	 * @throws ApisException
	 *             whenever the APIS request is invalid
	 */
	public ApisCriterion createApisCriterion(ItemKey itemKey)
			throws ApisException;
}
