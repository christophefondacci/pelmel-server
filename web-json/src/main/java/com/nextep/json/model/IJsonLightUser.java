package com.nextep.json.model;

import java.util.Date;

public interface IJsonLightUser {

	String getKey();

	String getPseudo();

	void setPseudo(String pseudo);

	Long getLastLocationTime();

	void setLastLocationTime(Long lastLocationTime);

	void setLastLocationTimeValue(Date lastLocationDate);

	boolean isOnline();

	void setOnline(boolean online);

	double getRawDistanceMeters();

	void setRawDistanceMeters(double rawDistance);

}