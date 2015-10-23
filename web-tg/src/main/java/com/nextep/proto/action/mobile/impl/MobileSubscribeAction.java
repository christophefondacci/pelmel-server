package com.nextep.proto.action.mobile.impl;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.nextep.advertising.model.MutableSubscription;
import com.nextep.advertising.model.Subscription;
import com.nextep.advertising.model.SubscriptionType;
import com.nextep.advertising.model.impl.AdvertisingRequestTypes;
import com.nextep.cal.util.model.impl.PriceImpl;
import com.nextep.cal.util.services.CalPersistenceService;
import com.nextep.geo.model.Place;
import com.nextep.json.model.impl.JsonStatus;
import com.nextep.proto.action.base.AbstractAction;
import com.nextep.proto.action.model.JsonProviderWithError;
import com.nextep.proto.blocks.CurrentUserSupport;
import com.nextep.proto.model.Constants;
import com.nextep.proto.services.BannerDisplayService;
import com.nextep.proto.services.LocalizationService;
import com.nextep.proto.services.ViewManagementService;
import com.nextep.proto.spring.ContextHolder;
import com.nextep.users.model.User;
import com.videopolis.apis.factory.ApisFactory;
import com.videopolis.apis.factory.SearchRestriction;
import com.videopolis.apis.model.ApisCriterion;
import com.videopolis.apis.model.ApisRequest;
import com.videopolis.apis.service.ApiCompositeResponse;
import com.videopolis.calm.factory.CalmFactory;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.cals.factory.ContextFactory;
import com.videopolis.smaug.common.model.SearchScope;

import net.sf.json.JSONObject;

public class MobileSubscribeAction extends AbstractAction implements JsonProviderWithError {

	private static final long serialVersionUID = 1L;
	private static final String APIS_ALIAS_SUBSCRIBED_ITEM = "subscribed";
	private static final Log LOGGER = LogFactory.getLog("PAYMENT");

	@Autowired
	private CurrentUserSupport currentUserSupport;
	@Autowired
	private BannerDisplayService bannerDisplayService;
	@Autowired
	private LocalizationService localizationService;
	@Autowired
	@Qualifier("advertisingService")
	private CalPersistenceService advertisingService;
	@Autowired
	private ViewManagementService viewManagementService;

	@Resource(mappedName = "subscription.referrerMaxDistance")
	private Double referrerMaxDistance;
	@Resource(mappedName = "subscription.referrerDays")
	private Integer referrerDays;

	// Dynamic arguments
	private String subscribedKey;
	private String appStoreReceipt;
	private String transactionId;
	private Double lat, lng;

	// Internal vars
	private JsonStatus status = new JsonStatus();

