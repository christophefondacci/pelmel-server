package com.nextep.properties.model.impl;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.nextep.cal.util.helpers.CalHelper;
import com.nextep.properties.model.MutableProperty;
import com.videopolis.calm.base.AbstractCalmObject;
import com.videopolis.calm.exception.CalException;
import com.videopolis.calm.factory.CalmFactory;
import com.videopolis.calm.model.ItemKey;

@Entity
@Table(name = "ITEM_PROPERTIES")
public class PropertyImpl extends AbstractCalmObject implements MutableProperty {

	private static final long serialVersionUID = -3145814387195217499L;
	private static final Log LOGGER = LogFactory.getLog(PropertyImpl.class);

	@GeneratedValue
	@Id
	@Column(name = "PROPERTY_ID")
	private long id;

	@Column(name = "ITEM_KEY")
	private String parentItemKey;

	@Column(name = "PROPERTY_CODE")
	private String code;
	@Column(name = "PROPERTY_LABEL")
	private String label;
	@Column(name = "PROPERTY_VALUE")
	private String value;

	public PropertyImpl() {
		super(null);
	}

	@Override
	public ItemKey getParentItemKey() {
		try {
			return CalmFactory.parseKey(parentItemKey);
		} catch (CalException e) {
			LOGGER.error("Unable to parse property's parent item key : "
					+ parentItemKey);
		}
		return null;
	}

	@Override
	public String getCode() {
		return code;
	}

	@Override
	public String getLabel() {
		return label;
	}

	@Override
	public String getValue() {
		return value;
	}

	@Override
	public ItemKey getKey() {
		if (id == 0) {
			return null;
		} else {
			return CalHelper.getItemKeyFromId(CAL_TYPE, id);
		}
	}

	@Override
	public void setParentItemKey(ItemKey parentItemKey) {
		this.parentItemKey = parentItemKey.toString();
	}

	@Override
	public void setCode(String code) {
		this.code = code;
	}

	@Override
	public void setLabel(String label) {
		this.label = label;
	}

	@Override
	public void setValue(String value) {
		this.value = value;
	}
}
