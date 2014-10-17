package com.nextep.geo.model;

import com.videopolis.calm.model.CalmObject;

/**
 * Interface representing an alternate name of a geographic element. In other
 * words, the name of a geographic element in another language than the language
 * of the country in which it resides.
 * 
 * @author cfondacci
 * 
 */
public interface AlternateName extends CalmObject {

	String CAL_TYPE = "ALTN";

	/**
	 * The language of this alternative name
	 * 
	 * @return the language iso-639-1 code
	 */
	String getLanguage();

	/**
	 * The alternate name
	 * 
	 * @return the altername name in the given language
	 */
	String getAlternameName();

	/**
	 * Provides the geoname ID that this alternate name refers to
	 * 
	 * @return
	 */
	long getParentGeonameId();
}
