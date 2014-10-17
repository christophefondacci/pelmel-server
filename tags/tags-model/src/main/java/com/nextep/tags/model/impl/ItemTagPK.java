package com.nextep.tags.model.impl;

import java.io.Serializable;

public class ItemTagPK implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2648028828811269936L;
	private String itemKey;
	private String tag;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((itemKey == null) ? 0 : itemKey.hashCode());
		result = prime * result + ((tag == null) ? 0 : tag.hashCode());
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
		ItemTagPK other = (ItemTagPK) obj;
		if (itemKey == null) {
			if (other.itemKey != null)
				return false;
		} else if (!itemKey.equals(other.itemKey))
			return false;
		if (tag == null) {
			if (other.tag != null)
				return false;
		} else if (!tag.equals(other.tag))
			return false;
		return true;
	}

}
