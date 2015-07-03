package com.nextep.proto.action.base;

import java.util.Date;
import java.util.Locale;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpStatus;
import org.apache.struts2.ServletActionContext;
import org.springframework.context.MessageSource;

import com.nextep.cal.util.services.CalExtendedPersistenceService;
import com.nextep.json.model.impl.JsonStatus;
import com.nextep.proto.action.model.AdBannerAware;
import com.nextep.proto.action.model.HeaderAware;
import com.nextep.proto.action.model.JsonProvider;
import com.nextep.proto.action.model.JsonProviderWithError;
import com.nextep.proto.action.model.LoginAware;
import com.nextep.proto.blocks.AdBannerSupport;
import com.nextep.proto.blocks.HeaderSearchSupport;
import com.nextep.proto.blocks.HeaderSupport;
import com.nextep.proto.blocks.LoginSupport;
import com.nextep.proto.exceptions.UserLoginTimeoutException;
import com.nextep.proto.helpers.LocalizationHelper;
import com.nextep.proto.helpers.MediaHelper;
import com.nextep.proto.model.Constants;
import com.nextep.proto.model.SearchType;
import com.nextep.proto.model.UrlConstants;
import com.nextep.proto.services.RightsManagementService;
import com.nextep.proto.services.UrlService;
import com.nextep.proto.spring.ContextHolder;
import com.nextep.smaug.service.SearchPersistenceService;
import com.nextep.users.model.User;
import com.nextep.users.services.UsersService;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.videopolis.apis.exception.ApisNoSuchElementException;
import com.videopolis.apis.service.ApiService;

/**
 * Global base action class providing exception management
 * 
 * @author Christophe Fondacci
 * 
 */
