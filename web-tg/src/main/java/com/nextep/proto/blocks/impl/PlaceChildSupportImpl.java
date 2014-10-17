package com.nextep.proto.blocks.impl;

import java.util.Locale;

import org.springframework.context.MessageSource;

import com.nextep.geo.model.City;
import com.nextep.geo.model.Place;
import com.nextep.proto.blocks.ChildSupport;
import com.nextep.proto.helpers.DisplayHelper;
import com.nextep.proto.services.UrlService;
import com.videopolis.calm.model.CalmObject;

public class PlaceChildSupportImpl implements ChildSupport {

	private static final String KEY_ADD_PLACE_ACTION = "action.add.placeChild";
	private static final String KEY_ADD_PLACE_ICON = "action.icon.add.placeChild";

	private MessageSource messageSource;

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
	public String getAddLabel(String childCalType) {
		return messageSource.getMessage(KEY_ADD_PLACE_ACTION,
				new Object[] { DisplayHelper.getName(parent) }, locale);
	}

	@Override
	public String getAddUrl(String placeType) {
		return urlService.getPlaceEditionFormUrl(
				DisplayHelper.getDefaultAjaxContainer(), parent.getKey(),
				placeType);
	}

	@Override
	public String getAddIconUrl(String childCalType) {
		return messageSource.getMessage(KEY_ADD_PLACE_ICON, null, locale);
	}

	@Override
	public boolean canAddChildFor(String childCalType) {
		return Place.CAL_TYPE.equals(childCalType) && (parent instanceof City);
	}

	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}
}
