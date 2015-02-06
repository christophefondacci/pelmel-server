package com.nextep.proto.action.impl.tools;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.nextep.proto.action.base.AbstractAction;
import com.nextep.proto.services.NotificationService;

public class ProcessPushFeedbackAction extends AbstractAction {

	private static final long serialVersionUID = 2353610528521870400L;

	private List<String> messages = new ArrayList<String>();
	@Autowired
	private NotificationService notificationService;

	@Override
	protected String doExecute() throws Exception {
		notificationService.checkExpiredPushDevices();
		messages.add("Push check started.");
		return SUCCESS;
	}

	public List<String> getMessages() {
		return messages;
	}

}
