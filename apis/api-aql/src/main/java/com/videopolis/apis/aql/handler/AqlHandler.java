package com.videopolis.apis.aql.handler;

import com.videopolis.apis.aql.exception.QueryParsingException;

/**
 * An handler for an AQL Query. Handler are used during the parsing of AQL
 * queries.
 * 
 * Similarily to SAX, when a particular event is encountered during the parsing,
 * the corresponding method on the handler is called.
 * 
 * All methods may throw a {@link QueryParsingException} if something goes
 * wrong.
 * 
 * @author julien
 * 
 */
public interface AqlHandler {

    /**
     * Called when the {@code get} keyword is found: {@code get type}
     * 
     * @param type
     *            The item type
     */
    void beginGet(String type);

    /**
     * Called at the end of a {@code get} block.
     */
    void endGet();

    /**
     * Called when an {@code unique key} clause is found:
     * {@code unique key itemId}
     * 
     * @param itemId
     *            The given item ID
     */
    void uniqueKeySelector(String itemId);

    /**
     * Called when an {@code alternate key} clause is found:
     * {@code alternate key itemKey}.
     * 
     * The {@code itemKey} is parsed and split in two parts: {@code itemType}
     * and {@code itemId}.
     * 
     * @param itemType
     *            Item type
     * @param itemId
     *            Item ID
     */
    void alternateKeySelector(String itemType, String itemId);

    /**
     * Called at the beginning of a {@code with} block (when the {@code with}
     * keyword is found}.
     * 
     * A {@code with} block contain one ore more criteria which are handled
     * separately.
     * 
     */
    void beginWith();

    /**
     * Called at the end of a {@code with} block.
     */
    void endWith();

    /**
     * Called at the beginning of a {@code with} criterion.
     * 
     * A criterion contains:
     * <ul>
     * <li>A simple with (e.g. {@code with type})</li>
     * <li>or a geographic with ({@code with nearest ...})</li>
     * <li>Eventually a nested {@code with} clause</li>
     * </ul>
     */
    void beginWithCriterion();

    /**
     * Called when a {@code with nearest} clause is found:
     * {@code with nearest type radius radius page X size Y}).
     * 
     * A {@code with nearest} clause is always followed by a pagination clause,
     * so {@link addPagination(int,int)} is called right after this method.
     * 
     * @param type
     *            Type of item
     * @param radius
     *            Radius search
     */
    void addWithNearest(String type, double radius);

    /**
     * Called when a simple {@code with} is found: {@code with type}
     * 
     * @param type
     *            Type
     */
    void addWith(String type);

    /**
     * Called at the end of a with criterion.
     * 
     * After this has been called, no more nested {@code with} block can be
     * added to the criterion.
     * 
     */
    void endWithCriterion();

    /**
     * Called when a pagination clause is added to the latest clause:
     * {@code page pageNumber size itemsPerPage}
     * 
     * @param itemsPerPage
     *            Page size
     * @param pageNumber
     *            Desired page number
     */
    void addPagination(int itemsPerPage, int pageNumber);

    /**
     * Called when a {@code for} clause is found:
     * {@code for itemKey page X size Y}.
     * 
     * The {@code itemKey} is parsed and split in two parts: {@code itemType}
     * and {@code itemId}.
     * 
     * A {@code for} clause is always followed by a pagination clause, so
     * {@link addPagination(int,int)} is called right after this method.
     * 
     * @param itemType
     *            Item type
     * @param itemId
     *            Item id
     */
    void beginFor(String itemType, String itemId);

    /**
     * Called at the end of a {@code for} clause.
     */
    void endFor();
}
