package com.nextep.proto.services.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;

import com.nextep.events.model.CalendarType;
import com.nextep.events.model.Event;
import com.nextep.geo.model.GeographicItem;
import com.nextep.geo.model.Place;
import com.nextep.proto.helpers.DisplayHelper;
import com.nextep.proto.helpers.LocalizationHelper;
import com.nextep.proto.helpers.SearchHelper;
import com.nextep.proto.model.SearchType;
import com.nextep.proto.model.UrlConstants;
import com.nextep.proto.services.UrlService;
import com.nextep.users.model.User;
import com.opensymphony.xwork2.ActionContext;
import com.videopolis.apis.model.FacetInformation;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.smaug.common.factory.FacetFactory;
import com.videopolis.smaug.common.model.Facet;
import com.videopolis.smaug.common.model.FacetCategory;
import com.videopolis.smaug.common.model.FacetRange;

public class UrlServiceImpl implements UrlService {

	private static final Log LOGGER = LogFactory.getLog(UrlServiceImpl.class);
	private static final FacetCategory PLACE_TYPE_CATEGORY = SearchHelper
			.getPlaceTypeCategory();

	private MessageSource messageSource;
	private String webappPrefix = "";
	private boolean isAjaxSearch, isAjaxOverview;
	private boolean addJSCalls;
	@Resource(mappedName = "togaytherDomain")
	private String domainName;
	@Resource(mappedName = "mediaBaseUrl")
	private String mediaBaseUrl;
	@Resource(mappedName = "staticBaseUrl")
	private String staticBaseUrl;
	@Resource(mappedName = "togaytherBaseUrl")
	private String baseUrl;

	@Override
	public String buildSearchUrl(String targetHtmlElementId,
			GeographicItem geoItem, SearchType searchType) {
		return buildSearchUrl(targetHtmlElementId, geoItem, searchType, null, 0);
	}

	@Override
	public String buildMapSearchUrl(Locale locale, String targetHtmlElementId,
			GeographicItem geoItem) {
		return internalBuildAjaxSearchUrl(locale, UrlConstants.SEARCH_TYPE_MAP,
				targetHtmlElementId, geoItem, null, null, null, 0,
				isAjaxSearch, isAjaxSearch);
	}

	@Override
	public String getMapInfoWindowUrl(ItemKey geoKey) {
		return webappPrefix + "/mapInfo.action?geoKey=" + geoKey.toString();
	}

