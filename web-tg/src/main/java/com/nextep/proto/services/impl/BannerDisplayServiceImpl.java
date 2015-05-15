package com.nextep.proto.services.impl;

import java.util.List;
import java.util.concurrent.Future;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import com.nextep.advertising.model.AdvertisingBanner;
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
import com.videopolis.smaug.common.model.SearchScope;

@Service("bannerDisplayService")
public class BannerDisplayServiceImpl implements BannerDisplayService {

	private static final Log LOGGER = LogFactory
			.getLog(BannerDisplayServiceImpl.class);
	private static final String APIS_ALIAS_BANNER = "banners";
	private static final int MAX_BANNERS = 10;
	private static final double BANNER_RADIUS = 10.0f;

	private static ApisItemKeyAdapter bannerTargetAdapter = new ApisBannerTargetAdapter();

	@Autowired
	@Qualifier("bannersService")
	private CalPersistenceService bannersService;

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
		try {
			final List<? extends AdvertisingBanner> banners = response
					.getElements(AdvertisingBanner.class, APIS_ALIAS_BANNER);

			// Random selection
			if (!banners.isEmpty()) {
				int index = (int) (Math.random() * banners.size());
				return banners.get(index);
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

}
