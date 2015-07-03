package com.nextep.json.model;

import java.util.List;

public interface IPrivateListContainer {

	List<IJsonLightUser> getPendingApprovals();

	List<IJsonLightUser> getPendingRequests();

	List<IJsonLightUser> getNetworkUsers();

	void setPendingApprovals(List<IJsonLightUser> pendingApprovals);

	void setPendingRequests(List<IJsonLightUser> pendingRequests);

	void setNetworkUsers(List<IJsonLightUser> network);

}