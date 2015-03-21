package com.nextep.proto.action.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.Cookie;

import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.NoSuchMessageException;

import com.nextep.descriptions.model.Description;
import com.nextep.json.model.impl.JsonUser;
import com.nextep.media.model.Media;
import com.nextep.media.model.MediaRequestTypes;
import com.nextep.proto.action.base.AbstractAction;
import com.nextep.proto.action.model.JsonProvider;
import com.nextep.proto.apis.adapters.ApisUserLocationItemKeyAdapter;
import com.nextep.proto.blocks.CurrentUserSupport;
import com.nextep.proto.builders.JsonBuilder;
import com.nextep.proto.helpers.LocalizationHelper;
import com.nextep.proto.model.Constants;
import com.nextep.proto.model.CookieProvider;
import com.nextep.proto.spring.ContextHolder;
import com.nextep.smaug.service.SearchPersistenceService;
import com.nextep.tags.model.Tag;
import com.nextep.users.model.User;
import com.nextep.users.services.UsersService;
import com.opensymphony.xwork2.ActionContext;
import com.videopolis.apis.factory.ApisFactory;
import com.videopolis.apis.factory.SearchRestriction;
import com.videopolis.apis.model.ApisCriterion;
import com.videopolis.apis.model.ApisItemKeyAdapter;
import com.videopolis.apis.model.ApisRequest;
import com.videopolis.apis.service.ApiCompositeResponse;
import com.videopolis.calm.exception.CalException;
import com.videopolis.cals.factory.ContextFactory;
import com.videopolis.smaug.exception.SearchException;

