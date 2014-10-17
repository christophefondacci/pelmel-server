package com.nextep.proto.blocks;

import java.util.List;
import java.util.Locale;

import com.nextep.geo.model.GeographicItem;
import com.nextep.proto.model.SearchType;
import com.nextep.proto.services.UrlService;
import com.nextep.tags.model.Tag;
import com.videopolis.apis.model.FacetInformation;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.cals.model.PaginationInfo;
import com.videopolis.smaug.common.model.Facet;
import com.videopolis.smaug.common.model.FacetCategory;
import com.videopolis.smaug.common.model.FacetRange;
import com.videopolis.smaug.model.FacetCount;

/**
 * This interface defines the support for elements which presents the facet
 * search result box.
 * 
 * @author cfondacci
 * 
 */
public interface SearchSupport extends PaginationSupport {

	/**
	 * Initializes the facet support
	 * 
	 * @param item
	 *            {@link CalmObject} parent where the search was made, generally
	 *            a {@link GeographicItem} but not always
	 * @param facetCounts
	 *            the list of {@link FacetCount} returned by the search
	 */
	void initialize(SearchType searchType, UrlService urlService,
			Locale locale, CalmObject parent, String locationName,
			FacetInformation facetInfo, PaginationInfo pagination,
			List<? extends CalmObject> results);

	/**
	 * Accessor to the {@link FacetInformation} bean
	 * 
	 * @return the raw {@link FacetInformation}
	 */
	FacetInformation getFacetInformation();

	/**
	 * Provides the title of the page
	 * 
	 * @return title of the search page
	 */
	String getSearchTitle();

	/**
	 * Provides the title of the box presenting facets
	 * 
	 * @return the title to display on the facet box
	 */
	String getFacetsBoxTitle();

	/**
	 * The location where the search had been performed
	 * 
	 * @return the name corresponding to the location
	 */
	String getSearchLocationName();

	List<FacetCategory> getFacetCategories();

	String getFacetCategoryTitle(FacetCategory facetCategory);

	/**
	 * A list of the facet counts for the search
	 * 
	 * @return a list of {@link FacetCount}
	 */
	List<FacetCount> getFacets(FacetCategory facetCategory);

	/**
	 * Retrieves the facet range for the specified facet category. This method
	 * will return null if category is invalid or if no statistic information is
	 * available for the given category.
	 * 
	 * @param category
	 *            the {@link FacetCategory} code of the information to retrieve
	 * @return the {@link FacetRange} information
	 */
	FacetRange getFacetRange(String category);

	/**
	 * Retrieves the current user range settings for the specified facet
	 * category. For example, if a range has a minimum of 20 and a max of 100
	 * but the user narrowed his search to 30 / 50, it will return a range of
	 * 30/50. This method will return
	 * {@link SearchSupport#getFacetRange(String)} when the user has not yet
	 * defined a setting.
	 * 
	 * @param category
	 *            the {@link FacetCategory} code of the information to retrieve
	 * @return the current {@link FacetRange}
	 */
	FacetRange getCurrentFacetRange(String category);

	/**
	 * Returns the total number of elements for the current search
	 * 
	 * @return the total number of elements
	 */
	int getResultsCount();

	/**
	 * Retrieves the search results for the currently displayed page
	 * 
	 * @return
	 */
	List<? extends CalmObject> getSearchResults();

	/**
	 * The title to display for the given search result
	 * 
	 * @param searchResult
	 *            the result for which we need the title
	 * @return the title to display
	 */
	String getResultTitle(CalmObject searchResult);

	/**
	 * The URL of the icon to display immediatly before the title
	 * 
	 * @param searchResult
	 *            the result for which we need the icon URL
	 * @return the icon URL
	 */
	String getResultTitleIconUrl(CalmObject searchResult);

	/**
	 * The thumbnail to display for the given search result
	 * 
	 * @param searchResult
	 *            the result for which we need a thumbnail
	 * @return the thumbnail URL for this search result
	 */
	String getResultThumbnailUrl(CalmObject searchResult);

	/**
	 * Allows customization of the thumbnail
	 * 
	 * @param searchResult
	 * @return
	 */
	String getResultThumbnailStyle(CalmObject searchResult);

	/**
	 * Provides the URL to the mini-thumb of the provided element.
	 * 
	 * @param searchResult
	 *            the result to get a mini thumb for
	 * @return the mini thumb url, eventually providing the default mini-thumb
	 *         when no thumb is available
	 */
	String getResultMiniThumbUrl(CalmObject searchResult);

	/**
	 * The short description to display for the given search result
	 * 
	 * @param searchResult
	 *            the result for which we need a description
	 * @return the description to display for this search result
	 */
	String getResultDescription(CalmObject searchResult);

