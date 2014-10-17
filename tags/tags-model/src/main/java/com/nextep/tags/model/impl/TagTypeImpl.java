package com.nextep.tags.model.impl;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "TAG_ITEM_TYPES")
public class TagTypeImpl implements Serializable {

	@Id
	@Column(name = "TAG_ID")
	private String tagId;

	@Id
	@Column(name = "ITEM_TYPE")
	private String tagType;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((tagId == null) ? 0 : tagId.hashCode());
		result = prime * result + ((tagType == null) ? 0 : tagType.hashCode());
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
		TagTypeImpl other = (TagTypeImpl) obj;
		if (tagId == null) {
			if (other.tagId != null)
				return false;
		} else if (!tagId.equals(other.tagId))
			return false;
		if (tagType == null) {
			if (other.tagType != null)
				return false;
		} else if (!tagType.equals(other.tagType))
			return false;
		return true;
	}

}
