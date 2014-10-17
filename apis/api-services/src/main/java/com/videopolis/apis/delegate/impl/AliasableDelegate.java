package com.videopolis.apis.delegate.impl;

import com.videopolis.apis.delegate.base.AbstractOwnedDelegate;
import com.videopolis.apis.exception.ApisException;
import com.videopolis.apis.helper.Assert;
import com.videopolis.apis.model.Aliasable;

/**
 * <p>
 * A default delegate implementation of {@link Aliasable}
 * </p>
 * <p>
 * This implementation provides the default behavior for {@link Aliasable}: The
 * {@link #aliasedBy(String)} method will set an alias (this method can only be
 * called once, further calls will throw an exception) which can be retrieved by
 * {@link #getAlias()}
 * </p>
 * <p>
 * If no alias is set, {@link #getAlias()} will return {@code null}
 * </p>
 * 
 * @author julien
 * 
 * @param <T>
 *            Type of object owning the delegate
 */
public class AliasableDelegate<T> extends AbstractOwnedDelegate<T> implements
	Aliasable<T> {

    /** The alias */
    private String alias;

    /**
     * Default constructor
     * 
     * @param owner
     *            Owner of this delegate
     */
    public AliasableDelegate(final T owner) {
	super(owner);
    }

    @Override
    public String getAlias() {
	return alias;
    }

    @Override
    public T aliasedBy(final String alias) throws ApisException {
	Assert.isNull(this.alias,
		"Cannot set an alias on a  which has already been set");
	this.alias = alias;
	return getOwner();
    }

}
