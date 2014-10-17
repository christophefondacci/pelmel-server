package com.nextep.geo.model.impl;

import java.io.Serializable;

public class AlternateNamePKImpl implements Serializable {

	private static final long serialVersionUID = -6985609509550636901L;
	private long geonameId;
	private String language;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (geonameId ^ (geonameId >>> 32));
		result = prime * result
				+ ((language == null) ? 0 : language.hashCode());
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
		AlternateNamePKImpl other = (AlternateNamePKImpl) obj;
		if (geonameId != other.geonameId)
			return false;
		if (language == null) {
			if (other.language != null)
				return false;
		} else if (!language.equals(other.language))
			return false;
		return true;
	}

}
