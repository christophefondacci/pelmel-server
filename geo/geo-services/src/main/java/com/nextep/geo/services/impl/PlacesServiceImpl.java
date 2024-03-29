package com.nextep.geo.services.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.nextep.cal.util.helpers.CalHelper;
import com.nextep.cal.util.model.CalDao;
import com.nextep.cal.util.services.CalPersistenceService;
import com.nextep.geo.dao.GeoDao;
import com.nextep.geo.model.City;
import com.nextep.geo.model.MutablePlace;
import com.nextep.geo.model.Place;
import com.nextep.geo.model.impl.PlaceImpl;
import com.nextep.geo.model.impl.RequestTypeListPlaces;
import com.nextep.geo.model.impl.RequestTypeWithAlternates;
import com.videopolis.calm.exception.CalException;
import com.videopolis.calm.helper.Assert;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.calm.model.RequestType;
import com.videopolis.cals.model.CalContext;
import com.videopolis.cals.model.ItemsResponse;
import com.videopolis.cals.model.PaginatedItemsResponse;
import com.videopolis.cals.model.RequestSettings;
import com.videopolis.cals.model.impl.ItemsResponseImpl;
import com.videopolis.cals.model.impl.PaginatedItemsResponseImpl;
import com.videopolis.cals.service.base.AbstractCalService;

public class PlacesServiceImpl extends AbstractCalService implements CalPersistenceService {

	private static final Log LOGGER = LogFactory.getLog(PlacesServiceImpl.class);
	private GeoDao geoDao;

	@Override
	public ItemsResponse getItems(List<ItemKey> ids, CalContext context) throws CalException {
		// Unwrapping places id
		final List<Object> idList = new ArrayList<Object>();
		boolean facebookIds = false;
		for (ItemKey key : ids) {
			if (Place.CAL_TYPE.equals(key.getType())) {
				idList.add(key.getNumericId());
			} else if (Place.CAL_FB_TYPE.equals(key.getType())) {
				facebookIds = true;
				idList.add(key.getId());
			} else {
				throw new CalException("Could only getItems of PLAC type");
			}
		}
		final ItemsResponseImpl response = new ItemsResponseImpl();
		List<Place> places;
		if (!facebookIds) {
			places = geoDao.getPlaces((List) idList);
		} else {
			places = geoDao.getPlacesFromFacebookId((List) idList);
		}
		// Filling geo alternates of place localization
		final Map<Long, List<CalmObject>> alternateIdMap = new HashMap<Long, List<CalmObject>>();
		for (Place p : places) {
			if (LOGGER.isTraceEnabled()) {
				LOGGER.trace("Filling alternate for place " + p.getKey().toString());
			}
			GeoServiceHelper.fillAlternateMap(alternateIdMap, p);
		}
		GeoServiceHelper.fillAlternatesFromMap(geoDao, alternateIdMap, context.getLocale());
		// Reordering elements
		places = reorderCalmObjects(ids, places);
		response.setItems(places);
		return response;
	}

	@Override
	public PaginatedItemsResponse getPaginatedItemsFor(ItemKey itemKey, CalContext context, int resultsPerPage,
			int pageNumber) throws CalException {
		if (City.CAL_ID.equals(itemKey.getType())) {
			List<Place> places = geoDao.getPlacesInCity(itemKey.getNumericId(), resultsPerPage, pageNumber);
			// Filling geo alternates of place localization
			final Map<Long, List<CalmObject>> alternateIdMap = new HashMap<Long, List<CalmObject>>();
			for (Place p : places) {
				GeoServiceHelper.fillAlternateMap(alternateIdMap, p);
			}
			GeoServiceHelper.fillAlternatesFromMap(geoDao, alternateIdMap, context.getLocale());
			// Building response
			final PaginatedItemsResponseImpl response = new PaginatedItemsResponseImpl(resultsPerPage, pageNumber);
			response.setItems(places);
			return response;
		} else {
			final List<Place> places = geoDao.getPlacesFor(itemKey, resultsPerPage, pageNumber);
			// Filling geo alternates of place localization
			final Map<Long, List<CalmObject>> alternateIdMap = new HashMap<Long, List<CalmObject>>();
			for (Place p : places) {
				GeoServiceHelper.fillAlternateMap(alternateIdMap, p);
			}
			GeoServiceHelper.fillAlternatesFromMap(geoDao, alternateIdMap, context.getLocale());
			final int placesCount = geoDao.getPlacesForCount(itemKey);
			final PaginatedItemsResponseImpl response = new PaginatedItemsResponseImpl(resultsPerPage, pageNumber);
			response.setItemCount(placesCount);
			response.setItems(places);
			int pages = placesCount / resultsPerPage;
			int pagesMod = placesCount % resultsPerPage;
			response.setPageCount(pages + (pagesMod > 0 ? 1 : 0));
			return response;
		}
	}

