package com.nextep.descriptions.model.impl;

import java.util.Date;
import java.util.Locale;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.nextep.descriptions.model.MutableDescription;
import com.videopolis.calm.base.AbstractCalmObject;
import com.videopolis.calm.exception.CalException;
import com.videopolis.calm.factory.CalmFactory;
import com.videopolis.calm.model.ItemKey;

@Entity
@Table(name = "DESCRIPTIONS")
public class DescriptionImpl extends AbstractCalmObject implements
		MutableDescription {

	private static final long serialVersionUID = 4672417292681077816L;
	private static final Log LOGGER = LogFactory.getLog(DescriptionImpl.class);

	@GeneratedValue
	@Id
	@Column(name = "DESC_ID")
	private long id;
	@Column(name = "DESCRIPTION_TEXT")
	private String description;
	@Column(name = "DESC_DATE")
	private Date date;
	@Column(name = "LANGUAGE_ISO_639_1")
	private String languageIso6391;
	@Column(name = "COUNTRY_ISO_3166_ALPHA_2")
	private String countryIso3166Alpha2;
	@Column(name = "ITEM_KEY")
	private String describedItemKey;
	@Column(name = "SOURCE_ID")
	private int sourceId = 1000;

	public DescriptionImpl() {
		super(null);
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public Date getDate() {
		return date;
	}

	@Override
	public Locale getLocale() {
		if (languageIso6391 != null) {
			if (countryIso3166Alpha2 != null) {
				return new Locale(languageIso6391, countryIso3166Alpha2);
			} else {
				return new Locale(languageIso6391);
			}
		} else {
			return null;
		}
	}

	@Override
	public ItemKey getKey() {
		if (id == 0) {
			return null;
		} else {
			try {
				return CalmFactory.createKey(CAL_TYPE, id);
			} catch (CalException e) {
				return null;
			}
		}
	}

	@Override
	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public void setDate(Date date) {
		this.date = date;
	}

	@Override
	public void setLocale(Locale l) {
		this.languageIso6391 = l.getLanguage();
		this.countryIso3166Alpha2 = l.getCountry();
	}

	@Override
	public void setDescribedItemKey(ItemKey describedItemKey) {
		this.describedItemKey = describedItemKey.toString();
	}

	@Override
	public ItemKey getDescribedItemKey() {
		try {
			return CalmFactory.parseKey(describedItemKey);
		} catch (CalException e) {
			LOGGER.error("Unable to parse described item key '"
					+ describedItemKey + "' : " + e.getMessage());
			return null;
		}
	}

	@Override
	public void setSourceId(int sourceId) {
		this.sourceId = sourceId;
	}

	@Override
	public int getSourceId() {
		return sourceId;
	}
}
