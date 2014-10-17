package com.nextep.smaug.solr.model.impl;

import com.nextep.smaug.solr.model.LikeActionResult;

/**
 * Default implementation for the {@link LikeActionResult} interface as a read
 * only bean
 * 
 * @author cfondacci
 * 
 */
public class LikeActionResultImpl implements LikeActionResult {

	private final boolean liked;
	private final int likeCount;

	public LikeActionResultImpl(boolean liked, int likeCount) {
		this.liked = liked;
		this.likeCount = likeCount;
	}

	@Override
	public boolean wasLiked() {
		return liked;
	}

	@Override
	public int getNewLikeCount() {
		return likeCount;
	}

}
