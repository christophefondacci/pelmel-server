package com.nextep.smaug.solr.model.impl;

import org.apache.solr.client.solrj.beans.Field;

import com.nextep.smaug.solr.model.base.AbstractLocalizedSearchItem;

public class CitiesSearchItemImpl extends AbstractLocalizedSearchItem {

	@Field
	private String id;

	public CitiesSearchItemImpl(String id) {
		this.id = id;
	}

}
