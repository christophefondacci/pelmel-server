package com.nextep.statistics.dao.impl;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import com.nextep.cal.util.helpers.CalHelper;
import com.nextep.cal.util.model.Statistic;
import com.nextep.cal.util.model.base.AbstractCalDao;
import com.nextep.statistic.model.ItemView;
import com.nextep.statistic.model.impl.ItemViewImpl;
import com.nextep.statistic.model.impl.StatisticPeriod;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;

public class StatisticsDaoImpl extends AbstractCalDao<Statistic>implements StatisticsDao {

	@PersistenceContext(unitName = "nextep-statistics")
	private EntityManager entityManager;

	@Override
	public Statistic getById(long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Statistic> getItemsFor(ItemKey key) {
		return entityManager.createQuery("from StatisticImpl where itemKey=:itemKey and viewType='OVERVIEW'")
				.setParameter("itemKey", key.toString()).getResultList();
	}

	@Override
	public List<Statistic> getStatisticsFor(List<ItemKey> itemKeys) {
		final Collection<String> keys = CalHelper.unwrapItemKeys(itemKeys);

		return entityManager.createQuery("from StatisticImpl where itemKeys in (:itemKeys) and viewType='OVERVIEW'")
				.setParameter("itemKeys", keys).getResultList();
	}

	@Override
	public List<ItemView> getReportFor(ItemKey itemKey, StatisticPeriod period) {
		Query query = null;
		query = entityManager.createNativeQuery(
				"select DAte(view_date) as view_day, view_type, count(distinct item_key_viewer) from STAT_VIEWS"
						+ " where ITEM_KEY_VIEWED=:itemKey and view_date>date_add(now(),interval -"
						+ period.getRangeDuration() + " day) and  ITEM_KEY_VIEWER is not null"
						+ " group by view_day, view_type;")
				.setParameter("itemKey", itemKey.toString());
		final List<ItemView> items = new ArrayList<>();
		if (query != null) {
			List<Object[]> results = query.getResultList();
			for (Object[] result : results) {
				final Date day = (Date) result[0];
				final String viewType = (String) result[1];
				final BigInteger viewCount = (BigInteger) result[2];

				final ItemViewImpl itemView = new ItemViewImpl();
				itemView.setViewDate(new Date(day.getTime()));
				itemView.setViewedItemKey(itemKey);
				itemView.setViewType(viewType == null ? "VIEW" : viewType);
				itemView.setCount(viewCount.intValue());
				items.add(itemView);
			}
		}
		return items;
	}

	@Override
	public void save(CalmObject object) {
		if (object.getKey() == null) {
			entityManager.persist(object);
		} else {
			entityManager.merge(object);
		}
	}
}
