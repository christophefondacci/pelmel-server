package com.videopolis.apis.factory;

import java.util.Arrays;
import java.util.List;

import com.videopolis.apis.exception.ApisException;
import com.videopolis.apis.helper.ApisRegistry;
import com.videopolis.apis.helper.Assert;
import com.videopolis.apis.model.AlternateKeyCriterion;
import com.videopolis.apis.model.ApisCalmObjectAdapter;
import com.videopolis.apis.model.ApisCriterion;
import com.videopolis.apis.model.ApisCustomAdapter;
import com.videopolis.apis.model.ApisItemKeyAdapter;
import com.videopolis.apis.model.ApisRequest;
import com.videopolis.apis.model.ForCriterion;
import com.videopolis.apis.model.ItemKeyAdapterCriterion;
import com.videopolis.apis.model.ListCriterion;
import com.videopolis.apis.model.PaginationSettings;
import com.videopolis.apis.model.SearchCriterion;
import com.videopolis.apis.model.UniqueKeyCriterion;
import com.videopolis.apis.model.WithCriterion;
import com.videopolis.apis.model.impl.AdapterCriterionImpl;
import com.videopolis.apis.model.impl.AlternateKeyCriterionImpl;
import com.videopolis.apis.model.impl.CustomAdaptCriterionImpl;
import com.videopolis.apis.model.impl.CustomizedWithCriterionImpl;
import com.videopolis.apis.model.impl.ForCriterionImpl;
import com.videopolis.apis.model.impl.ItemKeyAdapterCriterionImpl;
import com.videopolis.apis.model.impl.ListCriterionImpl;
import com.videopolis.apis.model.impl.PaginationSettingsImpl;
import com.videopolis.apis.model.impl.SearchAllCriterionImpl;
import com.videopolis.apis.model.impl.SearchCriterionImpl;
import com.videopolis.apis.model.impl.SearchNearCriterionImpl;
import com.videopolis.apis.model.impl.SearchTextCriterionImpl;
import com.videopolis.apis.model.impl.UniqueKeyCriterionImpl;
import com.videopolis.apis.model.impl.WithCriterionImpl;
import com.videopolis.calm.exception.CalException;
import com.videopolis.calm.factory.CalmFactory;
import com.videopolis.calm.factory.SorterFactory;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.calm.model.Localized;
import com.videopolis.calm.model.RequestType;
import com.videopolis.calm.model.Sorter;
import com.videopolis.cals.service.CalService;
import com.videopolis.smaug.common.model.SearchMethod;
import com.videopolis.smaug.common.model.SearchScope;
import com.videopolis.smaug.common.model.SuggestScope;

/**
 * Convenience factory methods able to create {@link ApisCriterion} which should
 * be passed to {@link ApisRequest#addCriteria(ApisCriterion)} method to define
 * the request search criterias.<br>
 * We typically delegate criteria creation to this class to avoid to mess up the
 * {@link ApisRequest} implementation which should stay focused on request
 * processing.
 * 
 * @author Christophe Fondacci
 * 
 */
public final class SearchRestriction {

	private SearchRestriction() {
	}

	public static UniqueKeyCriterion uniqueKeys(List<ItemKey> keys)
			throws ApisException {
		return new UniqueKeyCriterionImpl(
				keys.toArray(new ItemKey[keys.size()]));
	}

	/**
	 * Builds an alternate key using the CAL service registered for the given
	 * class and the alternate key provided.
	 * 
	 * @param itemType
	 *            the class of the model to fetch
	 * @param alternateKey
	 *            the alternate key argument
	 * @return an alternate key {@link ApisCriterion}
	 */
	public static AlternateKeyCriterion alternateKey(
			Class<? extends CalmObject> itemType, ItemKey alternateKey) {
		final String calType = ApisRegistry.getTypeFromModel(itemType);
		return new AlternateKeyCriterionImpl(calType, alternateKey);
	}

	/**
	 * Builds a "with" criteria which defines elements to retrieve along with
	 * the primary item type. For example, you may want to retrieve hotels
	 * <i>with</i> reviews.
	 * 
	 * @param itemType
	 *            type of items to retrieve <i>with</i> the primary item type
	 * @return the corresponding {@link ApisCriterion}
	 */
	public static WithCriterion with(final Class<? extends CalmObject> itemType)
			throws ApisException {
		final String type = ApisRegistry.getTypeFromModel(itemType);
		return new WithCriterionImpl(type);
	}