	private String internalBuildAjaxSearchUrl(Locale locale, String action,
			String targetHtmlElementId, CalmObject geoItem,
			FacetInformation currentFacetting, Facet newFacet,
			Facet removedFacet, int page, boolean isAjaxUrl, boolean addJSCall) {
		final Map<String, StringBuilder> buildersMap = new HashMap<String, StringBuilder>();
		final StringBuilder buf = new StringBuilder();
		if (addJSCall) {
			buf.append("javascript:call('");
		}
		buf.append(webappPrefix);
		// Generating page type SEO translation
		if (locale == null) {
			locale = ActionContext.getContext().getLocale();
		}
		final String seoPage = encode(DisplayHelper.getName(geoItem, locale));
		// Building URL
		buf.append("/" + UrlConstants.SEARCH_PAGE + "-" + seoPage + "/"
				+ action + "-");
		// Appending SEO representation
		try {
			final String msg = encode(messageSource.getMessage(
					UrlConstants.KEY_SEO_ACTION_PREFIX + action, null, locale));
			buf.append(msg);
		} catch (NoSuchMessageException e) {
			LOGGER.error("Ungenerable SEO action because of missing translation: "
					+ UrlConstants.KEY_SEO_ACTION_PREFIX + action);
		}
		// Appending geo name and ID
		if (geoItem != null) {
			final ItemKey geoKey = geoItem.getKey();
			buf.append("/" + geoKey.toString());
		}

		// handling facetting
		final List<Facet> urlFacets = new ArrayList<Facet>();
		final List<Facet> urlAmenities = new ArrayList<Facet>();
		if (currentFacetting != null) {
			final Collection<Facet> facets = currentFacetting.getFacetFilters();
			// Generating the facet comma-separated list
			for (Facet f : facets) {
				if (!PLACE_TYPE_CATEGORY.equals(f.getFacetCategory())) {
					// Appending the facet code, only if not removed
					if (f.getFacetCategory().isRange()) {
						if (removedFacet == null
								|| !removedFacet.getFacetCategory().equals(
										f.getFacetCategory())) {
							urlFacets.add(f);
						}
					} else {
						if (removedFacet == null
								|| (!f.getFacetCode().equals(
										removedFacet.getFacetCode()))) {
							// Handling amenities differently
							if (SearchHelper.getAmenitiesFacetCategory()
									.equals(f.getFacetCategory())) {
								urlAmenities.add(f);
							} else {
								urlFacets.add(f);
							}
						}
					}
				}
			}
		}
		// Adding new facet
		if (newFacet != null) {
			// Handling amenities differently
			if (SearchHelper.getAmenitiesFacetCategory().equals(
					newFacet.getFacetCategory())) {
				urlAmenities.add(newFacet);
			} else {
				urlFacets.add(newFacet);
			}
		}
		if (!urlFacets.isEmpty()) {
			// Sorting facet list
			Collections.sort(urlFacets, new Comparator<Facet>() {
				@Override
				public int compare(Facet o1, Facet o2) {
					return o1.getFacetCode().compareTo(o2.getFacetCode());
				}
			});
			// Appending the URL "facets" query GET parameter
			String separator = "-" + UrlConstants.SEARCH_ARG_FACET_PREFIX;
			// Building facet list
			for (Facet f : urlFacets) {
				addFacet(f, separator, buf, buildersMap);
			}
		}
		// Generating amenities
		if (!urlAmenities.isEmpty()) {
			Collections.sort(urlAmenities, new Comparator<Facet>() {
				@Override
				public int compare(Facet o1, Facet o2) {
					return o1.getFacetCode().compareTo(o2.getFacetCode());
				}
			});
			// Appending the URL "amenities" query GET parameter
			String separator = "-" + UrlConstants.SEARCH_ARG_AMENITIES_PREFIX;
			for (Facet f : urlAmenities) {
				addFacet(f, separator, buf, buildersMap);
			}
		}
		if (page > 0) {
			buf.append("-p" + page);
		}
		// Appending range fragments
		for (StringBuilder b : buildersMap.values()) {
			buf.append(b.toString());
		}
		if (addJSCall) {
			buf.append("','" + targetHtmlElementId + "');");
		}
		return buf.toString();
	}

