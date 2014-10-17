package com.nextep.proto.apis.model.impl;

import com.nextep.proto.apis.model.ApisCriterionFactory;
import com.nextep.proto.blocks.CurrentUserSupport;
import com.nextep.users.model.User;
import com.videopolis.apis.exception.ApisException;
import com.videopolis.apis.factory.SearchRestriction;
import com.videopolis.apis.model.ApisCriterion;
import com.videopolis.calm.model.ItemKey;

public class ApisUserFromTokenCriterionFactory implements ApisCriterionFactory {

	@Override
	public ApisCriterion createApisCriterion(ItemKey itemKey)
			throws ApisException {
		return SearchRestriction.alternateKey(User.class, itemKey).aliasedBy(
				CurrentUserSupport.APIS_ALIAS_CURRENT_USER);
	}

}
