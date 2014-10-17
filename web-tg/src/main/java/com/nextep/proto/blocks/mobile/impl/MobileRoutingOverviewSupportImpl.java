package com.nextep.proto.blocks.mobile.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.nextep.proto.blocks.OverviewSupport;
import com.nextep.proto.services.UrlService;
import com.nextep.users.model.User;
import com.videopolis.calm.model.CalmObject;

/**
 * This implementation is generic to every type of overview and is able to route
 * to the proper implementation based on the type of the object passed to the
 * initializer.
 * 
 * @author cfondacci
 * 
 */
public class MobileRoutingOverviewSupportImpl implements OverviewSupport {
	private OverviewSupport baseOverviewSupport;
	private Map<String, OverviewSupport> typedOverviewSupportMap = new HashMap<String, OverviewSupport>();;

	@Override
	public void initialize(UrlService urlService, Locale locale,
			CalmObject object, int likesCount, int dislikesCount,
			User currentUser) {
		baseOverviewSupport = typedOverviewSupportMap.get(object.getKey()
				.getType());
		baseOverviewSupport.initialize(urlService, locale, object, likesCount,
				dislikesCount, currentUser);
	}

	@Override
	public CalmObject getOverviewObject() {
		return baseOverviewSupport.getOverviewObject();
	}

	@Override
	public String getTitle(CalmObject o) {
		return baseOverviewSupport.getTitle(o);
	}

	@Override
	public String getTitleIconUrl(CalmObject o) {
		return baseOverviewSupport.getTitleIconUrl(o);
	}

	@Override
	public String getAddress(CalmObject o) {
		return baseOverviewSupport.getAddress(o);
	}

	@Override
	public String getToolbarActionUrl(String action, String targetHtmlId) {
		return baseOverviewSupport.getToolbarActionUrl(action, targetHtmlId);
	}

	@Override
	public String getToolbarLabel(String action) {
		return baseOverviewSupport.getToolbarLabel(action);
	}

	public void setTypedOverviewSupportMap(
			Map<String, OverviewSupport> typedOverviewSupportMap) {
		this.typedOverviewSupportMap = typedOverviewSupportMap;
	}

	@Override
	public List<String> getAdditionalActions() {
		return baseOverviewSupport.getAdditionalActions();
	}

	@Override
	public String getToolbarActionIconUrl(String action) {
		return baseOverviewSupport.getToolbarActionIconUrl(action);
	}

	@Override
	public int getLikesCount() {
		return baseOverviewSupport.getLikesCount();
	}

	@Override
	public int getDislikesCount() {
		return baseOverviewSupport.getDislikesCount();
	}

	@Override
	public boolean isCurrentOwner(User user) {
		return baseOverviewSupport.isCurrentOwner(user);
	}

	@Override
	public boolean isUpdatable() {
		return baseOverviewSupport.isUpdatable();
	}

	@Override
	public String getOwnershipInfoLabel(User user) {
		return baseOverviewSupport.getOwnershipInfoLabel(user);
	}
}
