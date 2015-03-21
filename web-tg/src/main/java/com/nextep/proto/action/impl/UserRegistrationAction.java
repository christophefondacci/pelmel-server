package com.nextep.proto.action.impl;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.Cookie;

import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.nextep.activities.model.ActivityType;
import com.nextep.activities.model.MutableActivity;
import com.nextep.cal.util.services.CalPersistenceService;
import com.nextep.descriptions.model.Description;
import com.nextep.events.model.Event;
import com.nextep.geo.model.City;
import com.nextep.geo.model.Place;
import com.nextep.json.model.impl.JsonUser;
import com.nextep.messages.model.MutableMessage;
import com.nextep.properties.model.Property;
import com.nextep.proto.action.base.AbstractAction;
import com.nextep.proto.action.model.DescriptionsUpdateAware;
import com.nextep.proto.action.model.JsonProvider;
import com.nextep.proto.action.model.PropertiesUpdateAware;
import com.nextep.proto.apis.adapters.ApisCityLocalizerAdapter;
import com.nextep.proto.apis.model.impl.ApisLocalizationHelper;
import com.nextep.proto.blocks.MediaPersistenceSupport;
import com.nextep.proto.builders.JsonBuilder;
import com.nextep.proto.helpers.DisplayHelper;
import com.nextep.proto.helpers.LocalizationHelper;
import com.nextep.proto.model.Constants;
import com.nextep.proto.model.CookieProvider;
import com.nextep.proto.services.DescriptionsManagementService;
import com.nextep.proto.services.PropertiesManagementService;
import com.nextep.proto.spring.ContextHolder;
import com.nextep.smaug.service.SearchPersistenceService;
import com.nextep.tags.model.Tag;
import com.nextep.users.model.MutableUser;
import com.nextep.users.model.User;
import com.nextep.users.model.impl.UserImpl;
import com.nextep.users.services.UsersService;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.conversion.annotations.Conversion;
import com.videopolis.apis.factory.ApisFactory;
import com.videopolis.apis.factory.SearchRestriction;
import com.videopolis.apis.model.ApisCriterion;
import com.videopolis.apis.model.ApisCustomAdapter;
import com.videopolis.apis.model.ApisRequest;
import com.videopolis.apis.service.ApiCompositeResponse;
import com.videopolis.calm.exception.CalException;
import com.videopolis.calm.factory.CalmFactory;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.cals.factory.ContextFactory;
import com.videopolis.smaug.common.model.SearchScope;

/**
 * This action is called by the user registration form and handles the
 * persistence of user information in our data stores.
 * 
 * @author cfondacci
 * 
 */
