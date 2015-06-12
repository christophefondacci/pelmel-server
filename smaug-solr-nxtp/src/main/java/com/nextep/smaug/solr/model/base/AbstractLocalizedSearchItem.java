package com.nextep.smaug.solr.model.base;

import org.apache.solr.client.solrj.beans.Field;

/**
 * A base class that factorizes implementation of LATLON support for localized
 * beans
 * 
 * @author cfondacci
 *
 */
public abstract class AbstractLocalizedSearchItem {

	@Field
	private String latlon;
	@Field
	private Double geodistance;

	private transient Double lat = new Double(0);
	private transient Double lng = new Double(0);

	public final void setLat(Double lat) {
		this.lat = lat;
		updateLatLng();
	}

	public final void setLng(Double lng) {
		this.lng = lng;
		updateLatLng();
	}

	private void updateLatLng() {
		if (this.lat == null || this.lng == null) {
			this.latlon = null;
		} else {
			this.latlon = this.lat + "," + this.lng;
		}
	}

	public Double getGeodistance() {
		return geodistance;
	}
}
