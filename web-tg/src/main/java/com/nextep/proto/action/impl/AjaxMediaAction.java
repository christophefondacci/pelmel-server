package com.nextep.proto.action.impl;

import java.util.Arrays;

import com.nextep.media.model.Media;
import com.nextep.proto.action.model.MediaAware;
import com.nextep.proto.blocks.MediaProvider;
import com.opensymphony.xwork2.ActionSupport;
import com.videopolis.apis.factory.ApisFactory;
import com.videopolis.apis.model.ApisRequest;
import com.videopolis.apis.service.ApiResponse;
import com.videopolis.apis.service.ApiService;
import com.videopolis.calm.factory.CalmFactory;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.cals.factory.ContextFactory;

public class AjaxMediaAction extends ActionSupport implements MediaAware {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7427034081539550829L;
	private ApiService apiService;
	private MediaProvider mediaProvider;
	private String id;
	private Media media;

	@Override
	public String execute() throws Exception {
		final ItemKey key = CalmFactory.parseKey(id);
		final ApisRequest request = ApisFactory.createRequest(Media.class)
				.uniqueKey(key.getId());

		final ApiResponse response = apiService.execute(request,
				ContextFactory.createContext(getLocale()));

		media = (Media) response.getUniqueElement();
		mediaProvider.initialize(key, Arrays.asList(media));
		return SUCCESS;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Media getMedia() {
		return media;
	}

	public void setApiService(ApiService apiService) {
		this.apiService = apiService;
	}

	@Override
	public MediaProvider getMediaProvider() {
		return mediaProvider;
	}

	@Override
	public void setMediaProvider(MediaProvider mediaProvider) {
		this.mediaProvider = mediaProvider;
	}
}
