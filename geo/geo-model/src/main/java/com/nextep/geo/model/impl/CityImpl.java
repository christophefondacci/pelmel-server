package com.nextep.geo.model.impl;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.nextep.geo.model.Admin;
import com.nextep.geo.model.City;
import com.nextep.geo.model.Country;
import com.videopolis.calm.base.AbstractCalmObject;
import com.videopolis.calm.exception.CalException;
import com.videopolis.calm.factory.CalmFactory;
import com.videopolis.calm.model.ItemKey;

@Entity
@Table(name = "GEO_CITIES")
public class CityImpl extends AbstractCalmObject implements City {

	private static final long serialVersionUID = -7256496078947512422L;

	@Id
	@Column(name = "CITY_ID")
	private long id;

	@Column(name = "CITY_NAME")
	private String name;

	@ManyToOne(optional = false, targetEntity = CountryImpl.class)
	@JoinColumn(name = "COUNTRY_CODE")
	private Country country;

	@ManyToOne(optional = true, targetEntity = AdmImpl.class)
	@JoinColumn(name = "ADM1_ID")
	private Admin adm1;

	@ManyToOne(optional = true, targetEntity = AdmImpl.class)
	@JoinColumn(name = "ADM2_ID")
	private Admin adm2;

	@Column(name = "GEO_LATITUDE")
	private double latitude;

	@Column(name = "GEO_LONGITUDE")
	private double longitude;

	@Column(name = "POPULATION")
	private int population;

	@Column(name = "TIMEZONE_ID")
	private String timezoneId;

	public CityImpl() {
		super(null);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Country getCountry() {
		return country;
	}

	@Override
	public Admin getAdm1() {
		return adm1;
	}

	@Override
	public Admin getAdm2() {
		return adm2;
	}

	@Override
	public double getLatitude() {
		return latitude;
	}

	@Override
	public double getLongitude() {
		return longitude;
	}

	@Override
	public ItemKey getKey() {
		try {
			return CalmFactory.createKey(CAL_ID, id);
		} catch (CalException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public int getPopulation() {
		return population;
	}

	@Override
	public Long getGeonameId() {
		return id;
	}

	@Override
	public String getTimezoneId() {
		return timezoneId;
	}
}
