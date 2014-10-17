package com.nextep.statistic.model.impl;

import java.io.Serializable;
import java.util.Date;

public class ItemViewPK implements Serializable {

	private static final long serialVersionUID = 1900492882817399597L;
	private String viewedItemKey;
	private Date viewDate;
	private String viewType;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((viewDate == null) ? 0 : viewDate.hashCode());
		result = prime * result
				+ ((viewType == null) ? 0 : viewType.hashCode());
		result = prime * result
				+ ((viewedItemKey == null) ? 0 : viewedItemKey.hashCode());
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
		ItemViewPK other = (ItemViewPK) obj;
		if (viewDate == null) {
			if (other.viewDate != null)
				return false;
		} else if (!viewDate.equals(other.viewDate))
			return false;
		if (viewType == null) {
			if (other.viewType != null)
				return false;
		} else if (!viewType.equals(other.viewType))
			return false;
		if (viewedItemKey == null) {
			if (other.viewedItemKey != null)
				return false;
		} else if (!viewedItemKey.equals(other.viewedItemKey))
			return false;
		return true;
	}

}
