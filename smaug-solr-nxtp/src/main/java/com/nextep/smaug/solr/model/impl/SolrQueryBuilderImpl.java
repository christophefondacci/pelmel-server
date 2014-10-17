package com.nextep.smaug.solr.model.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.common.params.StatsParams;

import com.nextep.events.model.Event;
import com.nextep.smaug.solr.model.QueryBuilder;
import com.nextep.users.model.User;
import com.videopolis.calm.model.Sorter;
import com.videopolis.smaug.common.model.Facet;
import com.videopolis.smaug.common.model.FacetCategory;
import com.videopolis.smaug.model.SearchSettings;
import com.videopolis.smaug.model.SearchWindow;

/**
 * This {@link QueryBuilder} implementor build a {@link SolrQuery}
 * 
 * @since 04 Jan 2011
 */
public class SolrQueryBuilderImpl implements QueryBuilder {
	private static final Log LOGGER = LogFactory
			.getLog(SolrQueryBuilderImpl.class);

	/** The field for amenity faceting. */
	private static final String AMENITY_FIELD = "amenity";
	/** The field for star faceting. */
	private static final String STAR_FIELD = "star";
	/** The field for style faceting. */
	private static final String STYLE_FIELD = "style";
	/** The field for brand faceting. */
	private static final String BRAND_FIELD = "brand";
	/** The limit label for brand faceting. */
	private static final String BRAND_FACET_LIMIT = "f.brand.facet.limit";
	/** The limit value for brand faceting. */
	private static final String BRAND_FACET_LIMIT_VALUE = "15";
	/** The sort label for brand faceting. */
	private static final String BRAND_FACET_SORT = "f.brand.facet.sort";
	/** The sort value for brand faceting. */
	private static final String BRAND_FACET_SORT_VALUE = "count";
	/** The field for neighborhood faceting. */
	private static final String NEIGHBORHOOD_FIELD = "neighborhood";
	/** The field for price faceting. */
	private static final String PRICE_FIELD = "price";
	/** The field for first letter sorting. */
	private static final String BEGIN_WITH_FIELD = "beginWith";
	/** The field for name sorting. */
	private static final String NAME_SORT_FIELD = "name_sort";
	/** The field for hotel name search. */
	private static final String HOTEL_NAME_FIELD = "hotelName";
	/** The field for category faceting. */
	private static final String CATEGORY_FIELD = "category";

	/**
	 * This represents the price grouping strategy for solr. It should be RANGE
	 * (given as input) but it's OR operator for solr.
	 */
	private static final String PRICE_GROUPING_STRATEGY = "OR";

	/**
	 * a constant for test purpose used to handle default value for search
	 * window
	 */
	private static final int DEFAULT_START = 0;

	/**
	 * a constant for test purpose used to handle default value for search
	 * window
	 */
	private static final int DEFAULT_ROWS_NUMBER = 20000;

	/**
	 * take care of dispatching the of the build logic to the appropriate
	 * depending on the search scope
	 * 
	 * @param searchSettings
	 *            the different settings to build the SolrQuery object
	 * @param searchWindow
	 *            has information about the start and number of rows returned
	 */
	@Override
	public SolrQuery buildQuery(final SearchSettings searchSettings,
			final SearchWindow searchWindow) {
		final SolrQuery solrQuery = new SolrQuery();

		handleSettings(searchSettings, searchWindow, solrQuery);
		if (User.CAL_TYPE.equals(searchSettings.getReturnedType())
				|| Event.CAL_ID.equals(searchSettings.getReturnedType())) {
			solrQuery.add(StatsParams.STATS, "true");
			addStatFields(solrQuery, searchSettings);
		}
		return solrQuery;
	}

	private void addStatFields(SolrQuery solrQuery, SearchSettings settings) {
		final Collection<FacetCategory> filteredRangeCategories = new HashSet<FacetCategory>();
		for (Facet f : settings.getFilters()) {
			if (f.getFacetCategory().isRange()) {
				filteredRangeCategories.add(f.getFacetCategory());
			}
		}
		for (FacetCategory category : settings.getFacettedCategories()) {
			// We add stat field only if not filtered for range categories
			if (category.isRange()
					&& !filteredRangeCategories.contains(category)) {
				solrQuery.add(StatsParams.STATS_FIELD,
						category.getCategoryCode());
			}
		}
	}

	/**
	 * take care of building when it about ListView search scope
	 * 
	 * @param searchSettings
	 *            the different settings to build the SolrQuery object
	 * @param searchWindow
	 *            has information about the start and number of rows returned
	 * @param solrQuery
	 *            the SolrQuery object to be modified
	 */
	protected void handleSettings(final SearchSettings searchSettings,
			final SearchWindow searchWindow, final SolrQuery solrQuery) {
		// handling faceting for continuous fields like price
		addFacets(solrQuery, searchSettings);
		// handling filters
		final Collection<Facet> filters = searchSettings.getFilters();
		addFilters(filters, solrQuery, searchSettings);
		// handling search Windows
		handleSearchWindow(searchWindow, solrQuery, DEFAULT_START,
				DEFAULT_ROWS_NUMBER);
		// handling sorters
		if (searchSettings.getSorters() != null
				&& !searchSettings.getSorters().isEmpty()) {
			addSorters(searchSettings, solrQuery);
		}
	}

