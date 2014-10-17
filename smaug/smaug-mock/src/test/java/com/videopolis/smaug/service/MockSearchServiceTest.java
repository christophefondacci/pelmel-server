package com.videopolis.smaug.service;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.Properties;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;

import com.tripvisit.dao.impl.geodata.GeodataDAOImpl;
import com.tripvisit.hibernate.SessionManager;
import com.tripvisit.hibernate.ThreadLocalSessionManager;
import com.tripvisit.hibernate.configuration.AggregatorConfigurator;
import com.tripvisit.hibernate.configuration.DefaultConfigurator;
import com.tripvisit.hibernate.configuration.PropertiesConfigurator;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.calm.model.Localized;
import com.videopolis.smaug.factory.SearchFactory;
import com.videopolis.smaug.model.MockTypes;
import com.videopolis.smaug.model.SearchItem;
import com.videopolis.smaug.model.SearchResponse;
import com.videopolis.smaug.service.impl.MockSearchServiceImpl;

public class MockSearchServiceTest extends TestCase {

    private static final Log LOGGER = LogFactory
	    .getLog(MockSearchServiceTest.class);

    private static final int DEFAULT_PAGE_SIZE = 10;

    private static final int DEFAULT_PAGE_NUMBER = 0;

    private static final Localized POINT = new Localized() {
	@Override
	public double getLongitude() {
	    return 2.333d;
	}

	@Override
	public double getLatitude() {
	    return 48.885d;
	}
    };
    private static final double RADIUS = 10d;

    private MockSearchServiceImpl searchService;

    @Override
    protected void setUp() throws Exception {

	super.setUp();

	final Properties properties = new Properties();
	final InputStream inputStream = getClass().getResourceAsStream(
		"/hibernate.properties");
	properties.load(inputStream);
	inputStream.close();
	SessionManager.setSessionManager(new ThreadLocalSessionManager(
		new AggregatorConfigurator().add(new DefaultConfigurator())
			.add(new PropertiesConfigurator(properties))));

	searchService = new MockSearchServiceImpl();
	searchService.setSolrServer(new CommonsHttpSolrServer(
		"http://carlos:8080/solr/"));
	searchService.setGeodataDao(new GeodataDAOImpl());
    }

    @Override
    protected void tearDown() throws Exception {

	SessionManager.closeAll();

	super.tearDown();
    }

    public void testSearchHotels() {
	doTestSearchNear(MockTypes.HOTEL);
    }

    public void testSearchPois() {
	doTestSearchNear(MockTypes.POI);
    }

    public void testSearchPoisPagination() {
	// Get the 2 first results
	final SearchResponse response = doSearchNear(MockTypes.POI, 2, 0);
	assertEquals("Incorrect result count", 2, response.getItems().size());

	// Extract 1st key
	final ItemKey firstKey = response.getItems().get(0).getKey();
	assertNotNull("Result item key is null", firstKey);

	// Extract 2nd key
	final ItemKey secondKey = response.getItems().get(1).getKey();
	assertNotNull("Result item key is null", secondKey);

	// Make sure the 1st key is still the same
	final SearchResponse firstResponse = doSearchNear(MockTypes.POI, 1, 0);
	assertEquals("Incorrect result count", 1, firstResponse.getItems()
		.size());
	assertEquals("Invalid result key", firstKey, firstResponse.getItems()
		.get(0).getKey());

	// Make sure the pagination returns the expected data when page > 0
	final SearchResponse secondResponse = doSearchNear(MockTypes.POI, 1, 1);
	assertEquals("Incorrect result count", 1, secondResponse.getItems()
		.size());
	assertEquals("Invalid result key", secondKey, secondResponse.getItems()
		.get(0).getKey());
    }

    private void doTestSearchNear(final String type) {
	final SearchResponse response = doSearchNear(type, DEFAULT_PAGE_SIZE,
		DEFAULT_PAGE_NUMBER);

	LOGGER.info("Got page " + response.getSearchWindow().getPageNumber()
		+ " of " + response.getSearchWindow().getItemsPerPage()
		+ " items, in a total of "
		+ response.getSearchWindow().getItemCount());
	assertFalse("No results found", response.getItems().isEmpty());
	assertEquals("Invalid result count", DEFAULT_PAGE_SIZE, response
		.getItems().size());
	assertTrue(
		"The total number of result is less than the returned result count",
		response.getSearchWindow().getItemCount() >= response
			.getItems().size());

	double previousDistance = 0;
	for (final SearchItem item : response.getItems()) {
	    final double currentDistance = item.getInfo(null).doubleValue();
	    LOGGER.info(item.getKey() + ", " + currentDistance);
	    assertEquals(type, item.getKey().getType());
	    assertFalse("Previous result distance (" + previousDistance
		    + ") is greater than current result (" + currentDistance
		    + ")", previousDistance > currentDistance);
	    previousDistance = currentDistance;
	}
    }

    /**
     * Just performs an actual search
     * 
     * @param type
     *            Expected item type
     * @param pageSize
     *            Expected page size
     * @param pageNumber
     *            Expected page number
     * @return Results
     */
    @SuppressWarnings("unchecked")
    private SearchResponse doSearchNear(final String type, int pageSize,
	    int pageNumber) {
	return searchService.searchNear(POINT, RADIUS, SearchFactory
		.createSearchSettings(Arrays.asList(type),
			Collections.EMPTY_LIST, Collections.EMPTY_LIST,
			Collections.EMPTY_LIST), SearchFactory
		.createSearchWindow(pageSize, pageNumber));
    }
}
