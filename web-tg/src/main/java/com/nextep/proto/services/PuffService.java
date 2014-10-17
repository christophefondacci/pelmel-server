package com.nextep.proto.services;

import java.util.Locale;

import com.nextep.users.model.User;
import com.videopolis.calm.model.ItemKey;

/**
 * A service to interact with Portable Update File Format generation.
 * 
 * 
 * @author cfondacci
 * 
 */
public interface PuffService {

	/**
	 * Logs the field new value if different from the current value, tagged with
	 * the locale
	 * 
	 * @param elementKey
	 *            the {@link ItemKey} that uniquely identify the element being
	 *            updated
	 * @param fieldCode
	 *            field code that is being logged
	 * @param currentValue
	 *            current field value
	 * @param newValue
	 *            new value
	 * @param locale
	 *            the {@link Locale} in which the data has been set
	 * @param author
	 *            the author of this modification
	 * @return <code>true</code> if data was logged (meaning there is a
	 *         difference), else <code>false</code>
	 */
	boolean log(ItemKey elementKey, String fieldCode, String currentValue,
			String newValue, Locale locale, User author);

	/**
	 * Logs the field new value if different from the current value, tagged with
	 * the locale, with extra data
	 * 
	 * @param elementKey
	 *            the {@link ItemKey} that uniquely identify the element being
	 *            updated
	 * @param fieldCode
	 *            field code that is being logged
	 * @param currentValue
	 *            current field value
	 * @param newValue
	 *            new value
	 * @param locale
	 *            the {@link Locale} in which the data has been set
	 * @param extra
	 *            extra data
	 * @return <code>true</code> if data was logged (meaning there is a
	 *         difference), else <code>false</code>
	 */
	boolean logPuff(ItemKey elementKey, String fieldCode, String currentValue,
			String newValue, Locale locale, String extra);
}
