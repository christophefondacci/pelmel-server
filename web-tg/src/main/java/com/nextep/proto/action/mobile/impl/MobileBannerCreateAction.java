package com.nextep.proto.action.mobile.impl;

import java.io.File;
import java.util.Arrays;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.nextep.advertising.model.AdvertisingBanner;
import com.nextep.advertising.model.BannerStatus;
import com.nextep.advertising.model.BannerType;
import com.nextep.advertising.model.MutableAdvertisingBanner;
import com.nextep.cal.util.services.CalPersistenceService;
import com.nextep.json.model.impl.JsonBanner;
import com.nextep.media.model.Media;
import com.nextep.proto.action.base.AbstractAction;
import com.nextep.proto.action.model.JsonProvider;
import com.nextep.proto.blocks.CurrentUserSupport;
import com.nextep.proto.blocks.MediaPersistenceSupport;
import com.nextep.proto.builders.JsonBuilder;
import com.nextep.proto.spring.ContextHolder;
import com.nextep.smaug.service.SearchPersistenceService;
import com.nextep.users.model.User;
import com.videopolis.apis.factory.ApisFactory;
import com.videopolis.apis.factory.SearchRestriction;
import com.videopolis.apis.model.ApisRequest;
import com.videopolis.apis.service.ApiCompositeResponse;
import com.videopolis.calm.factory.CalmFactory;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.cals.factory.ContextFactory;
import com.videopolis.smaug.common.model.SearchScope;

/**
 * This action creates (or updates) an advertising banner.
 * 
 * @author cfondacci
 *
 */
public class MobileBannerCreateAction extends AbstractAction implements
		JsonProvider {

	private static final long serialVersionUID = 1L;
	private static final String APIS_ALIAS_BANNER = "banner";

	// Injected services and supports
	@Autowired
	private CurrentUserSupport currentUserSupport;
	@Autowired
	@Qualifier("bannersService")
	private CalPersistenceService bannersPersistenceService;
	@Autowired
	private MediaPersistenceSupport mediaPersistenceSupport;
	@Autowired
	private SearchPersistenceService searchPersistenceService;
	@Autowired
	private JsonBuilder jsonBuilder;

	// Dynamic arguments
	private String bannerKey;
	private String targetItemKey;
	private String targetUrl;
	private double lat;
	private double lng;
	private double radius;
	private boolean highRes;
	private long targetDisplayCount;
	private File media;
	private String mediaContentType, mediaFileName;
	private boolean active = true;

	// Internal variable
	private AdvertisingBanner banner;

	@Override
	protected String doExecute() throws Exception {
		ItemKey bannerItemKey = null;
		if (bannerKey != null) {
			bannerItemKey = CalmFactory.parseKey(bannerKey);
		}
		// Building request
		final ApisRequest request = (ApisRequest) ApisFactory
				.createCompositeRequest().addCriterion(
						currentUserSupport.createApisCriterionFor(
								getNxtpUserToken(), true));

		if (bannerItemKey != null) {
			request.addCriterion(SearchRestriction.uniqueKeys(
					Arrays.asList(bannerItemKey)).aliasedBy(APIS_ALIAS_BANNER));
		}

		// Executing
		final ApiCompositeResponse response = (ApiCompositeResponse) getApiService()
				.execute(request, ContextFactory.createContext(getLocale()));

		final User user = response.getUniqueElement(User.class,
				CurrentUserSupport.APIS_ALIAS_CURRENT_USER);
		checkCurrentUser(user);

		// Creating banner
		banner = response.getUniqueElement(AdvertisingBanner.class,
				APIS_ALIAS_BANNER);
		if (banner == null) {
			banner = (AdvertisingBanner) bannersPersistenceService
					.createTransientObject();
		}
		MutableAdvertisingBanner mutableBanner = (MutableAdvertisingBanner) banner;
		ContextHolder.toggleWrite();
		if (active) {
			mutableBanner.setBannerType(BannerType.MOBILE_HORIZONTAL);
			mutableBanner.setLatitude(lat);
			mutableBanner.setLongitude(lng);
			mutableBanner.setRadius(radius);
			mutableBanner.setOwnerItemKey(user.getKey());
			mutableBanner.setTargetDisplayCount(targetDisplayCount);
			if (targetItemKey != null) {
				mutableBanner.setTargetItemKey(CalmFactory
						.parseKey(targetItemKey));
			}
			mutableBanner.setTargetUrl(targetUrl);
			mutableBanner.setEndValidity(null);
		} else {
			// Setting end validity in the past to de-activate it
			mutableBanner.setStatus(BannerStatus.DELETED);
		}

		// Saving banner
		bannersPersistenceService.saveItem(mutableBanner);
		searchPersistenceService.storeCalmObject(mutableBanner,
				SearchScope.CHILDREN);

		// If we have an embedded media
		if (media != null) {
			ContextHolder.toggleWrite();
			final Media addedMedia = mediaPersistenceSupport.createMedia(user,
					mutableBanner.getKey(), media, mediaFileName,
					mediaContentType, null, false, 1);
			mutableBanner.add(addedMedia);
		}

		banner = mutableBanner;
		return SUCCESS;
	}

	@Override
	public String getJson() {
		JsonBanner json = jsonBuilder.buildJsonBanner(banner, highRes,
				getLocale());
		return JSONObject.fromObject(json).toString();
	}

	public void setBannerKey(String bannerKey) {
		this.bannerKey = bannerKey;
	}

	public String getBannerKey() {
		return bannerKey;
	}

	public void setTargetItemKey(String targetItemKey) {
		this.targetItemKey = targetItemKey;
	}

	public String getTargetItemKey() {
		return targetItemKey;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLat() {
		return lat;
	}

	public void setLng(double lng) {
		this.lng = lng;
	}

	public double getLng() {
		return lng;
	}

	public void setMedia(File media) {
		this.media = media;
	}

	public File getMedia() {
		return media;
	}

	public void setMediaContentType(String mediaContentType) {
		this.mediaContentType = mediaContentType;
	}

	public String getMediaContentType() {
		return mediaContentType;
	}

	public void setMediaFileName(String mediaFileName) {
		this.mediaFileName = mediaFileName;
	}

	public String getMediaFileName() {
		return mediaFileName;
	}

	public void setRadius(double radius) {
		this.radius = radius;
	}

	public double getRadius() {
		return radius;
	}

	public void setTargetDisplayCount(long targetDisplayCount) {
		this.targetDisplayCount = targetDisplayCount;
	}

	public long getTargetDisplayCount() {
		return targetDisplayCount;
	}

	public void setHighRes(boolean highRes) {
		this.highRes = highRes;
	}

	public boolean isHighRes() {
		return highRes;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public boolean isActive() {
		return active;
	}

	public void setTargetUrl(String targetUrl) {
		this.targetUrl = targetUrl;
	}

	public String getTargetUrl() {
		return targetUrl;
	}
}
