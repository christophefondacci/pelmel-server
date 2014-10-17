package com.nextep.media.dao;

import java.util.List;
import java.util.Map;

import com.nextep.cal.util.model.CalDao;
import com.nextep.media.model.Media;
import com.videopolis.calm.model.ItemKey;

public interface MediaDao extends CalDao<Media> {

	Map<ItemKey, List<Media>> getMediasFor(List<ItemKey> keys);
}
