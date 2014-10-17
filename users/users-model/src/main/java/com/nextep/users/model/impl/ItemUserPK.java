package com.nextep.users.model.impl;

import java.io.Serializable;

public class ItemUserPK implements Serializable {

	private static final long serialVersionUID = -2164878006136916915L;
	private Long userId;
	private String itemKey;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((itemKey == null) ? 0 : itemKey.hashCode());
		result = prime * result + ((userId == null) ? 0 : userId.hashCode());
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
		ItemUserPK other = (ItemUserPK) obj;
		if (itemKey == null) {
			if (other.itemKey != null)
				return false;
		} else if (!itemKey.equals(other.itemKey))
			return false;
		if (userId == null) {
			if (other.userId != null)
				return false;
		} else if (!userId.equals(other.userId))
			return false;
		return true;
	}

}
