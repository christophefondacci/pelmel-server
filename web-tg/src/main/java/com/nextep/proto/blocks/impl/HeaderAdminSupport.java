package com.nextep.proto.blocks.impl;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Component;

import com.nextep.proto.blocks.HeaderSupport;
import com.nextep.proto.blocks.SearchSupport;
import com.nextep.proto.model.PlaceType;
import com.nextep.proto.model.SearchType;
import com.videopolis.calm.model.CalmObject;

@Component("adminHeaderSupport")
public class HeaderAdminSupport implements HeaderSupport {

	@Override
	public void initialize(Locale locale, CalmObject element,
			SearchSupport searchSupport, SearchType searchType) {
		// TODO Auto-generated method stub

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
		return null;
	}

	@Override
	public List<String> getAvailableLanguages() {
		return Collections.emptyList();
	}

	@Override
	public String getCanonical() {
		return "/admin/places";
	}

	@Override
	public String getAlternate(String language) {
		return "/admin/places";
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
