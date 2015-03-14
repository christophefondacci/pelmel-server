package com.nextep.smaug.solr.service.impl;

import java.net.MalformedURLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.client.solrj.response.FieldStatsInfo;
import org.apache.solr.client.solrj.response.QueryResponse;

import com.nextep.activities.model.Activity;
import com.nextep.events.model.Event;
import com.nextep.events.model.EventSeries;
import com.nextep.geo.model.Admin;
import com.nextep.geo.model.City;
import com.nextep.geo.model.Country;
import com.nextep.geo.model.Place;
import com.nextep.smaug.solr.model.QueryBuilder;
import com.nextep.smaug.solr.model.impl.ActivitySearchItemImpl;
import com.nextep.smaug.solr.model.impl.SearchItemImpl;
import com.nextep.smaug.solr.model.impl.SearchResponseImpl;
import com.nextep.smaug.solr.model.impl.SearchTextItemImpl;
import com.nextep.smaug.solr.model.impl.SolrQueryBuilderImpl;
import com.nextep.smaug.solr.model.impl.SuggestResponseImpl;
import com.nextep.users.model.User;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.calm.model.Localized;
import com.videopolis.smaug.common.factory.FacetFactory;
import com.videopolis.smaug.common.model.FacetCategory;
import com.videopolis.smaug.common.model.SearchMethod;
import com.videopolis.smaug.common.model.SearchScope;
import com.videopolis.smaug.common.model.SuggestScope;
import com.videopolis.smaug.common.model.impl.FacetCategoryImpl;
import com.videopolis.smaug.common.model.impl.FacetImpl;
import com.videopolis.smaug.exception.SearchException;
import com.videopolis.smaug.model.FacetCount;
import com.videopolis.smaug.model.SearchItem;
import com.videopolis.smaug.model.SearchResponse;
import com.videopolis.smaug.model.SearchSettings;
import com.videopolis.smaug.model.SearchTextResponse;
import com.videopolis.smaug.model.SearchTextSettings;
import com.videopolis.smaug.model.SearchWindow;
import com.videopolis.smaug.model.impl.FacetCountImpl;
import com.videopolis.smaug.service.SearchService;

public class SolrSearchServiceImpl implements SearchService {
	private static final String DISTANCE_FIELD = "geo_distance";
	/** Label of geo query type. */
	private static final String GEO_QUERY_TYPE = "geo";
	/** Label of latitude field for geo query. */
	private static final String LATITUDE_PARAMETER = "lat";
	/** Label of longitude field for geo query. */
	private static final String LONGITUDE_PARAMETER = "long";
	/** Label of radius field for geo query. */
	private static final String RADIUS_PARAMETER = "radius";
	private static final String HIGHLIGHTING_PARAMETER = "hl";
	private static final String HIGHLIGHTING_FIELD_LIST = "hl.fl";
	private static final String SUGGEST_HL_PARAMETER = "name";
	private static final String ID_FIELD = "id";

	private static final DateFormat EVENT_DATE_FORMAT = new SimpleDateFormat(
			"yyyyMMddHHmmss");

	private final static Log log = LogFactory
			.getLog(SolrSearchServiceImpl.class);
	private String userSolrUrl;
	private String placesSolrUrl;
	private String eventsSolrUrl;
	private String activitiesSolrUrl;
	private String suggestSolrUrl;
	private String citiesSolrUrl;
	private boolean filterSeo;

	private CommonsHttpSolrServer userSolrServer;
	private CommonsHttpSolrServer placesSolrServer;
	private CommonsHttpSolrServer eventsSolrServer;
	private CommonsHttpSolrServer activitiesSolrServer;
	private CommonsHttpSolrServer suggestSolrServer;
	private CommonsHttpSolrServer citiesSolrServer;
	private QueryBuilder queryBuilder;

	public void init() throws MalformedURLException {
		userSolrServer = new CommonsHttpSolrServer(userSolrUrl);
		placesSolrServer = new CommonsHttpSolrServer(placesSolrUrl);
		eventsSolrServer = new CommonsHttpSolrServer(eventsSolrUrl);
		activitiesSolrServer = new CommonsHttpSolrServer(activitiesSolrUrl);
		suggestSolrServer = new CommonsHttpSolrServer(suggestSolrUrl);
		citiesSolrServer = new CommonsHttpSolrServer(citiesSolrUrl);
	}

