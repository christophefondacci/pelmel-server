package com.nextep.proto.blocks.impl;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.nextep.events.model.Event;
import com.nextep.geo.model.GeographicItem;
import com.nextep.media.model.Media;
import com.nextep.proto.blocks.MediaProvider;
import com.nextep.proto.helpers.MediaHelper;
import com.videopolis.calm.exception.CalException;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;

public class MediaEventsProviderImpl implements MediaProvider {
	private static final Log LOGGER = LogFactory
			.getLog(MediaEventsProviderImpl.class);
	private MediaProvider baseMediaProvider;

	@Override
	public void initialize(ItemKey parentKey, List<? extends Media> medias) {
		baseMediaProvider.initialize(parentKey, medias);
	}

	@Override
	public void initialize(ItemKey parentKey, List<? extends Media> medias,
			Media currentMedia) {
		baseMediaProvider.initialize(parentKey, medias, currentMedia);
	}

	@Override
	public Media getCurrentMedia() {
		return baseMediaProvider.getCurrentMedia();
	}

	@Override
	public List<? extends Media> getMedia() {
		return baseMediaProvider.getMedia();
	}

	@Override
	public String getMediaThumbUrl(Media m) {
		return baseMediaProvider.getMediaThumbUrl(m);
	}

	@Override
	public String getMediaUrl(Media m) {
		return baseMediaProvider.getMediaUrl(m);
	}

	@Override
	public String getMediaMiniThumbUrl(Media m) {
		return baseMediaProvider.getMediaMiniThumbUrl(m);
	}

	@Override
	public String getAjaxMediaUrl(Media media) {
		return baseMediaProvider.getAjaxMediaUrl(media);
	}

	@Override
	public String getDeletionUrl(Media media) {
		return baseMediaProvider.getDeletionUrl(media);
	}

	@Override
	public String getMoveUrl(Media media, int direction) {
		return baseMediaProvider.getMoveUrl(media, direction);
	}

	@Override
	public String getOverviewFitStyle(Media media) {
		return baseMediaProvider.getOverviewFitStyle(media);
	}

	@Override
	public String getAddMediaDialogUrl() {
		return baseMediaProvider.getAddMediaDialogUrl();
	}

	@Override
	public Media getMainMedia(CalmObject o) {
		final Media m = MediaHelper.getSingleMedia(o);
		if (m == null) {
			final Event e = (Event) o;
			try {
				final GeographicItem eventLocation = e
						.getUnique(GeographicItem.class);
				if (eventLocation != null) {
					return MediaHelper.getSingleMedia(eventLocation);
				}
			} catch (CalException ex) {
				LOGGER.error("Unable to extract event localization for event "
						+ e.getKey() + " : " + ex.getMessage(), ex);
			}
		}
		return m;
	}

	@Override
	public String getPreviewFitClass(Media media) {
		return null;
	}

	public void setBaseMediaProvider(MediaProvider baseMediaProvider) {
		this.baseMediaProvider = baseMediaProvider;
	}
}
