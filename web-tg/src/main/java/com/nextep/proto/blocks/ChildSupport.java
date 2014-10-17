package com.nextep.proto.blocks;

import java.util.Locale;

import com.nextep.proto.services.UrlService;
import com.videopolis.calm.model.CalmObject;

/**
 * Provides control to add child to a parent element.
 * 
 * @author cfondacci
 * 
 */
public interface ChildSupport {

	/**
	 * Initializes the support
	 * 
	 * @param locale
	 *            current {@link Locale}
	 * @param urlService
	 *            the current {@link UrlService}
	 * @param parent
	 *            the parent item to which we will add children
	 */
	void initialize(Locale locale, UrlService urlService, CalmObject parent);

	/**
	 * Indicates whether addition of children of the specified type to the
	 * parent is authorized.
	 * 
	 * @param childCalType
	 *            the CAL type of the child
	 * @return <code>true</code> if ok, else <code>false</code>
	 */
	boolean canAddChildFor(String childCalType);

	/**
	 * Provides the label of the "add" control that adds a child to the parent
	 * 
	 * @param childCalType
	 *            the CAL type that we want to create as a child
	 * @return the label of the control
	 */
	String getAddLabel(String childCalType);

	/**
	 * Provides the URL of the "add" action that adds a child to the parent
	 * 
	 * @param childCalType
	 *            the CAL type that we want to create as a child
	 * @return the URL of the "add" action
	 */
	String getAddUrl(String childCalType);

	/**
	 * Provides the URL of the icon of the "add" control that adds a child to
	 * the parent
	 * 
	 * @param childCalType
	 *            the CAL type that we want to create as a child
	 * @return the URL of the control icon
	 */
	String getAddIconUrl(String childCalType);
}
