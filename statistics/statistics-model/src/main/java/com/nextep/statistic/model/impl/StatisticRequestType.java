package com.nextep.statistic.model.impl;

import com.videopolis.calm.model.RequestType;

public class StatisticRequestType implements RequestType {

	private static final long serialVersionUID = 1L;
	private StatisticPeriod period;

	private StatisticRequestType(StatisticPeriod period) {
		this.period = period;
	}

	public StatisticPeriod getPeriod() {
		return period;
	}

	public static StatisticRequestType fromDaysPeriod(int days) {
		return new StatisticRequestType(StatisticPeriod.fromDays(days));
	}
}
