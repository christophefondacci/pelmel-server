package com.nextep.proto.services;

import java.util.Locale;

import com.nextep.events.model.CalendarType;
import com.nextep.geo.model.GeographicItem;
import com.nextep.proto.model.SearchType;
import com.nextep.users.model.User;
import com.videopolis.apis.model.FacetInformation;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.smaug.common.model.Facet;
import com.videopolis.smaug.common.model.FacetRange;

/**
 * Centralized service for URL generation
 * 
 * @author cfondacci
 * 
 */
public interface UrlService {

	String buildSearchUrl(String targetHtmlElementId, GeographicItem geoItem,
			SearchType searchType);

	String buildMapSearchUrl(Locale locale, String targetHtmlElementId,
			GeographicItem geoItem);

	String getMapInfoWindowUrl(ItemKey geoKey);

	/**
	 * Builds the search URL for range-based queries
	 * 
	 * @param targetHtmlElementId
	 * @param geoKey
	 * @param currentFacetting
	 * @param range
	 * @return
	 */
	String buildUserSearchUrl(String targetHtmlElementId, CalmObject geoItem,
			FacetInformation currentFacetting, FacetRange range);

	/**
	 * Builds a URL pointing to the search results at the specified page from
	 * the provided information.
	 * 
	 * @param targetHtmlElementId
	 *            the HTML element in which the result should be integrated.
	 *            This information MUST be provided but MAY be ignored depending
	 *            on the implementation. Its purpose is to allow AJAX
	 *            implementations to know the target HTML id where AJAX response
	 *            need to be injected.
	 * @param geoKey
	 *            the {@link ItemKey} of the {@link GeographicItem} in which the
	 *            search is made
	 * @param currentFacetting
	 *            the current facetting information as a
	 *            {@link FacetInformation} bean
	 * @param page
	 *            the page that should be displayed
	 * @return the corresponding URL pointing to the search result main page
	 */
	String buildSearchUrl(String targetHtmlElementId, CalmObject geoItem,
			SearchType searchType, FacetInformation currentFacetting, int page);

	String buildSearchUrl(Locale language, String targetHtmlElementId,
			CalmObject geoItem, SearchType searchType,
			FacetInformation currentFacetting, int page);

	/**
	 * Builds a URL pointing to the search results main page from the provided
	 * information
	 * 
	 * @param targetHtmlElementId
	 *            the HTML element in which the result should be integrated.
	 *            This information MUST be provided but MAY be ignored depending
	 *            on the implementation. Its purpose is to allow AJAX
	 *            implementations to know the target HTML id where AJAX response
	 *            need to be injected.
	 * @param geoItem
	 *            the {@link GeographicItem} in which the search is made
	 * @param currentFacetting
	 *            the current facetting information as a
	 *            {@link FacetInformation} bean
	 * @param newFacet
	 *            any new facet to add to the current facetting or
	 *            <code>null</code> if none
	 * @param removedFacet
	 *            any facet to remove from current facetting or
	 *            <code>null</code> if none
	 * @return the corresponding URL pointing to the search result main page
	 */
	String buildSearchUrl(String targetHtmlElementId, CalmObject geoItem,
			SearchType searchType, FacetInformation currentFacetting,
			Facet newFacet, Facet removedFacet);

	/**
	 * Builds a URL pointing to the overview page of the place specified through
	 * its {@link ItemKey}.
	 * 
	 * @param targetHtmlElementId
	 *            the HTML element in which the result should be integrated.
	 *            This information MUST be provided but MAY be ignored depending
	 *            on the implementation. Its purpose is to allow AJAX
	 *            implementations to know the target HTML id where AJAX response
	 *            need to be injected.
	 * @param placeKey
	 *            the {@link ItemKey} of the place to display
	 * @return the URL of the overview page for this place.
	 */
	String getPlaceOverviewUrl(String targetHtmlElementId, CalmObject place);

	String getPlaceOverviewUrl(Locale locale, String targetHtmlElementId,
			CalmObject place);

	/**
	 * Builds a URL pointing to the overview page of the event specified through
	 * its {@link ItemKey}.
	 * 
	 * @param targetHtmlElementId
	 *            the HTML element in which the result should be integrated.
	 *            This information MUST be provided but MAY be ignored depending
	 *            on the implementation. Its purpose is to allow AJAX
	 *            implementations to know the target HTML id where AJAX response
	 *            need to be injected.
	 * @param placeKey
	 *            the {@link ItemKey} of the event to display
	 * @return the URL of the overview page for this event.
	 */
	String getEventOverviewUrl(String targetHtmlElementId, CalmObject event);

	String getEventOverviewUrl(Locale locale, String targetHtmlElementId,
			CalmObject event);

	/**
	 * Builds a URL pointing to the overview page of the user specified through
	 * its {@link ItemKey}.
	 * 
	 * @param targetHtmlElementId
	 *            the HTML element in which the result should be integrated.
	 *            This information MUST be provided but MAY be ignored depending
	 *            on the implementation. Its purpose is to allow AJAX
	 *            implementations to know the target HTML id where AJAX response
	 *            need to be injected.
	 * @param placeKey
	 *            the {@link ItemKey} of the user to display
	 * @return the URL of the overview page for this user.
	 */
	String getUserOverviewUrl(String targetHtmlElementId, CalmObject user);

	String getUserOverviewUrl(Locale locale, String targetHtmlElementId,
			CalmObject user);

	String getILikeUrl(String targetHtmlElementId, ItemKey element);

