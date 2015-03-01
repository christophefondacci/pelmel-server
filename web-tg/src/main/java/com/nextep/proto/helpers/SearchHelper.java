package com.nextep.proto.helpers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.MessageSource;

import com.nextep.events.model.Event;
import com.nextep.geo.model.Admin;
import com.nextep.geo.model.City;
import com.nextep.geo.model.Continent;
import com.nextep.geo.model.Country;
import com.nextep.geo.model.Place;
import com.nextep.proto.model.SearchType;
import com.nextep.users.model.User;
import com.videopolis.apis.model.FacetInformation;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.calm.model.Sorter;
import com.videopolis.calm.model.Sorter.Order;
import com.videopolis.calm.model.impl.SorterImpl;
import com.videopolis.smaug.common.model.Facet;
import com.videopolis.smaug.common.model.FacetCategory;
import com.videopolis.smaug.common.model.FacetRange;
import com.videopolis.smaug.common.model.SearchScope;
import com.videopolis.smaug.common.model.impl.FacetCategoryImpl;
import com.videopolis.smaug.common.model.impl.FacetImpl;
import com.videopolis.smaug.model.FacetCount;

public final class SearchHelper {

	private final static Log LOGGER = LogFactory.getLog(SearchHelper.class);

	private static final String TRANSLATION_FACET_SENTENCE_PREFIX = "facet.sentence.";
	private static final String TRANSLATION_FACET_LABEL_PREFIX = "facet.label.";
	private static final String TRANSLATION_FACET_RANGE_PREFIX = "facets.range.";
	private static final String TRANSLATION_FACET_RANGE_TITLE = "search.users.titleFacetRange";
	private static final String TRANSLATION_WITH = "search.users.titleWithFacets";

	private SearchHelper() {
	}

	public static Collection<FacetCategory> buildUserFacetCategories() {
		final Collection<FacetCategory> userCategories = new ArrayList<FacetCategory>();
		FacetCategory c = getFacetCategory("tags");
		userCategories.add(c);
		userCategories.add(getCityFacetCategory());

		FacetCategoryImpl rangeCategory = new FacetCategoryImpl();
		rangeCategory.setRange(true);
		rangeCategory.setCategoryCode("birthyear");
		userCategories.add(rangeCategory);

		rangeCategory = new FacetCategoryImpl();
		rangeCategory.setRange(true);
		rangeCategory.setCategoryCode("height_cm");
		userCategories.add(rangeCategory);

		rangeCategory = new FacetCategoryImpl();
		rangeCategory.setRange(true);
		rangeCategory.setCategoryCode("weight_kg");
		userCategories.add(rangeCategory);
		return userCategories;
	}

	public static FacetCategory getCityFacetCategory() {
		return getFacetCategory("cityId");
	}

	public static FacetCategory getCountryFacetCategory() {
		return getFacetCategory("countryId");
	}

	public static FacetCategory getRegionFacetCategory() {
		return getFacetCategory("adm1");
	}

	public static FacetCategory getContinentFacetCategory() {
		return getFacetCategory("continentId");
	}

	public static Collection<FacetCategory> buildPlaceFacetCategories(
			ItemKey parentKey) {
		FacetCategory c = getFacetCategory("placeType");
		Collection<FacetCategory> placeCategories = new ArrayList<FacetCategory>();
		placeCategories.add(c);
		c = getGeoSubFacettingCategory(parentKey);
		if (c != null) {
			placeCategories.add(c);
		}
		c = getTagFacetCategory();
		placeCategories.add(c);
		c = getAmenitiesFacetCategory();
		placeCategories.add(c);
		return placeCategories;
	}

	public static Collection<FacetCategory> buildTagPlaceFacetCategories() {
		final Collection<FacetCategory> placeCategories = new ArrayList<FacetCategory>();
		placeCategories.add(getTagFacetCategory());
		placeCategories.add(getAmenitiesFacetCategory());
		return placeCategories;
	}

