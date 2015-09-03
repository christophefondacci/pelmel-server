package com.fgp.deals.model.impl;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.fgp.deals.model.DealStatus;
import com.fgp.deals.model.DealType;
import com.fgp.deals.model.MutableDeal;
import com.nextep.cal.util.helpers.CalHelper;
import com.videopolis.calm.base.AbstractCalmObject;
import com.videopolis.calm.exception.CalException;
import com.videopolis.calm.factory.CalmFactory;
import com.videopolis.calm.model.ItemKey;

/**
 * Default implementation of a deal
 * 
 * @author cfondacci
 *
 */
@Entity
@Table(name = "DEALS")
public class DealImpl extends AbstractCalmObject implements MutableDeal {

	private static final Log LOGGER = LogFactory.getLog(DealImpl.class);
	private static final long serialVersionUID = 1L;

	@GeneratedValue
	@Id
	@Column(name = "DEAL_ID")
	private long id;
	@Column(name = "ITEM_KEY")
	private String relatedItemKey;
	@Column(name = "START_DATE")
	private Date startDate;
	@Column(name = "DEAL_TYPE")
	private String dealType;
	@Column(name = "STATUS")
	private String status;
	@Column(name = "MAX_DEALS_PER_DAY")
	private Integer maxDealUses;

	public DealImpl() {
		super(null);
	}

	@Override
	public ItemKey getKey() {
		return CalHelper.getItemKeyFromId(CAL_ID, id);
	}

	@Override
	public ItemKey getRelatedItemKey() {
		try {
			if (relatedItemKey == null) {
				return null;
			} else {
				return CalmFactory.parseKey(relatedItemKey);
			}
		} catch (CalException e) {
			LOGGER.error("Unable to parse deal key '" + relatedItemKey + "': " + e.getMessage(), e);
			return null;
		}
	}

	@Override
	public Date getStartDate() {
		return startDate;
	}

	@Override
	public DealType getDealType() {
		try {
			return DealType.valueOf(dealType);
		} catch (IllegalArgumentException e) {
			LOGGER.error("Invalid deal type '" + dealType + "', using default " + DealType.TWO_FOR_ONE.name());
			return DealType.TWO_FOR_ONE;
		}
	}

	@Override
	public DealStatus getStatus() {
		try {
			return DealStatus.valueOf(status);
		} catch (IllegalArgumentException e) {
			LOGGER.error("Invalid deal status '" + status + "', using default " + DealStatus.RUNNING.name());
			return DealStatus.RUNNING;
		}
	}

	@Override
	public void setRelatedItemKey(ItemKey itemKey) {
		this.relatedItemKey = itemKey == null ? null : itemKey.toString();
	}

	@Override
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	@Override
	public void setDealType(DealType dealType) {
		this.dealType = dealType == null ? null : dealType.name();
	}

	@Override
	public void setStatus(DealStatus dealStatus) {
		this.status = dealStatus == null ? null : dealStatus.name();
	}

	@Override
	public void setMaxDealUses(Integer maxDealUses) {
		this.maxDealUses = maxDealUses;
	}

	@Override
	public Integer getMaxDealUses() {
		return maxDealUses;
	}
}
