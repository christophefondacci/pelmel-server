package com.nextep.ratings.model.impl;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import com.nextep.ratings.model.MutableRating;
import com.videopolis.calm.base.AbstractCalmObject;
import com.videopolis.calm.exception.CalException;
import com.videopolis.calm.factory.CalmFactory;
import com.videopolis.calm.model.ItemKey;

@Entity
@Table(name = "ITEM_RATINGS")
@IdClass(RatingPK.class)
public class RatingImpl extends AbstractCalmObject implements MutableRating {

	private static final long serialVersionUID = -114405287978823937L;

	@Id
	@Column(name = "RATED_ITEM_KEY")
	private String ratedItemKey;
	@Id
	@Column(name = "RATED_BY_ITEM_KEY")
	private String ratedByItemKey;
	@Column(name = "RATE_VALUE")
	private int rate;

	public RatingImpl() {
		super(null);
	}

	@Override
	public ItemKey getRatedItemKey() {
		try {
			return CalmFactory.parseKey(ratedItemKey);
		} catch (CalException e) {
			return null;
		}
	}

	@Override
	public ItemKey getRatedByItemKey() {
		try {
			return CalmFactory.parseKey(ratedByItemKey);
		} catch (CalException e) {
			return null;
		}
	}

	@Override
	public int getRate() {
		return rate;
	}

	@Override
	public void setRatedItemKey(ItemKey ratedItemKey) {
		this.ratedItemKey = ratedItemKey == null ? null : ratedItemKey
				.toString();
	}

	@Override
	public void setRatedByItemKey(ItemKey ratedByItemKey) {
		this.ratedByItemKey = ratedByItemKey == null ? null : ratedByItemKey
				.toString();
	}

	@Override
	public void setRate(int rate) {
		this.rate = rate;
	}

}
