package com.nextep.proto.blocks.impl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.MessageSource;

import com.nextep.events.model.Event;
import com.nextep.events.model.EventSeries;
import com.nextep.geo.model.City;
import com.nextep.geo.model.GeographicItem;
import com.nextep.geo.model.Place;
import com.nextep.proto.blocks.OverviewSupport;
import com.nextep.proto.helpers.DisplayHelper;
import com.nextep.proto.model.Constants;
import com.nextep.proto.model.ImageConstants;
import com.nextep.proto.services.UrlService;
import com.nextep.users.model.User;
import com.opensymphony.xwork2.ActionContext;
import com.videopolis.calm.exception.CalException;
import com.videopolis.calm.model.CalmObject;

public class EventOverviewSupportImpl implements OverviewSupport {

	private static final String TRANSLATION_KEY_ACTION_PREFIX = "toolbar.event.";
	private static final Log LOGGER = LogFactory
			.getLog(EventOverviewSupportImpl.class);
	private static final String ACTION_CODE_OPEN_SERIES = "openSeries";
	private MessageSource messageSource;

	private DateFormat dayDateFormat;
	private DateFormat hourDateFormat;
	private OverviewSupport commonOverviewSupport;
	private UrlService urlService;
	private Event event;
	private String baseUrl;
	private Locale locale;

	@Override
	public void initialize(UrlService urlService, Locale locale,
			CalmObject object, int likesCount, int dislikesCount,
			User currentUser) {
		this.urlService = urlService;
		this.locale = locale;
		this.event = (Event) object;
		commonOverviewSupport.initialize(urlService, locale, object,
				likesCount, dislikesCount, currentUser);
		final Locale l = ActionContext.getContext().getLocale();
		dayDateFormat = new SimpleDateFormat("dd MMMM yyyy", l);
		hourDateFormat = new SimpleDateFormat("HH:mm", l);
	}

	@Override
	public CalmObject getOverviewObject() {
		return event;
	}

	@Override
	public String getTitle(CalmObject o) {
		return DisplayHelper.getName(event);
	}

	@Override
	public String getAddress(CalmObject o) {
		try {
			final GeographicItem geoItem = o.getUnique(GeographicItem.class);
			if (geoItem instanceof Place) {
				return ((Place) geoItem).getAddress1();
			} else if (geoItem instanceof City) {
				return DisplayHelper.getName(geoItem);
			}
		} catch (CalException e) {
			LOGGER.error("Unable to extract event address of " + o.getKey()
					+ " : " + e.getMessage());
		}
		return "";
	}

	@Override
	public String getToolbarActionUrl(String action, String targetHtmlId) {
		if (Constants.ACTION_CODE_EDIT.equals(action)) {
			return urlService.getEventEditionFormUrl(targetHtmlId,
					event.getKey());
		} else if (ACTION_CODE_OPEN_SERIES.equals(action)) {
			if (event.getSeriesKey() != null) {
				try {
					final EventSeries series = event
							.getUnique(EventSeries.class);
					if (series != null) {
						return urlService
								.getEventOverviewUrl(
										DisplayHelper.getDefaultAjaxContainer(),
										series);
					}
				} catch (CalException e) {
					LOGGER.error(
							"Parent event series is not available for event "
									+ event.getKey() + " / parent series "
									+ event.getSeriesKey() + ": "
									+ e.getMessage(), e);
				}
			}
			return "#";
		} else {
			return commonOverviewSupport.getToolbarActionUrl(action,
					targetHtmlId);
		}
	}

	@Override
	public String getToolbarActionIconUrl(String action) {
		if (ACTION_CODE_OPEN_SERIES.equals(action)) {
			return ImageConstants.ICON_OPEN_EVENT_SERIES;
		}
		return commonOverviewSupport.getToolbarActionIconUrl(action);
	}

	public void setCommonOverviewSupport(OverviewSupport commonOverviewSupport) {
		this.commonOverviewSupport = commonOverviewSupport;
	}

	@Override
	public String getTitleIconUrl(CalmObject o) {
		return baseUrl + "/images/icon-event-24.png";
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	@Override
	public String getToolbarLabel(String action) {
		if (Constants.ACTION_CODE_LIKE.equals(action)
				|| ACTION_CODE_OPEN_SERIES.equals(action)) {
			return messageSource.getMessage(TRANSLATION_KEY_ACTION_PREFIX
					+ action, null, locale);
		} else {
			return commonOverviewSupport.getToolbarLabel(action);
		}
	}

	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	@Override
	public List<String> getAdditionalActions() {
		final List<String> additionalActions = new ArrayList<String>();
		try {
			if (event.getUnique(EventSeries.class) != null) {
				additionalActions.add(ACTION_CODE_OPEN_SERIES);
			} else {
				// Logging only if we should have an event here
				if (event.getSeriesKey() != null) {
					LOGGER.warn("No event series while there should be for event "
							+ event.getKey()
							+ " : expecting series "
							+ event.getSeriesKey());
				}
			}
		} catch (CalException e) {
			LOGGER.error(
					"Error while extracting series from event "
							+ event.getKey() + ": " + e.getMessage(), e);
		}
		additionalActions.addAll(commonOverviewSupport.getAdditionalActions());
		return additionalActions;
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
