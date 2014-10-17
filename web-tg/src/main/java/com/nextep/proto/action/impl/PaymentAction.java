package com.nextep.proto.action.impl;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.nextep.cal.util.services.CalPersistenceService;
import com.nextep.proto.action.base.AbstractAction;
import com.nextep.proto.blocks.CurrentUserSupport;
import com.nextep.proto.spring.ContextHolder;
import com.nextep.users.model.MutableUser;
import com.videopolis.apis.factory.ApisFactory;
import com.videopolis.apis.model.ApisRequest;
import com.videopolis.apis.service.ApiCompositeResponse;
import com.videopolis.cals.factory.ContextFactory;

import de.paymill.Paymill;
import de.paymill.model.Payment;
import de.paymill.model.Transaction;
import de.paymill.net.ApiException;
import de.paymill.service.TransactionService;

/**
 * This action is dedicated to reports from mobile devices
 * 
 * @author cfondacci
 */
public class PaymentAction extends AbstractAction {

	private static final long serialVersionUID = 4675592935061950446L;

	// Logger
	private static final Log LOGGER = LogFactory.getLog(PaymentAction.class);

	private String privateKey;
	private CurrentUserSupport currentUserSupport;
	private CalPersistenceService userService;

	private String paymentToken;
	private Integer amount;
	private String currency;

	@Override
	protected String doExecute() throws Exception {
		Paymill.setApiKey(privateKey);
		// We query current user to make sure he is still connected and valid
		final ApisRequest request = (ApisRequest) ApisFactory
				.createCompositeRequest().addCriterion(
						currentUserSupport.createApisCriterionFor(
								getNxtpUserToken(), true));
		// Executing query
		final ApiCompositeResponse response = (ApiCompositeResponse) getApiService()
				.execute(request, ContextFactory.createContext(getLocale()));

		// Getting user
		final MutableUser user = response.getUniqueElement(MutableUser.class,
				CurrentUserSupport.APIS_ALIAS_CURRENT_USER);
		checkCurrentUser(user);

		// User is checked, now we can proceed with payment
		TransactionService srvTx = Paymill.getService(TransactionService.class);
		Transaction params = new Transaction();
		params.setToken(paymentToken);
		params.setAmount(amount);
		params.setCurrency(currency);
		params.setDescription("PELMEL Guide advertising slot");
		try {
			Transaction tx = srvTx.create(params);
			final Integer amount = tx.getAmount();
			final String currency = tx.getCurrency();
			final Date date = tx.getCreatedAt();
			final Payment payment = tx.getPayment();
			String paymentId = null;
			String cardType = null;
			String cardholder = null;
			if (payment != null) {
				paymentId = payment.getId();
				cardType = payment.getCardType();
				cardholder = payment.getCardHolder();
			}
			// Important payment log in case something goes wrong after this
			// point
			LOGGER.info("Processed payment ID=" + paymentId + " of " + amount
					+ " " + currency + " with " + cardType + " for Mr '"
					+ cardholder + "' -> about to credit user " + user.getKey());

			// Crediting user
			final int currentCredits = user.getCredits();
			user.setCredits(currentCredits + (amount / 100));
			ContextHolder.toggleWrite();
			userService.saveItem(user);
			return SUCCESS;
		} catch (ApiException e) {
			LOGGER.error(
					"Payment failure TOKEN=" + paymentToken + " / AMOUNT="
							+ amount + " " + currency + " / reason = "
							+ e.getMessage(), e);
			setErrorMessage(e.getMessage());
			return error();
		}
	}

	public void setPaymentToken(String paymentToken) {
		this.paymentToken = paymentToken;
	}

	public String getPaymentToken() {
		return paymentToken;
	}

	public void setAmount(Integer price) {
		this.amount = price;
	}

	public Integer getAmount() {
		return amount;
	}

	public void setCurrency(String curreny) {
		this.currency = curreny;
	}

	public String getCurrency() {
		return currency;
	}

	public void setPrivateKey(String privateKey) {
		this.privateKey = privateKey;
	}

	public void setCurrentUserSupport(CurrentUserSupport currentUserSupport) {
		this.currentUserSupport = currentUserSupport;
	}

	public void setUserService(CalPersistenceService userService) {
		this.userService = userService;
	}
}
