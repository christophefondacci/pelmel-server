package com.nextep.proto.blocks.impl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.MessageSource;

import com.nextep.activities.model.Activity;
import com.nextep.cal.util.model.Indexable;
import com.nextep.events.model.Event;
import com.nextep.geo.model.City;
import com.nextep.geo.model.Place;
import com.nextep.media.model.Media;
import com.nextep.proto.blocks.PopularSupport;
import com.nextep.proto.blocks.PopularSupportSubtitled;
import com.nextep.proto.helpers.DisplayHelper;
import com.nextep.proto.helpers.MediaHelper;
import com.nextep.proto.model.SearchType;
import com.nextep.proto.services.UrlService;
import com.videopolis.calm.exception.CalException;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;

public class PopularActivitiesSupportImpl implements PopularSupportSubtitled {
	private static final String TRANSLATION_KEY_TITLE = "block.popular.activities.title";
	private static final String TRANSLATION_KEY_SUBTITLE = "block.popular.activities.subtitle";
	private static final String TRANSLATION_KEY_PLACETYPE_PREFIX = "place.type.";
	private static final String TRANSLATION_KEY_EVENT = "url.objType.EVNT";
	// private static final String KEY_ICON_PREFIX = "facet.icon.";
	private static final Log LOGGER = LogFactory
			.getLog(PopularActivitiesSupportImpl.class);

	private PopularSupport basePopularSupport;
	private UrlService urlService;
	private Locale locale;
	private MessageSource messageSource;
	private int maxElements = 100;
	private List<CalmObject> latestModifiedObjects;

	@Override
	public void initialize(SearchType searchType, Locale locale,
			UrlService urlService, CalmObject parent,
			List<? extends CalmObject> popularActivities, Object countObject) {

		latestModifiedObjects = new ArrayList<CalmObject>();
		final Set<ItemKey> processedItemKeys = new HashSet<ItemKey>();

		// Unwrapping activity objects
		int count = 0;
		for (CalmObject element : popularActivities) {
			final CalmObject obj = getObjectFromActivity(element);
			if (obj != null) {
				if (!processedItemKeys.contains(obj.getKey())) {
					// Checking indexation status
					if (obj instanceof Indexable) {
						final Indexable indexable = (Indexable) obj;
						if (!indexable.isIndexed()) {
							continue;
						}
					}
					latestModifiedObjects.add(element);
					processedItemKeys.add(obj.getKey());
					// Stopping when we reach max number of elements
					if (++count >= maxElements) {
						break;
					}
				}
			}

		}

		// Initializing base support
		basePopularSupport.initialize(searchType, locale, urlService, parent,
				latestModifiedObjects, countObject);
		this.urlService = urlService;
		this.locale = locale;
	}

	@Override
	public String getUrl(CalmObject element) {
		final CalmObject obj = getObjectFromActivity(element);
		return urlService.getOverviewUrl(
				DisplayHelper.getDefaultAjaxContainer(), obj);
	}

	public CalmObject getObjectFromActivity(CalmObject activity) {
		// Unwrapping object from activity
		try {
			final CalmObject obj = activity.getUnique(CalmObject.class);
			return obj;
		} catch (CalException e) {
			LOGGER.error(
					"Unable to extract object from activity "
							+ activity.getKey() + " : " + e.getMessage(), e);
		}
		return null;
	}

	@Override
	public String getIconUrl(CalmObject element) {
		final CalmObject obj = getObjectFromActivity(element);
		final Media m = MediaHelper.getSingleMedia(obj);
		if (m != null) {
			return m.getMiniThumbUrl();
		} else {
			return "/images/V2/no-photo-small.png";
		}
	}

	@Override
	public String getTitle() {
		return messageSource.getMessage(TRANSLATION_KEY_TITLE, null, locale);
	}

	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	@Override
	public List<? extends CalmObject> getPopularElements() {
		return basePopularSupport.getPopularElements();
	}

	@Override
	public String getName(CalmObject element) {
		final CalmObject obj = getObjectFromActivity(element);
		return DisplayHelper.getName(obj);
	}

	@Override
	public int getCount(CalmObject element) {
		return basePopularSupport.getCount(element);
	}

	public void setBasePopularSupport(PopularSupport basePopularSupport) {
		this.basePopularSupport = basePopularSupport;
	}

	@Override
	public String getSubtitle(CalmObject element) {
		final Activity activity = (Activity) element;
		final CalmObject obj = getObjectFromActivity(activity);

		// Computing the label of the object type that has been updated
		String typeLabel = "";
		if (obj instanceof Place) {
			final String placeType = ((Place) obj).getPlaceType();
			typeLabel = messageSource.getMessage(
					TRANSLATION_KEY_PLACETYPE_PREFIX + placeType, null, locale);
		} else if (obj instanceof Event) {
			typeLabel = messageSource.getMessage(TRANSLATION_KEY_EVENT, null,
					locale);
		}

		// Computing the date label in short format for the current locale
		DateFormat format = SimpleDateFormat.getDateInstance(DateFormat.SHORT,
				locale);
		final String dateLabel = format.format(activity.getDate());

		// Building the message
		String message = messageSource.getMessage(TRANSLATION_KEY_SUBTITLE,
				new Object[] { typeLabel, dateLabel }, locale).trim();
		if (message.length() > 1) {
			message = message.substring(0, 1).toUpperCase()
					+ message.substring(1);
		} else {
			message = message.toUpperCase();
		}
		return message;
	}

	public void setMaxElements(int maxElements) {
		this.maxElements = maxElements;
	}

	@Override
	public String getSubLinkTitle(CalmObject element) {
		final Activity activity = (Activity) element;
		final CalmObject obj = getObjectFromActivity(activity);
		if (obj instanceof Place) {
			final Place place = (Place) obj;
			final City city = place.getCity();
			return city.getName();
		}
		return null;
	}

	@Override
	public String getSubLinkUrl(CalmObject element) {
		final Activity activity = (Activity) element;
		final CalmObject obj = getObjectFromActivity(activity);
		if (obj instanceof Place) {
			final Place place = (Place) obj;
			final City city = place.getCity();
			return urlService.buildSearchUrl(
					DisplayHelper.getDefaultAjaxContainer(), city,
					SearchType.fromPlaceType(place.getPlaceType()));
		}
		return null;
	}
}
