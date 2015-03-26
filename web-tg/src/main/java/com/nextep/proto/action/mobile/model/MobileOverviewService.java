package com.nextep.proto.action.mobile.model;

/**
 * General contract of the overview service in terms of parameters
 * 
 * @author cfondacci
 *
 */
public interface MobileOverviewService {

	/**
	 * Sets the ID of the element to guild overview for
	 * 
	 * @param id
	 *            the element key
	 */
	void setId(String id);

	/**
	 * Getter of the element ID
	 * 
	 * @return the element key
	 */
	String getId();

	/**
	 * Sets the HD flag to set whether the service should return HD images or SD
	 * 
	 * @param highRes
	 *            <code>true</code> for HD, else <code>false</code> (the
	 *            default)
	 */
	void setHighRes(boolean highRes);

	/**
	 * Getter of the HD flag
	 * 
	 * @return <code>true</code> if HD images requested, else <code>false</code>
	 */
	boolean isHighRes();

	/**
	 * Defines the current user (caller) latitude
	 * 
	 * @param lat
	 *            user latitude
	 */
	void setLat(double lat);

	/**
	 * Defines the current user (caller) longitude
	 * 
	 * @param lng
	 *            user longitude
	 */
	void setLng(double lng);

	/**
	 * Getter of user's latitude
	 * 
	 * @return the user latitude
	 */
	double getLat();

	/**
	 * Getter of user's longitude
	 * 
	 * @return the user longitude
	 */
	double getLng();

}