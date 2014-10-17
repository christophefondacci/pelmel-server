package com.nextep.proto.blocks.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;

import com.nextep.cal.util.model.Named;
import com.nextep.descriptions.model.Description;
import com.nextep.geo.model.City;
import com.nextep.geo.model.Place;
import com.nextep.media.model.Media;
import com.nextep.proto.blocks.SearchSupport;
import com.nextep.proto.helpers.DisplayHelper;
import com.nextep.proto.helpers.MediaHelper;
import com.nextep.proto.model.PlaceType;
import com.nextep.proto.model.SearchType;
import com.nextep.proto.services.UrlService;
import com.nextep.tags.model.Tag;
import com.nextep.users.model.User;
import com.videopolis.apis.model.FacetInformation;
import com.videopolis.calm.exception.CalException;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.cals.model.PaginationInfo;
import com.videopolis.smaug.common.model.Facet;
import com.videopolis.smaug.common.model.FacetCategory;
import com.videopolis.smaug.common.model.FacetRange;
import com.videopolis.smaug.model.FacetCount;

/**
 * A generic, common search support that could be use as a base for other search
 * support implementations. This implementation should generally not be used
 * directly as customization is generally required. Instead, compose your
 * implementation with this search support and delegate standard behaviour to
 * it.
 * 
 * @author cfondacci
 * 
 */
public class CommonSearchSupportImpl implements SearchSupport {

	private static final Log LOGGER = LogFactory
			.getLog(CommonSearchSupportImpl.class);
	private static final String KEY_ICON_PREFIX = "facet.icon.";
	private static final String KEY_TRANSLATION_CATEGORY_PREFIX = "facets.categories.";
	private static final String KEY_FACET_TRANSLATION_PREFIX = "facet.label.";
	private static final int PAGES_BEFORE_AFTER = 5;

	private MessageSource messageSource;

	private UrlService urlService;
	private SearchType searchType;
	private Locale locale;
	private CalmObject geoItem;
	private String locationName;
	private FacetInformation facetInfo;
	private PaginationInfo pagination;
	private List<? extends CalmObject> results;

	@Override
	public void initialize(SearchType searchType, UrlService urlService,
			Locale locale, CalmObject geoItem, String locationName,
			FacetInformation facetInfo, PaginationInfo pagination,
			List<? extends CalmObject> results) {
		this.searchType = searchType;
		this.urlService = urlService;
		this.locale = locale;
		this.geoItem = geoItem;
		this.locationName = locationName;
		this.facetInfo = facetInfo;
		this.pagination = pagination;
		this.results = results;
	}

	@Override
	public FacetInformation getFacetInformation() {
		return facetInfo;
	}

	@Override
	public String getSearchTitle() {
		return null;
	}

	@Override
	public String getSearchLocationName() {
		return locationName;
	}

	@Override
	public List<FacetCategory> getFacetCategories() {
		final List<FacetCategory> categories = new ArrayList<FacetCategory>(
				facetInfo.getFacettedCategories());
		Collections.sort(categories, new Comparator<FacetCategory>() {
			@Override
			public int compare(FacetCategory o1, FacetCategory o2) {
				return getFacetCategoryTitle(o1).compareTo(
						getFacetCategoryTitle(o2));
			}
		});
		// Removing any category having no facet
		for (FacetCategory c : facetInfo.getFacettedCategories()) {
			if (facetInfo.getFacetCounts(c).isEmpty()) {
				categories.remove(c);
			}
		}
		return categories;
	}

	@Override
	public String getFacetCategoryTitle(FacetCategory facetCategory) {
		final String translationKey = KEY_TRANSLATION_CATEGORY_PREFIX
				+ searchType.name();
		try {
			return messageSource.getMessage(translationKey, null, locale);
		} catch (NoSuchMessageException e) {
			return translationKey;
		}
	}

	@Override
	public List<FacetCount> getFacets(FacetCategory category) {
		List<FacetCount> facetCounts = facetInfo.getFacetCountsMap().get(
				category);
		if (facetCounts != null && !facetCounts.isEmpty()) {
			Collections.sort(facetCounts, new Comparator<FacetCount>() {
				@Override
				public int compare(FacetCount o1, FacetCount o2) {
					final String t1 = getFacetTranslation(o1.getFacet()
							.getFacetCode());
					final String t2 = getFacetTranslation(o2.getFacet()
							.getFacetCode());
					return t1.compareTo(t2);
				}
			});
			return facetCounts;
		} else {
			return Collections.emptyList();
		}
	}

	@Override
	public FacetRange getFacetRange(String category) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FacetRange getCurrentFacetRange(String category) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getResultsCount() {
		return pagination.getItemCount();
	}

	@Override
	public List<? extends CalmObject> getSearchResults() {
		return results;
	}

	@Override
	public String getResultTitle(CalmObject searchResult) {
		if (searchResult instanceof Named) {
			return DisplayHelper.getName(searchResult);
		}
		return null;
	}