	@Override
	public SearchResponse searchNear(Localized point, double radius,
			SearchSettings settings, SearchWindow window) {
		final SolrQuery query = queryBuilder.buildQuery(settings, window);
		// Building the query string
		if (query.getQuery() == null || query.getQuery().equals("")) {
			query.setQuery("*:*");
		}
		query.setQueryType(GEO_QUERY_TYPE);
		query.setParam(LATITUDE_PARAMETER, String.valueOf(point.getLatitude()));
		query.setParam(LONGITUDE_PARAMETER,
				String.valueOf(point.getLongitude()));
		query.setParam(RADIUS_PARAMETER, String.valueOf(radius));
		query.setFields(ID_FIELD, DISTANCE_FIELD);

		final String kind = settings.getReturnedType();
		log.info("Solr geo query = " + query);
		if (Place.CAL_TYPE.equals(kind)) {
			query.addSortField(DISTANCE_FIELD, SolrQuery.ORDER.asc);
			StringBuilder buf = new StringBuilder();
			if (filterSeo) {
				buf.append("seoIndexed:1");
			}
			switch (settings.getSearchScope()) {
			case BOOSTED_PLACES:
				final Date now = new Date();
				final String nowTag = EVENT_DATE_FORMAT.format(now);
				buf.append(" AND adBoostEndDate:[" + nowTag + " TO *]");
				buf.append(" AND adBoostValue:[1 TO *]");
				query.addSortField("adBoostValue", ORDER.desc);
				break;
			}
			if (buf.length() > 0) {
				query.setQuery(buf.toString());
			}
			return processSolrQuery(placesSolrServer, query, settings, window);
		} else if (settings.getSearchScope() == SearchScope.NEARBY_ACTIVITIES) {
			query.addSortField(DISTANCE_FIELD, SolrQuery.ORDER.asc);
			return processSolrQuery(activitiesSolrServer, query, settings,
					window);
		} else if (settings.getSearchScope() == SearchScope.CITY) {
			query.addSortField(DISTANCE_FIELD, SolrQuery.ORDER.asc);
			return processSolrQuery(citiesSolrServer, query, settings, window);
		} else if (Event.CAL_ID.equals(settings.getReturnedType())) {
			final String dateTag = EVENT_DATE_FORMAT.format(new Date());
			query.setQuery("end_date:[" + dateTag + " TO *]");
			query.addSortField("start_date", ORDER.asc);
			query.addSortField(DISTANCE_FIELD, SolrQuery.ORDER.asc);
			return processSolrQuery(eventsSolrServer, query, settings, window);
		} else {
			query.addSortField(DISTANCE_FIELD, SolrQuery.ORDER.asc);
			return processSolrQuery(userSolrServer, query, settings, window);
		}
	}

