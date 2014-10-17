package com.videopolis.apis.test;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.videopolis.apis.cals.impl.ProxiedItemsResponseImpl;
import com.videopolis.apis.exception.ApisException;
import com.videopolis.apis.factory.ApisFactory;
import com.videopolis.apis.factory.SearchRestriction;
import com.videopolis.apis.model.ApisCriterion;
import com.videopolis.apis.model.ApisRequest;
import com.videopolis.apis.model.impl.ModelCModelAAdapter;
import com.videopolis.apis.service.ApiResponse;
import com.videopolis.apis.service.ApiService;
import com.videopolis.calm.exception.CalException;
import com.videopolis.calm.factory.CalmFactory;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ITestModelB;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.calm.model.Sorter;
import com.videopolis.calm.model.impl.TestModelA;
import com.videopolis.calm.model.impl.TestModelB;
import com.videopolis.calm.model.impl.TestModelC;
import com.videopolis.cals.factory.ContextFactory;
import com.videopolis.cals.service.impl.TestRequestType;
import com.videopolis.cals.service.impl.TestServiceB;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:META-INF/apis/test/testContext.xml" })
public class ApisServiceTest {

    @Autowired
    private ApiService apiService;

    public void setApiService(final ApiService apiService) {
	this.apiService = apiService;
    }

    @Test
    public void testSimpleRequest() throws Exception {
	final ApisRequest request = ApisFactory.createRequest(TestModelA.class)
		.uniqueKey(1l);
	Assert.assertNotNull(request);
	final ApiResponse response = apiService.execute(request,
		ContextFactory.createContext(new Locale("fr")));
	Assert.assertNotNull(response);
	Assert.assertTrue("Elements should not be empty", response
		.getElements().size() > 0);
	Assert.assertTrue("Unique key criteria must return a unique result",
		response.getElements().size() == 1);
    }

    @Test
    public void testAggregatedRequest() throws Exception {
	final ApisRequest request = (ApisRequest) ApisFactory
		.createRequest(TestModelA.class).uniqueKey(1l)
		.with(ITestModelB.class);
	Assert.assertNotNull(request);
	final ApiResponse response = apiService.execute(request,
		ContextFactory.createContext(new Locale("fr")));
	Assert.assertNotNull(response);
	Assert.assertTrue("Elements should not be empty", response
		.getElements().size() > 0);
	Assert.assertTrue("Unique key criteria must return a unique result",
		response.getElements().size() == 1);

	final CalmObject modelA = response.getElements().iterator().next();
	Assert.assertNotNull(modelA);
	Assert.assertTrue("Returned model differs from the requested one",
		modelA instanceof TestModelA);
	final Collection<? extends ITestModelB> modelsB = modelA
		.get(ITestModelB.class);
	Assert.assertNotNull(modelsB);
	Assert.assertTrue("Elements should not be empty", modelsB.size() > 0);
	// for (final ITestModelB modelB : modelsB) {
	// Assert.assertSame(modelA, modelB.getParent(TestModelA.class));
	// }
    }

    @Test
    public void testAggregatedPaginatedRequest() throws Exception {
	final ApisRequest request = (ApisRequest) ApisFactory
		.createRequest(TestModelA.class).uniqueKey(1l)
		.with(ITestModelB.class, 10, 1)
		.with(ITestModelB.class, "alias");
	Assert.assertNotNull(request);
	final ApiResponse response = apiService.execute(request,
		ContextFactory.createContext(new Locale("fr")));
	Assert.assertNotNull(response);
	Assert.assertTrue("Elements should not be empty", response
		.getElements().size() > 0);
	Assert.assertTrue("Unique key criteria must return a unique result",
		response.getElements().size() == 1);

	final TestModelA modelA = (TestModelA) response.getElements()
		.iterator().next();
	Assert.assertNotNull(modelA);
	Assert.assertTrue("Returned model differs from requested",
		modelA instanceof TestModelA);
	final Collection<? extends ITestModelB> modelsB = modelA
		.get(ITestModelB.class);
	Assert.assertNotNull(modelsB);
	Assert.assertTrue("Elements should noITestModelB", modelsB.size() > 0);
	// for (final ITestModelB modelB : modelsB) {
	// Assert.assertSame(modelA, modelB.getParent(TestModelA.class));
	// }
	Assert.assertEquals(10, modelsB.size());
	final Collection<? extends ITestModelB> aliasedModelsB = modelA.get(
		ITestModelB.class, "alias");
	Assert.assertNotNull(aliasedModelsB);
	// for (final ITestModelB modelB : aliasedModelsB) {
	// Assert.assertSame(modelA, modelB.getParent(TestModelA.class));
	// }
	Assert.assertFalse("Aliased elements should not be empty",
		aliasedModelsB.isEmpty());
    }

