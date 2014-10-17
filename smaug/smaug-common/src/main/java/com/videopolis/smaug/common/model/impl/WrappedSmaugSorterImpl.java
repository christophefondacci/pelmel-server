package com.videopolis.smaug.common.model.impl;

import com.videopolis.calm.model.Sorter;
import com.videopolis.smaug.common.model.SmaugSorter;

public class WrappedSmaugSorterImpl implements SmaugSorter {

    private final Sorter sorter;

    public WrappedSmaugSorterImpl(final Sorter sorter) {
	this.sorter = sorter;
    }

    @Override
    public String getCriterion() {
	return sorter.getCriterion();
    }

    @Override
    public Order getOrder() {
	return sorter.getOrder();
    }

    @Override
    @Deprecated
    public String getSortField() {
	return sorter.getCriterion();
    }

    @Override
    @Deprecated
    public boolean isAscending() {
	return sorter.getOrder() == Sorter.Order.ASCENDING;
    }

    @Override
    public String getUrlCode() {
	return null;
    }

    @Override
    public boolean isSticky() {
	return false;
    }
}
