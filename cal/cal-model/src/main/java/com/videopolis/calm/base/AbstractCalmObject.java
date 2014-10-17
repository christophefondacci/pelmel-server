package com.videopolis.calm.base;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.keyvalue.MultiKey;

import com.videopolis.calm.exception.CalException;
import com.videopolis.calm.exception.CalRuntimeException;
import com.videopolis.calm.helper.Assert;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;

/**
 * Base abstract class for {@link CalmObject}. Any {@link CalmObject}
 * implementation should extend this base class rather than implementing the
 * interface directly as this abstraction provides common features that are an
 * essential part of the APIS / CAL collaboration.
 * 
 * @author Christophe Fondacci
 * 
 */
public abstract class AbstractCalmObject implements CalmObject {

	private static final long serialVersionUID = 3686793228480521170L;

	/**
	 * A connection key, which is just a facility for {@link MultiKey}
	 * 
	 * @author julien
	 * 
	 */
	private static class ConnectionKey extends MultiKey {
		private static final long serialVersionUID = -1562083101605260445L;

		/**
		 * Default constructor
		 * 
		 * @param alias
		 *            Connection alias
		 * @param classKey
		 *            Class
		 */
		protected ConnectionKey(final String alias,
				final Class<? extends CalmObject> classKey) {
			super(alias, classKey);
		}
	}

	/** Parent model */
	private CalmObject parent;
	/** Dynamically connected CAL-model objects */
	private final Map<String, Collection<CalmObject>> aliasedConnectionsMap;
	private final List<CalmObject> connections;

	/** Cached dynamic connections */
	private final Map<ConnectionKey, List<CalmObject>> cachedConnections;

	/** The unique key of this object */
	private final ItemKey key;

	/**
	 * Default constructor
	 * 
	 * @param key
	 *            Key
	 */
	protected AbstractCalmObject(final ItemKey key) {
		aliasedConnectionsMap = new HashMap<String, Collection<CalmObject>>();
		connections = new ArrayList<CalmObject>();
		cachedConnections = new HashMap<ConnectionKey, List<CalmObject>>();
		this.key = key;
	}

	@Override
	public void add(final CalmObject relatedObject) {
		if (relatedObject != null) {
			connections.add(relatedObject);
			relatedObject.setParent(this);
		} else
			throw new CalRuntimeException(
					"Cannot add a null object to CalmObject [" + getKey() + "]");
	}

	@Override
	public void addAll(final List<? extends CalmObject> relatedObjects) {
		connections.addAll(relatedObjects);
	}

	@Override
	public void addAll(final String alias,
			final List<? extends CalmObject> relatedObjects) {
		if (alias == null) {
			addAll(relatedObjects);
		} else {
			final Collection<CalmObject> connections = getOrCreateConnectionsForAlias(alias);
			connections.addAll(relatedObjects);
		}
	}

	@Override
	public <T extends CalmObject> List<? extends T> get(
			final Class<T> classToGet) {
		return get(null, classToGet, connections);
	}

	@Override
	public <T extends CalmObject> List<? extends T> get(
			final Class<T> classToGet, final String alias) {
		// Retrieving the connection from the specified alias
		Collection<CalmObject> aliasedConnections = aliasedConnectionsMap
				.get(alias);
		if (aliasedConnections == null) {
			aliasedConnections = Collections.emptyList();
		}
		return get(alias, classToGet, aliasedConnections);
	}

	@SuppressWarnings("unchecked")
	private <T extends CalmObject> List<? extends T> get(String alias,
			final Class<T> classToGet, Collection<CalmObject> connections) {
		final ConnectionKey connectionKey = new ConnectionKey(alias, classToGet);
		List<CalmObject> connectionResult = cachedConnections
				.get(connectionKey);
		if (connectionResult == null) {
			connectionResult = new ArrayList<CalmObject>();
			// Processing the aliased connection elements to extract the
			// requested class
			for (final CalmObject obj : connections) {
				if (obj != null && classToGet.isAssignableFrom(obj.getClass())) {
					connectionResult.add(obj);
				}
			}
			cachedConnections.put(connectionKey, connectionResult);
		}
		return (List<T>) connectionResult;
	}

	@Override
	public Collection<? extends CalmObject> getConnectedObjects() {
		return Collections.unmodifiableCollection(connections);
	}

	@Override
	public ItemKey getKey() {
		return key;
	}

	/**
	 * Given an alias, returns the related objects.
	 * 
	 * @param alias
	 *            Alias
	 * @return Related objects
	 */
	private Collection<CalmObject> getOrCreateConnectionsForAlias(
			final String alias) {
		Collection<CalmObject> connections = aliasedConnectionsMap.get(alias);
		if (connections == null) {
			connections = new ArrayList<CalmObject>();
			aliasedConnectionsMap.put(alias, connections);
		}
		return connections;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends CalmObject> T getParent(final Class<T> classToGet) {
		if (classToGet == null) {
			throw new IllegalArgumentException(
					"getParent method needs a non-null class parameter");
		}
		if (parent != null) {
			if (classToGet.isAssignableFrom(parent.getClass())) {
				return (T) parent;
			} else {
				throw new IllegalArgumentException("A parent of class <"
						+ parent.getClass().getName()
						+ "> is defined while parent class <"
						+ classToGet.getName() + "> is requested");
			}
		} else {
			return null;
		}
	}

	@Override
	public <T extends CalmObject> T getUnique(final Class<T> classToGet)
			throws CalException {
		final Collection<? extends T> objects = get(classToGet);
		return getUniqueElementFromCollection(objects);
	}

	@Override
	public <T extends CalmObject> T getUnique(final Class<T> classToGet,
			final String alias) throws CalException {
		final Collection<? extends T> objects = get(classToGet, alias);
		return getUniqueElementFromCollection(objects);
	}

	/**
	 * Extracts the unique element of a collection and returns it. Checks are
	 * made against the collection to ensure that input collection as a maximum
	 * of 1 element, else it raises a CalException.
	 * 
	 * @param <T>
	 * @param elements
	 *            input collection from which the single element should be
	 *            extracted
	 * @return the unique element of the collection or null if collection is
	 *         empty or <code>null</code>
	 * @throws CalException
	 *             when the collection contains more than 1 element
	 */
	private <T extends CalmObject> T getUniqueElementFromCollection(
			final Collection<? extends T> elements) throws CalException {
		if (elements != null && !elements.isEmpty()) {
			Assert.uniqueElement(elements);
			return elements.iterator().next();
		} else {
			return null;
		}
	}

	@Override
	public void setParent(final CalmObject parent) {
		this.parent = parent;
	}

	// @Override
	// public int hashCode() {
	// final int prime = 31;
	// int result = 1;
	// result = prime * result + ((key == null) ? 0 : key.hashCode());
	// return result;
	// }
	//
	// @Override
	// public boolean equals(Object obj) {
	// if (this == obj)
	// return true;
	// if (obj == null)
	// return false;
	// if (getClass() != obj.getClass())
	// return false;
	// AbstractCalmObject other = (AbstractCalmObject) obj;
	// if (key == null) {
	// if (other.key != null)
	// return false;
	// } else if (!key.equals(other.key))
	// return false;
	// return true;
	// }

}
