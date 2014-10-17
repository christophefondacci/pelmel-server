package com.videopolis.apis.reflect.impl;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import com.videopolis.calm.base.AbstractCalmObject;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;

/**
 * This proxy acts as an intermediate {@link CalmObject} allowing
 * parallelization of some CAL calls when targetted aggregation object is not
 * yet available. <br>
 * This proxy holds all aggregated objects <u>on behalf</u> of the proxied
 * object.
 * 
 * @author Christophe Fondacci
 * 
 */
public class CalmObjectProxyInvocationHandler extends AbstractCalmObject
	implements InvocationHandler {

    private static final long serialVersionUID = 1L;
    private CalmObject proxiedObject = null;

    public CalmObjectProxyInvocationHandler(ItemKey key) {
	super(key);
    }

    @Override
    public Object invoke(Object obj, Method meth, Object[] args)
	    throws Throwable {
	final Class<?> declaringClass = meth.getDeclaringClass();
	if (declaringClass.isAssignableFrom(CalmObject.class)) {
	    return meth.invoke(this, args);
	} else {
	    if (proxiedObject != null) {
		return meth.invoke(proxiedObject, args);
	    } else {
		throw new RuntimeException("Proxied object unavailable");
	    }
	}
    }

    @Override
    public void add(CalmObject relatedObject) {
	// Specific check for proxied item
	if (relatedObject.getKey().equals(getKey())) {
	    proxiedObject = relatedObject;
	}
	super.add(relatedObject);
    }
}
