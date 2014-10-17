package com.nextep.proto.action.impl;

import com.nextep.proto.action.base.AbstractAction;

public class StaticNotFoundAction extends AbstractAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2020420814477478126L;

	@Override
	protected String doExecute() throws Exception {
		return notFoundStatus();
	}

}