@Conversion()
public class UserRegistrationAction extends AbstractAction implements
		CookieProvider, PropertiesUpdateAware, DescriptionsUpdateAware,
		JsonProvider {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3108725625627129332L;
	private static final Log log = LogFactory
			.getLog(UserRegistrationAction.class);
	private final static DateFormat DATE_FORMAT = new SimpleDateFormat(
			"yyyyMMdd");
	private final static String APIS_ALIAS_USER = "user";
	private final static String APIS_ALIAS_EMAIL_EXISTS = "email";
	private final static String APIS_ALIAS_CITY_NEARBY_DEFAULT = "citydefault";
	private final static String MESSAGE_KEY_WELCOME = "register.welcomeMsg";
	private static ApisCustomAdapter cityLocalizerAdapter = new ApisCityLocalizerAdapter();

	// Injected services
	private CalPersistenceService tagsService;
	private CalPersistenceService geoService;
	private SearchPersistenceService searchPersistenceService;
	private MediaPersistenceSupport mediaPersistenceSupport;
	private CalPersistenceService messagePersistenceService;
	private PropertiesManagementService propertiesManagementService;
	private DescriptionsManagementService descriptionManagementService;
	private CalPersistenceService activitiesService;
	private double cityRadius;
	private JsonBuilder jsonBuilder;
	private String welcomeMsgUser;

	private File media;
	// Dynamic arguments
	private String name, email, password, contentType, fileName,
			passwordConfirm;
	private String defaultCityKey;
	private String userKey;
	private String cityKey;
	private String intro;
	private String height, weight, birthDD, birthMM, birthYYYY;
	private String[] tagKeys = {};

	private String[] propertyCode = {}, propertyKey = {}, propertyValue = {};
	private String[] descriptionLanguageCodes = {}, descriptionItemKeys = {},
			descriptions = {}, descriptionSourceId = {};
	private double lat, lng;
	private String redirectUrl;
	private boolean highRes;

	// Internal variable
	private MutableUser user;
	private City localizedCity;

	@Override
	protected String doExecute() throws Exception {
		final CalPersistenceService usersService = getUsersService();
		getHeaderSupport().initialize(getLocale(), null, null, null);
		// Make sure we are doing everything on master DB
		ContextHolder.toggleWrite();
		// Creating our new user
		if (userKey != null && !"".equals(userKey)) {
			final ItemKey userItemKey = CalmFactory.parseKey(userKey);
			final ApisRequest request = (ApisRequest) ApisFactory
					.createCompositeRequest().addCriterion(
							(ApisCriterion) SearchRestriction
									.uniqueKeys(Arrays.asList(userItemKey))
									.aliasedBy(APIS_ALIAS_USER)
									.with(Property.class)
									.with(Description.class)
									.with(Place.class,
											Constants.APIS_ALIAS_FAVORITE)
									.with(Event.class,
											Constants.APIS_ALIAS_FAVORITE)
									.with(User.class,
											Constants.APIS_ALIAS_FAVORITE));
			if (lat != 0 && lng != 0) {
				// Looking for the closest city (=most populated among the 5
				// closest cities)
				request.addCriterion(ApisLocalizationHelper
						.buildNearestCityCriterion(lat, lng, cityRadius));
			}
			final ApiCompositeResponse response = (ApiCompositeResponse) getApiService()
					.execute(request, ContextFactory.createContext(getLocale()));
			user = (MutableUser) response.getUniqueElement(User.class,
					APIS_ALIAS_USER);
			localizedCity = response.getUniqueElement(City.class,
					ApisLocalizationHelper.APIS_ALIAS_CITY_NEARBY);
			((UserImpl) user).setToken(getNxtpUserToken());
		} else {
			final ApisRequest request = ApisFactory.createCompositeRequest();
			if (lat != 0 && lng != 0) {
				request.addCriterion(ApisLocalizationHelper
						.buildNearestCityCriterion(lat, lng, cityRadius));
			} else {
				// That means use de-activated localization, so we assign it our
				// default
				// city for everything to work fine. He will be relocalized
				// properly as soon
				// as we got localization info
				request.addCriterion(SearchRestriction.uniqueKeys(
						Arrays.asList(CalmFactory.parseKey(defaultCityKey)))
						.aliasedBy(APIS_ALIAS_CITY_NEARBY_DEFAULT));
			}
			// Building an email key
			final ItemKey emailKey = CalmFactory.createKey(User.EMAIL_TYPE,
					email);

			// Querying user box to check whether this user already exist (so
			// that we prevent recreation)
			request.addCriterion(SearchRestriction.alternateKey(User.class,
					emailKey).aliasedBy(APIS_ALIAS_EMAIL_EXISTS));

			// Executing APIS request
			ApiCompositeResponse response = (ApiCompositeResponse) getApiService()
					.execute(request, ContextFactory.createContext(getLocale()));
			localizedCity = response.getUniqueElement(City.class,
					ApisLocalizationHelper.APIS_ALIAS_CITY_NEARBY);

			// Fallback on default city when not found
			if (localizedCity == null) {
				localizedCity = response.getUniqueElement(City.class,
						APIS_ALIAS_CITY_NEARBY_DEFAULT);
			}
			final User emailUser = response.getUniqueElement(User.class,
					APIS_ALIAS_EMAIL_EXISTS);
			// If we already have a user, we return an error
			if (emailUser != null) {
				setErrorMessage(getText("index.register.error.email"));
				return unauthorized();
			}
			user = (MutableUser) usersService.createTransientObject();
		}
		// Parsing birthday
		if (birthDD != null && birthMM != null && birthYYYY != null) {
			if (birthDD.length() < 2) {
				birthDD = '0' + birthDD;
			}
			if (birthMM.length() < 2) {
				birthMM = '0' + birthMM;
			}
			try {
				final Date birthday = DATE_FORMAT.parse(birthYYYY + birthMM
						+ birthDD);
				user.setBirthday(birthday);
			} catch (ParseException e) {
				log.error("Unable to parse user date : " + birthYYYY + birthMM
						+ birthDD + " for user " + email);
			}
		}
		user.setPseudo(name);
		if (email != null) {
			user.setEmail(email);
		}

		user.setStatus(intro);
		if (height != null) {
			user.setHeightInCm(Integer.valueOf(height));
		}
		if (weight != null) {
			user.setWeightInKg(Integer.valueOf(weight));
		}
		final boolean newUser = user.getKey() == null;
		ContextHolder.toggleWrite();
		usersService.saveItem(user);
		if (password != null && newUser) {
			((UsersService) usersService).savePassword(user.getKey(), null,
					password);
		}

		// Binding tags to this user
		final List<ItemKey> keys = new ArrayList<ItemKey>();
		if (tagKeys != null) {
			for (String tagKey : tagKeys) {
				keys.add(CalmFactory.parseKey(tagKey.startsWith(Tag.CAL_ID) ? tagKey
						: Tag.CAL_ID + tagKey));
			}
			List<? extends CalmObject> tags = tagsService.setItemFor(
					user.getKey(), keys.toArray(new ItemKey[keys.size()]));
			user.addAll(tags);
		}

		// Binding city to this user
		List<? extends CalmObject> cities = Collections.emptyList();
		ItemKey cityItemKey = null;
		if (cityKey != null) {
			cityItemKey = CalmFactory.parseKey(cityKey);
		} else if (localizedCity != null) {
			cityItemKey = localizedCity.getKey();
		}
		if (cityItemKey != null) {
			cities = geoService.setItemFor(user.getKey(), cityItemKey);
			user.addAll(cities);
		}
		// Creates the new media
		if (media != null && fileName != null) {
			mediaPersistenceSupport.createMedia(user, user.getKey(), media,
					fileName, contentType, "Profile", false, null);
		}
		// Saving search item
		searchPersistenceService.storeCalmObject(user, null);
		// Updating properties
		if (!newUser && propertyKey != null) {
			propertiesManagementService.updateProperties(user, user,
					getLocale(), propertyKey, propertyCode, propertyValue);
		}
		if (!newUser) {
			// For existing users we store every description submitted
			if (descriptionLanguageCodes != null && descriptionItemKeys != null
					&& descriptions != null) {
				descriptionManagementService.updateDescriptions(user, user,
						descriptionLanguageCodes, descriptionItemKeys,
						descriptions, descriptionSourceId);
			}
		} else {
			// Since registration form is shorter than profile form (i.e. not
			// containing full description edition), we inject user's
			// description has the headline he defined
			final Locale l = getLocale();
			descriptionManagementService.updateDescriptions(user, user,
					new String[] { l.getLanguage() }, new String[] { "0" },
					new String[] { user.getStatusMessage() },
					new String[] { "1000" });
		}
		// Generating REGISTER activity for new users
		if (newUser) {
			final MutableActivity activity = (MutableActivity) activitiesService
					.createTransientObject();
			activity.setActivityType(ActivityType.REGISTER);
			activity.setDate(new Date());
			activity.setUserKey(user.getKey());
			activity.setLoggedItemKey(user.getKey());
			ContextHolder.toggleWrite();
			activitiesService.saveItem(activity);
			// Localizing activity and storing in search
			activity.addAll(user.get(City.class));
			searchPersistenceService.storeCalmObject(activity,
					SearchScope.CHILDREN);
			// Sending the welcome message for new users
			try {
				sendWelcomeMessage(user);
			} catch (CalException e) {
				log.error(
						"Unable to send welcome message to user "
								+ user.getKey() + ": " + e.getMessage(), e);
			}
		}

		redirectUrl = getUrlService().getUserOverviewUrl(
				DisplayHelper.getDefaultAjaxContainer(), user);
		return SUCCESS;
	}

	private void sendWelcomeMessage(User user) throws CalException {
		final MutableMessage msg = (MutableMessage) messagePersistenceService
				.createTransientObject();
		final ItemKey userKey = CalmFactory.parseKey(welcomeMsgUser);
		msg.setFromKey(userKey);
		msg.setToKey(user.getKey());
		msg.setMessage(getMessageSource().getMessage(MESSAGE_KEY_WELCOME, null,
				getLocale()));
		msg.setMessageDate(new Date());
		ContextHolder.toggleWrite();
		messagePersistenceService.saveItem(msg);
	}

	@Override
	public String getJson() {
		// We provide a JSON user response only for user creation
		if (userKey == null) {
			JsonUser user = jsonBuilder.buildJsonUser(getUser(), highRes, null);
			return JSONObject.fromObject(user).toString();
		}
		return null;
	}

	// @RequiredStringValidator(message = "Please define a user name")
	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	// @RequiredStringValidator(message =
	// "Please provide a valid email address")
	// @EmailValidator(message = "Invalid email address")
	public void setEmail(String email) {
		this.email = email;
	}

	public String getEmail() {
		return email;
	}

	// @RequiredStringValidator(message = "You must define a password")
	public void setPassword(String password) {
		this.password = password;
	}

	public String getPassword() {
		return password;
	}

	// @RequiredStringValidator(message = "You must confirm your password")
	public void setPasswordConfirm(String passwordConfirm) {
		this.passwordConfirm = passwordConfirm;
	}

	public String getPasswordConfirm() {
		return passwordConfirm;
	}

	public void setMedia(File media) {
		this.media = media;
	}

	// @RequiredStringValidator(message = "You need to tell us where you live")
	public void setCityKey(String city) {
		this.cityKey = city;
	}

	public String getCityKey() {
		return cityKey;
	}

	public void setMediaContentType(String contentType) {
		this.contentType = contentType;
	}

	public void setTags(String[] tagKeys) {
		this.tagKeys = tagKeys;
	}

	public void setMediaFileName(String fileName) {
		this.fileName = fileName;
	}

	public void setTagsService(CalPersistenceService tagsService) {
		this.tagsService = tagsService;
	}

	public void setSearchPersistenceService(
			SearchPersistenceService searchPersistenceService) {
		this.searchPersistenceService = searchPersistenceService;
	}

	public void setGeoService(CalPersistenceService geoService) {
		this.geoService = geoService;
	}

	public User getUser() {
		return user;
	}

	@Override
	public List<Cookie> getCookies() {
		final List<Cookie> cookies = new ArrayList<Cookie>(1);
		if (user != null) {
			// Only setting cookie on new user, because this action is also use
			// for profile edition and we don't want to unlog the user each time
			// he updates his profile
			if (userKey == null || "".equals(userKey)) {
				final List<String> domainExts = LocalizationHelper
						.getSupportedDomains();
				for (String domainExt : domainExts) {
					final Cookie c = new Cookie(Constants.USER_COOKIE_NAME,
							user.getToken().toString());
					final String subdomain = (String) ActionContext
							.getContext().get(
									Constants.ACTION_CONTEXT_SUBDOMAIN);
					c.setDomain(subdomain + "." + getDomainName() + "."
							+ domainExt);
					cookies.add(c);
				}
			}
		}
		return cookies;
	}

	public List<Tag> getAllTags() {
		return Collections.emptyList();
	}

	public void setMediaPersistenceSupport(
			MediaPersistenceSupport mediaPersistenceSupport) {
		this.mediaPersistenceSupport = mediaPersistenceSupport;
	}

	public void setIntro(String intro) {
		this.intro = intro;
	}

	public String getIntro() {
		return intro;
	}

	public void setHeight(String height) {
		this.height = height;
	}

	public String getHeight() {
		return height;
	}

	public void setWeight(String weight) {
		this.weight = weight;
	}

	public String getWeight() {
		return weight;
	}

	public String getBirthDD() {
		return birthDD;
	}

	public void setBirthDD(String birthDD) {
		this.birthDD = birthDD;
	}

	public String getBirthMM() {
		return birthMM;
	}

	public void setBirthMM(String birthMM) {
		this.birthMM = birthMM;
	}

	public String getBirthYYYY() {
		return birthYYYY;
	}

	public void setBirthYYYY(String birthYYYY) {
		this.birthYYYY = birthYYYY;
	}

	public void setUserKey(String userKey) {
		this.userKey = userKey;
	}

	public String getUserKey() {
		return userKey;
	}

	@Override
	public String[] getPropertyCode() {
		return propertyCode;
	}

	@Override
	public String[] getPropertyKey() {
		return propertyKey;
	}

	@Override
	public String[] getPropertyValue() {
		return propertyValue;
	}

	@Override
	public void setPropertyCode(String[] propertyCode) {
		this.propertyCode = propertyCode;
	}

	@Override
	public void setPropertyKey(String[] propertyKey) {
		this.propertyKey = propertyKey;
	}

	@Override
	public void setPropertyValue(String[] propertyValue) {
		this.propertyValue = propertyValue;
	}

	@Override
	public void setPropertiesManagementService(
			PropertiesManagementService propertiesService) {
		this.propertiesManagementService = propertiesService;
	}

	public void setActivitiesService(CalPersistenceService activitiesService) {
		this.activitiesService = activitiesService;
	}

	@Override
	public void setDescription(String[] descriptions) {
		this.descriptions = descriptions;
	}

	@Override
	public String[] getDescription() {
		return descriptions;
	}

	@Override
	public void setDescriptionLanguageCode(String[] languages) {
		this.descriptionLanguageCodes = languages;
	}

	@Override
	public String[] getDescriptionLanguageCode() {
		return descriptionLanguageCodes;
	}

	@Override
	public void setDescriptionKey(String[] descriptionItemKeys) {
		this.descriptionItemKeys = descriptionItemKeys;
	}

	@Override
	public String[] getDescriptionKey() {
		return descriptionItemKeys;
	}

	@Override
	public void setDescriptionsManagementService(
			DescriptionsManagementService descriptionManagementService) {
		this.descriptionManagementService = descriptionManagementService;
	}

	@Override
	public void setDescriptionSourceId(String[] descriptionSourceId) {
		this.descriptionSourceId = descriptionSourceId;
	}

	@Override
	public String[] getDescriptionSourceId() {
		return descriptionSourceId;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLat() {
		return lat;
	}

	public void setLng(double lng) {
		this.lng = lng;
	}

	public double getLng() {
		return lng;
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

	public void setDefaultCityKey(String defaultCityKey) {
		this.defaultCityKey = defaultCityKey;
	}

	public void setMessagePersistenceService(
			CalPersistenceService messageService) {
		this.messagePersistenceService = messageService;
	}

	public void setWelcomeMsgUser(String welcomeMsgUser) {
		this.welcomeMsgUser = welcomeMsgUser;
	}
}
