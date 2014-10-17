package com.nextep.descriptions.dao.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import com.nextep.cal.util.helpers.CalHelper;
import com.nextep.cal.util.model.base.AbstractCalDao;
import com.nextep.descriptions.dao.DescriptionDao;
import com.nextep.descriptions.model.Description;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;

public class DescriptionsDaoImpl extends AbstractCalDao<Description> implements
		DescriptionDao {

	@PersistenceContext(unitName = "nextep-descriptions")
	private EntityManager entityManager;

	@Override
	public Description getById(long id) {
		return (Description) entityManager
				.createQuery("from DescriptionImpl where id=:id")
				.setParameter("id", id).getSingleResult();
	}

	@Override
	public List<Description> getByIds(List<Long> idList) {
		return entityManager
				.createQuery("from DescriptionImpl where id in (:id)")
				.setParameter("id", idList).getResultList();
	}

	@Override
	public Map<ItemKey, List<Description>> getDescriptionsFor(
			List<ItemKey> itemKeys, Locale locale, boolean oneDescPerKey) {
		final List<String> itemKeysStr = new ArrayList<String>();
		for (ItemKey itemKey : itemKeys) {
			itemKeysStr.add(itemKey.toString());
		}
		List<Description> descriptions = null;
		if (locale != null) {
			descriptions = entityManager
					.createQuery(
							"from DescriptionImpl d where describedItemKey in (:itemKeys) order by describedItemKey, case d.languageIso6391 when :languageIso6391 then 1 when 'en' then 2 else 3 end asc")
					.setParameter("languageIso6391", locale.getLanguage())
					.setParameter("itemKeys", itemKeysStr).getResultList();
		} else {
			descriptions = entityManager
					.createQuery(
							"from DescriptionImpl d where describedItemKey in (:itemKeys) order by describedItemKey")
					.setParameter("itemKeys", itemKeysStr).getResultList();
		}
		final Map<ItemKey, List<Description>> descKeyMap = new HashMap<ItemKey, List<Description>>();
		for (Description d : descriptions) {
			List<Description> descsForKey = descKeyMap.get(d
					.getDescribedItemKey());
			// Initializing list
			if (descsForKey == null) {
				descsForKey = new ArrayList<Description>();
				descKeyMap.put(d.getDescribedItemKey(), descsForKey);
			} else if (oneDescPerKey) {
				// If we only want 1 description per key we already have it so
				// we skip this one
				continue;
			}
			descsForKey.add(d);
		}
		return descKeyMap;
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
	public void clearDescriptions(ItemKey parentKey, ItemKey... descriptionKeys) {
		if (descriptionKeys == null || descriptionKeys.length == 0) {
			entityManager
					.createNativeQuery(
							"delete from DESCRIPTIONS where ITEM_KEY=:itemKey")
					.setParameter("itemKey", parentKey.toString())
					.executeUpdate();
		} else {
			List<ItemKey> keysList = Arrays.asList(descriptionKeys);
			Collection<Long> descIds = CalHelper.unwrapItemKeyIds(keysList);
			entityManager
					.createNativeQuery(
							"delete from DESCRIPTIONS where ITEM_KEY=:itemKey and DESC_ID in (:descIds)")
					.setParameter("itemKey", parentKey.toString())
					.setParameter("descIds", descIds).executeUpdate();
		}
	}

	@SuppressWarnings({ "unchecked" })
	@Override
	public List<Description> listDescriptionsByLength(boolean ascending,
			int maxValues, int startOffset) {

		final Query query = entityManager
				.createQuery("from DescriptionImpl order by length(description) desc");
		if (maxValues > 0) {
			query.setMaxResults(maxValues);
		}
		if (startOffset > 0) {
			query.setFirstResult(startOffset);
		}
		final List<Description> descriptions = query.getResultList();
		return descriptions;
	}
}
