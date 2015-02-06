package com.nextep.geo.model.impl;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Type;

import com.nextep.cal.util.helpers.CalHelper;
import com.nextep.geo.model.City;
import com.nextep.geo.model.MutablePlace;
import com.videopolis.calm.base.AbstractCalmObject;
import com.videopolis.calm.exception.CalException;
import com.videopolis.calm.factory.CalmFactory;
import com.videopolis.calm.model.ItemKey;

@Entity
@Table(name = "GEO_PLACES")
public class PlaceImpl extends AbstractCalmObject implements MutablePlace {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8739730690896798670L;
	private static final Log LOGGER = LogFactory.getLog(PlaceImpl.class);

	@Id
	@GeneratedValue
	@Column(name = "PLACE_ID")
	private long id;

	@Column(name = "PLACE_NAME")
	private String name;

	@Column(name = "GEO_LONGITUDE")
	private Double longitude;

	@Column(name = "GEO_LATITUDE")
	private Double latitude;

	@Column(name = "PLACE_TYPE")
	private String placeType;

	@ManyToOne(targetEntity = CityImpl.class)
	@JoinColumn(name = "CITY_ID")
	@Fetch(FetchMode.JOIN)
	private City city;

	@Column(name = "ADDRESS1")
	private String address1;

	@Column(name = "ADDRESS2")
	private String address2;

	@Column(name = "IS_ONLINE")
	@Type(type = "yes_no")
	private boolean online = true;

	@Column(name = "REDIRECTION_PLACE_KEY")
	private String redirectionItemKey;

	@Column(name = "IS_INDEXED")
	@Type(type = "yes_no")
	private boolean indexed;

	@Column(name = "UDATE")
	private Date lastUpdate;

	@Column(name = "CLOSED_REPORT_COUNT")
	private int closedCount;

	public PlaceImpl() {
		super(null);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public double getLongitude() {
		if (longitude != null) {
			return longitude;
		} else {
			return 0;
		}
	}

	@Override
	public double getLatitude() {
		if (latitude != null) {
			return latitude;
		} else {
			return 0;
		}
	}

	@Override
	public String getPlaceType() {
		return placeType;
	}

	@Override
	public City getCity() {
		return city;
	}

	@Override
	public String getAddress1() {
		return address1;
	}

	@Override
	public String getAddress2() {
		return address2;
	}

	@Override
	public ItemKey getKey() {
		return CalHelper.getItemKeyFromId(CAL_TYPE, id);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
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
		PlaceImpl other = (PlaceImpl) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	@Override
	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	@Override
	public void setPlaceType(String placeType) {
		this.placeType = placeType;
	}

	@Override
	public void setCity(City city) {
		this.city = city;
	}

	@Override
	public void setAddress1(String address1) {
		this.address1 = address1;
	}

	@Override
	public void setAddress2(String address2) {
		this.address2 = address2;
	}

	@Override
	public ItemKey getRedirectionItemKey() {
		if (redirectionItemKey != null) {
			try {
				return CalmFactory.parseKey(redirectionItemKey);
			} catch (CalException e) {
				LOGGER.error("Unable to build redirection item key : "
						+ redirectionItemKey + " for place id " + id, e);
			}
		}
		return null;
	}

	@Override
	public void setRedirectionItemKey(ItemKey redirectionItemKey) {
		this.redirectionItemKey = redirectionItemKey != null ? redirectionItemKey
				.toString() : null;
	}

	@Override
	public boolean isOnline() {
		return online;
	}

	@Override
	public void setOnline(boolean online) {
		this.online = online;
	}

	@Override
	public boolean isIndexed() {
		return indexed;
	}

	@Override
	public void setIndexed(boolean indexed) {
		this.indexed = indexed;
	}

	@Override
	public Date getLastUpdateTime() {
		return lastUpdate;
	}

	@Override
	public void setLastUpdateTime(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	@Override
	public void setClosedCount(int closedCount) {
		this.closedCount = closedCount;
	}

	@Override
	public int getClosedCount() {
		return closedCount;
	}
}
