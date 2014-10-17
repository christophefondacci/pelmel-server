package com.nextep.media.model.impl;

import com.nextep.media.model.MediaFormat;

public class MediaFormatImpl implements MediaFormat {
	private int width, height;
	private String encoding;

	public MediaFormatImpl(int width, int height, String encoding) {
		this.width = width;
		this.height = height;
		this.encoding = encoding;
	}

	@Override
	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	@Override
	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	@Override
	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

}
