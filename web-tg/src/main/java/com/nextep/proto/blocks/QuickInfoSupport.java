package com.nextep.proto.blocks;

import java.util.List;

import com.nextep.proto.services.UrlService;
import com.nextep.tags.model.Tag;
import com.videopolis.calm.model.CalmObject;

public interface QuickInfoSupport {

	void initialize(UrlService urlService, CalmObject o);

	String getTitle();

	String getDescription();

	List<? extends Tag> getTags();

	String getTagIconUrl(Tag tag);

	String getThumbnailUrl();

	String getOverviewUrl();

	String getLocalization();

	String getLocalizationUrl();
}
