package com.nextep.proto.action.mobile.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.fgp.deals.model.Deal;
import com.fgp.deals.model.DealUse;
import com.fgp.deals.model.MutableDealUse;
import com.fgp.deals.model.impl.DealRequestTypes;
import com.nextep.cal.util.services.CalPersistenceService;
import com.nextep.json.model.impl.JsonDeal;
import com.nextep.json.model.impl.JsonStatus;
import com.nextep.proto.action.base.AbstractAction;
import com.nextep.proto.action.model.JsonProviderWithError;
import com.nextep.proto.blocks.CurrentUserSupport;
import com.nextep.proto.builders.JsonBuilder;
import com.nextep.proto.model.Constants;
import com.nextep.proto.services.LocalizationService;
import com.nextep.proto.services.ViewManagementService;
import com.nextep.proto.spring.ContextHolder;
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

public class MobileUseDealAction extends AbstractAction implements JsonProviderWithError {

	private static final long serialVersionUID = 1L;
	private static final String APIS_ALIAS_DEAL = "deal";

	@Autowired
	private CurrentUserSupport currentUserSupport;
	@Autowired
	@Qualifier("dealUseService")
	private CalPersistenceService dealUseService;
	@Autowired
	private ViewManagementService viewManagementService;
	@Autowired
	private LocalizationService localizationService;
	@Autowired
	private JsonBuilder jsonBuilder;

	private String dealKey;
	private boolean dryRun;

	// Internal variable
	private Deal deal;

	@Override
	protected String doExecute() throws Exception {
		final ItemKey dealItemKey = CalmFactory.parseKey(dealKey);

		final ApisRequest request = (ApisRequest) ApisFactory.createCompositeRequest()
				.addCriterion((ApisCriterion) currentUserSupport.createApisCriterionFor(getNxtpUserToken(), true)
						.with(DealUse.class, DealRequestTypes.DAILY_DEAL))
				.addCriterion((ApisCriterion) SearchRestriction.uniqueKey(dealItemKey).aliasedBy(APIS_ALIAS_DEAL)
						.with(DealUse.class, DealRequestTypes.DAILY_DEAL));

		final ApiCompositeResponse response = (ApiCompositeResponse) getApiService().execute(request,
				ContextFactory.createContext(getLocale()));

		// Extracting and checking user
		final User user = response.getUniqueElement(User.class, CurrentUserSupport.APIS_ALIAS_CURRENT_USER);
		checkCurrentUser(user);

		// Getting deal
		deal = response.getUniqueElement(Deal.class, APIS_ALIAS_DEAL);

		// If dry run we only need to fetch the deal and return it, otherwise we
		// use it
		if (!dryRun) {
			// Getting uses of this deal today
			final List<? extends DealUse> dealUses = deal.get(DealUse.class);

			// If maximum reached, we deny access to the deal
			if (deal.getMaxDealUses() != null && deal.getMaxDealUses() > 0 && dealUses.size() > deal.getMaxDealUses()) {
				setErrorMessage("Max deal uses reached");
				return error(403);
			}
			// Checking if already used by this person
			final List<? extends DealUse> userDealUses = user.get(DealUse.class);
			for (DealUse dealUse : userDealUses) {
				if (dealUse.getDeal().getKey().equals(dealItemKey)) {
					setErrorMessage("Already used");
					return error(403);
				}
			}

			// If not checked in
			final ItemKey checkedInPlaceKey = localizationService.getCheckedInPlaceKey(user);
			if (!deal.getRelatedItemKey().equals(checkedInPlaceKey)) {
				setErrorMessage("Not checked in");
				return error(403);
			}

			// Saving new deal
			ContextHolder.toggleWrite();
			final MutableDealUse dealUse = (MutableDealUse) dealUseService.createTransientObject();
			dealUse.setDeal(deal);
			dealUse.setConsumerItemKey(user.getKey());
			dealUse.setUseTime(new Date());
			final Calendar cal = Calendar.getInstance();
			cal.set(Calendar.HOUR, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			dealUse.setUseDay(cal.getTime());
			dealUseService.saveItem(dealUse);

			// Logging event for place statistics
			viewManagementService.logViewCountByKey(deal.getRelatedItemKey(), user, Constants.VIEW_STAT_DEAL_USE);
			deal.add(dealUse);
		}
		return SUCCESS;
	}

	@Override
	public String getJson() {
		final JsonDeal json = jsonBuilder.buildJsonDeal(deal);
		return JSONObject.fromObject(json).toString();
	}

	@Override
	public String getJsonError() {
		if (deal != null) {
			return getJson();
		} else {
			final JsonStatus status = new JsonStatus(true, getErrorMessage());
			return JSONObject.fromObject(status).toString();
		}
	}

	public void setDealKey(String dealKey) {
		this.dealKey = dealKey;
	}

	public String getDealKey() {
		return dealKey;
	}

	public void setDryRun(boolean dryRun) {
		this.dryRun = dryRun;
	}

	public boolean isDryRun() {
		return dryRun;
	}
}
