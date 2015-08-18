package com.nextep.deals.dao;

import java.util.List;
import java.util.Map;

import com.fgp.deals.model.Deal;
import com.nextep.cal.util.model.CalDao;
import com.videopolis.calm.model.ItemKey;

public interface DealDao extends CalDao<Deal> {

	Map<ItemKey, List<Deal>> getDealsFor(List<ItemKey> itemKeys);
}
