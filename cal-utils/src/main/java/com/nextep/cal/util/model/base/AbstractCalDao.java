package com.nextep.cal.util.model.base;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.nextep.cal.util.model.CalDao;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.calm.model.RequestType;
import com.videopolis.cals.model.RequestSettings;

/**
 * A base empty DAO class so that extension decide what to implement and
 * framework changes are absorbed seamlessly.
 * 
 * @author cfondacci
 * 
 * @param <T>
 */
public abstract class AbstractCalDao<T extends CalmObject> implements CalDao<T> {

	@Override
	public List<T> getByIds(List<Long> idList) {
		final List<T> items = new ArrayList<T>(idList.size());
		for (Long id : idList) {
			items.add(getById(id));
		}
		return items;
	}

	@Override
	public List<T> getItemsFor(ItemKey key) {
		return Collections.emptyList();
	}

	@Override
	public List<T> getItemsFor(ItemKey key, int resultsPerPage, int pageOffset) {
		return Collections.emptyList();
	}

	@Override
	public List<T> listItems(RequestType requestType,
			RequestSettings requestSettings) {
		return Collections.emptyList();
	}

	@Override
	public void save(CalmObject object) {

	}

}
