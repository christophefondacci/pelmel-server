package com.nextep.media.model.impl;

import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.annotations.Type;

import com.nextep.media.model.Media;
import com.nextep.media.model.MediaFormat;
import com.nextep.media.model.MutableMedia;
import com.videopolis.calm.base.AbstractCalmObject;
import com.videopolis.calm.exception.CalException;
import com.videopolis.calm.factory.CalmFactory;
import com.videopolis.calm.model.ItemKey;

@Entity
@Table(name = "MEDIAS")
public class MediaImpl extends AbstractCalmObject implements Media,
		MutableMedia {

	private static final Log log = LogFactory.getLog(MediaImpl.class);
	/**
	 * 
	 */
	private static final long serialVersionUID = -1830156132512606981L;

	@Id
	@GeneratedValue
	@Column(name = "MEDIA_ID")
	private Long id;
	@Column(name = "MEDIA_TITLE")
	private String title;
	@Column(name = "UPLOAD_DATE")
	private Date uploadDate;
	@Column(name = "PREF_ORDER")
	private Integer prefOrder = 0;
	@Embedded
	@AttributeOverrides({
			@AttributeOverride(name = "width", column = @Column(name = "MEDIA_WIDTH")),
			@AttributeOverride(name = "height", column = @Column(name = "MEDIA_HEIGHT")),
			@AttributeOverride(name = "encoding", column = @Column(name = "MEDIA_FORMAT")) })
	private MediaFormat mediaFormat;

	@Column(name = "URL")
	private String url;
	@Column(name = "THUMB_URL")
	private String thumbUrl;
	@Column(name = "MINI_THUMB_URL")
	private String miniThumbUrl;
	@Column(name = "ORIGINAL_URL")
	private String originalUrl;
	@Column(name = "ITEM_KEY")
	private String relatedItemKey;
	@Column(name = "IS_VIDEO")
	@Type(type = "yes_no")
	private Boolean isVideo = false;
	@Column(name = "IS_ONLINE")
	@Type(type = "yes_no")
	private Boolean online = true;
	@Column(name = "SOURCE_ID")
	private int sourceId;
	@Column(name = "MEDIA_WIDTH")
	private Integer width;
	@Column(name = "MEDIA_HEIGHT")
	private Integer height;
	@Column(name = "ORIGINAL_WIDTH")
	private Integer originalWidth;
	@Column(name = "ORIGINAL_HEIGHT")
	private Integer originalHeight;
	@Column(name = "CROP_X")
	private Integer cropX;
	@Column(name = "CROP_Y")
	private Integer cropY;
	@Column(name = "CROP_WIDTH")
	private Integer cropWidth;
	@Column(name = "CROP_HEIGHT")
	private Integer cropHeight;
	@Column(name = "AUTHOR_ITEM_KEY")
	private String authorItemKey;
	@Column(name = "MOBILE_URL")
	private String mobileUrl;
	@Column(name = "MOBILE_URL_HIGH_DEF")
	private String mobileUrlHighDef;
	@Column(name = "ABUSE_REPORT_COUNT")
	private int abuseCount;

	@Override
	public Integer getOriginalWidth() {
		return originalWidth;
	}

	@Override
	public void setOriginalWidth(Integer originalWidth) {
		this.originalWidth = originalWidth;
	}

	@Override
	public Integer getOriginalHeight() {
		return originalHeight;
	}

	@Override
	public void setOriginalHeight(Integer originalHeight) {
		this.originalHeight = originalHeight;
	}

	@Override
	public Integer getCropX() {
		return cropX;
	}

	@Override
	public void setCropX(Integer cropX) {
		this.cropX = cropX;
	}

	@Override
	public Integer getCropY() {
		return cropY;
	}

	@Override
	public void setCropY(Integer cropY) {
		this.cropY = cropY;
	}

	@Override
	public Integer getCropWidth() {
		return cropWidth;
	}

	@Override
	public void setCropWidth(Integer cropWidth) {
		this.cropWidth = cropWidth;
	}

	@Override
	public Integer getCropHeight() {
		return cropHeight;
	}

	@Override
	public void setCropHeight(Integer cropHeight) {
		this.cropHeight = cropHeight;
	}

	public MediaImpl() {
		super(null);
	}

	public MediaImpl(ItemKey key) {
		super(key);
	}

	@Override
	public String getTitle() {
		return title;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.nextep.media.model.impl.MutableMedia#setTitle(java.lang.String)
	 */
	@Override
	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public Date getUploadDate() {
		return uploadDate;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.nextep.media.model.impl.MutableMedia#setUploadDate(java.util.Date)
	 */
	@Override
	public void setUploadDate(Date uploadDate) {
		this.uploadDate = uploadDate;
	}

	@Override
	public MediaFormat getFormat() {
		return mediaFormat;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.nextep.media.model.impl.MutableMedia#setFormat(com.nextep.media.model
	 * .MediaFormat)
	 */
	@Override
	public void setFormat(MediaFormat mediaFormat) {
		this.mediaFormat = mediaFormat;
	}

	@Override
	public String getUrl() {
		return url;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.nextep.media.model.impl.MutableMedia#setUrl(java.lang.String)
	 */
	@Override
	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public ItemKey getRelatedItemKey() {
		try {
			return CalmFactory.parseKey(relatedItemKey);
		} catch (CalException e) {
			log.error("Unable to parse media related key : " + relatedItemKey,
					e);
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.nextep.media.model.impl.MutableMedia#setRelatedItemKey(com.videopolis
	 * .calm.model.ItemKey)
	 */
	@Override
	public void setRelatedItemKey(ItemKey relatedItemKey) {
		this.relatedItemKey = relatedItemKey.toString();
	}

	@Override
	public boolean isVideo() {
		return isVideo;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.nextep.media.model.impl.MutableMedia#setVideo(boolean)
	 */
	@Override
	public void setVideo(boolean isVideo) {
		this.isVideo = isVideo;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.nextep.media.model.impl.MutableMedia#setThumbUrl(java.lang.String)
	 */
	@Override
	public void setThumbUrl(String thumbUrl) {
		this.thumbUrl = thumbUrl;
	}

	@Override
	public String getThumbUrl() {
		return thumbUrl;
	}

	@Override
	public String getMiniThumbUrl() {
		return miniThumbUrl;
	}

	@Override
	public void setMiniThumbUrl(String miniThumbUrl) {
		this.miniThumbUrl = miniThumbUrl;
	}

	@Override
	public ItemKey getKey() {
		ItemKey key = super.getKey();
		if (key == null) {
			if (id == null) {
				return null;
			}
			try {
				key = CalmFactory.createKey(CAL_TYPE, id);
			} catch (CalException e) {
				log.error("Unable to build media related item key: " + id);
				key = null;
			}
		}
		return key;
	}

	@Override
	public void setOriginalUrl(String originalUrl) {
		this.originalUrl = originalUrl;
	}

	@Override
	public String getOriginalUrl() {
		return originalUrl;
	}

	@Override
	public boolean isOnline() {
		return online;
	}

	@Override
	public void setOnline(boolean online) {
		this.online = online;
	}

	@Override
	public void setPreferenceOrder(int prefOrder) {
		this.prefOrder = prefOrder;
	}

	@Override
	public int getPreferenceOrder() {
		return prefOrder != null ? prefOrder : 0;
	}

	@Override
	public void setSourceId(int sourceId) {
		this.sourceId = sourceId;
	}

	@Override
	public int getSourceId() {
		return sourceId;
	}

	@Override
	public Integer getWidth() {
		return width;
	}

	@Override
	public Integer getHeight() {
		return height;
	}

	@Override
	public void setWidth(Integer width) {
		this.width = width;
	}

	@Override
	public void setHeight(Integer height) {
		this.height = height;
	}

	@Override
	public void setMobileUrl(String mobileUrl) {
		this.mobileUrl = mobileUrl;
	}

	@Override
	public String getMobileUrl() {
		return mobileUrl;
	}

	@Override
	public void setMobileUrlHighDef(String mobileUrlHighDef) {
		this.mobileUrlHighDef = mobileUrlHighDef;
	}

	@Override
	public String getMobileUrlHighDef() {
		return mobileUrlHighDef;
	}

	@Override
	public void setAuthorKey(ItemKey authorItemKey) {
		this.authorItemKey = authorItemKey == null ? null : authorItemKey
				.toString();
	}

	@Override
	public ItemKey getAuthorKey() {
		if (authorItemKey == null) {
			return null;
		} else {
			try {
				return CalmFactory.parseKey(authorItemKey);
			} catch (CalException e) {
				log.error(
						"Unable to parse media author key : " + authorItemKey,
						e);
				return null;
			}
		}
	}

	@Override
	public int getAbuseCount() {
		return abuseCount;
	}

	@Override
	public void setAbuseCount(int abuseCount) {
		this.abuseCount = abuseCount;
	}
}
