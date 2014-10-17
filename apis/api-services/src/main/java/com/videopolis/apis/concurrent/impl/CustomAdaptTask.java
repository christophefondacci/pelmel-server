package com.videopolis.apis.concurrent.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.videopolis.apis.concurrent.base.AbstractApisTask;
import com.videopolis.apis.exception.ApisException;
import com.videopolis.apis.helper.ApisRegistry;
import com.videopolis.apis.helper.Assert;
import com.videopolis.apis.model.Aliasable;
import com.videopolis.apis.model.ApisContext;
import com.videopolis.apis.model.ApisCriterion;
import com.videopolis.apis.model.ApisCustomAdapter;
import com.videopolis.apis.model.ApisItemKeyAdapter;
import com.videopolis.apis.service.ApiMutableCompositeResponse;
import com.videopolis.apis.service.ApiMutableResponse;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.cals.model.ItemsResponse;
import com.videopolis.cals.model.impl.ItemsResponseImpl;
import com.videopolis.cals.service.CalService;
import com.videopolis.concurrent.exception.TaskExecutionException;
import com.videopolis.concurrent.model.TaskExecutionContext;

/**
 * This tasks adapt item keys of parent objects through a
 * {@link ApisItemKeyAdapter} and load the resulting CAL objects, providing them
 * to the APIS execution pipeline for further aggregation.
 * 
 * @author Christophe Fondacci
 * 
 */
public class CustomAdaptTask extends AbstractApisTask {

    private final ApisCustomAdapter adapter;
    private final ApisContext context;
    private final CalmObject[] parents;

    public CustomAdaptTask(ApisCustomAdapter adapter, ApisContext context,
	    CalmObject... parents) {
	this.adapter = adapter;
	this.context = context;
	this.parents = parents;
    }

    @Override
    protected ItemsResponse doExecute(TaskExecutionContext taskContext)
	    throws TaskExecutionException, InterruptedException {
	List<ItemKey> itemKeys = adapter.adapt(context, parents);
	if (itemKeys != null) {
	    // Adapting all parents keys
	    final Map<String, List<ItemKey>> itemKeysMap = new HashMap<String, List<ItemKey>>();
	    for (ItemKey key : itemKeys) {
		// Extracting CAL type
		if (key != null) {
		    final String calType = key.getType();
		    // Building our key collection
		    List<ItemKey> keys = itemKeysMap.get(calType);
		    if (keys == null) {
			keys = new ArrayList<ItemKey>();
			itemKeysMap.put(calType, keys);
		    }
		    if (key != null) {
			keys.add(key);
		    }
		}
	    }
	    // Calling a get items on the adapted keys
	    final List<CalmObject> objects = new LinkedList<CalmObject>();
	    for (String type : itemKeysMap.keySet()) {
		final CalService service = ApisRegistry.getCalService(type);
		final List<ItemKey> keys = itemKeysMap.get(type);
		GetCalItemsTask task = new GetCalItemsTask(service, context,
			keys.toArray(new ItemKey[keys.size()]));
		final ItemsResponse response = task.execute(taskContext);
		final List<? extends CalmObject> items = response.getItems();
		// Hashing resulting objects by their key
		for (CalmObject item : items) {
		    // itemsKeyMap.put(item.getKey(), item);
		    // Filling full response
		    objects.add(item);
		}
	    }
	    try {
		// Extracting alias for aggregation
		final ApisCriterion crit = getCriterion();
		String alias = null;
		if (crit instanceof Aliasable) {
		    alias = ((Aliasable<?>) crit).getAlias();
		}
		Assert.notNull(alias,
			"Cannot custom-adapt a non aliased criterion, please provide an alias");
		// Explicitly aggregating on root response
		final ApiMutableResponse response = context.getApiResponse();
		Assert.instanceOf(
			response,
			ApiMutableCompositeResponse.class,
			"You must use a composite request/response for custom adapt since elements are aggregated at the root level");
		final ApiMutableCompositeResponse compositeResponse = (ApiMutableCompositeResponse) response;
		compositeResponse.setElements(alias, objects);
		final ItemsResponseImpl itemsResponse = new ItemsResponseImpl();
		itemsResponse.setItems(objects);
		return itemsResponse;
	    } catch (ApisException e) {
		throw new TaskExecutionException(
			"Error during aggregation of custom adapt : "
				+ e.getMessage(), e);
	    }
	}
	return null;
    }
}
