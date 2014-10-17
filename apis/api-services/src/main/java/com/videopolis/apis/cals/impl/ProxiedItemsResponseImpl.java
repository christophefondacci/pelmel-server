package com.videopolis.apis.cals.impl;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.videopolis.apis.exception.ApisException;
import com.videopolis.apis.helper.ApisRegistry;
import com.videopolis.apis.helper.Assert;
import com.videopolis.apis.reflect.impl.CalmObjectProxyInvocationHandler;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.cals.model.ItemsResponse;

/**
 * A proxied response which should be used when target aggregation object is not
 * yet available but will be fetched in parallel with its child elements.
 * 
 * @author Christophe Fondacci
 * 
 */
public class ProxiedItemsResponseImpl implements ItemsResponse {

    /** Serialization UID */
    private static final long serialVersionUID = 1L;
    private List<CalmObject> proxiedObjects;

    public ProxiedItemsResponseImpl(Collection<ItemKey> keys)
	    throws ApisException {
	Assert.notNull(keys, "Cannot proxy a null list");
	proxiedObjects = new ArrayList<CalmObject>();
	for (ItemKey key : keys) {
	    final Class<? extends CalmObject> modelClass = ApisRegistry
		    .getModelFromType(key.getType());
	    final InvocationHandler handler = new CalmObjectProxyInvocationHandler(
		    key);
	    CalmObject proxy = (CalmObject) Proxy.newProxyInstance(modelClass
		    .getClassLoader(), new Class<?>[] { modelClass }, handler);

	    proxiedObjects.add(proxy);
	}
    }

    @Override
    public List<? extends CalmObject> getItems() {
	return proxiedObjects;
    }

    @Override
    public Date getLastUpdateTimestamp() {
	return new Date();
    }

    @Override
    public void setLastUpdateTimestamp(Date lastUpdateTimestamp) {
    }

}
