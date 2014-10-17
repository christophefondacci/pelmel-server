package com.nextep.tags.services.impl;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.nextep.cal.util.services.CalPersistenceService;
import com.nextep.cal.util.services.base.AbstractDaoBasedCalServiceImpl;
import com.nextep.tags.dao.TagsDao;
import com.nextep.tags.model.Tag;
import com.nextep.tags.model.impl.TagImpl;
import com.nextep.tags.model.impl.TagRequestTypeFor;
import com.videopolis.calm.exception.CalException;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.calm.model.RequestType;
import com.videopolis.cals.model.CalContext;
import com.videopolis.cals.model.ItemsResponse;
import com.videopolis.cals.model.MultiKeyItemsResponse;
import com.videopolis.cals.model.RequestSettings;
import com.videopolis.cals.model.impl.ItemsResponseImpl;
import com.videopolis.cals.model.impl.MultiKeyItemsResponseImpl;

public class TagsServiceImpl extends AbstractDaoBasedCalServiceImpl implements
		CalPersistenceService {

	@Override
	public Class<? extends CalmObject> getProvidedClass() {
		return Tag.class;
	}

	@Override
	public String getProvidedType() {
		return Tag.CAL_ID;
	}

	@Override
	public void saveItem(CalmObject object) {
		// TODO Auto-generated method stub

	}

	@Override
	public CalmObject createTransientObject() {
		return new TagImpl();
	}

	@Override
	public List<? extends CalmObject> setItemFor(ItemKey contributedItemKey,
			ItemKey... internalItemKeys) throws CalException {
		final List<ItemKey> tagKeys = Arrays.asList(internalItemKeys);
		return ((TagsDao) getCalDao()).bindTags(contributedItemKey, tagKeys);
	}

	@Override
	public ItemsResponse listItems(CalContext context, RequestType requestType,
			RequestSettings requestSettings) throws CalException {
		return super.listItems(context, requestType, requestSettings);
	}

	@Override
	public ItemsResponse getItems(List<ItemKey> itemKeys, CalContext context)
			throws CalException {
		final TagsDao dao = (TagsDao) getCalDao();
		final List<Tag> tags = dao.getByTagId(itemKeys);
		final ItemsResponseImpl response = new ItemsResponseImpl();
		response.setItems(reorderCalmObjects(itemKeys, tags));
		return response;
	}

	@Override
	public MultiKeyItemsResponse getItemsFor(List<ItemKey> itemKeys,
			CalContext context) throws CalException {
		return getItemsFor(itemKeys, context, null);
	}

	@Override
	public MultiKeyItemsResponse getItemsFor(List<ItemKey> itemKeys,
			CalContext context, RequestType requestType) throws CalException {
		final TagsDao dao = (TagsDao) getCalDao();
		Map<ItemKey, List<Tag>> tagsMap = Collections.emptyMap();
		// Specific behaviour for TagRequestTypeFor
		if (requestType instanceof TagRequestTypeFor) {
			final ItemKey taggedItemKey = ((TagRequestTypeFor) requestType)
					.getTaggedItemKey();
			tagsMap = dao.getUserTagsFor(taggedItemKey, itemKeys);
		} else {
			tagsMap = dao.getItemsFor(itemKeys);
		}
		final MultiKeyItemsResponseImpl response = new MultiKeyItemsResponseImpl();
		for (ItemKey key : tagsMap.keySet()) {
			response.setItemsFor(key, tagsMap.get(key));
		}
		return response;

	}

}
