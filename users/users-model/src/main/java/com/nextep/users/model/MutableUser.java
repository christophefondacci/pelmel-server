package com.nextep.users.model;

import java.util.Date;

import com.videopolis.calm.model.ItemKey;

public interface MutableUser extends User {

	void setEmail(String email);

	void setCredits(int credits);

	void setPseudo(String pseudo);

	void setStatus(String newStatus);

	void setBirthday(Date birthday);

	void setHeightInCm(Integer height);

	void setWeightInKg(Integer weight);

	void setUnitSystemCode(String unitSystemCode);

	void setLongitude(double longitude);

	void setLatitude(double latitude);

	void setLastLocationKey(ItemKey lastLocationKey);

	void setLastLocationTime(Date lastLocationTime);

	void setFacebookId(String facebookId);

	// void setFacebookToken(String token);

	void setPushProvider(PushProvider provider);

	void setPushDeviceId(String deviceId);

	void setStatLocationKey(ItemKey locationKey);

	void setDeviceInfo(String deviceInfo);

	void setLastEmailDate(Date lastEmailDate);
}