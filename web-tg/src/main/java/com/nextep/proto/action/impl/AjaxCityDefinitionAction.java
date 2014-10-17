package com.nextep.proto.action.impl;

import com.nextep.geo.model.City;
import com.nextep.geo.model.GeographicItem;
import com.nextep.proto.action.base.AbstractAction;
import com.videopolis.apis.factory.ApisFactory;
import com.videopolis.apis.model.ApisRequest;
import com.videopolis.apis.service.ApiResponse;
import com.videopolis.calm.factory.CalmFactory;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.cals.factory.ContextFactory;

public class AjaxCityDefinitionAction extends AbstractAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8971969369237663816L;
	private String cityKey;
	private City city;

	@Override
	protected String doExecute() throws Exception {
		final ItemKey cityItemKey = CalmFactory.parseKey(cityKey);
		ApisRequest request = ApisFactory.createRequest(GeographicItem.class)
				.uniqueKey(cityItemKey.getId());

		ApiResponse response = getApiService().execute(request,
				ContextFactory.createContext(getLocale()));

		city = (City) response.getUniqueElement();
		return SUCCESS;
	}

	public void setCityKey(String cityKey) {
		this.cityKey = cityKey;
	}

	public String getCityKey() {
		return cityKey;
	}

	public City getCity() {
		return city;
	}

}
