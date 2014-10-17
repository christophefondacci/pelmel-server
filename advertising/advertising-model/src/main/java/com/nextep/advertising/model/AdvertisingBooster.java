package com.nextep.advertising.model;

import java.util.Date;

import com.nextep.cal.util.model.Price;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;

public interface AdvertisingBooster extends CalmObject {

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
	 * @return the {@link BoosterType}
	 */
	BoosterType getBoosterType();

}
