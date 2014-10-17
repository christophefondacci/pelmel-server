package com.videopolis.apis.model.impl;

import java.util.Arrays;
import java.util.Collection;

import com.videopolis.apis.exception.ApisException;
import com.videopolis.apis.exception.MalformedRequestException;
import com.videopolis.apis.factory.ApisFactory;
import com.videopolis.apis.factory.SearchRestriction;
import com.videopolis.apis.helper.ApisRegistry;
import com.videopolis.apis.helper.Assert;
import com.videopolis.apis.model.ApisCriterion;
import com.videopolis.apis.model.ApisCustomAdapter;
import com.videopolis.apis.model.ApisRequest;
import com.videopolis.apis.model.CriteriaContainer;
import com.videopolis.apis.model.SearchCriterion;
import com.videopolis.apis.model.WithCriterion;
import com.videopolis.apis.service.ApiMutableResponse;
import com.videopolis.apis.service.impl.ApiResponseImpl;
import com.videopolis.calm.exception.CalException;
import com.videopolis.calm.factory.CalmFactory;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.calm.model.RequestType;
import com.videopolis.calm.model.Sorter;
import com.videopolis.smaug.common.model.SearchMethod;
import com.videopolis.smaug.common.model.SearchScope;
import com.videopolis.smaug.common.model.SuggestScope;

/**
 * Concrete implementation of an APISRequest. This class should <b>never</b> be
 * instantiated directly. An {@link ApisRequest} could be obtained though the
 * corresponding factory : {@link ApisFactory}.
 * 
 * @author Christophe
 * 
 */
public class ApisRequestImpl implements ApisRequest {
    /** Type of items to retrieve by this request */
    private final String itemType;
    private ApisCriterion criterion;
    private boolean emptyResultsAllowed = false;

    /**
     * Default constructor
     * 
     * @param itemType
     *            type of the items that the request provides.
     */
    public ApisRequestImpl(final String itemType) {
	this.itemType = itemType;
    }

    /**
     * Internal constructor for extensions.
     */
    protected ApisRequestImpl() {
	this(null);
    }

    @Override
    public String getType() {
	return itemType;
    }

    @Override
    public Collection<ApisCriterion> getApisCriteria() {
	return Arrays.asList(criterion);
    }

    @Override
    public ApisRequest connectedTo(final Class<? extends CalmObject> type,
	    final Long id) {
	throw new UnsupportedOperationException(
		"Connected to criteria has not yet been implemented");
    }

    @Override
    public ApisRequest uniqueKey(final Long id) throws ApisException {
	assertNullCriterion();
	Assert.notNull(id, "Cannot define a unique key from a null identifier");
	return uniqueKey(String.valueOf(id));
    }

    @Override
    public ApisRequest uniqueKey(final String id) throws ApisException {
	assertNullCriterion();
	try {
	    final ItemKey uniqueKey = CalmFactory.createKey(getType(), id);
	    criterion = new UniqueKeyCriterionImpl(uniqueKey);
	    return this;
	} catch (final CalException e) {
	    throw new ApisException(
		    "Unable to create the unique key criterion (" + getType()
			    + " - " + id + ")", e);
	}
    }

    @Override
    public CriteriaContainer forKey(final String type, final String id,
	    final int itemsPerPage, final int pageNumber) throws ApisException {
	assertNullCriterion();
	criterion = SearchRestriction
		.forKey(type, id, itemsPerPage, pageNumber);
	return this;
    }

    @Override
    public ApisRequest list(final Class<? extends CalmObject> type,
	    final RequestType requestType) throws ApisException {
	assertNullCriterion();
	criterion = SearchRestriction.list(type, requestType);
	return this;
    }

    @Override
    public ApisRequest alternateKey(final String alternateKeyType,
	    final String keyValue) throws ApisException {
	assertNullCriterion();
	try {
	    final ItemKey alternateKey = CalmFactory.createKey(
		    alternateKeyType, keyValue);
	    criterion = new AlternateKeyCriterionImpl(alternateKey);
	    return this;
	} catch (final CalException e) {
	    throw new ApisException(
		    "Unable to create an alternate key criterion ("
			    + alternateKeyType + " - " + keyValue + ")", e);
	}
    }

    @Override
    public ApisRequest addCriterion(final ApisCriterion criterion) {
	// Delegating to our inner criterion
	this.criterion.addCriterion(criterion);
	return this;
    }

    private void assertNullCriterion() throws MalformedRequestException {
	if (criterion != null) {
	    throw new MalformedRequestException(
		    "Main criterion of this request has already been defined");
	}
    }

    private void assertNotNullRootCriterion() throws MalformedRequestException {
	if (criterion == null) {
	    throw new MalformedRequestException(
		    "Clause referencing an undefined root element");

	}
    }

    @Override
    public String toString() {
	return "request;" + getType();
    }

