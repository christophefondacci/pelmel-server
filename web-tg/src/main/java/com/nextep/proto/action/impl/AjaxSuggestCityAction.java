package com.nextep.proto.action.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSONArray;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.nextep.geo.model.City;
import com.nextep.geo.model.Country;
import com.nextep.geo.model.GeographicItem;
import com.nextep.geo.model.Place;
import com.nextep.geo.services.GeoService;
import com.nextep.json.model.impl.JsonSuggest;
import com.nextep.media.model.Media;
import com.nextep.media.model.MediaRequestTypes;
import com.nextep.proto.action.base.AbstractAction;
import com.nextep.proto.action.model.SuggestProvider;
import com.nextep.proto.helpers.DisplayHelper;
import com.nextep.proto.helpers.GeoHelper;
import com.nextep.proto.helpers.MediaHelper;
import com.nextep.proto.model.SearchType;
import com.nextep.proto.services.UrlService;
import com.nextep.statistic.model.Statistic;
import com.videopolis.apis.factory.ApisFactory;
import com.videopolis.apis.factory.SearchRestriction;
import com.videopolis.apis.model.ApisRequest;
import com.videopolis.apis.model.SearchStatistic;
import com.videopolis.apis.model.WithCriterion;
import com.videopolis.apis.service.ApiCompositeResponse;
import com.videopolis.calm.exception.CalException;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.Localized;
import com.videopolis.cals.factory.ContextFactory;
import com.videopolis.smaug.common.model.SuggestScope;

