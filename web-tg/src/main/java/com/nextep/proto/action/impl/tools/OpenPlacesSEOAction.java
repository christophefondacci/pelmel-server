package com.nextep.proto.action.impl.tools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.nextep.activities.model.Activity;
import com.nextep.activities.model.ActivityType;
import com.nextep.activities.model.MutableActivity;
import com.nextep.activities.model.impl.RequestTypeLatestActivities;
import com.nextep.cal.util.services.CalPersistenceService;
import com.nextep.descriptions.model.Description;
import com.nextep.descriptions.model.DescriptionRequestType;
import com.nextep.geo.model.MutablePlace;
import com.nextep.geo.model.Place;
import com.nextep.proto.action.base.AbstractAction;
import com.nextep.proto.apis.adapters.ApisActivityTargetKeyAdapter;
import com.nextep.proto.apis.adapters.ApisDescriptionItemKeyAdapter;
import com.nextep.proto.blocks.PopularSupport;
import com.nextep.proto.spring.ContextHolder;
import com.nextep.smaug.service.SearchPersistenceService;
import com.videopolis.apis.factory.ApisFactory;
import com.videopolis.apis.factory.SearchRestriction;
import com.videopolis.apis.model.ApisCriterion;
import com.videopolis.apis.model.ApisItemKeyAdapter;
import com.videopolis.apis.model.ApisRequest;
import com.videopolis.apis.service.ApiCompositeResponse;
import com.videopolis.calm.exception.CalException;
import com.videopolis.calm.factory.CalmFactory;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.cals.factory.ContextFactory;

public class OpenPlacesSEOAction extends AbstractAction {

	private static final long serialVersionUID = -8924871749070579819L;
	private static final Log LOGGER = LogFactory
			.getLog(OpenPlacesSEOAction.class);
	private static final ApisItemKeyAdapter activityTargetKeyAdapter = new ApisActivityTargetKeyAdapter();
	private static final ApisItemKeyAdapter descriptionKeyAdapter = new ApisDescriptionItemKeyAdapter();

	private static final String APIS_ALIAS_RECENT_CHANGES = "recent";
	private static final String APIS_ALIAS_DESCRIPTIONS = "descs";

	// Injected supports & services
	private PopularSupport popularActivitiesSupport;
	private CalPersistenceService placesService;
	private CalPersistenceService activitiesService;
	private SearchPersistenceService searchService;

	private int placesToOpen = 0; // Number of requested places to open
	private int maxFetchedActivities = 3000;
	private boolean openRegularPlaces;
	private boolean forceOpen; // Indicates that we force indexation of already
								// opened places

