package com.nextep.advertising.model;

import java.util.Date;

import com.nextep.cal.util.model.Price;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;

public interface Payment extends CalmObject {

	String CAL_ID = "PYMT";
	String CAL_TRANSACTION_ID = "TRNS";

	String getTransactionId();

	Date getDate();

	String getPayerEmail();

	ItemKey getUserKey();

	Price getPrice();

	String getStatus();
}
