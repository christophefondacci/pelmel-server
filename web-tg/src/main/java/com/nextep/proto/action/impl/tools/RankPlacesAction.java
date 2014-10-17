package com.nextep.proto.action.impl.tools;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.nextep.activities.model.Activity;
import com.nextep.cal.util.services.CalPersistenceService;
import com.nextep.geo.model.City;
import com.nextep.geo.model.Place;
import com.nextep.proto.action.base.AbstractAction;
import com.nextep.proto.helpers.SearchHelper;
import com.nextep.proto.model.Constants;
import com.nextep.proto.spring.ContextHolder;
import com.nextep.smaug.service.SearchPersistenceService;
import com.nextep.statistic.model.MutableStatistic;
import com.nextep.statistic.model.Statistic;
import com.videopolis.apis.factory.ApisFactory;
import com.videopolis.apis.factory.SearchRestriction;
import com.videopolis.apis.model.ApisRequest;
import com.videopolis.apis.model.FacetInformation;
import com.videopolis.apis.model.WithCriterion;
import com.videopolis.apis.service.ApiResponse;
import com.videopolis.calm.factory.CalmFactory;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.cals.factory.ContextFactory;
import com.videopolis.smaug.common.model.FacetCategory;
import com.videopolis.smaug.common.model.SearchScope;
import com.videopolis.smaug.model.FacetCount;

public class RankPlacesAction extends AbstractAction {

	private static final long serialVersionUID = 7771994690749840993L;

	private static final Log LOGGER = LogFactory.getLog(RankPlacesAction.class);
	private static final String APIS_ALIAS_ALL_PLACES = "all";
	private static final int MAX_PLACES = 1000; // Max places in a city

	private CalPersistenceService statisticService;
	private SearchPersistenceService searchService;

	@Override
	protected String doExecute() throws Exception {
		// Preparing a facetting by city
		final Collection<FacetCategory> categories = new ArrayList<FacetCategory>();
		final FacetCategory cityFacetCategory = SearchHelper
				.getCityFacetCategory();
		categories.add(cityFacetCategory);

		// Querying places facetted by city (so that we will query places by
		// city in a loop later)
		final ApisRequest request = (ApisRequest) ApisFactory
				.createCompositeRequest().addCriterion(
						SearchRestriction
								.searchAll(Place.class, SearchScope.CHILDREN,
										10, 0).facettedBy(categories)
								.aliasedBy(APIS_ALIAS_ALL_PLACES));

		// Executing query
		final ApiResponse response = getApiService().execute(request,
				ContextFactory.createContext(getLocale()));

		// We are only interested in the city facets
		FacetInformation facetInfo = response
				.getFacetInformation(SearchScope.CHILDREN);

		// Retrieving the list of city keys with number of places inside
		List<FacetCount> cityPlacesCounts = facetInfo
				.getFacetCounts(cityFacetCategory);

		// Logging
		final int citiesCount = cityPlacesCounts.size();
		LOGGER.info(citiesCount + " cities to process");

		// Processing all cities one by one
		int i = 1;
		for (FacetCount fc : cityPlacesCounts) {
			final String cityKey = fc.getFacet().getFacetCode();
			final ItemKey cityItemKey = CalmFactory.parseKey(cityKey);
			LOGGER.info("[" + i + " / " + citiesCount
					+ "] : Processing city key " + cityKey);

			// Building query that fetches all places in that city with
			// statistics
			final ApisRequest cityPlacesRequest = (ApisRequest) ApisFactory
					.createRequest(cityItemKey.getType())
					.uniqueKey(cityItemKey.getId())
					.with((WithCriterion) SearchRestriction
							.withContained(Place.class, SearchScope.CHILDREN,
									MAX_PLACES, 0).with(Statistic.class)
							.with(Activity.class)).with(Statistic.class);

			// Executing
			final ApiResponse cityPlacesResponse = getApiService().execute(
					cityPlacesRequest,
					ContextFactory.createContext(getLocale()));

			// Retrieving root element : current city
			final City city = (City) cityPlacesResponse.getUniqueElement();
			MutableStatistic cityStat = null;
			List<? extends Statistic> stats = city.get(MutableStatistic.class);
			for (Statistic stat : stats) {
				// We are looking for the overview stat
				if (Constants.VIEW_TYPE_OVERVIEW.equals(stat.getViewType())) {
					cityStat = (MutableStatistic) stat;
					break;
				}
			}

			// Instantiating new city stat if no existing
			if (cityStat == null) {
				cityStat = (MutableStatistic) statisticService
						.createTransientObject();
				cityStat.setItemKey(city.getKey());
				cityStat.setViewType(Constants.VIEW_TYPE_OVERVIEW);
			}

			// Retrieving city's places list
			final List<? extends Place> places = city.get(Place.class);
			int placesCount = places.size();
			LOGGER.info("[" + i + " / " + citiesCount + "] : Processing "
					+ placesCount + " places in " + city.getName());

			// Iterating
			int totalRank = 0;
			int j = 0;
			ContextHolder.toggleWrite();
			for (Place place : places) {
				LOGGER.info("[" + i + " / " + citiesCount + "] [" + j + "/"
						+ placesCount + "] Processing ranking of "
						+ place.getKey() + " - " + place.getName());

				// Getting place stats
				MutableStatistic stat = place.getUnique(MutableStatistic.class);
				if (stat == null) {
					stat = (MutableStatistic) statisticService
							.createTransientObject();
					stat.setItemKey(place.getKey());
					stat.setViewType(Constants.VIEW_TYPE_OVERVIEW);
				}

				// Getting activities
				final List<? extends Activity> activities = place
						.get(Activity.class);

				int placeRank = stat.getViewsCount() * 2;
				for (Activity activity : activities) {
					switch (activity.getActivityType()) {
					case DELETION:
						placeRank += 20;
						break;
					case LOCALIZATION:
						placeRank += 2000;
						break;
					case UPDATE:
						placeRank += 100;
						break;
					case SEO_OPEN:
						break;
					case ABUSE:
					case REMOVAL_REQUESTED:
						placeRank -= 100;
						break;
					case LIKE:
						placeRank += 500;
						break;
					case UNLIKE:
						placeRank -= 500;
						break;
					}

				}
				// Updating ranking
				stat.setRating(placeRank);
				LOGGER.info("[" + i + " / " + citiesCount + "] [" + j + "/"
						+ placesCount + "] Place ranked " + placeRank + " ("
						+ place.getKey() + " - " + place.getName() + ")");
				statisticService.saveItem(stat);
				searchService.updateRating(place.getKey(), placeRank);

				// Incrementing rank because there is a place here
				totalRank++;
				// Updating rank
				totalRank += placeRank;
				j++;
			}
			cityStat.setRating(totalRank);
			LOGGER.info("[" + i + " / " + citiesCount + "] City ranked "
					+ totalRank + " (" + city.getName() + ")");
			try {
				statisticService.saveItem(cityStat);
			} catch (RuntimeException e) {
				LOGGER.error(
						"Unable to save city statistic: " + e.getMessage(), e);
			}
			i++;
		}

		return SUCCESS;
	}

	public void setStatisticService(CalPersistenceService statisticService) {
		this.statisticService = statisticService;
	}

	public List<String> getMessages() {
		return Collections.emptyList();
	}

	public void setSearchService(SearchPersistenceService searchService) {
		this.searchService = searchService;
	}
}
