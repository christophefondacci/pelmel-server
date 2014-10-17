package com.videopolis.apis.delegate.base;

/**
 * A base implementation for delegates which are owned by another object and
 * needs to keep a reference to the owner object.
 * 
 * @author julien
 * 
 * @param <T>
 */
public abstract class AbstractOwnedDelegate<T> {

    /** The owner */
    private final T owner;

    /**
     * Default constructor
     * 
     * @param owner
     *            The owner of this object
     */
    protected AbstractOwnedDelegate(final T owner) {
	this.owner = owner;
    }

    /**
     * Returns this object's owner
     * 
     * @return Owner
     */
    protected final T getOwner() {
	return owner;
    }
}
