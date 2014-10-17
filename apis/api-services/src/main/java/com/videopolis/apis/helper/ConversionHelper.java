package com.videopolis.apis.helper;

import java.util.ArrayList;
import java.util.Collection;

import com.videopolis.apis.model.PaginationSettings;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.calm.model.Keyed;
import com.videopolis.calm.model.Sorter;
import com.videopolis.cals.factory.RequestSettingsFactory;
import com.videopolis.cals.model.PaginationRequestSettings;

/**
 * This helper class provides method for converting objects for the APIS
 * framework.
 * 
 * @author Christophe Fondacci
 * 
 */
public final class ConversionHelper {

    private ConversionHelper() {
    }

    /**
     * This is a helper method which converts an array of {@link CalmObject}
     * into an array of {@link ItemKey}. The keys in the output array are the
     * keys of the {@link Keyed} input objects, preserving the order.
     * 
     * @param objects
     *            an array of {@link CalmObject}
     * @return an array of the {@link ItemKey} of the objects in the input array
     */
    public static ItemKey[] getKeysFromObjects(final CalmObject... objects) {
	final Collection<ItemKey> keys = new ArrayList<ItemKey>();
	for (final CalmObject object : objects) {
	    keys.add(object.getKey());
	}
	return keys.toArray(keys.toArray(new ItemKey[keys.size()]));
    }

    /**
     * Given a {@link Sorter} collection and a {@link PaginationSettings}
     * instance, create and returns a {@link PaginationRequestSettings}
     * instance.
     * 
     * @param sorters
     *            Sorters
     * @param paginationSettings
     *            Pagination
     * @return {@link PaginationRequestSettings}
     */
    public static PaginationRequestSettings toPaginationRequestSettings(
	    final Collection<Sorter> sorters,
	    final PaginationSettings paginationSettings) {
	return RequestSettingsFactory.createPaginationRequestSettings(
		paginationSettings.getItemsByPage(),
		paginationSettings.getPageOffset(), sorters);
    }
}
