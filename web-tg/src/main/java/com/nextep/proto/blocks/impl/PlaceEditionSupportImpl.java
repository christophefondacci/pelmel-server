package com.nextep.proto.blocks.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.context.MessageSource;

import com.nextep.descriptions.model.Description;
import com.nextep.geo.model.GeographicItem;
import com.nextep.geo.model.Place;
import com.nextep.proto.blocks.PlaceEditionSupport;
import com.nextep.proto.helpers.DisplayHelper;
import com.nextep.proto.helpers.GeoHelper;
import com.nextep.proto.model.PlaceType;
import com.nextep.proto.services.RightsManagementService;
import com.nextep.users.model.User;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.Localized;

public class PlaceEditionSupportImpl implements PlaceEditionSupport {

	private static final String TRANSLATION_PLACE_TYPE_PREFIX = "place.type.";
	private MessageSource messageSource;
	private RightsManagementService rightsManagementService;

	private Locale locale;
	private Place place;
	private GeographicItem location;
	private Description description;
	private String placeType;
	private User currentUser;

	@Override
	public void initialize(Locale locale, CalmObject placeOrCity, String placeType, User currentUser) {
		this.locale = locale;
		if (placeOrCity instanceof Place) {
			this.place = (Place) placeOrCity;
			this.location = place.getCity();
			this.description = DisplayHelper.getMainLocaleDescription(place, locale);
		} else {
			this.place = null;
			this.location = (GeographicItem) placeOrCity;
		}
		this.placeType = this.place == null ? placeType : this.place.getPlaceType();
		this.currentUser = currentUser;
	}

	@Override
	public String getPlaceId() {
		if (place != null) {
			return place.getKey().toString();
		} else {
			return null;
		}
	}

	@Override
	public String getName() {
		return place != null ? DisplayHelper.getName(place) : null;
	}

	@Override
	public String getAddress() {
		return place != null ? place.getAddress1() : null;
	}

	@Override
	public List<String> getPlaceTypes() {
		final List<String> placeTypes = new ArrayList<String>();
		final List<PlaceType> authorizedTypes = rightsManagementService.getAvailablePlaceTypes(currentUser, location);
		for (PlaceType type : authorizedTypes) {
			placeTypes.add(type.name());
		}
		return placeTypes;
	}

	@Override
	public String getPlaceTypeLabel(String placeType) {
		return messageSource.getMessage(TRANSLATION_PLACE_TYPE_PREFIX + placeType, null, locale);
	}

	@Override
	public String getSelected(String placeType) {
		if (place != null) {
			return placeType.equals(place.getPlaceType()) ? "selected" : "";
		} else {
			return this.placeType.equals(placeType) ? "selected" : "";
		}
	}

	@Override
	public String getCityId() {
		return location.getKey().toString();
	}

	@Override
	public String getCityName() {
		return GeoHelper.buildShortLocalizationString(location, DisplayHelper.getName(location));
	}

	@Override
	public String getDescription() {
		return description != null ? description.getDescription() : null;
	}

	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	@Override
	public Double getLatitude() {
		if (place != null) {
			return place.getLatitude();
		} else if (location instanceof Localized) {
			return ((Localized) location).getLatitude();
		} else {
			return null;
		}
	}

	@Override
	public Double getLongitude() {
		if (place != null) {
			return place.getLongitude();
		} else if (location instanceof Localized) {
			return ((Localized) location).getLongitude();
		} else {
			return null;
		}
	}

	@Override
	public String getDescriptionItemKey() {
		if (description != null) {
			return description.getKey().toString();
		} else {
			return "";
		}

	}

	@Override
	public String getPlaceType() {
		return placeType;
	}

	public void setRightsManagementService(RightsManagementService rightsManagementService) {
		this.rightsManagementService = rightsManagementService;
	}

	@Override
	public String getFacebookId() {
		return place != null ? place.getFacebookId() : null;
	}
}
