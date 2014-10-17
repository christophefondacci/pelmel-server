package com.nextep.statistic.model.impl;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.nextep.statistic.model.MutableStatistic;
import com.videopolis.calm.base.AbstractCalmObject;
import com.videopolis.calm.exception.CalException;
import com.videopolis.calm.factory.CalmFactory;
import com.videopolis.calm.model.ItemKey;

@IdClass(value = StatisticPK.class)
@Entity
@Table(name = "STATISTICS")
public class StatisticImpl extends AbstractCalmObject implements
		MutableStatistic {

	private static final long serialVersionUID = -3298879857885438921L;
	private static final Log LOGGER = LogFactory.getLog(StatisticImpl.class);

	@Id
	@Column(name = "ITEM_KEY")
	private String itemKey;

	@Column(name = "VIEWS")
	private int viewsCount;

	@Column(name = "RATING")
	private int rating;

	@Id
	@Column(name = "VIEW_TYPE")
	private String viewType = "OVERVIEW";

	public StatisticImpl() {
		super(null);
	}

	@Override
	public ItemKey getKey() {
		if (itemKey != null) {
			try {
				return CalmFactory.createKey(CAL_TYPE, itemKey);
			} catch (CalException e) {
				LOGGER.error("Unable to build STAT item key for '" + itemKey
						+ "' : " + e.getMessage());
			}
		}
		return null;
	}

	@Override
	public ItemKey getItemKey() {
		if (itemKey != null) {
			try {
				return CalmFactory.parseKey(itemKey);
			} catch (CalException e) {
				LOGGER.error("Unable to parse CAL key '" + itemKey + "' : "
						+ e.getMessage());
			}
		}
		return null;
	}

	@Override
	public void setItemKey(ItemKey itemKey) {
		this.itemKey = itemKey == null ? null : itemKey.toString();
	}

	@Override
	public void setRating(int rating) {
		this.rating = rating;
	}

	@Override
	public int getViewsCount() {
		return viewsCount;
	}

	@Override
	public int getRating() {
		return rating;
	}

	@Override
	public String getViewType() {
		return viewType;
	}

	@Override
	public void setViewType(String viewType) {
		this.viewType = viewType;
	}
}
