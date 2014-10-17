package com.nextep.proto.blocks;

import com.nextep.proto.services.UrlService;
import com.nextep.tags.model.Tag;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;

/**
 * An extension to the basic tag support that provides tag selection features.
 * 
 * @author cfondacci
 * 
 */
public interface SelectableTagSupport extends TagSupport {

	void initializeSelection(UrlService urlService, ItemKey taggedItemKey,
			CalmObject tagHolder);

	boolean isTagSelected(Tag t);

	String getRemoveTagUrl(Tag t);

	String getAddTagUrl(Tag t);
}
