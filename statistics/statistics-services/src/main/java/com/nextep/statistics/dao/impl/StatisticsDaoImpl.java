package com.nextep.statistics.dao.impl;

import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.nextep.cal.util.helpers.CalHelper;
import com.nextep.cal.util.model.Statistic;
import com.nextep.cal.util.model.base.AbstractCalDao;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;

public class StatisticsDaoImpl extends AbstractCalDao<Statistic> implements
		StatisticsDao {

	@PersistenceContext(unitName = "nextep-statistics")
	private EntityManager entityManager;

	@Override
	public Statistic getById(long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Statistic> getItemsFor(ItemKey key) {
		return entityManager
				.createQuery(
						"from StatisticImpl where itemKey=:itemKey and viewType='OVERVIEW'")
				.setParameter("itemKey", key.toString()).getResultList();
	}

	@Override
	public List<Statistic> getStatisticsFor(List<ItemKey> itemKeys) {
		final Collection<String> keys = CalHelper.unwrapItemKeys(itemKeys);

		return entityManager
				.createQuery(
						"from StatisticImpl where itemKeys in (:itemKeys) and viewType='OVERVIEW'")
				.setParameter("itemKeys", keys).getResultList();
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
