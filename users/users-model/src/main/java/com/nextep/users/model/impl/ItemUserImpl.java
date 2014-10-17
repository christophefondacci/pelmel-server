package com.nextep.users.model.impl;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import com.videopolis.calm.model.ItemKey;

@Entity
@Table(name = "USERS_ITEMS")
@IdClass(ItemUserPK.class)
public class ItemUserImpl {

	@Id
	@Column(name = "USER_ID")
	private Long userId;
	@Id
	@Column(name = "ITEM_KEY")
	private String itemKey;

	public ItemUserImpl() {
	}

	public ItemUserImpl(ItemKey itemKey, ItemKey userKey) {
		this.userId = userKey.getNumericId();
		this.itemKey = itemKey.toString();
	}

	public String getItemKey() {
		return itemKey;
	}

	public Long getUserId() {
		return userId;
	}
}