	/**
	 * The AJAX URL of the link that will be put on the result title and
	 * thumbnail. The response of this URL will be used to replace the
	 * <code>mainCol</code> DIV content of the page.
	 * 
	 * @param searchResult
	 *            the result for which we want a AJAX URL
	 * @return the AJAX URL of the specified search result
	 */
	String getResultUrl(CalmObject searchResult);

	/**
	 * Retrieves the Tags of the given search result
	 * 
	 * @param searchResult
	 *            the search result for which we need the tags
	 * @return the {@link Tag} list associated with this result
	 */
	List<? extends Tag> getTags(CalmObject searchResult);

	/**
	 * Retrieves the translation of the given facet code
	 * 
	 * @param facetCode
	 *            the code of the facet for which we want the translation
	 * @return the translated facet label
	 */
	String getFacetTranslation(String facetCode);

	/**
	 * Retrieves the URL of the icon for this facet
	 * 
	 * @param facet
	 *            the {@link ItemKey} string of the element to get an icon for
	 * @return the icon URL
	 */
	String getFacetIconUrl(String itemKey);

	/**
	 * Generates the AJAX URL that will refresh the search page by adding the
	 * specified facet to the search query. Any page information will be reset
	 * to the first page.
	 * 
	 * @param facet
	 *            the facet to generate an URL for
	 * @return the AJAX URL able to refresh the page
	 */
	String getFacetAddedUrl(Facet facet);

	/**
	 * Generates the AJAX URL that will refresh the search page by removing the
	 * specified facet to the search query. Any page information will be reset
	 * to the first page.
	 * 
	 * @param facet
	 *            the facet to remove in the generated URL
	 * @return the AJAX URL able to refresh the page without the specified facet
	 *         selected
	 */
	String getFacetRemovedUrl(Facet facet);

	/**
	 * Indicates whether the specified facet is selected or not.
	 * 
	 * @param facet
	 *            the {@link Facet} to test
	 * @return <code>true</code> if the specified facet is currently selected,
	 *         else <code>false</code>
	 */
	boolean isSelected(Facet facet);

	/**
	 * Indicates whether the specified tag is currently selected
	 * 
	 * @param tag
	 *            the {@link Tag} to check
	 * @return <code>true</code> if this tag is selected, else
	 *         <code>false</code>
	 */
	boolean isTagSelected(Tag tag);

	/**
	 * Gets the AJAX URL that can refresh the current search page by adding the
	 * tag as an additional filter
	 * 
	 * @param tag
	 *            the {@link Tag} to add to the current search query
	 * @return the AJAX URL which can refresh the page with new search results
	 */
	String getTagUrl(Tag tag);

	/**
	 * Gets the AJAX URL that can refresh the current search page by removing
	 * the tag as an additional filter
	 * 
	 * @param tag
	 *            the {@link Tag} to remove from the current search query
	 * @return the AJAX URL which can refresh the page with new search results
	 */
	String getRemoveTagUrl(Tag tag);

	/**
	 * Retrieves the text to display as an overlay over the search result thumbs
	 * 
	 * @param result
	 *            the result to get the text for
	 * @return the text to display in the overlay
	 */
	String getOverlayText(CalmObject result);

	/**
	 * Retrieves a URL for a facet range with the specified placeholders for min
	 * / max. This URL could be processed in javascript to inject the proper
	 * slider values.
	 * 
	 * @param category
	 *            the category of the facet range
	 * @return the URL template
	 */
	String getFacetRangeUrl(String category, String minPlaceHolder,
			String maxPlaceholder);

	/**
	 * Provides the name of the localization of this search result
	 * 
	 * @param result
	 *            the result to get the localization for
	 * @return the name of the location of the provided element
	 */
	String getResultLocalizationName(CalmObject result);

	/**
	 * Provides the label of the link pointing to the category search page.
	 * 
	 * @param result
	 *            the result to generate the label for
	 * @return the label
	 */
	String getResultCategoryLinkLabel(CalmObject result);

	/**
	 * Provides the URL for the link to put on the localization of this search
	 * result
	 * 
	 * @param result
	 *            the result to get the localization URL for
	 * @return the URL to a view of the location of the provided element
	 */
	String getResultLocalizationUrl(CalmObject result);

	/**
	 * Extensibility method allowing search supports to provide more information
	 * accessible through this key / value method
	 * 
	 * @param result
	 *            the {@link CalmObject} for which the information should be
	 *            retrieved
	 * @param key
	 *            key of the information to retrieve
	 * @return the information.
	 */
	String getCustomText(CalmObject result, String key);

	/**
	 * Provides the description that appears on top of the search results.
	 * 
	 * @return the description to display, or <code>null</code> if no
	 *         description available
	 */
	String getSearchDescription();
}
