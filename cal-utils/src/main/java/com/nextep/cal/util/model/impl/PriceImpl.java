package com.nextep.cal.util.model.impl;

import com.nextep.cal.util.model.Price;

/**
 * Default price implementation
 * 
 * @author cfondacci
 * 
 */
public class PriceImpl implements Price {

	private final double price;
	private final String currency;

	/**
	 * Builds a new price from value and currency
	 * 
	 * @param price
	 *            price value
	 * @param currency
	 *            currency in which the value is expressed.
	 */
	public PriceImpl(double price, String currency) {
		this.price = price;
		this.currency = currency;
	}

	@Override
	public double getPrice() {
		return price;
	}

	@Override
	public String getCurrency() {
		return currency;
	}
}
