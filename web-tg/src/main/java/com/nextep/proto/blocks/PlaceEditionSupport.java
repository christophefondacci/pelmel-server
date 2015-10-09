package com.nextep.proto.blocks;

import java.util.List;
import java.util.Locale;

import com.nextep.users.model.User;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;

public interface PlaceEditionSupport {

	void initialize(Locale locale, CalmObject placeOrCity, String placeType, User currentUser);

	/**
	 * Retrieves the place unique key
	 * 
	 * @return the place key
	 */
	String getPlaceId();

	/**
	 * Retrieves the place name
	 * 
	 * @return the place name
	 */
	String getName();

	/**
	 * Provides the place address
	 * 
	 * @return the place address
	 */
	String getAddress();

	/**
	 * Provides the list of available place types
	 * 
	 * @return the list of place types in the order they will appear in the
	 *         selection combo
	 */
	List<String> getPlaceTypes();

	/**
	 * Provides the label of the place type
	 * 
	 * @param placeType
	 *            the place type code to get the label for
	 * @return the corresponding place type label
	 */
	String getPlaceTypeLabel(String placeType);

	/**
	 * Provides the current place type
	 * 
	 * @return the place type of the place being edited
	 */
	String getPlaceType();

	/**
	 * Provides the "selected" tag to put on the option for this place type
	 * 
	 * @param placeType
	 *            the place type that is being tagged for selection
	 * @return the selected attribute value
	 */
	String getSelected(String placeType);

	/**
	 * Provides the id of the city in which the place is located.
	 * 
	 * @return the city's unique key
	 */
	String getCityId();

	/**
	 * Provides a user friendly name of the city in which the place is located
	 * 
	 * @return the user-friendly city name
	 */
	String getCityName();

	/**
	 * Provides the description of the place.
	 * 
	 * @return the place's description
	 */
	String getDescription();

	/**
	 * Provides the description's unique key that is being edited
	 * 
	 * @return the description's {@link ItemKey} as a string
	 */
	String getDescriptionItemKey();

	Double getLatitude();

	Double getLongitude();

	String getFacebookId();

}
