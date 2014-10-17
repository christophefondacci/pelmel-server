package com.nextep.tags.dao;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.nextep.cal.util.model.CalDao;
import com.nextep.tags.model.Tag;
import com.videopolis.calm.exception.CalException;
import com.videopolis.calm.model.ItemKey;

public interface TagsDao extends CalDao<Tag> {

	List<Tag> bindTags(ItemKey itemKey, Collection<ItemKey> tagKeys)
			throws CalException;

	List<Tag> getByTagId(List<ItemKey> itemKeys);

	Map<ItemKey, List<Tag>> getItemsFor(List<ItemKey> items)
			throws CalException;

	Map<ItemKey, List<Tag>> getUserTagsFor(ItemKey taggedItemKey,
			List<ItemKey> items) throws CalException;
}
