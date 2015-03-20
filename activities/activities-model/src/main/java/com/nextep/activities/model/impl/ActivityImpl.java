package com.nextep.activities.model.impl;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.annotations.Type;

import com.nextep.activities.model.Activity;
import com.nextep.activities.model.ActivityType;
import com.nextep.activities.model.MutableActivity;
import com.videopolis.calm.base.AbstractCalmObject;
import com.videopolis.calm.exception.CalException;
import com.videopolis.calm.factory.CalmFactory;
import com.videopolis.calm.model.ItemKey;

@Entity
@Table(name = "ACTIVITIES")
public class ActivityImpl extends AbstractCalmObject implements Activity,
		MutableActivity {

	private static final long serialVersionUID = -7876382759957297477L;
	private static final Log LOGGER = LogFactory.getLog(ActivityImpl.class);

	@GeneratedValue
	@Id
	@Column(name = "ACTIVITY_ID")
	private long id;
	@Column(name = "ACTIVITY_DATE")
	private Date date = new Date();
	@Column(name = "USER_KEY")
	private String userKey;
	@Column(name = "ITEM_KEY")
	private String loggedItemKey;
	@Column(name = "EXTRA")
	private String extraInformation;
	@Column(name = "ACTIVITY_TYPE")
	private String activityType;
	@Column(name = "IS_VISIBLE")
	@Type(type = "yes_no")
	private boolean visible = true;

	public ActivityImpl() {
		super(null);
	}

	@Override
	public Date getDate() {
		return date;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.nextep.activities.model.impl.MutableActivity#setDate(java.util.Date)
	 */
	@Override
	public void setDate(Date date) {
		this.date = date;
	}

	@Override
	public ItemKey getUserKey() {
		try {
			return CalmFactory.parseKey(userKey);
		} catch (CalException e) {
			LOGGER.error("Unable to parse user item key '" + userKey
					+ "' for activity " + id);
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.nextep.activities.model.impl.MutableActivity#setUserKey(com.videopolis
	 * .calm.model.ItemKey)
	 */
	@Override
	public void setUserKey(ItemKey userKey) {
		this.userKey = userKey.toString();
	}

	@Override
	public ItemKey getLoggedItemKey() {
		try {
			return CalmFactory.parseKey(loggedItemKey);
		} catch (CalException e) {
			LOGGER.error("Unable to parse logged item key '" + loggedItemKey
					+ "' for activity " + id);
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.nextep.activities.model.impl.MutableActivity#setLoggedItemKey(com
	 * .videopolis.calm.model.ItemKey)
	 */
	@Override
	public void setLoggedItemKey(ItemKey loggedItemKey) {
		this.loggedItemKey = loggedItemKey.toString();
	}

	@Override
	public String getExtraInformation() {
		return extraInformation;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.nextep.activities.model.impl.MutableActivity#setExtraInformation(
	 * java.lang.String)
	 */
	@Override
	public void setExtraInformation(String extraInformation) {
		this.extraInformation = extraInformation;
	}

	@Override
	public ActivityType getActivityType() {
		return ActivityType.fromCode(activityType);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.nextep.activities.model.impl.MutableActivity#setActivityType(com.
	 * nextep.activities.model.ActivityType)
	 */
	@Override
	public void setActivityType(ActivityType activityType) {
		this.activityType = activityType.getCode();
	}

	@Override
	public ItemKey getKey() {
		if (id == 0) {
			return null;
		}
		try {
			return CalmFactory.createKey(CAL_TYPE, id);
		} catch (CalException e) {
			LOGGER.error("Unable to create item key for activity '" + id + "'");
			return null;
		}
	}

	@Override
	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	@Override
	public boolean isVisible() {
		return visible;
	}
}