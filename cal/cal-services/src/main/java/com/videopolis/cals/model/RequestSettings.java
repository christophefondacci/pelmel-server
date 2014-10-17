package com.videopolis.cals.model;

import java.io.Serializable;
import java.util.List;

import com.videopolis.calm.model.Sorter;

/**
 * <p>
 * Settings used to parametrize a CAL request.
 * </p>
 * <p>
 * This interface defines a sort criterion and a sort order to define how the
 * results must be sorted
 * </p>
 * 
 * @author julien
 * 
 */
public interface RequestSettings extends Serializable {

    /**
     * If the results must be sorted in ascendent order (equivalent to SQL ORDER
     * BY ASC)
     */
    @Deprecated
    byte ASCENDING_ORDER = Sorter.Order.ASCENDING.getCompatibleValue();

    /**
     * If the results must be sorted in descendent order (equivalent to SQL
     * ORDER BY DESC)
     */
    @Deprecated
    byte DESCENDING_ORDER = Sorter.Order.DESCENDING.getCompatibleValue();

    /**
     * Returns the sorting criterion, which may be, for example, the name of the
     * data column to sort by.
     * 
     * @return Sorting criterion
     */
    @Deprecated
    String getSortCriterion();

    /**
     * Returns the sorting order, which may be {@code ASCENDING_ORDER} or
     * {@code DESCENDING_ORDER}
     * 
     * @return
     */
    @Deprecated
    byte getSortOrder();

    List<? extends Sorter> getSortParameters();
}
