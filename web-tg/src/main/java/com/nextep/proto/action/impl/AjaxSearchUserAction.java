package com.nextep.proto.action.impl;

import java.util.Collections;

import com.nextep.geo.model.City;
import com.nextep.geo.model.GeographicItem;
import com.nextep.geo.model.Place;
import com.nextep.proto.action.base.AbstractSearchUserAction;
import com.nextep.users.model.User;
import com.videopolis.apis.exception.ApisException;
import com.videopolis.apis.service.ApiResponse;
import com.videopolis.smaug.common.model.SearchScope;

public class AjaxSearchUserAction extends AbstractSearchUserAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1488033036866379585L;

	@Override
	protected void initialize(ApiResponse response) throws ApisException {
		final GeographicItem geopoint = (GeographicItem) response
				.getUniqueElement();
		getSearchSupport().initialize(getSearchType(), getUrlService(),
				getLocale(), geopoint, geopoint.getName(),
				response.getFacetInformation(SearchScope.CHILDREN),
				response.getPaginationInfo(User.class),
				geopoint.get(User.class));
		getLocalizationSupport().initialize(getSearchType(), getUrlService(),
				getLocale(), geopoint,
				response.getFacetInformation(SearchScope.CHILDREN));
		// getPlacesSupport().initialize(geopoint.get(Place.class));
		getPlaceSearchSupport()
				.initialize(getSearchType(), getUrlService(), getLocale(),
						geopoint, geopoint.getName(),
						response.getFacetInformation(SearchScope.POI),
						response.getPaginationInfo(Place.class),
						Collections.EMPTY_LIST);

		getPopularSupport().initialize(getSearchType(), getLocale(),
				getUrlService(), geopoint, geopoint.get(City.class), null);
	}

}
