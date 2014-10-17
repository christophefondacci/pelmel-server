package com.nextep.cal.util.model;

/**
 * This interface represents a price.
 * 
 * @author cfondacci
 * 
 */
public interface Price {

	/**
	 * Price value
	 * 
	 * @return the price value
	 */
	double getPrice();

	/**
	 * Price currency
	 * 
	 * @return the currency in ISO-3 letters code
	 */
	String getCurrency();
}
