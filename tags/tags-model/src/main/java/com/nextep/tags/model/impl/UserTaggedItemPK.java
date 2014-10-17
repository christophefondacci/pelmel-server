package com.nextep.tags.model.impl;

import java.io.Serializable;

public class UserTaggedItemPK implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2648028828811269936L;
	private String userItemKey;
	private String taggedItemKey;
	private String tag;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((tag == null) ? 0 : tag.hashCode());
		result = prime * result
				+ ((taggedItemKey == null) ? 0 : taggedItemKey.hashCode());
		result = prime * result
				+ ((userItemKey == null) ? 0 : userItemKey.hashCode());
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
		UserTaggedItemPK other = (UserTaggedItemPK) obj;
		if (tag == null) {
			if (other.tag != null)
				return false;
		} else if (!tag.equals(other.tag))
			return false;
		if (taggedItemKey == null) {
			if (other.taggedItemKey != null)
				return false;
		} else if (!taggedItemKey.equals(other.taggedItemKey))
			return false;
		if (userItemKey == null) {
			if (other.userItemKey != null)
				return false;
		} else if (!userItemKey.equals(other.userItemKey))
			return false;
		return true;
	}

}