	/**
	 * Builds a <b>named</b> "with" criteria which defines elements to retrieve
	 * along with the primary item type.
	 * 
	 * @param itemType
	 *            type of items to retrieve <i>with</i> the primary item type
	 * @param alias
	 *            explicit name of this association. This name is the one that
	 *            needs to be used on the resulting {@link CalmObject} in order
	 *            to retrieve elements fetched by this criterion.
	 * @return the corresponding {@link ApisCriterion}
	 */
	public static WithCriterion with(
			final Class<? extends CalmObject> itemType, final String alias)
			throws ApisException {
		final WithCriterion crit = with(itemType);
		crit.aliasedBy(alias);
		return crit;
	}

	/**
	 * Builds a "with" criteria which defines elements to retrieve along with
	 * the primary item type. For example, you may want to retrieve hotels
	 * <i>with</i> reviews.<br>
	 * This method allows callers to specify a {@link RequestType} when querying
	 * connected items so that a specific fetch method and / or arguments could
	 * be given to the underlying {@link CalService}.
	 * 
	 * @param itemType
	 *            type of items to retrieve <i>with</i> the primary item type
	 * @param requestType
	 *            {@link RequestType} to use to query the CalService
	 * @return the corresponding {@link ApisCriterion}
	 */
	public static WithCriterion with(
			final Class<? extends CalmObject> itemType,
			final RequestType requestType) throws ApisException {
		final String type = ApisRegistry.getTypeFromModel(itemType);
		final WithCriterionImpl withCriterion = new WithCriterionImpl(type);
		withCriterion.setRequestType(requestType);
		return withCriterion;
	}

	/**
	 * Builds a "with" criteria with paginated results. It allows to define that
	 * you want to retrieve hotels <i>with</i> their reviews, paginated by 10.
	 * 
	 * @param itemType
	 *            type of items to retrieve <i>with</i> the primary item type
	 * @param itemsByPage
	 *            number of items by page
	 * @param pageNumber
	 *            page number (starts at 0)
	 * @return the corresponding {@link ApisCriterion}
	 */
	public static WithCriterion with(
			final Class<? extends CalmObject> itemType, final int itemsByPage,
			final int pageNumber) throws ApisException {
		final String type = ApisRegistry.getTypeFromModel(itemType);
		final WithCriterion withCrit = new WithCriterionImpl(type);
		final PaginationSettings window = new PaginationSettingsImpl();
		window.setItemsByPage(itemsByPage);
		window.setPageOffset(pageNumber);
		withCrit.setPagination(window);
		return withCrit;
	}

	/**
	 * Builds a "with" criteria with paginated and ordered results. It allows to
	 * define that you want to retrieve hotels <em>with</em> their reviews
	 * ordered by date in descending order, paginated by 10.
	 * 
	 * @param type
	 *            type of items to retrieve <em>with</em> the primary item type
	 * @param sortOrder
	 *            The sort order
	 * @param sortCriterion
	 *            The sort criterion, which is box-dependant
	 * @param itemsPerPage
	 *            number of items by page
	 * @param pageOffset
	 *            page start offset (starts at 0)
	 * @return the corresponding {@link ApisCriterion}
	 * @throws ApisException
	 */
	public static WithCriterion with(final Class<? extends CalmObject> type,
			final Sorter.Order sortOrder, final String sortCriterion,
			final int itemsPerPage, final int pageOffset) throws ApisException {
		final String itemType = ApisRegistry.getTypeFromModel(type);
		final WithCriterion crit = new CustomizedWithCriterionImpl(itemType);
		crit.paginatedBy(itemsPerPage, pageOffset);
		final Sorter sorter = SorterFactory.createSorter(sortCriterion,
				sortOrder);
		crit.sortBy(sorter);
		return crit;
	}

	/**
	 * Builds a nearby criteria with pagination. It allows to define that you
	 * want to retrieve the <i>nearest</i> hotels from a given {@link Localized}
	 * geographic point.
	 * 
	 * @param itemType
	 *            type of elements to retrieve
	 * @param pageSize
	 *            number of elements which should be returned for one page of
	 *            results
	 * @param pageOffset
	 *            number of the page to display, starts at 0
	 * @param latitude
	 *            latitude of the point to start the geographic search from
	 * @param longitude
	 *            longitude of the point to start the geographic search from
	 * @param radius
	 *            geographic search radius
	 * @return a {@link ApisCriterion} containing this information
	 * @deprecated use the overloaded method with the explicit search scope
	 */
	@Deprecated
	public static SearchCriterion withNearest(
			final Class<? extends CalmObject> itemType, final int pageSize,
			final int pageOffset, final double radius) throws ApisException {
		return withNearest(itemType, SearchScope.NEARBY_BLOCK, pageSize,
				pageOffset, radius);
	}

