package com.videopolis.apis.model;

import com.videopolis.apis.exception.ApisException;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;

public interface ApisItemKeyAdapter {

    ItemKey getItemKey(CalmObject object) throws ApisException;

}
