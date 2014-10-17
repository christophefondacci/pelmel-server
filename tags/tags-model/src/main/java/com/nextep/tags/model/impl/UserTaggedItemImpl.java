package com.nextep.tags.model.impl;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.nextep.tags.model.Tag;
import com.nextep.tags.model.UserTaggedItem;
import com.videopolis.calm.exception.CalException;
import com.videopolis.calm.factory.CalmFactory;
import com.videopolis.calm.model.ItemKey;

@Entity
@IdClass(UserTaggedItemPK.class)
@Table(name = "USERS_TAGGED_ITEMS")
public class UserTaggedItemImpl implements UserTaggedItem {

	private static final Log log = LogFactory.getLog(UserTaggedItemImpl.class);

	@Id
	@Column(name = "USER_ITEM_KEY")
	private String userItemKey;

	@Id
	@Column(name = "TAGGED_ITEM_KEY")
	private String taggedItemKey;

	@Id
	@OneToOne(optional = false, targetEntity = TagImpl.class)
	@JoinColumn(name = "TAG_ID", nullable = false)
	private Tag tag;

	public UserTaggedItemImpl() {
	}

	public UserTaggedItemImpl(ItemKey userItemKey, ItemKey taggedItemKey,
			Tag tag) {
		this.userItemKey = userItemKey.toString();
		this.taggedItemKey = taggedItemKey.toString();
		this.tag = tag;
	}

	@Override
	public ItemKey getUserItemKey() {
		try {
			return CalmFactory.parseKey(userItemKey);
		} catch (CalException e) {
			log.error("Unable to build user item key: " + e.getMessage());
			return null;
		}
	}

	@Override
	public ItemKey getTaggedItemKey() {
		try {
			return CalmFactory.parseKey(taggedItemKey);
		} catch (CalException e) {
			log.error("Unable to build tagged item key: " + e.getMessage());
			return null;
		}
	}

	@Override
	public Tag getTag() {
		return tag;
	}

}
