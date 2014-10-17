package com.nextep.proto.action.impl;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import com.nextep.advertising.model.AdvertisingBooster;
import com.nextep.advertising.model.BoosterType;
import com.nextep.advertising.model.MutableAdvertisingBooster;
import com.nextep.cal.util.model.impl.PriceImpl;
import com.nextep.cal.util.services.CalPersistenceService;
import com.nextep.proto.action.base.AbstractAction;
import com.nextep.proto.action.model.CurrentUserAware;
import com.nextep.proto.blocks.CurrentUserSupport;
import com.nextep.proto.spring.ContextHolder;
import com.nextep.smaug.service.SearchPersistenceService;
import com.nextep.users.model.MutableUser;
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

public class SponsorshipActivationAction extends AbstractAction implements
		CurrentUserAware {

	// Static constants
	private static final long serialVersionUID = -912914106940507151L;
	private static final String TRANSLATION_KEY_CONFLICT = "sponsor.error.conflict";
	private static final String TRANSLATION_KEY_CREDITS = "sponsor.error.credits";
	private static final String STATUS_PERIOD_CONFLICT = "conflict";
	private static final String APIS_ALIAS_PAYMENT = "payment";
	private static final String APIS_ALIAS_SPONSORED_ITEM = "sponsored";

	// Injected supports & services
	private CurrentUserSupport currentUserSupport;
	private CalPersistenceService userService;
	private CalPersistenceService advertisingService;
	// private CalPersistenceService paymentService;
	private SearchPersistenceService searchService;

	// Dynamic arguments
	private String sponsoredItemKey;
	private int duration;
	private int monthCredits;
	private String transactionId;

	@Override
	protected String doExecute() throws Exception {
		// Parsing sponsored item key
		final ItemKey sponsoredKey = CalmFactory.parseKey(sponsoredItemKey);
		// Fetching current user
		final ApisRequest request = (ApisRequest) ApisFactory
				.createCompositeRequest()
				.addCriterion(
						currentUserSupport.createApisCriterionFor(
								getNxtpUserToken(), true))
				.addCriterion(
						(ApisCriterion) SearchRestriction
								.uniqueKeys(Arrays.asList(sponsoredKey))
								.aliasedBy(APIS_ALIAS_SPONSORED_ITEM)
								.with(AdvertisingBooster.class));
		// .addCriterion(SearchRestriction.forKey(Payment.CAL_TRANSACTION_ID,
		// transactionId, 1, 0).aliasedBy(alias));

		// Executing APIS request
		final ApiCompositeResponse response = (ApiCompositeResponse) getApiService()
				.execute(request, ContextFactory.createContext(getLocale()));

		// Retrieving and checking current user
		final User currentUser = response.getUniqueElement(User.class,
				CurrentUserSupport.APIS_ALIAS_CURRENT_USER);
		checkCurrentUser(currentUser);

		// Retrieving sponsored item
		final CalmObject sponsoredObject = response.getUniqueElement(
				CalmObject.class, APIS_ALIAS_SPONSORED_ITEM);

		// Checking whether we have pre-existing booster for this period
		final List<? extends AdvertisingBooster> existingBoosters = sponsoredObject
				.get(AdvertisingBooster.class);

		// Computing start and end date from provided duration
		final Calendar cal = Calendar.getInstance();
		Date myStart = cal.getTime();
		cal.add(Calendar.MONTH, duration);
		Date myEnd = cal.getTime();

		// Sorting boosters by date
		Collections.sort(existingBoosters,
				new Comparator<AdvertisingBooster>() {
					@Override
					public int compare(AdvertisingBooster o1,
							AdvertisingBooster o2) {
						return o1.getStartDate().compareTo(o2.getStartDate());
					}
				});

		// Checking if there is any existing booster which may conflict
		for (AdvertisingBooster booster : existingBoosters) {
			final long boostStart = booster.getStartDate().getTime();
			final long boostEnd = booster.getEndDate().getTime();
			if ((boostStart >= myStart.getTime() && boostStart < myEnd
					.getTime())
					|| (boostEnd >= myStart.getTime() && boostEnd < myEnd
							.getTime())) {
				// If there is a conclict, it might be for the current user
				if (booster.getPurchaserItemKey().equals(currentUser.getKey())) {
					cal.setTimeInMillis(boostEnd);
					myStart = cal.getTime();
					cal.add(Calendar.MONTH, duration);
					myEnd = cal.getTime();
				} else {
					setErrorMessage(getText(TRANSLATION_KEY_CONFLICT));
					return error(412);
				}
			}
		}

		// Checking that user has enough credits
		final int requiredCredits = duration * monthCredits;
		if (currentUser.getCredits() < requiredCredits) {
			setErrorMessage(getText(TRANSLATION_KEY_CREDITS));
			return error(412);
		} else {
			// Preparing to add booster
			MutableAdvertisingBooster booster = (MutableAdvertisingBooster) advertisingService
					.createTransientObject();
			booster.setRelatedItemKey(sponsoredKey);
			booster.setBoosterType(BoosterType.DEFAULT);
			booster.setBoostValue(monthCredits);
			booster.setStartDate(myStart);
			booster.setEndDate(myEnd);
			booster.setPrice(new PriceImpl(duration * monthCredits, "EUR"));
			booster.setPurchaserItemKey(currentUser.getKey());
			// Saving
			ContextHolder.toggleWrite();
			advertisingService.saveItem(booster);
			// Indexing
			searchService.addBooster(sponsoredObject, myEnd, monthCredits);
			// Decreasing user credits
			final int userCredits = currentUser.getCredits();
			((MutableUser) currentUser).setCredits(userCredits
					- requiredCredits);
			userService.saveItem(currentUser);
		}
		return SUCCESS;
	}

	public void setSponsoredItemKey(String sponsoredItemKey) {
		this.sponsoredItemKey = sponsoredItemKey;
	}

	public String getSponsoredItemKey() {
		return sponsoredItemKey;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public int getDuration() {
		return duration;
	}

	public void setMonthCredits(int monthCredits) {
		this.monthCredits = monthCredits;
	}

	public int getMonthCredits() {
		return monthCredits;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public String getTransactionId() {
		return transactionId;
	}

	@Override
	public CurrentUserSupport getCurrentUserSupport() {
		return currentUserSupport;
	}

	@Override
	public void setCurrentUserSupport(CurrentUserSupport currentUserSupport) {
		this.currentUserSupport = currentUserSupport;
	}

	public void setSearchService(SearchPersistenceService searchService) {
		this.searchService = searchService;
	}

	// public void setPaymentService(CalPersistenceService paymentService) {
	// this.paymentService = paymentService;
	// }

	public void setUserService(CalPersistenceService userService) {
		this.userService = userService;
	}

	public void setAdvertisingService(CalPersistenceService advertisingService) {
		this.advertisingService = advertisingService;
	}
}
