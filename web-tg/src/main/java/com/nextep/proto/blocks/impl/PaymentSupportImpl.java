package com.nextep.proto.blocks.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.springframework.context.MessageSource;

import com.nextep.cal.util.model.Price;
import com.nextep.cal.util.model.impl.PriceImpl;
import com.nextep.proto.blocks.PaymentSupport;
import com.nextep.proto.helpers.LocalizationHelper;
import com.nextep.proto.services.UrlService;
import com.nextep.users.model.User;

public class PaymentSupportImpl implements PaymentSupport {

	private static final String KEY_TRANSLATION_CREDITS = "promote.credits";

	private MessageSource messageSource;

	private String paymentUrl;
	private String returnUrl;
	private String errorUrl;
	private String notifyUrl;
	private String businessId;
	private String domainName;

	private Locale locale;
	private UrlService urlService;
	private User currentUser;

	@Override
	public void initialize(Locale locale, UrlService urlService,
			User currentUser) {
		this.locale = locale;
		this.urlService = urlService;
		this.currentUser = currentUser;
	}

	@Override
	public String getPaymentUrl() {
		return paymentUrl;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public List<Price> getAvailableAmounts() {
		return (List) Arrays.asList(new PriceImpl(20, "EUR"), new PriceImpl(40,
				"EUR"), new PriceImpl(60, "EUR"), new PriceImpl(80, "EUR"));
	}

	@Override
	public String getPriceLabel(Price price) {
		return price.getPrice() + " " + price.getCurrency();
	}

	@Override
	public String getPriceOptionValue(Price price) {
		return String.valueOf((int) (price.getPrice() * 100));
	}

	@Override
	public String getReturnUrl() {
		return LocalizationHelper.buildUrl(locale, domainName, returnUrl);
	}

	@Override
	public String getErrorUrl() {
		return LocalizationHelper.buildUrl(locale, domainName, errorUrl);
	}

	@Override
	public String getNotifyUrl() {
		return LocalizationHelper.buildUrl(locale, domainName, notifyUrl);
	}

	@Override
	public String getBusinessId() {
		return businessId;
	}

	@Override
	public String getBuyerId() {
		return currentUser.getKey().toString();
	}

	public void setBusinessId(String businessId) {
		this.businessId = businessId;
	}

	public void setErrorUrl(String errorUrl) {
		this.errorUrl = errorUrl;
	}

	public void setNotifyUrl(String notifyUrl) {
		this.notifyUrl = notifyUrl;
	}

	public void setPaymentUrl(String paymentUrl) {
		this.paymentUrl = paymentUrl;
	}

	public void setReturnUrl(String returnUrl) {
		this.returnUrl = returnUrl;
	}

	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}

	@Override
	public String getCreditsLabel() {
		if (currentUser != null) {
			final String credits = String.valueOf(currentUser.getCredits());
			return messageSource.getMessage(KEY_TRANSLATION_CREDITS,
					new Object[] { credits }, locale);
		} else {
			return null;
		}
	}

	@Override
	public String getLanguage() {
		return locale.getLanguage().toUpperCase();
	}

	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}
}
