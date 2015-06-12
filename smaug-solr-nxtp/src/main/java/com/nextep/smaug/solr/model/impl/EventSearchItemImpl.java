package com.nextep.smaug.solr.model.impl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.solr.client.solrj.beans.Field;

public class EventSearchItemImpl extends SearchItemImpl {

	private static final DateFormat DATE_FORMAT = new SimpleDateFormat(
			"yyyyMMddHHmmss");

	@Field
	private String placeId;

	@Field
	private String start_date;

	@Field
	private String end_date;

	public void setPlaceId(String placeId) {
		this.placeId = placeId;
	}

	public void setStart(Date startDate) {
		this.start_date = DATE_FORMAT.format(startDate);
	}

	public void setEnd(Date endDate) {
		if (endDate != null) {
			this.end_date = DATE_FORMAT.format(endDate);
		} else if (start_date != null) {
			// We will set end date as start day + 1 at 0:00

			// Extracting YYYYMMDD starting part
			long start = Long.parseLong(start_date.substring(1, 9));
			// Adding 6 zeros for HHMMDD and assigning end date
			end_date = String.valueOf((start + 1) * 1000000);
		} else {
			end_date = null;
		}
	}

}
