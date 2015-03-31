package com.nextep.smaug.solr.model.impl;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.solr.client.solrj.beans.Field;

import com.videopolis.calm.exception.CalException;
import com.videopolis.calm.factory.CalmFactory;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.smaug.exception.SearchException;
import com.videopolis.smaug.model.SearchItem;

public class SearchTextItemImpl implements SearchItem {

	@Field
	private String id;
	@Field
	private List<String> name;
	@Field
	private String type;
	@Field
	private Collection<String> cityName;
	@Field
	private Collection<String> stateName;
	@Field
	private Collection<String> countryName;
	@Field
	private Long expirationTime;
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

	public void setId(String id) {
		this.id = id;
	}

	public void setNames(List<String> names) {
		this.name = names;
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

	public void setType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

	public void setCityName(Collection<String> cityName) {
		this.cityName = cityName;
	}

	public void setStateName(Collection<String> stateName) {
		this.stateName = stateName;
	}

	public void setCountryName(Collection<String> countryName) {
		this.countryName = countryName;
	}

	public void setExpirationTime(Date expirationDate) {
		this.expirationTime = expirationDate == null ? null : expirationDate
				.getTime();
	}
}
