package com.nextep.proto.blocks;

import java.util.List;
import java.util.Locale;

import com.nextep.cal.util.model.Price;
import com.nextep.proto.services.UrlService;
import com.nextep.users.model.User;

public interface PaymentSupport {

	/**
	 * Initializes the payment support
	 * 
	 * @param locale
	 *            current {@link Locale}
	 * @param urlService
	 *            the current {@link UrlService}
	 * @param currentUser
	 *            the current {@link User}
	 */
	void initialize(Locale locale, UrlService urlService, User currentUser);

	/**
	 * Provides the payment URL
	 * 
	 * @return the payment URL
	 */
	String getPaymentUrl();

	/**
	 * Provides the list of available amounts that the user can choose
	 * 
	 * @return the list of prices
	 */
	List<Price> getAvailableAmounts();

	/**
	 * Provides the label of a given price
	 * 
	 * @param price
	 *            the {@link Price} to get a label for
	 * @return the price label
	 */
	String getPriceLabel(Price price);

	String getPriceOptionValue(Price price);

	/**
	 * The URL where the user returns when a payment succeeds
	 * 
	 * @return the return success URL
	 */
	String getReturnUrl();

	/**
	 * The URL where the user returns when a payment fails
	 * 
	 * @return the return error URL
	 */
	String getErrorUrl();

	/**
	 * The URL to receive notifications sent by the payment server backend
	 * 
	 * @return the local notification URL
	 */
	String getNotifyUrl();

	/**
	 * The business identifier which identifies ourselves in the payment backend
	 * 
	 * @return our business ID
	 */
	String getBusinessId();

	/**
	 * The identifier of the buyer in our system so that we can automatically
	 * credit his account when the payment succeeds
	 * 
	 * @return buyer's unique ID
	 */
	String getBuyerId();

	/**
	 * Provides the label indicating the credits of the current user
	 * 
	 * @return the number of credits of the current user
	 */
	String getCreditsLabel();

	/**
	 * The preferred language to use in the payment backend
	 * 
	 * @return the 2 letter language ISO code
	 */
	String getLanguage();
}
