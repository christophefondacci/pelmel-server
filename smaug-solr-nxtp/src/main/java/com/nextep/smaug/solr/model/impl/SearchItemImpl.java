package com.nextep.smaug.solr.model.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.solr.client.solrj.beans.Field;

import com.videopolis.calm.exception.CalException;
import com.videopolis.calm.factory.CalmFactory;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.smaug.exception.SearchException;
import com.videopolis.smaug.model.SearchItem;

public class SearchItemImpl implements SearchItem {

	@Field
	private String id;
	@Field
	private String name;

	@Field
	private final List<String> tags = new ArrayList<String>();

	@Field
	private String continentId;
	@Field
	private String countryId;
	@Field
	private String adm1;
	@Field
	private String adm2;
	@Field
	private String cityId;
	@Field
	private String geo_distance;

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

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public int compareTo(SearchItem o) {
		return 0;
	}

	@Override
	public Number getInfo(String type) {
		if (SearchItem.DISTANCE.equals(type)
				|| SearchItem.GEO_DISTANCE.equals(type)) {
			return Double.valueOf(geo_distance);
		} else {
			return null;
		}
	}

	@Override
	public String getMatchedText() {
		return matchedText;
	}

	@Override
	public Locale getMatchedLocale() {
		return null;
	}

	public void addTag(String tag) {
		this.tags.add(tag);
	}

	public void setContinentId(String continentId) {
		this.continentId = continentId;
	}

	public void setCountryId(String countryId) {
		this.countryId = countryId;
	}

	public void setAdm1(String adm1) {
		this.adm1 = adm1;
	}

	public void setAdm2(String adm2) {
		this.adm2 = adm2;
	}

	public void setCityId(String cityId) {
		this.cityId = cityId;
	}

	public void setMatchedText(String matchedText) {
		this.matchedText = matchedText;
	}

}
