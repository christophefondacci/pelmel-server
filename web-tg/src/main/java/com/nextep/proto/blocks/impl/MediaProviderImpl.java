package com.nextep.proto.blocks.impl;

import java.util.List;

import com.nextep.media.model.Media;
import com.nextep.proto.blocks.MediaProvider;
import com.nextep.proto.helpers.DisplayHelper;
import com.nextep.proto.helpers.MediaHelper;
import com.nextep.proto.model.Constants;
import com.nextep.proto.services.UrlService;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;

public class MediaProviderImpl implements MediaProvider {

	private UrlService urlService;

	private int overviewImageWidth;
	private int overviewImageHeight;

	private List<? extends Media> media;
	private Media currentMedia;
	private ItemKey parentKey;
	private String baseUrl;

	private boolean fitAlways = false;

	@Override
	public void initialize(ItemKey parentKey, List<? extends Media> medias) {
		initialize(parentKey, medias, null);
	}

	@Override
	public void initialize(ItemKey parentKey, List<? extends Media> medias,
			Media currentMedia) {
		this.parentKey = parentKey;
		this.media = medias;
		if (currentMedia != null) {
			this.currentMedia = currentMedia;
		} else {
			if (!medias.isEmpty()) {
				this.currentMedia = medias.iterator().next();
			}
		}
	}

	@Override
	public Media getCurrentMedia() {
		return currentMedia;
	}

	@Override
	public String getMediaMiniThumbUrl(Media m) {
		if (m != null) {
			final String relativeUrl = m.getMiniThumbUrl();
			return urlService.getMediaUrl(relativeUrl);
		} else {
			return urlService.getStaticUrl(Constants.DEFAULT_IMAGE_PROFILE_URL);
		}
	}

	@Override
	public String getMediaThumbUrl(Media m) {
		final String relativeUrl = m.getThumbUrl();
		return MediaHelper.getImageUrl(relativeUrl);
	}

	@Override
	public String getMediaUrl(Media m) {
		final String relativeUrl = m.getUrl();
		return MediaHelper.getImageUrl(relativeUrl);

	}

	@Override
	public List<? extends Media> getMedia() {
		return media;
	}

	@Override
	public String getAjaxMediaUrl(Media media) {
		if (!media.isVideo()) {
			if (media.getKey() != null) {
				return "javascript:call('/ajaxMedia.action?id="
						+ media.getKey().toString() + "','preview-wrap')";
			} else {
				return "#";
			}
		} else {
			return "javascript:play('" + media.getUrl() + "','"
					+ urlService.getMediaUrl(media.getThumbUrl()) + "')";
		}
	}

	@Override
	public String getDeletionUrl(Media media) {
		return "/deleteMedia.action?id=" + media.getKey().toString();
	}

	@Override
	public String getMoveUrl(Media media, int direction) {
		return "javascript:moveMedia('/moveMedia.action?id="
				+ media.getKey().toString() + "&parent=" + parentKey.toString()
				+ "&direction=" + direction + "');";
	}

	@Override
	public String getOverviewFitStyle(Media media) {
		final int width = media.getWidth();
		final int height = media.getHeight();
		final float stdRatio = (float) overviewImageWidth
				/ (float) overviewImageHeight;
		final float curRatio = (float) width / (float) height;
		final StringBuilder buf = new StringBuilder();

		if (curRatio >= stdRatio) {
			buf.append("width:");
			buf.append(overviewImageWidth);
			buf.append("px;");
			// Computing new height
			final float newHeight = (((float) overviewImageWidth) * (float) height)
					/ width;
			if (fitAlways) {
				buf.append("height:" + newHeight + "px;");
			}
			// Computing empty space left
			int heightSpace = overviewImageHeight - (int) newHeight;
			if (heightSpace > 2 * 35) {
				heightSpace -= 35;
			}
			buf.append("position: relative; top:");
			buf.append(heightSpace / 2);
			buf.append("px");
			// } else {
			// buf.append("height:");
			// buf.append(overviewImageHeight);
			// buf.append("px;");
		} else if (fitAlways) {
			buf.append("height:");
			buf.append(overviewImageHeight);
			buf.append("px;");
			// Computing new height
			final float newWidth = (((float) overviewImageHeight) * (float) width)
					/ height;
			if (fitAlways) {
				buf.append("width:" + newWidth + "px;");
			}
			// Computing empty space left
			int widthSpace = overviewImageWidth - (int) newWidth;
			buf.append("position: relative; left:");
			buf.append(widthSpace / 2);
			buf.append("px;");
		}
		return buf.toString();
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	public void setOverviewImageHeight(int overviewImageHeight) {
		this.overviewImageHeight = overviewImageHeight;
	}

	public void setOverviewImageWidth(int overviewImageWidth) {
		this.overviewImageWidth = overviewImageWidth;
	}

	@Override
	public String getAddMediaDialogUrl() {
		return urlService.getMediaAdditionFormUrl(
				DisplayHelper.getDefaultAjaxContainer(), parentKey);
	}

	public void setUrlService(UrlService urlService) {
		this.urlService = urlService;
	}

	@Override
	public Media getMainMedia(CalmObject o) {
		return MediaHelper.getSingleMedia(o);
	}

	@Override
	public String getPreviewFitClass(Media media) {
		return media.getOriginalWidth() > media.getOriginalHeight() ? "main-image"
				: "main-image-portrait";
	}

	public void setFitAlways(boolean fitAlways) {
		this.fitAlways = fitAlways;
	}
}
