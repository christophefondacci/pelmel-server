package com.nextep.deals.dao;

import java.util.List;
import java.util.Map;

import com.fgp.deals.model.Deal;
import com.fgp.deals.model.DealUse;
import com.nextep.cal.util.model.CalDao;
import com.videopolis.calm.model.ItemKey;

public interface DealDao extends CalDao<Deal> {

	Map<ItemKey, List<Deal>> getDealsFor(List<ItemKey> itemKeys);

	List<DealUse> getDealUses(List<ItemKey> itemKeys);

	Map<ItemKey, List<DealUse>> getDealUsesFor(List<ItemKey> itemKeys, boolean lastDay);
}
