package com.nextep.proto.helpers;

import com.nextep.proto.model.Constants;
import com.nextep.proto.model.UrlConstants;
import com.opensymphony.xwork2.ActionContext;

public final class SeoHelper {

	/**
	 * Indicates whether the current context should block all SEO related stuff
	 * 
	 * @return <code>true</code> if every SEO related stuff should be blocked,
	 *         else <code>false</code>
	 */
	public static boolean isSeoBlocked() {
		return UrlConstants.TEST_SUBDOMAIN.equals(ActionContext.getContext()
				.get(Constants.ACTION_CONTEXT_SUBDOMAIN));
	}
}
