package com.nextep.proto.blocks;

import java.util.List;
import java.util.Locale;

import com.nextep.activities.model.Activity;
import com.nextep.proto.services.UrlService;
import com.videopolis.calm.model.CalmObject;

/**
 * Provides support for the mosaic of images
 * 
 * @author cfondacci
 * 
 */
public interface MosaicSupport {

	void initialize(UrlService urlService, Locale locale,
			CalmObject parentObject, ActivitySupport activitySupport,
			List<? extends CalmObject> likers,
			List<? extends CalmObject> likes,
			List<? extends Activity> activities,
			List<? extends CalmObject> viewers,
			List<? extends CalmObject>... elementLists);

	CalmObject getElementAt(int row, int col);

	String getImageUrl(int row, int col);

	String getLinkUrlAt(int row, int col);

	String getTooltipText(int row, int col);

	String getMosaicTitle();
}
