package com.nextep.geo.model.impl;

import com.videopolis.calm.model.RequestType;

public class RequestTypeMinPopulation implements RequestType {

	private static final long serialVersionUID = -6284299670392540191L;
	private int minPopulation;

	public RequestTypeMinPopulation(int minPopulation) {
		this.minPopulation = minPopulation;
	}

	public int getMinPopulation() {
		return minPopulation;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + minPopulation;
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
		RequestTypeMinPopulation other = (RequestTypeMinPopulation) obj;
		if (minPopulation != other.minPopulation)
			return false;
		return true;
	}
}
