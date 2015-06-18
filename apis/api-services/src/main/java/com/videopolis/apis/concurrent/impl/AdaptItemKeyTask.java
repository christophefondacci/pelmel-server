package com.videopolis.apis.concurrent.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.videopolis.apis.concurrent.base.AbstractApisTask;
import com.videopolis.apis.exception.ApisException;
import com.videopolis.apis.helper.ApisRegistry;
import com.videopolis.apis.model.ApisContext;
import com.videopolis.apis.model.ApisItemKeyAdapter;
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
public class AdaptItemKeyTask extends AbstractApisTask {

	private final ApisItemKeyAdapter adapter;
	private final ApisContext context;
	private final CalmObject[] parents;

	public AdaptItemKeyTask(ApisItemKeyAdapter adapter, ApisContext context,
			CalmObject... parents) {
		this.adapter = adapter;
		this.context = context;
		this.parents = parents;
	}

	@Override
	protected ItemsResponse doExecute(TaskExecutionContext taskContext)
			throws TaskExecutionException, InterruptedException {
		if (parents != null) {
			// Adapting all parents keys
			final Map<String, Set<ItemKey>> itemKeysMap = new HashMap<String, Set<ItemKey>>();
			for (CalmObject parent : parents) {
				try {
					// Adapting parent to ItemKey
					final ItemKey key = adapter.getItemKey(parent);
					// Extracting CAL type
					if (key != null) {
						final String calType = key.getType();
						// Building our key collection
						Set<ItemKey> keys = itemKeysMap.get(calType);
						if (keys == null) {
							keys = new HashSet<ItemKey>();
							itemKeysMap.put(calType, keys);
						}
						if (key != null) {
							keys.add(key);
						}
					}
				} catch (ApisException e) {
					throw new TaskExecutionException(
							"AdaptItemKeyTask conversion failed : "
									+ e.getMessage(), e);
				}
			}
			// Calling a get items on the adapted keys
			final ItemsResponseImpl fullResponse = new ItemsResponseImpl();
			for (String type : itemKeysMap.keySet()) {
				final CalService service = ApisRegistry.getCalService(type);
				final Set<ItemKey> keys = itemKeysMap.get(type);
				GetCalItemsTask task = new GetCalItemsTask(service, context,
						keys.toArray(new ItemKey[keys.size()]));
				final ItemsResponse response = task.execute(taskContext);
				final List<? extends CalmObject> items = response.getItems();
				// Hashing resulting objects by their key
				for (CalmObject item : items) {
					// itemsKeyMap.put(item.getKey(), item);
					// Filling full response
					fullResponse.addItem(item);
				}
			}
			// // Browsing parents, re-extracting key and aggregating result
			// if (parents.length > 1) {
			// for (CalmObject parent : parents) {
			// // Adapting parent to ItemKey
			// try {
			// final ItemKey key = adapter.getItemKey(parent);
			// // Retrieving object to aggregate
			// final CalmObject aggregatedObject = itemsKeyMap
			// .get(key);
			// // Aggregating
			// if (aggregatedObject != null) {
			// parent.add(aggregatedObject);
			// }
			// } catch (ApisException e) {
			// throw new TaskExecutionException(
			// "AdaptItemKeyTask conversion failed : "
			// + e.getMessage(), e);
			// }
			// }
			// }
			return fullResponse;
		}
		return null;
	}
}
