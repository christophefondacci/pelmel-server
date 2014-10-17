package com.nextep.tags.model.impl;

import com.videopolis.calm.model.RequestType;

public class TagTypeRequestType implements RequestType {

	private static final long serialVersionUID = 629002621544877830L;
	private final String calType;

	public TagTypeRequestType(String calType) {
		this.calType = calType;
	}

	public String getCalType() {
		return calType;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((calType == null) ? 0 : calType.hashCode());
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
		TagTypeRequestType other = (TagTypeRequestType) obj;
		if (calType == null) {
			if (other.calType != null)
				return false;
		} else if (!calType.equals(other.calType))
			return false;
		return true;
	}

}
