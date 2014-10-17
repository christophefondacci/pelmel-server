package com.nextep.proto.action.impl;

import java.util.Arrays;
import java.util.Date;

import com.nextep.activities.model.ActivityType;
import com.nextep.activities.model.MutableActivity;
import com.nextep.cal.util.services.CalPersistenceService;
import com.nextep.descriptions.model.Description;
import com.nextep.geo.model.GeographicItem;
import com.nextep.proto.action.base.AbstractAction;
import com.nextep.proto.action.model.CurrentUserAware;
import com.nextep.proto.action.model.DescriptionsUpdateAware;
import com.nextep.proto.blocks.CurrentUserSupport;
import com.nextep.proto.helpers.DisplayHelper;
import com.nextep.proto.model.ActivityConstants;
import com.nextep.proto.model.SearchType;
import com.nextep.proto.services.DescriptionsManagementService;
import com.nextep.proto.services.RightsManagementService;
import com.nextep.proto.spring.ContextHolder;
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

public class DescriptionUpdateAction extends AbstractAction implements
		CurrentUserAware, DescriptionsUpdateAware {

	private static final long serialVersionUID = 5072715805795548882L;
	private static final String APIS_ALIAS_PARENT = "parent";

	private DescriptionsManagementService descriptionsManagementService;
	private CalPersistenceService activitiesService;
	private RightsManagementService rightsManagementService;
	private CurrentUserSupport currentUserSupport;
	private String[] descriptionLanguageCode, descriptionKey, description,
			descriptionSourceId;

	private String parentId;
	private String redirectUrl;

	@Override
	protected String doExecute() throws Exception {
		// Parsing the Item key of the descriptions owner
		final ItemKey parentKey = CalmFactory.parseKey(parentId);

		// Building query for current user and description owner
		final ApisRequest request = (ApisRequest) ApisFactory
				.createCompositeRequest()
				.addCriterion(
						currentUserSupport.createApisCriterionFor(
								getNxtpUserToken(), true))
				.addCriterion(
						(ApisCriterion) SearchRestriction
								.uniqueKeys(Arrays.asList(parentKey))
								.aliasedBy(APIS_ALIAS_PARENT)
								.with(Description.class));

		// Executing the query
		ApiCompositeResponse response = (ApiCompositeResponse) getApiService()
				.execute(request, ContextFactory.createContext(getLocale()));

		// Getting parent object
		final CalmObject parent = response.getUniqueElement(CalmObject.class,
				APIS_ALIAS_PARENT);
		getHeaderSupport().initialize(getLocale(), parent, null, null);

		// Checking user connection
		final User user = response.getUniqueElement(User.class,
				CurrentUserSupport.APIS_ALIAS_CURRENT_USER);
		checkCurrentUser(user);

		// Checking if user can update
		if (rightsManagementService.canModify(user, parent)) {
			ContextHolder.toggleWrite();
			// Updating descriptions by delegating to the management service
			final boolean descChanged = descriptionsManagementService
					.updateDescriptions(user, parent, descriptionLanguageCode,
							descriptionKey, description, descriptionSourceId);

			// Creating corresponding activity
			if (descChanged) {
				final MutableActivity activity = (MutableActivity) activitiesService
						.createTransientObject();
				activity.setActivityType(ActivityType.UPDATE);
				activity.setDate(new Date());
				activity.setLoggedItemKey(parent.getKey());
				activity.setUserKey(user.getKey());
				final StringBuilder buf = new StringBuilder();
				buf.append(ActivityConstants.DESCRIPTION_FIELD);
				activity.setExtraInformation(buf.toString());
				ContextHolder.toggleWrite();
				activitiesService.saveItem(activity);
			}

			if (parent instanceof GeographicItem) {
				redirectUrl = getUrlService().buildSearchUrl(
						DisplayHelper.getDefaultAjaxContainer(),
						(GeographicItem) parent, SearchType.BARS);
			} else {
				redirectUrl = getUrlService().getOverviewUrl(
						DisplayHelper.getDefaultAjaxContainer(), parent);
			}
			return SUCCESS;
		} else {
			return FORBIDDEN;
		}

	}

	@Override
	public void setCurrentUserSupport(CurrentUserSupport currentUserSupport) {
		this.currentUserSupport = currentUserSupport;
	}

	@Override
	public CurrentUserSupport getCurrentUserSupport() {
		return currentUserSupport;
	}

	@Override
	public void setDescriptionsManagementService(
			DescriptionsManagementService descriptionsManagementService) {
		this.descriptionsManagementService = descriptionsManagementService;
	}

	@Override
	public void setRightsManagementService(
			RightsManagementService rightsManagementService) {
		this.rightsManagementService = rightsManagementService;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public String getParentId() {
		return parentId;
	}

	@Override
	public void setDescriptionLanguageCode(String[] languages) {
		this.descriptionLanguageCode = languages;
	}

	@Override
	public String[] getDescriptionLanguageCode() {
		return descriptionLanguageCode;
	}

	@Override
	public void setDescriptionKey(String[] descriptionKeys) {
		this.descriptionKey = descriptionKeys;
	}

	@Override
	public String[] getDescriptionKey() {
		return descriptionKey;
	}

	@Override
	public void setDescription(String[] descriptions) {
		this.description = descriptions;
	}

	@Override
	public String[] getDescription() {
		return description;
	}

	@Override
	public void setDescriptionSourceId(String[] descriptionSourceId) {
		this.descriptionSourceId = descriptionSourceId;
	}

	@Override
	public String[] getDescriptionSourceId() {
		return descriptionSourceId;
	}

	public String getRedirectUrl() {
		return redirectUrl;
	}

	public void setActivitiesService(CalPersistenceService activitiesService) {
		this.activitiesService = activitiesService;
	}
}
