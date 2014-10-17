package com.videopolis.smaug.model.impl;

import java.util.List;

import com.videopolis.calm.model.Sorter;
import com.videopolis.smaug.common.model.SuggestScope;
import com.videopolis.smaug.model.SearchTextSettings;

/**
 * Implementation of the {@link SearchTextSettings} interface
 * 
 * @author Shoun Ichida
 * @since 11 janv. 2011
 */
public class SearchTextSettingsImpl implements SearchTextSettings {

    /** Map of returned type for the request indexed by scope. */
    private List<SuggestScope> suggestScope = null;
    /** List of sorter (element and order) to apply to the request. */
    private List<Sorter> sorters = null;

    /**
     * Default Ctor
     */
    public SearchTextSettingsImpl() {
	super();
    }

    /**
     * Convenience Ctor
     * 
     * @param suggestScope
     *            The returned type for scope to set
     * @param sorters
     *            The sorters to set
     */
    public SearchTextSettingsImpl(final List<SuggestScope> suggestScope,
	    final List<Sorter> sorters) {
	this.suggestScope = suggestScope;
	this.sorters = sorters;
    }

    @Override
    public List<SuggestScope> getSuggestScope() {
	return suggestScope;
    }

    @Override
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
     * @param suggestScope
     *            the suggestScope to set
     */
    public void setSuggestScope(final List<SuggestScope> suggestScope) {
	this.suggestScope = suggestScope;
    }
}
