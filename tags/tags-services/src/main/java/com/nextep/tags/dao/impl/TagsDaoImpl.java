package com.nextep.tags.dao.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.nextep.cal.util.helpers.CalHelper;
import com.nextep.tags.dao.TagsDao;
import com.nextep.tags.model.ItemTag;
import com.nextep.tags.model.Tag;
import com.nextep.tags.model.UserTaggedItem;
import com.nextep.tags.model.impl.ItemTagImpl;
import com.nextep.tags.model.impl.TagTypeRequestType;
import com.nextep.tags.model.impl.UserTaggedItemImpl;
import com.videopolis.calm.exception.CalException;
import com.videopolis.calm.helper.Assert;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.calm.model.RequestType;
import com.videopolis.cals.model.RequestSettings;

public class TagsDaoImpl implements TagsDao {

	private static final Log log = LogFactory.getLog(TagsDaoImpl.class);

	@PersistenceContext(unitName = "nextep-tags")
	private EntityManager entityManager;

	@Override
	public Tag getById(long id) {
		throw new UnsupportedOperationException(
				"Unsupported get TAG by numeric ID");
	}

	@Override
	public List<Tag> getByTagId(List<ItemKey> itemKeys) {
		final List<String> ids = new ArrayList<String>();
		for (ItemKey itemKey : itemKeys) {
			ids.add(itemKey.getId());
		}
		return getByTagInternalId(ids);
	}

