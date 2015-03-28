package com.nextep.proto.action.mobile.impl;

import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.nextep.cal.util.services.CalPersistenceService;
import com.nextep.json.model.impl.JsonStatus;
import com.nextep.proto.action.base.AbstractAction;
import com.nextep.proto.action.model.JsonProvider;
import com.nextep.proto.blocks.CurrentUserSupport;
import com.nextep.proto.spring.ContextHolder;
import com.nextep.users.model.MutableUser;
import com.nextep.users.model.PushProvider;
import com.nextep.users.model.User;
import com.videopolis.apis.factory.ApisFactory;
import com.videopolis.apis.model.ApisRequest;
import com.videopolis.apis.service.ApiCompositeResponse;
import com.videopolis.cals.factory.ContextFactory;

public class MobileRegisterToken extends AbstractAction implements JsonProvider {

	private static final long serialVersionUID = -2798074549028757740L;

	private static final Log LOGGER = LogFactory
			.getLog(MobileRegisterToken.class);

	@Autowired
	private CurrentUserSupport currentUserSupport;
	@Autowired
	@Qualifier("usersService")
	private CalPersistenceService usersService;

	private String pushProvider;
	private String pushDeviceId;

	@Override
	public String getJson() {
		JsonStatus status = new JsonStatus();
		status.setError(false);
		status.setMessage("Push device registered");
		return JSONObject.fromObject(status).toString();
	}

	@Override
	protected String doExecute() throws Exception {
		final ApisRequest request = (ApisRequest) ApisFactory
				.createCompositeRequest().addCriterion(
						currentUserSupport.createApisCriterionFor(
								getNxtpUserToken(), true));

		// Executing query
		final ApiCompositeResponse response = (ApiCompositeResponse) getApiService()
				.execute(request, ContextFactory.createContext(getLocale()));

		// Getting current user
		final MutableUser user = (MutableUser) response.getUniqueElement(
				User.class, CurrentUserSupport.APIS_ALIAS_CURRENT_USER);

		// Checking
		checkCurrentUser(user);

		ContextHolder.toggleWrite();
		user.setPushDeviceId(pushDeviceId);
		if (pushProvider != null
				&& !pushProvider.equals(user.getPushProvider())) {
			try {
				final PushProvider provider = PushProvider
						.valueOf(pushProvider);
				user.setPushProvider(provider);
			} catch (IllegalArgumentException e) {
				// Ignoring this
				LOGGER.warn("PUSH: Invalid provider: " + pushProvider);
			}
		}
		usersService.saveItem(user);
		return SUCCESS;
	}

	public void setPushDeviceId(String pushDeviceId) {
		this.pushDeviceId = pushDeviceId;
	}

	public String getPushDeviceId() {
		return pushDeviceId;
	}

	public void setPushProvider(String pushProvider) {
		this.pushProvider = pushProvider;
	}

	public String getPushProvider() {
		return pushProvider;
	}
}