	/**
	 * Builds a nearby criteria with pagination. It allows to define that you
	 * cant to retrieve the <i>nearest</i> hotels from a given {@link Localized}
	 * geographic point.
	 * 
	 * @param itemType
	 *            type of elements to retrieve
	 * @param scope
	 *            the {@link SearchScope} to use which indicates the kind of
	 *            search to perform
	 * @param pageSize
	 *            number of elements which should be returned for one page of
	 *            results
	 * @param pageOffset
	 *            number of the page to display, starts at 0
	 * @param latitude
	 *            latitude of the point to start the geographic search from
	 * @param longitude
	 *            longitude of the point to start the geographic search from
	 * @param radius
	 *            geographic search radius
	 * @return a {@link ApisCriterion} containing this information
	 */
	public static SearchCriterion withNearest(
			final Class<? extends CalmObject> itemType,
			final SearchScope scope, final int pageSize, final int pageOffset,
			final double radius) throws ApisException {
		final String type = ApisRegistry.getTypeFromModel(itemType);
		final SearchCriterion searchCrit = new SearchCriterionImpl(type, scope,
				SearchMethod.CITIES_WITHOUT_SHADOW, radius);
		searchCrit.paginatedBy(pageSize, pageOffset);

		return searchCrit;
	}

	/**
	 * Builds a "forKey" criterion, which literally fetch elements <i>for a
	 * given explicit key</i>, with pagination
	 * 
	 * @param type
	 *            type of the element to which elements to retrieve are
	 *            connected
	 * @param id
	 *            identifier of the element to which elements to retrieve are
	 *            connected
	 * @param itemsByPage
	 *            number of items to retrieve
	 * @param pageNumber
	 *            page number of the element to retrieve
	 * @return the corresponding {@link ApisCriterion}
	 * @throws ApisException
	 *             whenever a problem prevents the criterion from being created
	 */
	public static ForCriterion forKey(final String type, final String id,
			final int itemsByPage, final int pageNumber) throws ApisException {
		try {
			final ForCriterionImpl forCriterion = new ForCriterionImpl(
					CalmFactory.createKey(type, id), itemsByPage, pageNumber);
			return forCriterion;
		} catch (final CalException e) {
			throw new ApisException("Invalid CAL key: " + e.getMessage(), e);
		}
	}

	/**
	 * Builds a "forKey" criterion, which literally fetch elements <i>for a
	 * given explicit key</i>, with pagination. This method differs from the
	 * {@link SearchRestriction#forKey(String, String, int, int)} as it
	 * explicitly defines the expected CAL class to fetch, thus indicating the
	 * CAL service that should be called. It should be used for root FOR
	 * criterion of composite requests where global {@link ApisRequest} CAL type
	 * is not known.
	 * 
	 * @param calClass
	 *            the {@link CalmObject} class of the elements to fetch
	 * @param type
	 *            type of the element to which elements to retrieve are
	 *            connected
	 * @param id
	 *            identifier of the element to which elements to retrieve are
	 *            connected
	 * @param itemsByPage
	 *            number of items to retrieve
	 * @param pageNumber
	 *            page number of the element to retrieve
	 * @return the corresponding {@link ApisCriterion}
	 * @throws ApisException
	 *             whenever a problem prevents the criterion from being created
	 */
	public static ForCriterion forKey(
			final Class<? extends CalmObject> calClass, final String type,
			final String id, final int itemsByPage, final int pageNumber)
			throws ApisException {
		try {
			final ForCriterionImpl forCriterion = new ForCriterionImpl(
					CalmFactory.createKey(type, id), itemsByPage, pageNumber);
			// Retrieving the CAL type associated with the CAL class
			final String calType = ApisRegistry.getTypeFromModel(calClass);
			// Safety check
			Assert.notNull(calType, "Invalid or unmapped CAL class : "
					+ calClass);
			// Assigning the type to the criterion
			forCriterion.setType(ApisRegistry.getTypeFromModel(calClass));

			// We're done
			return forCriterion;
		} catch (final CalException e) {
			throw new ApisException("Invalid CAL key: " + e.getMessage(), e);
		}
	}

	/**
	 * Creates a criterion which adapts the parent element using the specified
	 * adapter. Any further criteria linked to this one will be relative to the
	 * adapted {@link CalmObject}.
	 * 
	 * @param adapter
	 *            an {@link ApisCalmObjectAdapter} which will adapt the
	 *            {@link CalmObject} returned by the parent criteria
	 * @return the APIS adapter criterion
	 */
	public static ApisCriterion adapt(final ApisCalmObjectAdapter adapter) {
		return new AdapterCriterionImpl(adapter);
	}

	public static ItemKeyAdapterCriterion adaptKey(
			final ApisItemKeyAdapter adapter) {
		return new ItemKeyAdapterCriterionImpl(adapter);
	}