public class AjaxSuggestCityAction extends AbstractAction implements
		SuggestProvider {

	private static final long serialVersionUID = -3897665410499160971L;
	private static final Log LOGGER = LogFactory
			.getLog(AjaxSuggestCityAction.class);
	private static final String APIS_ALIAS_CITY = "city";
	private static final String KEY_PREFIX_ICON = "suggest.icon.";
	private static final String KEY_PREFIX_LABEL = "suggest.category.";

	private GeoService geoService;
	private String cityName;
	private String type;
	private String searchType;
	private boolean filterEmptyCities;
	private List<? extends CalmObject> proposals;
	private ApiCompositeResponse response;

	@Override
	protected String doExecute() throws Exception {
		List<SuggestScope> scopes = new ArrayList<SuggestScope>();
		if (type == null || City.CAL_ID.equals(type)) {
			scopes.add(SuggestScope.DESTINATION);
		}
		if (type == null || Place.CAL_TYPE.equals(type)) {
			scopes.add(SuggestScope.PLACE);
		}
		final ApisRequest request = (ApisRequest) ApisFactory
				.createCompositeRequest().addCriterion(
						(WithCriterion) SearchRestriction
								.searchFromText(GeographicItem.class, scopes,
										cityName, 15)
								.aliasedBy(APIS_ALIAS_CITY)
								.with(Statistic.class)
								.with(Media.class, MediaRequestTypes.THUMB));
		response = (ApiCompositeResponse) getApiService().execute(request,
				ContextFactory.createContext(getLocale()));
		proposals = response.getElements(CalmObject.class, APIS_ALIAS_CITY);

		// Filtering City elements without any statistic from the results if
		// needed
		List<CalmObject> filteredProposals = new ArrayList<CalmObject>();
		if (filterEmptyCities) {
			for (CalmObject proposal : proposals) {
				// Getting statistic
				final Statistic stat = proposal.getUnique(Statistic.class);
				if (!(proposal instanceof City)
						|| (stat != null && stat.getRating() != 0)) {
					filteredProposals.add(proposal);
				}
			}
		} else {
			// No filter
			filteredProposals = (List) proposals;
		}

		Collections.sort(filteredProposals, new Comparator<CalmObject>() {
			@Override
			public int compare(CalmObject o1, CalmObject o2) {
				try {
					Statistic stat1 = o1.getUnique(Statistic.class);
					Statistic stat2 = o2.getUnique(Statistic.class);

					return (stat2 == null ? 0 : stat2.getRating())
							- (stat1 == null ? 0 : stat1.getRating());
				} catch (CalException e) {
					LOGGER.error("Unable to sort suggestions : "
							+ DisplayHelper.getName(o1) + " - "
							+ DisplayHelper.getName(o2));
					return 0;
				}
			}
		});
		proposals = filteredProposals;
		return SUCCESS;
	}

	public void setTerm(String cityName) {
		this.cityName = cityName;
	}

	public String getTerm() {
		return cityName;
	}

	public void setGeoService(GeoService geoService) {
		this.geoService = geoService;
	}

	public List<? extends CalmObject> getCities() {
		return proposals;
	}

	@Override
	public String getSuggestionsAsJSON() {
		// First we build a map that categorizes results by type
		final Map<String, List<JsonSuggest>> suggestTypeMap = new HashMap<String, List<JsonSuggest>>();

		for (CalmObject object : proposals) {
			// Extracting highlight
			final SearchStatistic stat = response.getStatistic(object.getKey(),
					SearchStatistic.MATCHED_TEXT);

			String localizationString = null;
			// If we got the highlight we use it as HTML text
			if (stat != null) {
				localizationString = GeoHelper.buildShortLocalizationString(
						object, stat.getStringValue());
			} else {
				// Building fully qualified city name (no HTML)
				localizationString = GeoHelper.buildShortLocalizationString(
						object, DisplayHelper.getName(object));
			}
			String itemName = DisplayHelper.getName(object) + " - "
					+ localizationString;
			Country country = null;
			String objType = null;
			// Differenciating cities / places cases : extracting country for
			// flag image and keeping type
			if (object instanceof City) {
				country = ((City) object).getCountry();
				objType = City.CAL_ID;
			} else if (object instanceof Place) {
				country = ((Place) object).getCity().getCountry();
				objType = Place.CAL_TYPE;
			}
			String cssClass = "suggest-entry";
			if (type == null) {
				cssClass = "suggest-header-item";
			}
			final String flagIconUrl = getUrlService().getStaticUrl(
					"/images/flags/" + country.getKey().getId().toLowerCase()
							+ ".png");
			final String htmlName = "<span class=\"" + cssClass
					+ "\"><img class=\"flag-icon\" src=\"" + flagIconUrl
					+ "\">" + localizationString + "</span>";
			String url = "";
			final UrlService urlService = getUrlService();
			if (!Place.CAL_TYPE.equals(objType) && searchType != null
					&& !"".equals(searchType)) {
				final SearchType sType = SearchType.valueOf(searchType);
				url = urlService.buildSearchUrl(
						DisplayHelper.getDefaultAjaxContainer(), object, sType,
						null, 0);
			} else {
				url = getUrlService().getOverviewUrl(
						DisplayHelper.getDefaultAjaxContainer(), object);
			}
			final JsonSuggest suggest = new JsonSuggest(htmlName, itemName,
					object.getKey().toString(), url);

			// Adding image
			Media media = MediaHelper.getSingleMedia(object);
			if (media != null) {
				suggest.setImageUrl(urlService.getMediaUrl(media
						.getMiniThumbUrl()));
			} else {
				suggest.setImageUrl(urlService.getMediaUrl(MediaHelper
						.getNoMiniThumbUrl(object)));
			}
			// suggest.setImageUrl("/images/flags/"
			// + country.getKey().getId().toLowerCase() + ".png");
			suggest.setMainText(DisplayHelper.getName(object));
			// suggest.setSuffix(localizationString.substring(
			// stat.getStringValue().length() + 1).trim());
			suggest.setSuffix(localizationString.trim());
			List<? extends Place> places = object.get(Place.class);
			suggest.setCount(places.size());

			// Adding lat/lng
			if (object instanceof Localized) {
				final Localized localized = (Localized) object;
				suggest.setLat(localized.getLatitude());
				suggest.setLng(localized.getLongitude());
			}
			// Hashing suggest by type
			List<JsonSuggest> suggests = suggestTypeMap.get(objType);
			if (suggests == null) {
				suggests = new LinkedList<JsonSuggest>();
				suggestTypeMap.put(objType, suggests);
			}
			suggests.add(suggest);
		}
		final Set<String> entries = suggestTypeMap.keySet();
		if (type != null && type.length() == 4) {
			return JSONArray.fromObject(
					suggestTypeMap.values().iterator().next()).toString();
		} else {
			final List<JsonSuggest> citiesArray = new ArrayList<JsonSuggest>();
			for (String entryType : entries) {
				final String iconUrl = getMessageSource().getMessage(
						KEY_PREFIX_ICON + entryType, null, getLocale());
				final String categoryCities = getMessageSource().getMessage(
						KEY_PREFIX_LABEL + entryType, null, getLocale());
				String cssClass = "suggest-section";
				String img = "<img class=\"flag-icon\" src=\"" + iconUrl
						+ "\">";
				if (type == null) {
					cssClass = "suggest-category suggest-category-" + entryType;
					img = "";
				}
				citiesArray.add(new JsonSuggest("<span class=\"" + cssClass
						+ "\">" + img + categoryCities + "</span>", "",
						entryType, "#"));
				citiesArray.addAll(suggestTypeMap.get(entryType));
			}
			return JSONArray.fromObject(citiesArray).toString();
		}

	}

	public void setType(String typeRestriction) {
		this.type = typeRestriction;
	}

	public String getType() {
		return type;
	}

	public void setSearchType(String searchType) {
		this.searchType = searchType;
	}

	public String getSearchType() {
		return searchType;
	}

	public void setFilterEmptyCities(boolean filterEmptyCities) {
		this.filterEmptyCities = filterEmptyCities;
	}

	public boolean isFilterEmptyCities() {
		return filterEmptyCities;
	}
}