	@Override
	public SearchResponse searchIn(ItemKey parent, SearchSettings settings,
			SearchWindow window) {
		final SolrQuery query = queryBuilder.buildQuery(settings, window);
		// Building the query string
		final StringBuilder buf = new StringBuilder();
		if (parent == null) {
			buf.append("cityId:*");
		} else if (City.CAL_ID.equals(parent.getType())) {
			buf.append("cityId:" + parent.toString());
		} else if (Admin.CAL_ID.equals(parent.getType())) {
			buf.append("(adm1:" + parent.toString() + " OR adm2:"
					+ parent.toString() + ")");
		} else if (Country.CAL_ID.equals(parent.getType())) {
			buf.append("countryId:" + parent.toString());
		} else if (Place.CAL_TYPE.equals(parent.getType())) {
			switch (settings.getSearchScope()) {
			case NEARBY_BLOCK:
				// Nearby block is the scope for search of users IN a place
				buf.append("currentPlaceTimeout:[" + System.currentTimeMillis()
						+ " TO *]");
				buf.append(" AND currentPlace:" + parent.toString());
				break;
			default:
				buf.append("places:" + parent.toString());
			}
		} else if (Event.CAL_ID.equals(parent.getType())
				|| EventSeries.SERIES_CAL_ID.equals(parent.getType())) {
			buf.append("events:" + parent.toString());
		} else if (User.CAL_TYPE.equals(parent.getType())
				&& User.CAL_TYPE.equals(settings.getReturnedType())) {
			buf.append("users:" + parent.toString());
		} else {
			buf.append("continentId:" + parent.toString());
		}

		// Handling scopes that restricts place types
		final SearchScope scope = settings.getSearchScope();
		switch (scope) {
		case BAR:
		case ASSO:
		case SAUNA:
		case CLUB:
		case RESTAURANT:
		case SEXCLUB:
		case SEXSHOP:
			buf.append(" AND placeType:" + scope.name().toLowerCase());
			break;
		case BOOSTED_PLACES:
			final Date now = new Date();
			final String nowTag = EVENT_DATE_FORMAT.format(now);
			buf.append(" AND adBoostEndDate:[" + nowTag + " TO *]");
			buf.append(" AND adBoostValue:[1 TO *]");
			query.addSortField("adBoostValue", ORDER.desc);
			break;
		}

		// Targetting the Solr server
		SolrServer solrServer = userSolrServer;
		if (settings.getSearchScope() == SearchScope.POI
				|| Place.CAL_TYPE.equals(settings.getReturnedType())) {
			solrServer = placesSolrServer;
			// Optional SEO filter
			if (filterSeo) {
				buf.append(" AND seoIndexed:1");
			}
		} else if (Event.CAL_ID.equals(settings.getReturnedType())) {
			solrServer = eventsSolrServer;
			final String dateTag = EVENT_DATE_FORMAT.format(new Date());
			buf.append(" AND end_date:[" + dateTag + " TO *]");
			query.addSortField("start_date", ORDER.asc);
		} else if (Activity.CAL_TYPE.equals(settings.getReturnedType())) {
			solrServer = activitiesSolrServer;
		}
		query.setQuery(buf.toString());
		log.info("Solr query = " + query);
		return processSolrQuery(solrServer, query, settings, window);
	}

	@Override
	public SearchTextResponse searchText(String textToSearch,
			SearchTextSettings settings, SearchWindow window) {
		final StringBuilder buf = new StringBuilder();
		if (settings.getSuggestScope().contains(SuggestScope.GEO_FULLTEXT)) {
			textToSearch = textToSearch.replaceAll(" ", " AND ");
			buf.append("fulltextName:(" + textToSearch + ")");
		} else {
			buf.append("name:(" + textToSearch + ")");
		}

		// Optionally restricting searched type
		String separator = " AND (";
		if (settings.getSuggestScope().contains(SuggestScope.DESTINATION)) {
			buf.append(separator + "type:" + City.CAL_ID);
			separator = " OR ";
		}
		if (settings.getSuggestScope().contains(SuggestScope.PLACE)) {
			buf.append(separator + "type:" + Place.CAL_TYPE);
			separator = " OR ";
		}
		if (settings.getSuggestScope().contains(SuggestScope.EVENT)) {
			buf.append(separator + "type:" + Event.CAL_ID);
			separator = " OR ";
		}
		if (settings.getSuggestScope().contains(SuggestScope.USER)) {
			buf.append(separator + "type:" + User.CAL_TYPE);
			separator = " OR ";
		}
		buf.append(")");
		// Building query
		final SolrQuery query = new SolrQuery(buf.toString());
		if (window != null) {
			query.setRows(window.getItemsPerPage());
			query.setStart(window.getPageNumber() * window.getItemsPerPage());
		}
		log.info("Suggest SOLR search: " + query);
		// Enabling highlighting of matches
		// query.setParam(HIGHLIGHTING_PARAMETER, true);
		// query.setParam(HIGHLIGHTING_FIELD_LIST, SUGGEST_HL_PARAMETER);
		QueryResponse response;
		try {
			response = suggestSolrServer.query(query);
			final List<SearchTextItemImpl> items = response
					.getBeans(SearchTextItemImpl.class);
			// Unwrapping highlights
			for (SearchTextItemImpl item : items) {
				if (response.getHighlighting() != null) {
					Map<String, List<String>> highlights = response
							.getHighlighting().get(item.getKey().toString());
					if (highlights != null) {
						final List<String> snippets = highlights.get("name");
						if (snippets != null && !snippets.isEmpty()) {
							item.setMatchedText(snippets.iterator().next());
						}
					}
				}
			}
			// Builds response and returns
			return new SuggestResponseImpl((List) items, settings, window,
					items.size());
		} catch (SolrServerException e) {
			throw new SearchException(
					"Unable to complete suggest SOLR query : " + e.getMessage(),
					e);
		}
	}

