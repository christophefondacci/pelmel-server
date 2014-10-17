package com.videopolis.apis.aql.model;

import java.util.ArrayList;
import java.util.List;

import com.videopolis.apis.aql.helper.AqlParsingHelper;
import com.videopolis.apis.exception.ApisException;
import com.videopolis.apis.factory.SearchRestriction;
import com.videopolis.apis.model.ApisCriterion;
import com.videopolis.apis.model.WithCriterion;
import com.videopolis.calm.model.CalmObject;

/**
 * Represents a with criterion which is not yet added to the query.
 * 
 * It's internally used when parsing queries and the final user will never have
 * to use this directly.
 * 
 * @author julien
 * 
 */
public class PendingCriterion {

    private String itemType;
    private String itemId;
    private Double radius;
    private Integer itemsPerPage;
    private Integer pageNumber;
    private List<ApisCriterion> children;

    public String getItemType() {
	return itemType;
    }

    public void setItemType(String itemType) {
	this.itemType = itemType;
    }

    public String getItemId() {
	return itemId;
    }

    public void setItemId(String itemId) {
	this.itemId = itemId;
    }

    public Double getRadius() {
	return radius;
    }

    public void setRadius(Double radius) {
	this.radius = radius;
    }

    public Integer getItemsPerPage() {
	return itemsPerPage;
    }

    public void setItemsPerPage(Integer itemsPerPage) {
	this.itemsPerPage = itemsPerPage;
    }

    public Integer getPageNumber() {
	return pageNumber;
    }

    public void setPageNumber(Integer pageNumber) {
	this.pageNumber = pageNumber;
    }

    public void addChild(ApisCriterion child) {
	if (children == null) {
	    children = new ArrayList<ApisCriterion>();
	}
	this.children.add(child);
    }

    public WithCriterion asWithCriterion() throws ApisException {
	final WithCriterion criterion;

	// Get type
	Class<? extends CalmObject> typeClass = AqlParsingHelper
		.getCalmObjectClass(itemType);

	if (radius != null) {
	    // That's a with nearest
	    criterion = SearchRestriction.withNearest(typeClass, itemsPerPage,
		    pageNumber, radius);
	} else {
	    if (itemsPerPage != null && pageNumber != null) {
		// That's paginated
		criterion = SearchRestriction.with(typeClass, itemsPerPage,
			pageNumber);
	    } else {
		// That's nothing special
		criterion = SearchRestriction.with(typeClass);
	    }
	}

	// Add children
	if (children != null) {
	    for (final ApisCriterion child : children) {
		criterion.addCriterion(child);
	    }
	}

	return criterion;
    }
}
