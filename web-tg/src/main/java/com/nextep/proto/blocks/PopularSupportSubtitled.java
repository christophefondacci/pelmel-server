package com.nextep.proto.blocks;

import com.videopolis.calm.model.CalmObject;

/**
 * Extension of the regular popular support providing subtitle.
 * 
 * @author cfondacci
 */
public interface PopularSupportSubtitled extends PopularSupport {

	/**
	 * Provides the subtitle of the given element
	 * 
	 * @param obj
	 *            the {@link CalmObject} to get the subtitle for
	 * @return the subtitle
	 */
	String getSubtitle(CalmObject obj);

	/**
	 * If the element has a sublink, it provides its title
	 * 
	 * @param obj
	 *            the {@link CalmObject} to generate the sublink title for
	 * @return the sublink title
	 */
	String getSubLinkTitle(CalmObject obj);

	/**
	 * If the element has a sublink, it provides its URL
	 * 
	 * @param obj
	 *            the {@link CalmObject} to generate the sublink URL for
	 * @return the sublink URL
	 */
	String getSubLinkUrl(CalmObject obj);
}
