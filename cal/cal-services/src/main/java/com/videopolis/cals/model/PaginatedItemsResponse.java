package com.videopolis.cals.model;

import com.videopolis.cals.service.CalService;

/**
 * This interface describes the response of the
 * {@link CalService#getPaginatedItemsFor(com.videopolis.calm.model.ItemKey, String, int, int)}
 * method. Since when a user asks for paginated information he will generally
 * need the total number of pages and optionally the total number of elements,
 * this response allows implementors to "hold" this information along with the
 * results.
 *
 * @see ItemsResponse
 * @author Christophe Fondacci
 *
 */
public interface PaginatedItemsResponse extends ItemsResponse, PaginationInfo {

}
