package com.nextep.proto.blocks.impl;

import java.util.List;
import java.util.Locale;

import com.nextep.proto.blocks.SelectableTagSupport;
import com.nextep.proto.blocks.TagSupport;
import com.nextep.proto.helpers.DisplayHelper;
import com.nextep.proto.model.Constants;
import com.nextep.proto.services.UrlService;
import com.nextep.tags.model.Tag;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;

public class CommentSelectableTagSupportImpl implements SelectableTagSupport {

	private ItemKey taggedItemKey;
	private CalmObject object;
	private UrlService urlService;
	private TagSupport baseTagSupport;

	@Override
	public void initializeSelection(UrlService urlService,
			ItemKey taggedItemKey, CalmObject o) {
		this.taggedItemKey = taggedItemKey;
		this.urlService = urlService;
		this.object = o;
	}

	@Override
	public boolean isTagSelected(Tag t) {
		return object.get(Tag.class, Constants.APIS_ALIAS_USER_ITEM_TAGS)
				.contains(t);
	}

	@Override
	public String getRemoveTagUrl(Tag t) {
		return getAddTagUrl(t);
	}

	@Override
	public String getAddTagUrl(Tag t) {
		final String toggleUrl = urlService.getToggleUserTagUrl(
				DisplayHelper.getDefaultAjaxContainer(), taggedItemKey,
				t.getKey());
		return "checkCommentTag('" + t.getKey().getId() + "','" + toggleUrl
				+ "')";
	}

	public void setTagSupport(TagSupport tagSupport) {
		this.baseTagSupport = tagSupport;
	}

	@Override
	public void initialize(Locale locale, List<Tag> availableTags) {
		baseTagSupport.initialize(locale, availableTags);
	}

	@Override
	public String getTagTranslation(Tag tag) {
		return baseTagSupport.getTagTranslation(tag);
	}

	@Override
	public String getTagIconUrl(Tag tag) {
		return baseTagSupport.getTagIconUrl(tag);
	}

	@Override
	public List<Tag> getAvailableTags() {
		return baseTagSupport.getAvailableTags();
	}

	@Override
	public List<? extends Tag> getTags(CalmObject o) {
		return baseTagSupport.getTags(o);
	}

	public void setBaseTagSupport(TagSupport baseTagSupport) {
		this.baseTagSupport = baseTagSupport;
	}
}