	/**
	 * Provides the URL which can generate the media addition form that adds a
	 * media to the specified element.
	 * 
	 * @param targetHtmlElementId
	 *            the HTML element in which the result should be integrated.
	 *            This information MUST be provided but MAY be ignored depending
	 *            on the implementation. Its purpose is to allow AJAX
	 *            implementations to know the target HTML id where AJAX response
	 *            need to be injected.
	 * @param element
	 *            the {@link ItemKey} of the element to add a media to
	 * @return the corresponding URL
	 */
	String getMediaAdditionFormUrl(String targetHtmlElementId, ItemKey element);

	/**
	 * Same method for generating URL to the media addition form except that we
	 * provide a redirection URL allowing us to customize the page on which the
	 * user will end up after a media is uploaded.
	 * 
	 * @param targetHtmlElementId
	 *            target ajax node ID
	 * @param element
	 *            element to add media to
	 * @param redirectUrl
	 *            URL to redirect to after a media has been uploaded
	 * @return the media addition URL
	 */
	String getMediaAdditionFormUrl(String targetHtmlElementId, ItemKey element,
			String redirectUrl);

	String getWriteMessageDialogUrl(String targetHtmlElementId, ItemKey element);

	/**
	 * Provides the URL which can generate the event edition form that edits the
	 * event identified by the provided item key.
	 * 
	 * @param targetHtmlElementId
	 *            the HTML element in which the result should be integrated.
	 *            This information MUST be provided but MAY be ignored depending
	 *            on the implementation. Its purpose is to allow AJAX
	 *            implementations to know the target HTML id where AJAX response
	 *            need to be injected.
	 * @param element
	 *            the {@link ItemKey} of the event to edit
	 * @return the corresponding URL
	 */
	String getEventEditionFormUrl(String targetHtmlElementId, ItemKey element);

	/**
	 * Same as {@link UrlService#getEventEditionFormUrl(String, ItemKey)} except
	 * that the form that will display the event edition will be dedicated to
	 * this calendar type (typically use to spawn the opening hours creation
	 * form)
	 */
	String getEventEditionFormUrl(String targetHtmlElementId, ItemKey element,
			CalendarType calendarType);

	String getPlaceEditionFormUrl(String targetHtmlElementId, ItemKey element,
			String placeType);

	/**
	 * Generic method retrieving the overview URL of an unknown element.
	 * 
	 * @param targetHtmlElementId
	 *            the HTML element in which the result should be integrated.
	 *            This information MUST be provided but MAY be ignored depending
	 *            on the implementation. Its purpose is to allow AJAX
	 *            implementations to know the target HTML id where AJAX response
	 *            need to be injected.
	 * @param itemKey
	 *            the {@link ItemKey} of the element to display
	 * @return the URL of the overview page for this element.
	 */
	String getOverviewUrl(String targetHtmlElementId, CalmObject item);

	String getOverviewUrl(Locale locale, String targetHtmlElementId,
			CalmObject item);

	/**
	 * Provides the URL that can update the comments of the specified target
	 * HTML element with a new comments page.
	 * 
	 * @param targetHtmlElementId
	 *            ID of the element to update in the source HTML document
	 * @param parentKey
	 *            the key of the element for which comments need to be retrieved
	 * @param page
	 *            the page offset to retrieve
	 * @return the corresponding URL
	 */
	String getCommentUrl(String targetHtmlElementId, ItemKey parentKey, int page);

	/**
	 * Provides the URL that can update the activities of the specified target
	 * HTML element with a new comments page.
	 * 
	 * @param targetHtmlElementId
	 *            ID of the element to update in the source HTML document
	 * @param target
	 *            the activity target or <code>null</code>
	 * @param user
	 *            the activity user or <code>null</code>
	 * @param geoItem
	 *            the geographical item to search activities in
	 * @param page
	 *            the page offset to retrieve
	 * @return the corresponding URL
	 */
	String getActivitiesUrl(String targetHtmlElementId, CalmObject target,
			CalmObject user, GeographicItem geoItem, int page, String typeFilter);

	String getToggleUserTagUrl(String targetHtmlElementId,
			ItemKey taggedItemKey, ItemKey tag);

	String getMyMessagesUrl(String targetHtmlElementId, CalmObject parent,
			int page);

	String getHomepageUrl(Locale locale);

	String getXMLSitemapIndexUrl(Locale locale, String pageType,
			SearchType searchType);

	String getXMLSitemapUrl(Locale locale, String pageType,
			SearchType searchType, int page);

	/**
	 * Provides the URL of the secured promotion page
	 * 
	 * @param l
	 *            the {@link Locale} of the link to generate
	 * @return the HTTPS promote URL
	 */
	String getPromoteUrl(Locale l);

	/**
	 * Provides the URL to sponsor this place
	 * 
	 * @return the sponsorship URL
	 */
	String getSponsorshipUrl(CalmObject object);

	/**
	 * Provides the URL of the action to post the form for sponsorship
	 * activation
	 * 
	 * @return the activation URL
	 */
	String getSponsorshipActivationUrl();

	/**
	 * Generates an absolute media URL for the given URL
	 * 
	 * @param url
	 *            the URL to transform to an absolute media URL
	 * @return the absolute media URL
	 */
	String getMediaUrl(String url);

	/**
	 * Provides the base URL for static resources
	 * 
	 * @param url
	 *            the relative URL of the static resource
	 * @return the static absolute URL
	 */
	String getStaticUrl(String url);

	/**
	 * Generates the URL pointing to the reset password action
	 * 
	 * @param user
	 *            the {@link User} for which we generate the link
	 * @return the absolute URL
	 */
	String getResetPasswordUrl(User user);
}
