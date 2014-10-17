package com.nextep.proto.action.impl;

import com.nextep.cal.util.helpers.CalHelper;
import com.nextep.cal.util.services.CalPersistenceService;
import com.nextep.proto.action.base.AbstractAction;
import com.nextep.proto.action.model.CurrentUserAware;
import com.nextep.proto.blocks.CurrentUserSupport;
import com.nextep.proto.spring.ContextHolder;
import com.nextep.users.model.User;
import com.videopolis.apis.factory.ApisFactory;
import com.videopolis.apis.model.ApisRequest;
import com.videopolis.apis.service.ApiCompositeResponse;
import com.videopolis.calm.factory.CalmFactory;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.cals.factory.ContextFactory;

public class ToggleUserTagAction extends AbstractAction implements
		CurrentUserAware {

	private static final long serialVersionUID = 6453189960338385128L;

	private CurrentUserSupport currentUserSupport;
	private CalPersistenceService tagsService;

	private String id;
	private String tag;

	@Override
	protected String doExecute() throws Exception {
		// Parsing keys
		final ItemKey taggedItemKey = CalmFactory.parseKey(id);
		final ItemKey tagKey = CalmFactory.parseKey(tag);

		final ApisRequest request = (ApisRequest) ApisFactory
				.createCompositeRequest().addCriterion(
						currentUserSupport.createApisCriterionFor(
								getNxtpUserToken(), true));
		final ApiCompositeResponse response = (ApiCompositeResponse) getApiService()
				.execute(request, ContextFactory.createContext(getLocale()));

		final User currentUser = response.getUniqueElement(User.class,
				CurrentUserSupport.APIS_ALIAS_CURRENT_USER);

		// Toggling tag
		ContextHolder.toggleWrite();
		final ItemKey userTaggedItemKey = CalHelper.createMultiKey(
				currentUser.getKey(), taggedItemKey);
		tagsService.setItemFor(userTaggedItemKey, tagKey);
		return SUCCESS;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public String getTag() {
		return tag;
	}

	@Override
	public void setCurrentUserSupport(CurrentUserSupport currentUserSupport) {
		this.currentUserSupport = currentUserSupport;
	}

	@Override
	public CurrentUserSupport getCurrentUserSupport() {
		return currentUserSupport;
	}

	public void setTagsService(CalPersistenceService tagsService) {
		this.tagsService = tagsService;
	}

}
