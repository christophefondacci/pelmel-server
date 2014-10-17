package com.videopolis.apis.concurrent.base;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import com.videopolis.apis.model.ApisContext;
import com.videopolis.apis.model.PaginationSettings;
import com.videopolis.calm.model.Sorter;
import com.videopolis.cals.service.CalService;
import com.videopolis.smaug.common.model.Facet;
import com.videopolis.smaug.common.model.FacetCategory;
import com.videopolis.smaug.common.model.SearchMethod;
import com.videopolis.smaug.common.model.SearchScope;
import com.videopolis.smaug.factory.SearchFactory;
import com.videopolis.smaug.model.SearchSettings;
import com.videopolis.smaug.model.SearchWindow;

/**
 * A base implementation of {@link AbstractApisTask} which provide common stuff
 * for search-based tasks.
 * 
 * @author julien
 * 
 */
public abstract class AbstractSearchApisTask extends AbstractApisTask {

	private final String requestedType;
	private final PaginationSettings pagination;
	private final CalService service;
	private final ApisContext context;
	private Collection<FacetCategory> facettedCategories = Collections
			.emptyList();
	private Collection<Facet> filteredFacets = Collections.emptyList();
	private List<Sorter> sorters = Collections.emptyList();
	private Locale locale;
	private SearchScope searchScope = SearchScope.NEARBY_BLOCK;
	private SearchMethod searchMethod = SearchMethod.CITIES_WITHOUT_SHADOW;

	/**
	 * Creates a new {@link AbstractSearchApisTask} with the specified
	 * information
	 * 
	 * @param service
	 *            CAL service to be called
	 * @param context
	 *            {@link ApisContext} in use
	 * @param requestedType
	 *            requested CAL item type
	 * @param pagination
	 *            pagination information
	 */
	protected AbstractSearchApisTask(final CalService service,
			final ApisContext context, final String requestedType,
			final PaginationSettings pagination) {
		this.service = service;
		this.context = context;
		this.requestedType = requestedType;
		this.pagination = pagination;
		locale = context.getCalContext().getLocale();
	}

	/**
	 * <p>
	 * Create a new instance of {@link SearchSettings} given the information
	 * contained in this instance.
	 * </p>
	 * <p>
	 * This method may be overrided if the default behavior is not suitable
	 * </p>
	 * 
	 * @return New {@link SearchSettings} implementation
	 */
	protected SearchSettings createSearchSettings() {
		return SearchFactory.createSearchSettings(requestedType, searchScope,
				searchMethod, filteredFacets, facettedCategories, sorters,
				locale);
	}

	/**
	 * <p>
	 * Create a new instance of {@link SearchWindow} given the information
	 * contained in this instance.
	 * </p>
	 * <p>
	 * This method may be overrided if the default behavior is not suitable
	 * </p>
	 * 
	 * @return New {@link SearchWindow} implementation
	 */
	protected SearchWindow createSearchWindow() {
		if (pagination != null) {
			return SearchFactory.createSearchWindow(
					pagination.getItemsByPage(), pagination.getPageOffset());
		} else {
			return SearchFactory.createSearchWindow(0, 0);
		}
	}

	/**
	 * @return the facettedCategories
	 */
	public Collection<FacetCategory> getFacettedCategories() {
		return facettedCategories;
	}

	/**
	 * @param facettedCategories
	 *            the facettedCategories to set
	 */
	public void setFacettedCategories(
			final Collection<FacetCategory> facettedCategories) {
		this.facettedCategories = facettedCategories;
	}

	/**
	 * @return the filteredFacets
	 */
	public Collection<Facet> getFilteredFacets() {
		return filteredFacets;
	}

	/**
	 * @param filteredFacets
	 *            the filteredFacets to set
	 */
	public void setFilteredFacets(final Collection<Facet> filteredFacets) {
		this.filteredFacets = filteredFacets;
	}

	/**
	 * @return the sorters
	 */
	public List<Sorter> getSorters() {
		return sorters;
	}

	/**
	 * @param sorters
	 *            the sorters to set
	 */
	public void setSorters(final List<Sorter> sorters) {
		this.sorters = sorters;
	}

	/**
	 * @return the locale
	 */
	public Locale getLocale() {
		return locale;
	}

	/**
	 * @param locale
	 *            the locale to set
	 */
	public void setLocale(final Locale locale) {
		this.locale = locale;
	}

	/**
	 * @return the searchScope
	 */
	public SearchScope getSearchScope() {
		return searchScope;
	}

	/**
	 * @param searchScope
	 *            the searchScope to set
	 */
	public void setSearchScope(final SearchScope searchScope) {
		this.searchScope = searchScope;
	}

	/**
	 * @return the requestedType
	 */
	public String getRequestedType() {
		return requestedType;
	}

	/**
	 * @return the pagination
	 */
	public PaginationSettings getPagination() {
		return pagination;
	}

	/**
	 * @return the service
	 */
	public CalService getService() {
		return service;
	}

	/**
	 * @return the context
	 */
	public ApisContext getContext() {
		return context;
	}

	/**
	 * @return the searchMethod
	 */
	public SearchMethod getSearchMethod() {
		return searchMethod;
	}

	/**
	 * @param searchMethod
	 *            the searchMethod to set
	 */
	public void setSearchMethod(final SearchMethod searchMethod) {
		this.searchMethod = searchMethod;
	}

	@Override
	public String toString() {
		return requestedType + ";" + searchScope + ";" + searchMethod + ";"
				+ filteredFacets.size();
	}
}
