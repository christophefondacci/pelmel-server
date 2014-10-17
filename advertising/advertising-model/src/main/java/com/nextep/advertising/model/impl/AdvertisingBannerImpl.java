package com.nextep.advertising.model.impl;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.nextep.advertising.model.BannerType;
import com.nextep.advertising.model.MutableAdvertisingBanner;
import com.nextep.cal.util.helpers.CalHelper;
import com.videopolis.calm.base.AbstractCalmObject;
import com.videopolis.calm.exception.CalException;
import com.videopolis.calm.factory.CalmFactory;
import com.videopolis.calm.model.ItemKey;

@Entity
@Table(name = "ADS_BANNERS")
public class AdvertisingBannerImpl extends AbstractCalmObject implements
		MutableAdvertisingBanner {

	private static final long serialVersionUID = 150602715349628112L;

	private static final Log LOGGER = LogFactory
			.getLog(AdvertisingBannerImpl.class);

	@GeneratedValue
	@Id
	@Column(name = "BANNER_ID")
	private long id;
	@Column(name = "START_VALIDITY")
	private Date startValidity;
	@Column(name = "END_VALIDITY")
	private Date endValidity;
	@Column(name = "HTML_CODE")
	private String bannerHTMLCode;
	@Column(name = "BANNER_TYPE")
	private String bannerType;
	@Column(name = "OWNER_ITEM_KEY")
	private String ownerItemKey;
	@Column(name = "DISPLAY_COUNT")
	private int displayCount;
	@Column(name = "CLICK_COUNT")
	private int clickCount;
	@Column(name = "TOP_GEO_ITEM_KEY")
	private String topGeographicItemKey;
	@Column(name = "SEARCH_TYPE")
	private String searchType;
	@Column(name = "LOCALE")
	private String locale;

	public AdvertisingBannerImpl() {
		super(null);
	}

	@Override
	public ItemKey getKey() {
		return CalHelper.getItemKeyFromId(CAL_ID, id);
	}

	@Override
	public Date getStartValidity() {
		return startValidity;
	}

	@Override
	public void setStartValidity(Date startValidity) {
		this.startValidity = startValidity;
	}

	@Override
	public Date getEndValidity() {
		return endValidity;
	}

	@Override
	public void setEndValidity(Date endValidity) {
		this.endValidity = endValidity;
	}

	@Override
	public String getBannerHTMLCode() {
		return bannerHTMLCode;
	}

	@Override
	public void setBannerHTMLCode(String bannerHTMLCode) {
		this.bannerHTMLCode = bannerHTMLCode;
	}

	@Override
	public BannerType getBannerType() {
		try {
			if (bannerType != null) {
				return BannerType.valueOf(bannerType);
			}
		} catch (IllegalArgumentException e) {
			LOGGER.error("Invalid banner type : " + bannerType);
		}
		return null;
	}

	@Override
	public void setBannerType(BannerType bannerType) {
		if (bannerType != null) {
			this.bannerType = bannerType.name();
		} else {
			this.bannerType = null;
		}
	}

	@Override
	public ItemKey getOwnerItemKey() {
		try {
			if (ownerItemKey != null) {
				return CalmFactory.parseKey(ownerItemKey);
			}
		} catch (CalException e) {
			LOGGER.error("Unable to parse banner owner item key : "
					+ ownerItemKey);
		}
		return null;
	}

	@Override
	public void setOwnerItemKey(ItemKey ownerItemKey) {
		if (ownerItemKey != null) {
			this.ownerItemKey = this.ownerItemKey.toString();
		} else {
			this.ownerItemKey = null;
		}
	}

	@Override
	public int getDisplayCount() {
		return displayCount;
	}

	@Override
	public int getClickCount() {
		return clickCount;
	}

	@Override
	public void setDisplayCount(int displayCount) {
		this.displayCount = displayCount;
	}

	@Override
	public void setClickCount(int clickCount) {
		this.clickCount = clickCount;
	}

	@Override
	public ItemKey getTopGeographicItemKey() {
		try {
			if (topGeographicItemKey != null) {
				return CalmFactory.parseKey(topGeographicItemKey);
			} else {
				return null;
			}
		} catch (CalException e) {
			LOGGER.error("Unable to parse banner geo item key : "
					+ topGeographicItemKey, e);
		}
		return null;
	}

	@Override
	public void setTopGeographicItemKey(ItemKey itemKey) {
		if (itemKey != null) {
			topGeographicItemKey = itemKey.toString();
		} else {
			topGeographicItemKey = null;
		}

	}

	@Override
	public String getSearchType() {
		return searchType;
	}

	@Override
	public void setSearchType(String searchType) {
		this.searchType = searchType;
	}

	@Override
	public String getLocale() {
		return locale;
	}

	@Override
	public void setLocale(String locale) {
		this.locale = locale;
	}
}
