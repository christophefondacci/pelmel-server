package com.nextep.media.model;

import java.util.Date;

import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;

public interface Media extends CalmObject {

	String CAL_TYPE = "MDIA";

	/**
	 * Provides the title associated with this media
	 * 
	 * @return the media title
	 */
	String getTitle();

	/**
	 * The date when this media has been uploaded
	 * 
	 * @return
	 */
	Date getUploadDate();

	/**
	 * The media format
	 * 
	 * @return the {@link MediaFormat}
	 */
	MediaFormat getFormat();

	/**
	 * The URL of this media
	 * 
	 * @return the media's URL
	 */
	String getUrl();

	/**
	 * The URL of the thumb of this media
	 * 
	 * @return the media thumb URL
	 */
	String getThumbUrl();

	/**
	 * The URL of the mini-thumb (icon) of this media
	 * 
	 * @return the media mini-thumb URL
	 */
	String getMiniThumbUrl();

	/**
	 * Points to the original, unmodified image that the user submitted
	 * 
	 * @return the URL to the original uploaded file, untouched
	 */
	String getOriginalUrl();

	/**
	 * The unique key of the item to which this media is associated
	 * 
	 * @return the {@link ItemKey} of the element which has this media
	 */
	ItemKey getRelatedItemKey();

	/**
	 * Whether or not this media is a video
	 * 
	 * @return <code>true</code> if this media is a video, else
	 *         <code>false</code>
	 */
	boolean isVideo();

	/**
	 * Informs whether this media is online or not. This flag will generally
	 * always be <code>true</code> since offline media will almost always be
	 * excluded in database queries. But it could be set to false to de-activate
	 * a media.
	 * 
	 * @return <code>true</code> if the media is online, else <code>false</code>
	 */
	boolean isOnline();

	/**
	 * An integer used to sort media of a same element. The absolute value
	 * should not be consider solely, but should always be used
	 * "compared to others" as increments could change without notice.
	 * 
	 * @return the preference order.
	 */
	int getPreferenceOrder();

	/**
	 * Provides the ID of the source for this media
	 * 
	 * @return the media's source identifier
	 */
	int getSourceId();

	/**
	 * Provides the width of this media
	 * 
	 * @return the media width
	 */
	Integer getWidth();

	/**
	 * Provides the height of this media
	 * 
	 * @return the media height
	 */
	Integer getHeight();

	Integer getOriginalWidth();

	Integer getOriginalHeight();

	Integer getCropX();

	Integer getCropY();

	Integer getCropWidth();

	Integer getCropHeight();

	/**
	 * Provides the {@link ItemKey} of the author of this media. This
	 * information is only available when the media has been uploaded by a user.
	 * 
	 * @return the author's {@link ItemKey}
	 */
	ItemKey getAuthorKey();

	/**
	 * Provides the URL of the image optimized for mobile devices.
	 * 
	 * @return URL of mobile-optimized image
	 */
	String getMobileUrl();

	/**
	 * Provides the URL of the image optimized for high-definitino mobile
	 * devices
	 * 
	 * @return URL of mobile-optimized, high definition image
	 */
	String getMobileUrlHighDef();

	/**
	 * Number of times an abuse was reported on this media
	 * 
	 * @return the number of abuses
	 */
	int getAbuseCount();
}
