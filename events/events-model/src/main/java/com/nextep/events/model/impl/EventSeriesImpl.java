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

import com.nextep.events.model.CalendarType;
import com.nextep.events.model.MutableEventSeries;
import com.videopolis.calm.base.AbstractCalmObject;
import com.videopolis.calm.exception.CalException;
import com.videopolis.calm.factory.CalmFactory;
import com.videopolis.calm.model.ItemKey;

@Entity
@Table(name = "EVENTS_SERIES")
public class EventSeriesImpl extends AbstractCalmObject implements
		MutableEventSeries {

	private static final long serialVersionUID = 7939172366927627865L;
	private static final Log LOGGER = LogFactory.getLog(EventSeriesImpl.class);

	@Id
	@GeneratedValue
	@Column(name = "ESERIE_ID")
	private Long seriesId;

	@Column(name = "SERIES_START_DATE")
	private Date seriesStart;

	@Column(name = "SERIES_END_DATE")
	private Date seriesEnd;

	@Column(name = "WEEK_OFFSET")
	private Integer weekOfMonthOffset;

	@Column(name = "IS_MONDAY")
	@Type(type = "yes_no")
	private boolean isMonday;

	@Column(name = "IS_TUESDAY")
	@Type(type = "yes_no")
	private boolean isTuesday;

	@Column(name = "IS_WEDNESDAY")
	@Type(type = "yes_no")
	private boolean isWednesday;

	@Column(name = "IS_THURSDAY")
	@Type(type = "yes_no")
	private boolean isThursday;

	@Column(name = "IS_FRIDAY")
	@Type(type = "yes_no")
	private boolean isFriday;

	@Column(name = "IS_SATURDAY")
	@Type(type = "yes_no")
	private boolean isSaturday;

	@Column(name = "IS_SUNDAY")
	@Type(type = "yes_no")
	private boolean isSunday;

	@Column(name = "START_HOUR")
	private int startHour;

	@Column(name = "START_MINUTE")
	private int startMinute;

	@Column(name = "END_HOUR")
	private int endHour;

	@Column(name = "END_MINUTE")
	private int endMinute;

	@Column(name = "EVNT_NAME")
	private String name;

	@Column(name = "EVNT_PLACE_KEY")
	private String placeKey;

	@Column(name = "CALENDAR_TYPE")
	private String calendarType;

	public EventSeriesImpl() {
		super(null);
	}

	@Override
	public ItemKey getKey() {
		if (seriesId != null) {
			try {
				return CalmFactory.createKey(SERIES_CAL_ID, seriesId);
			} catch (CalException e) {
				LOGGER.error("Unable to build event series item key from id ["
						+ seriesId + "]: " + e.getMessage());
			}
		}
		return null;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setStartDate(Date seriesStart) {
		this.seriesStart = seriesStart;
	}

	@Override
	public Date getStartDate() {
		return seriesStart;
	}

	@Override
	public void setEndDate(Date endDate) {
		this.seriesEnd = endDate;
	}

	@Override
	public Date getEndDate() {
		return seriesEnd;
	}

	@Override
	public Integer getWeekOfMonthOffset() {
		return weekOfMonthOffset;
	}

	@Override
	public void setWeekOfMonthOffset(Integer weekOfMonthOffset) {
		this.weekOfMonthOffset = weekOfMonthOffset;
	}

	@Override
	public boolean isMonday() {
		return isMonday;
	}

	@Override
	public void setMonday(boolean isMonday) {
		this.isMonday = isMonday;
	}

	@Override
	public boolean isTuesday() {
		return isTuesday;
	}

	@Override
	public void setTuesday(boolean isTuseday) {
		this.isTuesday = isTuseday;
	}

	@Override
	public boolean isWednesday() {
		return isWednesday;
	}

	@Override
	public void setWednesday(boolean isWednesday) {
		this.isWednesday = isWednesday;
	}

	@Override
	public boolean isThursday() {
		return isThursday;
	}

	@Override
	public void setThursday(boolean isThursday) {
		this.isThursday = isThursday;
	}

	@Override
	public boolean isFriday() {
		return isFriday;
	}

	@Override
	public void setFriday(boolean isFriday) {
		this.isFriday = isFriday;
	}

	@Override
	public boolean isSaturday() {
		return isSaturday;
	}

	@Override
	public void setSaturday(boolean isSaturday) {
		this.isSaturday = isSaturday;
	}

	@Override
	public boolean isSunday() {
		return isSunday;
	}

	@Override
	public void setSunday(boolean isSunday) {
		this.isSunday = isSunday;
	}

	@Override
	public int getStartHour() {
		return startHour;
	}

	@Override
	public void setStartHour(int startHour) {
		this.startHour = startHour;
	}

	@Override
	public int getStartMinute() {
		return startMinute;
	}

	@Override
	public void setStartMinute(int startMinute) {
		this.startMinute = startMinute;
	}

	@Override
	public int getEndHour() {
		return endHour;
	}

	@Override
	public void setEndHour(int endHour) {
		this.endHour = endHour;
	}

	@Override
	public int getEndMinute() {
		return endMinute;
	}

	@Override
	public void setEndMinute(int endMinute) {
		this.endMinute = endMinute;
	}

	@Override
	public ItemKey getLocationKey() {
		if (placeKey != null) {
			try {
				return CalmFactory.parseKey(placeKey);
			} catch (CalException e) {
				LOGGER.error("Unable to parse place item key for event series : "
						+ e.getMessage());
			}
		}
		return null;
	}

	@Override
	public void setLocationKey(ItemKey placeKey) {
		if (placeKey != null) {
			this.placeKey = placeKey.toString();
		} else {
			this.placeKey = null;
		}
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public ItemKey getSeriesKey() {
		return null;
	}

	@Override
	public void setSeriesKey(ItemKey seriesKey) {
		throw new UnsupportedOperationException();
	}

	@Override
	public CalendarType getCalendarType() {
		// Initializing default
		CalendarType type = CalendarType.EVENT;
		try {
			if (calendarType != null) {
				type = CalendarType.valueOf(calendarType.trim());
			}
		} catch (IllegalArgumentException e) {
			LOGGER.warn("Invalid calendar type '" + calendarType
					+ "' for event", e);
		}
		return type;
	}

	@Override
	public void setCalendarType(CalendarType calendarType) {
		if (calendarType != null) {
			this.calendarType = calendarType.name();
		} else {
			LOGGER.warn("Trying to assign unknown calendar type '"
					+ calendarType + "' to event");
			this.calendarType = CalendarType.EVENT.name();
		}
	}
}