    @Test
    public void testAggregatedRequestWithRequestType() throws ApisException {
	final ApisRequest request = (ApisRequest) ApisFactory
		.createRequest(TestModelA.class).uniqueKey(1l)
		.with(TestModelC.class, TestRequestType.CODE_ONLY);
	Assert.assertNotNull(request);
	final ApiResponse response = apiService.execute(request,
		ContextFactory.createContext(new Locale("fr")));
	Assert.assertNotNull(response);
	Assert.assertTrue("Elements should not be empty", response
		.getElements().size() > 0);
	Assert.assertTrue("Unique key criteria must return a unique result",
		response.getElements().size() == 1);

	final CalmObject modelA = response.getElements().iterator().next();
	Assert.assertNotNull(modelA);
	Assert.assertTrue("Returned model differs from requested",
		modelA instanceof TestModelA);
	final Collection<? extends TestModelC> modelsC = modelA
		.get(TestModelC.class);
	Assert.assertNotNull(modelsC);
	Assert.assertTrue("Elements should not be empty", modelsC.size() > 0);
	for (final TestModelC modelC : modelsC) {
	    // Assert.assertSame(modelA, modelC.getParent(TestModelA.class));
	    Assert.assertNull("Request type has not been understood",
		    modelC.getLocale());
	}
    }

    @Test
    public void testCustomizedWith() throws ApisException {
	final ApisRequest request = (ApisRequest) ApisFactory
		.createRequest(TestModelA.class)
		.uniqueKey(1l)
		.with(ITestModelB.class, Sorter.Order.DESCENDING,
			TestServiceB.DEFAULT_SORT, 10, 1);
	Assert.assertNotNull(request);
	final ApiResponse response = apiService.execute(request,
		ContextFactory.createContext(new Locale("fr")));
	Assert.assertNotNull(response);
	Assert.assertTrue("Elements should not be empty", response
		.getElements().size() > 0);
	Assert.assertTrue("Unique key criteria must return a unique result",
		response.getElements().size() == 1);
	final Collection<? extends ITestModelB> modelsB = response
		.getUniqueElement().get(ITestModelB.class);
	Assert.assertNotNull(modelsB);
	Assert.assertTrue("Elements should not be empty", modelsB.size() > 0);
	Assert.assertEquals(10, modelsB.size());

	// Check the elements order
	long previous = Long.MAX_VALUE;
	for (final ITestModelB modelB : modelsB) {
	    Assert.assertTrue("Elements should be in descending order", modelB
		    .getKey().getNumericId() < previous);
	    previous = modelB.getKey().getNumericId();
	}
    }

    @Test
    public void testAdapter() throws ApisException {
	ApisRequest request = (ApisRequest) ApisFactory
		.createRequest(TestModelC.class)
		.uniqueKey(1l)
		.addCriterion(
			(ApisCriterion) SearchRestriction.adapt(
				new ModelCModelAAdapter()).with(
				ITestModelB.class));
	Assert.assertNotNull(request);
	final ApiResponse response = apiService.execute(request,
		ContextFactory.createContext(new Locale("fr")));
	Assert.assertNotNull(response);
	Assert.assertTrue("Elements should not be empty", response
		.getElements().size() > 0);
	Assert.assertTrue("Unique key criteria must return a unique result",
		response.getElements().size() == 1);
	TestModelC modelC = (TestModelC) response.getUniqueElement();
	final TestModelA modelA = modelC.getModelA();
	Assert.assertNotNull("Model A should be provided by modelC", modelA);
	List<? extends ITestModelB> modelBs = modelA.get(ITestModelB.class);
	Assert.assertNotNull(
		"Model B has not been properly aggregated on the adapted model A",
		modelBs);
	Assert.assertFalse(
		"Aggregated Model B should not be empty on the adapted model A",
		modelBs.isEmpty());
    }

    @Test
    public void testProxy() throws ApisException, CalException {
	final ItemKey key = CalmFactory.parseKey("BBBB1");
	ProxiedItemsResponseImpl response = new ProxiedItemsResponseImpl(
		Arrays.asList(key));
	final CalmObject proxy = response.getItems().iterator().next();
	Assert.assertTrue("Invalid proxied object",
		proxy instanceof ITestModelB);
	// Simulating first sub element added first
	TestModelA aggObj1 = new TestModelA(CalmFactory.parseKey("AAAA1"));
	proxy.add(aggObj1);
	CalmObject testedProxy = response.getItems().iterator().next();
	Assert.assertTrue("Invalid proxied object",
		testedProxy instanceof ITestModelB);
	// Simulating second sub element added to proxy, the object itself
	ITestModelB innerObj = new TestModelB(CalmFactory.parseKey("BBBB1"));
	innerObj.setName("myName");
	proxy.add(innerObj);
	// Simulating listener call to getItems()
	response.getItems();
	// Simulating third object added to proxy after proxied object is here
	TestModelC aggObj2 = new TestModelC(CalmFactory.parseKey("CCCC1"));
	proxy.add(aggObj2);

	testedProxy = response.getItems().iterator().next();
	Assert.assertEquals("Proxied object not switched", innerObj.getName(),
		((ITestModelB) testedProxy).getName());
	Collection<? extends CalmObject> connections = testedProxy
		.getConnectedObjects();
	Assert.assertTrue(
		"Real element has not been aggregated with connections added BEFORE proxy switch",
		connections.contains(aggObj1));
	Assert.assertTrue(
		"Real element has not been aggregated with connections added AFTER proxy switch",
		connections.contains(aggObj2));
    }

    // @Test
    // void testMultiSearch() throws ApisException {
    // SearchService searchService = mock(SearchService.class);
    //
    // when(
    // searchService.searchIn(any(ItemKey.class),
    // any(SearchSettings.class), any(SearchWindow.class)))
    // .thenReturn(null);
    // }
}
