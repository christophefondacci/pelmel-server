package com.nextep.geo.model;

public interface Admin extends GeographicItem, GeonameItem {

	String CAL_ID = "ADMS";

	Country getCountry();

	/**
	 * If this admin is an ADM2, returns the parent ADM1, else it returns null
	 * 
	 * @return the ADM1 if and only if current admin is an ADM2
	 */
	Admin getAdm1();

	String getAdminCode();
}
