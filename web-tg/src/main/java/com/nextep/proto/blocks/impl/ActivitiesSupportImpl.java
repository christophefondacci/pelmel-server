package com.nextep.proto.blocks.impl;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;

import com.nextep.activities.model.Activity;
import com.nextep.activities.model.ActivityType;
import com.nextep.geo.model.GeographicItem;
import com.nextep.geo.model.Place;
import com.nextep.media.model.Media;
import com.nextep.proto.blocks.ActivitySupport;
import com.nextep.proto.helpers.DisplayHelper;
import com.nextep.proto.model.ActivityConstants;
import com.nextep.proto.model.Constants;
import com.nextep.proto.services.DistanceDisplayService;
import com.nextep.proto.services.UrlService;
import com.nextep.users.model.User;
import com.videopolis.calm.exception.CalException;
import com.videopolis.calm.factory.CalmFactory;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.cals.model.PaginationInfo;

/**
 * Default implementation of {@link ActivitySupport}
 * 
 * @author cfondacci
 * 
 */
public class ActivitiesSupportImpl implements ActivitySupport {

	// Block static constants
	private static final Log LOGGER = LogFactory
			.getLog(ActivitiesSupportImpl.class);
	private static final String TRANSLATION_ACTIVITY_PREFIX = "activity.";
	private static final String TRANSLATION_OBJECT_TYPE_PREFIX = "activity.objType.";
	private static final String TRANSLATION_ACTIVITIES_TITLE = "block.activities.title";
	private static final String TRANSLATION_FIELD_PREFIX = "activity.field.";
	private static final String TRANSLATION_AGO = "time.ago";

	// Injected services
	private MessageSource messageSource;
	private DistanceDisplayService distanceService;

	// Local variables
	private UrlService urlService;
	private Locale locale;
	private PaginationInfo paginationInfo;
	private CalmObject targetObj;
	private User user;
	private GeographicItem geoItem;
	private List<? extends Activity> activities;
	private String title;
	private String activityHtmlContentId = "activity-contents";
	private String typeFilter;

	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	@Override
	public void initialize(UrlService urlService, Locale locale,
			PaginationInfo paginationInfo, List<? extends Activity> activities) {
		this.urlService = urlService;
		this.locale = locale;
		this.paginationInfo = paginationInfo;
		this.activities = activities;
		this.targetObj = null;
		this.user = null;
	}

	@Override
	public void initializeUser(User user) {
		this.user = user;
	}

	@Override
	public void initializeTarget(CalmObject object) {
		this.targetObj = object;
	}

	@Override
	public void initializeGeo(GeographicItem geoItem, String typeFilter) {
		this.geoItem = geoItem;
		this.typeFilter = typeFilter;
	}

	@Override
	public void setActivityHtmlContentId(String contentId) {
		this.activityHtmlContentId = contentId;
	}

	@Override
	public String getActivityHtmlContentId() {
		return activityHtmlContentId;
	}

	@Override
	public User getUser(Activity activity) {
		if (user != null) {
			return user;
		} else {
			try {
				final User user = activity.getUnique(User.class,
						Constants.ALIAS_ACTIVITY_USER);
				return user;
			} catch (CalException e) {
				LOGGER.error("Unable to get user of activity "
						+ activity.getKey());
			}
		}
		return null;
	}

	@Override
	public CalmObject getTarget(Activity activity) {
		if (targetObj != null) {
			return targetObj;
		} else {
			try {
				final CalmObject o = activity.getUnique(CalmObject.class,
						Constants.ALIAS_ACTIVITY_TARGET);
				return o;
			} catch (CalException e) {
				LOGGER.error("Unable to get target of activity "
						+ activity.getKey());
			}
		}
		return null;
	}

	@Override
	public List<? extends Activity> getActivities() {
		return activities;
	}

	@Override
	public String getDateTimeLabel(Activity activity) {
		final String timeLabel = distanceService.getTimeBetweenDates(
				new Date(), activity.getDate(), locale, false);

		final String fullLabel = messageSource.getMessage(TRANSLATION_AGO,
				new Object[] { timeLabel }, locale);
		return fullLabel;
	}

	@Override
	public String getFrom(Activity activity) {
		final User user = getUser(activity);
		return DisplayHelper.getName(user);
	}

	@Override
	public String getFromUrl(Activity a) {
		final User user = getUser(a);
		return urlService.getUserOverviewUrl("mainContent", user);
	}

	@Override
	public String getActivityIconUrl(Activity a) {
		String imageUrl = null;
		if (a != null) {
			if (a.getActivityType() == ActivityType.SEO_OPEN) {
				imageUrl = urlService.getStaticUrl("/images/V2/logo-thumb.png");
			} else {
				final User user = getUser(a);
				final List<? extends Media> mediaList = user.get(Media.class);
				Media media = null;
				if (mediaList != null && !mediaList.isEmpty()) {
					media = mediaList.iterator().next();
				}
				if (media != null) {
					imageUrl = urlService.getMediaUrl(media.getMiniThumbUrl());
				} else {
					imageUrl = urlService
							.getStaticUrl("/images/V2/no-photo-profile-small.png");
				}
			}
		}
		return imageUrl;
	}

	@Override
	public String getActivityIconLinkUrl(Activity activity) {
		return getFromUrl(activity);
	}

	private CalmObject getExtraObject(Activity a) {
		try {
			final CalmObject object = a.getUnique(CalmObject.class,
					Constants.ALIAS_ACTIVITY_OBJECT);
			return object;
		} catch (CalException e) {
			LOGGER.error("Problems fetching extra object of activity "
					+ a.getKey().toString());
		}
		return null;
	}

