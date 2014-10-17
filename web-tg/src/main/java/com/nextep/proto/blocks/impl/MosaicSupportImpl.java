package com.nextep.proto.blocks.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.MessageSource;

import com.nextep.activities.model.Activity;
import com.nextep.activities.model.ActivityType;
import com.nextep.events.model.Event;
import com.nextep.geo.model.Place;
import com.nextep.media.model.Media;
import com.nextep.proto.blocks.ActivitySupport;
import com.nextep.proto.blocks.MosaicSupport;
import com.nextep.proto.helpers.DisplayHelper;
import com.nextep.proto.helpers.MediaHelper;
import com.nextep.proto.model.Constants;
import com.nextep.proto.services.UrlService;
import com.nextep.users.model.User;
import com.videopolis.apis.calm.impl.CalmObjectAggregator;
import com.videopolis.calm.exception.CalException;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;

public class MosaicSupportImpl implements MosaicSupport {

	private static final Log LOGGER = LogFactory
			.getLog(MosaicSupportImpl.class);
	private static final String MESSAGE_LIKE = "activity.LIKE";
	private static final String MESSAGE_LIKE_EVENT = "activity.LIKE.EVNT";
	private static final String MESSAGE_LIKER_PREFIX = "activity.LIKER.";
	private static final String MESSAGE_LIKER_DEFAULT = "activity.LIKER";
	private static final String MESSAGE_OBJ_TYPE_PREFIX = "activity.objType.";

	private int rows;
	private int columns;
	private MessageSource messageSource;

	private UrlService urlService;
	private Locale locale;
	private ActivitySupport activitySupport;
	private String titleMessageKey;
	private List<Integer> rowContents;
	private List<Integer> rowOffsets;

	private List<CalmObject> elements;
	private CalmObject parent;

	private List<CalmObject> gridElements;
	private List<? extends CalmObject> likers;
	private List<? extends CalmObject> likes;
	private List<? extends CalmObject> viewers;

	private static CalmObjectAggregator BLANK_FILLER = new CalmObjectAggregator();

	@SuppressWarnings("unchecked")
	@Override
	public void initialize(UrlService urlService, Locale locale,
			CalmObject parentObject, ActivitySupport activitySupport,
			List<? extends CalmObject> likers,
			List<? extends CalmObject> likes,
			List<? extends Activity> activities,
			List<? extends CalmObject> viewers,
			List<? extends CalmObject>... elementLists) {
		this.urlService = urlService;
		this.locale = locale;
		this.parent = parentObject;
		this.activitySupport = activitySupport;
		this.likers = likers == null ? Collections.EMPTY_LIST : likers;
		this.likes = likes == null ? Collections.EMPTY_LIST : likes;
		this.viewers = viewers == null ? Collections.EMPTY_LIST : viewers;
		elements = new ArrayList<CalmObject>();
		if (likes != null) {
			elements.addAll(likes);
		}
		if (likers != null) {
			elements.addAll(likers);
		}
		if (activities != null) {
			for (Activity a : activities) {
				if (!a.get(User.class).isEmpty()
						|| !a.get(User.class, Constants.ALIAS_ACTIVITY_TARGET)
								.isEmpty()
						|| !a.get(User.class, Constants.ALIAS_ACTIVITY_USER)
								.isEmpty()) {
					if (a.getActivityType() != ActivityType.LIKE) {
						elements.add(a);
					}
				}
			}
		}
		if (viewers != null) {
			elements.addAll(viewers);
		}
		for (List<? extends CalmObject> elementList : elementLists) {
			elements.addAll(elementList);
		}

		// Preparing the grid
		final Iterator<CalmObject> elementsIt = elements.iterator();
		gridElements = new ArrayList<CalmObject>();
		rowContents = new ArrayList<Integer>();
		rowOffsets = new ArrayList<Integer>();
		final long id = parentObject.getKey().getNumericId();
		final List<ItemKey> processedKeys = new ArrayList<ItemKey>();
		for (int i = 0; i < rows; i++) {
			// Computing row size
			final int size = (((int) id * (i + 1)) % columns) + 1;
			rowContents.add(size);
			// Computing row offset
			final int spaceLeft = columns - size;
			int offset = 0;
			if (spaceLeft > 0) {
				offset = ((int) id / (i + 1)) % spaceLeft;
			}
			rowOffsets.add(offset);
			// Filling structure
			for (int j = 0; j < columns; j++) {
				if (j < offset) {
					gridElements.add(null);
				} else if (j >= offset && j < offset + size) {
					if (elementsIt.hasNext()) {
						final CalmObject o = elementsIt.next();
						if (!processedKeys.contains(o.getKey())) {
							gridElements.add(o);
							processedKeys.add(o.getKey());
						}
					} else {
						gridElements.add(BLANK_FILLER);
					}
				} else {
					gridElements.add(null);
				}
			}
		}
	}

