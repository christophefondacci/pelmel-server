package com.nextep.proto.blocks;

import java.util.List;
import java.util.Locale;

import com.nextep.activities.model.Activity;
import com.nextep.geo.model.GeographicItem;
import com.nextep.proto.services.UrlService;
import com.nextep.users.model.User;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.cals.model.PaginationInfo;

public interface ActivitySupport extends PaginationSupport {

	/**
	 * Initializes the support of activities history
	 * 
	 * @param urlService
	 *            the {@link UrlService} to use for link generation
	 * @param locale
	 *            the locale for translation
	 * @param paginationInfo
	 *            the {@link PaginationInfo} of the activities
	 * @param activities
	 *            list of {@link Activity} to display
	 */
	void initialize(UrlService urlService, Locale locale,
			PaginationInfo paginationInfo, List<? extends Activity> activities);

	/**
	 * Optional initializer to call when a single User is known to be the
	 * originator of all activities from this support.
	 * 
	 * @param user
	 *            the {@link User}
	 */
	void initializeUser(User user);

	/**
	 * Optional initializer to call when a object is known to be the target of
	 * all activities
	 * 
	 * @param object
	 *            the {@link CalmObject} target
	 */
	void initializeTarget(CalmObject object);

	/**
	 * Initialization of a geographical activity search.
	 * 
	 * @param geoItem
	 *            the {@link GeographicItem} where the activites were searched
	 * @param typeFilter
	 *            the restriction of type to look for in this location or
	 *            <code>null</code> for no restriction
	 */
	void initializeGeo(GeographicItem geoItem, String typeFilter);

	/**
	 * Overrides the default div id for injecting the activities contents after
	 * a page change by the provided string. This method should be used when
	 * more than one activity section is displayed on a page, thus requiring to
	 * uniquely identify sections to refresh.
	 * 
	 * @param contentId
	 *            the HTML ID of the tag to inject activity contents
	 */
	void setActivityHtmlContentId(String contentId);

	/**
	 * Provides the HTML ID of the element which is the parent of content
	 * injection through ajax (used when the user clicks on a pagination link)
	 * 
	 * @return the ID of the HTML element where the activities should be
	 *         injected
	 */
	String getActivityHtmlContentId();

	/**
	 * Provides the user which initiated this activity
	 * 
	 * @param a
	 *            the {@link Activity}
	 * @return the {@link User}
	 */
	User getUser(Activity a);

	/**
	 * The target of the activity, that is to say the element on which an action
	 * has been performed.
	 * 
	 * @param a
	 *            the {@link Activity}
	 * @return the target of this activity
	 */
	CalmObject getTarget(Activity a);

	/**
	 * Provides the list of all activities
	 * 
	 * @return the list of {@link Activity} to display
	 */
	List<? extends Activity> getActivities();

	/**
	 * Provides the date / time label of this activity
	 * 
	 * @param activity
	 *            the {@link Activity} to get the date time of
	 * @return the date time label
	 */
	String getDateTimeLabel(Activity activity);

	/**
	 * Provides the username of the person who originated this activity
	 * 
	 * @param activity
	 *            the {@link Activity}
	 * @return the username
	 */
	String getFrom(Activity activity);

	/**
	 * Provides the URL of the overview page for the user who made this activity
	 * 
	 * @param activity
	 *            the {@link Activity}
	 * @return the URL of the user overview page
	 */
	String getFromUrl(Activity activity);

	/**
	 * Icon to display for the given activity
	 * 
	 * @param activity
	 *            the Activity
	 * @return the icon's URL
	 */
	String getActivityIconUrl(Activity activity);

	/**
	 * URL of the link added to the icon of this activity
	 * 
	 * @param activity
	 *            the {@link Activity} to get the URL of the icon link
	 * @return the icon link
	 */
	String getActivityIconLinkUrl(Activity activity);

	/**
	 * The text to display for this activity, expressed as HTML as it may
	 * contain links
	 * 
	 * @param activity
	 *            the {@link Activity}
	 * @return the activity text
	 */
	String getActivityHtmlLine(Activity activity);

	/**
	 * The text that will be displayed as the title of the activities list box
	 * 
	 * @return the activities list title
	 */
	String getTitle();

	/**
	 * Defines the title of the activities box
	 * 
	 * @param title
	 *            new activities title
	 */
	void setTitle(String title);

}
