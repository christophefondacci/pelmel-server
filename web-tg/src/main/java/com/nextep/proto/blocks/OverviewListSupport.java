package com.nextep.proto.blocks;

import com.videopolis.calm.model.CalmObject;

/**
 * Interface providing extension to the {@link ItemsListBoxSupport} by
 * augmenting it with localization & rating information
 * 
 * @author cfondacci
 * 
 */
public interface OverviewListSupport extends ItemsListBoxSupport {

	String getLocalization(CalmObject item);

	String getLocalizationUrl(CalmObject item);
}
