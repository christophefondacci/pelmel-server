package com.videopolis.apis.model;

import java.util.Collection;

import com.videopolis.apis.exception.ApisException;
import com.videopolis.apis.factory.SearchRestriction;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.RequestType;
import com.videopolis.calm.model.Sorter;
import com.videopolis.smaug.common.model.SearchScope;
import com.videopolis.smaug.common.model.SuggestScope;

/**
 * Common interface for any class which can contain a collection of criteria.
 * 
 * @author Christophe Fondacci
 * 
 */
public interface CriteriaContainer {

    /**
     * Retrieves the child criteria contained within this element.
     * 
     * @return the collection of all child criteria of this element
     */
    Collection<ApisCriterion> getApisCriteria();

    /**
     * Adds the specified criterion to this criteria container.
     * 
     * @param criterion
     *            request criterion to add to this container
     */
    CriteriaContainer addCriterion(ApisCriterion criterion);

    /**
     * @return the type of the items you want to retrieve <i>with</i> the
     *         primary item type.
     */
    String getType();

    /**
     * Adds a {@link WithCriterion} to this request. It will tell the API
     * framework to fetch the specified type and to aggregate the contributed
     * items to the primary type.<br>
     * <br>
     * <b>Example :</b><br>
     * <code>ApisFactory.createRequest(Hotel.class).with(Review.class);</code>
     * 
     * @param type
     *            type of items to fetch along with the primary type
     * @return the request
     */
    CriteriaContainer with(Class<? extends CalmObject> type)
	    throws ApisException;

    /**
     * Adds a <b>named</b> {@link WithCriterion} to this request. It will tell
     * the API framework to fetch the specified type and to aggregate the
     * contributed items to the primary type under the specified name.<br>
     * <br>
     * Elements connected through this method (that is to say with an explicit
     * argument name), will <u>only be accessible</u> when requesting the
     * argument name on the {@link CalmObject}. <br>
     * <br>
     * <b>Example :</b><br>
     * <code>ApisFactory.createRequest(Hotel.class)<br>
     * &nbsp;&nbsp;&nbsp;.with(Geopoint.class,"location")<br>
     * &nbsp;&nbsp;&nbsp;.withNearest(Geopoint.class,15,0,10.0d);</code><br>
     * <br>
     * Once executed, you will then be able to access the Geopoint connection,
     * given the returned Hotel element through :<br>
     * <code>Geopoint location = myHotel.getUnique(Geopoint.class, "location");</code>
     * <br>
     * <br>
     * And the nearby geopoint could be accessed simply through :<br>
     * <code>List&lt;Geopoint&gt; nearbys = myHotel.get(Geopoint.class);</code><br>
     * 
     * @param type
     *            type of items to fetch along with the primary type
     * @param alias
     *            alias of this association
     * @return the request
     */
    CriteriaContainer with(Class<? extends CalmObject> type, String alias)
	    throws ApisException;

    /**
     * Adds a {@link WithCriterion} to this request. It will tell the API
     * framework to fetch the specified type and to aggregate the contributed
     * items to the primary type.<br>
     * Connected elements will be retrieved using the specified
     * {@link RequestType}. <br>
     * <b>Example :</b><br>
     * <code>ApisFactory.createRequest(Hotel.class).with(Review.class);</code>
     * 
     * @param type
     *            type of items to fetch along with the primary type
     * @return the request
     */
    CriteriaContainer with(Class<? extends CalmObject> type,
	    RequestType requestType) throws ApisException;

    /**
     * Adds a {@link WithCriterion} with pagination to this request. Same as the
     * {@link ApisRequest#with(Class)} method excepts that it will only fetch
     * the specified page and provide global pagination statistics. <br>
     * <br>
     * <b>Example :</b><br>
     * <code>ApisFactory.createRequest(Hotel.class).with(Review.class,10,1)</code>
     * 
     * @param type
     *            type of items to fetch along with the primary type
     * @param itemsPerPage
     *            number of items per page
     * @param pageNb
     *            number of the page of the desired items to fetch (starts at 1)
     * @return the request
     */
    CriteriaContainer with(Class<? extends CalmObject> type, int itemsPerPage,
	    int pageNb) throws ApisException;

    /**
     * <p>
     * Adds a {@link WithCriterion} with pagination and custom ordering to this
     * request. Same as the {@link ApisRequest#with(Class)} method excepts that
     * it will fetch the specified page, with a custom ordering and provide
     * global pagination statistics.
     * </p>
     * <p>
     * Example :
     * </p>
     * {@code ApisFactory.createRequest(Hotel.class).with(Review.class,10,1)}
     * 
     * @param type
     *            type of items to fetch along with the primary type
     * @param sortOrder
     *            The sort order as a {@link Sorter.Order} enum
     * @param sortCriterion
     *            The sort criterion, which is box-dependant
     * @param itemsPerPage
     *            number of items by page
     * @param pageNb
     *            page number (starts at 0)
     * @return the request
     * @throws ApisException
     */
    CriteriaContainer with(Class<? extends CalmObject> type,
	    Sorter.Order sortOrder, String sortCriterion, int itemsPerPage,
	    int pageNb) throws ApisException;

