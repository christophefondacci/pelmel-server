package com.videopolis.cals.service;

import java.util.Collection;
import java.util.List;

import com.videopolis.calm.exception.CalException;
import com.videopolis.calm.factory.RequestTypeFactory;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.calm.model.RequestType;
import com.videopolis.cals.model.CalContext;
import com.videopolis.cals.model.ItemsResponse;
import com.videopolis.cals.model.MultiKeyItemsResponse;
import com.videopolis.cals.model.PaginatedItemsResponse;
import com.videopolis.cals.model.RequestSettings;
import com.videopolis.cals.service.base.AbstractCalService;

/**
 * This is the common abstraction service layer shared by all components willing
 * to provide content to the aggregated proxy integration layer (api).<br>
 * All components provide a single type of items. Items can be retrieved through
 * their unique ID or "contribute" to another item passed by an {@link ItemKey}. <br>
 * <br>
 * This service should not be implemented directly and implementors should
 * rather extend the abstract base class {@link AbstractCalService} which will
 * provide the root features and will 'absorb' any further evolution of the
 * framework.
 * 
 * @author Christophe Fondacci
 * 
 */
public interface CalService {

	/**
	 * Retrieves items that this service provide through their unique
	 * identifier. All items returned by this method must validate the following
	 * condition :<br>
	 * <code>
	 * item.getKey().getType().equals(thisService.getProvidedType());
	 * </code><br>
	 * <br>
	 * <b>Important:</b> The ordering of requested {@link ItemKey} list
	 * <u>must</u> be preserved in the response items. In other words, a call to
	 * {@link ItemsResponse#getItems()} must return the items in the same order
	 * than the list of {@link ItemKey} asked to the service. Services which may
	 * corrupt the original ordering could call the helper method
	 * reorderCalmObjects which will regenerate a proper ordering.
	 * 
	 * @param ids
	 *            unique id of elements to retrieve
	 * @param context
	 *            Invocation context, implementors may need to adapt the
	 *            returned results according to this context information
	 * @return an {@link ItemsResponse} containing a collection of items of the
	 *         type this service provides. If there is no item corresponding to
	 *         any or every input {@link ItemKey}, this service should return a
	 *         valid {@link ItemsResponse} with respectively partial or empty
	 *         result.
	 * @throws CalException
	 *             whenever a technical error occurred. This exception should
	 *             <b>NOT</b> be raised when some {@link ItemKey} are not found.
	 *             This exception should wrap any {@link RuntimeException}
	 */
	ItemsResponse getItems(List<ItemKey> ids, CalContext context)
			throws CalException;

	/**
	 * Retrieves items that this service provides through their unique id. This
	 * method will return lightweight objects as it will only fill the objects
	 * with values depending on the <code>requestType</code> argument.<br>
	 * <br>
	 * The request type is an implementation of a {@link RequestType} interface.
	 * Standard request type are defined directly in the interface, such as :<br>
	 * <code>RequestType.LAT_LONG</code> or <code>RequestType.ID_ONLY</code><br>
	 * CAL implementation may provide new request types implementing this
	 * interface that are specific to a given model / service. In this case,
	 * this is the implementor's responsibility to expose the
	 * {@link RequestType} (usually along with the CAL model), and the APIS
	 * caller's responsibility to query this RequestType accordingly. <br>
	 * <b>Every implementor</b> must respect the contract of providing <b>at
	 * least</b> the requested fields.<br>
	 * <b>Every caller</b> can assert that the requested fields are available
	 * and should <u>never</u> assume any other data will be returned.<br>
	 * <br>
	 * <b>Example</b> for a fake HotelService providing hotels :<br>
	 * <code>getItems(Arrays.asList(13),context,RequestType.LAT_LONG);</code> <br>
	 * <br>
	 * will return a Hotel bean whose unique id is 13 for the FR locale.
	 * Returned hotels will contain values for <i>at least</i> latitude and
	 * longitude. This is not an error if the requested bean contains more data,
	 * simply the caller must not assume so as it may change over time without
	 * notice.
	 * 
	 * @param ids
	 *            list of unique identifiers of elements to retrieve
	 * @param context
	 *            a {@link CalContext} invocation context, implementors may need
	 *            to adapt the returned results according to this context
	 *            information
	 * @param requestType
	 *            the type of request to perform. Every CAL provider may provide
	 *            some request type through a static class. This allows to build
	 *            lightweight services which will return partial information
	 * @return an {@link ItemsResponse} containing a collection of items of the
	 *         type this service provides. If there is no item corresponding to
	 *         any or every input {@link ItemKey}, this service should return a
	 *         valid {@link ItemsResponse} with respectively partial or empty
	 *         result.
	 * @throws CalException
	 *             whenever a technical error occurred. This exception should
	 *             <b>NOT</b> be raised when some {@link ItemKey} are not found.
	 *             This exception should wrap any {@link RuntimeException}
	 * @see RequestType
	 * @see RequestType#ID_ONLY
	 * @see RequestType#LAT_LONG
	 */
	ItemsResponse getItems(List<ItemKey> ids, CalContext context,
			RequestType requestType) throws CalException;

