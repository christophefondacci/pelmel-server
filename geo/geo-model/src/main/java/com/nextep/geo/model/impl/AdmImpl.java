package com.nextep.geo.model.impl;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.nextep.geo.model.Admin;
import com.nextep.geo.model.Country;
import com.videopolis.calm.base.AbstractCalmObject;
import com.videopolis.calm.exception.CalException;
import com.videopolis.calm.factory.CalmFactory;
import com.videopolis.calm.model.ItemKey;

@Entity
@Table(name = "GEO_ADMINS")
public class AdmImpl extends AbstractCalmObject implements Admin {

	private static final long serialVersionUID = -5851471265648123422L;

	@Id
	@Column(name = "ADM_ID")
	private String admId;

	@Column(name = "ADM_CODE")
	private String admCode;

	@ManyToOne(optional = false, targetEntity = CountryImpl.class)
	@JoinColumn(name = "COUNTRY_CODE")
	private Country country;

	@Column(name = "ADM_NAME")
	private String name;

	@ManyToOne(optional = true, targetEntity = AdmImpl.class)
	@JoinColumn(name = "PARENT_ADM_ID")
	private Admin parentAdm;

	@Column(name = "GEONAME_ID")
	private Long geonameId;

	public AdmImpl() {
		super(null);
	}

	@Override
	public ItemKey getKey() {
		try {
			return CalmFactory.createKey(CAL_ID, admId);
		} catch (CalException e) {
			throw new RuntimeException(e);
		}
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
	public String getAdminCode() {
		return admCode;
	}

	@Override
	public Admin getAdm1() {
		return parentAdm;
	}

	@Override
	public Long getGeonameId() {
		return geonameId;
	}
}
