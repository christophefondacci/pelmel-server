package com.nextep.proto.services;

import java.util.Date;
import java.util.Locale;

import com.videopolis.apis.service.ApiResponse;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.unit.model.UnitSystem;

/**
 * A helper service which handles distance conversions or selection for Web
 * display.
 * 
 * @author Christophe Fondacci
 * 
 */
public interface DistanceDisplayService {

	/**
	 * This helper method take a distance expressed in the pivot unit, converts
	 * it in the TLD-specific measurement system, and returns a clean formatted
	 * string with the distance and unit.<br>
	 * Note that for systems having several units (yards / miles, or KM / M),
	 * the formatted value is the smallest distance found, <u>superior to 1</u>,
	 * found within all the unit systems for the locale.
	 * 
	 * @param pivotDistance
	 *            distance expressed in the pivot unit
	 * @param tld
	 *            current tld to convert the distance to
	 * @return a formatted string of the converted value
	 */
	String getDistanceToDisplay(Number pivotDistance, Locale locale);

	/**
	 * Retrieves the unit system of the given locale
	 * 
	 * @param locale
	 *            the {@link Locale}
	 * @return the {@link UnitSystem} to use, always returned
	 */
	UnitSystem getUnitSystem(Locale locale);

	/**
	 * A helper method which returns the formatted distance string corresponding
	 * to the distance from the central element to the element referenced by the
	 * provided key. Default is a compact format, please use
	 * {@link DistanceDisplayService#getTimeBetweenDates(Date, Date, Locale, boolean)}
	 * if you would like a full format
	 * 
	 * @param key
	 *            the {@link ItemKey} of the element to compute the distance
	 *            from central point
	 * @param response
	 *            {@link ApiResponse} from which distance information is
	 *            extracted
	 * @param locale
	 *            the {@link Locale} to use to determine which unit system
	 *            should be used
	 * @return the formatted distance, expressed in the most appropriate unit
	 *         system
	 */
	String getDistanceFromItem(ItemKey key, ApiResponse response, Locale locale);

	/**
	 * Provides the label presenting the time between 2 given dates.
	 * 
	 * @param fromDate
	 *            the first date
	 * @param toDate
	 *            the second date
	 * @param locale
	 *            {@link Locale} to use for translation
	 * @param isCompact
	 *            whether or not to return a compact time format (with
	 *            abbreviated time symbols)
	 * @return a label of the time between those 2 dates
	 */
	String getTimeBetweenDates(Date fromDate, Date toDate, Locale locale);

	String getTimeBetweenDates(Date fromDate, Date toDate, Locale locale,
			boolean isCompact);
}