	/**
	 * add the facets one by one to the SolrQuery object to be modified
	 * 
	 * @param solrQuery
	 *            the SolrQuery object to be modified
	 * @param searchSettings
	 *            The {@link SearchSettings}
	 */
	protected void addFacets(final SolrQuery solrQuery,
			final SearchSettings searchSettings) {
		final Collection<FacetCategory> facetedCategories = searchSettings
				.getFacettedCategories();

		if (facetedCategories != null && !facetedCategories.isEmpty()) {
			solrQuery.add("facet.mincount", "1");
			// if (searchSettings.getSearchScope() == SearchScope.CITY) {
			solrQuery.add("facet.limit", "-1");
			// }
			solrQuery.setFacet(true);
			final String excludedRangeTags = buildRangeExclusions(searchSettings
					.getFilters());
			// building a set of facet categories which are filtered
			final Collection<FacetCategory> filteredCategories = new HashSet<FacetCategory>();
			for (Facet f : searchSettings.getFilters()) {
				filteredCategories.add(f.getFacetCategory());
			}
			// Building facetted fields
			for (final FacetCategory facetCategory : facetedCategories) {
				// Only facetting ranges when filtered
				if (facetCategory.isRange()
						&& filteredCategories.contains(facetCategory)) {
					solrQuery.addFacetField("{!ex=" + excludedRangeTags + "}"
							+ facetCategory.getCategoryCode());
				} else if ("placeType".equals(facetCategory.getCategoryCode())) {
					solrQuery.addFacetField("{!ex="
							+ getQueryTagFor(facetCategory) + "}"
							+ facetCategory.getCategoryCode());
				} else if (!facetCategory.isRange()) {
					// Default and standard behaviour for non-range fields
					solrQuery.addFacetField(facetCategory.getCategoryCode());
				}
			}
			// TODO: Put adm faceting
		}

	}

	private String getQueryTagFor(FacetCategory category) {
		return "tag_" + category.getCategoryCode();
	}

	/**
	 * add the filters one by one to the SolrQuery object to be modified
	 * 
	 * @param filters
	 *            list of filter to apply to the SolrQuery object
	 * @param solrQuery
	 *            the SolrQuery object to be modified
	 */
	protected void addFilters(final Collection<Facet> filters,
			final SolrQuery solrQuery, final SearchSettings searchsettings) {

		if (filters != null && !filters.isEmpty()) {
			if (filters != null && !filters.isEmpty()) {
				final List<String> facetQueries = new ArrayList<String>();
				final Map<FacetCategory, List<Facet>> facetsCategoryMap = hashFacets(filters);
				for (FacetCategory c : facetsCategoryMap.keySet()) {
					final StringBuilder facetBuf = new StringBuilder();
					final String code = c.getCategoryCode();
					// We tag range filters so that we could eliminate them
					// from facet counts
					if (c.isRange() || "placeType".equals(c.getCategoryCode())) {
						facetBuf.append("{!tag=" + getQueryTagFor(c) + "}");
					}
					facetBuf.append(code + ":(");
					// Appending facets list
					String separator = "";
					final Collection<Facet> categoryFilters = facetsCategoryMap
							.get(c);
					for (Facet f : categoryFilters) {
						facetBuf.append(separator);
						facetBuf.append(f.getFacetCode());
						separator = " AND ";
					}
					facetBuf.append(")");
					facetQueries.add(facetBuf.toString());
				}
				solrQuery.setFilterQueries(facetQueries
						.toArray(new String[facetQueries.size()]));
			}
		}
	}

	private String buildRangeExclusions(Collection<Facet> facets) {
		final StringBuilder buf = new StringBuilder();
		String separator = "";
		for (Facet f : facets) {
			final FacetCategory c = f.getFacetCategory();
			if (c.isRange()) {
				buf.append(separator);
				buf.append(getQueryTagFor(c));
				separator = ",";
			}
		}
		return buf.toString();
	}

	private Map<FacetCategory, List<Facet>> hashFacets(Collection<Facet> facets) {
		final Map<FacetCategory, List<Facet>> facetsMap = new HashMap<FacetCategory, List<Facet>>();
		for (Facet f : facets) {
			List<Facet> facetList = facetsMap.get(f.getFacetCategory());
			if (facetList == null) {
				facetList = new ArrayList<Facet>();
				facetsMap.put(f.getFacetCategory(), facetList);
			}
			facetList.add(f);
		}
		return facetsMap;
	}

	/**
	 * set up the search window for the SolrQuery object
	 * 
	 * @param searchWindow
	 *            has information about the start and number of rows returned
	 * @param solrQuery
	 *            the SolrQuery object to be modified
	 * @param defaultStart
	 *            for test purpose
	 * @param defaultRowNumbers
	 *            for test purpose
	 */
	protected void handleSearchWindow(final SearchWindow searchWindow,
			final SolrQuery solrQuery, final int defaultStart,
			final int defaultRowNumbers) {
		// TODO the else bloc is just for test purpose
		if (searchWindow != null) {
			solrQuery.setRows(searchWindow.getItemsPerPage());
			solrQuery.setStart(searchWindow.getPageNumber()
					* searchWindow.getItemsPerPage());
		} else {
			solrQuery.setStart(defaultStart);
			solrQuery.setRows(defaultRowNumbers);
		}
	}

	/**
	 * take sorters one by one and add them to the SolrQuery Object
	 * 
	 * @param sorters
	 *            sorters list
	 * @param solrQuery
	 *            the SolrQuery object to be modified
	 */
	protected void addSorters(final SearchSettings settings,
			final SolrQuery solrQuery) {
		final List<? extends Sorter> sorters = settings.getSorters();
		SolrQuery.ORDER solrOrder = null;
		for (final Sorter sorter : sorters) {
			solrOrder = sorter.getOrder() == Sorter.Order.ASCENDING ? SolrQuery.ORDER.asc
					: SolrQuery.ORDER.desc;

			solrQuery.addSortField(sorter.getCriterion(), solrOrder);
		}

	}

}