	/**
	 * Retrieves items that this service provides which <u>contribute</u> to the
	 * specified external items. Any item returned by this method must return
	 * true to the following expression :<br>
	 * <code>
	 * item.getKey().getType().equals(thisService.getProvidedType());
	 * </code><br>
	 * This method will be implemented automatically by the
	 * {@link AbstractCalService}. However, implementors may want to override
	 * this method to provide performance optimizations.
	 * 
	 * @param itemKeys
	 *            a list of unique key of the items to contribute to
	 * @param context
	 *            invocation context information such as locale, ...
	 * @return all item contributions through a {@link MultiKeyItemsResponse}
	 */
	MultiKeyItemsResponse getItemsFor(List<ItemKey> itemKeys, CalContext context)
			throws CalException;

	/**
	 * Retrieves items that this service provides which <u>contribute</u> to the
	 * specified external items. Any item returned by this method must return
	 * true to the following expression :<br>
	 * <code>
	 * item.getKey().getType().equals(thisService.getProvidedType());
	 * </code><br>
	 * This method will be implemented automatically by the
	 * {@link AbstractCalService}. However, implementors may want to override
	 * this method to provide performance optimizations. <br>
	 * The request type is an implementation of a {@link RequestType} interface.
	 * Standard request type are defined directly in the interface, such as :<br>
	 * <code>RequestType.LAT_LONG</code> or <code>RequestType.ID_ONLY</code><br>
	 * CAL implementation may provide new request types implementing this
	 * interface that are specific to a given model / service. In this case,
	 * this is the implementor's responsibility to expose the
	 * {@link RequestType} (usually along with the CAL model), and the APIS
	 * caller's responsibility to query this RequestType accordingly. <br>
	 * <b>Every implementor</b> must respect the contract of providing <b>at
	 * least</b> the requested fields.<br>
	 * <b>Every caller</b> can assert that the requested fields are available
	 * and should <u>never</u> assume any other data will be returned.<br>
	 * <br>
	 * 
	 * @param itemKeys
	 *            a list of unique key of the items to contribute to
	 * @param context
	 *            invocation context information such as locale, ...
	 * @return all item contributions through a {@link MultiKeyItemsResponse}
	 */
	MultiKeyItemsResponse getItemsFor(List<ItemKey> itemKeys,
			CalContext context, RequestType requestType) throws CalException;

	/**
	 * Retrieves items provided by this service which <u>contribute</u> to the
	 * specified external item, with pagination. The pagination is defined by
	 * the number of elements a page contains and the page number that the
	 * client wants to fetch. An empty collection should be returned by this
	 * method when the specified window does not contain any item.
	 * 
	 * 
	 * @param itemKey
	 *            unique key of the item to contribute to
	 * @param context
	 *            invocation contextual information
	 * @param resultsPerPage
	 *            number of result per page for windowing
	 * @param pageNumber
	 *            page number to get
	 * @return a collection of {@link CalmObject}
	 */
	PaginatedItemsResponse getPaginatedItemsFor(ItemKey itemKey,
			CalContext context, int resultsPerPage, int pageNumber)
			throws CalException;

	/**
	 * Same behaviour as
	 * {@link CalService#getPaginatedItemsFor(ItemKey, CalContext, int, int)}
	 * except that it provides a {@link RequestType} allowing to alter standard
	 * behaviour
	 * 
	 * @param itemKey
	 *            unique key of the item to contribute to
	 * @param context
	 *            invocation contextual information
	 * @param resultsPerPage
	 *            number of result per page for windowing
	 * @param pageNumber
	 *            page number to get
	 * @param requestType
	 *            the {@link RequestType}
	 * @return a collection of {@link CalmObject}
	 * @throws CalException
	 */
	PaginatedItemsResponse getPaginatedItemsFor(ItemKey itemKey,
			CalContext context, int resultsPerPage, int pageNumber,
			RequestType requestType) throws CalException;

