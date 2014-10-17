package com.videopolis.cals.service.base;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.videopolis.calm.exception.CalException;
import com.videopolis.calm.factory.RequestTypeFactory;
import com.videopolis.calm.factory.impl.DefaultRequestTypeFactory;
import com.videopolis.calm.helper.Assert;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.calm.model.RequestType;
import com.videopolis.cals.exception.UnsupportedCalServiceException;
import com.videopolis.cals.model.CalContext;
import com.videopolis.cals.model.ItemsResponse;
import com.videopolis.cals.model.MultiKeyItemsResponse;
import com.videopolis.cals.model.PaginatedItemsResponse;
import com.videopolis.cals.model.PaginationRequestSettings;
import com.videopolis.cals.model.RequestSettings;
import com.videopolis.cals.model.impl.MultiKeyItemsResponseImpl;
import com.videopolis.cals.service.CalService;

/**
 * <p>
 * A base implementation of {@link CalService}.
 * </p>
 * <p>
 * Implementors should not directly implement {@link CalService} but rather
 * extend this class which provide base functionality.
 * </p>
 * 
 * @author julien
 * 
 */
public abstract class AbstractCalService implements CalService {

	private String baseUrl;
	private RequestTypeFactory requestTypeFactory;

	/**
	 * Retrieves the base URL of the remote "real" service to call. Any
	 * implementor should rely on this method to obtain the URL of the distant
	 * service to call, should you need to.
	 * 
	 * @return the base URL of the distant service.
	 */
	public String getBaseUrl() {
		return baseUrl;
	}

	/**
	 * {@inheritDoc} <br>
	 * <br>
	 * This default implementation redirects to
	 * {@link CalService#getItems(Collection, CalContext)} standard
	 * implementation. Implementors may extend to optimize the load of partial
	 * object.<br>
	 * 
	 */
	@Override
	public ItemsResponse getItems(final List<ItemKey> ids,
			final CalContext context, final RequestType requestType)
			throws CalException {
		// Default implementation return the full object
		return getItems(ids, context);
	}

	/**
	 * A unitary version of the
	 * {@link CalService#getItemsFor(Collection, CalContext)} method more simple
	 * to implement for general purposes. Services which could provide
	 * optimizations for multi contributions may also consider to override the
	 * base {@link CalService#getItemsFor(Collection, CalContext) method}
	 * 
	 * @param itemKey
	 *            unique key of the element to contribute to
	 * @param context
	 *            invocation context
	 * @return contributed elements through a {@link ItemsResponse}
	 */
	protected abstract ItemsResponse getItemsFor(ItemKey itemKey,
			CalContext context) throws CalException;

	/**
	 * <p>
	 * A unitary version of the
	 * {@link CalService#getItemsFor(Collection, CalContext, RequestType)}
	 * method more simple to implement for general purposes. Services which
	 * could provide optimizations for multi contributions may also consider to
	 * override the base
	 * {@link CalService#getItemsFor(Collection, CalContext, RequestType)
	 * method}
	 * </p>
	 * <p>
	 * The default implementation of this method will just call
	 * {@link CalService#getItemsFor(ItemKey, CalContext)}, ignoring the
	 * {@link RequestType} parameter. Implementors who works with
	 * {@link RequestType}s may have to override this method.
	 * </p>
	 * 
	 * @param itemKey
	 *            unique key of the element to contribute to
	 * @param context
	 *            invocation context
	 * @param requestType
	 *            The request's type, to narrow the results
	 * @return contributed elements through a {@link ItemsResponse}
	 */
	protected ItemsResponse getItemsFor(final ItemKey itemKey,
			final CalContext context, final RequestType requestType)
			throws CalException {
		return getItemsFor(itemKey, context);
	}

	@Override
	public MultiKeyItemsResponse getItemsFor(final List<ItemKey> itemKeys,
			final CalContext context) throws CalException {
		final MultiKeyItemsResponseImpl response = new MultiKeyItemsResponseImpl();
		for (final ItemKey itemKey : itemKeys) {
			final ItemsResponse unitaryResponse = getItemsFor(itemKey, context);
			response.setItemsFor(itemKey, unitaryResponse.getItems());
		}
		return response;
	}

