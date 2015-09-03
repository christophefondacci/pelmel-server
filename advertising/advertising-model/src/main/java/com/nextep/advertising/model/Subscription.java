package com.nextep.advertising.model;

import java.util.Date;

import com.nextep.cal.util.model.Price;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;

public interface Subscription extends CalmObject {

	static String CAL_ID = "BOST";

	/**
	 * Item key of the element being boost
	 * 
	 * @return the {@link ItemKey}
	 */
	ItemKey getRelatedItemKey();

	/**
	 * Start date of the boost period
	 * 
	 * @return the start {@link Date}
	 */
	Date getStartDate();

	/**
	 * End date of the boost period
	 * 
	 * @return the end {@link Date}
	 */
	Date getEndDate();

	/**
	 * The value of boost. The highest boost value gets priority over lower
	 * values inside a same nearby radius
	 * 
	 * @return the boost value
	 */
	int getBoostValue();

	/**
	 * The price paid for this boost
	 * 
	 * @return the boost price
	 */
	Price getPrice();

	/**
	 * Unique key of the purchaser of this booster
	 * 
	 * @return the purchaser's {@link ItemKey}
	 */
	ItemKey getPurchaserItemKey();

	/**
	 * Informs about the type of booster
	 * 
	 * @return the {@link SubscriptionType}
	 */
	SubscriptionType getSubscriptionType();

	/**
	 * Transaction ID of the original payment of auto-renewed subscription. Will
	 * be the same for all entities generated by the renewal of the same
	 * subscription.
	 * 
	 * @return the original transaction identifier
	 */
	String getOriginalTransactionId();

	/**
	 * Transaction ID of the payment of this period
	 * 
	 * @return the transaction ID for this period
	 */
	String getTransactionId();

	/**
	 * The last time when an announcement has been sent from this subscription.
	 * This information is used to limit the number of announcements sent by a
	 * same account.
	 * 
	 * @return the date of the last announcement made from this account
	 */
	Date getLastAnnouncementDate();
}
