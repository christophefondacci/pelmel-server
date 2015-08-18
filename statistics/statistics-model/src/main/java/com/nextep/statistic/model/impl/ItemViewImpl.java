package com.nextep.statistic.model.impl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.nextep.statistic.model.MutableItemView;
import com.videopolis.calm.base.AbstractCalmObject;
import com.videopolis.calm.exception.CalException;
import com.videopolis.calm.factory.CalmFactory;
import com.videopolis.calm.model.ItemKey;

@Entity
@IdClass(value = ItemViewPK.class)
@Table(name = "STAT_VIEWS")
public class ItemViewImpl extends AbstractCalmObject implements MutableItemView {

	private static final long serialVersionUID = 3232642302332321504L;
	private static final Log LOGGER = LogFactory.getLog(ItemViewImpl.class);
	private static final DateFormat ID_DATE_FORMAT = new SimpleDateFormat("yyyyMMddHHmmss");

	@Id
	@Column(name = "ITEM_KEY_VIEWED")
	private String viewedItemKey;

	@Column(name = "ITEM_KEY_VIEWER")
	private String viewerItemKey;

	@Id
	@Column(name = "VIEW_DATE")
	private Date viewDate;

	@Id
	@Column(name = "VIEW_TYPE")
	private String viewType;

	private transient int count;

	public ItemViewImpl() {
		super(null);
	}

	@Override
	public ItemKey getKey() {
		if (viewedItemKey != null) {
			try {
				return CalmFactory.createKey(CAL_TYPE, viewedItemKey + viewerItemKey + ID_DATE_FORMAT.format(viewDate));
			} catch (CalException e) {
				LOGGER.error("Unable to build fake ItemView item key " + viewedItemKey + "/" + viewerItemKey + "/"
						+ viewDate + " : " + e.getMessage());
			}
		}
		return null;
	}

	@Override
	public ItemKey getViewedItemKey() {
		if (viewedItemKey != null) {
			try {
				return CalmFactory.parseKey(viewedItemKey);
			} catch (CalException e) {
				LOGGER.error("Unable to parse CAL key '" + viewedItemKey + "' : " + e.getMessage());
			}
		}
		return null;
	}

	@Override
	public ItemKey getViewerItemKey() {
		if (viewerItemKey != null) {
			try {
				return CalmFactory.parseKey(viewerItemKey);
			} catch (CalException e) {
				LOGGER.error("Unable to parse CAL key '" + viewerItemKey + "' : " + e.getMessage());
			}
		}
		return null;
	}

	@Override
	public Date getViewDate() {
		return viewDate;
	}

	@Override
	public void setViewDate(Date viewDate) {
		this.viewDate = viewDate;
	}

	@Override
	public void setViewedItemKey(ItemKey viewedItemKey) {
		this.viewedItemKey = viewedItemKey == null ? null : viewedItemKey.toString();
	}

	@Override
	public void setViewerItemKey(ItemKey viewerItemKey) {
		this.viewerItemKey = viewerItemKey == null ? null : viewerItemKey.toString();
	}

	@Override
	public String getViewType() {
		return viewType;
	}

	@Override
	public void setViewType(String viewType) {
		this.viewType = viewType;
	}

	@Override
	public void setCount(int count) {
		this.count = count;
	}

	@Override
	public int getCount() {
		return count;
	}
}
