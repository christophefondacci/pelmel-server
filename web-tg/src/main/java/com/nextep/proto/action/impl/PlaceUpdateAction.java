package com.nextep.proto.action.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.nextep.activities.model.ActivityType;
import com.nextep.activities.model.MutableActivity;
import com.nextep.advertising.model.AdvertisingBooster;
import com.nextep.cal.util.services.CalPersistenceService;
import com.nextep.descriptions.model.Description;
import com.nextep.descriptions.model.DescriptionRequestType;
import com.nextep.geo.model.AlternateName;
import com.nextep.geo.model.City;
import com.nextep.geo.model.MutablePlace;
import com.nextep.geo.model.Place;
import com.nextep.json.model.impl.JsonPlace;
import com.nextep.properties.model.Property;
import com.nextep.proto.action.base.AbstractAction;
import com.nextep.proto.action.model.DescriptionsUpdateAware;
import com.nextep.proto.action.model.JsonProvider;
import com.nextep.proto.action.model.PropertiesUpdateAware;
import com.nextep.proto.apis.adapters.ApisPlaceAdm1Adapter;
import com.nextep.proto.apis.adapters.ApisPlaceCountryAdapter;
import com.nextep.proto.apis.adapters.ApisPlaceLocationAdapter;
import com.nextep.proto.apis.model.impl.ApisLocalizationHelper;
import com.nextep.proto.blocks.CurrentUserSupport;
import com.nextep.proto.builders.JsonBuilder;
import com.nextep.proto.model.ActivityConstants;
import com.nextep.proto.model.PlaceType;
import com.nextep.proto.services.DescriptionsManagementService;
import com.nextep.proto.services.PropertiesManagementService;
import com.nextep.proto.services.PuffService;
import com.nextep.proto.services.RightsManagementService;
import com.nextep.proto.spring.ContextHolder;
import com.nextep.smaug.service.SearchPersistenceService;
import com.nextep.statistic.model.Statistic;
import com.nextep.tags.model.Tag;
import com.nextep.users.model.User;
import com.videopolis.apis.factory.ApisFactory;
import com.videopolis.apis.factory.SearchRestriction;
import com.videopolis.apis.model.ApisCriterion;
import com.videopolis.apis.model.ApisRequest;
import com.videopolis.apis.service.ApiCompositeResponse;
import com.videopolis.apis.service.ApiResponse;
import com.videopolis.calm.factory.CalmFactory;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.calm.model.RequestType;
import com.videopolis.cals.factory.ContextFactory;
import com.videopolis.smaug.common.model.SearchScope;

