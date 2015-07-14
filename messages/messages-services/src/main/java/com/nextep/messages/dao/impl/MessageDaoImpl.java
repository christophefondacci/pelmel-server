package com.nextep.messages.dao.impl;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import com.nextep.cal.util.helpers.CalHelper;
import com.nextep.cal.util.model.base.AbstractCalDao;
import com.nextep.messages.dao.MessageDao;
import com.nextep.messages.model.Message;
import com.nextep.messages.model.MessageRecipientsGroup;
import com.nextep.messages.model.MutableMessage;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.calm.model.Keyed;

public class MessageDaoImpl extends AbstractCalDao<Message>implements MessageDao {

	// private static final Log log = LogFactory.getLog(MessageDaoImpl.class);
	@PersistenceContext(unitName = "nextep-messages")
	private EntityManager entityManager;

	@Override
	public Message getById(long id) {
		return null;
	}

	@Override
	public List<Message> getItemsFor(ItemKey key) {
		return getItemsFor(key, 15, 0);
	}

	@Override
	public void save(CalmObject object) {
		final Keyed message = object;

		if (message.getKey() == null) {
			entityManager.persist(message);
		} else {
			entityManager.merge(message);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Message> getItemsFor(ItemKey key, int resultsPerPage, int pageOffset) {
		return entityManager
				.createQuery(
						"from MessageImpl where toKey=:toKey and (fromKey!=toKey or recipientsGroupKey is not null) order by messageDate desc ")
				.setMaxResults(resultsPerPage).setFirstResult(pageOffset * resultsPerPage)
				.setParameter("toKey", key.toString()).getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Message> getMessagesForAfterId(ItemKey userKey, int page, int pageSize, ItemKey afterId) {
		return entityManager
				.createQuery(
						"from MessageImpl where (toKey=:toKey or fromKey=:toKey) and id>:minId and (fromKey!=toKey or recipientsGroupKey is not null) order by messageDate asc ")
				.setMaxResults(pageSize).setFirstResult(page * pageSize).setParameter("toKey", userKey.toString())
				.setParameter("minId", afterId.getNumericId()).getResultList();
	}

	@Override
	public List<Message> getUnreadMessages(ItemKey userKey) {
		return getUnreadMessages(userKey, -1);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Message> getUnreadMessages(ItemKey userKey, int maxResults) {
		final Query query = entityManager
				.createQuery(
						"from MessageImpl where toKey=:toKey and (fromKey!=toKey or recipientsGroupKey is not null) and isUnread=1 order by messageDate")
				.setParameter("toKey", userKey.toString());
		if (maxResults > 0) {
			query.setMaxResults(maxResults);
		}
		return query.getResultList();
	}

	@Override
	public int getMessageCount(ItemKey userKey, boolean unreadOnly) {
		if (unreadOnly) {
			final BigInteger count = (BigInteger) entityManager
					.createNativeQuery(
							"select count(1) from MESSAGES where to_item_key=:toItemKey and IS_UNREAD=1 and (from_item_key!=to_item_key or RECIPIENTS_ITEM_KEY is not null)")
					.setParameter("toItemKey", userKey.toString()).getSingleResult();
			return count.intValue();
		} else {
			final BigInteger count = (BigInteger) entityManager
					.createNativeQuery(
							"select count(1) from MESSAGES where to_item_key=:toItemKey and (from_item_key!=to_item_key or RECIPIENTS_ITEM_KEY is not null)")
					.setParameter("toItemKey", userKey.toString()).getSingleResult();
			return count.intValue();
		}
	}

	@Override
	public int getMessageCountAfterId(ItemKey userKey, ItemKey minItemKey) {
		final BigInteger count = (BigInteger) entityManager
				.createNativeQuery(
						"select count(1) from MESSAGES where to_item_key=:toItemKey and MSG_ID>:minId and (from_item_key!=to_item_key or RECIPIENTS_ITEM_KEY is not null) ")
				.setParameter("toItemKey", userKey.toString()).setParameter("minId", minItemKey.getNumericId())
				.getSingleResult();
		return count.intValue();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<? extends Message> markRead(List<ItemKey> messageKeys) {
		// Converting to a list of strings for injection as SQL param
		final List<Long> keys = new ArrayList<Long>();
		for (ItemKey messageKey : messageKeys) {
			keys.add(messageKey.getNumericId());
		}
		// Querying messages
		final List<MutableMessage> messages = entityManager.createQuery("from MessageImpl where id in (:id)")
				.setParameter("id", keys).getResultList();
		for (MutableMessage m : messages) {
			if (m.isUnread()) {
				m.setUnread(false);
				entityManager.merge(m);
			}
		}
		return messages;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Message> getConversation(ItemKey from, ItemKey to, int offset, int count) {
		List<Message> messages = Collections.emptyList();
		if (MessageRecipientsGroup.CAL_TYPE.equals(from.getType())) {
			messages = entityManager
					.createQuery(
							"from MessageImpl where recipientsGroupKey=:groupKey and toKey=:to order by messageDate desc")
					.setParameter("groupKey", from.toString()).setParameter("to", to.toString()).getResultList();
		} else {
			messages = entityManager
					.createQuery(
							"from MessageImpl where (fromKey=:from and toKey=:to) or (fromKey=:to and toKey=:from) order by messageDate desc")
					.setParameter("from", from.toString()).setParameter("to", to.toString()).setFirstResult(offset)
					.setMaxResults(count).getResultList();
		}
		return messages;
	}

	@Override
	public int getConversationMessageCount(ItemKey from, ItemKey to) {
		BigInteger count = new BigInteger("0");
		if (MessageRecipientsGroup.CAL_TYPE.equals(from.getType())) {
			count = (BigInteger) entityManager
					.createNativeQuery(
							"select count(1) from MESSAGES where RECIPIENTS_ITEM_KEY=:groupKey and TO_ITEM_KEY=:to")
					.setParameter("groupKey", from.toString()).setParameter("to", to.toString()).getSingleResult();
		} else {
			count = (BigInteger) entityManager
					.createNativeQuery(
							"select count(1) from MESSAGES where (FROM_ITEM_KEY=:from and TO_ITEM_KEY=:to) or (FROM_ITEM_KEY=:to and TO_ITEM_KEY=:from)")
					.setParameter("from", from.toString()).setParameter("to", to.toString()).getSingleResult();
		}
		return count.intValue();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<MessageRecipientsGroup> getMessageGroups(List<ItemKey> itemKeys) {
		final Collection<Long> itemKeyStrs = CalHelper.unwrapItemKeyIds(itemKeys);

		final List<MessageRecipientsGroup> groups = entityManager
				.createQuery("from MessageRecipientsGroupImpl where id in (:ids)").setParameter("ids", itemKeyStrs)
				.getResultList();
		return groups;
	}
}
