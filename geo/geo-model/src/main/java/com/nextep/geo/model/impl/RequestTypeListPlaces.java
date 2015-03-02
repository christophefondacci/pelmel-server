package com.nextep.geo.model.impl;

import java.util.Collection;

import com.videopolis.calm.model.RequestType;

public class RequestTypeListPlaces implements RequestType {

	private static final long serialVersionUID = 1L;

	public static enum SortField {
		UDATE, CLOSED_REPORT_COUNT
	}

	public static enum FilterField {
		ONLINE, OFFLINE, INDEXED, UNINDEXED
	}

	private SortField sortField;
	private boolean sortAsc;
	private Collection<FilterField> filterFields;
	private int pageSize = 15;
	private int page;

	public RequestTypeListPlaces(int pageSize, int page, SortField field,
			boolean asc, Collection<FilterField> filters) {
		this.pageSize = pageSize;
		this.page = page;
		this.sortField = field;
		this.sortAsc = asc;
		this.filterFields = filters;
	}

	public SortField getSortField() {
		return sortField;
	}

	public boolean isSortAsc() {
		return sortAsc;
	}

	public Collection<FilterField> getFilterFields() {
		return filterFields;
	}

	public int getPageSize() {
		return pageSize;
	}

	public int getPage() {
		return page;
	}
}
