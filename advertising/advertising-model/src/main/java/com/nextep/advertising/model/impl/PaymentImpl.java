package com.nextep.advertising.model.impl;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.nextep.advertising.model.Payment;
import com.nextep.cal.util.helpers.CalHelper;
import com.nextep.cal.util.model.Price;
import com.nextep.cal.util.model.impl.PriceImpl;
import com.videopolis.calm.base.AbstractCalmObject;
import com.videopolis.calm.exception.CalException;
import com.videopolis.calm.factory.CalmFactory;
import com.videopolis.calm.model.ItemKey;

@Entity
@Table(name = "ADS_PAYMENTS")
public class PaymentImpl extends AbstractCalmObject implements Payment {

	private static final long serialVersionUID = 566325962867073975L;
	private static final Log LOGGER = LogFactory.getLog(PaymentImpl.class);

	@GeneratedValue
	@Id
	@Column(name = "PAYMENT_ID")
	private long id;
	@Column(name = "TRANSACTION_ID")
	private String transactionId;
	@Column(name = "PAYMENT_DATE")
	private Date paymentDate;
	@Column(name = "PAYER_EMAIL")
	private String payerEmail;
	@Column(name = "USER_KEY")
	private String userKey;
	@Column(name = "AMOUNT")
	private double priceValue;
	@Column(name = "CURRENCY")
	private String priceCurrency;
	@Column(name = "STATUS")
	private String status;

	public PaymentImpl() {
		super(null);
	}

	@Override
	public ItemKey getKey() {
		return CalHelper.getItemKeyFromId(CAL_ID, id);
	}

	@Override
	public String getTransactionId() {
		return transactionId;
	}

	@Override
	public Date getDate() {
		return paymentDate;
	}

	@Override
	public String getPayerEmail() {
		return payerEmail;
	}

	@Override
	public ItemKey getUserKey() {
		try {
			return CalmFactory.parseKey(userKey);
		} catch (CalException e) {
			LOGGER.error("Unable to build user key '" + userKey
					+ "' of payment " + id + " : " + e.getMessage(), e);
			return null;
		}
	}

	@Override
	public Price getPrice() {
		return new PriceImpl(priceValue, priceCurrency);
	}

	@Override
	public String getStatus() {
		return status;
	}

}
