package com.videopolis.cals.model.impl;

import java.util.Collection;

import com.videopolis.calm.model.Sorter;
import com.videopolis.cals.model.PaginationRequestSettings;

/**
 * Default implementation of {@link PaginationRequestSettings}
 * 
 * @author julien
 * 
 */
public class PaginationRequestSettingsImpl extends RequestSettingsImpl
	implements PaginationRequestSettings {

    private static final long serialVersionUID = -7789244047099132162L;

    private final int pageNumber;
    private final int resultsPerPage;

    public PaginationRequestSettingsImpl(final int pageNumber,
	    final int resultsPerPage, final Collection<? extends Sorter> sorters) {
	super(sorters);
	this.pageNumber = pageNumber;
	this.resultsPerPage = resultsPerPage;
    }

    public PaginationRequestSettingsImpl(final int pageNumber,
	    final int resultsPerPage, final Sorter... sorters) {
	super(sorters);
	this.pageNumber = pageNumber;
	this.resultsPerPage = resultsPerPage;
    }

    /**
     * Default constructor
     * 
     * @param sortCriterion
     *            Sorting criterion
     * @param sortOrder
     *            Sorting order
     * @param pageNumber
     *            Page number
     * @param resultsPerPage
     *            Results per page
     */
    @Deprecated
    public PaginationRequestSettingsImpl(final String sortCriterion,
	    final byte sortOrder, final int pageNumber, final int resultsPerPage) {
	super(sortCriterion, sortOrder);
	this.pageNumber = pageNumber;
	this.resultsPerPage = resultsPerPage;
    }

    @Override
    public int getPageNumber() {
	return pageNumber;
    }

    @Override
    public int getResultsPerPage() {
	return resultsPerPage;
    }

    @Override
    public String toString() {
	return "PaginationRequestSettings[sortOrder=" + getSortOrder()
		+ ", sortCriterion=" + getSortCriterion() + ", pageNumber="
		+ pageNumber + ", resultsPerPage=" + resultsPerPage + "]";
    }
}
