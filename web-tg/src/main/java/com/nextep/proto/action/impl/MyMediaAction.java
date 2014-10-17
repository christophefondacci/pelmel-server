package com.nextep.proto.action.impl;

import java.util.List;

import com.nextep.media.model.Media;
import com.nextep.proto.action.base.AbstractAction;
import com.nextep.proto.action.model.CurrentObjectAware;
import com.nextep.proto.action.model.CurrentUserAware;
import com.nextep.proto.action.model.MediaAware;
import com.nextep.proto.action.model.MessagingAware;
import com.nextep.proto.blocks.CurrentUserSupport;
import com.nextep.proto.blocks.MediaProvider;
import com.nextep.proto.blocks.MessagingSupport;
import com.nextep.proto.model.SearchType;
import com.nextep.users.model.User;
import com.videopolis.apis.factory.ApisFactory;
import com.videopolis.apis.model.ApisRequest;
import com.videopolis.apis.service.ApiCompositeResponse;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.cals.factory.ContextFactory;

public class MyMediaAction extends AbstractAction implements MediaAware,
		CurrentUserAware, MessagingAware, CurrentObjectAware {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8327854921914270045L;
	private MediaProvider mediaProvider;
	private CurrentUserSupport currentUserSupport;
	private MessagingSupport messagingSupport;

	@Override
	protected String doExecute() throws Exception {

		final ApisRequest request = (ApisRequest) ApisFactory
				.createCompositeRequest().addCriterion(
						currentUserSupport.createApisCriterionFor(
								getNxtpUserToken(), false));
		final ApiCompositeResponse response = (ApiCompositeResponse) getApiService()
				.execute(request, ContextFactory.createContext(getLocale()));

		final User user = response.getUniqueElement(User.class,
				CurrentUserSupport.APIS_ALIAS_CURRENT_USER);
		getHeaderSupport().initialize(getLocale(), user, null, SearchType.MAP);
		checkCurrentUser(user);
		currentUserSupport.initialize(getUrlService(), user);
		final List<? extends Media> media = user.get(Media.class);
		mediaProvider.initialize(user.getKey(), media);
		return SUCCESS;
	}

	public String getRedirectUrl() {
		return "/myMedia.action";
	}

	@Override
	public void setMediaProvider(MediaProvider mediaProvider) {
		this.mediaProvider = mediaProvider;
	}

	@Override
	public MediaProvider getMediaProvider() {
		return mediaProvider;
	}

	@Override
	public void setCurrentUserSupport(CurrentUserSupport currentUserSupport) {
		this.currentUserSupport = currentUserSupport;
	}

	@Override
	public CurrentUserSupport getCurrentUserSupport() {
		return currentUserSupport;
	}

	@Override
	public void setMessagingSupport(MessagingSupport messagingSupport) {
		this.messagingSupport = messagingSupport;
	}

	@Override
	public MessagingSupport getMessagingSupport() {
		return messagingSupport;
	}

	@Override
	public ItemKey getCurrentObjectKey() {
		return currentUserSupport.getCurrentUser().getKey();
	}
}
