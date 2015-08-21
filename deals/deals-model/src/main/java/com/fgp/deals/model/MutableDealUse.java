package com.fgp.deals.model;

import java.util.Date;

import com.videopolis.calm.model.ItemKey;

public interface MutableDealUse extends DealUse {

	void setDeal(Deal deal);

	void setConsumerItemKey(ItemKey itemKey);

	void setUseDay(Date useDay);

	void setUseTime(Date useTime);
}
