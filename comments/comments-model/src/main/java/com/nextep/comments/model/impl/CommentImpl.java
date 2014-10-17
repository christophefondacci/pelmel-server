package com.nextep.comments.model.impl;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.nextep.comments.model.Comment;
import com.nextep.comments.model.MutableComment;
import com.videopolis.calm.base.AbstractCalmObject;
import com.videopolis.calm.exception.CalException;
import com.videopolis.calm.factory.CalmFactory;
import com.videopolis.calm.model.ItemKey;

@Entity
@Table(name = "COMMENTS")
public class CommentImpl extends AbstractCalmObject implements Comment,
		MutableComment {

	private static final long serialVersionUID = 8096652335512507541L;
	private static final Log LOGGER = LogFactory.getLog(CommentImpl.class);

	@GeneratedValue
	@Id
	@Column(name = "COMMENT_ID")
	private long id;
	@Column(name = "AUTHOR_ITEM_KEY")
	private String authorItemKey;
	@Column(name = "ITEM_KEY")
	private String commentedItemKey;
	@Column(name = "COMMENT_DATE")
	private Date date;
	@Column(name = "COMMENT_TEXT")
	private String message;
	@Column(name = "COMMENT_RATE")
	private Integer rate;

	public CommentImpl() {
		super(null);
	}

	@Override
	public ItemKey getAuthorItemKey() {
		if (authorItemKey != null) {
			try {
				return CalmFactory.parseKey(authorItemKey);
			} catch (CalException e) {
				LOGGER.error("Unable to parse comment's author item key");
				return null;
			}
		} else {
			return null;
		}
	}

	@Override
	public ItemKey getCommentedItemKey() {
		if (commentedItemKey != null) {
			try {
				return CalmFactory.parseKey(commentedItemKey);
			} catch (CalException e) {
				LOGGER.error("Unable to parse comment's commented item key");
				return null;
			}
		} else {
			return null;
		}
	}

	@Override
	public Date getDate() {
		return date;
	}

	@Override
	public String getMessage() {
		return message;
	}

	@Override
	public Integer getRate() {
		return rate;
	}

	@Override
	public void setCommentedItemKey(ItemKey commentedItemKey) {
		this.commentedItemKey = commentedItemKey.toString();
	}

	@Override
	public void setDate(Date date) {
		this.date = date;
	}

	@Override
	public void setAuthorItemKey(ItemKey fromKey) {
		this.authorItemKey = fromKey.toString();
	}

	@Override
	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public void setRate(Integer rate) {
		this.rate = rate;
	}

	@Override
	public ItemKey getKey() {
		if (id != 0) {
			try {
				return CalmFactory.createKey(CAL_ID, id);
			} catch (CalException e) {
				LOGGER.error("Unable to parse comment's unique key");
				return null;
			}
		} else {
			return null;
		}
	}
}
