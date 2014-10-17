package com.nextep.descriptions.model;

import java.util.Date;
import java.util.Locale;

import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;

public interface Description extends CalmObject {

	String CAL_TYPE = "DESC";

	String getDescription();

	Date getDate();

	Locale getLocale();

	ItemKey getDescribedItemKey();

	int getSourceId();

}
