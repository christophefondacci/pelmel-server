package com.nextep.messages.model.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.nextep.messages.model.MutableMessageRecipientsGroup;
import com.videopolis.calm.base.AbstractCalmObject;
import com.videopolis.calm.exception.CalException;
import com.videopolis.calm.factory.CalmFactory;
import com.videopolis.calm.model.ItemKey;

@Entity
@Table(name = "MESSAGES_RECIPIENTS")
public class MessageRecipientsGroupImpl extends AbstractCalmObject implements MutableMessageRecipientsGroup {

	private static final long serialVersionUID = 1L;

	private static final Log LOGGER = LogFactory.getLog(MessageRecipientsGroupImpl.class);

	@Id
	@GeneratedValue
	@Column(name = "MRCPT_ID")
	private Long id;

	@Column(name = "RECIPIENTS_ITEM_KEYS")
	private String recipientKeys;

	public MessageRecipientsGroupImpl() {
		super(null);
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
				LOGGER.error("Unable to build recipients group related item key: " + id);
				key = null;
			}
		}
		return key;
	}

	@Override
	public Collection<ItemKey> getRecipients() {
		final String[] recipients = recipientKeys.split(",");
		final List<ItemKey> recipientItemKeys = new ArrayList<ItemKey>();
		for (String recipientKey : recipients) {
			try {
				recipientItemKeys.add(CalmFactory.parseKey(recipientKey));
			} catch (CalException e) {
				LOGGER.warn("Unable to parse '" + recipientKey + "' for recipients list: " + e.getMessage(), e);
			}
		}
		return recipientItemKeys;
	}

	@Override
	public void setRecipients(Collection<ItemKey> recipients) {
		final StringBuilder buf = new StringBuilder();
		String separator = "";
		for (ItemKey recipientKey : recipients) {
			buf.append(separator);
			buf.append(recipientKey.toString());
			separator = ",";
		}
		recipientKeys = buf.toString();
	}

}
