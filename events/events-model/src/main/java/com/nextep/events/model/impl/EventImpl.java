package com.nextep.events.model.impl;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.annotations.Type;

import com.nextep.events.model.MutableEvent;
import com.videopolis.calm.base.AbstractCalmObject;
import com.videopolis.calm.exception.CalException;
import com.videopolis.calm.factory.CalmFactory;
import com.videopolis.calm.model.ItemKey;

@Entity
@Table(name = "EVENTS")
public class EventImpl extends AbstractCalmObject implements MutableEvent {

	private static final long serialVersionUID = -7288546385198377224L;
	private static final Log LOGGER = LogFactory.getLog(EventImpl.class);
	@Id
	@GeneratedValue
	@Column(name = "EVNT_ID")
	private long id;
	@Column(name = "EVNT_NAME")
	private String name;
	@Column(name = "EVNT_START_DATE")
	private Date startDate;
	@Column(name = "EVNT_END_DATE")
	private Date endDate;
	@Column(name = "EVNT_DESC")
	private String description;
	@Column(name = "EVNT_PLACE_KEY")
	private String placeKey;
	@Column(name = "EVNT_CITY_KEY")
	private String cityKey;
	@Column(name = "EVNT_SERIES_KEY")
	private String seriesKey;
	@Column(name = "UDATE")
	private Date lastUpdateTime = new Date();
	@Column(name = "AUTHOR_ITEM_KEY")
	private String authorKey;
	@Column(name = "IS_ONLINE")
	@Type(type = "yes_no")
	private boolean isOnline = true;

	public EventImpl() {
		super(null);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Date getStartDate() {
		return startDate;
	}

	@Override
	public Date getEndDate() {
		return endDate;
	}

	@Override
	public ItemKey getLocationKey() {
		try {
			if (placeKey != null) {
				return CalmFactory.parseKey(placeKey);
			} else if (cityKey != null) {
				return CalmFactory.parseKey(cityKey);
			} else {
				return null;
			}
		} catch (CalException e) {
			LOGGER.error("Unable to parse place ItemKey '" + placeKey
					+ "' of event '" + id + "'");
			return null;
		}
	}

	@Override
	public ItemKey getKey() {
		if (id == 0) {
			return null;
		} else {
			try {
				return CalmFactory.createKey(CAL_ID, id);
			} catch (CalException e) {
				LOGGER.error("Unable to create item key for event '" + id + "'");
				return null;
			}
		}
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
		EventImpl other = (EventImpl) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	@Override
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	@Override
	public void setLocationKey(ItemKey locationKey) {
		if (locationKey != null) {
			this.placeKey = locationKey.toString();
		} else {
			this.placeKey = null;
		}
	}

	@Override
	public ItemKey getSeriesKey() {
		if (seriesKey != null) {
			try {
				return CalmFactory.parseKey(seriesKey);
			} catch (CalException e) {
				LOGGER.error("Unable to create parent series key [" + seriesKey
						+ "]: " + e.getMessage());
			}
		}
		return null;
	}

	@Override
	public void setSeriesKey(ItemKey seriesKey) {
		if (seriesKey == null) {
			this.seriesKey = null;
		} else {
			this.seriesKey = seriesKey.toString();
		}
	}

	@Override
	public Date getLastUpdateTime() {
		return lastUpdateTime;
	}

	@Override
	public void setLastUpdateTime(Date lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}

	@Override
	public ItemKey getAuthorKey() {
		try {
			return authorKey == null ? null : CalmFactory.parseKey(authorKey);
		} catch (CalException e) {
			LOGGER.error("Unable to create series author key [" + authorKey
					+ "]: " + e.getMessage());
		}
		return null;
	}

	@Override
	public void setAuthorKey(ItemKey authorKey) {
		this.authorKey = authorKey == null ? null : authorKey.toString();
	}

	@Override
	public boolean isOnline() {
		return isOnline;
	}

	@Override
	public void setOnline(boolean isOnline) {
		this.isOnline = isOnline;
	}
}
