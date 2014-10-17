package com.nextep.proto.action.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.sf.json.JSONArray;

import com.nextep.events.model.Event;
import com.nextep.geo.model.GeographicItem;
import com.nextep.geo.model.Place;
import com.nextep.json.model.impl.JsonSuggest;
import com.nextep.proto.action.base.AbstractAction;
import com.nextep.proto.action.model.JsonProvider;
import com.nextep.proto.apis.adapters.ApisFacetToItemKeyAdapter;
import com.nextep.proto.helpers.DisplayHelper;
import com.nextep.proto.helpers.SearchHelper;
import com.nextep.proto.model.SearchType;
import com.nextep.users.model.User;
import com.videopolis.apis.factory.ApisFactory;
import com.videopolis.apis.factory.SearchRestriction;
import com.videopolis.apis.helper.ApisRegistry;
import com.videopolis.apis.model.ApisCriterion;
import com.videopolis.apis.model.ApisCustomAdapter;
import com.videopolis.apis.model.ApisRequest;
import com.videopolis.apis.model.FacetInformation;
import com.videopolis.apis.model.WithCriterion;
import com.videopolis.apis.service.ApiCompositeResponse;
import com.videopolis.calm.factory.CalmFactory;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.cals.factory.ContextFactory;
import com.videopolis.smaug.common.model.Facet;
import com.videopolis.smaug.common.model.FacetCategory;
import com.videopolis.smaug.common.model.SearchScope;

public class AjaxGeoListAction extends AbstractAction implements JsonProvider {

	private static final long serialVersionUID = -3704023228134275468L;
	private static final String APIS_ALIAS_GEO_PARENT = "geo";
	private static final String APIS_ALIAS_GEO_RESULT = "result";
	private static final int MAX_RESULTS = 20;
	private static final String LEVEL_CONTINENT = "continent";
	private static final String LEVEL_COUNTRY = "country";
	private static final String LEVEL_REGION = "region";
	private static final String LEVEL_CITY = "city";

	private String type;
	private String subType;
	private String parent;
	private String level;

	private List<JsonSuggest> suggestions;

	@Override
	public String getJson() {
		return JSONArray.fromObject(suggestions).toString();
	}

	@Override
	protected String doExecute() throws Exception {
		final ItemKey parentKey = parent == null || "".equals(parent.trim()) ? null
				: CalmFactory.parseKey(parent);
		if (type == null || "".equals(type.trim())) {
			type = Place.CAL_TYPE;
			subType = SearchScope.BAR.name().toLowerCase();
		}
		final Class<? extends CalmObject> calClass = ApisRegistry
				.getModelFromType(type);
		final SearchScope scope = SearchHelper.getSearchScope(subType);
		final FacetCategory facet = buildFacetting();
		final ApisCustomAdapter facetAdapter = new ApisFacetToItemKeyAdapter(
				scope, facet, MAX_RESULTS);

		// Building request which looks for the parent item and search for
		// geographic facets (city, region, country, continent) of places
		// in this area.
		ApisRequest request = null;
		if (parentKey != null) {
			request = (ApisRequest) ApisFactory.createCompositeRequest()
					.addCriterion(
							(ApisCriterion) SearchRestriction
									.uniqueKeys(Arrays.asList(parentKey))
									.aliasedBy(APIS_ALIAS_GEO_PARENT)
									.with((WithCriterion) SearchRestriction
											.withContained(calClass, scope, 5,
													0)
											.facettedBy(Arrays.asList(facet))
											.customAdapt(facetAdapter,
													APIS_ALIAS_GEO_RESULT)));
		} else {
			// Here we got no parent
			request = (ApisRequest) ApisFactory.createCompositeRequest()
					.addCriterion(
							(ApisCriterion) SearchRestriction
									.searchAll(calClass, scope, 5, 0)
									.facettedBy(Arrays.asList(facet))
									.customAdapt(facetAdapter,
											APIS_ALIAS_GEO_RESULT));
		}

		// Executing the request
		final ApiCompositeResponse response = (ApiCompositeResponse) getApiService()
				.execute(request, ContextFactory.createContext(getLocale()));

		// Extracting resulting structures
		final List<? extends GeographicItem> results = response.getElements(
				GeographicItem.class, APIS_ALIAS_GEO_RESULT);
		final FacetInformation facetInfo = response.getFacetInformation(scope);

		// Building suggestions
		suggestions = new ArrayList<JsonSuggest>();
		for (GeographicItem result : results) {
			final Facet f = SearchHelper
					.buildFacetFilters(facet, result.getKey().toString())
					.iterator().next();
			final int facetCount = facetInfo.getFacetCount(f);
			final String url = getSearchUrl(result);
			final JsonSuggest suggest = new JsonSuggest(
					DisplayHelper.getName(result),
					DisplayHelper.getName(result), String.valueOf(facetCount),
					url);
			suggestions.add(suggest);
		}
		return SUCCESS;
	}

	private String getSearchUrl(GeographicItem parent) {
		final SearchType searchType = SearchType.fromPlaceType(subType);
		if (User.CAL_TYPE.equals(type)) {
			return getUrlService().buildSearchUrl(
					DisplayHelper.getDefaultAjaxContainer(), parent,
					SearchType.MEN, null, 0);
		} else if (Place.CAL_TYPE.equals(type)) {
			return getUrlService().buildSearchUrl(
					DisplayHelper.getDefaultAjaxContainer(), parent,
					searchType, null, 0);
		} else if (Event.CAL_ID.equals(type)) {
			return getUrlService().buildSearchUrl(
					DisplayHelper.getDefaultAjaxContainer(), parent,
					SearchType.EVENTS, null, 0);
		}
		return null;
	}

	private FacetCategory buildFacetting() {
		if (LEVEL_CONTINENT.equals(level)) {
			return SearchHelper.getContinentFacetCategory();
		} else if (LEVEL_CITY.equals(level)) {
			return SearchHelper.getCityFacetCategory();
		} else if (LEVEL_REGION.equals(level)) {
			return SearchHelper.getRegionFacetCategory();
		} else if (LEVEL_COUNTRY.equals(level)) {
			return SearchHelper.getCountryFacetCategory();
		}
		return null;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getParent() {
		return parent;
	}

	public void setParent(String parent) {
		this.parent = parent;
	}

	public void setSubType(String subType) {
		this.subType = subType;
	}

	public String getSubType() {
		return subType;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public String getLevel() {
		return level;
	}
}
