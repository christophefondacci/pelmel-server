package com.nextep.activities.model;

public enum ActivityType {

	CREATION("C"), UPDATE("U"), LIKE("I"), UNLIKE("N"), LOCALIZATION("L"), COMMENT(
			"O"), REGISTER("R"), DELETION("D"), SEO_OPEN("S"), ABUSE("A"), REMOVAL_REQUESTED(
			"E"), HOURS("H"), CITY_CHANGE("Y"), CHECKIN("K");

	private String code;

	private ActivityType(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}

	public static ActivityType fromCode(String code) {
		for (ActivityType t : ActivityType.values()) {
			if (t.getCode().equals(code)) {
				return t;
			}
		}
		throw new IllegalArgumentException("No activity type defined for code "
				+ code);
	}
}
