package com.nextep.json.model.impl;

import java.util.ArrayList;
import java.util.List;

public class JsonLiker extends JsonMessagingStatistic {
	private int likes;
	private boolean liked;
	private List<JsonLightUser> likeUsers = new ArrayList<JsonLightUser>();

	public void setLiked(boolean isLiked) {
		this.liked = isLiked;
	}

	public boolean isLiked() {
		return liked;
	}

	public void addLikeUser(JsonLightUser user) {
		likeUsers.add(user);
	}

	public List<JsonLightUser> getLikeUsers() {
		return likeUsers;
	}

	public void setLikes(int likes) {
		this.likes = likes;
	}

	public int getLikes() {
		return likes;
	}

}
