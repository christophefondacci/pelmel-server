package com.nextep.proto.action.impl.tools;

import org.springframework.beans.factory.annotation.Autowired;

import com.nextep.proto.action.base.AbstractAction;
import com.nextep.proto.action.model.SimpleResultProvider;
import com.nextep.proto.blocks.CurrentUserSupport;
import com.nextep.proto.spring.ContextHolder;
import com.nextep.users.model.User;
import com.nextep.users.services.UsersService;
import com.videopolis.apis.factory.ApisFactory;
import com.videopolis.apis.model.ApisRequest;
import com.videopolis.apis.service.ApiCompositeResponse;
import com.videopolis.cals.factory.ContextFactory;

/**
 * This action either prompts for new password so that the user could reset his
 * password from a link previously sent by email or changes the password if
 * password and passwordConfirm information is sent.
 * 
 * @author cfondacci
 *
 */
public class ResetPasswordAction extends AbstractAction implements
		SimpleResultProvider {

	private static final long serialVersionUID = 1L;

	@Autowired
	private UsersService usersService;
	@Autowired
	private CurrentUserSupport currentUserSupport;

	// Action arguments (optional)
	private String password;
	private String passwordConfirm;

	@Override
	protected String doExecute() throws Exception {
		if (password == null && passwordConfirm == null) {
			return INPUT;
		} else if (password != null && passwordConfirm != null
				&& password.equals(passwordConfirm)) {

			// Building a query that fetches current user from the user token
			final ApisRequest request = (ApisRequest) ApisFactory
					.createCompositeRequest().addCriterion(
							currentUserSupport.createApisCriterionFor(
									getNxtpUserToken(), false));

			// Executing
			final ApiCompositeResponse response = (ApiCompositeResponse) getApiService()
					.execute(request, ContextFactory.createContext(getLocale()));

			// Extracting user
			final User currentUser = response.getUniqueElement(User.class,
					CurrentUserSupport.APIS_ALIAS_CURRENT_USER);
			if (currentUser != null) {
				ContextHolder.toggleWrite();
				usersService.resetPassword(currentUser.getKey(),
						getNxtpUserToken(), password);
			}
			return SUCCESS;
		} else {
			setErrorMessage(getText("index.register.passworddonotmatch"));
			return ERROR;
		}
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPassword() {
		return password;
	}

	public void setPasswordConfirm(String passwordConfirm) {
		this.passwordConfirm = passwordConfirm;
	}

	public String getPasswordConfirm() {
		return passwordConfirm;
	}

	@Override
	public String getPageText() {
		return getText("static.text.resetPassword");
	}

	@Override
	public String getPageTitle() {
		return getText("static.title.resetPassword");
	}
}
