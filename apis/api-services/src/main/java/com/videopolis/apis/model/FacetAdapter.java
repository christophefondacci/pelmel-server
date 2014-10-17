package com.videopolis.apis.model;

import java.util.List;

import com.videopolis.calm.model.CalmObject;

/**
 * Adapts facetting information into {@link CalmObject} so that we can connect
 * information to facets.
 * 
 * @author Christophe Fondacci
 * 
 */
public interface FacetAdapter {

    List<CalmObject> adaptFacets(FacetInformation facetInfo);
}
