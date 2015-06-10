package com.nextep.users.dao.impl;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.nextep.cal.util.model.CalDaoExt;
import com.nextep.cal.util.model.base.AbstractCalDao;
import com.nextep.users.dao.UsersDao;
import com.nextep.users.model.User;
import com.nextep.users.model.impl.ItemUserImpl;
import com.nextep.users.model.impl.UserImpl;
import com.videopolis.calm.exception.CalException;
import com.videopolis.calm.factory.CalmFactory;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.calm.model.RequestType;
import com.videopolis.cals.model.RequestSettings;

public class UsersDaoImpl extends AbstractCalDao<User> implements UsersDao,
		CalDaoExt<User> {

	final Log log = LogFactory.getLog(UsersDaoImpl.class);

	private final MessageDigest messageDigest;
	@PersistenceContext(unitName = "nextep-users")
	private EntityManager entityManager;

	public UsersDaoImpl() throws NoSuchAlgorithmException {
		messageDigest = MessageDigest.getInstance("SHA-1");
	}

	@Override
	public User getById(long id) {
		final Query query = entityManager.createQuery(
				"from UserImpl where id=:id").setParameter("id", id);
		try {
			return (User) query.getSingleResult();
		} catch (RuntimeException e) {
			log.error(
					"Unable to retrieve user for id " + id + ": "
							+ e.getMessage(), e);
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<User> getByIds(List<Long> idList) {
		if (idList.isEmpty()) {
			return Collections.emptyList();
		} else {
			final Query query = entityManager.createQuery(
					"from UserImpl where id in (:ids)").setParameter("ids",
					idList);
			try {
				return query.getResultList();
			} catch (RuntimeException e) {
				log.error(
						"Unable to retrieve user ids " + idList + ": "
								+ e.getMessage(), e);
				return null;
			}
		}
	}

	@Override
	public void save(CalmObject object) {
		final UserImpl user = (UserImpl) object;

		if (user.getId() == null) {
			entityManager.persist(user);
		} else {
			entityManager.merge(object);
		}
	}

	@Override
	public User getUser(String email, String password) {
		final String sha1 = getSha1(password);
		try {
			return (User) entityManager
					.createQuery(
							"from UserImpl where email=:email and password=:sha1")
					.setParameter("email", email).setParameter("sha1", sha1)
					.setMaxResults(1).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	@Override
	public User getUserFromEmail(String email) {
		try {
			return (User) entityManager
					.createQuery("from UserImpl where email=:email")
					.setParameter("email", email).setMaxResults(1)
					.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	@Override
	public User getFacebookUser(String facebookId) {
		try {
			return (User) entityManager
					.createQuery("from UserImpl where facebookId=:facebookId")
					.setParameter("facebookId", facebookId).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<User> listItems(RequestType requestType,
			RequestSettings requestSettings) {
		return entityManager.createQuery("from UserImpl").getResultList();
	}

	@Override
	public void savePassword(ItemKey userKey, String oldPassword,
			String newPassword) {
		final String newSha1 = getSha1(newPassword);
		if (oldPassword != null) {
			// If old password is supplied we propagate old password
			// verification down to the database
			// by explicitly filtering the update with old password hash
			final String oldSha1 = getSha1(oldPassword);
			entityManager
					.createNativeQuery(
							"update USERS set password=:password where user_id=:userId and password=:oldPassword")
					.setParameter("password", newSha1)
					.setParameter("userId", userKey.getNumericId())
					.setParameter("oldPassword", oldSha1).executeUpdate();
		} else {
			// When old password is null (only because we are in a first user
			// creation, we update the password
			// by explicitly checking this nullity
			entityManager
					.createNativeQuery(
							"update USERS set password=:password where user_id=:userId and password is null")
					.setParameter("password", newSha1)
					.setParameter("userId", userKey.getNumericId())
					.executeUpdate();
		}
	}

	@Override
	public void resetPassword(ItemKey userKey, String nxtpUserToken,
			String newPassword) {
		final String newSha1 = getSha1(newPassword);
		// Changing password of user that matches the authentication token
		entityManager
				.createNativeQuery(
						"update USERS set password=:password where user_id=:userId and TOKEN=:token")
				.setParameter("password", newSha1)
				.setParameter("userId", userKey.getNumericId())
				.setParameter("token", nxtpUserToken).executeUpdate();
	}

	/**
	 * Gets the hexadecimal SHA1 hash string of the provided string input
	 * 
	 * @param s
	 *            string to encode
	 * @return the hexadecimal representation of the generated SHA1
	 */
	private String getSha1(String s) {
		final StringBuilder buf = new StringBuilder();
		byte[] hash = messageDigest.digest(s.getBytes());
		// Outputting an hexadecimal conversion of this byte array
		for (int i = 0; i < hash.length; i++) {
			String hex = Integer.toHexString(0xFF & hash[i]);
			if (hex.length() == 1) {
				// could use a for loop, but we're only dealing with a
				// single byte
				buf.append('0');
			}
			buf.append(hex);
		}
		return buf.toString();
	}

	@Override
	public Date setUserOnlineTimeout(User user, String token, int timeoutMinutes) {
		final Calendar c = Calendar.getInstance();
		c.add(Calendar.MINUTE, timeoutMinutes);
		final Date newTimeout = c.getTime();
		entityManager
				.createNativeQuery(
						"update USERS set ONLINE_TIMEOUT=:timeout, token=:token where user_id=:userId")
				.setParameter("timeout", newTimeout)
				.setParameter("token", token)
				.setParameter("userId", user.getKey().getNumericId())
				.executeUpdate();
		return newTimeout;
	}

	@Override
	public User getUser(String token) {
		try {
			final User user = (User) entityManager
					.createQuery("from UserImpl where token=:token") // and
																		// onlineTimeout>:now")
					.setParameter("token", token)
					// .setParameter("now", new Date())
					.getSingleResult();
			((UserImpl) user).setToken(token);
			return user;
		} catch (NoResultException e) {
			return null;
		} catch (NonUniqueResultException e) {
			log.error("1 token associated with 2 users : " + token);
			return null;
		}
	}

	@Override
	public String getEmailFromToken(String token) {
		try {
			return (String) entityManager
					.createNativeQuery(
							"select email from USERS where token=:token and facebook_id is null")
					.setParameter("token", token).getSingleResult();
		} catch (NoResultException e) {
			return "";
		}
	}

	@Override
	public List<User> bindUsers(ItemKey externalItem, List<ItemKey> userKeys) {
		// Looking for any pre-existing city definition for this user
		final List<ItemUserImpl> itemPlaces = findItemUsersFor(externalItem,
				1000, 0);
		// Hashing the found places by place key for fast lookup
		final Map<ItemKey, ItemUserImpl> itemPlacesMap = new HashMap<ItemKey, ItemUserImpl>();
		for (ItemUserImpl itemPlace : itemPlaces) {
			try {
				final ItemKey placeKey = CalmFactory.createKey(User.CAL_TYPE,
						itemPlace.getUserId());
				itemPlacesMap.put(placeKey, itemPlace);
			} catch (CalException e) {
				log.error("Exception: " + e.getMessage(), e);
			}
		}
		// Processing new places
		final List<Long> userIds = new ArrayList<Long>();
		final List<User> allUsers = new ArrayList<User>();
		for (ItemKey userKey : userKeys) {
			final ItemUserImpl existingItemPlace = itemPlacesMap.get(userKey);
			if (existingItemPlace == null) {
				// If not existing we load the city
				userIds.add(userKey.getNumericId());
			} else {
				// If existing we remove association
				entityManager.remove(existingItemPlace);
				userIds.remove(userKey.getNumericId());
				// allPlaces.add(existingItemPlace.getPlace());
			}
		}
		// Registering new places
		final List<User> users = getByIds(userIds);
		for (User p : users) {
			// final Place mergedPlace = entityManager.merge(p);
			// Setting up new association
			final ItemUserImpl itemUser = new ItemUserImpl(externalItem,
					p.getKey());
			entityManager.persist(itemUser);
		}
		entityManager.flush();
		allUsers.addAll(users);
		return allUsers;
	}

	/**
	 * Finds users registered for given external item key
	 * 
	 * @param key
	 *            the {@link ItemKey} of element to get users for
	 * @return the list of places for this item
	 */
	@SuppressWarnings("unchecked")
	private List<ItemUserImpl> findItemUsersFor(ItemKey key, int pageSize,
			int pageOffset) {
		if (key == null) {
			return null;
		} else {
			final Query query = entityManager.createQuery(
					"from ItemUserImpl where itemKey=:key").setParameter("key",
					key.toString());
			if (pageSize > 0) {
				query.setMaxResults(pageSize).setFirstResult(
						pageOffset * pageSize);
			}
			try {
				// Retrieving from DB
				final List<ItemUserImpl> itemUsers = query.getResultList();
				if (itemUsers != null) {
					return itemUsers;
				} else {
					return Collections.emptyList();
				}
			} catch (RuntimeException e) {
				log.error("Unable to retrieve users for id " + key.toString()
						+ ": " + e.getMessage());
				return Collections.emptyList();
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean userExists(String email) {
		final List<User> users = entityManager
				.createQuery("from UserImpl where email=:email")
				.setParameter("email", email).getResultList();
		return users != null && !users.isEmpty();
	}

	@Override
	public List<User> getUsersFor(ItemKey itemKey, int pageSize, int pageOffset) {
		final List<ItemUserImpl> itemUsers = findItemUsersFor(itemKey,
				pageSize, pageOffset);
		final List<Long> userIds = new ArrayList<Long>();
		for (ItemUserImpl itemUser : itemUsers) {
			userIds.add(Long.valueOf(itemUser.getUserId()));
		}
		return getByIds(userIds);
	}

	@Override
	public int getUsersForCount(ItemKey itemKey) {
		return ((BigInteger) entityManager
				.createNativeQuery(
						"select count(1) from USERS_ITEMS where ITEM_KEY=:itemKey")
				.setParameter("itemKey", itemKey.toString()).getSingleResult())
				.intValue();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<User> listItems(RequestType requestType, Integer pageSize,
			Integer pageOffset) {
		return entityManager
				.createQuery("from UserImpl order by onlineTimeout desc")
				.setFirstResult(pageOffset).setMaxResults(pageSize)
				.getResultList();
	}

	@Override
	public int getCount() {
		return (int) (long) entityManager.createQuery(
				"select count(u) from UserImpl u").getSingleResult();
	}

	@Override
	public int getListItemsCount(RequestType requestType) {
		return getCount();
	}

	@SuppressWarnings({ "unchecked" })
	@Override
	public List<User> getUsersBeforeLoginDate(Date lastLoginMaxDate,
			boolean oldestFirst, int pageSize, int pageOffset) {

		List<User> users = entityManager
				.createQuery(
						"from UserImpl where onlineTimeout<:lastLoginMaxDate order by onlineTimeout "
								+ (oldestFirst ? "ASC" : "DESC"))
				.setParameter("lastLoginMaxDate", lastLoginMaxDate)
				.setMaxResults(pageSize).setFirstResult(pageSize * pageOffset)
				.getResultList();
		return users;
	}

	@Override
	public long getUsersCountBeforeLoginDate(Date lastLoginMaxDate) {
		return ((Number) entityManager
				.createQuery(
						"UserImpl.findAllCount where onlineTimeout<:lastLoginMaxDate")
				.setParameter("lastLoginMaxDate", lastLoginMaxDate)
				.getSingleResult()).longValue();
	}
}