	/**
	 * Retrieves a <u>range</u> of items provided by this service which
	 * <u>contribute</u> to the specified external items <u>list</u>.
	 * Contributed items is defined by the number of elements a page contains
	 * and the page number that the client wants to fetch, just like regular
	 * pagination. The response is a multi key response as we will never expect
	 * implementors to return pagination information for all requested items.<br>
	 * Instead, this method is a mean for reducing the amount of items returned.
	 * It is typically used when we want to access a single (or few) information
	 * for a list of elements : descriptions for a list of hotels, latest review
	 * for a given hotel, etc.
	 * 
	 * @param itemKeys
	 *            the list of {@link ItemKey} to retrieve contributing items for
	 * @param context
	 *            the {@link CalContext}
	 * @param resultsPerPage
	 *            max number of items to provide by {@link ItemKey}
	 * @param pageNumber
	 *            an page offset to start from
	 * @return a {@link MultiKeyItemsResponse} with no pagination info
	 * @throws CalException
	 *             whenever we experienced problems when retrieving information
	 */
	MultiKeyItemsResponse getItemsRangeFor(List<ItemKey> itemKeys,
			CalContext context, int resultsPerPage, int pageNumber)
			throws CalException;

	/**
	 * Retrieves items provided by this service which <u>contribute</u> to the
	 * specified external item, with custom settings. {@link RequestSettings}
	 * bean allow callers and implementors to implement custom filters, sort,
	 * pagination, etc. This method is designed for future use as
	 * {@link RequestSettings} could be highly overridden to fit upcoming needs.
	 * 
	 * @param itemKey
	 *            unique key of the item to contribute to
	 * @param context
	 *            invocation contextual information
	 * @param requestSettings
	 *            The settings used to perform the request, like the sort order,
	 *            pagination window, specific filters, etc.
	 * @return a collection of {@link CalmObject}
	 * @throws CalException
	 */
	ItemsResponse getCustomizedItemsFor(ItemKey itemKey, CalContext context,
			RequestSettings requestSettings) throws CalException;

	/**
	 * <p>
	 * Retrieves a list of items provided by this service with custom settings.
	 * </p>
	 * <p>
	 * The set of items the service will list is determined by the
	 * {@link RequestType} parameter. Possible values and effects for this
	 * parameter are implementation-dependant
	 * </p>
	 * <p>
	 * {@link RequestSettings} bean allow callers and implementors to implement
	 * custom filters, sort, pagination, etc.
	 * </p>
	 * 
	 * @param context
	 *            invocation contextual information
	 * @param requestType
	 *            the type of request to perform. Every CAL provider may provide
	 *            some request type through a static class. This allows to build
	 *            lightweight services which will return partial information
	 * @param requestSettings
	 *            The settings used to perform the request, like the sort order,
	 *            pagination window, specific filters, etc.
	 * @return a collection of {@link CalmObject}
	 * @throws CalException
	 */
	ItemsResponse listItems(CalContext context, RequestType requestType,
			RequestSettings requestSettings) throws CalException;

	/**
	 * Defines the class of objects provided by this service
	 * 
	 * @return the provided object class
	 */
	Class<? extends CalmObject> getProvidedClass();

	/**
	 * Defines the type name which this service provides
	 * 
	 * @return the type of items that the service provides
	 */
	String getProvidedType();

	/**
	 * <p>
	 * Returns a {@link RequestTypeFactory} instance use to create available
	 * {@link RequestType}s.
	 * </p>
	 * <p>
	 * The {@link RequestTypeFactory} is usually not used by Java clients which
	 * will construct {@link RequestType}s directly. However, query parsers
	 * (like the AQL parser) will make use of the factory to dynamically create
	 * required request types.
	 * </p>
	 * 
	 * @return RequestTypeFactory
	 */
	RequestTypeFactory getRequestTypeFactory();

	/**
	 * Defines the type name which this service understood. This enforces
	 * framework's error management since it allows the APIS layer to detect any
	 * inconsistent call and to raise an error instead of returning empty
	 * collections which would generate transparent errors. <br>
	 * <br>
	 * <b>Example :</b><br>
	 * A hotel content provider may provide a "HOTEL" type and support
	 * "PAGE_NAME" abstract type. This type is not provided by any other content
	 * box but is an alternate way of retrieving an hotel.
	 * 
	 * @return the input types that this service handles and supports. Those
	 *         types should correspond to other content box provided types or
	 *         they could be "abstract" types understood by this box.
	 * 
	 */
	Collection<String> getSupportedInputTypes();
}
