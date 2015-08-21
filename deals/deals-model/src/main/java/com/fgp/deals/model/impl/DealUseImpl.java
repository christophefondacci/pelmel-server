package com.fgp.deals.model.impl;

import java.util.Calendar;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.fgp.deals.model.Deal;
import com.fgp.deals.model.MutableDealUse;
import com.nextep.cal.util.helpers.CalHelper;
import com.videopolis.calm.base.AbstractCalmObject;
import com.videopolis.calm.exception.CalException;
import com.videopolis.calm.factory.CalmFactory;
import com.videopolis.calm.model.ItemKey;

@Entity
@Table(name = "DEAL_USES")
public class DealUseImpl extends AbstractCalmObject implements MutableDealUse {

	private static final long serialVersionUID = 1L;
	private static final Log LOGGER = LogFactory.getLog(DealUseImpl.class);

	@Id
	@Column(name = "DLUSE_ID")
	private long dealUseId;

	@JoinColumn(name = "DEAL_ID")
	@ManyToOne(targetEntity = DealImpl.class, fetch = FetchType.EAGER)
	private Deal deal;

	@Column(name = "CONSUMER_ITEM_KEY")
	private String consumerItemKey;

	@Column(name = "USE_DAY")
	private Date useDay;

	@Column(name = "USE_TIME")
	private Date useTime;

	public DealUseImpl() {
		super(null);
	}

	public DealUseImpl(Deal deal, ItemKey consumerItemKey, Date useTime) {
		this();
		this.deal = deal;
		this.consumerItemKey = consumerItemKey.toString();
		this.useTime = useTime;
		final Calendar cal = Calendar.getInstance();
		cal.setTime(useTime);
		cal.set(Calendar.HOUR, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		useDay = cal.getTime();
	}

	@Override
	public ItemKey getKey() {
		return CalHelper.getItemKeyFromId(CAL_ID, dealUseId);
	}

	@Override
	public Deal getDeal() {
		return deal;
	}

	@Override
	public void setDeal(Deal deal) {
		this.deal = deal;
	}

	@Override
	public ItemKey getConsumerItemKey() {
		try {
			return consumerItemKey == null ? null : CalmFactory.parseKey(consumerItemKey);
		} catch (CalException e) {
			LOGGER.error("Unable to parse consumerItemKey '" + consumerItemKey + "': " + e.getMessage(), e);
		}
		return null;
	}

	@Override
	public void setConsumerItemKey(ItemKey itemKey) {
		this.consumerItemKey = itemKey == null ? null : itemKey.toString();
	}

	@Override
	public Date getUseDay() {
		return useDay;
	}

	@Override
	public void setUseDay(Date usageDay) {
		this.useDay = usageDay;
	}

	@Override
	public Date getUseTime() {
		return useTime;
	}

	@Override
	public void setUseTime(Date usageTime) {
		this.useTime = usageTime;
	}

}
