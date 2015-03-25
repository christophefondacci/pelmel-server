package com.nextep.proto.action.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;

import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.nextep.activities.model.ActivityType;
import com.nextep.activities.model.MutableActivity;
import com.nextep.cal.util.services.CalPersistenceService;
import com.nextep.descriptions.model.Description;
import com.nextep.geo.model.Admin;
import com.nextep.geo.model.City;
import com.nextep.geo.model.Country;
import com.nextep.geo.model.GeographicItem;
import com.nextep.json.model.impl.JsonUser;
import com.nextep.media.model.Media;
import com.nextep.media.model.MediaRequestTypes;
import com.nextep.media.model.MutableMedia;
import com.nextep.messages.model.Message;
import com.nextep.messages.model.impl.MessageRequestTypeFactory;
import com.nextep.proto.action.base.AbstractAction;
import com.nextep.proto.action.model.JsonProvider;
import com.nextep.proto.apis.adapters.ApisMessageFromUserAdapter;
import com.nextep.proto.apis.adapters.ApisUserLocationItemKeyAdapter;
import com.nextep.proto.builders.JsonBuilder;
import com.nextep.proto.helpers.DisplayHelper;
import com.nextep.proto.helpers.LocalizationHelper;
import com.nextep.proto.model.Constants;
import com.nextep.proto.model.CookieProvider;
import com.nextep.proto.spring.ContextHolder;
import com.nextep.smaug.service.SearchPersistenceService;
import com.nextep.tags.model.Tag;
import com.nextep.users.model.MutableUser;
import com.nextep.users.model.User;
import com.nextep.users.services.UsersService;
import com.videopolis.apis.exception.ApisException;
import com.videopolis.apis.exception.ApisNoSuchElementException;
import com.videopolis.apis.factory.ApisFactory;
import com.videopolis.apis.factory.SearchRestriction;
import com.videopolis.apis.model.ApisCriterion;
import com.videopolis.apis.model.ApisItemKeyAdapter;
import com.videopolis.apis.model.ApisRequest;
import com.videopolis.apis.model.WithCriterion;
import com.videopolis.apis.service.ApiCompositeResponse;
import com.videopolis.calm.exception.CalException;
import com.videopolis.calm.factory.CalmFactory;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.cals.factory.ContextFactory;
import com.videopolis.smaug.common.model.SearchScope;
import com.videopolis.smaug.common.model.SuggestScope;
import com.videopolis.smaug.exception.SearchException;

