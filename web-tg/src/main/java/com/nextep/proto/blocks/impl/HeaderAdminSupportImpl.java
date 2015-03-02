package com.nextep.proto.blocks.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import com.nextep.proto.blocks.HeaderSupport;
import com.nextep.proto.blocks.SearchSupport;
import com.nextep.proto.model.PlaceType;
import com.nextep.proto.model.SearchType;
import com.videopolis.calm.model.CalmObject;

public class HeaderAdminSupportImpl implements HeaderSupport {
	private String url;

	@Override
	public void initialize(Locale locale, CalmObject element,
			SearchSupport searchSupport, SearchType searchType) {
		// TODO Auto-generated method stub

	}

	public void setCanonical(String url) {
		this.url = url;
	}

	@Override
	public CalmObject getElement() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getRobotsTags() {
		return "NOINDEX,NOFOLLOW";
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getTitle() {
		return "PELMEL Admin page";
	}

	@Override
	public String getThumbUrl() {
		return null;
	}

	@Override
	public String getFacebookType() {
		return null;
	}

	@Override
	public String getLanguage() {
		return "en";
	}

	@Override
	public List<String> getAvailableLanguages() {
		return Arrays.asList("en");
	}

	@Override
	public String getCanonical() {
		return url;
	}

	@Override
	public String getAlternate(String language) {
		return url;
	}

	@Override
	public String getSearchType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPageStyle() {
		return PlaceType.bar.name();
	}

	@Override
	public String getPageTypeLabel() {
		return PlaceType.bar.name();
	}

}
