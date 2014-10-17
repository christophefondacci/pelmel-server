package com.nextep.statistics.dao.impl;

import java.util.List;

import com.nextep.cal.util.model.CalDao;
import com.nextep.cal.util.model.Statistic;
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
}
