package com.nextep.media.services.impl;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.nextep.cal.util.services.CalPersistenceService;
import com.nextep.cal.util.services.base.AbstractDaoBasedCalServiceImpl;
import com.nextep.media.dao.MediaDao;
import com.nextep.media.model.Media;
import com.nextep.media.model.MutableMedia;
import com.nextep.media.model.impl.MediaImpl;
import com.videopolis.calm.exception.CalException;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.calm.model.RequestType;
import com.videopolis.cals.exception.UnsupportedCalServiceException;
import com.videopolis.cals.model.CalContext;
import com.videopolis.cals.model.MultiKeyItemsResponse;
import com.videopolis.cals.model.impl.MultiKeyItemsResponseImpl;

public class MediaServiceImpl extends AbstractDaoBasedCalServiceImpl implements
		CalPersistenceService {
	private final int modificationSourceId = 1000;
	private static final Log LOGGER = LogFactory.getLog(MediaServiceImpl.class);

	@Override
	public Class<? extends CalmObject> getProvidedClass() {
		return Media.class;
	}

	@Override
	public String getProvidedType() {
		return Media.CAL_TYPE;
	}

	@Override
	public MultiKeyItemsResponse getItemsFor(List<ItemKey> itemKeys,
			CalContext context) throws CalException {
		return getItemsFor(itemKeys, context, null);
	}

	@Override
	public MultiKeyItemsResponse getItemsFor(List<ItemKey> itemKeys,
			CalContext context, RequestType requestType) throws CalException {
		LOGGER.debug("Getting media for parent keys : " + itemKeys);
		final Map<ItemKey, List<Media>> mediasMap = ((MediaDao) getCalDao())
				.getMediasFor(itemKeys);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Resulting media : ");
			for (ItemKey key : mediasMap.keySet()) {
				final List<Media> medias = mediasMap.get(key);
				LOGGER.debug("  " + key.toString() + " -> "
						+ (medias != null ? medias.size() : "0")
						+ " media found");
			}
		}
		final MultiKeyItemsResponseImpl response = new MultiKeyItemsResponseImpl();
		for (ItemKey key : mediasMap.keySet()) {
			response.setItemsFor(key, mediasMap.get(key));
		}
		return response;
	}

	@Override
	public CalmObject createTransientObject() {
		final MutableMedia m = new MediaImpl();
		m.setSourceId(modificationSourceId);
		return m;
	}

	@Override
	public void saveItem(CalmObject object) {
		getCalDao().save(object);
	}

	@Override
	public List<? extends CalmObject> setItemFor(ItemKey contributedItemKey,
			ItemKey... internalItemKeys) throws CalException {
		throw new UnsupportedCalServiceException("Unsupported MDIA.setItemsFor");
	}

}
