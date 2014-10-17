package com.nextep.proto.blocks.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.MessageSource;

import com.nextep.advertising.model.AdvertisingBooster;
import com.nextep.geo.model.GeographicItem;
import com.nextep.media.model.Media;
import com.nextep.proto.blocks.SponsorshipSupport;
import com.nextep.proto.helpers.DisplayHelper;
import com.nextep.proto.helpers.MediaHelper;
import com.nextep.proto.model.Constants;
import com.nextep.proto.services.UrlService;
import com.nextep.users.model.User;
import com.videopolis.calm.exception.CalException;
import com.videopolis.calm.model.CalmObject;

/**
 * Default {@link SponsorshipSupport} implementation
 * 
 * @author cfondacci
 */
public class SponsorshipSupportImpl implements SponsorshipSupport {

	// Constants declaration
	private static final Log LOGGER = LogFactory
			.getLog(SponsorshipSupportImpl.class);
	private static final String KEY_TRANSLATION_CREDITS = "promote.credits";
	private static final String KEY_TRANSLATION_SPONSOR = "sponsor.sentence";
	private static final String KEY_TRANSLATION_DURATION = "sponsor.duration";
	private static final String KEY_TRANSLATION_MONTHCREDITS = "sponsor.monthCredits";

	// Injected services
	private MessageSource messageSource;
	private int monthCredits = 20;

	// Internal variables
	private Locale locale;
	private UrlService urlService;
	private CalmObject sponsoredObject;
	private User currentUser;
	private String transactionId;

	@Override
	public void initialize(Locale locale, UrlService urlService,
			CalmObject sponsoredObject, User currentUser) {
		this.locale = locale;
		this.urlService = urlService;
		this.sponsoredObject = sponsoredObject;
		this.currentUser = currentUser;
		transactionId = currentUser.getKey().toString() + "."
				+ System.currentTimeMillis();
	}

	@Override
	public CalmObject getObject() {
		return sponsoredObject;
	}

	@Override
	public boolean canSponsor() {
		return currentUser.getCredits() >= monthCredits;
	}

	@Override
	public String getSponsorshipLabel() {
		return messageSource
				.getMessage(
						KEY_TRANSLATION_SPONSOR,
						new Object[] { DisplayHelper.getName(sponsoredObject) },
						locale);
	}

	@Override
	public String getCreditsLabel() {
		return messageSource.getMessage(KEY_TRANSLATION_CREDITS,
				new Object[] { String.valueOf(currentUser.getCredits()) },
				locale);
	}

	@Override
	public List<Integer> getAvailableSponsorshipDuration() {
		final int maxDuration = currentUser.getCredits() / monthCredits;
		final List<Integer> availableDurations = new ArrayList<Integer>();
		for (int i = 1; i <= maxDuration; i++) {
			availableDurations.add(i);
		}
		return availableDurations;
	}

	@Override
	public String getDurationLabel(int duration) {
		return messageSource.getMessage(KEY_TRANSLATION_DURATION,
				new Object[] { String.valueOf(duration) }, locale);
	}

	@Override
	public String getValidationUrl() {
		return urlService.getSponsorshipActivationUrl();
	}

	@Override
	public List<GeographicItem> getSampleItems() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getSampleItemPriceLabel(GeographicItem item) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Integer> getAvailableMonthCredits() {
		final int maxMonthPrice = currentUser.getCredits() / monthCredits;
		final List<Integer> monthCredits = new ArrayList<Integer>();
		for (int i = 1; i <= maxMonthPrice; i++) {
			monthCredits.add(i * this.monthCredits);
		}
		return monthCredits;
	}

	@Override
	public String getMonthCreditsLabel(int credits) {
		return messageSource.getMessage(KEY_TRANSLATION_MONTHCREDITS,
				new Object[] { String.valueOf(credits) }, locale);
	}

	@Override
	public String getTransactionId() {
		return transactionId;
	}

	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	public void setMonthCredits(int monthCredits) {
		this.monthCredits = monthCredits;
	}

	@Override
	public List<CalmObject> getSponsoredElements() {
		final List<? extends AdvertisingBooster> boosters = currentUser
				.get(AdvertisingBooster.class);
		final List<CalmObject> sponsoredElements = new ArrayList<CalmObject>();
		final long currentTime = System.currentTimeMillis();
		for (AdvertisingBooster booster : boosters) {
			if (booster.getStartDate().getTime() <= currentTime
					&& booster.getEndDate().getTime() > currentTime) {
				try {
					final CalmObject sponsoredObject = booster.getUnique(
							CalmObject.class,
							Constants.APIS_ALIAS_ADVERTISING_ITEM);
					if (sponsoredObject != null) {
						sponsoredElements.add(sponsoredObject);
					}
				} catch (CalException e) {
					LOGGER.error(
							"Unable to extract item from AdvertisingBooster "
									+ (booster == null ? "null" : booster
											.getKey().toString()) + " : "
									+ e.getMessage(), e);
				}
			}
		}

		return sponsoredElements;
	}

	@Override
	public String getSponsoredElementName(CalmObject object) {
		return DisplayHelper.getName(object);
	}

	@Override
	public String getSponsoredElementUrl(CalmObject object) {
		return urlService.getOverviewUrl(
				DisplayHelper.getDefaultAjaxContainer(), object);
	}

	@Override
	public String getSponsoredElementIcon(CalmObject object) {
		final Media m = MediaHelper.getSingleMedia(object);
		if (m != null) {
			return m.getThumbUrl();
		}
		return "/images/V2/no-photo.png";
	}
}
