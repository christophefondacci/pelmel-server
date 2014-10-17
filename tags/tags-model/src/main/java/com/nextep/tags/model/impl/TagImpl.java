package com.nextep.tags.model.impl;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.nextep.tags.model.Tag;
import com.videopolis.calm.base.AbstractCalmObject;
import com.videopolis.calm.exception.CalException;
import com.videopolis.calm.factory.CalmFactory;
import com.videopolis.calm.model.ItemKey;

@Entity
@Table(name = "TAGS")
public class TagImpl extends AbstractCalmObject implements Tag {

	private static final Log log = LogFactory.getLog(TagImpl.class);
	private static final long serialVersionUID = -4425394412518251695L;

	@Id
	@Column(name = "TAG_ID")
	private String id;
	@Column(name = "TAG_NAME")
	private String code;
	@Column(name = "DESCRIPTION")
	private String description;
	@Column(name = "DISPLAY_MODE")
	private String displayMode;

	public TagImpl() {
		super(null);
	}

	@Override
	public String getCode() {
		return code;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public ItemKey getKey() {
		try {
			return CalmFactory.createKey(CAL_ID, id);
		} catch (CalException e) {
			log.error("Unable to build tag CAL key : " + e);
			return null;
		}
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Tag) {
			return id.equals(((Tag) obj).getKey().getId());
		} else {
			return false;
		}
	}

	@Override
	public String toString() {
		return "(" + id + ":" + code + ")";
	}

	@Override
	public String getDisplayMode() {
		return displayMode;
	}
}