public class UserLoginAction extends AbstractAction implements CookieProvider,
		JsonProvider {

	private static final long serialVersionUID = 8931650837836559418L;
	private static final Log LOGGER = LogFactory.getLog(UserLoginAction.class);
	private static final String KEY_DEFAULT_ERROR_MESSAGE = "error.default";

	private static final ApisItemKeyAdapter userLocationAdapter = new ApisUserLocationItemKeyAdapter();

	// Injected services
	private SearchPersistenceService searchService;
	private CurrentUserSupport currentUserSupport;
	private JsonBuilder jsonBuilder;

	// Injected parameter
	private boolean isMobileService = false;
	private String baseUrl;

	// Dynamic arguments
	private String email;
	private String password;
	private String loginErrorMessage = "";
	private String pushProvider;
	private String pushDeviceId;
	private String url;
	private String queryParams;
	private boolean highRes;

	// Internal variables
	private User user;

	@Override
	protected String doExecute() throws Exception {
		final UsersService usersService = (UsersService) getUsersService();
		getHeaderSupport().initialize(getLocale(), null, null, null);
		if (email != null && !"".equals(email.trim())) {
			try {
				ContextHolder.toggleWrite();
				user = usersService.login(email, password, pushDeviceId,
						pushProvider);
				searchService.updateUserOnlineStatus(user);
				if (user != null) {
					if (isMobileService) {

						// Checking on our read replica that we can login with
						// our new token
						ContextHolder.toggleReadonly();
						User loggedUser = null;
						int retries = 0;
						while (loggedUser == null && retries < 5) {

							// If this is not our first attempt we wait 500ms
							// before another try
							if (retries > 0) {
								try {
									Thread.sleep(500);
								} catch (InterruptedException e) {
									LOGGER.warn(
											"Thread interrupted while waiting read replica: "
													+ e.getMessage(), e);
								}
							}
							ApisRequest request = (ApisRequest) ApisFactory
									.createCompositeRequest()
									.addCriterion(
											(ApisCriterion) currentUserSupport
													.createApisCriterionFor(
															user.getToken(),
															false)
													.with(Description.class)
													.with(Tag.class)
													.addCriterion(
															(ApisCriterion) SearchRestriction
																	.adaptKey(
																			userLocationAdapter)
																	.aliasedBy(
																			Constants.APIS_ALIAS_USER_LOCATION)
																	.with(Media.class,
																			MediaRequestTypes.THUMB)));
							ApiCompositeResponse response = (ApiCompositeResponse) getApiService()
									.execute(
											request,
											ContextFactory
													.createContext(getLocale()));
							loggedUser = response.getUniqueElement(User.class,
									CurrentUserSupport.APIS_ALIAS_CURRENT_USER);
							retries++;
						}
						user = loggedUser;
						return SUCCESS;
					} else {
						return url != null
								&& !"".equals(url.trim())
								&& !url.toLowerCase()
										.endsWith("togayther.net/") ? "redirect"
								: SUCCESS;
					}
				} else {
					return unauthorized();
				}
			} catch (CalException e) {
				LOGGER.error("Login error for " + email + " : "
						+ e.getMessage());
				if (email != null && !"".equals(email.trim())) {
					loginErrorMessage = "index.login.invalid";
				}
				return unauthorized();
			} catch (SearchException e) {
				LOGGER.error(
						"Search exception during login: " + e.getMessage(), e);
				loginErrorMessage = "error.search.unavailable";
				return error();
			}
		} else {
			return unauthorized();
		}
	}

	@Override
	public String getJson() {
		if (user != null) {
			final JsonUser json = jsonBuilder
					.buildJsonUser(user, highRes, null);
			return JSONObject.fromObject(json).toString();
		} else {
			return "";
		}
	};

	// @RequiredStringValidator(message = "Please define an email")
	public void setEmail(String email) {
		this.email = email;
	}

	public String getEmail() {
		return email;
	}

	// @RequiredStringValidator(message = "Please define a password")
	public void setPassword(String password) {
		this.password = password;
	}

	public String getPassword() {
		return password;
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
				final String subdomain = (String) ActionContext.getContext()
						.get(Constants.ACTION_CONTEXT_SUBDOMAIN);
				c.setDomain(subdomain + "." + getDomainName() + "." + domainExt);
				cookies.add(c);
			}
		}
		return cookies;
	}

	public User getUser() {
		return user;
	}

	public List<Tag> getAllTags() {
		return Collections.emptyList();
	}

	public void setSearchService(SearchPersistenceService searchService) {
		this.searchService = searchService;
	}

	public String getLoginErrorMessage() {
		return loginErrorMessage;
	}

	public String getLoginErrorMessageLabel() {
		if (loginErrorMessage != null) {
			try {
				return getMessageSource().getMessage(loginErrorMessage, null,
						getLocale());
			} catch (NoSuchMessageException e) {
				return getMessageSource().getMessage(KEY_DEFAULT_ERROR_MESSAGE,
						null, getLocale());
			}
		} else {
			return null;
		}
	}

	@Override
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public String getQueryParams() {
		return queryParams;
	}

	public void setQueryParams(String queryParams) {
		this.queryParams = queryParams;
	}

	@Override
	public String getRedirectUrl() {
		if (queryParams != null && !"".equals(queryParams)) {
			return url + "?" + queryParams;
		} else {
			return url;
		}
	}

	public void setCurrentUserSupport(CurrentUserSupport currentUserSupport) {
		this.currentUserSupport = currentUserSupport;
	}

	public void setMobileService(boolean isMobileService) {
		this.isMobileService = isMobileService;
	}

	public void setHighRes(boolean highRes) {
		this.highRes = highRes;
	}

	public boolean isHighRes() {
		return highRes;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	public void setJsonBuilder(JsonBuilder jsonBuilder) {
		this.jsonBuilder = jsonBuilder;
	}

	public void setPushProvider(String pushProvider) {
		this.pushProvider = pushProvider;
	}

	public String getPushProvider() {
		return pushProvider;
	}

	public void setPushDeviceId(String pushDeviceId) {
		this.pushDeviceId = pushDeviceId;
	}

	public String getPushDeviceId() {
		return pushDeviceId;
	}
}