    @Override
    public CriteriaContainer with(final Class<? extends CalmObject> type)
	    throws ApisException {
	assertNotNullRootCriterion();
	criterion.with(type);
	return this;
    }

    @Override
    public CriteriaContainer with(final Class<? extends CalmObject> type,
	    final int itemsPerPage, final int pageNb) throws ApisException {
	assertNotNullRootCriterion();
	criterion.with(type, itemsPerPage, pageNb);
	return this;
    }

    @Override
    public CriteriaContainer with(final WithCriterion withCriteria)
	    throws ApisException {
	assertNotNullRootCriterion();
	criterion.with(withCriteria);
	return this;
    }

    @Override
    @Deprecated
    public CriteriaContainer withNearest(
	    final Class<? extends CalmObject> type, final int itemsPerPage,
	    final int pageNb, final double distance) throws ApisException {
	assertNotNullRootCriterion();
	criterion.withNearest(type, itemsPerPage, pageNb, distance);
	return this;
    }

    @Override
    public CriteriaContainer withNearest(
	    final Class<? extends CalmObject> type, final SearchScope scope,
	    final int pageSize, final int pageOffset, final double distance)
	    throws ApisException {
	assertNotNullRootCriterion();
	criterion.withNearest(type, scope, pageSize, pageOffset, distance);
	return this;
    }

    @Override
    public CriteriaContainer with(final Class<? extends CalmObject> type,
	    final RequestType requestType) throws ApisException {
	assertNotNullRootCriterion();
	criterion.with(type, requestType);
	return this;
    }

    @Override
    public CriteriaContainer with(final Class<? extends CalmObject> type,
	    final String alias) throws ApisException {
	assertNotNullRootCriterion();
	criterion.with(type, alias);
	return this;
    }

    @Override
    public CriteriaContainer with(final Class<? extends CalmObject> type,
	    final Sorter.Order sortOrder, final String sortCriterion,
	    final int itemsPerPage, final int pageNb) throws ApisException {
	assertNotNullRootCriterion();
	criterion.with(type, sortOrder, sortCriterion, itemsPerPage, pageNb);
	return this;
    }

    @Override
    public CriteriaContainer withContained(
	    final Class<? extends CalmObject> type, final SearchScope scope,
	    final int pageSize, final int pageOffset) throws ApisException {
	assertNotNullRootCriterion();
	criterion.withContained(type, scope, pageSize, pageOffset);
	return this;
    }

    @Override
    public ApisCriterion getRootCriterion() {
	return criterion;
    }

    /**
     * Defines the root criterion of this APIS request
     * 
     * @param rootCriterion
     *            root {@link ApisCriterion} of this request
     */
    protected void setRootCriterion(final ApisCriterion rootCriterion) {
	criterion = rootCriterion;
    }

    @Override
    public CriteriaContainer searchFromText(
	    final Class<? extends CalmObject> itemType,
	    final SuggestScope scopes, final String text, final int itemsCount)
	    throws ApisException {
	assertNotNullRootCriterion();
	criterion.addCriterion(SearchRestriction.searchFromText(itemType,
		scopes, text, itemsCount));
	return this;
    }

    @Override
    public Class<? extends ApiMutableResponse> getResponseClass() {
	return ApiResponseImpl.class;
    }

    @Override
    public ApisRequest searchAll(final Class<? extends CalmObject> type,
	    final SearchScope scope, final int pageSize, final int pageOffset)
	    throws ApisException {
	return searchAll(type, scope, SearchMethod.CITIES_WITHOUT_SHADOW,
		pageSize, pageOffset);
    }

    @Override
    public ApisRequest searchAll(
	    final java.lang.Class<? extends CalmObject> type,
	    final SearchScope scope, final SearchMethod method,
	    final int pageSize, final int pageOffset) throws ApisException {
	assertNullCriterion();
	criterion = new SearchAllCriterionImpl(
		ApisRegistry.getTypeFromModel(type), scope, method);
	((SearchCriterion) criterion).paginatedBy(pageSize, pageOffset);
	return this;
    }

    @Override
    public CriteriaContainer split(final int itemsCount) throws ApisException {
	assertNotNullRootCriterion();
	getRootCriterion().split(itemsCount);
	return this;
    }

    @Override
    public ApisRequest allowEmptyResults() {
	this.emptyResultsAllowed = true;
	return this;
    }

    @Override
    public boolean isEmptyResultsAllowed() {
	return emptyResultsAllowed;
    }

    @Override
    public CriteriaContainer customAdapt(ApisCustomAdapter adapter, String alias)
	    throws ApisException {
	assertNotNullRootCriterion();
	final ApisCriterion crit = getRootCriterion();
	crit.addCriterion(SearchRestriction.customAdapt(adapter, alias));
	return this;
    }
}