public abstract class AbstractAction extends ActionSupport implements
		HeaderAware, LoginAware, AdBannerAware {

	// Static stuff
	private static final long serialVersionUID = 4698469674120411898L;
	private static final Log LOGGER = LogFactory.getLog(AbstractAction.class);
	protected static final String FORBIDDEN = "forbidden";
	protected static final String NOT_FOUND = "notfound";
	protected static final String CONFIRM = "confirm";
	protected static final String REDIRECT = "redirect";
	protected static final String REDIRECT_PERMANENTLY = "301";
	private static final String MESSAGE_KEY_LANG = "language.";

	// Injected information & services
	private String domainName;
	private UrlService urlService;
	private ApiService apiService;
	private ApiService noSeoApiService;
	private HeaderSupport headerSupport;
	private MessageSource messageSource;
	private CalExtendedPersistenceService usersService;
	private SearchPersistenceService searchService;
	private RightsManagementService rightsManagementService;
	private LoginSupport loginSupport;
	private int refreshTimeoutMillisec;
	private boolean googleEnabled = true;
	private AdBannerSupport adBannerSupport;

	@Resource(mappedName = "redirectEnabled")
	private Boolean redirectEnabled;

	// Dynamic variables
	private String nxtpUserToken;
	private String errorMessage;
	private String url;
	private String redirectUrl;
	private String queryParams;
	private String userEmail;
	private String language;

	// Internal variables
	private boolean logged = false;
	private Locale overriddenLocale;
	private Exception lastException;
	private SearchType currentSearchType = SearchType.BARS; // Default search
															// type is BAR

	@Override
	public final String execute() throws Exception {
		// Default login support initialization
		loginSupport.initialize(getLocale(), getUrlService(),
				getHeaderSupport(), null);
		// Everything is readonly by default
		ContextHolder.toggleReadonly();
		final HttpServletResponse response = ServletActionContext.getResponse();
		// response.setDateHeader("Expires",
		// System.currentTimeMillis() + 31536000000l);
		try {
			return doExecute();
		} catch (ApisNoSuchElementException e) {
			lastException = e;
			response.setStatus(HttpStatus.SC_NOT_FOUND);
		} catch (UserLoginTimeoutException e) {
			setErrorMessage("Unauthorized user token");
			lastException = e;
			return loginTimeoutError(e.getMessage());
		} catch (Exception e) {
			lastException = e;
			response.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
			LOGGER.error(
					"Error while executing the action : " + e.getMessage(), e);
		}
		return error();
	}

	protected Exception getLastException() {
		return lastException;
	}

	/**
	 * Handles redirection if needed and returns whether or not the page is
	 * redirected
	 * 
	 * @return <code>true</code> when redirection was done, else
	 *         <code>false</code>
	 */
	protected boolean handleRedirect() {
		if (redirectEnabled) {
			final HttpServletRequest request = ServletActionContext
					.getRequest();
			final String url = request.getRequestURL().toString()
					.replace(request.getContextPath() + "/", "");
			final String language = getHeaderSupport().getLanguage();
			String officialUrl = null;
			if (getHeaderSupport() instanceof HeaderSearchSupport) {
				officialUrl = ((HeaderSearchSupport) getHeaderSupport())
						.getAlternate(language, true);
			} else {
				officialUrl = getHeaderSupport().getAlternate(language);
			}
			if (!officialUrl.equals(url)) {
				if (url.startsWith(officialUrl)) {
					LOGGER.warn("REDIRECT ignored for:   " + url + " -> "
							+ officialUrl);
				} else {
					// Last chance, ignoring everything in the last SEO part
					try {
						int lastSlash = url.lastIndexOf("/");
						int lastSep = url.indexOf("-", lastSlash);
						if (lastSep > 0
								&& url.substring(0, lastSep).equals(
										officialUrl.substring(0, lastSep))) {
							LOGGER.warn("REDIRECT ignored (last SEO unmatch) for:   "
									+ url + " -> " + officialUrl);
							return false;
						}
					} catch (Exception e) {
						LOGGER.error(
								"REDIRECT: Error while trying to remove last SEO part from '"
										+ url + "'", e);
					}
					LOGGER.warn("REDIRECT:   " + url + " -> " + officialUrl);
					LOGGER.warn("REDIRECT: ->" + officialUrl);
					final HttpServletResponse servletResponse = ServletActionContext
							.getResponse();
					servletResponse.setStatus(HttpStatus.SC_MOVED_PERMANENTLY);
					servletResponse.setHeader("Location", officialUrl);
					servletResponse.setHeader("Cache-Control",
							"max-age=3600, must-revalidate");
					redirectUrl = officialUrl;
					return true;
				}
			}
		}
		return false;
	}

	// /**
	// * By default redirect is not supported unless explicitely activated
	// *
	// * @return whether or not this page supports 301 redirect if canonical
	// does
	// * not match request URL
	// *
	// */
	// protected boolean isRedirectSupported() {
	// return false;
	// }

	protected String notFoundStatus() {
		final HttpServletResponse response = ServletActionContext.getResponse();
		response.setStatus(404);
		return NOT_FOUND;
	}

	private String loginTimeoutError(String message) {
		errorMessage = message;
		final HttpServletRequest request = ServletActionContext.getRequest();
		// url = request.getRequestURL().toString()
		// .replace(request.getContextPath() + "/", "");
		queryParams = request.getQueryString();
		try {
			userEmail = ((UsersService) usersService)
					.getEmailFromToken(getNxtpUserToken());
		} catch (RuntimeException e) {
			LOGGER.error("Unable to get email from token " + getNxtpUserToken()
					+ ": " + e.getMessage());
			userEmail = "";
		}
		final HttpServletResponse response = ServletActionContext.getResponse();
		response.setStatus(getLoginRequiredErrorCode());
		return INPUT;
	}

	protected int getLoginRequiredErrorCode() {
		return 403;
	}

	protected abstract String doExecute() throws Exception;

	protected void checkCurrentUser(User user) throws UserLoginTimeoutException {
		if (user == null) {
			throwConnectionTimeout();
		} else {
			final Date timeout = user.getOnlineTimeout();
			// If we have already spend half of our timeout, we reset timeout
			if (timeout.getTime() - System.currentTimeMillis() < refreshTimeoutMillisec) {
				final boolean previousWritableState = ContextHolder
						.isWritable();
				ContextHolder.toggleWrite();
				((UsersService) usersService).refreshUserOnlineTimeout(user,
						nxtpUserToken);
				searchService.updateUserOnlineStatus(user);
				if (!previousWritableState) {
					ContextHolder.toggleReadonly();
				}
			}
			// Update our logged flag
			logged = true;
		}
	}

	public boolean isLogged() {
		return logged;
	}

	public void throwConnectionTimeout() throws UserLoginTimeoutException {
		throw new UserLoginTimeoutException("login.mustLogin");
	}

	public void setNxtpUserToken(String nxtpUserToken) {
		this.nxtpUserToken = nxtpUserToken;
	}

	public String getNxtpUserToken() {
		return nxtpUserToken;
	}

	public final void setUrlService(UrlService urlService) {
		this.urlService = urlService;
	}

	public final void setApiService(ApiService apiService) {
		this.apiService = apiService;
	}

	public final UrlService getUrlService() {
		return urlService;
	}

	protected final ApiService getApiService() {
		if (ActionContext.getContext() != null
				&& UrlConstants.TEST_SUBDOMAIN.equals(ActionContext
						.getContext().get(Constants.ACTION_CONTEXT_SUBDOMAIN))) {
			return noSeoApiService;
		} else {
			return apiService;
		}
	}

	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	protected MessageSource getMessageSource() {
		return messageSource;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getUserEmail() {
		return userEmail;
	}

	public String getUrl() {
		return url;
	}

	public String getQueryParams() {
		return queryParams;
	}

	public void setConnectionTimeoutInMinutes(int connectionTimeoutInMinutes) {
		this.refreshTimeoutMillisec = connectionTimeoutInMinutes * 60 * 1000 / 2;
	}

	public final void setUsersService(CalExtendedPersistenceService usersService) {
		this.usersService = usersService;
	}

	protected CalExtendedPersistenceService getUsersService() {
		return usersService;
	}

	public final void setUserSearchService(
			SearchPersistenceService searchService) {
		this.searchService = searchService;
	}

	@Override
	public HeaderSupport getHeaderSupport() {
		return headerSupport;
	}

	@Override
	public void setHeaderSupport(HeaderSupport headerSupport) {
		this.headerSupport = headerSupport;
	}

	public final void setDomainName(String domainName) {
		this.domainName = domainName;
	}

	protected final String getDomainName() {
		return domainName;
	}

	public String getFullDomain() {
		return LocalizationHelper.buildFullDomain(domainName, getLocale());
	}

	public String getHomepageUrl() {
		return LocalizationHelper.buildUrl(getLocale(), domainName, "/");
	}

	public String getImage(String img) {
		return MediaHelper.getImageUrl(img);
	}

	public String getLanguageLabel(String langCode) {
		return messageSource.getMessage(MESSAGE_KEY_LANG + langCode, null,
				getLocale());
	}

	@Override
	public final void setLoginSupport(LoginSupport loginSupport) {
		this.loginSupport = loginSupport;
	}

	@Override
	public final LoginSupport getLoginSupport() {
		return loginSupport;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getLanguage() {
		if (language != null) {
			return language;
		} else {
			return getLocale().getLanguage();
		}
	}

	@Override
	public Locale getLocale() {
		if (overriddenLocale != null) {
			return overriddenLocale;
		} else if (language != null) {
			overriddenLocale = new Locale(language);
			return overriddenLocale;
		}
		return super.getLocale();
	}

	public void setSearchTypeUrlCode(String searchTypeCode) {
		currentSearchType = SearchType.fromUrlAction(searchTypeCode);
	}

	protected SearchType getCurrentSearchType() {
		return currentSearchType;
	}

	public void setNoSeoApiService(ApiService noSeoApiService) {
		this.noSeoApiService = noSeoApiService;
	}

	public String buildUrl(String relativePath) {
		return LocalizationHelper.buildUrl(getLocale(), domainName,
				relativePath);
	}

	public String buildSecuredUrl(String relativePath) {
		return LocalizationHelper.buildUrl(getLocale().getLanguage(),
				domainName, relativePath, true);
	}

	public boolean isGoogleEnabled() {
		final ActionContext context = ActionContext.getContext();
		if (!googleEnabled) {
			return googleEnabled;
		} else {
			return !(context != null && UrlConstants.TEST_SUBDOMAIN
					.equals(context.get(Constants.ACTION_CONTEXT_SUBDOMAIN)));
		}
	}

	public void setGoogleEnabled(boolean googleEnabled) {
		this.googleEnabled = googleEnabled;
	}

	public String getPromoteLink() {
		return getUrlService().getPromoteUrl(getLocale());
	}

	public void setRightsManagementService(
			RightsManagementService rightsManagementService) {
		this.rightsManagementService = rightsManagementService;
	}

	public RightsManagementService getRightsManagementService() {
		return rightsManagementService;
	}

	@Override
	public void setAdBannerSupport(AdBannerSupport adBannerSupport) {
		this.adBannerSupport = adBannerSupport;
	}

	@Override
	public AdBannerSupport getAdBannerSupport() {
		return adBannerSupport;
	}

	protected String error() {
		return response(ERROR, 500);
	}

	protected String error(int statusCode) {
		return response(ERROR, statusCode);
	}

	protected String unauthorized() {
		return response(INPUT, 401);
	}

	protected String response(String code, int statusCode) {
		final HttpServletResponse response = ServletActionContext.getResponse();
		response.setStatus(statusCode);
		return code;
	}

	protected void setLoginRedirectUrl(String url) {
		this.url = url;
	}

	public final String getMediaUrl(String url) {
		return urlService.getMediaUrl(url);
	}

	public final String getStaticUrl(String url) {
		return urlService.getStaticUrl(url);
	}

	public String getRedirectUrl() {
		return redirectUrl;
	}

	public String getSafeJson() {
		try {
			return ((JsonProvider) this).getJson();
		} catch (Throwable e) {
			final HttpServletResponse response = ServletActionContext
					.getResponse();
			response.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
			final JsonStatus status = new JsonStatus();
			status.setError(true);
			status.setMessage(e.getMessage());
			LOGGER.error("Error while generating JSON: " + e.getMessage(), e);
			return JSONObject.fromObject(status).toString();
		}
	}

	public String getSafeJsonError() {
		try {
			if (this instanceof JsonProviderWithError) {
				return ((JsonProviderWithError) this).getJsonError();
			} else {
				final JsonStatus status = new JsonStatus();
				status.setError(true);
				status.setMessage(getErrorMessage() != null ? getErrorMessage()
						: (lastException != null ? lastException.getMessage()
								: "[No exception caught]"));
				LOGGER.error(
						"Error catched by safeJsonError: "
								+ status.getMessage(), lastException);
				return JSONObject.fromObject(status).toString();
			}
		} catch (Throwable e) {
			final HttpServletResponse response = ServletActionContext
					.getResponse();
			response.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
			final JsonStatus status = new JsonStatus();
			status.setError(true);
			status.setMessage(e.getMessage());
			LOGGER.error(
					"Error while generating error message JSON: "
							+ e.getMessage(), e);
			return JSONObject.fromObject(status).toString();
		}
	}

}
