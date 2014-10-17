package com.nextep.geo.model.impl;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.nextep.geo.model.Continent;
import com.nextep.geo.model.Country;
import com.videopolis.calm.base.AbstractCalmObject;
import com.videopolis.calm.exception.CalException;
import com.videopolis.calm.factory.CalmFactory;
import com.videopolis.calm.model.ItemKey;

@Entity
@Table(name = "GEO_COUNTRIES")
public class CountryImpl extends AbstractCalmObject implements Country {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3624561282711536806L;

	@Id
	@Column(name = "COUNTRY_CODE")
	private String code;

	@Column(name = "COUNTRY_NAME")
	private String name;

	@ManyToOne(optional = false, targetEntity = ContinentImpl.class)
	@JoinColumn(name = "CONTINENT_CODE")
	private Continent continent;

	@Column(name = "GEONAME_ID")
	private Long geonameId;

	public CountryImpl() {
		super(null);
	}

	public CountryImpl(String countryCode) throws CalException {
		super(CalmFactory.createKey(CAL_ID, countryCode));
		this.code = countryCode;
	}

	@Override
	public ItemKey getKey() {
		try {
			return CalmFactory.createKey(CAL_ID, code);
		} catch (CalException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Continent getContinent() {
		return continent;
	}

	@Override
	public String getCode() {
		return code;
	};

	@Override
	public Long getGeonameId() {
		return geonameId;
	}
}
