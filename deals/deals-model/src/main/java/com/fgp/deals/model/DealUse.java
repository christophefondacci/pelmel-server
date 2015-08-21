package com.fgp.deals.model;

import java.util.Date;

import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;

public interface DealUse extends CalmObject {

	String CAL_ID = "DUSE";

	Deal getDeal();

	ItemKey getConsumerItemKey();

	Date getUseDay();

	Date getUseTime();
}
