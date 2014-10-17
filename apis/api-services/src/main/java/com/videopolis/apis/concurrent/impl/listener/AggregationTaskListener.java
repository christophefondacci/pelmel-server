package com.videopolis.apis.concurrent.impl.listener;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.videopolis.apis.concurrent.base.listener.AbstractApisTaskListener;
import com.videopolis.apis.concurrent.model.ApisTask;
import com.videopolis.apis.exception.ApisException;
import com.videopolis.apis.helper.Assert;
import com.videopolis.apis.model.Aliasable;
import com.videopolis.apis.model.ApisContext;
import com.videopolis.apis.model.ApisCriterion;
import com.videopolis.apis.model.ApisItemKeyAdapter;
import com.videopolis.apis.model.ItemKeyAdapterCriterion;
import com.videopolis.apis.model.impl.CustomAdaptCriterionImpl;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.cals.model.ItemsResponse;
import com.videopolis.cals.model.MultiKeyItemsResponse;
import com.videopolis.cals.service.CalService;
import com.videopolis.concurrent.event.TaskListener;
import com.videopolis.concurrent.model.Task;
import com.videopolis.concurrent.model.TaskExecutionContext;

/**
 * This class is a {@link TaskListener} designed to aggregate responses returned
 * from task executions (typically {@link CalService} calls) into designated
 * parent model objects.
 * 
 * @author Christophe Fondacci
 * 
 */
public class AggregationTaskListener extends AbstractApisTaskListener {

    private static final Log LOGGER = LogFactory
	    .getLog(AggregationTaskListener.class);

    /** Parent model of the aggregation */
    private Collection<CalmObject> parents;

    /**
     * Creates a new aggregation listener which will aggregate responses into
     * the specified parents.
     * 
     * @param parents
     *            parent model object(s) to aggregate to
     */
    public AggregationTaskListener(ApisContext apisContext,
	    CalmObject... parents) throws ApisException {
	Assert.notNull(apisContext,
		"Cannot create an aggregation listener without a non-null ApisContext");
	if (parents == null) {
	    this.parents = Collections.emptyList();
	} else {
	    this.parents = Arrays.asList(parents);
	}
    }

    @Override
    protected void doTaskFinished(Task<ItemsResponse> task,
	    TaskExecutionContext context, ItemsResponse result) {
	// Fetching aggregation alias
	final ApisCriterion criterion = ((ApisTask<?>) task).getCriterion();
	if (criterion instanceof CustomAdaptCriterionImpl) {
	    return;
	}
	String aggregationAlias = null;
	if (criterion instanceof Aliasable<?>) {
	    aggregationAlias = ((Aliasable<?>) criterion).getAlias();
	}

	// Starting aggregation on parent
	if (result instanceof MultiKeyItemsResponse) {
	    final MultiKeyItemsResponse multiResponse = (MultiKeyItemsResponse) result;
	    // Iterating over all input parent keys
	    for (CalmObject parent : new HashSet<CalmObject>(parents)) {
		final ItemKey parentKey = parent.getKey();
		final List<? extends CalmObject> relatedObjects = multiResponse
			.getItemsFor(parentKey);
		parent.addAll(aggregationAlias, relatedObjects);
	    }
	} else {
	    final List<? extends CalmObject> relatedObjects = result.getItems();
	    final int parentsCount = parents.size();
	    if (parentsCount == 1) {
		// Aggregating to the parent
		// There should be one and only one parent. We cannot assert
		// here as listeners don't raise exceptions but we already
		// performed this check during the construction of the
		// request.
		final CalmObject parent = parents.iterator().next();
		parent.addAll(aggregationAlias, relatedObjects);
	    } else if (parentsCount > 1) {
		if (criterion instanceof ItemKeyAdapterCriterion) {
		    final ApisItemKeyAdapter adapter = ((ItemKeyAdapterCriterion) criterion)
			    .getAdapter();
		    // Mapping child items by key
		    final Map<ItemKey, CalmObject> childMap = new HashMap<ItemKey, CalmObject>();
		    for (CalmObject c : relatedObjects) {
			childMap.put(c.getKey(), c);
		    }
		    // Processing parents
		    for (CalmObject p : parents) {
			ItemKey childKey;
			try {
			    childKey = adapter.getItemKey(p);
			    final CalmObject child = childMap.get(childKey);
			    p.addAll(aggregationAlias, Arrays.asList(child));
			} catch (ApisException e) {
			    LOGGER.error("Error while adapting item key of "
				    + p.getKey() + " with " + adapter + ": "
				    + e.getMessage(), e);
			}
		    }
		} else {
		    final Map<ItemKey, CalmObject> keyMap = buildItemKeyMap(
			    parents, criterion);
		    // In this case we have proxies whose "real" content is
		    // being aggregated so we map related objects keys with
		    // parent keys for aggregation
		    for (CalmObject o : relatedObjects) {
			final CalmObject parent = keyMap.get(o.getKey());
			// We may not find our parent object when working with
			// multi-items adapters, in which case we silently skip
			// aggregation
			if (parent != null) {
			    parent.addAll(aggregationAlias, Arrays.asList(o));
			} else {
			    LOGGER.warn("Skipped aggregation of "
				    + relatedObjects + " to " + parents
				    + ": unsupported");
			}
		    }
		}
	    }
	}
	// Response last update timestamp computation
	// TODO Current implementation does not prevent a timestamp from being
	// set as soon as a CalResponse has a null timestamp
	// final ApiResponse response = apisContext.getApiResponse();
	// if (response != null) {
	// final Date calResponseTimestamp = result.getLastUpdateTimestamp();
	// if (calResponseTimestamp == null) {
	// response.setLastUpdateTimestamp(null);
	// } else {
	// final Date currentDate = response.getLastUpdateTimestamp();
	// if (currentDate == null) {
	// response.setLastUpdateTimestamp(calResponseTimestamp);
	// } else {
	// if (currentDate.compareTo(calResponseTimestamp) < 0) {
	// response.setLastUpdateTimestamp(calResponseTimestamp);
	// }
	// }
	// }
	// }
    }

    private Map<ItemKey, CalmObject> buildItemKeyMap(
	    Collection<CalmObject> objects, ApisCriterion criterion) {
	final Map<ItemKey, CalmObject> itemKeyMap = new HashMap<ItemKey, CalmObject>();
	// We handle the key adapter if defined
	ApisItemKeyAdapter adapter = null;
	if (criterion instanceof ItemKeyAdapterCriterion) {
	    adapter = ((ItemKeyAdapterCriterion) criterion).getAdapter();
	}
	for (CalmObject o : objects) {
	    if (adapter == null) {
		itemKeyMap.put(o.getKey(), o);
	    } else {
		try {
		    itemKeyMap.put(adapter.getItemKey(o), o);
		} catch (ApisException e) {
		    LOGGER.error(
			    "Error while adapting item key during key mapping for aggregation: "
				    + e.getMessage(), e);
		}
	    }
	}
	return itemKeyMap;
    }

    @Override
    public String toString() {
	return "AGG";
    }
}