	@Override
	public SearchResponse searchAll(SearchSettings settings, SearchWindow window) {
		final SolrQueryBuilderImpl queryBuilder = new SolrQueryBuilderImpl();
		if (settings.getSearchMethod() == SearchMethod.NO_FACET_LIMIT) {
			queryBuilder.setNoFacetLimit(true);
		}
		final SolrQuery query = queryBuilder.buildQuery(settings, window);
		final StringBuilder queryBuf = new StringBuilder();
		String prefix = "";

		// Selecting SOLR server
		SolrServer server = userSolrServer;
		if (Place.CAL_TYPE.equals(settings.getReturnedType())) {
			server = placesSolrServer;
		} else if (Event.CAL_ID.equals(settings.getReturnedType())) {
			server = eventsSolrServer;
		} else if (User.CAL_TYPE.equals(settings.getReturnedType())) {
			server = userSolrServer;
		} else if (Activity.CAL_TYPE.equals(settings.getReturnedType())) {
			server = activitiesSolrServer;
			if (settings.getSearchScope() == SearchScope.USERS) {
				// A week = 60 x 60 x 24 x 7 x 1000 millis
				final Calendar cal = Calendar.getInstance();
				cal.add(Calendar.DAY_OF_YEAR, -7);
				final long lastWeekDate = Long
						.valueOf(ActivitySearchItemImpl.DATE_FORMAT.format(cal
								.getTime()));
				queryBuf.append("activityDate:[" + lastWeekDate + " TO *]");
				prefix = " AND ";
			}
		}

		if (settings.getSearchScope() == SearchScope.USER_LOCALIZATION) {
			// Getting current time
			final long currentTime = System.currentTimeMillis();
			// Restricting SOLR query to get users
			queryBuf.append(prefix + "currentPlaceTimeout: [" + currentTime
					+ " TO *]");
			prefix = " AND ";
		}
		// Adding optional SEO filter
		if (filterSeo && Place.CAL_TYPE.equals(settings.getReturnedType())) {
			queryBuf.append(prefix + "seoIndexed:1");
			prefix = " AND ";
		}
		if (queryBuf.length() == 0) {
			queryBuf.append("*:*");
		}
		// Setting query
		query.setQuery(queryBuf.toString());
		log.info("Solr ALL query ["
				+ ((CommonsHttpSolrServer) server).getBaseURL() + "] = "
				+ query);
		// Processing searchAll query
		return processSolrQuery(server, query, settings, window);
	}

