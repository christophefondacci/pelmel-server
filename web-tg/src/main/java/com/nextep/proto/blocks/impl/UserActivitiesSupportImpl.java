package com.nextep.proto.blocks.impl;

import java.util.List;
import java.util.Locale;

import com.nextep.activities.model.Activity;
import com.nextep.geo.model.GeographicItem;
import com.nextep.media.model.Media;
import com.nextep.proto.blocks.ActivitySupport;
import com.nextep.proto.helpers.DisplayHelper;
import com.nextep.proto.services.UrlService;
import com.nextep.users.model.User;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.cals.model.PaginationInfo;

public class UserActivitiesSupportImpl implements ActivitySupport {

	private ActivitySupport baseActivitySupport;

	private UrlService urlService;

	@Override
	public void initialize(UrlService urlService, Locale locale,
			PaginationInfo paginationInfo, List<? extends Activity> activities) {
		baseActivitySupport.initialize(urlService, locale, paginationInfo,
				activities);
		this.urlService = urlService;
	}

	@Override
	public void initializeUser(User user) {
		baseActivitySupport.initializeUser(user);
	}

	@Override
	public void initializeGeo(GeographicItem geoItem, String typeFilter) {
		baseActivitySupport.initializeGeo(geoItem, typeFilter);
	}

	public void setBaseActivitySupport(ActivitySupport baseActivitySupport) {
		this.baseActivitySupport = baseActivitySupport;
	}

	@Override
	public void initializeTarget(CalmObject object) {
		baseActivitySupport.initializeTarget(object);
	}

	@Override
	public List<? extends Activity> getActivities() {
		return baseActivitySupport.getActivities();
	}

	@Override
	public String getDateTimeLabel(Activity activity) {
		return baseActivitySupport.getDateTimeLabel(activity);
	}

	@Override
	public String getFrom(Activity activity) {
		return baseActivitySupport.getFrom(activity);
	}

	@Override
	public String getFromUrl(Activity activity) {
		return baseActivitySupport.getFromUrl(activity);
	}

	@Override
	public CalmObject getTarget(Activity a) {
		return baseActivitySupport.getTarget(a);
	}

	@Override
	public User getUser(Activity a) {
		return baseActivitySupport.getUser(a);
	}

	@Override
	public String getActivityIconUrl(Activity activity) {
		final CalmObject o = getTarget(activity);
		final List<? extends Media> media = o.get(Media.class);
		if (media != null && !media.isEmpty()) {
			return media.iterator().next().getMiniThumbUrl();
		} else {
			return DisplayHelper.getNoMiniThumbUrl();
		}
	}

	@Override
	public String getActivityIconLinkUrl(Activity activity) {
		final CalmObject o = getTarget(activity);
		return urlService.getOverviewUrl(
				DisplayHelper.getDefaultAjaxContainer(), o);
	}

	@Override
	public String getActivityHtmlLine(Activity activity) {
		return baseActivitySupport.getActivityHtmlLine(activity);
	}

	@Override
	public String getTitle() {
		return baseActivitySupport.getTitle();
	}

	@Override
	public void setTitle(String title) {
		baseActivitySupport.setTitle(title);
	}

	@Override
	public List<Integer> getPagesList() {
		return baseActivitySupport.getPagesList();
	}

	@Override
	public Integer getCurrentPage() {
		return baseActivitySupport.getCurrentPage();
	}

	@Override
	public String getPageUrl(int page) {
		return baseActivitySupport.getPageUrl(page);
	}

	@Override
	public String getActivityHtmlContentId() {
		return baseActivitySupport.getActivityHtmlContentId();
	}

	@Override
	public void setActivityHtmlContentId(String contentId) {
		baseActivitySupport.setActivityHtmlContentId(contentId);
	}
}
