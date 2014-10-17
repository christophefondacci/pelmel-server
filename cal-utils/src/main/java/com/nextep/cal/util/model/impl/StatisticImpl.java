package com.nextep.cal.util.model.impl;

import com.nextep.cal.util.model.Statistic;
import com.videopolis.calm.base.AbstractCalmObject;

public class StatisticImpl extends AbstractCalmObject implements Statistic {

	private static final long serialVersionUID = 1951274397024088066L;
	private int count = 0;

	public StatisticImpl() {
		super(null);
	}

	@Override
	public void setCount(int count) {
		this.count = count;
	}

	@Override
	public int getCount() {
		return count;
	}
}
