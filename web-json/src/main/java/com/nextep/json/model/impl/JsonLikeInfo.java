package com.nextep.json.model.impl;

import java.util.List;

public class JsonLikeInfo {

	private String key;
	private int likeCount;
	private int dislikeCount;
	private List<JsonActivity> likes;
	private List<JsonActivity> likers;

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public int getLikeCount() {
		return likeCount;
	}

	public void setLikeCount(int likeCount) {
		this.likeCount = likeCount;
	}

	public int getDislikeCount() {
		return dislikeCount;
	}

	public void setDislikeCount(int dislikeCount) {
		this.dislikeCount = dislikeCount;
	}

	public List<JsonActivity> getLikes() {
		return likes;
	}

	public void setLikes(List<JsonActivity> likes) {
		this.likes = likes;
	}

	public List<JsonActivity> getLikers() {
		return likers;
	}

	public void setLikers(List<JsonActivity> likers) {
		this.likers = likers;
	}
}
