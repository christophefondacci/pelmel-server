package com.nextep.proto.blocks.impl;

import java.util.List;

import com.nextep.advertising.model.AdvertisingBanner;
import com.nextep.proto.blocks.AdBannerSupport;
import com.nextep.proto.blocks.HeaderSupport;

public class GoogleAdBannerSupportImpl implements AdBannerSupport {

	private boolean googleEnabled;

	@Override
	public void initialize(HeaderSupport headerSupport,
			List<? extends AdvertisingBanner> banners) {
	}

	@Override
	public String getSquareBannerHtml() {
		if (googleEnabled) {
			return "<script async src=\"//pagead2.googlesyndication.com/pagead/js/adsbygoogle.js\"></script>"
					+ "<!-- Bloc carré 250x250 -->"
					+ "<ins class=\"adsbygoogle\""
					+ "     style=\"display:inline-block;width:250px;height:250px\""
					+ "     data-ad-client=\"ca-pub-9894675391208591\""
					+ "     data-ad-slot=\"5365920539\"></ins>"
					+ "<script>"
					+ "(adsbygoogle = window.adsbygoogle || []).push({});"
					+ "</script>";
		} else {
			return "<div style='width:300px;height:266px;background:blue;'>&nbsp;</div>";
		}
	}

	@Override
	public String getHorizontalBannerHtml() {
		if (googleEnabled) {
			return "<script async src=\"//pagead2.googlesyndication.com/pagead/js/adsbygoogle.js\"></script>"
					+ "<!-- LeaderBoard -->"
					+ "<ins class=\"adsbygoogle\""
					+ "     style=\"display:inline-block;width:728px;height:90px\""
					+ "     data-ad-client=\"ca-pub-9894675391208591\""
					+ "     data-ad-slot=\"0754076047\"></ins>"
					+ "<script>"
					+ "(adsbygoogle = window.adsbygoogle || []).push({});"
					+ "</script>";
		} else {
			return "<div style='width:728px;height:90px;background:blue;'>&nbsp;</div>";
		}
	}

	@Override
	public String getVerticalBannerHtml() {
		if (googleEnabled) {
			return "<script async src=\"//pagead2.googlesyndication.com/pagead/js/adsbygoogle.js\"></script>"
					+ "<!-- Vertical -->"
					+ "<ins class=\"adsbygoogle\""
					+ "     style=\"display:inline-block;width:120px;height:600px\""
					+ "     data-ad-client=\"ca-pub-9894675391208591\""
					+ "     data-ad-slot=\"1119020931\"></ins>"
					+ "<script>"
					+ "(adsbygoogle = window.adsbygoogle || []).push({});"
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