public class PlaceUpdateAction extends AbstractAction implements
		PropertiesUpdateAware, DescriptionsUpdateAware, JsonProvider {

	private static final long serialVersionUID = 1594590269681266362L;

	private static final Log LOGGER = LogFactory
			.getLog(PlaceUpdateAction.class);
	private static final RequestType MAIN_DESC = DescriptionRequestType.SINGLE_DESC;

	private static final String APIS_ALIAS_CITY = "city";
	private static final String APIS_ALIAS_PLACE = "place";
	private static final String APIS_ALIAS_TAGS = "tags";

	private static final String PUFF_FIELD_NAME = "name";
	private static final String PUFF_FIELD_ADDRESS = "address";
	private static final String PUFF_FIELD_PLACE_TYPE = "type";
	private static final String PUFF_FIELD_CITY = "city";
	private static final String PUFF_FIELD_LATITUDE = "latitude";
	private static final String PUFF_FIELD_LONGITUDE = "longitude";
	private static final String PUFF_FIELD_TAGS = "tags";

	private CalPersistenceService placeService;
	private CalPersistenceService activitiesService;
	private CalPersistenceService tagsService;
	private SearchPersistenceService searchService;
	private PuffService puffService;
	private PropertiesManagementService propertiesService;
	private DescriptionsManagementService descriptionManagementService;
	private RightsManagementService rightsManagementService;
	private JsonBuilder jsonBuilder;

	private boolean mobile;

	private String userId;
	private String placeId;
	private String name;
	private String address;
	private String placeType;
	private String cityId;
	private double latitude;
	private double longitude;
	private double cityRadius;
	private boolean highRes;
	private String descriptionItemKey;
	private String[] propertyCode;
	private String[] propertyValue;
	private String[] propertyKey;
	private String newPlaceId;
	private String[] descriptionLanguageCode, descriptionKey, description,
			descriptionSourceId;
	private String[] tags;

	private JsonPlace jsonPlace;

	@Override
	protected String doExecute() throws Exception {
		MutablePlace place = null;
		City city = null;
		final ApisRequest request = ApisFactory.createCompositeRequest();
		ApiCompositeResponse response = null;
		// If a city ID is defined, we fetch the corresponding city
		if (cityId != null) {
			final ItemKey cityKey = CalmFactory.parseKey(cityId);
			request.addCriterion(SearchRestriction.uniqueKeys(
					Arrays.asList(cityKey)).aliasedBy(APIS_ALIAS_CITY));
		} else {
			// Adding the city lookup
			request.addCriterion(ApisLocalizationHelper
					.buildNearestCityCriterion(latitude, longitude, cityRadius));
		}
		// We fetch selected tags definition
		final List<ItemKey> tagKeys = new ArrayList<ItemKey>();
		if (tags != null) {
			for (String tag : tags) {
				final ItemKey tagKey = CalmFactory.parseKey(tag);
				tagKeys.add(tagKey);
			}
			request.addCriterion(SearchRestriction.uniqueKeys(tagKeys)
					.aliasedBy(APIS_ALIAS_TAGS));
		}
		// Adding the criterion that fetches USER
		final ItemKey currentUserKey = CalmFactory.createKey(User.TOKEN_TYPE,
				getNxtpUserToken());
		request.addCriterion(SearchRestriction.alternateKey(User.class,
				currentUserKey).aliasedBy(
				CurrentUserSupport.APIS_ALIAS_CURRENT_USER));

		// If a placeId is defined we fetch it for update
		if (placeId != null && !"".equals(placeId)) {
			// We fetch the Place from CAL provider when existing (update)
			final ItemKey placeKey = CalmFactory.parseKey(placeId);
			request.addCriterion((ApisCriterion) SearchRestriction
					.uniqueKeys(Arrays.asList(placeKey))
					.aliasedBy(APIS_ALIAS_PLACE).with(Property.class)
					.with(Description.class).with(Tag.class)
					.with(AdvertisingBooster.class));
			response = (ApiCompositeResponse) getApiService().execute(request,
					ContextFactory.createContext(getLocale()));
			place = response.getUniqueElement(MutablePlace.class,
					APIS_ALIAS_PLACE);

		} else {
			// We create a new transient place for creation
			place = (MutablePlace) placeService.createTransientObject();
			// We execute the APIS request to fetch city
			response = (ApiCompositeResponse) getApiService().execute(request,
					ContextFactory.createContext(getLocale()));
		}

		// Retrieving city (either if specified explicitely or through nearby
		// search)
		if (cityId != null) {
			// Explicit city
			city = response.getUniqueElement(City.class, APIS_ALIAS_CITY);
		} else {
			// Extracting nearest city
			city = response.getUniqueElement(City.class,
					ApisLocalizationHelper.APIS_ALIAS_CITY_NEARBY);
		}

		final User user = response.getUniqueElement(User.class,
				CurrentUserSupport.APIS_ALIAS_CURRENT_USER);
		checkCurrentUser(user);

		// Checking whether we are allowed to make a change
		if (!rightsManagementService.canModify(user, place)) {
			setErrorMessage(getText("rights.forbidden.desc"));
			return error(403);
		}
		// Keeping old values for later PUFF log
		final String oldName = place.getName();
		final String oldAddress = place.getAddress1();
		final String oldPlaceType = place.getPlaceType();
		final String oldCity = place.getCity() == null ? null : place.getCity()
				.getKey().toString();
		final String oldLat = String.valueOf(place.getLatitude());
		final String oldLng = String.valueOf(place.getLongitude());
		final List<ItemKey> oldTagKeys = new ArrayList<ItemKey>();
		for (Tag tag : place.get(Tag.class)) {
			oldTagKeys.add(tag.getKey());
		}
		// Updating place
		place.setName(name);
		place.setAddress1(address);
		place.setPlaceType(placeType == null ? PlaceType.bar.name() : placeType);
		place.setCity(city);
		// Setting localization
		if (latitude != 0 && longitude != 0) {
			place.setLatitude(latitude);
			place.setLongitude(longitude);
		}
		boolean isCreation = place.getKey() == null;
		ContextHolder.toggleWrite();
		// Every updated place becomes automatically indexable
		place.setIndexed(true);
		placeService.saveItem(place);
		newPlaceId = place.getKey().toString();

		if (!mobile) {
			// Storing tags
			tagsService.setItemFor(place.getKey(),
					tagKeys.toArray(new ItemKey[tagKeys.size()]));
		}
		// Now we have the new place id
		final ItemKey placeKey = place.getKey();
		final Locale locale = getLocale();
		final boolean nameChanged = puffService.log(placeKey, PUFF_FIELD_NAME,
				oldName, name, locale, user);
		final boolean addressChanged = puffService.log(placeKey,
				PUFF_FIELD_ADDRESS, oldAddress, address, locale, user);
		final boolean cityChanged = puffService.log(placeKey, PUFF_FIELD_CITY,
				oldCity, cityId, locale, user);
		final boolean placeTypeChanged = puffService.log(placeKey,
				PUFF_FIELD_PLACE_TYPE, oldPlaceType, placeType, locale, user);
		final boolean tagsChanged = puffService.log(placeKey, PUFF_FIELD_TAGS,
				oldTagKeys.toString(), tagKeys.toString(), locale, user);
		boolean localizationChanged = puffService.log(placeKey,
				PUFF_FIELD_LATITUDE, oldLat, String.valueOf(latitude), locale,
				user);
		localizationChanged |= puffService.log(placeKey, PUFF_FIELD_LONGITUDE,
				oldLng, String.valueOf(longitude), locale, user);
		// Updating descriptions by delegating to the management service
		boolean descChanged = false;
		boolean propertiesChanged = false;

		// Only for non mobile
		if (!mobile) {
			descChanged = descriptionManagementService.updateDescriptions(user,
					place, descriptionLanguageCode, descriptionKey,
					description, descriptionSourceId);
			// Updating properties by delegating to the management service
			propertiesChanged = propertiesService.updateProperties(user, place,
					getLocale(), propertyKey, propertyCode, propertyValue);
		} else {
			descChanged = descriptionManagementService.updateSingleDescription(
					user, place, descriptionLanguageCode, descriptionKey,
					description, descriptionSourceId);
		}

		// Fetching the updated place for search storage
		final ApisRequest fetchRequest = (ApisRequest) ApisFactory
				.createRequest(Place.class)
				.uniqueKey(place.getKey().getId())
				.addCriterion(
						(ApisCriterion) SearchRestriction.adapt(
								new ApisPlaceLocationAdapter()).with(
								AlternateName.class))
				.addCriterion(
						(ApisCriterion) SearchRestriction.adapt(
								new ApisPlaceAdm1Adapter()).with(
								AlternateName.class))
				.addCriterion(
						(ApisCriterion) SearchRestriction.adapt(
								new ApisPlaceCountryAdapter()).with(
								AlternateName.class)).with(Tag.class)
				.with(AdvertisingBooster.class).with(Statistic.class);
		final ApiResponse fetchResponse = getApiService().execute(fetchRequest,
				ContextFactory.createContext(getLocale()));
		final Place fetchedPlace = (Place) fetchResponse.getUniqueElement();
		searchService.storeCalmObject(fetchedPlace, SearchScope.CHILDREN);

		// Logging activity
		if (nameChanged || addressChanged || cityChanged || placeTypeChanged
				|| descChanged || localizationChanged || propertiesChanged
				|| tagsChanged) {
			final MutableActivity activity = (MutableActivity) activitiesService
					.createTransientObject();
			activity.add(city);
			activity.setActivityType(isCreation ? ActivityType.CREATION
					: ActivityType.UPDATE);
			activity.setDate(new Date());
			activity.setLoggedItemKey(place.getKey());
			activity.setUserKey(user.getKey());
			if (isCreation) {
				activity.setExtraInformation(place.getCity().getKey()
						.toString());
			} else {
				final StringBuilder buf = new StringBuilder();
				String separator = "";
				if (nameChanged) {
					buf.append(ActivityConstants.NAME_FIELD);
					separator = ActivityConstants.SEPARATOR;
				}
				if (addressChanged) {
					buf.append(separator);
					buf.append(ActivityConstants.ADDRESS_FIELD);
					separator = ActivityConstants.SEPARATOR;
				}
				if (cityChanged) {
					buf.append(separator);
					buf.append(ActivityConstants.CITY_FIELD);
					separator = ActivityConstants.SEPARATOR;
				}
				if (placeTypeChanged) {
					buf.append(separator);
					buf.append(ActivityConstants.TYPE_FIELD);
					separator = ActivityConstants.SEPARATOR;
				}
				if (descChanged) {
					buf.append(separator);
					buf.append(ActivityConstants.DESCRIPTION_FIELD);
					separator = ActivityConstants.SEPARATOR;
				}
				if (localizationChanged) {
					buf.append(separator);
					buf.append(ActivityConstants.LOCALIZATION_FIELD);
					separator = ActivityConstants.SEPARATOR;
				}
				if (propertiesChanged) {
					buf.append(separator);
					buf.append(ActivityConstants.PROPERTIES_FIELD);
					separator = ActivityConstants.SEPARATOR;
				}
				if (tagsChanged) {
					buf.append(separator);
					buf.append(ActivityConstants.TAGS_FIELD);
					separator = ActivityConstants.SEPARATOR;
				}
				activity.setExtraInformation(buf.toString());
			}
			ContextHolder.toggleWrite();
			activitiesService.saveItem(activity);
			searchService.storeCalmObject(activity, SearchScope.CHILDREN);
		}

		// Building JSON
		jsonPlace = jsonBuilder.buildJsonPlace(place, highRes, getLocale());
		return SUCCESS;
	}

	@Override
	public String getJson() {
		return JSONObject.fromObject(jsonPlace).toString();
	}

	public String getPlaceId() {
		return placeId;
	}

	public void setPlaceId(String placeId) {
		this.placeId = placeId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPlaceType() {
		return placeType;
	}

	public void setPlaceType(String placeType) {
		this.placeType = placeType;
	}

	public String getCityId() {
		return cityId;
	}

	public void setCityId(String cityId) {
		this.cityId = cityId;
	}

	public void setPlaceService(CalPersistenceService placeService) {
		this.placeService = placeService;
	}

	public void setSearchService(SearchPersistenceService searchService) {
		this.searchService = searchService;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public String getNewPlaceId() {
		return newPlaceId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserId() {
		return userId;
	}

	public void setPuffService(PuffService puffService) {
		this.puffService = puffService;
	}

	public void setActivitiesService(CalPersistenceService activitiesService) {
		this.activitiesService = activitiesService;
	}

	public void setDescriptionItemKey(String descriptionItemKey) {
		this.descriptionItemKey = descriptionItemKey;
	}

	@Override
	public void setPropertyCode(String[] propertyCode) {
		this.propertyCode = propertyCode;
	}

	@Override
	public String[] getPropertyCode() {
		return propertyCode;
	}

	@Override
	public void setPropertyValue(String[] propertyValue) {
		this.propertyValue = propertyValue;
	}

	@Override
	public String[] getPropertyValue() {
		return propertyValue;
	}

	@Override
	public void setPropertyKey(String[] propertyKey) {
		this.propertyKey = propertyKey;
	}

	@Override
	public String[] getPropertyKey() {
		return propertyKey;
	}

	@Override
	public void setPropertiesManagementService(
			PropertiesManagementService propertiesService) {
		this.propertiesService = propertiesService;
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
	public void setDescriptionsManagementService(
			DescriptionsManagementService descriptionManagementService) {
		this.descriptionManagementService = descriptionManagementService;
	}

	public void setTags(String[] tags) {
		this.tags = tags;
	}

	public String[] getTags() {
		return tags;
	}

	public void setTagsService(CalPersistenceService tagsService) {
		this.tagsService = tagsService;
	}

	@Override
	public void setDescriptionSourceId(String[] descriptionSourceId) {
		this.descriptionSourceId = descriptionSourceId;
	}

	@Override
	public String[] getDescriptionSourceId() {
		return descriptionSourceId;
	}

	@Override
	public void setRightsManagementService(
			RightsManagementService rightsManagementService) {
		this.rightsManagementService = rightsManagementService;
	}

	public void setCityRadius(double cityRadius) {
		this.cityRadius = cityRadius;
	}

	public void setJsonBuilder(JsonBuilder jsonBuilder) {
		this.jsonBuilder = jsonBuilder;
	}

	public void setHighRes(boolean highRes) {
		this.highRes = highRes;
	}

	public boolean isHighRes() {
		return highRes;
	}

	public void setMobile(boolean mobile) {
		this.mobile = mobile;
	}
}