	@Override
	public String getResultThumbnailUrl(CalmObject searchResult) {
		final Media media = MediaHelper.getSingleMedia(searchResult);
		if (media != null) {
			return media.getThumbUrl();
		} else {
			// No photo is handled by CSS for bars, saunas and clubs
			final Place p = (Place) searchResult;
			try {
				final PlaceType type = PlaceType.valueOf(p.getPlaceType());
				switch (type) {
				case bar:
				case club:
				case sauna:
					return null;
				default:
					break;
				}
			} catch (RuntimeException e) {
				LOGGER.warn("Invalid place type: '" + p.getPlaceType()
						+ "' for place " + p.getKey());
			}
		}
		if (searchResult instanceof User) {
			return "/images/V2/no-photo-user.png";
		} else {
			return "/images/V2/no-photo.png";
		}
	}

	@Override
	public String getResultMiniThumbUrl(CalmObject searchResult) {
		final Media m = MediaHelper.getSingleMedia(searchResult);
		if (m != null) {
			return m.getMiniThumbUrl();
		}
		return null;
	}

	@Override
	public String getResultDescription(CalmObject searchResult) {
		final Description desc = DisplayHelper.getSingleDescription(
				searchResult, locale);
		if (desc != null) {
			final String status = desc.getDescription();
			if (status.length() > 300) {
				return status.substring(0, 300) + "...";
			} else {
				return status;
			}
		} else {
			return "";
		}
	}

	@Override
	public String getResultUrl(CalmObject searchResult) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<? extends Tag> getTags(CalmObject searchResult) {
		final List<? extends Tag> tags = searchResult.get(Tag.class);
		Collections.sort(tags, new Comparator<Tag>() {
			@Override
			public int compare(Tag o1, Tag o2) {
				final String t1 = getFacetTranslation(o1.getKey().toString());
				final String t2 = getFacetTranslation(o2.getKey().toString());
				return t1.compareTo(t2);
			}
		});
		return tags;
	}

	@Override
	public List<Integer> getPagesList() {
		return DisplayHelper.buildPagesList(pagination.getPageCount(),
				pagination.getCurrentPageNumber() + 1, PAGES_BEFORE_AFTER);
	}

	@Override
	public Integer getCurrentPage() {
		return pagination.getCurrentPageNumber() + 1;
	}

	@Override
	public String getPageUrl(int page) {
		return urlService.buildSearchUrl(
				DisplayHelper.getDefaultAjaxContainer(), geoItem, searchType,
				facetInfo, page - 1);
	}

	@Override
	public String getFacetTranslation(String facetCode) {
		return messageSource.getMessage(KEY_FACET_TRANSLATION_PREFIX
				+ facetCode, null, locale);
	}

	@Override
	public String getFacetIconUrl(String itemKey) {
		return messageSource
				.getMessage(KEY_ICON_PREFIX + itemKey, null, locale);
	}

	@Override
	public String getFacetAddedUrl(Facet facet) {
		return urlService.buildSearchUrl(
				DisplayHelper.getDefaultAjaxContainer(), geoItem, searchType,
				facetInfo, facet, null);
	}

	@Override
	public String getFacetRemovedUrl(Facet facet) {
		return urlService.buildSearchUrl(
				DisplayHelper.getDefaultAjaxContainer(), geoItem, searchType,
				facetInfo, null, facet);
	}

	@Override
	public boolean isSelected(Facet facet) {
		return facetInfo.getFacetFilters().contains(facet);
	}

	@Override
	public boolean isTagSelected(Tag tag) {
		if (facetInfo != null) {
			final Collection<Facet> currentFacets = facetInfo.getFacetFilters();
			for (Facet f : currentFacets) {
				if (f.getFacetCode().equals(tag.getKey().toString())) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public String getTagUrl(Tag tag) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getRemoveTagUrl(Tag tag) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getOverlayText(CalmObject result) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getFacetRangeUrl(String category, String minPlaceHolder,
			String maxPlaceholder) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getResultLocalizationName(CalmObject result) {
		Exception ex = null;
		try {
			final City city = result.getUnique(City.class);
			if (city != null) {
				return DisplayHelper.getName(city);
			}
		} catch (CalException e) {
			ex = e;
		}
		LOGGER.error("Unable to extract city for user search result "
				+ result.getKey() + (ex != null ? " : " + ex.getMessage() : ""));
		return "n/a";
	}

	@Override
	public String getResultLocalizationUrl(CalmObject result) {
		// TODO Auto-generated method stub
		return null;
	}

	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	@Override
	public String getCustomText(CalmObject object, String key) {
		return "";
	}

	@Override
	public String getFacetsBoxTitle() {
		return "";
	}

	@Override
	public String getResultTitleIconUrl(CalmObject searchResult) {
		return null;
	}

	@Override
	public String getResultThumbnailStyle(CalmObject searchResult) {
		if (searchResult instanceof Place) {
			return ((Place) searchResult).getPlaceType() + "-photo-filler";
		}
		return null;
	}

	@Override
	public String getSearchDescription() {
		final List<? extends Description> descriptions = geoItem
				.get(Description.class);
		// If we have at least one description
		if (descriptions != null && !descriptions.isEmpty()) {
			// Only displaying for page 1 on bars
			if (pagination.getCurrentPageNumber() == 0
					&& searchType == SearchType.BARS) {
				// Extracting localized description
				final Description d = DisplayHelper.getSingleDescription(
						geoItem, locale);
				if (d != null) {
					return d.getDescription();
				}
			}
		}
		return null;
	}

	@Override
	public String getResultCategoryLinkLabel(CalmObject result) {
		return getResultLocalizationName(result);
	}
}
