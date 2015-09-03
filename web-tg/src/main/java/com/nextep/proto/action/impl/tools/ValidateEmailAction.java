package com.nextep.proto.action.impl.tools;

import org.springframework.beans.factory.annotation.Autowired;

import com.nextep.json.model.impl.JsonStatus;
import com.nextep.proto.action.base.AbstractAction;
import com.nextep.proto.action.model.JsonProvider;
import com.nextep.proto.action.model.SimpleResultProvider;
import com.nextep.proto.services.NotificationService;
import com.nextep.proto.spring.ContextHolder;
import com.nextep.users.model.MutableUser;
import com.nextep.users.model.User;
import com.videopolis.apis.factory.ApisFactory;
import com.videopolis.apis.model.ApisRequest;
import com.videopolis.apis.service.ApiResponse;
import com.videopolis.calm.factory.CalmFactory;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.cals.factory.ContextFactory;

import net.sf.json.JSONObject;

public class ValidateEmailAction extends AbstractAction implements SimpleResultProvider, JsonProvider {

	private static final long serialVersionUID = 1L;

	@Autowired
	private NotificationService notificationService;

	private String userKey;
	private String emailToken;
	private boolean isValidated;

	@Override
	protected String doExecute() throws Exception {
		final ItemKey userItemKey = CalmFactory.parseKey(userKey);
		final ApisRequest request = ApisFactory.createRequest(User.class).uniqueKey(userItemKey.getId());
		final ApiResponse response = getApiService().execute(request, ContextFactory.createContext(getLocale()));

		final User user = (User) response.getUniqueElement();
		if (user != null) {
			ContextHolder.toggleWrite();
			if (emailToken != null) {
				isValidated = false;
				if (emailToken.equals(user.getEmailValidationToken())) {
					isValidated = true;
					((MutableUser) user).setEmailValidated(true);
					getUsersService().saveItem(user);
					return REDIRECT;
				}
			} else {

				// Sending the email
				notificationService.sendEmailValidationEmail(user);
			}
		}
		return SUCCESS;
	}

	public void setUserKey(String userKey) {
		this.userKey = userKey;
	}

	public String getUserKey() {
		return userKey;
	}

	public void setEmailToken(String emailToken) {
		this.emailToken = emailToken;
	}

	public String getEmailToken() {
		return emailToken;
	}

	@Override
	public String getJson() {
		return JSONObject.fromObject(new JsonStatus(false, "Validation email has been sent")).toString();
	}

	@Override
	public String getPageText() {
		return getText(isValidated ? "static.text.emailValidation" : "static.text.emailValidationFailed");
	}

	@Override
	public String getPageTitle() {
		return getText(isValidated ? "static.title.emailValidation" : "static.title.emailValidationFailed");
	}

}
