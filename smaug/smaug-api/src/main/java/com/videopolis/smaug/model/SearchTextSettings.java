package com.videopolis.smaug.model;

import java.util.List;

import com.videopolis.calm.model.Sorter;
import com.videopolis.smaug.common.model.SuggestScope;

/**
 * This interface represents settings for suggest request behavior.
 * 
 * @author Shoun Ichida
 * @since 11 janv. 2011
 */
public interface SearchTextSettings {

    /**
     * Returns the expected scope for the request.
     * 
     * @return a list of expected scope
     */
    List<SuggestScope> getSuggestScope();

    /**
     * Returns the sorting parameters of the query
     * 
     * @return a list of {@link Sorter} to use to sort search results
     */
    List<Sorter> getSorters();
}