	@Override
	protected String doExecute() throws Exception {
		final ApisRequest request = (ApisRequest) ApisFactory
				.createCompositeRequest()
				.addCriterion(
						(ApisCriterion) SearchRestriction
								.list(Activity.class,
										new RequestTypeLatestActivities(
												maxFetchedActivities,
												ActivityType.CREATION,
												ActivityType.UPDATE,
												ActivityType.COMMENT))
								.aliasedBy(APIS_ALIAS_RECENT_CHANGES)
								.addCriterion(
										SearchRestriction
												.adaptKey(activityTargetKeyAdapter)));

		final ApiCompositeResponse response = (ApiCompositeResponse) getApiService()
				.execute(request, ContextFactory.createContext(getLocale()));
		final List<? extends Activity> activities = response.getElements(
				Activity.class, APIS_ALIAS_RECENT_CHANGES);

		// Preparing our list of places to open
		final List<Place> places = new ArrayList<Place>();
		final Set<ItemKey> placeKeys = new HashSet<ItemKey>();

		// We open everything that needs to be
		for (Activity activity : activities) {
			// Unwrapping object from activity
			try {
				final Place place = activity.getUnique(Place.class);
				// We add the place to places to open if it is not yet opened or
				// if we are in forced mode
				if (place != null && !place.isIndexed() || forceOpen) {
					if (!placeKeys.contains(place.getKey())) {
						places.add(place);
						placeKeys.add(place.getKey());
					}
				}
			} catch (CalException e) {
				LOGGER.error("Unable to extract object from activity "
						+ activity.getKey() + " : " + e.getMessage(), e);
			}
		}

		// If we got enough activities to open what is requested
		if (openRegularPlaces && (places.size() < placesToOpen)) {
			// This request retrieves the longest descriptions with their
			// associated element
			int currentPage = 0;
			while (places.size() < placesToOpen) {
				final ApisRequest descRequest = (ApisRequest) ApisFactory
						.createCompositeRequest()
						.addCriterion(
								(ApisCriterion) SearchRestriction
										.list(Description.class,
												DescriptionRequestType.SORT_BY_LENGTH_DESC)
										.paginatedBy(placesToOpen, currentPage)
										.aliasedBy(APIS_ALIAS_DESCRIPTIONS)
										.addCriterion(
												SearchRestriction
														.adaptKey(descriptionKeyAdapter)));

				// Executing the query
				final ApiCompositeResponse descResponse = (ApiCompositeResponse) getApiService()
						.execute(descRequest,
								ContextFactory.createContext(getLocale()));

				final List<? extends Description> descriptions = descResponse
						.getElements(Description.class, APIS_ALIAS_DESCRIPTIONS);
				// Processing descriptions
				for (Description d : descriptions) {

					// Getting described object
					final CalmObject describedObject = d
							.getUnique(CalmObject.class);
					// Is it a place ?
					if (describedObject != null
							&& Place.CAL_TYPE.equals(describedObject.getKey()
									.getType())) {
						final Place p = (Place) describedObject;
						// Have we already processed it ?
						if (!placeKeys.contains(p.getKey())) {
							if (p.isOnline() && (!p.isIndexed() || forceOpen)) {
								places.add(p);
								placeKeys.add(p.getKey());
							}
						}
					}
				}
				// Going to next page
				currentPage++;
			}
		}

		// Now we toggle indexation for what we have in the list
		ContextHolder.toggleWrite();
		for (Place place : places) {
			LOGGER.info("Storing indexed place " + place.getKey() + " : "
					+ place.getName());
			// Flagging as indexed
			if (!place.isIndexed()) {
				// Opening place to SEO
				((MutablePlace) place).setIndexed(true);
				placesService.saveItem(place);
				// Adding the activity so that the place will appear in recent
				// changes
				final MutableActivity activity = (MutableActivity) activitiesService
						.createTransientObject();
				activity.add(place.getCity());
				activity.setActivityType(ActivityType.SEO_OPEN);
				activity.setDate(new Date());
				activity.setLoggedItemKey(place.getKey());
				// Using a fake non-existing internal admin user (anyway it
				// should not be used)
				activity.setUserKey(CalmFactory.parseKey("USER0"));
				activitiesService.saveItem(activity);
			}
			// Storing in index
			searchService.toggleIndex(place, place.isIndexed() ? 1 : 0);
		}
		return SUCCESS;
	}

	public List<String> getMessages() {
		return Arrays.asList("Places successfully SEO opened");
	}

	public void setPlacesToOpen(int placesToOpen) {
		this.placesToOpen = placesToOpen;
	}

	public int getPlacesToOpen() {
		return placesToOpen;
	}

	public void setPopularActivitiesSupport(
			PopularSupport popularActivitiesSupport) {
		this.popularActivitiesSupport = popularActivitiesSupport;
	}

	public void setPlacesService(CalPersistenceService placesService) {
		this.placesService = placesService;
	}

	public void setSearchService(SearchPersistenceService searchService) {
		this.searchService = searchService;
	}

	public void setOpenRegularPlaces(boolean openRegularPlaces) {
		this.openRegularPlaces = openRegularPlaces;
	}

	public boolean getOpenRegularPlaces() {
		return openRegularPlaces;
	}

	public void setForceOpen(boolean forceOpen) {
		this.forceOpen = forceOpen;
	}

	public boolean isForceOpen() {
		return forceOpen;
	}

	public void setActivitiesService(CalPersistenceService activitiesService) {
		this.activitiesService = activitiesService;
	}

	public void setMaxFetchedActivities(int maxFetchedActivities) {
		this.maxFetchedActivities = maxFetchedActivities;
	}
}
