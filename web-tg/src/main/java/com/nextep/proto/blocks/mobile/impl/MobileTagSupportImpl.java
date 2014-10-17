package com.nextep.proto.blocks.mobile.impl;

import java.util.List;
import java.util.Locale;

import com.nextep.proto.blocks.TagSupport;
import com.nextep.tags.model.Tag;
import com.videopolis.calm.model.CalmObject;

public class MobileTagSupportImpl implements TagSupport {

	private TagSupport baseTagSupport;
	private String baseUrl;

	@Override
	public void initialize(Locale locale, List<Tag> availableTags) {
		baseTagSupport.initialize(locale, availableTags);
	}

	public void setBaseTagSupport(TagSupport baseTagSupport) {
		this.baseTagSupport = baseTagSupport;
	}

	@Override
	public String getTagTranslation(Tag tag) {
		return "";
	}

	@Override
	public String getTagIconUrl(Tag tag) {
		return baseUrl + baseTagSupport.getTagIconUrl(tag);
	}

	@Override
	public List<Tag> getAvailableTags() {
		return baseTagSupport.getAvailableTags();
	}

	@Override
	public List<? extends Tag> getTags(CalmObject o) {
		return baseTagSupport.getTags(o);
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}
}