	@Override
	public String getActivityHtmlLine(Activity a) {
		try {
			final ActivityType type = a.getActivityType();
			final User user = getUser(a);
			final CalmObject target = getTarget(a);
			// Target initialization
			final String targetName = DisplayHelper.getName(target);
			final String targetType = target.getKey().getType();
			// if (LOGGER.isDebugEnabled()) {
			// LOGGER.debug("Looking for message '"
			// + TRANSLATION_OBJECT_TYPE_PREFIX + targetType
			// + "' for locale " + locale);
			// }
			String targetTypeLabel = messageSource.getMessage(
					TRANSLATION_OBJECT_TYPE_PREFIX + targetType, null, locale);
			if (type == ActivityType.LOCALIZATION
					&& Place.CAL_TYPE.equals(targetType)) {
				targetTypeLabel = "";
			}
			final String targetUrl = urlService.getOverviewUrl("mainContent",
					target);
			final String userName = DisplayHelper.getName(user);
			final String userUrl = user == null ? null : urlService
					.getUserOverviewUrl(
							DisplayHelper.getDefaultAjaxContainer(), user);
			// Extra object
			final CalmObject extraObject = getExtraObject(a);
			String extraObjName = DisplayHelper.getName(extraObject);
			if (extraObjName == null) {
				extraObjName = "";
			}
			String extraObjTypeLabel = "";
			String extraObjUrl = "";
			String extraObjType = "";
			if (extraObject != null) {
				extraObjType = extraObject.getKey().getType();
				extraObjTypeLabel = messageSource.getMessage(
						TRANSLATION_OBJECT_TYPE_PREFIX + extraObjType, null,
						locale);
				extraObjUrl = urlService.getOverviewUrl(
						DisplayHelper.getDefaultAjaxContainer(), extraObject);
			} else if (a.getExtraInformation() != null
					&& a.getActivityType() == ActivityType.DELETION) {
				// This step is required for deleted elements which may not be
				// retrieved by APIS. We still need the type of the removed
				// element to build the proper activity string so we deduce
				// it from the CAL key of the extra information
				try {
					// Trying to get CAL item key from extra obj
					extraObjType = CalmFactory
							.parseKey(a.getExtraInformation()).getType();
					extraObjTypeLabel = messageSource.getMessage(
							TRANSLATION_OBJECT_TYPE_PREFIX + extraObjType,
							null, locale);
				} catch (CalException e) {
					LOGGER.debug("Unable to parse CAL key from extra activity key '"
							+ a.getExtraInformation()
							+ "', this might be normal");
				}
			}
			// Optionally translating extra info
			String extraInfoLabel = a.getExtraInformation();
			if (a.getActivityType() == ActivityType.UPDATE) {
				// UPDATE means we have a list of fields in extra info
				// so we split them and translate
				String[] fields = extraInfoLabel
						.split(ActivityConstants.SEPARATOR);
				final StringBuilder buf = new StringBuilder();
				String separator = "";
				// Translating field by field and building our label
				for (String field : fields) {
					final String fieldLabel = messageSource.getMessage(
							TRANSLATION_FIELD_PREFIX + field, null, locale);
					buf.append(separator + fieldLabel);
					separator = ActivityConstants.SEPARATOR;
				}

				extraInfoLabel = buf.toString();
			}
			// if (LOGGER.isDebugEnabled()) {
			// LOGGER.debug("Providing activity for key '"
			// + TRANSLATION_ACTIVITY_PREFIX + type.name()
			// + "' for locale " + locale);
			// }
			// Now rendering the full activity message
			final List<String> messageKeys = Arrays.asList(
					TRANSLATION_ACTIVITY_PREFIX + type.name() + "."
							+ targetType + "." + extraObjType,
					TRANSLATION_ACTIVITY_PREFIX + type.name() + "."
							+ targetType,
					TRANSLATION_ACTIVITY_PREFIX + type.name());
			for (String messageKey : messageKeys) {
				try {
					final String msg = messageSource.getMessage(messageKey,
							new Object[] { targetTypeLabel, targetName,
									userName, extraObjName, extraObjTypeLabel,
									extraInfoLabel, targetUrl, extraObjUrl,
									userUrl }, locale);
					return msg;
				} catch (NoSuchMessageException e) {
					// Silently skipping to next message key
				}
			}
			return "unknown activity";
		} catch (Throwable t) {
			LOGGER.error("Problems during generation of activity line : "
					+ t.getMessage());
			return null;
		}
	}

	@Override
	public String getTitle() {
		if (title == null) {
			return messageSource.getMessage(TRANSLATION_ACTIVITIES_TITLE,
					new Object[] { DisplayHelper.getName(targetObj) }, locale);
		}
		return title;
	}

	@Override
	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public Integer getCurrentPage() {
		return paginationInfo.getCurrentPageNumber() + 1;
	}

	@Override
	public List<Integer> getPagesList() {
		return DisplayHelper.buildPagesList(paginationInfo.getPageCount(),
				paginationInfo.getCurrentPageNumber() + 1, 2);
	}

	@Override
	public String getPageUrl(int page) {
		return "javascript:waitCall('"
				+ urlService.getActivitiesUrl(activityHtmlContentId, targetObj,
						user, geoItem, page - 1, typeFilter) + "','"
				+ activityHtmlContentId + "','#" + activityHtmlContentId
				+ "-wait')";
	}

	public void setDistanceService(DistanceDisplayService distanceService) {
		this.distanceService = distanceService;
	}
}
