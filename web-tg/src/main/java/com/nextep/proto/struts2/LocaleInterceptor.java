package com.nextep.proto.struts2;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;

import com.nextep.proto.helpers.LocalizationHelper;
import com.nextep.proto.model.Constants;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;

public class LocaleInterceptor extends AbstractInterceptor {

	private static final long serialVersionUID = 2866746380148319467L;

	@Override
	public String intercept(ActionInvocation invocation) throws Exception {
		final HttpServletRequest request = (HttpServletRequest) invocation
				.getInvocationContext().get(ServletActionContext.HTTP_REQUEST);
		final String serverName = request.getServerName().toString();
		final String[] serverParts = serverName.split("\\.");
		if (serverParts.length >= 3) {
			// Storing subdomain for preserving custom urls
			final String subdomain = serverParts[0];
			ActionContext.getContext().put(Constants.ACTION_CONTEXT_SUBDOMAIN,
					subdomain);

			final String domain = serverParts[2];
			Locale locale = ActionContext.getContext().getLocale();
			locale = LocalizationHelper.getLocaleForDomain(domain);
			invocation.getInvocationContext().setLocale(locale);
			ActionContext.getContext().setLocale(locale);
		}
		return invocation.invoke();
	}

}
