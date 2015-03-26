package com.nextep.events.model.impl;

import java.io.Serializable;

public class ItemEventSeriesPK implements Serializable {

	private static final long serialVersionUID = 846009951974355363L;
	private String seriesOccurrenceKey;
	private String externalItemKey;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((externalItemKey == null) ? 0 : externalItemKey.hashCode());
		result = prime
				* result
				+ ((seriesOccurrenceKey == null) ? 0 : seriesOccurrenceKey
						.hashCode());
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
		ItemEventSeriesPK other = (ItemEventSeriesPK) obj;
		if (externalItemKey == null) {
			if (other.externalItemKey != null)
				return false;
		} else if (!externalItemKey.equals(other.externalItemKey))
			return false;
		if (seriesOccurrenceKey == null) {
			if (other.seriesOccurrenceKey != null)
				return false;
		} else if (!seriesOccurrenceKey.equals(other.seriesOccurrenceKey))
			return false;
		return true;
	}

}