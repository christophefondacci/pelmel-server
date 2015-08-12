package com.nextep.advertising.model.impl;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.nextep.advertising.model.SubscriptionType;
import com.nextep.advertising.model.MutableSubscription;
import com.nextep.cal.util.helpers.CalHelper;
import com.nextep.cal.util.model.Price;
import com.nextep.cal.util.model.impl.PriceImpl;
import com.videopolis.calm.base.AbstractCalmObject;
import com.videopolis.calm.exception.CalException;
import com.videopolis.calm.factory.CalmFactory;
import com.videopolis.calm.model.ItemKey;

@Entity
@Table(name = "ADS_BOOSTERS")
public class SubscriptionImpl extends AbstractCalmObject implements MutableSubscription {

	private static final long serialVersionUID = 4554744743456518505L;

	private static final Log LOGGER = LogFactory.getLog(SubscriptionImpl.class);

	@GeneratedValue
	@Id
	@Column(name = "BOOSTER_ID")
	private long id;

	@Column(name = "ITEM_KEY")
	private String relatedItemKey;

	@Column(name = "FROM_DATE")
	private Date fromDate;

	@Column(name = "TO_DATE")
	private Date toDate;

	@Column(name = "BOOST_VALUE")
	private int boostValue;

	@Column(name = "BOOST_PRICE")
	private double priceValue;

	@Column(name = "BOOST_CURRENCY")
	private String priceCurrency;

	@Column(name = "PURCHASER_USER_KEY")
	private String purchaserItemKey;

	@Column(name = "BOOSTER_TYPE")
	private String boosterType;

	@Column(name = "ORIGINAL_TRANSACTION_ID")
	private String originalTransactionId;

	@Column(name = "TRANSACTION_ID")
	private String transactionId;

	public SubscriptionImpl() {
		super(null);
	}

	@Override
	public ItemKey getKey() {
		return CalHelper.getItemKeyFromId(CAL_ID, id);
	}

	@Override
	public ItemKey getRelatedItemKey() {
		try {
			return CalmFactory.parseKey(relatedItemKey);
		} catch (CalException e) {
			LOGGER.error("Unable to parse related item key " + relatedItemKey + " : " + e.getMessage(), e);
		}
		return null;
	}

	@Override
	public Date getStartDate() {
		return fromDate;
	}

	@Override
	public Date getEndDate() {
		return toDate;
	}

	@Override
	public int getBoostValue() {
		return boostValue;
	}

	@Override
	public Price getPrice() {
		return new PriceImpl(priceValue, priceCurrency);
	}

	@Override
	public ItemKey getPurchaserItemKey() {
		try {
			return CalmFactory.parseKey(purchaserItemKey);
		} catch (CalException e) {
			LOGGER.error("Unable to parse purchaser item key " + purchaserItemKey + " : " + e.getMessage(), e);
		}
		return null;
	}

	@Override
	public SubscriptionType getSubscriptionType() {
		try {
			return SubscriptionType.valueOf(boosterType);
		} catch (IllegalArgumentException e) {
			LOGGER.error("Invalid booster type retrieved from database '" + boosterType + "', fallbacking to DEFAULT");
			return SubscriptionType.DEFAULT;
		} catch (NullPointerException e) {
			return SubscriptionType.DEFAULT;
		}
	}

	@Override
	public void setSubscriptionType(SubscriptionType boosterType) {
		this.boosterType = boosterType == null ? null : boosterType.name();
	}

	@Override
	public void setBoostValue(int boostValue) {
		this.boostValue = boostValue;
	}

	@Override
	public void setEndDate(Date endDate) {
		this.toDate = endDate;
	}

	@Override
	public void setPrice(Price price) {
		this.priceCurrency = price.getCurrency();
		this.priceValue = price.getPrice();
	}

	@Override
	public void setPurchaserItemKey(ItemKey purchaserKey) {
		this.purchaserItemKey = purchaserKey.toString();
	}

	@Override
	public void setRelatedItemKey(ItemKey itemKey) {
		this.relatedItemKey = itemKey.toString();
	}

	@Override
	public void setStartDate(Date startDate) {
		this.fromDate = startDate;
	}

	@Override
	public void setOriginalTransactionId(String originalTransactionId) {
		this.originalTransactionId = originalTransactionId;
	}

	@Override
	public String getOriginalTransactionId() {
		return originalTransactionId;
	}

	@Override
	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	@Override
	public String getTransactionId() {
		return transactionId;
	}
}
