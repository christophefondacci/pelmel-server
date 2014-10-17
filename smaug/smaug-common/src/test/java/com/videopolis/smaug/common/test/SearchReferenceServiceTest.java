package com.videopolis.smaug.common.test;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.videopolis.smaug.common.exception.SearchReferenceException;
import com.videopolis.smaug.common.model.Facet;
import com.videopolis.smaug.common.model.FacetCategory;
import com.videopolis.smaug.common.model.SearchScope;
import com.videopolis.smaug.common.model.SmaugSorter;
import com.videopolis.smaug.common.service.SearchReferenceService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:META-INF/smaug/common/testContext.xml" })
public class SearchReferenceServiceTest {

    @Autowired
    private SearchReferenceService searchReferenceService;

    @Test
    public void testFacetCategories() {

	final Collection<FacetCategory> categories = searchReferenceService
		.getFacettedCategories();
	Assert.assertNotNull("Null categories list", categories);
	final Collection<String> expectedCategories = Arrays.asList("price",
		"star", "amenity", "brand", "style", "hotelName", "beginWith", "category");
	Assert.assertEquals(
		"Unexpected number of categories: " + categories.size(),
		expectedCategories.size(), categories.size());
	for (final FacetCategory c : categories) {
	    Assert.assertTrue(
		    "Unexpected facet category: " + c.getCategoryCode(),
		    expectedCategories.contains(c.getCategoryCode()));
	    System.out.println("Found category: " + c.getCategoryCode());
	    Assert.assertNotNull("Undefined grouping strategy",
		    c.getGroupingStrategy());
	}
    }

    @Test
    public void testFacetCategoriesWithScope() {

	final Collection<FacetCategory> categories = searchReferenceService
		.getFacettedCategories(SearchScope.POI);
	Assert.assertNotNull("Null categories list", categories);
	final Collection<String> expectedCategories = Arrays.asList(
			"beginWith", "category");
	Assert.assertEquals(
		"Unexpected number of categories: " + categories.size(),
		expectedCategories.size(), categories.size());
	for (final FacetCategory c : categories) {
	    Assert.assertTrue(
		    "Unexpected facet category: " + c.getCategoryCode(),
		    expectedCategories.contains(c.getCategoryCode()));
	    System.out.println("Found category: " + c.getCategoryCode());
	    Assert.assertNotNull("Undefined grouping strategy",
		    c.getGroupingStrategy());
	}
    }

    @Test
    public void testSorters() throws SearchReferenceException {
	final List<String> sortFields = searchReferenceService
		.getAvailableSortFields();
	Assert.assertNotNull("Sort fields definition cannot be null",
		sortFields);
	final List<String> expectedSortFields = Arrays.asList("price",
		"ranking", "geo_distance", "name");
	Assert.assertEquals("Unexpected number of sort fields",
		expectedSortFields.size(), sortFields.size());
	for (final String f : sortFields) {
	    Assert.assertTrue("Unexpected sort field: " + f,
		    expectedSortFields.contains(f));
	    final List<SmaugSorter> availableSorters = searchReferenceService
		    .getAvailableSorters(f);
	    Assert.assertNotNull("Null sorters found for sort field '" + f
		    + "'", availableSorters);
	    for (final SmaugSorter s : availableSorters) {
		Assert.assertNotNull("No URL code for sorter " + f,
			s.getUrlCode());
	    }
	}
	for (final String s : Arrays.asList("i", "j", "q", "p", "r", "n", "m")) {
	    final SmaugSorter sorter = searchReferenceService
		    .getSorterFromUrlCode(s);
	    Assert.assertNotNull(
		    "No sorter could be found for URL code : " + s, sorter);
	}
    }

    @Test
    public void testFacets() throws SearchReferenceException {
	// Testing a valid facet
	final String validCategory = "amenity";
	final String facetCode = "myFacet";
	final Facet firstFacet = searchReferenceService.getFacet(validCategory,
		facetCode);
	Assert.assertNotNull("Service returned a null facet", firstFacet);
	// Testing that the facet is properly cached once requested
	final Facet secondFacet = searchReferenceService.getFacet(
		validCategory, facetCode);
	Assert.assertSame("Facets are not properly cached", firstFacet,
		secondFacet);

	// Testing an invalid facet
	final String invalidCategory = "blurp";
	try {
	    // We don't care about the result as we expect an exception
	    searchReferenceService.getFacet(invalidCategory, facetCode);
	    Assert.fail("A facet is returned for an invalid facet category");
	} catch (final SearchReferenceException e) {
	    // Normal behaviour
	}
    }
}
