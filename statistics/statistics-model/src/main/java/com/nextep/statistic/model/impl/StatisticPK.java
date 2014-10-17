package com.nextep.statistic.model.impl;

import java.io.Serializable;

public class StatisticPK implements Serializable {

	private static final long serialVersionUID = 3984871191688197529L;

	private String itemKey;
	private String viewType;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((itemKey == null) ? 0 : itemKey.hashCode());
		result = prime * result
				+ ((viewType == null) ? 0 : viewType.hashCode());
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
		StatisticPK other = (StatisticPK) obj;
		if (itemKey == null) {
			if (other.itemKey != null)
				return false;
		} else if (!itemKey.equals(other.itemKey))
			return false;
		if (viewType == null) {
			if (other.viewType != null)
				return false;
		} else if (!viewType.equals(other.viewType))
			return false;
		return true;
	}

}
