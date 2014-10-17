package com.nextep.comments.dao.impl;

import java.math.BigInteger;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.nextep.cal.util.model.base.AbstractCalDao;
import com.nextep.comments.dao.CommentsDao;
import com.nextep.comments.model.Comment;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;

public class CommentsDaoImpl extends AbstractCalDao<Comment> implements
		CommentsDao {

	private static final Log LOGGER = LogFactory.getLog(CommentsDaoImpl.class);

	@PersistenceContext(unitName = "nextep-comments")
	private EntityManager entityManager;

	@Override
	public Comment getById(long id) {
		try {
			return (Comment) entityManager
					.createQuery("from CommentImpl where id=:id")
					.setParameter("id", id).getSingleResult();
		} catch (NoResultException e) {
			LOGGER.warn("The comment #" + id + " could not be found");
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Comment> getItemsFor(ItemKey key) {
		return entityManager
				.createQuery(
						"from CommentImpl where commentedItemKey=:itemKey order by date desc")
				.setParameter("itemKey", key.toString()).getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Comment> getItemsFor(ItemKey key, int resultsPerPage,
			int pageOffset) {
		return entityManager
				.createQuery(
						"from CommentImpl where commentedItemKey=:itemKey order by date desc")
				.setParameter("itemKey", key.toString())
				.setFirstResult(pageOffset * resultsPerPage)
				.setMaxResults(resultsPerPage).getResultList();
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
	public int getCommentsCount(ItemKey forKey) {
		return ((BigInteger) entityManager
				.createNativeQuery(
						"select count(1) from COMMENTS where ITEM_KEY=:itemKey")
				.setParameter("itemKey", forKey.toString()).getSingleResult())
				.intValue();
	}
}
