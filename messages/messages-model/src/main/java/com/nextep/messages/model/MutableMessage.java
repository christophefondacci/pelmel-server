package com.nextep.messages.model;

import java.util.Date;

import com.videopolis.calm.model.ItemKey;

public interface MutableMessage extends Message {

	void setFromKey(ItemKey fromKey);

	void setToKey(ItemKey toKey);

	void setSourceMessage(Message sourceMessage);

	void setMessageDate(Date date);

	void setMessage(String message);

	void setUnread(boolean unread);
}