	private String encode(String urlFragment) {
		String encodedFragment = urlFragment;
		try {
			encodedFragment = URLEncoder.encode(urlFragment, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			LOGGER.error("Error while encoding localization URL fragment for "
					+ urlFragment + ": " + e.getMessage());
		}
		return encodedFragment;
	}

	private void addFacet(Facet f, String separator, StringBuilder buf,
			Map<String, StringBuilder> buildersMap) {
		if (!(f instanceof FacetRange)) {
			buf.append(separator);
			buf.append(f.getFacetCode());
			separator = ",";
		} else {
			final String category = f.getFacetCategory().getCategoryCode();
			// Retrieving (or initializing) the builder for this URL
			// section
			StringBuilder builder = buildersMap.get(category);
			if (builder == null) {
				builder = new StringBuilder();
				buildersMap.put(category, builder);
			}
			builder.append(buildRangeUrlHeader(f.getFacetCategory()));
			final FacetRange range = (FacetRange) f;
			builder.append(range.getLowerBoundCode());
			builder.append("to");
			builder.append(range.getHigherBoundCode());
		}
	}

	private String buildRangeUrlHeader(FacetCategory category) {
		return "-" + category.getCategoryCode().charAt(0);
	}

	private String getAction(SearchType searchType) {
		return searchType.getUrlAction();
	}

	@Override
	public String buildSearchUrl(String targetHtmlElementId,
			CalmObject geoItem, SearchType searchType,
			FacetInformation currentFacetting, int page) {
		return internalBuildAjaxSearchUrl(null, getAction(searchType),
				targetHtmlElementId, geoItem, currentFacetting, null, null,
				page, isAjaxSearch, isAjaxSearch);
	}

	@Override
	public String buildSearchUrl(Locale language, String targetHtmlElementId,
			CalmObject geoItem, SearchType searchType,
			FacetInformation currentFacetting, int page) {
		return internalBuildAjaxSearchUrl(language, getAction(searchType),
				targetHtmlElementId, geoItem, currentFacetting, null, null,
				page, isAjaxSearch, isAjaxSearch);
	}

	@Override
	public String buildSearchUrl(String targetHtmlElementId,
			CalmObject geoItem, SearchType searchType,
			FacetInformation currentFacetting, Facet newFacet,
			Facet removedFacet) {
		return internalBuildAjaxSearchUrl(null, getAction(searchType),
				targetHtmlElementId, geoItem, currentFacetting, newFacet,
				removedFacet, 0, isAjaxSearch, isAjaxSearch);
	}

	public void setWebappPrefix(String webappPrefix) {
		this.webappPrefix = webappPrefix;
	}

	@Override
	public String getPlaceOverviewUrl(String targetHtmlElementId,
			CalmObject place) {
		return getPlaceOverviewUrl(null, targetHtmlElementId, place);
	}

	@Override
	public String getPlaceOverviewUrl(Locale locale,
			String targetHtmlElementId, CalmObject place) {
		final Place p = (Place) place;
		final SearchType searchType = SearchType
				.fromPlaceType(p.getPlaceType());
		return buildOverviewUrl(locale, targetHtmlElementId, place,
				getAction(searchType), isAjaxOverview, addJSCalls);
	}

	@Override
	public String getUserOverviewUrl(String targetHtmlElementId, CalmObject user) {
		return getUserOverviewUrl(null, targetHtmlElementId, user);
	}

	@Override
	public String getUserOverviewUrl(Locale locale, String targetHtmlElementId,
			CalmObject user) {
		return buildOverviewUrl(locale, targetHtmlElementId, user,
				UrlConstants.SEARCH_TYPE_USERS, isAjaxOverview, addJSCalls);
	}

	private String buildOverviewUrl(Locale locale, String targetHtmlElementId,
			CalmObject item, String actionName, boolean isAjax,
			boolean addJSCall) {
		final StringBuilder buf = new StringBuilder();
		// Appending javascript call if ajax
		if (addJSCall) {
			buf.append("javascript:call('");
		}
		buf.append(webappPrefix + "/");
		final ItemKey key = item.getKey();
		// Switching ajax / non ajax action
		if (isAjax) {
			buf.append("ajax");
			buf.append(actionName.substring(0, 1).toUpperCase());
			buf.append(actionName.substring(1));
			buf.append(".action?id=");
		} else {
			if (locale == null) {
				locale = ActionContext.getContext().getLocale();
			}
			String seoPage = encode(messageSource.getMessage(
					UrlConstants.KEY_OVERVIEW_PAGE, null, locale));
			String seoObjTypeKey = key.getType();
			if (item instanceof Place) {
				final Place place = (Place) item;
				seoObjTypeKey = place.getPlaceType();
				seoPage = encode(DisplayHelper.getName(place.getCity()));
			}
			final String seoObjType = encode(messageSource.getMessage(
					UrlConstants.KEY_SEO_TYPE_PREFIX + seoObjTypeKey, null,
					locale));
			buf.append(UrlConstants.OVERVIEW_PAGE + "-" + seoPage + "/"
					+ actionName + "-" + seoObjType + "/");
		}
		buf.append(key.toString());
		buf.append("-" + encode(DisplayHelper.getName(item, locale)));
		// Appending javascript arguments for ajax (target html ID)
		if (addJSCall) {
			buf.append("','mainContent')");
		}
		return buf.toString();
	}

	public void setAjaxSearch(boolean isAjaxSearch) {
		this.isAjaxSearch = isAjaxSearch;
	}

	public void setAjaxOverview(boolean isAjaxOverview) {
		this.isAjaxOverview = isAjaxOverview;
	}

	@Override
	public String buildUserSearchUrl(String targetHtmlElementId,
			CalmObject geoItem, FacetInformation currentFacetting,
			FacetRange range) {
		FacetRange toRemoveRange = range;
		// Special case for age we need to remove the birthyear facet because of
		// age to year conversion
		if ("age".equals(range.getFacetCategory().getCategoryCode())) {
			toRemoveRange = FacetFactory.createFacetRange(
					SearchHelper.getFacetCategory("birthyear"), "",
					Long.MIN_VALUE, Long.MAX_VALUE);
		}
		return internalBuildAjaxSearchUrl(null, UrlConstants.SEARCH_TYPE_USERS,
				targetHtmlElementId, geoItem, currentFacetting, range,
				toRemoveRange, 0, isAjaxSearch, false);
	}

	@Override
	public String getILikeUrl(String targetHtmlElementId, ItemKey element) {
		return webappPrefix + "/ilike.action?id=" + element.toString();
	}

	@Override
	public String getMediaAdditionFormUrl(String targetHtmlElementId,
			ItemKey element) {
		return webappPrefix + "/ajaxAddMediaDialog.action?id="
				+ element.toString();
		// + "','" + targetHtmlElementId + "')";
	}

	@Override
	public String getMediaAdditionFormUrl(String targetHtmlElementId,
			ItemKey element, String redirectUrl) {
		String encodedRedirect = null;
		try {
			encodedRedirect = URLEncoder.encode(redirectUrl, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			LOGGER.error("Unsupported encoding thrown when encoding media addition redirect url : "
					+ e.getMessage() + " [->ignoring redirect]");
		}
		StringBuilder buf = new StringBuilder();
		buf.append(webappPrefix + "/ajaxAddMediaDialog.action?id="
				+ element.toString());
		if (encodedRedirect != null) {
			buf.append("&redirectUrl=" + encodedRedirect);
		}
		return buf.toString();
	}

	@Override
	public String getWriteMessageDialogUrl(String targetHtmlElementId,
			ItemKey element) {
		return webappPrefix + "/ajaxWriteMsgDialog.action?to="
				+ element.toString();
	}

	@Override
	public String getEventOverviewUrl(String targetHtmlElementId,
			CalmObject event) {
		return getEventOverviewUrl(null, targetHtmlElementId, event);
	}

	@Override
	public String getEventOverviewUrl(Locale locale,
			String targetHtmlElementId, CalmObject event) {
		return buildOverviewUrl(locale, targetHtmlElementId, event,
				UrlConstants.SEARCH_TYPE_EVENTS, isAjaxOverview, addJSCalls);

	}

	@Override
	public String getEventEditionFormUrl(String targetHtmlElementId,
			ItemKey element) {
		return getEventEditionFormUrl(targetHtmlElementId, element, null);
	}

	@Override
	public String getEventEditionFormUrl(String targetHtmlElementId,
			ItemKey element, CalendarType calendarType) {
		final StringBuilder buf = new StringBuilder();
		if (element != null) {
			buf.append(webappPrefix + "/ajaxGetEventForm?id="
					+ element.toString());
		} else {
			buf.append(webappPrefix + "/ajaxGetEventForm");
		}
		// Default calendar type is EVENT
		if (calendarType == null) {
			calendarType = CalendarType.EVENT;
		}
		buf.append("&calendarType=" + calendarType.name());
		return buf.toString();
	}

	@Override
	public String getPlaceEditionFormUrl(String targetHtmlElementId,
			ItemKey element, String placeType) {
		return webappPrefix + "/ajaxGetPlaceForm.action?id="
				+ element.toString() + "&placeType=" + placeType;
	}

	@Override
	public String getOverviewUrl(String targetHtmlElementId, CalmObject item) {
		return getOverviewUrl(null, targetHtmlElementId, item);
	}

	@Override
	public String getOverviewUrl(Locale locale, String targetHtmlElementId,
			CalmObject item) {
		final String type = item.getKey().getType();
		if (Event.CAL_ID.equals(type)) {
			return getEventOverviewUrl(locale, targetHtmlElementId, item);
		} else if (User.CAL_TYPE.equals(type)) {
			return getUserOverviewUrl(locale, targetHtmlElementId, item);
		} else if (Place.CAL_TYPE.equals(type)) {
			return getPlaceOverviewUrl(locale, targetHtmlElementId, item);
		} else if (item instanceof GeographicItem) {
			// Default bar-search
			return buildSearchUrl(DisplayHelper.getDefaultAjaxContainer(),
					(GeographicItem) item, SearchType.BARS);
		}
		return "";
	}

	public void setAddJSCalls(boolean addJSCalls) {
		this.addJSCalls = addJSCalls;
	}

	@Override
	public String getCommentUrl(String targetHtmlElementId, ItemKey parentKey,
			int page) {
		return "/ajaxComments.action?id=" + parentKey.toString() + "&page="
				+ page;
	}

	@Override
	public String getActivitiesUrl(String targetHtmlElementId,
			CalmObject target, CalmObject user, GeographicItem geoItem,
			int page, String typeFilter) {
		final StringBuilder buf = new StringBuilder();
		buf.append("/ajaxActivities.action?page=" + page);
		if (target != null) {
			buf.append("&target=" + target.getKey().toString());
		}
		if (user != null) {
			buf.append("&user=" + user.getKey().toString());
		}
		if (geoItem != null) {
			buf.append("&geoKey=" + geoItem.getKey().toString());
		}
		if (typeFilter != null) {
			buf.append("&typeFilter=" + typeFilter);
		}
		if (targetHtmlElementId != null) {
			buf.append("&htmlId=" + targetHtmlElementId);
		}
		return buf.toString();
	}

	@Override
	public String getToggleUserTagUrl(String targetHtmlElementId,
			ItemKey taggedItemKey, ItemKey tag) {
		return "/toggleUserTag.action?id=" + taggedItemKey.toString() + "&tag="
				+ tag.toString();
	}

	@Override
	public String getMyMessagesUrl(String targetHtmlElementId,
			CalmObject parent, int page) {
		return buildOverviewUrl(null, targetHtmlElementId, parent,
				"myMessages", true, true);
	}

	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	@Override
	public String getHomepageUrl(Locale locale) {
		return LocalizationHelper.buildUrl(locale, domainName, "/");
	}

	@Override
	public String getXMLSitemapIndexUrl(Locale locale, String pageType,
			SearchType searchType) {
		String url = "/sitemapindex-" + pageType + "-" + searchType.name()
				+ ".xml";
		return LocalizationHelper.buildUrl(locale, domainName, url);
	}

	@Override
	public String getXMLSitemapUrl(Locale locale, String pageType,
			SearchType searchType, int page) {
		String url = "/sitemap-" + pageType + "-" + searchType.name() + "-p"
				+ page + ".xml";
		return LocalizationHelper.buildUrl(locale, domainName, url);
	}

	@Override
	public String getPromoteUrl(Locale l) {
		return LocalizationHelper.buildUrl(l.getLanguage(), domainName,
				"/promote", true);
	}

	@Override
	public String getSponsorshipUrl(CalmObject object) {
		return "/ajaxGetSponsorForm?id=" + object.getKey();
	}

	@Override
	public String getSponsorshipActivationUrl() {
		return "/activateSponsorship";
	}

	@Override
	public String getMediaUrl(String url) {
		String resultUrl = url;
		if (!url.startsWith("http://") && !url.startsWith("//")) {
			resultUrl = mediaBaseUrl + url;
		}
		return resultUrl;
	}

	@Override
	public String getStaticUrl(String url) {
		String resultUrl = url;
		if (!url.startsWith("http://") && !url.startsWith("//")) {
			resultUrl = staticBaseUrl + url;
		}
		return resultUrl;
	}

	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}

	public void setMediaBaseUrl(String mediaBaseUrl) {
		this.mediaBaseUrl = mediaBaseUrl;
	}

	public void setStaticBaseUrl(String staticBaseUrl) {
		this.staticBaseUrl = staticBaseUrl;
	}

	@Override
	public String getResetPasswordUrl(User user) {
		return baseUrl + "/resetPassword?nxtpUserToken=" + user.getToken();
	}
}
