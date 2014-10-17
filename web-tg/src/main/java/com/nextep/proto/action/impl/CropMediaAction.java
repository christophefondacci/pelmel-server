package com.nextep.proto.action.impl;

import java.io.File;

import com.nextep.cal.util.services.CalPersistenceService;
import com.nextep.media.model.Media;
import com.nextep.media.model.MutableMedia;
import com.nextep.proto.action.base.AbstractAction;
import com.nextep.proto.blocks.MediaPersistenceSupport;
import com.nextep.proto.spring.ContextHolder;
import com.videopolis.apis.factory.ApisFactory;
import com.videopolis.apis.model.ApisRequest;
import com.videopolis.apis.service.ApiResponse;
import com.videopolis.calm.factory.CalmFactory;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.cals.factory.ContextFactory;

public class CropMediaAction extends AbstractAction {

	private static final long serialVersionUID = 7737003124560991931L;

	// Injected services
	private MediaPersistenceSupport mediaPersistenceSupport;
	private CalPersistenceService mediaService;

	// Dynamic action arguments
	private String redirectUrl;
	private String cropx, cropy, cropw, croph;
	private String mediaKey;

	@Override
	protected String doExecute() throws Exception {
		// Parsing media key and arguments
		final ItemKey mediaItemKey = CalmFactory.parseKey(mediaKey);
		if (cropx != null && !"".equals(cropx.trim())) {
			final Integer x = Double.valueOf(cropx).intValue();
			final Integer y = Double.valueOf(cropy).intValue();
			final Integer w = Double.valueOf(cropw).intValue();
			final Integer h = Double.valueOf(croph).intValue();

			// Building request that fetches media
			final ApisRequest request = ApisFactory.createRequest(Media.class)
					.uniqueKey(mediaItemKey.getId());
			// Executing
			final ApiResponse response = getApiService().execute(request,
					ContextFactory.createContext(getLocale()));

			// Fetching media result
			final MutableMedia m = (MutableMedia) response.getUniqueElement();
			// Getting image to process
			final File originalImage = mediaPersistenceSupport
					.getMediaLocalFile(m.getOriginalUrl());

			// Cropping image
			final File croppedFile = mediaPersistenceSupport.crop(m,
					originalImage, x, y, w, h);
			final String croppedUrl = mediaPersistenceSupport
					.getMediaUrl(croppedFile);
			m.setUrl(croppedUrl);

			// Saving altered media
			ContextHolder.toggleWrite();
			mediaService.saveItem(m);

			// Handling redirection
			if (redirectUrl == null) {
				redirectUrl = "/myProfile.action";
			}
		}
		// This is a success
		return "redirect";
	}

	public void setRedirectUrl(String redirectUrl) {
		this.redirectUrl = redirectUrl;
	}

	public String getRedirectUrl() {
		return redirectUrl;
	}

	public String getCropx() {
		return cropx;
	}

	public void setCropx(String cropx) {
		this.cropx = cropx;
	}

	public String getCropy() {
		return cropy;
	}

	public void setCropy(String cropy) {
		this.cropy = cropy;
	}

	public String getCropw() {
		return cropw;
	}

	public void setCropw(String cropw) {
		this.cropw = cropw;
	}

	public String getCroph() {
		return croph;
	}

	public void setCroph(String croph) {
		this.croph = croph;
	}

	public void setMediaKey(String mediaKey) {
		this.mediaKey = mediaKey;
	}

	public String getMediaKey() {
		return mediaKey;
	}

	public void setMediaPersistenceSupport(
			MediaPersistenceSupport mediaPersistenceSupport) {
		this.mediaPersistenceSupport = mediaPersistenceSupport;
	}

	public void setMediaService(CalPersistenceService mediaService) {
		this.mediaService = mediaService;
	}
}
