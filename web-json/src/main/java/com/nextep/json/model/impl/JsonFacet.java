package com.nextep.json.model.impl;

public class JsonFacet {

	private final String code;
	private final int count;

	public JsonFacet(String code, int count) {
		this.code = code;
		this.count = count;
	}

	public String getCode() {
		return code;
	}

	public int getCount() {
		return count;
	}
}
