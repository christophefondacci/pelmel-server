package com.nextep.proto.services.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.nextep.activities.model.Activity;
import com.nextep.advertising.model.AdvertisingBooster;
import com.nextep.geo.model.GeographicItem;
import com.nextep.geo.model.Place;
import com.nextep.media.model.Media;
import com.nextep.proto.model.PlaceType;
import com.nextep.proto.services.RightsManagementService;
import com.nextep.users.model.User;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;

public class RightsManagementServiceImpl implements RightsManagementService {

	private List<String> adminUserKeys = new ArrayList<String>();

	@Override
	public boolean canDelete(User user, CalmObject object) {
		// Getting owner
		final ItemKey ownerKey = getOwnerKey(object);
		// If the owner is the given user, then he has all rights over this
		// element
		if (ownerKey != null && ownerKey.equals(user.getKey())) {
			return true;
		} else if (object instanceof Media) {
			// Can only delete media of the user or that the user created
			final Media media = (Media) object;
			boolean isAuthor = media.getAuthorKey() != null
					&& media.getAuthorKey().equals(user.getKey());

			return media.getRelatedItemKey().equals(user.getKey()) || isAuthor
					|| isAdministrator(user);
		} else if (object instanceof Place || object instanceof Activity) {
			return isAdministrator(user);
		}
		return false;
	}

	@Override
	public boolean isAdministrator(User user) {
		return adminUserKeys.contains(user.getKey().toString());
	}

	private ItemKey getOwnerKey(CalmObject object) {
		if (object == null) {
			return null;
		}
		// Retrieving existing boosters
		final List<? extends AdvertisingBooster> boosters = object
				.get(AdvertisingBooster.class);
		// Getting current time
		final long currentTime = System.currentTimeMillis();
		// For all defined boosters
		for (AdvertisingBooster booster : boosters) {
			// If the booster is active (=current time is inside the booster
			// period)
			if (booster.getStartDate().getTime() <= currentTime
					&& booster.getEndDate().getTime() > currentTime) {
				// We return the purchaser key
				return booster.getPurchaserItemKey();
			}
		}
		// We end up here if no booster conflicts, so it is not locked
		return null;
	}

	@Override
	public boolean canModify(User user, CalmObject object) {
		final ItemKey ownerKey = getOwnerKey(object);
		// If user is the owner then he has all rights over the element
		if (ownerKey != null && ownerKey.equals(user.getKey())) {
			return true;
		} else if (object instanceof Media) {
			// Otherwise, same rules as delete (= must be the author or
			// superuser id 1)
			return canDelete(user, object);
		} else if (object instanceof GeographicItem
				&& !(object instanceof Place)) {
			return isAdministrator(user);
		} else {
			return ownerKey == null;
		}
	}

	@Override
	public boolean canCreate(User user, GeographicItem where, String calType) {
		return true;
	}

	@Override
	public List<PlaceType> getAvailablePlaceTypes(User user,
			GeographicItem location) {
		final List<PlaceType> placeTypes = new ArrayList<PlaceType>();
		for (PlaceType pt : PlaceType.values()) {
			if (pt != PlaceType.hotel
					|| (user != null && user.getKey().getNumericId() == 1)) {
				placeTypes.add(pt);
			}
		}
		return placeTypes;
	}

	public void setAdminUserKeys(String adminUserKeys) {
		if (adminUserKeys != null && !"".equals(adminUserKeys)) {
			this.adminUserKeys = Arrays.asList(adminUserKeys.split(","));
		} else {
			this.adminUserKeys = Arrays.asList("USER1");
		}
	}
}
