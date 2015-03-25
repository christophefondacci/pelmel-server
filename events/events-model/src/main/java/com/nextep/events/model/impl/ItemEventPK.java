package com.nextep.events.model.impl;

import java.io.Serializable;

public class ItemEventPK implements Serializable {

	private static final long serialVersionUID = 846009951974355363L;
	private long itemId;
	private String externalItemKey;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((externalItemKey == null) ? 0 : externalItemKey.hashCode());
		result = prime * result + (int) (itemId ^ (itemId >>> 32));
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
		ItemEventPK other = (ItemEventPK) obj;
		if (externalItemKey == null) {
			if (other.externalItemKey != null)
				return false;
		} else if (!externalItemKey.equals(other.externalItemKey))
			return false;
		if (itemId != other.itemId)
			return false;
		return true;
	}

}
