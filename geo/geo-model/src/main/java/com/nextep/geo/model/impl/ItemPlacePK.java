package com.nextep.geo.model.impl;

import java.io.Serializable;

public class ItemPlacePK implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2648028828811269936L;
	private String externalItemKey;
	private String placeId;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((externalItemKey == null) ? 0 : externalItemKey.hashCode());
		result = prime * result + ((placeId == null) ? 0 : placeId.hashCode());
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
		ItemPlacePK other = (ItemPlacePK) obj;
		if (externalItemKey == null) {
			if (other.externalItemKey != null)
				return false;
		} else if (!externalItemKey.equals(other.externalItemKey))
			return false;
		if (placeId == null) {
			if (other.placeId != null)
				return false;
		} else if (!placeId.equals(other.placeId))
			return false;
		return true;
	}

}