	/**
	 * Builds a geographical surfacic search criterion which will search for
	 * elements contained in the surface of a parent element.
	 * 
	 * @param itemType
	 *            the class of {@link CalmObject} to search for
	 * @param scope
	 *            the {@link SearchScope} to use which indicates the kind of
	 *            search to perform
	 * @param pageSize
	 *            the number of elements per page
	 * @param pageOffset
	 *            offset of the page of results to return, starts at 0
	 * @return the {@link SearchCriterion}
	 * @throws ApisException
	 */
	public static SearchCriterion withContained(
			final Class<? extends CalmObject> itemType,
			final SearchScope scope, final int pageSize, final int pageOffset)
			throws ApisException {
		final String type = ApisRegistry.getTypeFromModel(itemType);
		final SearchCriterion searchCrit = new SearchCriterionImpl(type, scope,
				SearchMethod.CITIES_WITHOUT_SHADOW);
		searchCrit.paginatedBy(pageSize, pageOffset);
		return searchCrit;
	}

	/**
	 * Builds a geographical surfacic search criterion which will search for
	 * elements contained in the surface of a parent element.
	 * 
	 * @param itemType
	 *            the class of {@link CalmObject} to search for
	 * @param scope
	 *            the {@link SearchScope} to use which indicates the kind of
	 *            search to perform
	 * @param searchMethod
	 *            the {@link SearchMethod} to use which indicates the method of
	 *            search to use
	 * @param pageSize
	 *            the number of elements per page
	 * @param pageOffset
	 *            offset of the page of results to return, starts at 0
	 * @return the {@link SearchCriterion}
	 * @throws ApisException
	 */
	public static SearchCriterion withContained(
			final Class<? extends CalmObject> itemType,
			final SearchScope scope, final SearchMethod searchMethod,
			final int pageSize, final int pageOffset) throws ApisException {
		final String type = ApisRegistry.getTypeFromModel(itemType);
		final SearchCriterion searchCrit = new SearchCriterionImpl(type, scope,
				searchMethod);
		searchCrit.paginatedBy(pageSize, pageOffset);
		return searchCrit;
	}

	/**
	 * Builds a textual search criterion which will retrieve elements based on a
	 * search on a fragment of its name.
	 * 
	 * @param itemType
	 *            type of elements to fetch
	 * @param scopes
	 *            the {@link SuggestScope} indicating the kind of elements we
	 *            are searching for
	 * @param text
	 *            the text to search for
	 * @param itemsCount
	 *            number of items to search for
	 * @return the corresponding {@link ApisCriterion}
	 */
	public static WithCriterion searchFromText(
			final Class<? extends CalmObject> itemType,
			final SuggestScope scopes, final String text, final int itemsCount) {
		return searchFromText(itemType, Arrays.asList(scopes), text, itemsCount);
	}

	public static WithCriterion searchFromText(
			final Class<? extends CalmObject> itemType,
			final List<SuggestScope> scopes, final String text,
			final int itemsCount) {
		final String type = ApisRegistry.getTypeFromModel(itemType);
		return new SearchTextCriterionImpl(type, scopes, text, itemsCount);
	}

	public static SearchCriterion searchAll(
			Class<? extends CalmObject> itemType, SearchScope scope,
			int pageSize, int pageOffset) {
		final String type = ApisRegistry.getTypeFromModel(itemType);
		final SearchCriterion crit = new SearchAllCriterionImpl(type, scope,
				SearchMethod.CITIES_WITH_SHADOW);
		crit.paginatedBy(pageSize, pageOffset);
		return crit;
	}

	public static SearchCriterion searchNear(
			Class<? extends CalmObject> itemType, SearchScope scope,
			double lat, double lng, double radius, int pageSize, int pageOffset) {
		final String type = ApisRegistry.getTypeFromModel(itemType);
		final SearchCriterion crit = new SearchNearCriterionImpl(type, scope,
				SearchMethod.CITIES_WITHOUT_SHADOW, lat, lng, radius);
		crit.paginatedBy(pageSize, pageOffset);
		return crit;
	}

	/**
	 * Retrieves a list of elements of a specific type with a particular request
	 * type. The exact behavior of this method will depend on the underlying CAL
	 * service.
	 * 
	 * @param type
	 *            Type
	 * @param requestType
	 *            Request type
	 * @return the request
	 * @throws ApisException
	 */
	public static ListCriterion list(
			final Class<? extends CalmObject> itemType,
			final RequestType requestType) throws ApisException {
		final String type = ApisRegistry.getTypeFromModel(itemType);
		return new ListCriterionImpl(type, requestType);
	}

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
	public static ApisCriterion customAdapt(ApisCustomAdapter adapter,
			String alias) {
		return new CustomAdaptCriterionImpl(adapter, alias);
	}
}
