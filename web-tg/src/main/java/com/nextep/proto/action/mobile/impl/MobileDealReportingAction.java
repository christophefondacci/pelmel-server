package com.nextep.proto.action.mobile.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.fgp.deals.model.Deal;
import com.nextep.advertising.model.Subscription;
import com.nextep.geo.model.Place;
import com.nextep.json.model.impl.JsonStatus;
import com.nextep.proto.action.base.AbstractAction;
import com.nextep.proto.action.model.JsonProvider;
import com.nextep.proto.apis.adapters.ApisDealPlaceItemKeyAdapter;
import com.nextep.proto.apis.adapters.ApisSubscriptionPurchaserItemKey;
import com.nextep.proto.blocks.CurrentUserSupport;
import com.nextep.proto.helpers.DisplayHelper;
import com.nextep.proto.services.NotificationService;
import com.nextep.users.model.User;
import com.videopolis.apis.factory.ApisFactory;
import com.videopolis.apis.factory.SearchRestriction;
import com.videopolis.apis.model.ApisCriterion;
import com.videopolis.apis.model.ApisRequest;
import com.videopolis.apis.model.WithCriterion;
import com.videopolis.apis.service.ApiCompositeResponse;
import com.videopolis.calm.factory.CalmFactory;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.cals.factory.ContextFactory;

import net.sf.json.JSONObject;

public class MobileDealReportingAction extends AbstractAction implements JsonProvider {

	private static final long serialVersionUID = 1L;
	private static final String APIS_ALIAS_DEAL_PLACE = "dealPlace";
	private static final String APIS_ALIAS_DEAL = "deal";

	@Autowired
	private CurrentUserSupport currentUserSupport;

	@Autowired
	private NotificationService notificationService;

	// Dynamic arguments
	private String dealKey;

	@Override
	protected String doExecute() throws Exception {

		final ItemKey dealItemKey = CalmFactory.parseKey(dealKey);
		final ApisRequest request = (ApisRequest) ApisFactory.createCompositeRequest()
				.addCriterion(currentUserSupport.createApisCriterionFor(getNxtpUserToken(), true))
				.addCriterion((ApisCriterion) SearchRestriction.uniqueKey(dealItemKey).aliasedBy(APIS_ALIAS_DEAL)
						.addCriterion((ApisCriterion) SearchRestriction.adaptKey(new ApisDealPlaceItemKeyAdapter())
								.aliasedBy(APIS_ALIAS_DEAL_PLACE)
								.with((WithCriterion) SearchRestriction.with(Subscription.class).addCriterion(
										SearchRestriction.adaptKey(new ApisSubscriptionPurchaserItemKey())))));

		final ApiCompositeResponse response = (ApiCompositeResponse) getApiService().execute(request,
				ContextFactory.createContext(getLocale()));

		// Checking user
		final User user = response.getUniqueElement(User.class, CurrentUserSupport.APIS_ALIAS_CURRENT_USER);
		checkCurrentUser(user);

		// Getting deal
		final Deal deal = response.getUniqueElement(Deal.class, APIS_ALIAS_DEAL);
		final Place dealPlace = deal.getUnique(Place.class, APIS_ALIAS_DEAL_PLACE);
		final List<? extends Subscription> subscriptions = dealPlace.get(Subscription.class);
		User owner = null;
		for (Subscription s : subscriptions) {
			if (s.getStartDate().getTime() < System.currentTimeMillis()
					&& s.getEndDate().getTime() > System.currentTimeMillis()) {
				owner = s.getUnique(User.class);
				break;
			}
		}

		final StringBuilder buf = new StringBuilder();
		final String url = getUrlService().getOverviewUrl(DisplayHelper.getDefaultAjaxContainer(), dealPlace);
		final String userUrl = getUrlService().getOverviewUrl(DisplayHelper.getDefaultAjaxContainer(), user);
		final String ownerUrl = owner == null ? null
				: getUrlService().getOverviewUrl(DisplayHelper.getDefaultAjaxContainer(), owner);
		buf.append("A problem has been reported:<br><br>");
		buf.append("Deal " + deal.getKey() + " at <a href='" + url + "'>" + dealPlace.getName() + "</a><br>");
		buf.append("Reported by: <a href='" + userUrl + "'>" + user.getPseudo() + "</a><br>");
		if (owner != null) {
			buf.append("Place owner: <a href='" + ownerUrl + "'>" + owner.getPseudo() + "</a><br><br>");
		}
		buf.append("<br>");
		buf.append(
				"Please see if you can identify the issue, or simply pause the deal from within the app until you can investigate what is going on.<br><br>");
		buf.append("The PELMEL Team.");

		notificationService.notifyAdminByEmail("Problem with deal " + deal.getKey(), buf.toString());
		if (owner != null) {
			notificationService.notifyByEmail("Problem with deal " + deal.getKey(), buf.toString(), owner.getEmail(),
					null);
		}
		return SUCCESS;
	}

	public void setDealKey(String dealKey) {
		this.dealKey = dealKey;
	}

	public String getDealKey() {
		return dealKey;
	}

	@Override
	public String getJson() {
		JsonStatus status = new JsonStatus();
		status.setError(false);
		return JSONObject.fromObject(status).toString();
	}
}
