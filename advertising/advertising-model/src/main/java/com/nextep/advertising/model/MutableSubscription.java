package com.nextep.advertising.model;

import java.util.Date;

import com.nextep.cal.util.model.Price;
import com.videopolis.calm.model.ItemKey;

/**
 * This interface exposes setters methods that can update an
 * {@link Subscription} bean
 * 
 * @author cfondacci
 * 
 */
public interface MutableSubscription extends Subscription {

	/**
	 * Updates the item key of the element being boosted
	 * 
	 * @param itemKey
	 *            the {@link ItemKey} of the boosted item
	 */
	void setRelatedItemKey(ItemKey itemKey);

	/**
	 * Defines the start date of the boost period
	 * 
	 * @param startDate
	 *            start {@link Date} of boost period
	 */
	void setStartDate(Date startDate);

	/**
	 * Defines the end date of the boost period
	 * 
	 * @param endDate
	 *            end {@link Date} of boost period
	 */
	void setEndDate(Date endDate);

	/**
	 * Defines the value of the boost
	 * 
	 * @param boostValue
	 */
	void setBoostValue(int boostValue);

	/**
	 * Defines the price which the user paid to activate this booster
	 * 
	 * @param price
	 *            the paid {@link Price}
	 */
	void setPrice(Price price);

	/**
	 * Defines the item key of the user who paid this booster
	 * 
	 * @param purchaserKey
	 *            the {@link ItemKey} of the buyer
	 */
	void setPurchaserItemKey(ItemKey purchaserKey);

	/**
	 * Defines the type of booster
	 * 
	 * @param boosterType
	 *            the new {@link SubscriptionType}
	 */
	void setSubscriptionType(SubscriptionType boosterType);

	/**
	 * Defines the transaction ID of the first transaction of the subscription.
	 * 
	 * @param originalTransactionId
	 *            the transaction ID of the first payment for the subscription
	 */
	void setOriginalTransactionId(String originalTransactionId);

	/**
	 * Defines the transaction ID of the payment of this period. Globally unique
	 * 
	 * @param transactionId
	 *            the transaction ID.
	 */
	void setTransactionId(String transactionId);

	/**
	 * Sets the date of the last announcement from this account.
	 * 
	 * @param lastAnnouncementDate
	 *            the date of the last sent announcement
	 */
	void setLastAnnouncementDate(Date lastAnnouncementDate);

	/**
	 * Sets the item key of the element which led to this subscription and who
	 * may therefore receive compensation.
	 * 
	 * @param itemKey
	 *            the {@link ItemKey} of the referrer of this subscription
	 */
	void setReferrerItemKey(ItemKey itemKey);
}
