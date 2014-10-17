package com.nextep.proto.blocks.impl;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import org.springframework.context.MessageSource;

import com.nextep.proto.blocks.SelectableTagSupport;
import com.nextep.proto.services.UrlService;
import com.nextep.tags.model.Tag;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;

public class TagSupportImpl implements SelectableTagSupport {

	private static final String KEY_TAG_ICON_PREFIX = "facet.icon.";
	private static final String KEY_TAG_TRANSLATION_PREFIX = "facet.label.";

	private Locale locale;
	private MessageSource messageSource;
	private List<Tag> availableTags;
	private List<Tag> selectedTags = Collections.emptyList();

	@Override
	public void initialize(Locale locale, List<Tag> availableTags) {
		this.locale = locale;
		this.availableTags = availableTags;
		Collections.sort(this.availableTags, new Comparator<Tag>() {
			@Override
			public int compare(Tag o1, Tag o2) {
				return o1.getCode().compareTo(o2.getCode());
			}
		});
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void initializeSelection(UrlService urlService,
			ItemKey taggedItemKey, CalmObject tagHolder) {
		selectedTags = (List) tagHolder.get(Tag.class);
	}

	@Override
	public String getTagTranslation(Tag tag) {
		return messageSource.getMessage(KEY_TAG_TRANSLATION_PREFIX
				+ tag.getKey().toString(), null, locale);
	}

	@Override
	public String getTagIconUrl(Tag tag) {
		return messageSource.getMessage(KEY_TAG_ICON_PREFIX
				+ tag.getKey().toString(), null, locale);
	}

	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	@Override
	public List<Tag> getAvailableTags() {
		return availableTags;
	}

	@Override
	public List<? extends Tag> getTags(CalmObject o) {
		return o.get(Tag.class);
	}

	@Override
	public boolean isTagSelected(Tag t) {
		return selectedTags != null ? selectedTags.contains(t) : false;
	}

	@Override
	public String getAddTagUrl(Tag t) {
		return null;
	}

	@Override
	public String getRemoveTagUrl(Tag t) {
		return null;
	}
}
