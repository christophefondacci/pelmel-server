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

import com.nextep.tags.model.ItemTag;
import com.nextep.tags.model.Tag;
import com.videopolis.calm.exception.CalException;
import com.videopolis.calm.factory.CalmFactory;
import com.videopolis.calm.model.ItemKey;

@Entity
@IdClass(ItemTagPK.class)
@Table(name = "ITEMS_TAGS")
public class ItemTagImpl implements ItemTag {

	private static final Log log = LogFactory.getLog(ItemTagImpl.class);

	@Id
	@Column(name = "ITEM_KEY")
	private String itemKey;

	@Id
	@OneToOne(optional = false, targetEntity = TagImpl.class)
	@JoinColumn(name = "TAG_ID", nullable = false)
	private Tag tag;

	public ItemTagImpl() {
	}

	public ItemTagImpl(ItemKey itemKey, Tag tag) {
		this.itemKey = itemKey.toString();
		this.tag = tag;
	}

	@Override
	public ItemKey getItemKey() {
		try {
			return CalmFactory.parseKey(itemKey);
		} catch (CalException e) {
			log.error("Unable to build item key: " + e.getMessage());
			return null;
		}
	}

	@Override
	public Tag getTag() {
		return tag;
	}

}
