package com.nextep.cal.util.model;

/**
 * Represents an element that is SEO-oriented and can provide information
 * regarding its indexation enablement.
 * 
 * @author cfondacci
 * 
 */
public interface Indexable {

	/**
	 * Indicates whether this element is opened for SEO indexation
	 * 
	 * @return <code>true</code> when element should be SEO-indexed, else
	 *         <code>false</code>
	 */
	boolean isIndexed();

}