	@Override
	public CalmObject getElementAt(int row, int col) {
		return gridElements.get((row - 1) * columns + (col - 1));
	}

	private CalmObject getActivityObject(Activity activity) {
		try {
			CalmObject activityObj = activity.getUnique(User.class);
			if (activityObj == null) {
				activityObj = activity.getUnique(CalmObject.class,
						Constants.ALIAS_ACTIVITY_TARGET);
			}
			if (activityObj == null) {
				activityObj = activity.getUnique(CalmObject.class,
						Constants.ALIAS_ACTIVITY_USER);
			}
			return activityObj;
		} catch (CalException e) {
			LOGGER.error(
					"Unable to extract user from activity: " + e.getMessage(),
					e);
			return null;
		}
	}

	@Override
	public String getImageUrl(int row, int col) {
		final CalmObject obj = getElementAt(row, col);
		if (obj == BLANK_FILLER) {
			return null;
		} else if (obj instanceof Activity) {
			final CalmObject activityObj = getActivityObject((Activity) obj);
			if (activityObj != null) {
				final Media m = MediaHelper.getSingleMedia(activityObj);
				if (m != null) {
					return m.getMiniThumbUrl();
				} else {
					return null;
				}
			} else {
				return null;
			}

		} else {
			String url = MediaHelper.getSingleMediaUrl(obj);
			if (url == null) {
				if (User.CAL_TYPE.equals(obj.getKey().getType())) {
					return "/images/V2/no-photo-profile-small.png";
				} else {
					return "/images/V2/no-photo-small.png";
				}
			} else {
				return url;
			}
		}
	}

	@Override
	public String getLinkUrlAt(int row, int col) {
		final CalmObject o = getElementAt(row, col);
		if (o instanceof Activity) {
			final CalmObject activityObject = getActivityObject((Activity) o);
			return urlService.getOverviewUrl(
					DisplayHelper.getDefaultAjaxContainer(), activityObject);
		} else {
			return urlService.getOverviewUrl(
					DisplayHelper.getDefaultAjaxContainer(), o);
		}
	}

	@Override
	public String getTooltipText(int row, int col) {
		final CalmObject o = getElementAt(row, col);
		if (o instanceof Activity) {
			return activitySupport.getActivityHtmlLine((Activity) o);
		} else { // if (likes.contains(o) || likers.contains(o)) {
			final String name = DisplayHelper.getName(parent);
			final String url = urlService.getOverviewUrl(
					DisplayHelper.getDefaultAjaxContainer(), parent);
			final String objType = messageSource.getMessage(
					MESSAGE_OBJ_TYPE_PREFIX + o.getKey().getType(), null,
					locale);
			final String parentType = messageSource.getMessage(
					MESSAGE_OBJ_TYPE_PREFIX + parent.getKey().getType(), null,
					locale);
			final String likedUrl = urlService.getOverviewUrl(
					DisplayHelper.getDefaultAjaxContainer(), o);
			final String likedName = DisplayHelper.getName(o);

			String messageKey = null;
			if (likes.contains(o)) {
				messageKey = MESSAGE_LIKE;
			} else if (Event.CAL_ID.equals(parent.getKey().getType())) {
				messageKey = MESSAGE_LIKER_PREFIX + Event.CAL_ID;
			} else if (User.CAL_TYPE.equals(parent.getKey().getType())) {
				if (Event.CAL_ID.equals(o.getKey().getType())) {
					messageKey = MESSAGE_LIKE_EVENT;
				} else if (Place.CAL_TYPE.equals(o.getKey().getType())) {
					messageKey = MESSAGE_LIKE;
				}
			} else {
				messageKey = MESSAGE_LIKER_DEFAULT;
			}
			if (messageKey != null) {
				return messageSource.getMessage(messageKey, new Object[] {
						objType, likedName, name, null, parentType, null,
						likedUrl, null, url }, locale);
			} else {
				return null;
			}
		}
		// return null;
	}

	@Override
	public String getMosaicTitle() {
		if (titleMessageKey != null) {
			return messageSource.getMessage(titleMessageKey,
					new Object[] { DisplayHelper.getName(parent) }, locale);
		} else {
			return null;
		}
	}

	public void setRows(int rows) {
		this.rows = rows;
	}

	public void setColumns(int columns) {
		this.columns = columns;
	}

	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	public void setTitleMessageKey(String titleMessageKey) {
		this.titleMessageKey = titleMessageKey;
	}
}
