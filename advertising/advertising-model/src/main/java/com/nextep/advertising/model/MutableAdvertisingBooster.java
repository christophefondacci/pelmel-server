package com.nextep.advertising.model;

import java.util.Date;

import com.nextep.cal.util.model.Price;
import com.videopolis.calm.model.ItemKey;

/**
 * This interface exposes setters methods that can update an
 * {@link AdvertisingBooster} bean
 * 
 * @author cfondacci
 * 
 */
public interface MutableAdvertisingBooster extends AdvertisingBooster {

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
	 *            the new {@link BoosterType}
	 */
	void setBoosterType(BoosterType boosterType);
}
