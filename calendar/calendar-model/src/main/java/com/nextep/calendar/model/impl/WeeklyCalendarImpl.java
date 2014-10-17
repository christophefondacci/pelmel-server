package com.nextep.calendar.model.impl;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.annotations.Type;

import com.nextep.calendar.model.MutableWeeklyCalendar;
import com.videopolis.calm.base.AbstractCalmObject;
import com.videopolis.calm.exception.CalException;
import com.videopolis.calm.factory.CalmFactory;
import com.videopolis.calm.model.ItemKey;

@Entity
@Table(name = "CAL_WEEKLY_CALENDARS")
public class WeeklyCalendarImpl extends AbstractCalmObject implements
		MutableWeeklyCalendar {

	private static final long serialVersionUID = -5336855360717233457L;
	private static final Log LOGGER = LogFactory
			.getLog(WeeklyCalendarImpl.class);

	@Id
	@GeneratedValue
	@Column(name = "CALENDAR_ID")
	private Long calendarId;

	@Column(name = "START_DATE")
	private Date startDate;

	@Column(name = "END_DATE")
	private Date endDate;

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

	@Column(name = "CALENDAR_TYPE")
	private String calendarType;

	@Column(name = "PARENT_ITEM_KEY")
	private String relatedItemKey;

	public WeeklyCalendarImpl() {
		super(null);
	}

	@Override
	public ItemKey getKey() {
		if (calendarId != null) {
			try {
				return CalmFactory.createKey(CAL_ID, calendarId);
			} catch (CalException e) {
				LOGGER.error("Unable to build calendar item key from id ["
						+ calendarId + "]: " + e.getMessage());
			}
		}
		return null;
	}

	public Long getCalendarId() {
		return calendarId;
	}

	public void setCalendarId(Long calendarId) {
		this.calendarId = calendarId;
	}

	@Override
	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	@Override
	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
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
	public void setTuesday(boolean isTuesday) {
		this.isTuesday = isTuesday;
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
	public void setCalendarType(String calendarType) {
		this.calendarType = calendarType;
	}

	@Override
	public String getCalendarType() {
		return calendarType;
	}

	@Override
	public ItemKey getRelatedItemKey() {
		try {
			return CalmFactory.parseKey(relatedItemKey);
		} catch (CalException e) {
			LOGGER.error("Unable to parse calendar related key : "
					+ relatedItemKey, e);
			return null;
		}
	}

	@Override
	public void setRelatedItemKey(ItemKey parentItemKey) {
		if (parentItemKey == null) {
			relatedItemKey = null;
		} else {
			relatedItemKey = parentItemKey.toString();
		}
	}
}
