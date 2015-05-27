package com.nextep.proto.services.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import javax.annotation.Resource;

import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import com.nextep.advertising.model.AdvertisingBanner;
import com.nextep.advertising.model.BannerStatus;
import com.nextep.advertising.model.MutableAdvertisingBanner;
import com.nextep.cal.util.services.CalPersistenceService;
import com.nextep.media.model.Media;
import com.nextep.proto.apis.adapters.ApisBannerTargetAdapter;
import com.nextep.proto.model.Constants;
import com.nextep.proto.services.BannerDisplayService;
import com.nextep.proto.spring.ContextHolder;
import com.videopolis.apis.exception.ApisException;
import com.videopolis.apis.factory.SearchRestriction;
import com.videopolis.apis.model.ApisCriterion;
import com.videopolis.apis.model.ApisItemKeyAdapter;
import com.videopolis.apis.model.ApisRequest;
import com.videopolis.apis.service.ApiCompositeResponse;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.smaug.common.model.SearchScope;

@Service("bannerDisplayService")
public class BannerDisplayServiceImpl implements BannerDisplayService {

	private static final Log LOGGER = LogFactory
			.getLog(BannerDisplayServiceImpl.class);
	private static final String APIS_ALIAS_BANNER = "banners";
	private static final int MAX_BANNERS = 10;
	private static final double BANNER_RADIUS = 10.0f;

	private static final String URL_PAYMENT_SANDBOX = "https://sandbox.itunes.apple.com/verifyReceipt";
	private static final String URL_PAYMENT_PRODUCTION = "https://buy.itunes.apple.com/verifyReceipt";

	private static ApisItemKeyAdapter bannerTargetAdapter = new ApisBannerTargetAdapter();

	@Autowired
	@Qualifier("bannersService")
	private CalPersistenceService bannersService;

	@Resource(mappedName = "payment.production")
	private Boolean production;

	@Override
	public void addBannersRequest(ApisRequest request, double latitude,
			double longitude) throws ApisException {
		request.addCriterion((ApisCriterion) SearchRestriction
				.searchNear(AdvertisingBanner.class, SearchScope.NEARBY_BLOCK,
						latitude, longitude, BANNER_RADIUS, MAX_BANNERS, 0)
				.aliasedBy(APIS_ALIAS_BANNER)
				.addCriterion(
						SearchRestriction.adaptKey(bannerTargetAdapter)
								.aliasedBy(Constants.APIS_ALIAS_BANNER_TARGET))
				.with(Media.class));
	}

	@Override
	public AdvertisingBanner getBannerSelection(ApiCompositeResponse response) {
		return getBannerSelection(response, null);
	}

	@Override
	public AdvertisingBanner getBannerSelection(ApiCompositeResponse response,
			ItemKey currentBannerKey) {

		try {
			final List<? extends AdvertisingBanner> banners = response
					.getElements(AdvertisingBanner.class, APIS_ALIAS_BANNER);

			// Filtering non ready banners
			final List<AdvertisingBanner> availableBanners = new ArrayList<AdvertisingBanner>();
			for (AdvertisingBanner banner : banners) {
				if (banner.getStatus() == BannerStatus.READY) {
					// We add the banner to the choices if no current banner, or
					// if only one banner available or if different than the
					// current banner
					// To sum-up, it will avoid to select the specified current
					// banner if we have at least another choice
					if (currentBannerKey == null || banners.size() == 1
							|| !banner.getKey().equals(currentBannerKey)) {
						availableBanners.add(banner);
					}
				}
			}

			// Random selection
			if (!availableBanners.isEmpty()) {
				int index = (int) (Math.random() * availableBanners.size());
				return availableBanners.get(index);
			}
		} catch (ApisException e) {
			LOGGER.error("Unable to extract banners: " + e.getMessage(), e);
		}
		return null;
	}

	@Override
	@Async
	public Future<Boolean> displayBanner(AdvertisingBanner banner) {
		ContextHolder.toggleWrite();
		MutableAdvertisingBanner mutableBanner = (MutableAdvertisingBanner) banner;

		// TODO: add synchronization / merge on display count
		mutableBanner.setDisplayCount(mutableBanner.getDisplayCount() + 1);
		bannersService.saveItem(mutableBanner);

		return new AsyncResult<Boolean>(true);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> validateAppleReceipt(String receipt) {
		final String json = "{\"receipt-data\":\"" + receipt + "\"}";
		final String url = production ? URL_PAYMENT_PRODUCTION
				: URL_PAYMENT_SANDBOX;

		try {
			final URL validationUrl = new URL(url);
			final HttpURLConnection connection = (HttpURLConnection) validationUrl
					.openConnection();
			connection.setRequestMethod("POST");
			connection.setDoOutput(true);
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setRequestProperty("Accept", "application/json");

			// Posting JSON body
			final OutputStreamWriter wr = new OutputStreamWriter(
					connection.getOutputStream());
			wr.write(json);
			wr.flush();

			// Get the response
			final BufferedReader rd = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));
			String line;
			StringBuilder buf = new StringBuilder();
			while ((line = rd.readLine()) != null) {
				LOGGER.info(line);
				buf.append(line);
			}
			wr.close();
			rd.close();
			final JSONObject obj = JSONObject.fromObject(buf.toString());
			final Number status = (Number) obj.get("status");
			if (status.intValue() == 0) {
				LOGGER.info("RECEIPT success");
			} else {
				LOGGER.error("RECEIPT error");
			}
			return (Map<String, Object>) obj.get("receipt");
		} catch (IOException e) {
			LOGGER.error(
					"Unable to contact Apple to validate payment receipt: "
							+ e.getMessage(), e);
		}
		return null;
	}
}