	@Override
	public ItemsResponse listItems(CalContext context, RequestType rt, RequestSettings requestSettings)
			throws CalException {
		ItemsResponse r = null;
		if (rt instanceof RequestTypeListPlaces) {
			final RequestTypeListPlaces requestType = (RequestTypeListPlaces) rt;
			final List<Place> places = geoDao.listPlaces(requestType);
			final int placesCount = (int) geoDao.countPlaces(requestType);
			final PaginatedItemsResponseImpl paginatedResponse = new PaginatedItemsResponseImpl(
					requestType.getPageSize(), requestType.getPage());
			paginatedResponse.setItems(places);
			paginatedResponse.setItemCount(placesCount);
			paginatedResponse.setPageCount(CalHelper.getPageCount(requestType.getPageSize(), placesCount));
			r = paginatedResponse;
		} else {
			final ItemsResponseImpl response = new ItemsResponseImpl();
			final List<Place> places = geoDao.listPlaces();
			response.setItems(places);
			if (rt instanceof RequestTypeWithAlternates) {
				final Map<Long, List<CalmObject>> alternateIdMap = new HashMap<Long, List<CalmObject>>();
				for (Place p : places) {
					GeoServiceHelper.fillAlternateMap(alternateIdMap, p);
				}
				GeoServiceHelper.fillAlternatesFromMap(geoDao, alternateIdMap, null);
			}
			r = response;
		}
		return r;
	}

	@Override
	public Class<? extends CalmObject> getProvidedClass() {
		return Place.class;
	}

	@Override
	public String getProvidedType() {
		return Place.CAL_TYPE;
	}

	@Override
	public Collection<String> getSupportedInputTypes() {
		return Arrays.asList(Place.CAL_TYPE, Place.CAL_FB_TYPE);
	}

	@Override
	protected ItemsResponse getItemsFor(ItemKey itemKey, CalContext context) throws CalException {
		final List<Place> places = geoDao.getPlacesFor(itemKey);
		// Filling geo alternates of place localization
		final Map<Long, List<CalmObject>> alternateIdMap = new HashMap<Long, List<CalmObject>>();
		for (Place p : places) {
			GeoServiceHelper.fillAlternateMap(alternateIdMap, p);
		}
		GeoServiceHelper.fillAlternatesFromMap(geoDao, alternateIdMap, context.getLocale());
		final ItemsResponseImpl response = new ItemsResponseImpl();
		response.setItems(places);
		return response;
	}

	public void setGeoDao(GeoDao geoDao) {
		this.geoDao = geoDao;
	}

	@Override
	public void saveItem(CalmObject object) {
		final MutablePlace p = (MutablePlace) object;
		p.setLastUpdateTime(new Date());
		((CalDao<?>) geoDao).save(object);
	}

	@Override
	public List<? extends CalmObject> setItemFor(ItemKey contributedItemKey, ItemKey... internalItemKeys)
			throws CalException {
		Assert.notNull(internalItemKeys, "Need to define the place item key");
		Assert.notNull(contributedItemKey, "Need to define the item to assign a place to");
		return geoDao.bindPlaces(contributedItemKey, Arrays.asList(internalItemKeys));
	}

	@Override
	public CalmObject createTransientObject() {
		return new PlaceImpl();
	}
}
