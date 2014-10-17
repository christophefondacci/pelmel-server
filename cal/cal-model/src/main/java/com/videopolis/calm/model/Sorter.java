package com.videopolis.calm.model;

/**
 * An interface representing information about sorting data
 * 
 * @author julien
 * 
 */
public interface Sorter {

    /**
     * Represents the sort order
     * 
     * @author julien
     * 
     */
    enum Order {
	/** Ascending order */
	ASCENDING((byte) 0),

	/** Descending order */
	DESCENDING((byte) 1);

	/** Value used for compatibility reasons */
	@Deprecated
	private final byte compatibleValue;

	/**
	 * Default constructor
	 * 
	 * @param compatibleValue
	 *            Value used for compatibility with old sorters
	 */
	private Order(final byte compatibleValue) {
	    this.compatibleValue = compatibleValue;
	}

	/**
	 * Returns the value used to represent this sorter in the old
	 * implementation
	 * 
	 * @return Value
	 */
	@Deprecated
	public byte getCompatibleValue() {
	    return compatibleValue;
	}

	/**
	 * For an old (deprecated) numeric value, returns the corresponding
	 * sorter.
	 * 
	 * @param compatibleValue
	 *            Value
	 * @return Sorter
	 * @throws IllegalArgumentException
	 *             If this value is unknown
	 */
	@Deprecated
	public static Order forCompatibleValue(final byte compatibleValue) {
	    for (final Order order : values()) {
		if (order.getCompatibleValue() == compatibleValue) {
		    return order;
		}
	    }
	    throw new IllegalArgumentException(
		    "Invalid order compatible value: " + compatibleValue);
	}
    }

    /**
     * Returns the sort criterion
     * 
     * @return Criterion
     */
    String getCriterion();

    /**
     * Returns the sort order
     * 
     * @return Order
     */
    Order getOrder();
}