	@SuppressWarnings("unchecked")
	private List<Tag> getByTagInternalId(List<String> ids) {
		if (ids != null && !ids.isEmpty()) {
			final Query query = entityManager.createQuery(
					"from TagImpl where id in (:ids)").setParameter("ids", ids);
			try {
				return query.getResultList();
			} catch (RuntimeException e) {
				log.error(
						"Unable to retrieve tag for id " + ids + ": "
								+ e.getMessage(), e);
				return null;
			}
		} else {
			return Collections.emptyList();
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Tag> getItemsFor(ItemKey key) {
		if (key == null) {
			return Collections.emptyList();
		} else {
			final Query query = entityManager.createQuery(
					"from ItemTagImpl where itemKey=:key").setParameter("key",
					key.toString());
			try {
				// Retrieving from DB
				final List<ItemTag> itemTags = query.getResultList();
				// Unwrapping tags
				final List<Tag> tags = new ArrayList<Tag>();
				for (ItemTag itemTag : itemTags) {
					tags.add(itemTag.getTag());
				}
				return tags;
			} catch (RuntimeException e) {
				log.error(
						"Unable to retrieve item tag for id " + key.toString()
								+ ": " + e.getMessage(), e);
				return null;
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Tag> listItems(RequestType requestType,
			RequestSettings requestSettings) {
		if (!(requestType instanceof TagTypeRequestType)) {
			final Query query = entityManager.createQuery("from TagImpl");
			try {
				// Retrieving every tag from DB
				return query.getResultList();
			} catch (RuntimeException e) {
				log.error("Unable to retrieve full list of tags: "
						+ e.getMessage());
				return null;
			}
		} else {
			final String calType = ((TagTypeRequestType) requestType)
					.getCalType();
			return entityManager
					.createQuery(
							"select tag from TagTypeImpl as typ, TagImpl as tag where typ.tagType=:tagType and tag.id=typ.tagId")
					.setParameter("tagType", calType).getResultList();
		}
	}

	@Override
	public List<Tag> bindTags(ItemKey itemKey, Collection<ItemKey> tagKeys)
			throws CalException {
		if (CalHelper.isMultiKey(itemKey)) {
			List<ItemKey> keys = CalHelper.extractKeysFromMultiKey(itemKey);
			// We need exactly 2 keys : first is user key, second is tagged key
			Assert.equals(keys.size(), 2, "Expected 2 exact item keys");
			final ItemKey userKey = keys.get(0);
			final ItemKey taggedKey = keys.get(1);
			return bindUserTags(userKey, taggedKey, tagKeys);
		} else {
			return bindItemTags(itemKey, tagKeys);
		}
	}

	private List<Tag> bindItemTags(ItemKey itemKey, Collection<ItemKey> tagKeys) {
		// Deleting previous entries
		entityManager.createQuery("delete from ItemTagImpl where itemKey=:key")
				.setParameter("key", itemKey.toString()).executeUpdate();
		// Setting new tags
		final List<Tag> tags = getByTagId(new ArrayList<ItemKey>(tagKeys));
		for (Tag tag : tags) {
			ItemTagImpl itemTag = new ItemTagImpl(itemKey, tag);
			entityManager.persist(itemTag);
		}
		return tags;
	}

	private List<Tag> bindUserTags(ItemKey userKey, ItemKey taggedItemKey,
			Collection<ItemKey> tagKeys) {
		// Retrieving previous entries
		final List<UserTaggedItem> userTaggedItems = getUserTagsFor(userKey,
				taggedItemKey);
		final Map<ItemKey, UserTaggedItem> existingTagKeysMap = new HashMap<ItemKey, UserTaggedItem>(
				userTaggedItems.size());
		// Hashing user tagged items by tag item key
		for (UserTaggedItem t : userTaggedItems) {
			existingTagKeysMap.put(t.getTag().getKey(), t);
		}
		// For every key in the array we TOGGLE the state :
		// - Remove when already tagged
		// - Add if absent
		final List<ItemKey> tagIds = new ArrayList<ItemKey>();
		for (ItemKey tagKey : tagKeys) {
			final UserTaggedItem userTaggedItem = existingTagKeysMap
					.get(tagKey);
			if (userTaggedItem != null) {
				entityManager.remove(userTaggedItem);
			} else {
				tagIds.add(tagKey);
			}
		}
		final List<Tag> tags = getByTagId(tagIds);
		for (Tag t : tags) {
			final UserTaggedItem userTaggedItem = new UserTaggedItemImpl(
					userKey, taggedItemKey, t);
			entityManager.persist(userTaggedItem);
		}
		return tags;
	}

	@SuppressWarnings("unchecked")
	private List<UserTaggedItem> getUserTagsFor(ItemKey userKey, ItemKey itemKey) {
		List<UserTaggedItem> userTaggedItems = entityManager
				.createQuery(
						"from UserTaggedItemImpl where userItemKey=:userItemKey and taggedItemKey=:taggedItemKey")
				.setParameter("userItemKey", userKey.toString())
				.setParameter("taggedItemKey", itemKey.toString())
				.getResultList();
		return userTaggedItems;
		// final List<Tag> tags = new ArrayList<Tag>();
		// for (UserTaggedItem i : userTaggedItems) {
		// tags.add(i.getTag());
		// }
		// return tags;
	}

	@Override
	public List<Tag> getByIds(List<Long> idList) {
		throw new UnsupportedOperationException(
				"Unsupported get TAG by numeric ID");
	}

	@Override
	public List<Tag> getItemsFor(ItemKey key, int resultsPerPage, int pageOffset) {
		return getItemsFor(key);
	}

	@Override
	public void save(CalmObject object) {

	}

	@Override
	public Map<ItemKey, List<Tag>> getItemsFor(List<ItemKey> items)
			throws CalException {
		if (items == null || items.isEmpty()) {
			return Collections.emptyMap();
		} else {
			// Building our item key list as a list of string to inject in our
			// SQL query
			final List<String> itemKeys = new ArrayList<String>();
			for (ItemKey itemKey : items) {
				itemKeys.add(itemKey.toString());
			}
			// Building our batch query
			final Query query = entityManager.createQuery(
					"from ItemTagImpl where itemKey in (:keys)").setParameter(
					"keys", itemKeys);
			try {
				// Retrieving from DB
				final List<ItemTag> itemTags = query.getResultList();
				// Hashing tags
				final Map<ItemKey, List<Tag>> tagsItemKeyMap = new HashMap<ItemKey, List<Tag>>();
				for (ItemTag itemTag : itemTags) {
					// Retrieving our related item key
					final ItemKey itemKey = itemTag.getItemKey();
					List<Tag> itemKeyTags = tagsItemKeyMap.get(itemKey);
					if (itemKeyTags == null) {
						itemKeyTags = new ArrayList<Tag>();
						tagsItemKeyMap.put(itemKey, itemKeyTags);
					}
					itemKeyTags.add(itemTag.getTag());
				}
				return tagsItemKeyMap;
			} catch (RuntimeException e) {
				log.error(
						"Unable to retrieve item tag for id "
								+ items.toString() + ": " + e.getMessage(), e);
				return null;
			}
		}
	}

	@Override
	public Map<ItemKey, List<Tag>> getUserTagsFor(ItemKey taggedItemKey,
			List<ItemKey> items) throws CalException {
		final Map<ItemKey, List<Tag>> tagsItemKeyMap = new HashMap<ItemKey, List<Tag>>();
		for (ItemKey userKey : items) {
			final List<UserTaggedItem> userTags = entityManager
					.createQuery(
							"from UserTaggedItemImpl where userItemKey=:userItemKey and taggedItemKey=:taggedItemKey")
					.setParameter("userItemKey", userKey.toString())
					.setParameter("taggedItemKey", taggedItemKey.toString())
					.getResultList();
			final List<Tag> tags = new ArrayList(userTags.size());
			for (UserTaggedItem userTag : userTags) {
				tags.add(userTag.getTag());
			}
			tagsItemKeyMap.put(userKey, tags);
		}
		return tagsItemKeyMap;
	}

}
