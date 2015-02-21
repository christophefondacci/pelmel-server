package com.nextep.events.model;

/**
 * This class defines the type of recurring events could be defined.
 * 
 * @author cfondacci
 * 
 */
public enum CalendarType {
	/** Opening times */
	OPENING(false),
	/** Happy hour calendar */
	HAPPY_HOUR(false),
	/** Theme nights */
	THEME(false),
	/** Default event */
	EVENT(true);

	private boolean generateEvents;

	CalendarType(boolean generateEvents) {
		this.generateEvents = generateEvents;
	}

	public boolean generateEvents() {
		return this.generateEvents;
	}
}
