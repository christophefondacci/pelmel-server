package com.nextep.messages.model.impl;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.nextep.messages.model.Message;
import com.nextep.messages.model.MessageType;
import com.nextep.messages.model.MutableMessage;
import com.videopolis.calm.base.AbstractCalmObject;
import com.videopolis.calm.exception.CalException;
import com.videopolis.calm.factory.CalmFactory;
import com.videopolis.calm.model.ItemKey;

@Entity
@Table(name = "MESSAGES")
public class MessageImpl extends AbstractCalmObject implements MutableMessage {

	private static final Log log = LogFactory.getLog(MessageImpl.class);
	private static final long serialVersionUID = 191539281625689401L;

	@Id
	@GeneratedValue
	@Column(name = "MSG_ID")
	private Long id;

	@Column(name = "FROM_ITEM_KEY")
	private String fromKey;

	@Column(name = "TO_ITEM_KEY")
	private String toKey;

	@ManyToOne(optional = true, targetEntity = MessageImpl.class, fetch = FetchType.LAZY)
	@JoinColumn(name = "REPLY_TO_MSG_ID")
	private Message sourceMessage;

	@Column(name = "MSG_DATE")
	private Date messageDate = new Date();

	@Column(name = "MSG_TEXT")
	private String message;

	@Column(name = "IS_UNREAD")
	private boolean isUnread = true;

	@Column(name = "RECIPIENTS_ITEM_KEY")
	private String recipientsGroupKey;

	@Column(name = "MSG_TYPE")
	private String messageType = MessageType.MESSAGE.name();

	public MessageImpl() {
		super(null);
	}

	@Override
	public ItemKey getFromKey() {
		try {
			return CalmFactory.parseKey(fromKey);
		} catch (CalException e) {
			log.error("Unparseable FROM item key: " + fromKey);
			return null;
		}
	}

	@Override
	public void setFromKey(ItemKey fromKey) {
		this.fromKey = fromKey.toString();
	}

	@Override
	public ItemKey getToKey() {
		try {
			return CalmFactory.parseKey(toKey);
		} catch (CalException e) {
			log.error("Unparseable TO item key: " + toKey);
			return null;
		}
	}

	@Override
	public void setToKey(ItemKey toKey) {
		this.toKey = toKey.toString();
	}

	@Override
	public Message getSourceMessage() {
		return sourceMessage;
	}

	@Override
	public void setSourceMessage(Message sourceMessage) {
		this.sourceMessage = sourceMessage;
	}

	@Override
	public Date getMessageDate() {
		return messageDate;
	}

	@Override
	public void setMessageDate(Date messageDate) {
		this.messageDate = messageDate;
	}

	@Override
	public String getMessage() {
		return message;
	}

	@Override
	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public ItemKey getKey() {
		ItemKey key = super.getKey();
		if (key == null) {
			if (id == null) {
				return null;
			}
			try {
				key = CalmFactory.createKey(CAL_TYPE, id);
			} catch (CalException e) {
				log.error("Unable to build media related item key: " + id);
				key = null;
			}
		}
		return key;
	}

	@Override
	public void setUnread(boolean unread) {
		this.isUnread = unread;
	}

	@Override
	public boolean isUnread() {
		return isUnread;
	}

	@Override
	public void setRecipientsGroupKey(ItemKey key) {
		this.recipientsGroupKey = key == null ? null : key.toString();
	}

	@Override
	public ItemKey getRecipientsGroupKey() {
		try {
			return recipientsGroupKey == null ? null : CalmFactory.parseKey(recipientsGroupKey);
		} catch (CalException e) {
			log.error("Unparseable Recipients group item key: " + recipientsGroupKey);
			return null;
		}
	}

	@Override
	public void setMessageType(MessageType messageType) {
		this.messageType = messageType == null ? MessageType.MESSAGE.name() : messageType.name();
	}

	@Override
	public MessageType getMessageType() {
		return messageType == null ? MessageType.MESSAGE : MessageType.valueOf(messageType);
	}
}
