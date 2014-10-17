package com.nextep.cal.util.model;

import com.videopolis.calm.model.CalmObject;

/**
 * A non-persistent CAL model object that can hold statistic information
 * regarding its parent.
 * 
 * @author cfondacci
 * 
 */
public interface Statistic extends CalmObject {

	int getCount();

	void setCount(int count);
}
