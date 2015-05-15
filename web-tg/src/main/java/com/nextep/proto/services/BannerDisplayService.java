package com.nextep.proto.services;

import java.util.concurrent.Future;

import com.nextep.advertising.model.AdvertisingBanner;
import com.videopolis.apis.exception.ApisException;
import com.videopolis.apis.model.ApisRequest;
import com.videopolis.apis.service.ApiCompositeResponse;
import com.videopolis.apis.service.ApiResponse;

/**
 * This service manages displays of banners.<br>
 * A typical use case is to first call
 * {@link BannerDisplayService#addBannersRequest(ApisRequest, double, double)},
 * then execute the APIS query, then call
 * {@link BannerDisplayService#getBannerSelection(ApiCompositeResponse)} on the
 * APIS response to get the banner to display and to return in the JSON, and
 * finally call asynchronous
 * {@link BannerDisplayService#displayBanner(AdvertisingBanner)}
 * 
 * @author cfondacci
 *
 */
public interface BannerDisplayService {

	/**
	 * Adds the request that will fetch banners to the given APIS request
	 * 
	 * @param request
	 *            the {@link ApisRequest} that will be executed
	 * @param latitude
	 *            the current user latitude
	 * @param longitude
	 *            the curren user longitude
	 */
	void addBannersRequest(ApisRequest request, double latitude,
			double longitude) throws ApisException;

	/**
	 * Extracts the banner from the {@link ApiResponse}
	 * 
	 * @param response
	 *            the {@link ApiCompositeResponse} of a request on which
	 *            {@link BannerDisplayService#addBannersRequest(ApisRequest, double, double)}
	 *            has been called
	 * @return the banner to display
	 */
	AdvertisingBanner getBannerSelection(ApiCompositeResponse response);

	/**
	 * Handles update of banner information to reflect a display of this banner
	 * 
	 * @param banner
	 *            the {@link AdvertisingBanner} that has been displayed
	 */
	Future<Boolean> displayBanner(AdvertisingBanner banner);

}
