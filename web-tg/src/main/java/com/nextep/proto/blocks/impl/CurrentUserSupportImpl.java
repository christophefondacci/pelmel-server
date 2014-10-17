package com.nextep.proto.blocks.impl;

import java.util.Collections;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.MessageSource;

import com.nextep.geo.model.City;
import com.nextep.geo.model.GeographicItem;
import com.nextep.media.model.Media;
import com.nextep.messages.model.Message;
import com.nextep.messages.model.impl.MessageRequestTypeFactory;
import com.nextep.proto.apis.adapters.ApisMessageFromUserAdapter;
import com.nextep.proto.blocks.CurrentUserSupport;
import com.nextep.proto.exceptions.UserLoginTimeoutException;
import com.nextep.proto.model.Constants;
import com.nextep.proto.services.UrlService;
import com.nextep.users.model.User;
import com.videopolis.apis.exception.ApisException;
import com.videopolis.apis.factory.SearchRestriction;
import com.videopolis.apis.model.ApisCriterion;
import com.videopolis.apis.model.ApisItemKeyAdapter;
import com.videopolis.apis.model.WithCriterion;
import com.videopolis.calm.exception.CalException;
import com.videopolis.calm.factory.CalmFactory;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;

public class CurrentUserSupportImpl implements CurrentUserSupport {

	private static final Log log = LogFactory
			.getLog(CurrentUserSupportImpl.class);
	private User currentUser;
	private UrlService urlService;
	private MessageSource messageSource;
	private static final ApisItemKeyAdapter messageFromUserAdapter = new ApisMessageFromUserAdapter();

	@Override
	public void initialize(UrlService urlService, User user) {
		this.currentUser = user;
		this.urlService = urlService;
	}

	@Override
	public User getCurrentUser() {
		return currentUser;
	}

	@Override
	public Media getCurrentUserMedia() {
		final List<? extends Media> medias = currentUser.get(Media.class);
		if (medias == null || medias.isEmpty()) {
			return null;
		} else {
			return medias.iterator().next();
		}
	}

	@Override
	public String getCurrentUserOverviewUrl() {
		return urlService.getUserOverviewUrl("mainContent", currentUser);
	}

	@Override
	public ApisCriterion createApisCriterionFor(String nxtpUserToken,
			boolean isLight) throws CalException, ApisException,
			UserLoginTimeoutException {
		// if (nxtpUserToken == null) {
		// throw new UserLoginTimeoutException("login.mustLogin");
		// }
		final ItemKey userKey = CalmFactory.createKey(User.TOKEN_TYPE,
				nxtpUserToken == null ? "" : nxtpUserToken);

		// Default light search
		ApisCriterion criterion = SearchRestriction.alternateKey(User.class,
				userKey).aliasedBy(APIS_ALIAS_CURRENT_USER);
		// If not light (default), we add standard left bar info (messages,
		// media and localization)
		if (!isLight) {
			criterion
					.with(Media.class)
					.with(SearchRestriction.with(GeographicItem.class))
					.with((WithCriterion) SearchRestriction.with(Message.class,
							MessageRequestTypeFactory.UNREAD).addCriterion(
							(ApisCriterion) SearchRestriction.adaptKey(
									messageFromUserAdapter).with(Media.class)));
		}
		return criterion;
	}

	@Override
	public City getCurrentUserCity() {
		try {
			return currentUser.getUnique(City.class);
		} catch (CalException e) {
			log.error("Cannot extract city information of user "
					+ currentUser.getKey().toString());
		}
		return null;
	}

	@Override
	public boolean isFavorite(CalmObject o) {
		List<? extends CalmObject> favorites = currentUser.get(
				CalmObject.class, Constants.APIS_ALIAS_FAVORITE);
		if (favorites == null) {
			favorites = Collections.emptyList();
		}
		return favorites.contains(o);
	}

	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}
}