public class FacebookLoginAction extends AbstractAction implements
		CookieProvider, JsonProvider {

	private static final long serialVersionUID = -2932724792527026339L;
	private static final Log LOGGER = LogFactory
			.getLog(FacebookLoginAction.class);
	private static final String APIS_ALIAS_CITY = "city";
	private static final String APIS_ALIAS_FB = "fb";
	private static final String APIS_ALIAS_EMAIL = "email";
	private static final String facebookAppId = "302478486488872";
	private static final String facebookSecret = "f16e1ba84008262848fccc07f2bb7a38";
	private static final String PARAM_ACCESS_TOKEN = "access_token";

	private static final ApisItemKeyAdapter userLocationAdapter = new ApisUserLocationItemKeyAdapter();
	private static final ApisItemKeyAdapter messageFromUserAdapter = new ApisMessageFromUserAdapter();

	private static final DateFormat FACEBOOK_DATE_FORMAT = new SimpleDateFormat(
			"MM/dd/yyyy");
	private CalPersistenceService mediaService;
	private CalPersistenceService geoService;
	private SearchPersistenceService searchService;
	private CalPersistenceService activitiesService;
	@Autowired
	private JsonBuilder jsonBuilder;

	private String state;
	private String code;
	private String redirectUrl;
	private boolean forceRedirect;
	private User user;
	private boolean newUser = false;

	private String fbAccessToken = null;

	@Override
	protected String doExecute() throws Exception {
		newUser = false;
		getLoginSupport().initialize(getLocale(), getUrlService(),
				getHeaderSupport(), redirectUrl);
		getLoginSupport().setForceRedirect(forceRedirect);
		final String callbackUrl = URLEncoder.encode(getLoginSupport()
				.getFacebookCallbackUrl(redirectUrl), "UTF-8");
		String accessToken = fbAccessToken;

		// Getting token from Facebook API if we don't have it
		if (accessToken == null) {
			String fbUrl = "https://graph.facebook.com/oauth/access_token?client_id="
					+ facebookAppId
					+ "&client_secret="
					+ facebookSecret
					+ "&code=" + code + "&redirect_uri=" + callbackUrl;
			final URL url = new URL(fbUrl);
			final HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			final InputStream is = connection.getInputStream();
			if (connection.getResponseCode() == 200) {
				final StringWriter writer = new StringWriter();
				try {
					final Reader reader = new BufferedReader(
							new InputStreamReader(is, "UTF-8"));
					char[] buffer = new char[10240];
					int i = 0;
					while ((i = reader.read(buffer)) != -1) {
						writer.write(buffer, 0, i);
					}
				} finally {
					is.close();
				}
				final String accessStr = URLDecoder.decode(writer.toString(),
						"UTF-8");
				final Map<String, String> accessMap = decodeAccess(accessStr);
				accessToken = accessMap.get(PARAM_ACCESS_TOKEN);
			}
		}
		final JSONObject jsonUser = getJsonUser(accessToken);
		if (jsonUser != null) {
			final String id = jsonUser.getString("id");
			final String email = jsonUser.getString("email");
			// Looking for existing user
			final ApisRequest request = (ApisRequest) ApisFactory
					.createCompositeRequest()
					.addCriterion(
							(ApisCriterion) SearchRestriction
									.alternateKey(
											User.class,
											CalmFactory.createKey(
													User.FACEBOOK_TYPE, id))
									.aliasedBy(APIS_ALIAS_FB)
									.with(Media.class)
									.with(SearchRestriction
											.with(GeographicItem.class))
									.with((WithCriterion) SearchRestriction
											.with(Message.class,
													MessageRequestTypeFactory.UNREAD)
											.addCriterion(
													(ApisCriterion) SearchRestriction
															.adaptKey(
																	messageFromUserAdapter)
															.with(Media.class)))
									.with(Description.class)
									.with(Tag.class)
									.addCriterion(
											(ApisCriterion) SearchRestriction
													.adaptKey(
															userLocationAdapter)
													.aliasedBy(
															Constants.APIS_ALIAS_USER_LOCATION)
													.with(Media.class,
															MediaRequestTypes.THUMB)))
					.addCriterion(
							SearchRestriction.alternateKey(
									User.class,
									CalmFactory.createKey(User.EMAIL_TYPE,
											email)).aliasedBy(APIS_ALIAS_EMAIL));
			try {
				final ApiCompositeResponse response = (ApiCompositeResponse) getApiService()
						.execute(request,
								ContextFactory.createContext(getLocale()));
				user = response.getUniqueElement(User.class, APIS_ALIAS_FB);
				if (user == null) {
					user = response.getUniqueElement(User.class,
							APIS_ALIAS_EMAIL);
					if (user != null) {
						// Connecting existing user to facebook account
						((MutableUser) user).setFacebookId(id);
						// ((MutableUser) user).setFacebookToken(accessToken);
						ContextHolder.toggleWrite();
						getUsersService().saveItem(user);
					} else {
						// Creating a new user from facebook info
						try {
							user = buildUserFromFacebookUser(accessToken,
									jsonUser);
							newUser = true;
						} catch (SearchException ex) {
							setErrorMessage("login.facebook.error");
							LOGGER.error(
									"Facebook login exception during search registration: "
											+ ex.getMessage(), ex);
							return error();
						}
					}
				}
			} catch (ApisNoSuchElementException e) {

			}
			// Updating PELMEL token
			ContextHolder.toggleWrite();
			((UsersService) getUsersService()).refreshUserOnlineTimeout(user,
					((UsersService) getUsersService())
							.generateUniqueToken(user));
		}
		// Generating redirection URL if needed
		if (forceRedirect) {
			redirectUrl = getUrlService().getOverviewUrl(
					DisplayHelper.getDefaultAjaxContainer(), user);
		}
		if (redirectUrl != null) {
			return "redirect";
		} else {
			return SUCCESS;
		}
	}

	@Override
	public String getJson() {
		if (user != null) {
			final JsonUser json = jsonBuilder.buildJsonUser(user, true, null);
			json.setNewUser(newUser);
			return JSONObject.fromObject(json).toString();
		} else {
			return "";
		}
	}

	private User buildUserFromFacebookUser(String accessToken,
			JSONObject jsonUser) throws ApisException, CalException,
			SearchException {
		final String id = jsonUser.getString("id");
		final MutableUser newUser = (MutableUser) getUsersService()
				.createTransientObject();
		user = newUser;
		String username = null;
		if (jsonUser.containsKey("username")) {
			username = jsonUser.getString("username");
		} else {
			username = jsonUser.getString("name");
		}
		final String email = jsonUser.getString("email");
		String birthday = null;
		if (jsonUser.containsKey("birthday_date")) {
			birthday = jsonUser.getString("birthday_date");
		} else {
			birthday = jsonUser.getString("birthday");
		}
		Date birthDate;
		try {
			birthDate = FACEBOOK_DATE_FORMAT.parse(birthday);
			newUser.setBirthday(birthDate);
		} catch (ParseException e) {
			LOGGER.error(
					"Unable to parse facebook birthdate: " + e.getMessage(), e);
		}
		// Setting a media using the facebook profile URLs
		final String mediaUrl = "http://graph.facebook.com/" + id
				+ "/picture?type=large";
		final String thumbUrl = "http://graph.facebook.com/" + id
				+ "/picture?type=normal";
		final String smallUrl = "http://graph.facebook.com/" + id
				+ "/picture?type=square";
		final MutableMedia media = (MutableMedia) mediaService
				.createTransientObject();
		media.setUrl(mediaUrl);
		media.setThumbUrl(thumbUrl);
		media.setMiniThumbUrl(smallUrl);
		media.setOnline(true);
		media.setUploadDate(new Date());
		media.setTitle(getMessageSource().getMessage(
				"login.facebook.photoTitle", null, getLocale()));
		newUser.setPseudo(username);
		newUser.setEmail(email);

		newUser.setFacebookId(id);
		// newUser.setFacebookToken(accessToken);

		ContextHolder.toggleWrite();
		getUsersService().saveItem(user);

		// Setting user location
		String location = null;
		GeographicItem city = null;
		if (jsonUser.containsKey("location")) {
			JSONObject jsonLocation = jsonUser.getJSONObject("location");
			location = jsonLocation.getString("name");
			String userLocaleStr = jsonUser.getString("locale");
			city = findLocation(location, userLocaleStr);
			if (city != null) {
				final List<? extends CalmObject> cities = geoService
						.setItemFor(user.getKey(), city.getKey());
				user.addAll(cities);
			}
		}
		media.setRelatedItemKey(user.getKey());
		ContextHolder.toggleWrite();
		mediaService.saveItem(media);
		searchService.storeCalmObject(newUser, SearchScope.CHILDREN);

		// Generating activity
		final MutableActivity activity = (MutableActivity) activitiesService
				.createTransientObject();
		activity.setActivityType(ActivityType.REGISTER);
		activity.setDate(new Date());
		activity.setUserKey(user.getKey());
		activity.setLoggedItemKey(user.getKey());
		ContextHolder.toggleWrite();
		activitiesService.saveItem(activity);
		// Localizing activity and storing in search
		activity.add(city);
		searchService.storeCalmObject(activity, SearchScope.CHILDREN);

		return newUser;
	}

	private GeographicItem findLocation(String location, String userLocaleStr)
			throws ApisException {
		// Parsing Facebook's locale so that we extract country code
		String[] localeInfo = userLocaleStr.split("_");
		String countryCode = null;
		if (localeInfo.length > 1) {
			countryCode = localeInfo[1];
		}
		// Eliminating comma (state)
		final int commaIndex = location.indexOf(',');
		String state = null;
		if (commaIndex > 0) {
			if (commaIndex < location.length() - 1) {
				state = location.substring(commaIndex + 1).trim();
			}
			location = location.substring(0, commaIndex).trim();
		}
		final ApisRequest request = (ApisRequest) ApisFactory
				.createCompositeRequest().addCriterion(
						SearchRestriction.searchFromText(GeographicItem.class,
								SuggestScope.DESTINATION, location, 40)
								.aliasedBy(APIS_ALIAS_CITY));
		final ApiCompositeResponse response = (ApiCompositeResponse) getApiService()
				.execute(request, ContextFactory.createContext(getLocale()));
		final List<? extends City> cities = response.getElements(City.class,
				APIS_ALIAS_CITY);
		GeographicItem city = null;
		// First pass, we look for exact match with all parameters
		city = getCity(cities, state, countryCode, location, true);
		if (city == null && state != null) {
			// Second pass, non-exact match on state
			city = getCity(cities, state, null, location, false);
			// If we fail trying exact match without state, in country
			if (city == null) {
				city = getCity(cities, null, countryCode, location, true);
			}
		}
		// Third pass is exact match without country and state
		if (city == null) {
			city = getCity(cities, null, null, location, true);
		}
		// Last call is first result
		if (city == null) {
			city = getCity(cities, null, null, location, false);
		}
		return city;
	}

	private GeographicItem getCity(List<? extends City> cities, String state,
			String countryCode, String location, boolean exact) {
		for (City city : cities) {
			if (state != null) {
				// If we got the state we try to match the state otherwise we
				// cannot find a match
				final Admin adm1 = city.getAdm1();
				if (adm1 != null && adm1.getName().equalsIgnoreCase(state)) {
					if ((exact && city.getName().equalsIgnoreCase(location))
							|| !exact) {
						return city;
					}
				} else {
					final Admin adm2 = city.getAdm2();
					if (adm2 != null && adm2.getName().equalsIgnoreCase(state)) {
						if ((exact && city.getName().equalsIgnoreCase(location))
								|| !exact) {
							return city;
						}
					} else {
						// Last try is to match country name on "state" fragment
						final Country country = city.getCountry();
						if (country.getName().equalsIgnoreCase(state)) {
							if ((exact && city.getName().equalsIgnoreCase(
									location))
									|| !exact) {
								return city;
							}
						}
					}
				}
			} else if (countryCode != null) {
				// If we have a country code, we try to match it
				if (city.getCountry().getCode().equalsIgnoreCase(countryCode)) {
					if ((exact && city.getName().equalsIgnoreCase(location))
							|| !exact) {
						return city;
					}
				}
			} else {
				// Last attempt is an exact name match
				if ((exact && city.getName().equalsIgnoreCase(location))
						|| !exact) {
					return city;
				}
			}

		}
		return null;

	}

	private Map<String, String> decodeAccess(String accessStr) {
		final Map<String, String> paramsMap = new HashMap<String, String>();
		int currentIndex = 0;
		int currentEqualsIndex = accessStr.indexOf('=');
		while (currentEqualsIndex >= 0) {
			// Extracting key string (before "=")
			final String key = accessStr.substring(currentIndex,
					currentEqualsIndex);
			// Computing end index of value
			int endIndex = accessStr.indexOf('&', currentEqualsIndex);
			if (endIndex < 0) {
				// We are at the end so we are done
				endIndex = accessStr.length();
			}
			final String value = accessStr.substring(currentEqualsIndex + 1,
					endIndex);
			paramsMap.put(key, value);
			currentEqualsIndex = accessStr.indexOf('=', endIndex);
		}
		return paramsMap;
	}

	// private JSONObject getJsonPicture(String accessToken) throws IOException
	// {
	// final String fbUrl =
	// "https://graph.facebook.com/me/picture?type=large&access_token="
	// + accessToken;
	// return buildJsonFromUrl(fbUrl);
	// }

	private JSONObject getJsonUser(String accessToken) throws IOException {
		final String fbUrl = "https://graph.facebook.com/me?access_token="
				+ accessToken;
		return buildJsonFromUrl(fbUrl);
	}

	private JSONObject buildJsonFromUrl(String fbUrl) throws IOException {
		final URL url = new URL(fbUrl);
		LOGGER.debug("Facebook user URL = " + fbUrl);
		final HttpURLConnection connection = (HttpURLConnection) url
				.openConnection();
		final InputStream is = connection.getInputStream();
		if (connection.getResponseCode() == 200) {
			final StringWriter writer = new StringWriter();
			try {
				final Reader reader = new BufferedReader(new InputStreamReader(
						is, "UTF-8"));
				char[] buffer = new char[10240];
				int i = 0;
				while ((i = reader.read(buffer)) != -1) {
					writer.write(buffer, 0, i);
				}
			} finally {
				is.close();
			}
			final JSONObject json = (JSONObject) JSONSerializer.toJSON(writer
					.toString());
			return json;
		} else {
			return null;
		}
	}

	@Override
	public List<Cookie> getCookies() {
		final List<Cookie> cookies = new ArrayList<Cookie>(1);
		if (user != null) {
			final List<String> domainExts = LocalizationHelper
					.getSupportedDomains();
			for (String domainExt : domainExts) {
				final Cookie c = new Cookie(Constants.USER_COOKIE_NAME, user
						.getToken().toString());
				c.setDomain("." + getDomainName() + "." + domainExt);
				cookies.add(c);
			}
		}
		return cookies;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getState() {
		return state;
	}

	public void setRedirectUrl(String redirectUrl) {
		this.redirectUrl = redirectUrl;
	}

	@Override
	public String getRedirectUrl() {
		return redirectUrl;
	}

	public void setForceRedirect(boolean forceRedirect) {
		this.forceRedirect = forceRedirect;
	}

	public boolean getForceRedirect() {
		return forceRedirect;
	}

	public void setMediaService(CalPersistenceService mediaService) {
		this.mediaService = mediaService;
	}

	public void setSearchService(SearchPersistenceService searchService) {
		this.searchService = searchService;
	}

	public void setGeoService(CalPersistenceService geoService) {
		this.geoService = geoService;
	}

	public void setActivitiesService(CalPersistenceService activitiesService) {
		this.activitiesService = activitiesService;
	}

	public void setFbAccessToken(String fbAccessToken) {
		this.fbAccessToken = fbAccessToken;
	}

	public String getFbAccessToken() {
		return fbAccessToken;
	}
}