	@Override
	public MultiKeyItemsResponse getItemsFor(final List<ItemKey> itemKeys,
			final CalContext context, final RequestType requestType)
			throws CalException {
		final MultiKeyItemsResponseImpl response = new MultiKeyItemsResponseImpl();
		for (final ItemKey itemKey : itemKeys) {
			final ItemsResponse unitaryResponse = getItemsFor(itemKey, context,
					requestType);
			response.setItemsFor(itemKey, unitaryResponse.getItems());
		}
		return response;
	}

	/**
	 * <p>
	 * This method is called by the default implementation of
	 * {@link getCustomizedItemsFor} when an instance of
	 * {@link PaginationRequestSettings} is passed for the
	 * {@code requestSettings} parameter.
	 * </p>
	 * <p>
	 * This implementation of the method will just forward the call to {@link
	 * CalService.getPaginatedItemsFor(itemKey,CalContext,int,int)}. This is
	 * suitable only when the cal service implementation does not support
	 * multiple ordering, since the ordering specification is contained into the
	 * {@link RequestSettings} object, which is not supported by the other forms
	 * of {@code getItemsFor}.
	 * </p>
	 * <p>
	 * In other words, if your implementation supports multiple sort orders, you
	 * must override this method.
	 * </p>
	 */
	protected PaginatedItemsResponse getPaginatedItemsFor(
			final ItemKey itemKey, final CalContext context,
			final PaginationRequestSettings requestSettings)
			throws CalException {
		return getPaginatedItemsFor(itemKey, context,
				requestSettings.getResultsPerPage(),
				requestSettings.getPageNumber());
	}

	@Override
	public PaginatedItemsResponse getPaginatedItemsFor(ItemKey itemKey,
			CalContext context, int resultsPerPage, int pageNumber,
			RequestType requestType) throws CalException {
		// Standard default behaviour is to route to the standard paginated
		// items for
		return getPaginatedItemsFor(itemKey, context, resultsPerPage,
				pageNumber);
	}

