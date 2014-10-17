package com.videopolis.calm.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import com.videopolis.calm.base.AbstractCalmObject;
import com.videopolis.calm.exception.CalException;

/**
 * A common abstract layer model object is the root interface for any
 * <u>model</u> interface wishes to contribute to the global aggregated model.
 * 
 * @author Christophe Fondacci
 */
public interface CalmObject extends Serializable, Keyed {

    /**
     * This method allows clients to navigate through dynamic model
     * relationships. According to the requested items, this method will return
     * the corresponding elements or an empty list.<br>
     * For example, one may perform a query to return hotels and their related
     * reviews. You will receive a {@link CalmObject} implementation (i.e. a
     * Hotel bean) on which you will be able to get the reviews by the following
     * construction :<br>
     * <code>hotel.get(Review.class)</code><br>
     * It will return the collection of <code>Review</code> implementation.<br>
     * Two implementation options :<br>
     * - Extending the base class {@link AbstractCalmObject}<br>
     * - Implementing directly and delegating the {@link CalmObject#get(Class)}
     * method to the {@link CalmService#get(Item, Class)}
     * 
     * @param <T>
     *            The parameterized class
     * @param classToGet
     *            the class you want objects for, depend on your model
     * @return a collection of the elements implementing the specified class and
     *         tight to the current model object.
     */
    <T extends CalmObject> List<? extends T> get(Class<T> classToGet);

    /**
     * Retrieves connected object which have been associated with an explicit
     * connection alias to this object.
     * 
     * @param <T>
     *            The parameterized class
     * @param classToGet
     *            the class you want objects for, depend on your model
     * @param alias
     *            alias with which items have been connected
     * @return a collection of the elements implementing the specified class and
     *         tight to the current model object.
     * @see CalmObject#get(Class)
     */
    <T extends CalmObject> List<? extends T> get(Class<T> classToGet,
	    String alias);

    /**
     * This convenience method will retrieve a single item atomically through
     * the dynamic model connection. You may call this method when you know
     * there is a 1-to-1 relationship between the 2 elements. This method will
     * raise a {@link CalException} if you try to get a unique element when
     * several items of this type match.<br>
     * <br>
     * This method is only here to avoid callers writing :<br>
     * <code>object.get(OtherClass.class).iterator().next()</code><br>
     * to access a single connected element. <br>
     * <br>
     * Note that this method may return <code>null</code> without throwing any
     * kind of exception when there is no such connected element.
     * 
     * 
     * @param <T>
     * @param classToGet
     *            class of the element to get
     * @return the connected object if and only if there is 1 connected element,
     *         will return <code>null</code> when no connected element has been
     *         found and else will raise a {@link CalException}
     * @throws CalException
     *             when more than 1 element of the given type is connected
     * @see CalmObject#get(Class)
     */
    <T extends CalmObject> T getUnique(Class<T> classToGet) throws CalException;

    /**
     * Retrieves a unique (1-to-1) element from a <u>named</u> connection. This
     * method is a convenience method equivalent to
     * {@link CalmObject#getUnique(Class)} for use with an explicitly named
     * connection.
     * 
     * @param <T>
     * @param classToGet
     *            connection class to get
     * @param alias
     *            alias of the connection
     * @return the connected object if and only if there is 1 connected element,
     *         will return <code>null</code> when no connected element has been
     *         found and else will raise a {@link CalException}
     * @throws CalException
     *             when more than 1 element of the given type is connected
     * @see CalmObject#getUnique(Class)
     * @see CalmObject#get(Class)
     */
    <T extends CalmObject> T getUnique(Class<T> classToGet, String alias)
	    throws CalException;

    /**
     * This method will return all the dynamically connected objects which are
     * connected to this {@code CalmObject}, without any type distinction.
     * 
     * @return Objects connected to this one
     */
    Collection<? extends CalmObject> getConnectedObjects();

    /**
     * Retrieves the parent of this {@link CalmObject}, should there be any. The
     * class argument is required for returning a properly typed parent object.
     * However, calling this method on an object which has a parent of an
     * incompatible class type would raise an {@link IllegalArgumentException}.
     * 
     * @param <T>
     *            parametized class type which must extends {@link CalmObject}
     * @param classToGet
     *            expected interface class of the parent to look for
     * @return the parent object, or <code>null</code> if orphan
     * @throws IllegalArgumentException
     *             if the specified class type is not compatible with the
     *             current parent.
     */
    <T extends CalmObject> T getParent(Class<T> classToGet);

    /**
     * Defines the parent of this {@link CalmObject}. The parent is defined by
     * the APIS component itself while aggregating the model and should usually
     * <b>not be called directly</b>.
     * 
     * @param parent
     *            {@link CalmObject} parent instance of this object
     */
    void setParent(CalmObject parent);

    /**
     * Adds a CAL model object contributing to this one. Please note that the
     * abstraction layer cannot validate consistency on these weak dependencies
     * so you may generate inconsistent relationships in your model.
     * 
     * @param relatedObject
     */
    void add(CalmObject relatedObject);

    /**
     * Adds a collection of CAL model object contributing to this one.
     * 
     * @param relatedObjects
     *            collection of related object to aggregate
     * @see CalmObject#add(CalmObject)
     */
    void addAll(List<? extends CalmObject> relatedObjects);

    /**
     * Adds a collection of CAL model object contributing to this one as an
     * explicit connected alias. Objects aggregated with an explicit alias can
     * only be obtained by specifying the same alias via the
     * getConnectedObjects(String) method.
     * 
     * @param alias
     *            alias of the connection in which items should be aggregated
     * @param relatedObjects
     *            collection of related object to aggregate
     * @see CalmObject#add(CalmObject)
     */
    void addAll(String alias, List<? extends CalmObject> relatedObjects);
}
