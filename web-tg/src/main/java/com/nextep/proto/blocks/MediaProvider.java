package com.nextep.proto.blocks;

import java.util.List;

import com.nextep.media.model.Media;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;

public interface MediaProvider {

	void initialize(ItemKey parentKey, List<? extends Media> medias);

	void initialize(ItemKey parentKey, List<? extends Media> medias,
			Media currentMedia);

	/**
	 * Provides the media currently being display, if available
	 * 
	 * @return the currently displayed {@link Media} or <code>null</code> when
	 *         there is no currently displayed media
	 */
	Media getCurrentMedia();

	/**
	 * Provides the list of media for this provider
	 * 
	 * @return the list of {@link Media}
	 */
	List<? extends Media> getMedia();

	/**
	 * Provides the URL of the thumb for the specified media
	 * 
	 * @param m
	 *            the {@link Media} to get a thumb for
	 * @return the URL of the thumbnail image
	 */
	String getMediaThumbUrl(Media m);

	/**
	 * Provides the URL of the given media
	 * 
	 * @param m
	 *            the {@link Media} to get the URL for
	 * @return the URL of the given media
	 */
	String getMediaUrl(Media m);

	/**
	 * 
	 * Provides the URL of the mini thumb of the given media
	 * 
	 * @param m
	 *            the {@link Media} to get the mini thumb for
	 * @return the URL of the mini thumbnail image
	 */
	String getMediaMiniThumbUrl(Media m);

	/**
	 * Provides the AJAX URL for displaying the media in the current context
	 * 
	 * @param media
	 *            the media to display
	 * @return the AJAX URL that displays this media in the current context
	 */
	String getAjaxMediaUrl(Media media);

	/**
	 * Provides the URL of the action which deletes the given media
	 * 
	 * @param media
	 *            the {@link Media} to be deleted
	 * @return the URL of the delete action for this media
	 */
	String getDeletionUrl(Media media);

	/**
	 * Provides the URL of the move action which changes the priority (up or
	 * down) of the given media in the list of existing online media for the
	 * same item
	 * 
	 * @param media
	 *            the {@link Media} to move
	 * @param direction
	 *            the direction : 1 or -1 to move down or up respectively
	 * @return the URL of the corresponding move action
	 */
	String getMoveUrl(Media media, int direction);

	/**
	 * Provides the CSS style that will fit this image to the overview display.
	 * Typically, this method will fit the vertical edge if media has its height
	 * superior to its width, and will otherwise fir the horizontal edge
	 * 
	 * @param media
	 *            the {@link Media} to fit.
	 * @return the CSS style for
	 */
	String getOverviewFitStyle(Media media);

	/**
	 * Provides the URL of the dialog that can offer the user to upload new
	 * photos
	 * 
	 * @return the corresponding URL
	 */
	String getAddMediaDialogUrl();

	/**
	 * Provides the main media of the given CAL-model object
	 * 
	 * @param o
	 *            the object to get a media for
	 * @return the main media of this object or <code>null</code> if none
	 *         available
	 */
	Media getMainMedia(CalmObject o);

	String getPreviewFitClass(Media media);
}
