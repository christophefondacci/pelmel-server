package com.nextep.properties.dao.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.nextep.cal.util.model.base.AbstractCalDao;
import com.nextep.properties.dao.PropertiesDao;
import com.nextep.properties.model.Property;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;

public class PropertiesDaoImpl extends AbstractCalDao<Property> implements
		PropertiesDao {

	@PersistenceContext(unitName = "nextep-properties")
	private EntityManager entityManager;

	@Override
	public Property getById(long id) {
		return (Property) entityManager
				.createQuery("from PropertyImpl where id=:id")
				.setParameter("id", id).getSingleResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Property> getItemsFor(ItemKey key) {
		return entityManager
				.createQuery(
						"from PropertyImpl where parentItemKey=:parentItemKey")
				.setParameter("parentItemKey", key.toString()).getResultList();
	}

	@Override
	public void save(CalmObject object) {
		if (object.getKey() == null) {
			entityManager.persist(object);
		} else {
			entityManager.merge(object);
		}
	}

	@Override
	public void clearProperties(ItemKey parentKey) {
		entityManager
				.createNativeQuery(
						"delete from ITEM_PROPERTIES where ITEM_KEY=:itemKey")
				.setParameter("itemKey", parentKey.toString()).executeUpdate();
	}
}
