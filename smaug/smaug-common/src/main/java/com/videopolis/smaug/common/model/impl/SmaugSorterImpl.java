package com.videopolis.smaug.common.model.impl;

import com.videopolis.calm.model.impl.SorterImpl;
import com.videopolis.smaug.common.model.SmaugSorter;

/**
 * Default implementation of a {@link SmaugSorter}
 * 
 * @author Christophe Fondacci
 * @author refactored by Shoun Ichida
 */
public class SmaugSorterImpl extends SorterImpl implements SmaugSorter {

    /** The url code for seol. */
    private final String urlCode;
    private final boolean sticky;

    @Deprecated
    public SmaugSorterImpl(final String criterion, final boolean isAscending) {
	this(criterion, isAscending ? Order.ASCENDING : Order.DESCENDING, null);
    }

    public SmaugSorterImpl(final String criterion, final Order order,
	    final String urlCode, final boolean sticky) {
	super(criterion, order);
	this.urlCode = urlCode;
	this.sticky = sticky;
    }

    public SmaugSorterImpl(final String criterion, final Order order,
	    final String urlCode) {
	super(criterion, order);
	this.urlCode = urlCode;
	sticky = false;
    }

    @Override
    @Deprecated
    public String getSortField() {
	return getCriterion();
    }

    @Override
    @Deprecated
    public boolean isAscending() {
	return getOrder() == Order.ASCENDING;
    }

    @Override
    public final String getUrlCode() {
	return urlCode;
    }

    @Override
    public boolean isSticky() {
	return sticky;
    }
}
