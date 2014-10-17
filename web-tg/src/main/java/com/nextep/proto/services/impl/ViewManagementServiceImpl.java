package com.nextep.proto.services.impl;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.nextep.cal.util.services.CalPersistenceService;
import com.nextep.geo.model.GeographicItem;
import com.nextep.proto.model.SearchType;
import com.nextep.proto.services.ViewManagementService;
import com.nextep.proto.spring.ContextHolder;
import com.nextep.statistic.model.MutableItemView;
import com.nextep.users.model.User;
import com.videopolis.calm.model.CalmObject;

public class ViewManagementServiceImpl implements ViewManagementService {
	private static final Log LOGGER = LogFactory
			.getLog(ViewManagementServiceImpl.class);

	private CalPersistenceService viewsService;

	@Override
	public void logViewedOverview(CalmObject overviewItem, User user) {
		logViewCount(overviewItem, user, null);
	}

	@Override
	public void logViewCount(CalmObject viewedObject, User user, String viewType) {
		// Counting views
		try {
			ContextHolder.toggleWrite();
			final MutableItemView itemView = (MutableItemView) viewsService
					.createTransientObject();
			itemView.setViewedItemKey(viewedObject.getKey());
			if (user != null) {
				itemView.setViewerItemKey(user.getKey());
			}
			if (viewType != null) {
				itemView.setViewType(viewType);
			}
			itemView.setViewDate(new Date());
			viewsService.saveItem(itemView);
		} catch (Throwable t) {
			LOGGER.error("Unable to store view stat : " + t.getMessage(), t);
		}

	}

	@Override
	public void logViewedSearch(GeographicItem geographicItem,
			SearchType searchType, User user) {
		// For the moment, routing to standard overview
		logViewCount(geographicItem, user,
				searchType != null ? searchType.name() : null);
	}

	public void setViewsService(CalPersistenceService viewsService) {
		this.viewsService = viewsService;
	}
}