	private SearchResponse processSolrQuery(SolrServer solrServer,
			SolrQuery query, SearchSettings settings, SearchWindow window) {
		try {
			final QueryResponse response = solrServer.query(query);
			final List<? extends SearchItem> items = response
					.getBeans(SearchItemImpl.class);

			Map<FacetCategory, List<FacetCount>> facetMap = new HashMap<FacetCategory, List<FacetCount>>();
			// Filling statistics
			fillStatistics(response, facetMap);
			for (FacetCategory c : settings.getFacettedCategories()) {
				// Retrieving the corresponding list
				List<FacetCount> counts = facetMap.get(c);
				if (counts == null) {
					counts = new ArrayList<FacetCount>();
					facetMap.put(c, counts);
				}
				// Retrieving facet field
				final FacetField field = response.getFacetField(c
						.getCategoryCode());

				// Filling information
				if (field != null && field.getValues() != null) {
					if (!c.isRange()) {
						for (Count count : field.getValues()) {
							// Building facet count
							final FacetCountImpl facetCount = new FacetCountImpl();
							final FacetImpl f = new FacetImpl();
							f.setFacetCategory(c);
							f.setFacetCode(count.getName());
							facetCount.setFacet(f);
							facetCount.setCount((int) count.getCount());
							// Adding to the list
							counts.add(facetCount);
						}
					} else {
						long min = Long.MAX_VALUE;
						long max = Long.MIN_VALUE;
						// Here we extract min / max from discrete values
						for (Count count : field.getValues()) {
							try {
								final long value = Long.parseLong(count
										.getName());
								if (value < min) {
									min = value;
								}
								if (value > max) {
									max = value;
								}
							} catch (NumberFormatException e) {
								log.error("Unable to parse range facet value for "
										+ c.getCategoryCode()
										+ " : "
										+ count.getName());
							}
						}
						// Initializing our single range information
						final FacetCountImpl facetCount = new FacetCountImpl();
						facetCount.setFacet(FacetFactory.createFacetRange(c,
								"", min, max + 1));
						counts.add(facetCount);
					}
				}

			}
			// Sorting all facets by count desc
			for (List<FacetCount> fc : facetMap.values()) {
				Collections.sort(fc, new Comparator<FacetCount>() {
					@Override
					public int compare(FacetCount o1, FacetCount o2) {
						return o2.getCount() - o1.getCount();
					}
				});
			}
			return new SearchResponseImpl(items, facetMap, settings, window,
					(int) response.getResults().getNumFound());
		} catch (SolrServerException e) {
			log.error("Error during search : " + e.getMessage(), e);
		}

		return null;
	}

	/**
	 * Fills our facet map with statistics (min / max) extracted from the SOLR
	 * query response.
	 * 
	 * @param response
	 *            the SOLRJ {@link QueryResponse}
	 * @param facetMap
	 *            the facet map to fill
	 */
	private void fillStatistics(QueryResponse response,
			Map<FacetCategory, List<FacetCount>> facetMap) {
		final Map<String, FieldStatsInfo> fieldStatsMap = response
				.getFieldStatsInfo();
		if (fieldStatsMap != null) {
			// For every statistic we generate Facet ranges
			for (final String field : fieldStatsMap.keySet()) {
				// Getting stats
				final FieldStatsInfo stats = fieldStatsMap.get(field);
				if (stats != null) {
					final FacetCategoryImpl c = new FacetCategoryImpl();
					c.setCategoryCode(field);
					// Preparing our facet count
					final FacetCountImpl facetCount = new FacetCountImpl();
					facetCount.setFacet(FacetFactory.createFacetRange(c, "",
							stats.getMin().longValue(), stats.getMax()
									.longValue() + 1));
					facetCount.setCount(0);
					// Filling our map
					List<FacetCount> counts = facetMap.get(c);
					if (counts == null) {
						counts = new ArrayList<FacetCount>();
						facetMap.put(c, counts);
					}
					counts.add(facetCount);
				}
			}
		}
	}

	public void setUserSolrUrl(String userSolrUrl) {
		this.userSolrUrl = userSolrUrl;
	}

	public void setPlacesSolrUrl(String placesSolrUrl) {
		this.placesSolrUrl = placesSolrUrl;
	}

	public void setQueryBuilder(QueryBuilder queryBuilder) {
		this.queryBuilder = queryBuilder;
	}

	public void setEventsSolrUrl(String eventsSolrUrl) {
		this.eventsSolrUrl = eventsSolrUrl;
	}

	public void setActivitiesSolrUrl(String activitiesSolrUrl) {
		this.activitiesSolrUrl = activitiesSolrUrl;
	}

	public void setSuggestSolrUrl(String suggestSolrUrl) {
		this.suggestSolrUrl = suggestSolrUrl;
	}

	public void setCitiesSolrUrl(String citiesSolrUrl) {
		this.citiesSolrUrl = citiesSolrUrl;
	}

	public void setFilterSeo(boolean filterSeo) {
		this.filterSeo = filterSeo;
	}
}
