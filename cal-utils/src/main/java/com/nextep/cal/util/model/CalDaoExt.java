package com.nextep.cal.util.model;

import java.util.List;

import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.RequestType;

public interface CalDaoExt<T extends CalmObject> extends CalDao<T> {

	List<T> listItems(RequestType requestType, Integer pageSize,
			Integer pageOffset);

	int getCount();
}
