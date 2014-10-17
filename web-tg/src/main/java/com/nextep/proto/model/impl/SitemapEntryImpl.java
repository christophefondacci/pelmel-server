package com.nextep.proto.model.impl;

import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.nextep.proto.model.SitemapEntry;

/**
 * Default {@link SitemapEntry} implementation.
 * 
 * @author Christophe Fondacci
 * 
 */
public class SitemapEntryImpl implements SitemapEntry {

	private static final DateFormat DATE_FORMAT = new SimpleDateFormat(
			"yyyy-MM-dd");
	private String url;
	private Date lastModification;
	private String changeFreq;
	private String priority;

	/**
	 * Builds a new {@link SitemapEntry} instance
	 * 
	 * @param url
	 *            the {@link URL} of this sitemap entry
	 */
	public SitemapEntryImpl(URL url, Date lastModification, String changeFreq,
			String priority) {
		this.url = url.toExternalForm();
		this.lastModification = lastModification;
		this.changeFreq = changeFreq;
		this.priority = priority;
	}

	/**
	 * Builds a {@link SitemapEntry} using a URL string, useful when no URL
	 * instance has been created so that it avoids unnecessary {@link URL}
	 * object creation
	 * 
	 * @param url
	 *            the URL string
	 */
	public SitemapEntryImpl(String url, Date lastModification,
			String changeFreq, String priority) {
		this.url = url;
		this.lastModification = lastModification;
		this.changeFreq = changeFreq;
		this.priority = priority;
	}

	@Override
	public String getUrl() {
		return url;
	}

	@Override
	public String getLastModification() {
		if (lastModification != null) {
			return DATE_FORMAT.format(lastModification);
		} else {
			return "";
		}
	}

	@Override
	public String getChangeFreq() {
		return changeFreq != null ? changeFreq : "";
	}

	@Override
	public String getPriority() {
		return priority != null ? priority : "";
	}
}
