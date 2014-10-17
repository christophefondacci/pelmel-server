package com.videopolis.smaug.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.response.QueryResponse;

import com.tripvisit.dao.geodata.GeodataDAO;
import com.tripvisit.model.bean.geodata.Geodata;
import com.videopolis.calm.model.Localized;
import com.videopolis.smaug.comparator.impl.GeodataComparatorImpl;
import com.videopolis.smaug.exception.SearchException;
import com.videopolis.smaug.factory.MockFactory;
import com.videopolis.smaug.model.Facet;
import com.videopolis.smaug.model.MockTypes;
import com.videopolis.smaug.model.SearchItem;
import com.videopolis.smaug.model.SearchResponse;
import com.videopolis.smaug.model.SearchSettings;
import com.videopolis.smaug.model.SearchWindow;
import com.videopolis.smaug.model.impl.MockGeodataSearchItemImpl;
import com.videopolis.smaug.model.impl.MockHotelSearchItemImpl;
import com.videopolis.smaug.service.SearchService;

/**
 * <p>
 * Mock implementation of {@link SearchService}
 * </p>
 * <p>
 * This implementation only supports search of nearby hotels using solr
 * </p>
 * 
 * @author julien
 * 
 */
public class MockSearchServiceImpl implements SearchService {

    /** Type of query (for solr) */
    private static final String GEO_QUERY_TYPE = "geo";

    /** Solr's latitude parameter */
    private static final String LATITUDE_PARAMETER = "lat";

    /** Solr's longitude parameter */
    private static final String LONGITUDE_PARAMETER = "long";

    /** Solr's radius parameter */
    private static final String RADIUS_PARAMETER = "radius";

    /** Hotel ID field in the solr response */
    private static final String ID_FIELD = "id";

    /** Distance field in the solr response */
    private static final String DISTANCE_FIELD = "geo_distance";

    /** Locale for use with geodata */
    private static final String GEODATA_LOCALE = "en";

    /** Solr server to query */
    private SolrServer solrServer;

    /** The GEO DAO */
    private GeodataDAO geodataDao;

    public void setSolrServer(SolrServer solrServer) {
	this.solrServer = solrServer;
    }

    public void setGeodataDao(GeodataDAO geodataDao) {
	this.geodataDao = geodataDao;
    }

    @Override
    public SearchResponse searchNear(Localized point, double radius,
	    SearchSettings settings, SearchWindow window) {

	// Get the type
	if (settings.getReturnedTypes().contains(MockTypes.HOTEL)) {
	    return searchNearbyHotels(point, radius, settings, window);
	}

	if (settings.getReturnedTypes().contains(MockTypes.POI)) {
	    return searchNearbyPois(point, radius, settings, window);
	}

	// No luck
	throw new SearchException("Unsupported returned type: "
		+ settings.getReturnedTypes());
    }

    /**
     * Performs a nearby search for hotels
     * 
     * @param point
     *            Point
     * @param radius
     *            Radius
     * @param settings
     *            Settings
     * @param window
     *            Window
     * @return Results
     * @throws SearchException
     *             If the solr call fails
     */
    @SuppressWarnings("unchecked")
    private SearchResponse searchNearbyHotels(final Localized point,
	    final double radius, final SearchSettings settings,
	    final SearchWindow window) {

	try {

	    // Build the query
	    final SolrQuery query = new SolrQuery();
	    query.setQuery("*:*");
	    query.setQueryType(GEO_QUERY_TYPE);
	    query.setParam(LATITUDE_PARAMETER, String.valueOf(point
		    .getLatitude()));
	    query.setParam(LONGITUDE_PARAMETER, String.valueOf(point
		    .getLongitude()));
	    query.setParam(RADIUS_PARAMETER, String.valueOf(radius));
	    query.setFields(ID_FIELD, DISTANCE_FIELD);
	    query.setStart(window.getPageNumber() * window.getItemsPerPage());
	    query.setRows(window.getItemsPerPage());
	    query.addSortField(DISTANCE_FIELD, ORDER.asc);

	    // Do it
	    final QueryResponse response = solrServer.query(query);
	    final List<? extends SearchItem> beans = response
		    .getBeans(MockHotelSearchItemImpl.class);
	    return MockFactory.createSearchResponse(MockFactory
		    .createSearchWindowResponse(window, (int) response
			    .getResults().getNumFound()), settings, beans,
		    (List<? extends Facet>) Collections.EMPTY_LIST);

	} catch (SolrServerException e) {
	    throw new SearchException(e.getMessage(), e);
	}
    }

    /**
     * 
     * @param point
     * @param radius
     * @param settings
     * @param window
     * @return
     */
    @SuppressWarnings("unchecked")
    private SearchResponse searchNearbyPois(final Localized point,
	    final double radius, final SearchSettings settings,
	    final SearchWindow window) {

	// Query GeoData
	final List<Geodata> nearbies = geodataDao.getGeodataNearbyHotel(point
		.getLatitude(), point.getLongitude(), GEODATA_LOCALE);
	final Set<Geodata> sortedNearbies = new TreeSet<Geodata>(
		new GeodataComparatorImpl());
	sortedNearbies.addAll(nearbies);

	// Build the response
	final List<SearchItem> items = new ArrayList<SearchItem>(window
		.getItemsPerPage());

	// Compute pagination indices
	final int startIndex = window.getItemsPerPage()
		* window.getPageNumber();
	final int endIndex = startIndex + window.getItemsPerPage();
	
	// Skip the N first results
	final Iterator<Geodata> iterator = sortedNearbies.iterator();
	for (int i = 0; i < startIndex; i++) {
	    iterator.next();
	}

	for (int i = startIndex; i < endIndex && i < nearbies.size(); i++) {
	    items.add(new MockGeodataSearchItemImpl(iterator.next()));
	}

	return MockFactory.createSearchResponse(MockFactory
		.createSearchWindowResponse(window, nearbies.size()), settings,
		items, (List<? extends Facet>) Collections.EMPTY_LIST);
    }
}
