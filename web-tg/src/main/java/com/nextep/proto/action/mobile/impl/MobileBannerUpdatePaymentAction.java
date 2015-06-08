package com.nextep.proto.action.mobile.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.nextep.advertising.model.AdvertisingBanner;
import com.nextep.advertising.model.BannerStatus;
import com.nextep.advertising.model.MutableAdvertisingBanner;
import com.nextep.cal.util.services.CalPersistenceService;
import com.nextep.json.model.impl.JsonBanner;
import com.nextep.json.model.impl.JsonStatus;
import com.nextep.proto.action.base.AbstractAction;
import com.nextep.proto.action.model.JsonProviderWithError;
import com.nextep.proto.blocks.CurrentUserSupport;
import com.nextep.proto.builders.JsonBuilder;
import com.nextep.proto.services.BannerDisplayService;
import com.nextep.proto.spring.ContextHolder;
import com.nextep.smaug.service.SearchPersistenceService;
import com.nextep.users.model.User;
import com.videopolis.apis.factory.ApisFactory;
import com.videopolis.apis.factory.SearchRestriction;
import com.videopolis.apis.model.ApisRequest;
import com.videopolis.apis.service.ApiCompositeResponse;
import com.videopolis.calm.factory.CalmFactory;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.cals.factory.ContextFactory;
import com.videopolis.smaug.common.model.SearchScope;

public class MobileBannerUpdatePaymentAction extends AbstractAction implements
		JsonProviderWithError {

	private static final Log LOGGER = LogFactory
			.getLog(MobileBannerUpdatePaymentAction.class);
	private static final long serialVersionUID = 1L;
	private static final String APIS_ALIAS_BANNER = "banner";
	private static final String JSON_TRANSACTION_ID = "transaction_id";
	private static final String JSON_PRODUCT_ID = "product_id";
	private static final String APPSTORE_PRODUCTID_1000 = "com.fgp.pelmel.banner1000";
	private static final String APPSTORE_PRODUCTID_2500 = "com.fgp.pelmel.banner2500";
	private static final String APPSTORE_PRODUCTID_6000 = "com.fgp.pelmel.banner6000";

	@Autowired
	private CurrentUserSupport currentUserSupport;
	@Autowired
	private JsonBuilder jsonBuilder;
	@Autowired
	@Qualifier("bannersService")
	private CalPersistenceService bannersService;
	@Autowired
	private SearchPersistenceService searchPersistenceService;
	@Autowired
	private BannerDisplayService bannerDisplayService;

	// Dynamic arguments
	private String bannerKey;
	private String appStoreReceipt;

	// Internal variables
	private AdvertisingBanner banner;

	@SuppressWarnings("unchecked")
	@Override
	protected String doExecute() throws Exception {
		final ItemKey bannerItemKey = CalmFactory.parseKey(bannerKey);

		// Building request
		final ApisRequest request = (ApisRequest) ApisFactory
				.createCompositeRequest()
				.addCriterion(
						currentUserSupport.createApisCriterionFor(
								getNxtpUserToken(), true))
				.addCriterion(
						SearchRestriction.uniqueKeys(
								Arrays.asList(bannerItemKey)).aliasedBy(
								APIS_ALIAS_BANNER));

		// Executing query
		final ApiCompositeResponse response = (ApiCompositeResponse) getApiService()
				.execute(request, ContextFactory.createContext(getLocale()));

		// Checking valid user
		final User user = response.getUniqueElement(User.class,
				CurrentUserSupport.APIS_ALIAS_CURRENT_USER);
		checkCurrentUser(user);

		// Getting banner
		banner = response.getUniqueElement(AdvertisingBanner.class,
				APIS_ALIAS_BANNER);
		if (banner == null) {
			setErrorMessage("Banner not found");
			return ERROR;
		}

		// Getting expected product ID

		// Validating receipt with apple and getting JSON back
		final Map<String, Object> receipt = bannerDisplayService
				.validateAppleReceipt(appStoreReceipt);
		if (receipt == null) {
			setErrorMessage("Invalid payment receipt confirmation received from App Store");
			return ERROR;
		}
		final List<Map<String, Object>> inAppPurchases = (List<Map<String, Object>>) receipt
				.get("in_app");

		// Preparing to update banner
		ContextHolder.toggleWrite();
		MutableAdvertisingBanner mutableBanner = (MutableAdvertisingBanner) banner;

		// Iterating over in-app to look for a matching product
		for (Map<String, Object> appPurchase : inAppPurchases) {
			final String transactionId = (String) appPurchase
					.get(JSON_TRANSACTION_ID);
			final String productId = (String) appPurchase.get(JSON_PRODUCT_ID);
			LOGGER.info("Iterating over purchase transaction '" + transactionId
					+ "' of product '" + productId + "'");

			// Getting target display count which has been bought
			final int targetDisplayCount = getBannerTargetDisplayCount(productId);
			if (mutableBanner.getTransactionId() == null) {
				mutableBanner.setTargetDisplayCount(targetDisplayCount);
				mutableBanner.setTransactionId(transactionId);
				mutableBanner.setStatus(BannerStatus.READY);
			} else {
				if (mutableBanner.getTransactionId().equals(transactionId)) {
					mutableBanner.setTargetDisplayCount(targetDisplayCount);
					mutableBanner.setStatus(BannerStatus.READY);
				} else {
					LOGGER.error("Banner " + mutableBanner.getKey()
							+ " has transaction ID "
							+ mutableBanner.getTransactionId()
							+ " while trying to assign transaction ID "
							+ transactionId);
					setErrorMessage("Banner '"
							+ mutableBanner.getKey()
							+ "' already has a transaction with a different identifier");
					return ERROR;
				}
			}
		}

		// Saving
		bannersService.saveItem(mutableBanner);
		searchPersistenceService.storeCalmObject(mutableBanner,
				SearchScope.CHILDREN);
		return SUCCESS;
	}

	private int getBannerTargetDisplayCount(String productId) {
		int targetDisplayCount = 0;
		if (APPSTORE_PRODUCTID_1000.equals(productId)) {
			targetDisplayCount = 1000;
		} else if (APPSTORE_PRODUCTID_2500.equals(productId)) {
			targetDisplayCount = 2500;
		} else if (APPSTORE_PRODUCTID_6000.equals(productId)) {
			targetDisplayCount = 6000;
		}
		return targetDisplayCount;
	}

	@Override
	public String getJson() {
		final JsonBanner jsonBanner = jsonBuilder.buildJsonBanner(banner, true,
				getLocale());

		return JSONObject.fromObject(jsonBanner).toString();
	}

	@Override
	public String getJsonError() {
		final JsonStatus status = new JsonStatus();
		status.setError(true);
		status.setMessage(getErrorMessage());
		return JSONObject.fromObject(status).toString();
	}

	public void setBannerKey(String bannerKey) {
		this.bannerKey = bannerKey;
	}

	public String getBannerKey() {
		return bannerKey;
	}

	public void setAppStoreReceipt(String appStoreReceipt) {
		this.appStoreReceipt = appStoreReceipt;
	}

	public String getAppStoreReceipt() {
		return appStoreReceipt;
	}
}
