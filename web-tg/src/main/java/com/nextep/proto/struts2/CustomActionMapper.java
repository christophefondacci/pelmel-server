package com.nextep.proto.struts2;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.dispatcher.mapper.ActionMapper;
import org.apache.struts2.dispatcher.mapper.ActionMapping;
import org.apache.struts2.dispatcher.mapper.DefaultActionMapper;

import com.nextep.events.model.Event;
import com.nextep.events.model.EventSeries;
import com.nextep.geo.model.Place;
import com.nextep.proto.model.SearchType;
import com.nextep.proto.model.UrlConstants;
import com.nextep.users.model.User;
import com.opensymphony.xwork2.config.ConfigurationManager;
import com.videopolis.calm.exception.CalException;
import com.videopolis.calm.factory.CalmFactory;
import com.videopolis.calm.model.ItemKey;

public class CustomActionMapper implements ActionMapper {

	private static final Log LOGGER = LogFactory
			.getLog(CustomActionMapper.class);
	private static final String SEARCH_GEO_KEY = "geoKey";
	private static final String SEARCH_TYPE = "searchType";
	private static final String SEARCH_FACETS = "facets";
	private static final String SEARCH_AMENITIES = "amenities";
	private static final String SEARCH_WEIGHT = "weight_kg";
	private static final String SEARCH_HEIGHT = "height_cm";
	private static final String SEARCH_AGE = "age";
	private static final String SEARCH_BIRTH = "birthyear";
	private static final String SEARCH_PAGE_OFFSET = "pageOffset";

	private static final String SITEMAP_PAGETYPE = "pageType";
	private static final String SITEMAP_SEARCHTYPE = "searchType";
	private static final String SITEMAP_PAGE = "page";

	private static final String LOCALE = "request_only_locale";
	public final ActionMapper baseMapper = new DefaultActionMapper();