	/**
	 * <p>
	 * This method is called by the default implementation of
	 * {@link getCustomizedItemsFor} when an instance of {@link RequestSettings}
	 * which is not an instance of {@link PaginationRequestSettings} is passed
	 * for the {@code requestSettings} parameter.
	 * </p>
	 * <p>
	 * This implementation of the method will just forward the call to {@link
	 * CalService.getItemsFor(itemKey,CalContext)}. This is suitable only when
	 * the cal service implementation does not support multiple ordering, since
	 * the ordering specification is contained into the {@link RequestSettings}
	 * object, which is not supported by the other forms of {@code getItemsFor}.
	 * </p>
	 * <p>
	 * In other words, if your implementation supports multiple sort orders, you
	 * must override this method.
	 * </p>
	 */
	protected ItemsResponse getItemsFor(final ItemKey itemKey,
			final CalContext calContext, final RequestSettings requestSettings)
			throws CalException {
		return getItemsFor(itemKey, calContext);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * This default implementation checks the actual class of
	 * {@code requestSettings} and forwards the call to {@link
	 * getPaginatedItemsFor(ItemKey,CalContext,PaginationRequestSettings} or
	 * {@link getItemsFor(ItemKey,CalContext,RequestSettings)} depending on the
	 * presence of pagination information in the {@link RequestSettings}
	 * </p>
	 */
	@Override
	public ItemsResponse getCustomizedItemsFor(final ItemKey itemKey,
			final CalContext context, final RequestSettings requestSettings)
			throws CalException {

		if (requestSettings instanceof PaginationRequestSettings) {
			// Redirects to the paginated version of this method
			return getPaginatedItemsFor(itemKey, context,
					(PaginationRequestSettings) requestSettings);
		} else {
			// Redirects to the not paginated version of this method
			return getItemsFor(itemKey, context, requestSettings);
		}
	}

	@Override
	public RequestTypeFactory getRequestTypeFactory() {
		if (requestTypeFactory == null) {
			requestTypeFactory = new DefaultRequestTypeFactory();
		}
		return requestTypeFactory;
	}

	/**
	 * This helper method allows CAL service implementors to easily order their
	 * output according to the original ordering of the {@link ItemKey} input
	 * collection.<br>
	 * All CAL implementors may not need to call this method as the
	 * implementation may natively preserve the input keys order. For the
	 * others, a call to this method will return a list of properly ordered
	 * {@link CalmObject}.<br>
	 * <br>
	 * <b>Important :</b> - The ordered list is a new collection, initial
	 * collection is not ordered in place (this is why it is a
	 * {@link Collection} argument rather than a list).<br>
	 * - Calling this method will induce a little performance overhead for
	 * sorting the collection and a little memory overhead as some objects will
	 * need to be allocated.<br>
	 * 
	 * @param <T>
	 * @param inputKeys
	 *            the list of {@link ItemKey} input asked to the
	 *            {@link CalService}. This list will be used to determine the
	 *            order we need to apply.
	 * @param result
	 *            the collection of {@link CalmObject} about to be returned by a
	 *            {@link CalService}. Elements from this collection will be
	 *            reordered in a new list.
	 * @return the list of reordered {@link CalmObject}
	 */
	protected <T extends CalmObject> List<T> reorderCalmObjects(
			final List<ItemKey> inputKeys, final Collection<T> result) {
		final List<T> orderedObjects = new LinkedList<T>();
		final Map<ItemKey, T> resultKeyMap = new HashMap<ItemKey, T>(
				result.size());
		// Pass-1: Hashing the CalmObject by their key
		for (final T object : result) {
			resultKeyMap.put(object.getKey(), object);
		}
		// Pass-2: iterating over initial input keys and filling output list
		for (final ItemKey inputKey : inputKeys) {
			final T object = resultKeyMap.get(inputKey);
			if (object != null) {
				orderedObjects.add(object);
			}
		}
		return orderedObjects;
	}

	/**
	 * Defines the base URL of the remote "real" service. This abstraction
	 * allows homogeneous injection of this property through spring among every
	 * {@link CalService}
	 * 
	 * @param baseUrl
	 *            base URL of the remote "real" service
	 */
	public void setBaseUrl(final String baseUrl) {
		this.baseUrl = baseUrl;
	}

	public void setRequestTypeFactory(
			final RequestTypeFactory requestTypeFactory) {
		this.requestTypeFactory = requestTypeFactory;
	}

	@Override
	public MultiKeyItemsResponse getItemsRangeFor(final List<ItemKey> itemKeys,
			final CalContext context, final int resultsPerPage,
			final int pageNumber) throws CalException {
		// Default implementation is iterating over "single-key" method
		Assert.notNull(itemKeys, "Cannot process null item keys list");
		final MultiKeyItemsResponseImpl response = new MultiKeyItemsResponseImpl();
		for (final ItemKey key : itemKeys) {
			final ItemsResponse singleResponse = getItemsRangeFor(key, context,
					resultsPerPage, pageNumber);
			Assert.notNull(singleResponse,
					"getRangeItemsFor(ItemKey) returned null");
			response.setItemsFor(key, singleResponse.getItems());
		}
		return response;
	}

	/**
	 * Retrieves the items contributing to the specified {@link ItemKey} from
	 * the pagination settings provided. This special method is called
	 * iteratively by the
	 * {@link CalService#getItemsRangeFor(List, CalContext, int, int)} which
	 * implementors may consider implementing directly to optimize performances.<br>
	 * This implementation should be very similar to the
	 * {@link CalService#getPaginatedItemsFor(ItemKey, CalContext, int, int)}
	 * method, except that it should not compute resulting pagination
	 * information in order to save speed.
	 * 
	 * @param itemKey
	 *            {@link ItemKey} to get elements for
	 * @param context
	 *            the {@link CalContext}
	 * @param resultsPerPage
	 *            max number of returned elements
	 * @param pageNumber
	 *            page number to start from
	 * @return the {@link ItemsResponse} containing contributing elements
	 * @throws CalException
	 */
	protected ItemsResponse getItemsRangeFor(final ItemKey itemKey,
			final CalContext context, final int resultsPerPage,
			final int pageNumber) throws CalException {
		throw new UnsupportedCalServiceException(
				"This method needs explicit implementation");
	}

	@Override
	public ItemsResponse listItems(final CalContext context,
			final RequestType requestType, final RequestSettings requestSettings)
			throws CalException {
		throw new UnsupportedCalServiceException(
				"This method needs explicit implementation");
	}

	@Override
	public String toString() {
		return getProvidedType() + "CalService";
	}
}
