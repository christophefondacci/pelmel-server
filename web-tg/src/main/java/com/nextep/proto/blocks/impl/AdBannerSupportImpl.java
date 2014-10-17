package com.nextep.proto.blocks.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.nextep.advertising.model.AdvertisingBanner;
import com.nextep.advertising.model.BannerType;
import com.nextep.proto.blocks.AdBannerSupport;
import com.nextep.proto.blocks.HeaderSupport;

public class AdBannerSupportImpl implements AdBannerSupport {

	private Map<BannerType, AdvertisingBanner> bannersMap;
	private AdBannerSupport parentSupport;

	@Override
	public void initialize(HeaderSupport headerSupport,
			List<? extends AdvertisingBanner> banners) {
		// Initializing parent if any
		if (parentSupport != null) {
			parentSupport.initialize(headerSupport, banners);
		}
		// Hashing all banners by their type for fast lookup
		bannersMap = new HashMap<BannerType, AdvertisingBanner>();
		if (banners != null) {
			for (AdvertisingBanner banner : banners) {
				bannersMap.put(banner.getBannerType(), banner);
			}
		}
	}

	@Override
	public String getSquareBannerHtml() {
		final AdvertisingBanner banner = bannersMap
				.get(BannerType.SQUARE_200_200);
		if (banner != null) {
			return banner.getBannerHTMLCode();
		} else if (parentSupport != null) {
			return parentSupport.getSquareBannerHtml();
		}
		return null;
	}

	@Override
	public String getHorizontalBannerHtml() {
		final AdvertisingBanner banner = bannersMap.get(BannerType.HORIZONTAL);
		if (banner != null) {
			return banner.getBannerHTMLCode();
		} else if (parentSupport != null) {
			return parentSupport.getHorizontalBannerHtml();
		}
		return null;
	}

	@Override
	public String getVerticalBannerHtml() {
		final AdvertisingBanner banner = bannersMap.get(BannerType.VERTICAL);
		if (banner != null) {
			return banner.getBannerHTMLCode();
		} else if (parentSupport != null) {
			return parentSupport.getVerticalBannerHtml();
		}
		return null;
	}

	@Override
	public String getGenericBanner(String code) {
		if (parentSupport != null) {
			return parentSupport.getGenericBanner(code);
		}
		return null;
	}

	public void setParentSupport(AdBannerSupport parentSupport) {
		this.parentSupport = parentSupport;
	}
}
