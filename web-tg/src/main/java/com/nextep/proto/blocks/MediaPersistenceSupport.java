package com.nextep.proto.blocks;

import java.io.File;
import java.io.IOException;

import com.nextep.media.model.Media;
import com.nextep.media.model.MutableMedia;
import com.nextep.users.model.User;
import com.videopolis.calm.model.ItemKey;

public interface MediaPersistenceSupport {

	Media createMedia(User author, ItemKey parentItemKey, File tmpFile,
			String filename, String contentType, String title, boolean isVideo,
			Integer firstMediaPriority) throws IOException;

	void generateThumb(MutableMedia mutableMedia, File localFile);

	/**
	 * Crops the provided image of the given media with information provided
	 * 
	 * @param media
	 *            the {@link Media} from which the image has been taken
	 * @param image
	 *            the image to crop
	 * @param x
	 *            starting X-position for crop
	 * @param y
	 *            starting X-position for crop
	 * @param width
	 *            crop width
	 * @param height
	 *            crop height
	 * @return the cropped file
	 */
	File crop(MutableMedia media, File image, int x, int y, int width,
			int height);

	/**
	 * Provides the local location of the media from the media url
	 * 
	 * @param mediaUrl
	 *            the url of a media component
	 * @return the corresponding local file
	 */
	File getMediaLocalFile(String mediaUrl);

	/**
	 * Provides the media URL from a local media file
	 * 
	 * @param localMediaFile
	 *            the file to convert
	 * @return the URL for external access
	 */
	String getMediaUrl(File localMediaFile);
}