	@Override
	public ActionMapping getMapping(HttpServletRequest request,
			ConfigurationManager configManager) {
		final ActionMapping am = new ActionMapping();
		am.setNamespace("/");
		final Map<String, Object> actionParams = new HashMap<String, Object>();
		// // Extracting locale
		// final String serverName = request.getServerName();
		// final String[] serverParts = serverName.split("\\.");
		// final String language = serverParts[0];
		// Locale locale = ActionContext.getContext().getLocale();
		// if ("www".equalsIgnoreCase(language)) {
		// locale = new Locale("en");
		// } else {
		// locale = new Locale(language);
		// }
		// actionParams.put(LOCALE, locale);
		// Parsing URI
		String uri = request.getRequestURI();
		uri = uri.substring(uri.indexOf("//") + 2);
		final String[] args = uri.split("/");
		if (args.length >= 2) {
			final String commandWithSeo = args[0];
			final String command = commandWithSeo.split("-")[0];
			if (UrlConstants.PROFILE_PAGE.equals(commandWithSeo)) {
				am.setName("myProfile");
			} else if (UrlConstants.OVERVIEW_PAGE.equals(command)) {
				final String searchTypeUrlCode = args[1].split("-")[0];
				final String seoId = args[2];
				try {
					final String id = seoId.split("-")[0];
					final ItemKey key = CalmFactory.parseKey(id);
					final String calType = key.getType();
					if (User.CAL_TYPE.equals(calType)) {
						am.setName("userOverview");
					} else if (Place.CAL_TYPE.equals(calType)) {
						am.setName("placeOverview");
					} else if (Event.CAL_ID.equals(calType)
							|| EventSeries.SERIES_CAL_ID.equals(calType)) {
						am.setName("eventOverview");
					}
					actionParams.put("id", id);
					actionParams.put("searchTypeUrlCode", searchTypeUrlCode);
					am.setParams(actionParams);
					return am;
				} catch (CalException e) {
					LOGGER.error(e);
					return null;
				}
			} else if (UrlConstants.SEARCH_PAGE.equals(command)) {
				final String searchStr = args[1];
				final String[] searchArgs = searchStr.split("-");
				if (UrlConstants.SEARCH_TYPE_USERS.equals(searchArgs[0])) {
					am.setName("searchUser");
				} else if (UrlConstants.SEARCH_TYPE_BARS.equals(searchArgs[0])) {
					am.setName("searchPlace");
					actionParams.put(SEARCH_TYPE, SearchType.BARS);
				} else if (UrlConstants.SEARCH_TYPE_ASSOCIATION
						.equals(searchArgs[0])) {
					am.setName("searchPlace");
					actionParams.put(SEARCH_TYPE, SearchType.ASSOCIATIONS);
				} else if (UrlConstants.SEARCH_TYPE_CLUB.equals(searchArgs[0])) {
					am.setName("searchPlace");
					actionParams.put(SEARCH_TYPE, SearchType.CLUBS);
				} else if (UrlConstants.SEARCH_TYPE_SAUNAS
						.equals(searchArgs[0])) {
					am.setName("searchPlace");
					actionParams.put(SEARCH_TYPE, SearchType.SAUNAS);
				} else if (UrlConstants.SEARCH_TYPE_SEXCLUB
						.equals(searchArgs[0])) {
					am.setName("searchPlace");
					actionParams.put(SEARCH_TYPE, SearchType.SEXCLUBS);
				} else if (UrlConstants.SEARCH_TYPE_SHOP.equals(searchArgs[0])) {
					am.setName("searchPlace");
					actionParams.put(SEARCH_TYPE, SearchType.SHOPS);
				} else if (UrlConstants.sEARCH_TYPE_RESTAURANT
						.equals(searchArgs[0])) {
					am.setName("searchPlace");
					actionParams.put(SEARCH_TYPE, SearchType.RESTAURANTS);
				} else if (UrlConstants.SEARCH_TYPE_HOTELS
						.equals(searchArgs[0])) {
					am.setName("searchPlace");
					actionParams.put(SEARCH_TYPE, SearchType.HOTELS);
				} else if (UrlConstants.SEARCH_TYPE_OUTDOORS
						.equals(searchArgs[0])) {
					am.setName("searchPlace");
					actionParams.put(SEARCH_TYPE, SearchType.OUTDOORS);
				} else if (UrlConstants.SEARCH_TYPE_EVENTS
						.equals(searchArgs[0])) {
					am.setName("searchEvent");
				} else if (UrlConstants.SEARCH_TYPE_NEWS.equals(searchArgs[0])) {
					am.setName("searchNews");
				} else if (UrlConstants.SEARCH_TYPE_MAP.equals(searchArgs[0])) {
					am.setName("searchMap");
				} else {
					// Default bar search
					am.setName("searchPlace");
					actionParams.put(SEARCH_TYPE, SearchType.BARS);
				}
				if (args.length > 2) {
					final String searchKeyStr = args[2];
					final String[] searchKeys = searchKeyStr.split("-");
					if (searchKeys.length >= 1) {
						final String geoKey = searchKeys[0];
						actionParams.put(SEARCH_GEO_KEY, geoKey);
						if (searchKeys.length >= 2) {
							for (int i = 1; i < searchKeys.length; i++) {
								final String searchArg = searchKeys[i];
								processArg(actionParams, searchArg);
							}
						}
					}
				}
				am.setParams(actionParams);
				return am;
			}
		} else {
			if ("sitemap.xml".equals(uri)) {
				am.setName("sitemaproot");
				return am;
			} else if (uri.startsWith("sitemapindex-")) {
				am.setName("sitemapindex");
				final String[] sitemapArgs = uri.replace(".xml", "").split("-");
				actionParams.put(SITEMAP_PAGETYPE, sitemapArgs[1]);
				actionParams.put(SITEMAP_SEARCHTYPE, sitemapArgs[2]);
				am.setParams(actionParams);
				return am;
			} else if (uri.startsWith("sitemap-")) {
				am.setName("sitemap");
				final String[] sitemapArgs = uri.replace(".xml", "").split("-");
				actionParams.put(SITEMAP_PAGETYPE, sitemapArgs[1]);
				actionParams.put(SITEMAP_SEARCHTYPE, sitemapArgs[2]);
				actionParams.put(SITEMAP_PAGE, sitemapArgs[3].substring(1));
				am.setParams(actionParams);
				return am;
			}
		}
		final ActionMapping baseMapping = baseMapper.getMapping(request,
				configManager);
		// if (baseMapping != null) {
		// final Map<String, Object> baseParams = baseMapping.getParams();
		// if (baseParams == null) {
		// baseMapping.setParams(actionParams);
		// } else {
		// baseParams.put(LOCALE, locale);
		// }
		// } else {
		// ActionContext.getContext().setLocale(locale);
		// }
		return baseMapping;
	}

