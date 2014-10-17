package com.nextep.messages.model.impl;

import java.io.Serializable;

import com.nextep.messages.model.MessageRequestTypeUnread;
import com.videopolis.calm.exception.CalException;
import com.videopolis.calm.factory.CalmFactory;
import com.videopolis.calm.factory.RequestTypeFactory;
import com.videopolis.calm.model.RequestType;

public class MessageRequestTypeFactory implements RequestTypeFactory {

	private static final long serialVersionUID = 8434607401273019991L;
	public static final String TYPE_UNREAD = "unread";
	public static final String TYPE_UNREAD_LIST = "listUnread";

	public static final RequestType UNREAD = new MessageRequestTypeUnread() {
		private static final long serialVersionUID = 6414322477582585154L;

		@Override
		public int hashCode() {
			return 1;
		}

		@Override
		public boolean equals(Object obj) {
			return obj instanceof MessageRequestTypeUnread;
		};

	};

	@Override
	public RequestType createRequestType(Serializable... parameters) {
		return null;
	}

	@Override
	public RequestType createRequestType(String type,
			Serializable... parameters) {
		if (TYPE_UNREAD.equals(type)) {
			return UNREAD;
		} else if (TYPE_UNREAD_LIST.equals(type)) {
			try {
				if (parameters.length == 1) {
					return new RequestTypeListUnreadMessages(
							CalmFactory.parseKey((String) parameters[0]));
				}
			} catch (CalException e) {
				return null;
			}
		}
		return null;
	}

}
