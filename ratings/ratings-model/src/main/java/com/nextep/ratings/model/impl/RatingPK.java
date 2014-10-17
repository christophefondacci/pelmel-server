package com.nextep.ratings.model.impl;

import java.io.Serializable;

public class RatingPK implements Serializable {

	private static final long serialVersionUID = -8153608801721289000L;

	private String ratedItemKey;
	private String ratedByItemKey;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((ratedByItemKey == null) ? 0 : ratedByItemKey.hashCode());
		result = prime * result
				+ ((ratedItemKey == null) ? 0 : ratedItemKey.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RatingPK other = (RatingPK) obj;
		if (ratedByItemKey == null) {
			if (other.ratedByItemKey != null)
				return false;
		} else if (!ratedByItemKey.equals(other.ratedByItemKey))
			return false;
		if (ratedItemKey == null) {
			if (other.ratedItemKey != null)
				return false;
		} else if (!ratedItemKey.equals(other.ratedItemKey))
			return false;
		return true;
	}

}
