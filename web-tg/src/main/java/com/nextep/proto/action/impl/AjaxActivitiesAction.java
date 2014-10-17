package com.nextep.proto.action.impl;

import java.util.Arrays;
import java.util.List;

import com.nextep.activities.model.Activity;
import com.nextep.geo.model.GeographicItem;
import com.nextep.proto.action.base.AbstractAction;
import com.nextep.proto.action.model.ActivityAware;
import com.nextep.proto.apis.model.impl.ApisActivitiesHelper;
import com.nextep.proto.blocks.ActivitySupport;
import com.nextep.proto.model.Constants;
import com.nextep.users.model.User;
import com.videopolis.apis.factory.ApisFactory;
import com.videopolis.apis.factory.SearchRestriction;
import com.videopolis.apis.model.ApisCriterion;
import com.videopolis.apis.model.ApisRequest;
import com.videopolis.apis.service.ApiCompositeResponse;
import com.videopolis.calm.factory.CalmFactory;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.cals.factory.ContextFactory;
import com.videopolis.cals.model.PaginationInfo;

public class AjaxActivitiesAction extends AbstractAction implements
		ActivityAware {

	private static final long serialVersionUID = 6480551775993583818L;
	private static final String APIS_ALIAS_PARENT = "parent";

	private ActivitySupport activitySupport;
	private ActivitySupport userActivitySupport;

	private String target;
	private String user;
	private String geoKey;
	private String typeFilter;
	private String htmlId;
	private int page;

	@Override
	protected String doExecute() throws Exception {
		boolean fromUser = false;
		boolean isGeo = false;
		ItemKey parentKey = null;
		if (user != null) {
			fromUser = true;
			parentKey = CalmFactory.parseKey(user);
		} else if (target != null) {
			parentKey = CalmFactory.parseKey(target);
		} else if (geoKey != null) {
			parentKey = CalmFactory.parseKey(geoKey);
			isGeo = true;
		}

		final ApisRequest request = ApisFactory.createCompositeRequest();
		if (fromUser) {
			// In this situation we load activities made by the user specified
			// by the parentKey
			request.addCriterion((ApisCriterion) SearchRestriction
					.uniqueKeys(Arrays.asList(parentKey))
					.aliasedBy(APIS_ALIAS_PARENT)
					.with(ApisActivitiesHelper.withUserActivities(
							Constants.MAX_ACTIVITIES, page)));
		} else if (!isGeo) {
			// Here we list activities for a given object
			request.addCriterion((ApisCriterion) SearchRestriction
					.uniqueKeys(Arrays.asList(parentKey))
					.aliasedBy(APIS_ALIAS_PARENT)
					.with(ApisActivitiesHelper.withActivities(
							Constants.MAX_ACTIVITIES, page)));
		} else {
			// Here we list activities made in a geographical district
			request.addCriterion((ApisCriterion) SearchRestriction
					.uniqueKeys(Arrays.asList(parentKey))
					.aliasedBy(APIS_ALIAS_PARENT)
					.with(ApisActivitiesHelper.withGeoActivities(
							Constants.MAX_ACTIVITIES, page, typeFilter)));
		}
		final ApiCompositeResponse response = (ApiCompositeResponse) getApiService()
				.execute(request, ContextFactory.createContext(getLocale()));

		// Retrieving parent
		final CalmObject parent = response.getUniqueElement(CalmObject.class,
				APIS_ALIAS_PARENT);
		// Initializing the activity support
		final List<? extends Activity> activities = parent.get(Activity.class);
		final PaginationInfo pagination = response
				.getPaginationInfo(Activity.class);
		getActivitySupport().initialize(getUrlService(), getLocale(),
				pagination, activities);

		// Initializing user or target
		if (fromUser) {
			getActivitySupport().initializeUser((User) parent);
		} else if (!isGeo) {
			getActivitySupport().initializeTarget(parent);
		} else {
			getActivitySupport().initializeGeo((GeographicItem) parent,
					typeFilter);
		}
		if (htmlId != null) {
			getActivitySupport().setActivityHtmlContentId(htmlId);
		}
		getHeaderSupport().initialize(getLocale(), parent, null, null);
		return SUCCESS;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public String getTarget() {
		return target;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getUser() {
		return user;
	}

	public void setGeoKey(String geoKey) {
		this.geoKey = geoKey;
	}

	public String getGeoKey() {
		return geoKey;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public String getHtmlId() {
		return htmlId;
	}

	public void setHtmlId(String htmlId) {
		this.htmlId = htmlId;
	}

	public void setTypeFilter(String typeFilter) {
		this.typeFilter = typeFilter;
	}

	public String getTypeFilter() {
		return typeFilter;
	}

	@Override
	public void setActivitySupport(ActivitySupport activitySupport) {
		this.activitySupport = activitySupport;
	}

	public void setUserActivitySupport(ActivitySupport userActivitySupport) {
		this.userActivitySupport = userActivitySupport;
	}

	@Override
	public ActivitySupport getActivitySupport() {
		return user != null ? userActivitySupport : activitySupport;
	}
}
