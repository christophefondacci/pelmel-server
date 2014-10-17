package com.nextep.ratings.model.impl;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.nextep.ratings.model.Rating;
import com.videopolis.calm.base.AbstractCalmObject;
import com.videopolis.calm.exception.CalException;
import com.videopolis.calm.factory.CalmFactory;
import com.videopolis.calm.model.ItemKey;

@Entity
@Table(name = "ITEM_RATING_TOTAL")
public class TotalRatingImpl extends AbstractCalmObject implements Rating {

	private static final long serialVersionUID = 3955370929956272319L;

	@Id
	@Column(name = "ITEM_KEY")
	private String itemKey;
	@Column(name = "MAX_RATING")
	private int maxRate;
	@Column(name = "MIN_RATING")
	private int minRate;
	@Column(name = "AVG_RATING")
	private double avgRate;

	private TotalRatingImpl() {
		super(null);
	}

	@Override
	public ItemKey getRatedItemKey() {
		try {
			return CalmFactory.parseKey(itemKey);
		} catch (CalException e) {
			return null;
		}
	}

	@Override
	public ItemKey getRatedByItemKey() {
		return null;
	}

	@Override
	public int getRate() {
		return Math.round((float) avgRate);
	}

}
