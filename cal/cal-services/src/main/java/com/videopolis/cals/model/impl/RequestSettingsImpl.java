package com.videopolis.cals.model.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.videopolis.calm.factory.SorterFactory;
import com.videopolis.calm.model.Sorter;
import com.videopolis.cals.model.RequestSettings;

/**
 * Default implementation of {@link RequestSettings}
 * 
 * @author julien
 * 
 */
public class RequestSettingsImpl implements RequestSettings {

    private static final long serialVersionUID = -3156361732344244212L;

    private final List<Sorter> sortParameters;

    /**
     * Default constructor
     * 
     * @param sortCriterion
     *            Sorting criterion
     * @param sortOrder
     *            Sorting order
     */
    @Deprecated
    public RequestSettingsImpl(final String sortCriterion, final byte sortOrder) {
	sortParameters = Arrays.asList(SorterFactory.createSorter(
		sortCriterion, Sorter.Order.forCompatibleValue(sortOrder)));
    }

    public RequestSettingsImpl(final Collection<? extends Sorter> sorters) {
	sortParameters = Collections.unmodifiableList(new ArrayList<Sorter>(
		sorters));
    }

    public RequestSettingsImpl(final Sorter... sorters) {
	sortParameters = Arrays.asList(sorters);
    }

    public RequestSettingsImpl(final String criterion, final Sorter.Order order) {
	sortParameters = Arrays.asList(SorterFactory.createSorter(criterion,
		order));
    }

    @Override
    @Deprecated
    public String getSortCriterion() {
	if (sortParameters == null || sortParameters.isEmpty()) {
	    return null;
	} else {
	    return sortParameters.get(0).getCriterion();
	}
    }

    @Override
    @Deprecated
    public byte getSortOrder() {
	if (sortParameters == null || sortParameters.isEmpty()) {
	    return 0;
	} else {
	    return sortParameters.get(0).getOrder().getCompatibleValue();
	}
    }

    @Override
    public List<? extends Sorter> getSortParameters() {
	return sortParameters;
    }
}
