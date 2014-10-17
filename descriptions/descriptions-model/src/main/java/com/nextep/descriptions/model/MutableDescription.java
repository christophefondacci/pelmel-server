package com.nextep.descriptions.model;

import java.util.Date;
import java.util.Locale;

import com.videopolis.calm.model.ItemKey;

public interface MutableDescription extends Description {

	void setDescription(String description);

	void setDate(Date date);

	void setLocale(Locale l);

	void setDescribedItemKey(ItemKey describedItemKey);

	void setSourceId(int id);
}
