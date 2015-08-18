package com.nextep.statistics.dao.impl;

import java.util.List;

import com.nextep.cal.util.model.CalDao;
import com.nextep.cal.util.model.Statistic;
import com.nextep.statistic.model.ItemView;
import com.nextep.statistic.model.impl.StatisticPeriod;
import com.videopolis.calm.model.ItemKey;

public interface StatisticsDao extends CalDao<Statistic> {

	/**
	 * Lists all statistics for the given list of item keys
	 * 
	 * @param itemKeys
	 *            the {@link ItemKey} to retrieve statistics for
	 * @return the list of statistics for those keys
	 */
	List<Statistic> getStatisticsFor(List<ItemKey> itemKeys);

	/**
	 * Provides the report information for the given period as {@link ItemView}
	 * items
	 * 
	 * @param period
	 *            the period
	 * @return the list of {@link ItemView} for the period (month = 30 daily
	 *         entries, day = 24 hours entries, etc)
	 */
	List<ItemView> getReportFor(ItemKey itemKey, StatisticPeriod period);
}
