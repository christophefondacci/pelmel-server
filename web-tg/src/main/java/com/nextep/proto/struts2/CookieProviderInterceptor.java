package com.nextep.proto.struts2;

import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.StrutsStatics;

import com.nextep.proto.model.CookieProvider;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import com.opensymphony.xwork2.interceptor.PreResultListener;

public class CookieProviderInterceptor extends AbstractInterceptor implements
		PreResultListener {

	private static final long serialVersionUID = -7209824710211440789L;

	public void beforeResult(ActionInvocation invocation, String resultCode) {
		// If we have a cookie provider, we add cookies to the response
		if (invocation.getAction() instanceof CookieProvider) {
			ActionContext ac = invocation.getInvocationContext();
			HttpServletResponse response = (HttpServletResponse) ac
					.get(StrutsStatics.HTTP_RESPONSE);
			addCookiesToResponse((CookieProvider) invocation.getAction(),
					response);
		}
	}

	@Override
	public String intercept(ActionInvocation invocation) throws Exception {
		// Intercepting only to add a pre-result listener
		invocation.addPreResultListener(this);
		return invocation.invoke();
	}

	private void addCookiesToResponse(CookieProvider provider,
			HttpServletResponse response) {
		// Adding all provided cookies
		List<Cookie> cookies = provider.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				response.addCookie(cookie);
			}
		}
	}
}
