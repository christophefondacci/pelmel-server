package com.nextep.proto.blocks.impl;

import java.util.List;
import java.util.Locale;

import org.springframework.context.MessageSource;

import com.nextep.events.model.CalendarType;
import com.nextep.geo.model.Place;
import com.nextep.proto.blocks.OverviewSupport;
import com.nextep.proto.helpers.DisplayHelper;
import com.nextep.proto.services.UrlService;
import com.nextep.users.model.User;
import com.opensymphony.xwork2.ActionContext;
import com.videopolis.calm.model.CalmObject;

public class PlaceOverviewSupportImpl implements OverviewSupport {

	private static final String ICON_PREFIX = "facet.icon.";
	private MessageSource messageSource;
	private OverviewSupport commonOverviewSupport;
	private String baseUrl;
	private Place place;
	private Locale locale;

	private UrlService urlService;

	@Override
	public void initialize(UrlService urlService, Locale locale,
			CalmObject object, int likesCount, int dislikesCount,
			User currentUser) {
		this.urlService = urlService;
		this.locale = locale;
		this.place = (Place) object;
		commonOverviewSupport.initialize(urlService, locale, object,
				likesCount, dislikesCount, currentUser);
	}

	@Override
	public CalmObject getOverviewObject() {
		return place;
	}

	@Override
	public String getTitle(CalmObject o) {
		String name = DisplayHelper.getName(place);
		// For offline places we prepend a "[CLOSED]" prefix to inform the user
		if (!place.isOnline()) {
			name = messageSource.getMessage("overview.title.offline",
					new Object[] { name }, locale);
		}
		return name;
	}

	@Override
	public String getAddress(CalmObject o) {
		return place.getAddress1();
	}

	@Override
	public String getToolbarActionUrl(String action, String targetHtmlId) {
		if ("edit".equals(action)) {
			return urlService.getPlaceEditionFormUrl("mainContent",
					place.getKey(), place.getPlaceType());
		} else if ("addevent".equals(action)) {
			return urlService.getEventEditionFormUrl(
					DisplayHelper.getDefaultAjaxContainer(), place.getKey(),
					CalendarType.EVENT);
		} else if ("delete".equals(action)) {
			return "/deleteItem?key=" + place.getKey();
		}
		return commonOverviewSupport.getToolbarActionUrl(action, targetHtmlId);
	}

	@Override
	public String getTitleIconUrl(CalmObject o) {
		return baseUrl
				+ messageSource.getMessage(
						ICON_PREFIX + ((Place) o).getPlaceType(), null,
						ActionContext.getContext().getLocale());
	}

	public void setCommonOverviewSupport(OverviewSupport commonOverviewSupport) {
		this.commonOverviewSupport = commonOverviewSupport;
	}

	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	@Override
	public String getToolbarLabel(String action) {
		return commonOverviewSupport.getToolbarLabel(action);
	}

	@Override
	public List<String> getAdditionalActions() {
		return commonOverviewSupport.getAdditionalActions();
	}

	@Override
	public String getToolbarActionIconUrl(String action) {
		return commonOverviewSupport.getToolbarActionIconUrl(action);
	}

	@Override
	public int getLikesCount() {
		return commonOverviewSupport.getLikesCount();
	}

	@Override
	public int getDislikesCount() {
		return commonOverviewSupport.getDislikesCount();
	}

	@Override
	public boolean isCurrentOwner(User user) {
		return commonOverviewSupport.isCurrentOwner(user);
	}

	@Override
	public boolean isUpdatable() {
		return commonOverviewSupport.isUpdatable();
	}

	@Override
	public String getOwnershipInfoLabel(User user) {
		return commonOverviewSupport.getOwnershipInfoLabel(user);
	}
}