	@Override
	protected String doExecute() throws Exception {

		// Fetching user (with any subscription) and subscribed item with
		// current subscriptions
		final ApisRequest request = (ApisRequest) ApisFactory.createCompositeRequest()
				.addCriterion((ApisCriterion) currentUserSupport.createApisCriterionFor(getNxtpUserToken(), true)
						.with(Subscription.class, AdvertisingRequestTypes.USER_SUBSCRIPTIONS));
		// If localization is provided, we fetch the nearest place
		if (lat != null && lng != null) {
			request.addCriterion(SearchRestriction
					.searchNear(Place.class, SearchScope.NEARBY_BLOCK, lat, lng, referrerMaxDistance, 5, 0)
					.aliasedBy(Constants.APIS_ALIAS_NEARBY_PLACES));
		}
		// If subscribed item key is provided then we fetch it
		ItemKey subscribedItemKey = null;
		if (subscribedKey != null) {
			// Parsing subscribed item key
			subscribedItemKey = CalmFactory.parseKey(subscribedKey);
			request.addCriterion((ApisCriterion) SearchRestriction.uniqueKeys(Arrays.asList(subscribedItemKey))
					.aliasedBy(APIS_ALIAS_SUBSCRIBED_ITEM).with(Subscription.class));
		}

		final ApiCompositeResponse response = (ApiCompositeResponse) getApiService().execute(request,
				ContextFactory.createContext(getLocale()));

		// Checking user
		final User user = response.getUniqueElement(User.class, CurrentUserSupport.APIS_ALIAS_CURRENT_USER);
		checkCurrentUser(user);

		// Getting our item
		final CalmObject object = response.getUniqueElement(CalmObject.class, APIS_ALIAS_SUBSCRIBED_ITEM);

		// Validating receipt
		LOGGER.info("Validating Apple receipt, looking for transaction " + transactionId);
		final Map<String, Object> receiptData = bannerDisplayService.validateAppleReceipt(appStoreReceipt);
		if (receiptData != null) {
			LOGGER.info("Apple receipt validated, looking for transaction " + transactionId);
			final List<Map<String, Object>> inAppPurchases = (List<Map<String, Object>>) receiptData.get("in_app");
			boolean transactionFound = false;
			for (Map<String, Object> inAppPurchase : inAppPurchases) {
				final String originalTransactionId = (String) inAppPurchase.get("original_transaction_id");
				final String transactionId = (String) inAppPurchase.get("transaction_id");
				final String startTimeStr = (String) inAppPurchase.get("original_purchase_date_ms");
				final String expiresTimeStr = (String) inAppPurchase.get("expires_date_ms");

				if (transactionId.equals(this.transactionId)) {
					transactionFound = true;
					LOGGER.info("Processing transaction " + transactionId + " from Apple Receipt");
					if (expiresTimeStr != null) {
						long startTime = Long.valueOf(startTimeStr);
						long expiresTime = Long.valueOf(expiresTimeStr);
						final Date startDate = new Date(startTime);
						final Date expirationDate = new Date(expiresTime);

						// Checking that there is no other subscription valid
						// for
						// either
						// another user or same
						// transaction ID, in which case we would do nothing
						if (object != null) {
							final List<? extends Subscription> subscriptions = object.get(Subscription.class);
							for (Subscription subscription : subscriptions) {
								if (subscription.getEndDate().getTime() > System.currentTimeMillis()
										&& !subscription.getPurchaserItemKey().equals(user.getKey())) {
									status.setError(true);
									status.setMessage("A subscription is already running for place '" + subscribedKey
											+ "' by user '" + subscription.getPurchaserItemKey() + "' until "
											+ subscription.getEndDate());
									return ERROR;
								}
								// Already processed?
								if (subscription.getTransactionId().equals(transactionId)) {
									return SUCCESS;
								}
							}
						}
						// If object is not provided, we browse all past user
						// subscription to find a matching
						// original transaction ID
						final List<? extends Subscription> subscriptions = user.get(Subscription.class);
						Subscription firstSubscription = null;
						Subscription formerSubscription = null;
						Subscription matchingSubscription = null;
						for (Subscription subscription : subscriptions) {
							if (subscription.getOriginalTransactionId() != null
									&& subscription.getOriginalTransactionId().equals(originalTransactionId)) {
								formerSubscription = subscription;
								if (firstSubscription == null) {
									firstSubscription = subscription;
								}
							}
							if (subscription.getTransactionId() != null
									&& subscription.getTransactionId().equals(transactionId)) {
								matchingSubscription = subscription;
							}
						}
						if (subscribedItemKey == null) {
							if (formerSubscription != null) {
								subscribedItemKey = formerSubscription.getRelatedItemKey();
							} else {
								status.setError(true);
								status.setMessage(
										"Unable to find the item to renew subscription for, original transaction ID '"
												+ originalTransactionId + "' not found");
								return ERROR;
							}
						}

						// Here we should be able to create our subscription
						// cause
						// there
						// is no conflict and we should have the item key to
						// renew
						if (matchingSubscription == null) {
							ContextHolder.toggleWrite();
							final MutableSubscription subscription = (MutableSubscription) advertisingService
									.createTransientObject();
							subscription.setStartDate(startDate);
							subscription.setEndDate(expirationDate);
							subscription.setRelatedItemKey(subscribedItemKey);
							subscription.setOriginalTransactionId(originalTransactionId);
							subscription.setPrice(new PriceImpl(20, "USD")); // Only
																				// here
																				// for
																				// ad
																				// boosting
																				// compatibility
																				// with
																				// legacy
																				// code
							subscription.setTransactionId(transactionId);
							subscription.setPurchaserItemKey(user.getKey());
							subscription.setSubscriptionType(SubscriptionType.DEFAULT);

							// Processing referrer
							if (user.getKey().equals(subscribedItemKey)) {
								LOGGER.info("User '" + user.getKey() + " is subscribed user!");
								// For a subscription renewal, the referrer is
								// the previous referrer is less than
								// 'referrerDays' months
								boolean referrerAssigned = false;
								if (formerSubscription != null) {
									if (System.currentTimeMillis()
											- firstSubscription.getStartDate().getTime() < referrerDays * 24 * 60 * 60
													* 1000) {
										LOGGER.info("User '" + user.getKey() + " subscription has been assigned to '"
												+ formerSubscription.getReferrerItemKey() + "' forwarded from '"
												+ firstSubscription.getKey() + "' !");
										referrerAssigned = true;
										subscription.setReferrerItemKey(formerSubscription.getReferrerItemKey());
									}
								}
								if (!referrerAssigned) {
									final ItemKey checkedInPlace = localizationService.getCheckedInPlaceKey(user);
									if (checkedInPlace != null) {
										subscription.setReferrerItemKey(checkedInPlace);
									} else {
										final List<? extends Place> nearbyPlaces = response.getElements(Place.class,
												Constants.APIS_ALIAS_NEARBY_PLACES);
										if (!nearbyPlaces.isEmpty()) {
											final Place nearestPlace = nearbyPlaces.iterator().next();
											subscription.setReferrerItemKey(nearestPlace.getKey());
										}
									}
								}
							} else {
								LOGGER.warn(
										"User '" + user.getKey() + " is NOT subscribed user '" + subscribedKey + "'!");
							}
							advertisingService.saveItem(subscription);

							// If we have a referrer we log the statistic for
							// reporting
							if (subscription.getReferrerItemKey() != null) {
								viewManagementService.logViewCountByKey(subscription.getReferrerItemKey(), user,
										Constants.VIEW_STAT_SUBSCRIPTION_REFERRER);
							}
						}
					} else {
						LOGGER.warn("No expiration time found, skipping (receipt transactionId" + transactionId);
					}
				} else {
					LOGGER.info("Skipping transaction " + transactionId + " differing from " + this.transactionId);
				}
			}
			if (!transactionFound) {
				LOGGER.warn("NOT FOUND transaction " + transactionId + " in Apple Receipt:\n" + receiptData);
			}
			return subscribedItemKey.getType().equals(Place.CAL_TYPE) ? "redirectPlace" : "redirectUser";
		}

		status.setError(true);
		status.setMessage("Invalid App Store receipt");
		return ERROR;
	}

	@Override
	public String getJson() {
		status.setError(false);
		status.setMessage("Payment OK");
		return JSONObject.fromObject(status).toString();
	}

	@Override
	public String getJsonError() {
		if (status == null) {
			status = new JsonStatus();
			status.setError(true);
			status.setMessage("Unknown error while subscribing to '" + subscribedKey + "'");
		}
		return JSONObject.fromObject(status).toString();
	}

	public void setSubscribedKey(String subscribedKey) {
		this.subscribedKey = subscribedKey;
	}

	public String getSubscribedKey() {
		return subscribedKey;
	}

	public void setAppStoreReceipt(String appStoreReceipt) {
		this.appStoreReceipt = appStoreReceipt;
	}

	public String getAppStoreReceipt() {
		return appStoreReceipt;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public void setLng(double lng) {
		this.lng = lng;
	}

	public double getLat() {
		return this.lat == null ? 0 : this.lat.doubleValue();
	}

	public double getLng() {
		return this.lng == null ? 0 : this.lng.doubleValue();
	}
}
