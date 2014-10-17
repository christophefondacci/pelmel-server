package com.nextep.proto.blocks.impl;

import java.util.List;

import com.nextep.advertising.model.AdvertisingBanner;
import com.nextep.proto.blocks.AdBannerSupport;
import com.nextep.proto.blocks.HeaderSupport;

public class GoogleAdBannerSupportImpl implements AdBannerSupport {

	private HeaderSupport headerSupport;
	private boolean googleEnabled;

	@Override
	public void initialize(HeaderSupport headerSupport,
			List<? extends AdvertisingBanner> banners) {
		this.headerSupport = headerSupport;
	}

	@Override
	public String getSquareBannerHtml() {
		if (googleEnabled) {
			return "<script type='text/javascript'><!--\n"
					+ "google_ad_client = 'ca-pub-9894675391208591';"
					+ "google_ad_slot = '5365920539';"
					+ "google_ad_width = 250;"
					+ "google_ad_height = 250;\n"
					+ "//-->\n"
					+ "</script>"
					+ "<script type='text/javascript' "
					+ "src='http://pagead2.googlesyndication.com/pagead/show_ads.js'>"
					+ "</script>";
		} else {
			return "<div style='width:300px;height:266px;background:blue;'>&nbsp;</div>";
		}
	}

	@Override
	public String getHorizontalBannerHtml() {
		if (googleEnabled) {
			return "<script type='text/javascript'><!--\n"
					+ "google_ad_client = 'ca-pub-9894675391208591';"
					+ "/* LeaderBoard */"
					+ "google_ad_slot = '0754076047';"
					+ "google_ad_width = 728;"
					+ "google_ad_height = 90;\n"
					+ "//-->\n"
					+ "</script>"
					+ "<script type='text/javascript' "
					+ "src='http://pagead2.googlesyndication.com/pagead/show_ads.js'>"
					+ "</script>'";
		} else {
			return "<div style='width:728px;height:90px;background:blue;'>&nbsp;</div>";
		}
	}

	@Override
	public String getVerticalBannerHtml() {
		if (googleEnabled) {
			return "<script type='text/javascript'><!--\n"
					+ "google_ad_client = 'ca-pub-9894675391208591';"
					+ "/* Vertical */"
					+ "google_ad_slot = '1119020931';"
					+ "google_ad_width = 120;"
					+ "google_ad_height = 600;\n"
					+ "//-->\n"
					+ "</script>"
					+ "<script type='text/javascript' "
					+ "src='http://pagead2.googlesyndication.com/pagead/show_ads.js'>"
					+ "</script>";
		} else {
			return "<div style='width:160px;height:600px;background:blue;'>&nbsp;</div>";
		}
	}

	@Override
	public String getGenericBanner(String code) {
		return null;
	}

	public void setGoogleEnabled(boolean googleEnabled) {
		this.googleEnabled = googleEnabled;
	}
}
