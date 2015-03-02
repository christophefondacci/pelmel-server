package com.nextep.proto.blocks.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import com.nextep.proto.blocks.PaginationSupport;
import com.nextep.proto.helpers.DisplayHelper;
import com.videopolis.cals.model.PaginationInfo;

@Component("adminPaginationSupport")
public class AdminPaginationSupport implements PaginationSupport {

	private static final int PAGES_BEFORE_AFTER = 5;
	private static final Log LOGGER = LogFactory
			.getLog(AdminPaginationSupport.class);

	private PaginationInfo pagination;
	private String baseUrl;
	private Map<String, String> args = new HashMap<String, String>();

	public void initialize(PaginationInfo pagination, String baseUrl,
			Map<String, String> args) {
		this.pagination = pagination;
		this.baseUrl = baseUrl;
		this.args = args;
	}

	@Override
	public List<Integer> getPagesList() {
		return DisplayHelper.buildPagesList(pagination.getPageCount(),
				pagination.getCurrentPageNumber() + 1, PAGES_BEFORE_AFTER);
	}

	@Override
	public Integer getCurrentPage() {
		return pagination.getCurrentPageNumber() + 1;
	}

	@Override
	public String getPageUrl(int page) {
		final StringBuilder buf = new StringBuilder();
		String sep = "";
		final Map<String, String> myArgs = new HashMap<String, String>(args);
		myArgs.put("page", Integer.toString(page - 1));
		for (String arg : myArgs.keySet()) {
			final String val = myArgs.get(arg);
			if (val != null) {
				try {
					buf.append(sep + arg + "="
							+ URLEncoder.encode(val, "UTF-8"));
					sep = "&";
				} catch (UnsupportedEncodingException e) {
					LOGGER.error("Unable to generate argument: " + arg + " / "
							+ val + " (encoding): " + e.getMessage(), e);
				}
			}
		}
		return baseUrl + "?" + buf.toString();
	}

}
