package com.nextep.proto.helpers;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.nextep.geo.model.Place;
import com.nextep.media.model.Media;
import com.nextep.proto.model.PlaceType;
import com.nextep.users.model.User;
import com.videopolis.calm.model.CalmObject;

/**
 * A helper for generic and global media operations
 * 
 * @author cfondacci
 * 
 */
public final class MediaHelper {

	private static final Log LOGGER = LogFactory.getLog(MediaHelper.class);
	private static String domainName;
	private static String mediaBaseUrl;
	private static String staticBaseUrl;

	/**
	 * No visible constructor, this is a static helper bean
	 */
	private MediaHelper() {
	}

	/**
	 * Provides the URL of the most relevant media for the provided object.
	 * 
	 * @param object
	 *            the {@link CalmObject} to extract {@link Media} URL from
	 * @return the URL of the corresponding media or <code>null</code> when no
	 *         media could be found
	 */
	public static String getSingleMediaUrl(CalmObject object) {
		final Media m = getSingleMedia(object);
		if (m != null) {
			return mediaBaseUrl + m.getThumbUrl();
		} else {
			return getNoThumbUrl(object);
		}
	}

	/**
	 * Provides the most relevant media for the provided object.
	 * 
	 * @param object
	 *            the {@link CalmObject} to extract {@link Media} from
	 * @return the corresponding media or <code>null</code> when no media could
	 *         be found
	 */
	public static Media getSingleMedia(CalmObject object) {
		final List<? extends Media> media = object.get(Media.class);
		if (media != null && !media.isEmpty()) {
			Media m = media.iterator().next();
			return m;
		}
		return null;
	}

	public static String getImageUrl(String relativeUrl) {
		if (!relativeUrl.toLowerCase().startsWith("http://")) {
			return mediaBaseUrl + relativeUrl;
		} else {
			return relativeUrl;
		}
	}

	public void setDomainName(String domainName) {
		MediaHelper.domainName = domainName;
	}

	public void setStaticBaseUrl(String staticBaseUrl) {
		MediaHelper.staticBaseUrl = staticBaseUrl;
	}

	public void setMediaBaseUrl(String mediaBaseUrl) {
		MediaHelper.mediaBaseUrl = mediaBaseUrl;
	}

	/**
	 * @deprecated please use the parametized method instead
	 * @return
	 */
	@Deprecated
	public static String getNoThumbUrl() {
		return staticBaseUrl + "/images/V2/no-photo.png";
	}

	/**
	 * Provides a "no photo" thumb image URL for the given object
	 * 
	 * @param obj
	 *            the {@link CalmObject} to provide a "no photo" image for
	 * @return the URL of a photo to display
	 */
	public static String getNoThumbUrl(CalmObject obj) {
		if (obj instanceof User) {
			return getNoThumbUserUrl();
		} else if (obj instanceof Place) {
			// TODO: Handle specific "no image" here for global compatibility
			final Place p = (Place) obj;
			try {
				final PlaceType type = PlaceType.valueOf(p.getPlaceType());
				switch (type) {
				case bar:
				case club:
				case sauna:
				default:
					break;
				}
			} catch (RuntimeException e) {
				LOGGER.warn("Invalid place type: '" + p.getPlaceType()
						+ "' for place " + p.getKey());
			}
		}
		return staticBaseUrl + "/images/V2/no-photo.png";
	}

	public static String getNoThumbUserUrl() {
		return staticBaseUrl + "/images/V2/no-photo-user.png";
	}

	/**
	 * @deprecated please use {@link MediaHelper#getN}
	 * @return
	 */
	@Deprecated
	public static String getNoMiniThumbUrl() {
		return staticBaseUrl + "/images/V2/no-photo-small.png";
	}

	public static String getNoMiniThumbUrl(CalmObject obj) {
		if (obj instanceof User) {
			return getNoMiniThumbUserUrl();
		} else if (obj instanceof Place) {

		}
		return staticBaseUrl + "/images/V2/no-photo-small.png";
	}

	public static String getNoMiniThumbUserUrl() {
		return staticBaseUrl + "/images/V2/no-photo-profile-small.png";
	}

}
