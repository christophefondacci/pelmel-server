package com.nextep.smaug.solr.model.impl;

import java.util.Locale;

import org.apache.solr.client.solrj.beans.Field;

import com.nextep.smaug.solr.model.base.AbstractLocalizedSearchItem;
import com.videopolis.calm.exception.CalException;
import com.videopolis.calm.factory.CalmFactory;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.smaug.exception.SearchException;
import com.videopolis.smaug.model.SearchItem;

public class BannerSearchItemImpl extends AbstractLocalizedSearchItem implements
		SearchItem {

	@Field
	private String id;
	@Field
	private String ownerItemKey;
	@Field
	private String targetItemKey;

	private String matchedText;

	@Override
	public ItemKey getKey() {
		try {
			return CalmFactory.parseKey(id);
		} catch (CalException e) {
			throw new SearchException("Unparseable search key: "
					+ e.getMessage());
		}
	}

	public void setKey(ItemKey key) {
		this.id = key.toString();
	}

	@Override
	public int compareTo(SearchItem o) {
		return 0;
	}

	@Override
	public Number getInfo(String type) {
		return null;
	}

	@Override
	public String getMatchedText() {
		return matchedText;
	}

	public void setMatchedText(String matchedText) {
		this.matchedText = matchedText;
	}

	@Override
	public Locale getMatchedLocale() {
		return null;
	}

	public void setOwnerItemKey(ItemKey ownerItemKey) {
		this.ownerItemKey = ownerItemKey == null ? null : ownerItemKey
				.toString();
	}

	public void setTargetItemKey(ItemKey targetItemKey) {
		this.targetItemKey = targetItemKey == null ? null : targetItemKey
				.toString();
	}

}
