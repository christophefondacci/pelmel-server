package com.nextep.json.model.impl;

import java.util.ArrayList;
import java.util.List;

import com.nextep.json.model.IJsonLightUser;
import com.nextep.json.model.IPrivateListContainer;

public class JsonPrivateListResponse implements IPrivateListContainer {

	private List<IJsonLightUser> pendingApprovals = new ArrayList<IJsonLightUser>();
	private List<IJsonLightUser> pendingRequests = new ArrayList<IJsonLightUser>();
	private List<IJsonLightUser> networkUsers = new ArrayList<IJsonLightUser>();

	@Override
	public List<IJsonLightUser> getPendingApprovals() {
		return pendingApprovals;
	}

	@Override
	public List<IJsonLightUser> getPendingRequests() {
		return pendingRequests;
	}

	@Override
	public List<IJsonLightUser> getNetworkUsers() {
		return networkUsers;
	}

	@Override
	public void setPendingApprovals(List<IJsonLightUser> pendingApprovals) {
		this.pendingApprovals = pendingApprovals;
	}

	@Override
	public void setPendingRequests(List<IJsonLightUser> pendingRequests) {
		this.pendingRequests = pendingRequests;
	}

	@Override
	public void setNetworkUsers(List<IJsonLightUser> network) {
		this.networkUsers = network;
	}

	public void addPendingApproval(IJsonLightUser user) {
		this.pendingApprovals.add(user);
	}

	public void addPendingRequest(IJsonLightUser user) {
		this.pendingRequests.add(user);
	}

	public void addNetworkUser(IJsonLightUser user) {
		this.networkUsers.add(user);
	}

}
