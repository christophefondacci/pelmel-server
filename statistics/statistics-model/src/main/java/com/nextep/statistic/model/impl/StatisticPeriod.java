package com.nextep.statistic.model.impl;

public enum StatisticPeriod {
	DAY(1, 3600000), WEEK(7, 3600000), MONTH(30, 86400000), TRIMESTER(90, 86400000), SEMESTER(180, 86400000);

	private int rangeDuration;
	private long incTime;

	private StatisticPeriod(int daysDuration, long incTimeMs) {
		this.rangeDuration = daysDuration;
		this.incTime = incTimeMs;
	}

	public int getRangeDuration() {
		return rangeDuration;
	}

	public long getIncrementTime() {
		return incTime;
	}

	public static StatisticPeriod fromDays(int days) {
		for (StatisticPeriod period : StatisticPeriod.values()) {
			if (period.getRangeDuration() == days) {
				return period;
			}
		}
		// Default
		return MONTH;
	}
}