    /**
     * Adds a Nearby criteria which will fetch items of the specified type which
     * are the nearest from the main item. The criterion will have a default
     * NEARBY_BLOCK scope.
     * 
     * @param type
     *            type of nearby elements to look for
     * @param pageSize
     *            number of close-by items to retrieve on one page of results
     * @param pageOffset
     *            result page number to start from
     * @param distance
     *            maximum distance search radius. This information may be used
     *            by the search engine to reduce the cost of a geographic
     *            search.
     * @return the request
     * @deprecated use the method which explicitly specifies the search scope
     */
    @Deprecated
    CriteriaContainer withNearest(Class<? extends CalmObject> type,
	    int pageSize, int pageOffset, double distance) throws ApisException;

    /**
     * Adds a Nearby criteria which will fetch items of the specified type which
     * are the nearest from the main item.
     * 
     * @param type
     *            type of nearby elements to look for
     * @param scope
     *            the {@link SearchScope} to use which indicates the kind of
     *            search to perform
     * @param pageSize
     *            number of close-by items to retrieve on one page of results
     * @param pageOffset
     *            result page number to start from
     * @param distance
     *            maximum distance search radius. This information may be used
     *            by the search engine to reduce the cost of a geographic
     *            search.
     * @return the request
     */
    CriteriaContainer withNearest(Class<? extends CalmObject> type,
	    SearchScope scope, int pageSize, int pageOffset, double distance)
	    throws ApisException;

    /**
     * Adds a search criteria which will search for elements of the specified
     * type which are geographically localized inside the parent element. More
     * options are available when using the {@link SearchRestriction}
     * construction.
     * 
     * @param type
     *            the class of {@link CalmObject} elements to return
     * @param scope
     *            the {@link SearchScope} to use which indicates the kind of
     *            search to perform
     * @param pageSize
     *            number of elements per page
     * @param pageOffset
     *            offset of the page to return, starts at 0
     * @return the request
     * @throws ApisException
     */
    CriteriaContainer withContained(Class<? extends CalmObject> type,
	    SearchScope scope, int pageSize, int pageOffset)
	    throws ApisException;

    /**
     * Adds a custom with criteria. This method allows more complex
     * constructions of a with criteria. For example, you can create a request
     * like this :<br>
     * <code>
     * ApisFactory.createRequest(Hotel.class).uniqueKey(13l).with(<br>
     * &nbsp;&nbsp;SearchRestriction.withNearest(POI.class,10,1,10).with(Stat.class)<br>
     * );
     * </code> <br>
     * This construction will return the hotel whose id is 13 with the nearest
     * 10 points of interests around with statistical information for every of
     * these points.
     * 
     * @param withCriteria
     *            a {@link WithCriterion} built via the
     *            {@link SearchRestriction} builder
     * @return the request
     */
    CriteriaContainer with(WithCriterion withCriteria) throws ApisException;

    /**
     * A for criterion allows to query data "for" a given CAL item. It is
     * necessarily a root-level criterion which directly transmit a CAL call to
     * CAL getItemsFor method.
     * 
     * @param type
     *            type of the "for" item
     * @param id
     *            id of the "for" item
     * @param itemsPerPage
     *            number of items per page
     * @param pageNumber
     *            page number for the data to retrieve
     * @return the criteria container
     * @throws ApisException
     *             whenever the input parameters are not valid
     */
    CriteriaContainer forKey(String type, String id, int itemsPerPage,
	    int pageNumber) throws ApisException;

    /**
     * A searchFromText criterion allows to search for elements based on a text
     * fragment.
     * 
     * @param itemType
     *            the class of {@link CalmObject} to return
     * @param scope
     *            the {@link SuggestScope} that identifies the kind of elements
     *            we are searching for (destinations, pois, hotels, etc.)
     * @param text
     *            the text fragment to search
     * @return the criteria container
     */
    CriteriaContainer searchFromText(Class<? extends CalmObject> itemType,
	    SuggestScope scope, String text, int itemsCount)
	    throws ApisException;

    /**
     * Splits APIS pipeline execution by the specified number of items. This
     * will make any exceeding items to be processed in seperate calls, in
     * parallel.
     * 
     * @param itemsCount
     *            maximum number of items to process in a single CAL call
     * @return the criteria container properly configured
     */
    CriteriaContainer split(int itemsCount) throws ApisException;

    /**
     * Applies a custom adaptation of the current APIS context and aggregates
     * the result at the root level under the specified alias. Note that you
     * must use a composite request to support custom adaptation.
     * 
     * @param adapter
     *            the {@link ApisCustomAdapter} to use for transformation
     * @param alias
     *            the alias under which elements are aggregated in the response
     * @return the criteria container properly configured
     */
    CriteriaContainer customAdapt(ApisCustomAdapter adapter, String alias)
	    throws ApisException;
}
