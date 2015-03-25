package com.nextep.proto.action.impl.tools;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;

import com.nextep.proto.action.base.AbstractAction;
import com.nextep.proto.action.model.JsonProvider;
import com.nextep.proto.model.impl.TaskResult;
import com.nextep.proto.services.NotificationService;
import com.nextep.proto.spring.ContextHolder;
import com.nextep.users.model.User;
import com.nextep.users.services.UsersService;
import com.videopolis.apis.factory.ApisFactory;
import com.videopolis.apis.model.ApisRequest;
import com.videopolis.apis.service.ApiResponse;
import com.videopolis.cals.factory.ContextFactory;

/**
 * This action will send an email containing a link to reset the password of the
 * given user matching the email.
 * 
 * @author cfondacci
 *
 */
public class LostPasswordAction extends AbstractAction implements JsonProvider {

	private static final long serialVersionUID = 1L;

	@Autowired
	private NotificationService notificationService;
	@Autowired
	private UsersService usersService;

	// Action parameters
	private String email;

	// Internal variables
	private TaskResult result;

	@Override
	protected String doExecute() throws Exception {
		// Querying user from email address
		final ApisRequest request = ApisFactory.createRequest(User.class)
				.alternateKey(User.EMAIL_TYPE, email);

		// Executing
		final ApiResponse response = getApiService().execute(request,
				ContextFactory.createContext(getLocale()));

		// Getting user
		final User user = (User) response.getUniqueElement();
		if (user == null) {
			result = new TaskResult(false, "Unknown email address '" + email
					+ "'");
		} else {

			// Refreshing token
			ContextHolder.toggleWrite();
			usersService.refreshUserOnlineTimeout(user,
					usersService.generateUniqueToken(user));

			notificationService.sendChangePasswordEmail(user);
			result = new TaskResult(true,
					"Password reset link has been sent to '" + email + "'");
		}
		return SUCCESS;
	}

	@Override
	public String getJson() {
		return JSONObject.fromObject(result).toString();
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getEmail() {
		return email;
	}
}
