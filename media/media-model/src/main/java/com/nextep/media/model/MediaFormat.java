package com.nextep.media.model;

import javax.persistence.Embeddable;

@Embeddable
public interface MediaFormat {

	int getWidth();

	int getHeight();

	String getEncoding();
}
