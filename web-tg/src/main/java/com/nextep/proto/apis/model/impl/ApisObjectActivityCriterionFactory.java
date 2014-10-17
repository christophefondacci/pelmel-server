package com.nextep.proto.apis.model.impl;

import com.nextep.activities.model.Activity;
import com.nextep.media.model.Media;
import com.nextep.proto.apis.adapters.ApisActivityExtraKeyAdapter;
import com.nextep.proto.apis.adapters.ApisActivityUserAdapter;
import com.nextep.proto.apis.model.ApisCriterionFactory;
import com.nextep.proto.model.Constants;
import com.videopolis.apis.exception.ApisException;
import com.videopolis.apis.factory.SearchRestriction;
import com.videopolis.apis.model.ApisCriterion;
import com.videopolis.apis.model.ApisItemKeyAdapter;
import com.videopolis.apis.model.WithCriterion;
import com.videopolis.calm.model.ItemKey;

/**
 * This factory creates the criterion that fetches activities of an element.
 * 
 * @author cfondacci
 * 
 */
public class ApisObjectActivityCriterionFactory implements ApisCriterionFactory {

	private static final ApisItemKeyAdapter activityUserKeyAdapter = new ApisActivityUserAdapter();
	private static final ApisItemKeyAdapter activityExtraKeyAdapter = new ApisActivityExtraKeyAdapter();

	@Override
	public ApisCriterion createApisCriterion(ItemKey itemKey)
			throws ApisException {
		return (WithCriterion) SearchRestriction
				.with(Activity.class, Constants.MAX_ACTIVITIES, 0)
				.addCriterion(
						(ApisCriterion) SearchRestriction
								.adaptKey(activityUserKeyAdapter)
								.aliasedBy(Constants.ALIAS_ACTIVITY_USER)
								.with(Media.class))
				.addCriterion(
						SearchRestriction.adaptKey(activityExtraKeyAdapter)
								.aliasedBy(Constants.ALIAS_ACTIVITY_OBJECT));
	}
}