	public static FacetCategory getGeoSubFacettingCategory(ItemKey parentKey) {
		if (parentKey != null) {
			final String type = parentKey.getType();
			if (City.CAL_ID.equals(type)) {
				return null;
			}
			if (Admin.CAL_ID.equals(type)) {
				return SearchHelper.getCityFacetCategory();
			} else if (Country.CAL_ID.equals(type)) {
				return SearchHelper.getRegionFacetCategory();
			} else if (Continent.CAL_ID.equals(type)) {
				return SearchHelper.getCountryFacetCategory();
			} else {
				return SearchHelper.getContinentFacetCategory();
			}
		} else {
			return null;
		}
	}

	public static Collection<FacetCategory> buildPlaceFacetCategoriesForUserSearch() {
		FacetCategory c = getFacetCategory("placeType");
		Collection<FacetCategory> placeCategories = new ArrayList<FacetCategory>();
		placeCategories.add(c);
		return placeCategories;
	}

	public static FacetCategory getPlacePopularCitiesFacetCategory() {
		return getFacetCategory("cityId");
	}

	public static Collection<FacetCategory> buildEventFacetCategories() {
		final Collection<FacetCategory> eventCategories = new ArrayList<FacetCategory>();
		FacetCategory c = getFacetCategory("tags");
		eventCategories.add(c);
		c = getEventPopularFacetCategory();
		eventCategories.add(c);

		// FacetCategoryImpl rangeCategory = new FacetCategoryImpl();
		// rangeCategory.setRange(true);
		// rangeCategory.setCategoryCode("start_date");
		// eventCategories.add(rangeCategory);

		return eventCategories;
	}

	public static FacetCategory getEventPopularFacetCategory() {
		return getFacetCategory("cityId");
	}

	/**
	 * A temporary helper to create facet category which will eventually route
	 * to the SMAUG common factory when XML definition will be generalized.
	 * 
	 * @param category
	 *            category code of this facet category
	 * @return the corresponding facet category
	 */
	public static FacetCategory getFacetCategory(String category) {
		final FacetCategoryImpl c = new FacetCategoryImpl();
		c.setCategoryCode(category);
		// Temporary workaround until proper XML definition
		if ("weight_kg".equals(category) || "height_cm".equals(category)
				|| "birthyear".equals(category) || "age".equals(category)) {
			c.setRange(true);
		}
		return c;
	}

	public static FacetCategory getTargetTypeFacetCategory() {
		return getFacetCategory("targetType");
	}

	public static List<Facet> getTargetTypeFilters(String targetType) {
		final FacetCategory c = getTargetTypeFacetCategory();
		final FacetImpl f = new FacetImpl();
		f.setFacetCategory(c);
		f.setFacetCode(targetType);
		return Arrays.asList((Facet) f);
	}

	public static FacetCategory getTagFacetCategory() {
		return getFacetCategory("tags");
	}

	public static FacetCategory getUserFacetCategory() {
		return getFacetCategory("userKey");
	}

	public static FacetCategory getAmenitiesFacetCategory() {
		return getFacetCategory("amenities");
	}

	public static List<Sorter> getEventDefaultSorters() {
		final Sorter sorter = new SorterImpl("start_date", Order.ASCENDING);
		return Arrays.asList(sorter);
	}

	public static List<Sorter> getUserDefaultSorters() {
		final Sorter sorter = new SorterImpl("onlineTimeout", Order.DESCENDING);
		return Arrays.asList(sorter);
	}

	public static List<Sorter> getPlaceDefaultSorters() {
		// final Sorter sorter = new SorterImpl("name", Order.ASCENDING);
		final Sorter sorter = getPlaceRatingSorter();
		return Arrays.asList(sorter);
	}

	public static Sorter getPlaceRatingSorter() {
		return new SorterImpl("rating", Order.DESCENDING);

	}

	public static Sorter getDistanceSorter() {
		final Sorter sorter = new SorterImpl("geo_distance", Order.ASCENDING);
		return sorter;
	}

	public static List<Sorter> getActivitiesDefaultSorter() {
		final Sorter sorter = new SorterImpl("activityDate", Order.DESCENDING);
		return Arrays.asList(sorter);
	}

	public static FacetCategory getActivitiesPlaceFacet() {
		return getFacetCategory("placeKey");
	}

	public static List<FacetCategory> buildNewsUserFacetCategories() {
		return Arrays.asList(getUserEventsCategory(), getUserPlacesCategory());
	}

