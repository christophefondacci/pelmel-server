package com.nextep.statistic.model;

import java.util.Date;

import com.videopolis.calm.model.ItemKey;

public interface MutableItemView extends ItemView {

	void setViewDate(Date date);

	void setViewedItemKey(ItemKey viewedItemKey);

	void setViewerItemKey(ItemKey viewerItemKey);

	void setViewType(String viewType);
}
