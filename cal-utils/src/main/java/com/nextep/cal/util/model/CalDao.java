package com.nextep.cal.util.model;

import java.util.List;

import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.calm.model.RequestType;
import com.videopolis.cals.model.RequestSettings;

public interface CalDao<T extends CalmObject> {

	T getById(long id);

	List<T> getByIds(List<Long> idList);

	List<T> getItemsFor(ItemKey key);

	List<T> getItemsFor(ItemKey key, int resultsPerPage, int pageOffset);

	List<T> listItems(RequestType requestType, RequestSettings requestSettings);

	void save(CalmObject object);
}