	private void processArg(Map<String, Object> actionParams, String arg) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Parsing argument " + arg);
		}
		if (arg.startsWith(UrlConstants.SEARCH_ARG_FACET_PREFIX)) {
			final String val = arg
					.substring(UrlConstants.SEARCH_ARG_FACET_PREFIX.length());
			String facets = (String) actionParams.get(SEARCH_FACETS);
			if (facets == null) {
				facets = val;
			} else {
				facets = facets + "," + val;
			}
			actionParams.put(SEARCH_FACETS, facets);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(" >> Facet prefix - facets = " + facets);
			}
		} else if (arg.startsWith(UrlConstants.SEARCH_ARG_AMENITIES_PREFIX)) {
			final String val = arg
					.substring(UrlConstants.SEARCH_ARG_AMENITIES_PREFIX
							.length());
			String facets = (String) actionParams.get(SEARCH_AMENITIES);
			if (facets == null) {
				facets = val;
			} else {
				facets = facets + "," + val;
			}
			actionParams.put(SEARCH_AMENITIES, facets);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(" >> Facet prefix - facets = " + facets);
			}
		} else if (arg.startsWith(UrlConstants.SEARCH_ARG_WEIGHT_PREFIX)) {
			final String val = arg
					.substring(UrlConstants.SEARCH_ARG_WEIGHT_PREFIX.length());
			LOGGER.debug(" >> Weight " + val);
			actionParams.put(SEARCH_WEIGHT, val);
		} else if (arg.startsWith(UrlConstants.SEARCH_ARG_HEIGHT_PREFIX)) {
			final String val = arg
					.substring(UrlConstants.SEARCH_ARG_HEIGHT_PREFIX.length());
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(" >> Height " + val);
			}
			actionParams.put(SEARCH_HEIGHT, val);
		} else if (arg.startsWith(UrlConstants.SEARCH_ARG_AGE_PREFIX)) {
			final String val = arg.substring(UrlConstants.SEARCH_ARG_AGE_PREFIX
					.length());
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(" >> Age " + val);
			}
			actionParams.put(SEARCH_AGE, val);

		} else if (arg.startsWith(UrlConstants.SEARCH_ARG_BIRTH_PREFIX)) {
			final String val = arg
					.substring(UrlConstants.SEARCH_ARG_BIRTH_PREFIX.length());
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(" >> Birth " + val);
			}
			actionParams.put(SEARCH_BIRTH, val);
		} else if (arg.startsWith(UrlConstants.SEARCH_ARG_PAGE)) {
			final String val = arg.substring(UrlConstants.SEARCH_ARG_PAGE
					.length());
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(" >> Page " + val);
			}
			actionParams.put(SEARCH_PAGE_OFFSET, val);
		}
	}

	@Override
	public ActionMapping getMappingFromActionName(String actionName) {
		return baseMapper.getMappingFromActionName(actionName);
	}

	@Override
	public String getUriFromActionMapping(ActionMapping mapping) {
		return baseMapper.getUriFromActionMapping(mapping);
	}

}
