package com.fgp.deals.model;

import java.util.Date;

import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;

/**
 * This interface represents a deal
 * 
 * @author cfondacci
 *
 */
public interface Deal extends CalmObject {

	String CAL_ID = "DEAL";

	/**
	 * The {@link ItemKey} of the CAL object that this deal is for
	 * 
	 * @return the {@link ItemKey} of the CAL object to which this deal is
	 *         bonded
	 */
	ItemKey getRelatedItemKey();

	/**
	 * Provides the start date of that deal (the date when it has been created)
	 * 
	 * @return the deal start date
	 */
	Date getStartDate();

	/**
	 * Provides the type of deal
	 * 
	 * @return the {@link DealType}
	 */
	DealType getDealType();

	/**
	 * The status of this deal (whether it is running or not)
	 * 
	 * @return the {@link DealStatus}
	 */
	DealStatus getStatus();

	/**
	 * Provides the maximum number of deals per day
	 * 
	 * @return the maximum number of deal uses allowed, or <code>null</code> for
	 *         no limit
	 */
	Integer getMaxDealUses();
}
