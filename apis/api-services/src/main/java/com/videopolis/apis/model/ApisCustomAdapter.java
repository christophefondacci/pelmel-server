package com.videopolis.apis.model;

import java.util.List;

import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;

public interface ApisCustomAdapter {

	List<ItemKey> adapt(ApisContext context, CalmObject... parents);
}
