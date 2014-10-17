package com.videopolis.cals.factory;

import java.util.Collection;

import com.videopolis.calm.model.Sorter;
import com.videopolis.cals.model.PaginationRequestSettings;
import com.videopolis.cals.model.RequestSettings;
import com.videopolis.cals.model.impl.PaginationRequestSettingsImpl;
import com.videopolis.cals.model.impl.RequestSettingsImpl;

/**
 * A factory used to build instances of {@link RequestSettings} and related
 * objects.
 * 
 * @author julien
 * 
 */
public final class RequestSettingsFactory {

    private RequestSettingsFactory() {
    }

    /**
     * Creates a {@link PaginationRequestSettings} instance
     * 
     * @param sortCriterion
     *            Sort criterion
     * @param sortOrder
     *            Sort order ({@code RequestSettings.DESCENDING_ORDER} or
     *            {@code RequestSettings.ASCENDING_ORDER})
     * @param resultsPerPage
     *            Number of results per page
     * @param pageNumber
     *            Page number
     * @return Settings
     */
    @Deprecated
    public static PaginationRequestSettings createPaginationRequestSettings(
	    final String sortCriterion, final byte sortOrder,
	    final int resultsPerPage, final int pageNumber) {
	return new PaginationRequestSettingsImpl(sortCriterion, sortOrder,
		pageNumber, resultsPerPage);
    }

    public static PaginationRequestSettings createPaginationRequestSettings(
	    final int resultsPerPage, final int pageNumber,
	    final Collection<? extends Sorter> sorters) {
	return new PaginationRequestSettingsImpl(pageNumber, resultsPerPage,
		sorters);
    }

    public static PaginationRequestSettings createPaginationRequestSettings(
	    final int resultsPerPage, final int pageNumber,
	    final Sorter... sorters) {
	return new PaginationRequestSettingsImpl(pageNumber, resultsPerPage,
		sorters);
    }

    /**
     * Creates a {@link RequestSettings} instance
     * 
     * @param sortCriterion
     *            Sort criterion
     * @param sortOrder
     *            Sort order ({@code RequestSettings.DESCENDING_ORDER} or
     *            {@code RequestSettings.ASCENDING_ORDER})
     * @return Settings
     */
    @Deprecated
    public static RequestSettings createRequestSettings(
	    final String sortCriterion, final byte sortOrder) {
	return new RequestSettingsImpl(sortCriterion, sortOrder);
    }

    public static RequestSettings createRequestSettings(
	    final Collection<? extends Sorter> sorters) {
	return new RequestSettingsImpl(sorters);
    }

    public static RequestSettings createRequestSettings(final Sorter... sorters) {
	return new RequestSettingsImpl(sorters);
    }
}