	public static FacetCategory getUserEventsCategory() {
		return getFacetCategory("events");
	}

	public static FacetCategory getUserPlacesCategory() {
		return getFacetCategory("places");
	}

	public static FacetCategory getUserCurrentPlaceCategory() {
		return getFacetCategory("currentPlace");
	}

	public static List<FacetCategory> buildUserPlacesCategories() {
		return Arrays.asList(getUserCurrentPlaceCategory(),
				getUserPlacesCategory());
	}

	public static List<FacetCategory> buildUserEventsCategories() {
		return Arrays.asList(getUserEventsCategory());
	}

	public static FacetCategory getPlaceTypeCategory() {
		return getFacetCategory("placeType");
	}

	public static List<Facet> buildFacetFilters(FacetCategory c, String facets) {
		return buildFacetFilters(c, facets, null);
	}

	public static List<Facet> buildFacetFilters(FacetCategory c, String facets,
			SearchType searchType) {
		final List<Facet> facetFilters = new ArrayList<Facet>();
		if (facets != null) {
			final String[] facetList = facets.split(",");
			final FacetCategory tagCategory = SearchHelper
					.getTagFacetCategory();
			for (String facet : facetList) {
				final String facetCode = facet.trim();
				final FacetImpl f = new FacetImpl();
				f.setFacetCode(facetCode);
				// if (facetCode.startsWith(Tag.CAL_ID)) {
				// f.setFacetCategory(tagCategory);
				// } else {
				f.setFacetCategory(c);
				// }
				facetFilters.add(f);
			}
		}
		// Filtering place type
		if (searchType != null) {
			final String placeType = searchType.getSubtype();
			final FacetImpl f = new FacetImpl();
			f.setFacetCode(placeType);
			f.setFacetCategory(getPlaceTypeCategory());
			facetFilters.add(f);
		}
		return facetFilters;
	}

	/**
	 * Builds the search title given a properly configured title message key and
	 * search parameters. This method will build the fully qualified facetted
	 * string.
	 * 
	 * @param messageSource
	 *            the {@link MessageSource} to use to retrieve messages
	 * @param locale
	 *            the current {@link Locale}
	 * @param titleKey
	 *            the message key of the main title. This message should have
	 *            the following parameters : 1.name of the parent, 2.results
	 *            count, 3.facets list, 4.facet ranges
	 * @param resultsCount
	 *            number of results
	 * @param parentName
	 *            name of the parent
	 * @param facets
	 *            list of current facet filters
	 * @return the title string
	 */
	public static String buildSearchTitle(MessageSource messageSource,
			Locale locale, String titleKey, int resultsCount,
			String parentName, Collection<Facet> facets) {
		// Building any facet text
		final StringBuilder facetText = new StringBuilder();
		final StringBuilder rangeText = new StringBuilder();
		String placeType = "";
		final Set<String> processedFacetCodes = new HashSet<String>();
		if (facets != null && !facets.isEmpty()) {
			String facetPrefix = "";
			String rangePrefix = " ";
			for (Facet f : facets) {
				if (!f.getFacetCategory().isRange()) {
					// Eliminating double facets
					if (!processedFacetCodes.contains(f.getFacetCode())) {
						if (!f.getFacetCategory().equals(
								SearchHelper.getPlaceTypeCategory())) {
							facetText.append(facetPrefix);
							// Simply appending the translation of our facet tag
							facetText.append(messageSource.getMessage(
									TRANSLATION_FACET_SENTENCE_PREFIX
											+ f.getFacetCode(), null, locale));
							facetPrefix = ", ";
							processedFacetCodes.add(f.getFacetCode());
						} else {
							placeType = messageSource.getMessage(
									TRANSLATION_FACET_LABEL_PREFIX
											+ f.getFacetCode(), null, locale)
									.toLowerCase();
						}
					}
				} else {
					rangeText.append(rangePrefix);
					// For range we need to build up a sentence
					final FacetRange range = (FacetRange) f;
					final String category = f.getFacetCategory()
							.getCategoryCode();
					// Translating our range
					final String categoryTranslation = messageSource
							.getMessage(TRANSLATION_FACET_RANGE_PREFIX
									+ category, null, locale);
					// Extracting range boundaries
					long lowerBound = range.getLowerBound();
					long upperBound = range.getHigherBound();
					if ("birthyear".equals(category)) {
						// Computing difference with current year to make an age
						final Calendar c = Calendar.getInstance();
						final int currentYear = c.get(Calendar.YEAR);
						final long ageLowerBound = currentYear - upperBound;
						final long ageUpperBound = currentYear - lowerBound;
						// Assigning age bounds
						lowerBound = ageLowerBound;
						upperBound = ageUpperBound;
					}
					rangeText.append(messageSource.getMessage(
							TRANSLATION_FACET_RANGE_TITLE,
							new Object[] { categoryTranslation, lowerBound,
									upperBound }, locale));
					rangePrefix = ", ";
				}

			}
		}
		// Building range sentence fragment if there is any range
		String rangeFragment = "";
		if (rangeText.length() > 0) {
			rangeFragment = messageSource.getMessage(TRANSLATION_WITH,
					new Object[] { rangeText.toString() }, locale);
		}
		String facetFragment = "";
		if (facetText.length() > 0) {
			facetFragment = messageSource.getMessage(TRANSLATION_WITH,
					new Object[] { facetText.toString() }, locale);
		}
		// Building the title by injecting result counts, parent name,
		// and facet string
		return messageSource
				.getMessage(titleKey, new Object[] { parentName, resultsCount,
						facetFragment, rangeFragment, placeType }, locale);
	}

