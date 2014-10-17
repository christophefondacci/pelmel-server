package com.nextep.proto.apis.adapters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.nextep.advertising.model.AdvertisingBanner;
import com.nextep.geo.model.Admin;
import com.nextep.geo.model.City;
import com.nextep.geo.model.Continent;
import com.nextep.geo.model.Country;
import com.nextep.geo.model.Place;
import com.videopolis.apis.exception.ApisException;
import com.videopolis.apis.helper.ApisRegistry;
import com.videopolis.apis.model.ApisContext;
import com.videopolis.apis.model.ApisCustomAdapter;
import com.videopolis.apis.service.ApiMutableCompositeResponse;
import com.videopolis.calm.exception.CalException;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.cals.model.MultiKeyItemsResponse;
import com.videopolis.cals.service.CalService;

public class ApisLocalizedBannersAdapter implements ApisCustomAdapter {

	public static final String APIS_ALIAS = "ads";
	private static final Log LOGGER = LogFactory
			.getLog(ApisLocalizedBannersAdapter.class);

	@Override
	public List<ItemKey> adapt(ApisContext context, CalmObject... parents) {
		final List<ItemKey> keys = new ArrayList<ItemKey>();

		for (CalmObject parent : parents) {
			if (parent instanceof Place) {
				final Place p = (Place) parent;
				keys.add(p.getCity().getKey());
				keys.add(p.getCity().getCountry().getKey());
				keys.add(p.getCity().getCountry().getContinent().getKey());
			} else if (parent instanceof City) {
				final City c = (City) parent;
				keys.add(c.getKey());
				keys.add(c.getCountry().getKey());
				keys.add(c.getCountry().getContinent().getKey());
			} else if (parent instanceof Admin) {
				final Admin a = (Admin) parent;
				keys.add(a.getKey());
				keys.add(a.getCountry().getKey());
				keys.add(a.getCountry().getContinent().getKey());
			} else if (parent instanceof Country) {
				final Country c = (Country) parent;
				keys.add(c.getKey());
				keys.add(c.getContinent().getKey());
			} else if (parent instanceof Continent) {
				keys.add(parent.getKey());
			}
		}
		CalService adsService = ApisRegistry
				.getCalService(AdvertisingBanner.CAL_ID);
		try {
			final MultiKeyItemsResponse response = adsService.getItemsFor(keys,
					context.getCalContext());
			final List<? extends CalmObject> objects = response.getItems();
			final ApiMutableCompositeResponse apiResponse = (ApiMutableCompositeResponse) context
					.getApiResponse();
			apiResponse.setElements(APIS_ALIAS, objects);
		} catch (CalException e) {
			LOGGER.error("Error while getting ads : " + e.getMessage(), e);
		} catch (ApisException e) {
			LOGGER.error("Error while getting ads : " + e.getMessage(), e);
		}
		return Collections.emptyList();
	}

}
