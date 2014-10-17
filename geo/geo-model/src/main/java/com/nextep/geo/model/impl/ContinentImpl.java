package com.nextep.geo.model.impl;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.nextep.geo.model.Continent;
import com.videopolis.calm.base.AbstractCalmObject;
import com.videopolis.calm.exception.CalException;
import com.videopolis.calm.factory.CalmFactory;
import com.videopolis.calm.model.ItemKey;

@Entity
@Table(name = "GEO_CONTINENTS")
public class ContinentImpl extends AbstractCalmObject implements Continent {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2367837841700161299L;

	@Id
	@Column(name = "CONTINENT_CODE")
	private String code;

	@Column(name = "CONTINENT_NAME")
	private String name;

	@Column(name = "GEONAME_ID")
	private Long geonameId;

	protected ContinentImpl() {
		super(null);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public ItemKey getKey() {
		try {
			return CalmFactory.createKey(Continent.CAL_ID, code);
		} catch (CalException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Long getGeonameId() {
		return geonameId;
	}
}