	public static String getPlaceType(FacetInformation facetInfo) {
		final Collection<Facet> facets = facetInfo.getFacetFilters();
		String placeType = null;
		for (Facet f : facets) {
			if (f.getFacetCategory()
					.equals(SearchHelper.getPlaceTypeCategory())) {
				placeType = f.getFacetCode();
			}
		}
		return placeType;
	}

	// public static FacetInformation buildFacetInformationFromPlaceType(
	// String placeType) {
	// final FacetInformationImpl facetInfo = new FacetInformationImpl(
	// new HashMap<FacetCategory, List<FacetCount>>());
	// facetInfo.setFacetFilters(buildFacetFilters(getPlaceTypeCategory(),
	// placeType));
	// return facetInfo;
	// }

	/**
	 * Provides the search scope that performs a search of the specific place
	 * type.
	 * 
	 * @param placeType
	 *            the place type to look for
	 * @return the corresponding {@link SearchScope}
	 */
	public static SearchScope getSearchScope(String placeType) {
		if (placeType != null) {
			try {
				return SearchScope.valueOf(placeType.toUpperCase());
			} catch (IllegalArgumentException e) {
				LOGGER.debug("Unable to build SearchScope from place type '"
						+ placeType + "' : " + e.getMessage(), e);
			}
		}
		return SearchScope.CHILDREN;
	}

	/**
	 * Provides the most reliable search type associated to this kind of element
	 * 
	 * @param object
	 *            any {@link CalmObject}
	 * @return the corresponding {@link SearchType}
	 */
	public static SearchType getSearchType(CalmObject object) {
		// Default search type is bars
		SearchType searchType = SearchType.BARS;
		if (object != null) {
			// For places we convert place types
			if (object instanceof Place) {
				final Place place = (Place) object;
				searchType = SearchType.fromPlaceType(place.getPlaceType());
			} else if (object instanceof Event) {
				searchType = SearchType.EVENTS;
			} else if (object instanceof User) {
				searchType = SearchType.MEN;
			}
		}
		return searchType;
	}

	/**
	 * Unwraps the facet information structure into a map of facet counts hashed
	 * by their facet codes represented as plain string.
	 * 
	 * @param facetInfo
	 *            the {@link FacetInformation} to unwrap
	 * @param category
	 *            the {@link FacetCategory} to unwrap
	 * @return the corresponding {@link Map}
	 */
	public static Map<String, Integer> unwrapFacets(FacetInformation facetInfo,
			FacetCategory category) {
		final Map<String, Integer> facetMap = new HashMap<String, Integer>();
		if (facetInfo != null) {
			final List<FacetCount> facetCounts = facetInfo
					.getFacetCounts(category);
			for (FacetCount c : facetCounts) {
				facetMap.put(c.getFacet().getFacetCode(), c.getCount());
			}
		}
		return facetMap;
	}
}
