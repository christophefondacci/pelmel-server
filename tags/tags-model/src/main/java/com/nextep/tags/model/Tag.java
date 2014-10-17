package com.nextep.tags.model;

import com.videopolis.calm.model.CalmObject;

public interface Tag extends CalmObject {

	String CAL_ID = "TAGS";

	String getCode();

	String getDescription();

	String getDisplayMode();

}
