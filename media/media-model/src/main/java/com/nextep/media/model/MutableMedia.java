package com.nextep.media.model;

import java.util.Date;

import com.videopolis.calm.model.ItemKey;

public interface MutableMedia extends Media {

	void setTitle(String title);

	void setUploadDate(Date uploadDate);

	void setFormat(MediaFormat mediaFormat);

	void setUrl(String url);

	void setRelatedItemKey(ItemKey relatedItemKey);

	void setVideo(boolean isVideo);

	void setThumbUrl(String thumbUrl);

	void setMiniThumbUrl(String miniThumbUrl);

	void setOriginalUrl(String originalUrl);

	void setOnline(boolean online);

	void setPreferenceOrder(int prefOrder);

	void setSourceId(int sourceId);

	/**
	 * Sets the width of this media
	 * 
	 * @param width
	 *            the media's width
	 */
	void setWidth(Integer width);

	/**
	 * Sets the height of this media
	 * 
	 * @param height
	 *            the media's height
	 */
	void setHeight(Integer height);

	void setOriginalWidth(Integer width);

	void setOriginalHeight(Integer height);

	void setCropX(Integer cropX);

	void setCropY(Integer cropY);

	void setCropWidth(Integer cropWidth);

	void setCropHeight(Integer cropHeight);

	/**
	 * Assigns the {@link ItemKey} of the user who uploaded this media, only
	 * available when this media comes from user.
	 * 
	 * @param sourceUserKey
	 *            the {@link ItemKey} of the author of this media
	 */
	void setAuthorKey(ItemKey sourceUserKey);

	/**
	 * Defines the URL of the image for mobile devices.
	 * 
	 * @param mobileUrl
	 *            the mobile-oriented version of the image
	 */
	void setMobileUrl(String mobileUrl);

	/**
	 * Defines the URL of the image for mobile devices with high definition
	 * screens.
	 * 
	 * @param mobileUrl
	 *            the mobile-oriented version of the high resolution image
	 */
	void setMobileUrlHighDef(String mobileUrl);

	/**
	 * Defines the number of abuses reported on this media
	 * 
	 * @param abuseCount
	 *            number of abuses
	 */
	void setAbuseCount(int abuseCount);
}