package com.nextep.proto.action.mobile.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.fgp.deals.model.Deal;
import com.fgp.deals.model.DealStatus;
import com.fgp.deals.model.DealType;
import com.fgp.deals.model.MutableDeal;
import com.nextep.advertising.model.Subscription;
import com.nextep.advertising.model.impl.AdvertisingRequestTypes;
import com.nextep.cal.util.services.CalPersistenceService;
import com.nextep.geo.model.Place;
import com.nextep.json.model.impl.JsonDeal;
import com.nextep.proto.action.base.AbstractAction;
import com.nextep.proto.action.model.JsonProvider;
import com.nextep.proto.blocks.CurrentUserSupport;
import com.nextep.proto.builders.JsonBuilder;
import com.nextep.proto.services.RightsManagementService;
import com.nextep.users.model.User;
import com.videopolis.apis.factory.ApisFactory;
import com.videopolis.apis.factory.SearchRestriction;
import com.videopolis.apis.model.ApisCriterion;
import com.videopolis.apis.model.ApisRequest;
import com.videopolis.apis.service.ApiCompositeResponse;
import com.videopolis.calm.factory.CalmFactory;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.cals.factory.ContextFactory;

import net.sf.json.JSONObject;

public class MobileActivateDealAction extends AbstractAction implements JsonProvider {

	private static final long serialVersionUID = 1L;
	private static final String APIS_ALIAS_PLACE = "place";

	@Autowired
	private CurrentUserSupport currentUserSupport;
	@Autowired
	private RightsManagementService rightsManagementService;
	@Autowired
	private JsonBuilder jsonBuilder;
	@Autowired
	@Qualifier("dealService")
	private CalPersistenceService dealsService;

	// Webservice arguments
	private String dealKey;
	private String placeKey;
	private String status;
	private String dealType;

	// Internal variables
	private Deal deal;

	@Override
	protected String doExecute() throws Exception {
		final ItemKey placeItemKey = CalmFactory.parseKey(placeKey);

		// Querying user, place and its deals
		final ApisRequest request = (ApisRequest) ApisFactory.createCompositeRequest()
				.addCriterion(currentUserSupport.createApisCriterionFor(getNxtpUserToken(), true))
				.addCriterion((ApisCriterion) SearchRestriction.uniqueKey(placeItemKey).aliasedBy(APIS_ALIAS_PLACE)
						.with(Deal.class).with(Subscription.class, AdvertisingRequestTypes.USER_CURRENT_SUBSCRIPTIONS));

		// Executing
		final ApiCompositeResponse response = (ApiCompositeResponse) getApiService().execute(request,
				ContextFactory.createContext(getLocale()));

		// Checking user
		final User user = response.getUniqueElement(User.class, CurrentUserSupport.APIS_ALIAS_CURRENT_USER);
		checkCurrentUser(user);

		final Place place = response.getUniqueElement(Place.class, APIS_ALIAS_PLACE);
		if (isOwner(user, place)) {

			final List<? extends Deal> deals = place.get(Deal.class);

			// If no deal specified we create it
			if (deals.isEmpty() && dealKey == null) {
				final MutableDeal deal = (MutableDeal) dealsService.createTransientObject();
				deal.setRelatedItemKey(place.getKey());
				deal.setStartDate(new Date());
				deal.setStatus(DealStatus.RUNNING);
				deal.setDealType(DealType.TWO_FOR_ONE);
				dealsService.saveItem(deal);
				this.deal = deal;
			} else if (dealKey != null) {
				final ItemKey dealItemKey = CalmFactory.parseKey(dealKey);
				// The specified deal key needs to match the current deal
				// attached to the place
				Deal selectedDeal = null;
				for (Deal deal : deals) {
					if (deal.getKey().equals(dealItemKey)) {
						selectedDeal = deal;
						break;
					}
				}
				// We only proceed if a match was found
				if (selectedDeal != null) {
					((MutableDeal) selectedDeal).setStatus(DealStatus.valueOf(status));
					dealsService.saveItem(selectedDeal);
					this.deal = selectedDeal;
				} else {
					setErrorMessage("Deal '" + dealKey + "' not found in deals of place '" + placeKey + "'");
					return ERROR;
				}
			} else {
				setErrorMessage("No deal key specified but place '" + placeKey + "' already has deals");
				return ERROR;
			}

		}

		return SUCCESS;
	}

	@Override
	public String getJson() {
		final JsonDeal jsonDeal = jsonBuilder.buildJsonDeal(deal);
		return JSONObject.fromObject(jsonDeal).toString();
	}

	private boolean isOwner(User user, Place place) {
		final List<? extends Subscription> subscriptions = user.get(Subscription.class);
		for (Subscription subscription : subscriptions) {
			if (subscription.getRelatedItemKey().equals(place.getKey())
					&& subscription.getStartDate().getTime() < System.currentTimeMillis()
					&& subscription.getEndDate().getTime() > System.currentTimeMillis()) {
				return true;
			}
		}
		return false || rightsManagementService.isAdministrator(user);
	}

	public void setDealKey(String dealKey) {
		this.dealKey = dealKey;
	}

	public String getDealKey() {
		return dealKey;
	}

	public void setPlaceKey(String placeKey) {
		this.placeKey = placeKey;
	}

	public String getPlaceKey() {
		return placeKey;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getStatus() {
		return status;
	}

	public void setDealType(String dealType) {
		this.dealType = dealType;
	}

	public String getDealType() {
		return dealType;
	}
}
