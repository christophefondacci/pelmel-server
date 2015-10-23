package com.nextep.proto.services;

import java.util.List;

import com.nextep.geo.model.GeographicItem;
import com.nextep.proto.model.PlaceType;
import com.nextep.users.model.User;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;

public interface RightsManagementService {

	boolean canDelete(User user, CalmObject object);

	boolean canModify(User user, CalmObject object);

	boolean canCreate(User user, GeographicItem where, String calType);

	/**
	 * Provides the list of place types that could be managed by the given user
	 * in the given geographic area.
	 * 
	 * @param user
	 *            the current {@link User} or <code>null</code> when not
	 *            connected
	 * @return the list of {@link PlaceType} that the user can create / update
	 */
	List<PlaceType> getAvailablePlaceTypes(User user, GeographicItem location);

	boolean isAdministrator(User user);

	boolean isAdministrator(ItemKey userKey);
}
