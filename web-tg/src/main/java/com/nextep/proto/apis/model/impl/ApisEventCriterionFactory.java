package com.nextep.proto.apis.model.impl;

import java.util.Arrays;

import com.nextep.descriptions.model.Description;
import com.nextep.media.model.Media;
import com.nextep.proto.apis.adapters.ApisEventLocationAdapter;
import com.nextep.proto.apis.model.ApisCriterionFactory;
import com.nextep.proto.model.Constants;
import com.nextep.tags.model.Tag;
import com.videopolis.apis.exception.ApisException;
import com.videopolis.apis.factory.SearchRestriction;
import com.videopolis.apis.model.ApisCriterion;
import com.videopolis.apis.model.ApisItemKeyAdapter;
import com.videopolis.calm.model.ItemKey;

/**
 * This criterion factory creates an APIS criterion which fetches the Event with
 * all its mandatory content.
 * 
 * @author cfondacci
 * 
 */
public class ApisEventCriterionFactory implements ApisCriterionFactory {

	private static final ApisItemKeyAdapter eventLocationAdapter = new ApisEventLocationAdapter();

	@Override
	public ApisCriterion createApisCriterion(ItemKey eventKey)
			throws ApisException {
		return (ApisCriterion) SearchRestriction
				.uniqueKeys(Arrays.asList(eventKey))
				.aliasedBy(Constants.APIS_ALIAS_EVENT)
				.addCriterion(
						(ApisCriterion) SearchRestriction.adaptKey(
								eventLocationAdapter).with(Media.class))
				.with(Media.class).with(Tag.class).with(Description.class);
	}

}
