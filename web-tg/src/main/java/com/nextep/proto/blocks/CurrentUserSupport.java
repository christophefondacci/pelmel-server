package com.nextep.proto.blocks;

import com.nextep.geo.model.City;
import com.nextep.media.model.Media;
import com.nextep.proto.exceptions.UserLoginTimeoutException;
import com.nextep.proto.model.Constants;
import com.nextep.proto.services.UrlService;
import com.nextep.users.model.User;
import com.videopolis.apis.exception.ApisException;
import com.videopolis.apis.model.ApisCriterion;
import com.videopolis.calm.exception.CalException;
import com.videopolis.calm.model.CalmObject;

public interface CurrentUserSupport {

	String APIS_ALIAS_CURRENT_USER = Constants.APIS_ALIAS_CURRENT_USER;

	void initialize(UrlService urlService, User user);

	User getCurrentUser();

	City getCurrentUserCity();

	Media getCurrentUserMedia();

	String getCurrentUserOverviewUrl();

	/**
	 * Indicates whether the specified CAL object belongs to the user's
	 * favorites
	 * 
	 * @param o
	 *            the {@link CalmObject} to check
	 * @return <code>true</code> if favorite, else <code>false</code>
	 */
	boolean isFavorite(CalmObject o);

	ApisCriterion createApisCriterionFor(String nxtpUserToken, boolean isAjax)
			throws CalException, ApisException, UserLoginTimeoutException;

}