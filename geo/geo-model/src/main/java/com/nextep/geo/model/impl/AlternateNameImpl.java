package com.nextep.geo.model.impl;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.nextep.geo.model.AlternateName;
import com.videopolis.calm.base.AbstractCalmObject;
import com.videopolis.calm.exception.CalException;
import com.videopolis.calm.factory.CalmFactory;
import com.videopolis.calm.model.ItemKey;

@Entity
@IdClass(AlternateNamePKImpl.class)
@Table(name = "GEO_ALTERNATE_NAMES")
public class AlternateNameImpl extends AbstractCalmObject implements
		AlternateName {

	private static final Log LOGGER = LogFactory
			.getLog(AlternateNameImpl.class);
	private static final long serialVersionUID = 6236307831277167641L;

	@Id
	@Column(name = "GEONAME_ID")
	private long geonameId = -1;
	@Id
	@Column(name = "LANGUAGE_ISO_639_1")
	private String language;
	@Column(name = "ALTERNATE_NAME")
	private String alternateName;

	public AlternateNameImpl() {
		super(null);
	}

	@Override
	public String getLanguage() {
		return language;
	}

	@Override
	public String getAlternameName() {
		return alternateName;
	}

	@Override
	public ItemKey getKey() {
		if (geonameId != -1) {
			try {
				return CalmFactory.createKey(AlternateName.CAL_TYPE, language
						+ geonameId);
			} catch (CalException e) {
				LOGGER.error("Unable to build key for alternate name : "
						+ language + " - " + geonameId);
				return null;
			}
		}
		return null;
	}

	@Override
	public long getParentGeonameId() {
		return geonameId;
	}
}
