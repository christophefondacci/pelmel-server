package com.nextep.users.services.impl;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.nextep.cal.util.services.CalPersistenceService;
import com.nextep.cal.util.services.base.AbstractDaoBasedCalServiceImpl;
import com.nextep.users.dao.UsersDao;
import com.nextep.users.model.MutableUser;
import com.nextep.users.model.PushProvider;
import com.nextep.users.model.User;
import com.nextep.users.model.impl.UserImpl;
import com.nextep.users.services.UsersService;
import com.videopolis.calm.exception.CalException;
import com.videopolis.calm.helper.Assert;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.cals.model.CalContext;
import com.videopolis.cals.model.ItemsResponse;
import com.videopolis.cals.model.PaginatedItemsResponse;
import com.videopolis.cals.model.impl.ItemsResponseImpl;
import com.videopolis.cals.model.impl.PaginatedItemsResponseImpl;

public class UsersServiceImpl extends AbstractDaoBasedCalServiceImpl implements
		CalPersistenceService, UsersService {

	private static final Log LOGGER = LogFactory.getLog(UsersServiceImpl.class);
	private static ThreadLocal<Random> random = new ThreadLocal<Random>();
	private int connectionTimeoutInMinutes;
	private static final User FAKE_USER = new UserImpl();

	@Override
	public Class<? extends CalmObject> getProvidedClass() {
		return User.class;
	}

	@Override
	public String getProvidedType() {
		return User.CAL_TYPE;
	}

	@Override
	public void saveItem(CalmObject object) {
		final User user = (User) object;
		getCalDao().save(object);
		String token = user.getToken();
		if (token == null) {
			token = generateUniqueToken();
			refreshUserOnlineTimeout(user, token);
		}
	}

	@Override
	public CalmObject createTransientObject() {
		return new UserImpl();
	}

	@Override
	public List<CalmObject> setItemFor(ItemKey contributedItemKey,
			ItemKey... internalItemKeys) {
		final List<ItemKey> userKeys = Arrays.asList(internalItemKeys);
		return (List) ((UsersDao) getCalDao()).bindUsers(contributedItemKey,
				userKeys);
	}

	@Override
	public User login(String email, String password, String deviceId,
			String pushProvider) throws CalException {
		final UsersDao dao = (UsersDao) getCalDao();
		final User user = dao.getUser(email, password);
		Assert.notNull(user, "Invalid email or password");
		((MutableUser) user).setPushDeviceId(deviceId);
		if (pushProvider != null) {
			try {
				final PushProvider provider = PushProvider
						.valueOf(pushProvider);
				((MutableUser) user).setPushProvider(provider);
			} catch (IllegalArgumentException e) {
				// Ignoring this
				LOGGER.warn("PUSH: Invalid provider: " + pushProvider);
			}
		}
		final String token = generateUniqueToken();
		refreshUserOnlineTimeout(user, token);
		return user;
	}

	@Override
	public void savePassword(ItemKey userKey, String oldPassword,
			String newPassword) {
		((UsersDao) getCalDao())
				.savePassword(userKey, oldPassword, newPassword);
	}

	@Override
	public String generateUniqueToken() {
		Random generator = random.get();
		if (generator == null) {
			generator = new Random();
			random.set(generator);
		}
		final long randomVal = generator.nextLong();
		final long token = System.currentTimeMillis() & randomVal;
		return String.valueOf(token);
	}

	@Override
	public ItemsResponse getItems(List<ItemKey> ids, CalContext context)
			throws CalException {
		if (ids.size() == 1) {
			final ItemKey id = ids.iterator().next();
			if (User.TOKEN_TYPE.equals(id.getType())) {
				final User user = ((UsersDao) getCalDao()).getUser(id.getId());
				final ItemsResponseImpl response = new ItemsResponseImpl();
				if (user != null) {
					response.addItem(user);
				}
				return response;
			} else if (User.EMAIL_TYPE.equals(id.getType())) {
				// We check if it exists
				final boolean userExists = ((UsersDao) getCalDao())
						.userExists(id.getId());
				final ItemsResponseImpl response = new ItemsResponseImpl();
				// If so we add a fake user
				if (userExists) {
					response.addItem(FAKE_USER);
				}
				return response;
			} else if (User.FACEBOOK_TYPE.equals(id.getType())) {
				final User user = ((UsersDao) getCalDao()).getFacebookUser(id
						.getId());
				final ItemsResponseImpl response = new ItemsResponseImpl();
				if (user != null) {
					response.addItem(user);
				}
				return response;
			}
		}
		return super.getItems(ids, context);
	}

	@Override
	protected ItemsResponse getItemsFor(ItemKey itemKey, CalContext context)
			throws CalException {
		final List<User> users = ((UsersDao) getCalDao()).getUsersFor(itemKey,
				-1, -1);
		final ItemsResponseImpl response = new ItemsResponseImpl();
		response.setItems(users);
		return response;
	}

	@Override
	public PaginatedItemsResponse getPaginatedItemsFor(ItemKey itemKey,
			CalContext context, int resultsPerPage, int pageNumber)
			throws CalException {
		final UsersDao usersDao = ((UsersDao) getCalDao());
		final List<User> users = usersDao.getUsersFor(itemKey, resultsPerPage,
				pageNumber);
		final int usersCount = usersDao.getUsersForCount(itemKey);
		final PaginatedItemsResponseImpl response = new PaginatedItemsResponseImpl(
				resultsPerPage, pageNumber);
		response.setItemCount(usersCount);
		response.setItems(users);
		int pages = usersCount / resultsPerPage;
		int pagesMod = usersCount % resultsPerPage;
		response.setPageCount(pages + (pagesMod > 0 ? 1 : 0));
		return response;
	}

	public void setConnectionTimeoutInMinutes(int connectionTimeoutInMinutes) {
		this.connectionTimeoutInMinutes = connectionTimeoutInMinutes;
	}

	@Override
	public String getEmailFromToken(String token) {
		return ((UsersDao) getCalDao()).getEmailFromToken(token);
	}

	@Override
	public void refreshUserOnlineTimeout(User user, String token) {
		int currentTimeout = connectionTimeoutInMinutes;
		if (token == null) {
			currentTimeout = 0;
		}
		if (user != null) {
			final Date newTimeout = ((UsersDao) getCalDao())
					.setUserOnlineTimeout(user, token, currentTimeout);
			((UserImpl) user).setOnlineTimeout(newTimeout);
			((UserImpl) user).setToken(token);
		}
	}
}
