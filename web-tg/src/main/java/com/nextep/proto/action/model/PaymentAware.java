package com.nextep.proto.action.model;

import com.nextep.proto.blocks.PaymentSupport;

/**
 * An interface for actions supporting payments
 * 
 * @author cfondacci
 * 
 */
public interface PaymentAware {

	/**
	 * Retrieves the support implementation for payments
	 * 
	 * @return the {@link PaymentSupport} implementation
	 */
	PaymentSupport getPaymentSupport();

	/**
	 * Installs the payment support implementation
	 * 
	 * @param paymentSupport
	 *            the {@link PaymentSupport} implementation
	 */
	void setPaymentSupport(PaymentSupport paymentSupport);
}
