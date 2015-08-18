package com.nextep.json.model.impl;

import java.util.ArrayList;
import java.util.List;

import com.nextep.json.model.IJsonLightUser;
import com.nextep.json.model.IPrivateListContainer;

public class JsonLoggedInUser extends JsonUser implements IPrivateListContainer {

	private boolean emailValidated;
	private boolean admin;
	private List<JsonLightPlace> ownedPlaces = new ArrayList<>();
	private List<IJsonLightUser> pendingApprovals = new ArrayList<IJsonLightUser>();
	private List<IJsonLightUser> pendingRequests = new ArrayList<IJsonLightUser>();
	private List<IJsonLightUser> networkUsers = new ArrayList<IJsonLightUser>();

	public void setEmailValidated(boolean emailValidated) {
		this.emailValidated = emailValidated;
	}

	public boolean isEmailValidated() {
		return emailValidated;
	}

	public List<JsonLightPlace> getOwnedPlaces() {
		return ownedPlaces;
	}

	public void setOwnedPlaces(List<JsonLightPlace> ownedPlaces) {
		this.ownedPlaces = ownedPlaces;
	}

	public void addOwnedPlace(JsonLightPlace ownedPlace) {
		this.ownedPlaces.add(ownedPlace);
	}

	@Override
	public List<IJsonLightUser> getPendingApprovals() {
		return pendingApprovals;
	}

	@Override
	public void setPendingApprovals(List<IJsonLightUser> pendingApprovals) {
		this.pendingApprovals = pendingApprovals;
	}

	@Override
	public List<IJsonLightUser> getPendingRequests() {
		return pendingRequests;
	}

	@Override
	public void setPendingRequests(List<IJsonLightUser> pendingRequests) {
		this.pendingRequests = pendingRequests;
	}

	@Override
	public List<IJsonLightUser> getNetworkUsers() {
		return networkUsers;
	}

	@Override
	public void setNetworkUsers(List<IJsonLightUser> networkUsers) {
		this.networkUsers = networkUsers;
	}

	public boolean isAdmin() {
		return admin;
	}

	public void setAdmin(boolean admin) {
		this.admin = admin;
	}
}
