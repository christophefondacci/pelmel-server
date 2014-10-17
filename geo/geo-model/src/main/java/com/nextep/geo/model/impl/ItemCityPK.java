package com.nextep.geo.model.impl;

import java.io.Serializable;

public class ItemCityPK implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2648028828811269936L;
	private String externalItemKey;
	private Long city;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((city == null) ? 0 : city.hashCode());
		result = prime * result
				+ ((externalItemKey == null) ? 0 : externalItemKey.hashCode());
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
		ItemCityPK other = (ItemCityPK) obj;
		if (city == null) {
			if (other.city != null)
				return false;
		} else if (!city.equals(other.city))
			return false;
		if (externalItemKey == null) {
			if (other.externalItemKey != null)
				return false;
		} else if (!externalItemKey.equals(other.externalItemKey))
			return false;
		return true;
	}

}