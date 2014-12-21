package com.nextep.proto.services.impl;

import java.util.Date;
import java.util.concurrent.Future;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;

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
	@Async
	public Future<CalmObject> logViewedOverview(CalmObject overviewItem,
			User user) {
		logViewCount(overviewItem, user, null);
		return new AsyncResult<CalmObject>(overviewItem);
	}

	@Override
	@Async
	public Future<CalmObject> logViewCount(CalmObject viewedObject, User user,
			String viewType) {
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
		return new AsyncResult<CalmObject>(viewedObject);
	}

	@Override
	@Async
	public Future<CalmObject> logViewedSearch(GeographicItem geographicItem,
			SearchType searchType, User user) {
		// For the moment, routing to standard overview
		logViewCount(geographicItem, user,
				searchType != null ? searchType.name() : null);
		return new AsyncResult<CalmObject>(geographicItem);
	}

	public void setViewsService(CalPersistenceService viewsService) {
		this.viewsService = viewsService;
	}
}
