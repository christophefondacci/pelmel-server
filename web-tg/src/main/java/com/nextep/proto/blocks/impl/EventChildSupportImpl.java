package com.nextep.proto.blocks.impl;

import java.util.Locale;

import com.nextep.proto.blocks.ChildSupport;
import com.nextep.proto.helpers.DisplayHelper;
import com.nextep.proto.services.UrlService;
import com.videopolis.calm.model.CalmObject;

public class EventChildSupportImpl implements ChildSupport {

	private Locale locale;
	private UrlService urlService;
	private CalmObject parent;

	@Override
	public void initialize(Locale locale, UrlService urlService,
			CalmObject parent) {
		this.locale = locale;
		this.urlService = urlService;
		this.parent = parent;
	}

	@Override
	public boolean canAddChildFor(String childCalType) {
		return true;
	}

	@Override
	public String getAddLabel(String childCalType) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getAddUrl(String childCalType) {
		return urlService.getEventEditionFormUrl(
				DisplayHelper.getDefaultAjaxContainer(), parent.getKey());
	}

	@Override
	public String getAddIconUrl(String childCalType) {
		// TODO Auto-generated method stub
		return null;
	}

}
