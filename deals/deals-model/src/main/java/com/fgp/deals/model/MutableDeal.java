package com.fgp.deals.model;

import java.util.Date;

import com.videopolis.calm.model.ItemKey;

public interface MutableDeal extends Deal {

	/**
	 * Attaches the given item key to this deal
	 * 
	 * @param itemKey
	 *            the {@link ItemKey} of the element on which this deal is
	 *            active
	 */
	void setRelatedItemKey(ItemKey itemKey);

	/**
	 * Defines the start date of the deal (the first creation)
	 * 
	 * @param startDate
	 *            the deal's start date
	 */
	void setStartDate(Date startDate);

	/**
	 * Defines the type of deal
	 * 
	 * @param dealType
	 *            the type of deal as a {@link DealType}
	 */
	void setDealType(DealType dealType);

	/**
	 * Defines the state of this deal
	 * 
	 * @param dealStatus
	 *            the {@link DealStatus} to set
	 */
	void setStatus(DealStatus dealStatus);
}
