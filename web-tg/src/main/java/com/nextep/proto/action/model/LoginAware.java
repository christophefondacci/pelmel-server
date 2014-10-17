package com.nextep.proto.action.model;

import com.nextep.proto.blocks.LoginSupport;

/**
 * This interface is implemented by action that can offer login.
 * 
 * @author cfondacci
 * 
 */
public interface LoginAware {

	/**
	 * Provides the login support
	 * 
	 * @return the {@link LoginSupport}
	 */
	LoginSupport getLoginSupport();

	/**
	 * Injects the {@link LoginSupport}
	 * 
	 * @param loginSupport
	 *            the {@link LoginSupport} implementation
	 */
	void setLoginSupport(LoginSupport loginSupport);
}
