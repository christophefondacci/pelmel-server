package com.nextep.proto.blocks;

import java.util.List;
import java.util.Locale;

import com.nextep.proto.services.UrlService;
import com.nextep.users.model.User;
import com.videopolis.calm.model.CalmObject;

/**
 * This interface provides support for overview and is used by overview page
 * fragments.
 * 
 * @author cfondacci
 * 
 */
public interface OverviewSupport {

	void initialize(UrlService urlService, Locale locale, CalmObject object,
			int likeCount, int dislikeCount, User currentUser);

	CalmObject getOverviewObject();

	/**
	 * Provides the title to display on the overview page
	 * 
	 * @param o
	 *            the element to get a title for (will generally be the overview
	 *            object)
	 * @return the title
	 */
	String getTitle(CalmObject o);

	/**
	 * Provides the URL to the icon to add next to the overview title.
	 * 
	 * @param o
	 *            the element being overviewed
	 * @return the URL of the Icon of the overview title
	 */
	String getTitleIconUrl(CalmObject o);

	/**
	 * Provides the description of the given element
	 * 
	 * @param o
	 *            the element to get a description for (will generally be the
	 *            overview object)
	 * @return the description of this element
	 */
	// String getDescription(CalmObject o);

	/**
	 * Provides the address of the element.
	 * 
	 * @param o
	 *            element to get the address for
	 * @return the address or <code>null</code> if none
	 */
	String getAddress(CalmObject o);

	/**
	 * Provides the URL of a toolbar action
	 * 
	 * @param action
	 *            action to provide URL for
	 * @return the action URL
	 */
	String getToolbarActionUrl(String action, String targetHtmlId);

	/**
	 * Provides the URL of the icon of the given action
	 * 
	 * @param action
	 *            the action code to get an icon for
	 * @return the icon URL
	 */
	String getToolbarActionIconUrl(String action);

	/**
	 * Provides the list of additional action codes to place on the toolbar
	 * 
	 * @return
	 */
	List<String> getAdditionalActions();

	/**
	 * Provides the label of the action
	 * 
	 * @param action
	 *            action code
	 * @return the translated label of this action
	 */
	String getToolbarLabel(String action);

	/**
	 * Provides the number of likes
	 * 
	 * @return the number of likes
	 */
	int getLikesCount();

	/**
	 * Provides the number of dislikes
	 * 
	 * @return the number of dislikes
	 */
	int getDislikesCount();

	/**
	 * Indicates whether the current user is the current owner of this element.
	 * 
	 * @param user
	 *            user to check
	 * @return <code>true</code> if user is current owner, else
	 *         <code>false</code>
	 */
	boolean isCurrentOwner(User user);

	/**
	 * Indicates whether the specified element is updatable
	 * 
	 * @return <code>true</code> if the user can update the element, else
	 *         <code>false</code>
	 */
	boolean isUpdatable();

	/**
	 * If the user is the current owner, it provides the label indicating the
	 * information regarding the ownership (expiration date, stats, etc.)
	 * 
	 * @return the ownership information label
	 */
	String getOwnershipInfoLabel(User user);

}
