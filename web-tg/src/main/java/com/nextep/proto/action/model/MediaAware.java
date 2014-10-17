package com.nextep.proto.action.model;

import com.nextep.proto.blocks.MediaProvider;

/**
 * Interface for actions which provides media to pages.
 * 
 * @author cfondacci
 * 
 */
public interface MediaAware {

	/**
	 * Injects the media provider
	 * 
	 * @param mediaProvider
	 *            the {@link MediaProvider} implementation
	 */
	void setMediaProvider(MediaProvider mediaProvider);

	/**
	 * Retrieves the media provider for this action
	 * 
	 * @return the {@link MediaProvider} current implementation
	 */
	MediaProvider getMediaProvider();
}
