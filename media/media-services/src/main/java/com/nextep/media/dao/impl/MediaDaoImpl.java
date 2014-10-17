package com.nextep.media.dao.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.nextep.cal.util.model.base.AbstractCalDao;
import com.nextep.media.dao.MediaDao;
import com.nextep.media.model.Media;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.calm.model.RequestType;
import com.videopolis.cals.model.RequestSettings;

public class MediaDaoImpl extends AbstractCalDao<Media> implements MediaDao {

	private static final Log log = LogFactory.getLog(MediaDaoImpl.class);

	@PersistenceContext(unitName = "nextep-media")
	EntityManager entityManager;

	@Override
	public Media getById(long id) {
		final Query query = entityManager.createQuery(
				"from MediaImpl where id=:id").setParameter("id", id);
		try {
			return (Media) query.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	@Override
	public List<Media> getByIds(List<Long> idList) {
		final Query query = entityManager.createQuery(
				"from MediaImpl where id in (:ids)")
				.setParameter("ids", idList);
		try {
			return query.getResultList();
		} catch (NoResultException e) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Media> getItemsFor(ItemKey key) {
		if (key == null) {
			return Collections.emptyList();
		} else {
			final Query query = entityManager
					.createQuery(
							"from MediaImpl where relatedItemKey=:key and online=true  order by prefOrder")
					.setParameter("key", key.toString());
			try {
				return query.getResultList();
			} catch (RuntimeException e) {
				log.error("Unable to retrieve media for id " + key.toString()
						+ ": " + e.getMessage(), e);
				return null;
			}
		}
	}

	@Override
	public Map<ItemKey, List<Media>> getMediasFor(List<ItemKey> keys) {
		final List<String> keyStrings = new ArrayList<String>(keys.size());
		for (ItemKey key : keys) {
			keyStrings.add(key.toString());
		}
		final Query query = entityManager
				.createQuery(
						"from MediaImpl where relatedItemKey in (:keys) and online=true order by prefOrder")
				.setParameter("keys", keyStrings);
		final List<Media> medias = query.getResultList();
		// Building our result map
		final Map<ItemKey, List<Media>> mediasMap = new HashMap<ItemKey, List<Media>>();
		// Hashing returned medias
		for (Media m : medias) {
			final ItemKey parentKey = m.getRelatedItemKey();
			List<Media> parentMedias = mediasMap.get(parentKey);
			// Initializing entry list
			if (parentMedias == null) {
				parentMedias = new LinkedList<Media>();
				mediasMap.put(parentKey, parentMedias);
			}
			// Appending our media
			parentMedias.add(m);
		}
		return mediasMap;
	}

	@Override
	public void save(CalmObject object) {
		final Media media = (Media) object;

		if (media.getKey() == null) {
			entityManager.persist(media);
		} else {
			entityManager.merge(media);
		}
	}

	@Override
	public List<Media> listItems(RequestType requestType,
			RequestSettings requestSettings) {
		return entityManager.createQuery("from MediaImpl").getResultList();
	}
}
