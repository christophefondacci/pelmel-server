package com.nextep.geo.model.impl;

import java.io.Serializable;

public class AdmPK implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7861909668881273972L;

	private String countryCode;
	private String admCode;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((admCode == null) ? 0 : admCode.hashCode());
		result = prime * result
				+ ((countryCode == null) ? 0 : countryCode.hashCode());
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
		AdmPK other = (AdmPK) obj;
		if (admCode == null) {
			if (other.admCode != null)
				return false;
		} else if (!admCode.equals(other.admCode))
			return false;
		if (countryCode == null) {
			if (other.countryCode != null)
				return false;
		} else if (!countryCode.equals(other.countryCode))
			return false;
		return true;
	}

}
