package com.nextep.proto.blocks.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.context.MessageSource;

import com.nextep.geo.model.Place;
import com.nextep.media.model.Media;
import com.nextep.proto.blocks.FavoritesSupport;
import com.nextep.proto.helpers.DisplayHelper;
import com.nextep.proto.helpers.MediaHelper;
import com.nextep.proto.model.PlaceType;
import com.nextep.proto.services.DistanceDisplayService;
import com.nextep.proto.services.UrlService;
import com.videopolis.apis.service.ApiResponse;
import com.videopolis.calm.model.CalmObject;

public class NearbyPlacesSupportImpl implements FavoritesSupport {

	private static final String TRANSLATION_PLACE_TYPE_PREFIX = "facet.label.";
	private static final String TRANSLATION_TITLE = "block.nearbyPlaces.title";
	private static final String ICON_PLACE_TYPE_PREFIX = "facet.icon.";
	private DistanceDisplayService distanceService;
	private MessageSource messageSource;

	private UrlService urlService;
	private Locale locale;
	private CalmObject parent;
	private Map<String, List<Place>> nearbyPlacesMap;
	private List<String> categories;
	private Map<String, String> translationCategoryMap;
	private ApiResponse response;
	private List<PlaceType> orderedPlaceTypes;

	@Override
	public void initilialize(UrlService urlService, Locale locale,
			CalmObject parent, List<? extends CalmObject> favorites) {
		this.urlService = urlService;
		this.locale = locale;
		this.parent = parent;
		this.nearbyPlacesMap = new HashMap<String, List<Place>>();
		translationCategoryMap = new HashMap<String, String>();
		orderedPlaceTypes = Arrays.asList(PlaceType.values());
		this.categories = new ArrayList<String>();
		for (CalmObject o : favorites) {
			if (!o.getKey().equals(parent.getKey())) {
				final Place p = (Place) o;
				final String placeType = p.getPlaceType();
				List<Place> places = nearbyPlacesMap.get(placeType);
				if (places == null) {
					places = new ArrayList<Place>();
					nearbyPlacesMap.put(placeType, places);
					// Translating place type
					final String placeTypeTranslation = messageSource
							.getMessage(TRANSLATION_PLACE_TYPE_PREFIX
									+ placeType, null, locale);
					categories.add(placeType);
					translationCategoryMap.put(placeType, placeTypeTranslation);
				}
				places.add(p);
			}
		}
	}

	@Override
	public void setMetadata(Object metadata) {
		this.response = (ApiResponse) metadata;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Object> getCategories() {
		// Sorting categories (which have already been translated in the init
		// method)
		Collections.sort(categories, new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				PlaceType type1 = PlaceType.valueOf(o1);
				PlaceType type2 = PlaceType.valueOf(o2);

				return orderedPlaceTypes.indexOf(type1)
						- orderedPlaceTypes.indexOf(type2);
			}
		});
		return (List) categories;
	}

	@Override
	public String getCategoryTitle(Object category) {
		final String translation = translationCategoryMap.get(category);
		return messageSource.getMessage("nearby.category",
				new Object[] { translation }, locale);
	}

	@Override
	public String getTitle() {
		return messageSource.getMessage(TRANSLATION_TITLE, null, locale);
	}

	@Override
	public List<? extends CalmObject> getFavorites(String category) {
		final String placeType = category;
		if (placeType != null) {
			return nearbyPlacesMap.get(placeType);
		} else {
			return Collections.emptyList();
		}
	}

	@Override
	public String getFavoriteName(CalmObject favoriteObj) {
		return DisplayHelper.getName(favoriteObj);
	}

	@Override
	public String getFavoriteNamePrefix(CalmObject favoriteObj) {
		return distanceService.getDistanceFromItem(favoriteObj.getKey(),
				response, locale);
	}

	@Override
	public String getFavoriteImageUrl(CalmObject favoriteObj) {
		final Media media = MediaHelper.getSingleMedia(favoriteObj);
		if (media != null) {
			return urlService.getMediaUrl(media.getThumbUrl());
		} else {
			return urlService.getStaticUrl(MediaHelper
					.getNoThumbUrl(favoriteObj));
		}
	}

	@Override
	public String getCategoryStyle(Object category) {
		return (String) category;
	}

	@Override
	public String getFavoriteLinkUrl(CalmObject favoriteObj) {
		return urlService.getPlaceOverviewUrl("mainContent", favoriteObj);
	}

	public void setDistanceService(DistanceDisplayService distanceService) {
		this.distanceService = distanceService;
	}

	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}
}
