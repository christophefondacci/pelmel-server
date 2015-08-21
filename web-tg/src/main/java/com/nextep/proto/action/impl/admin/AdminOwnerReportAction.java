package com.nextep.proto.action.impl.admin;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.nextep.advertising.model.Subscription;
import com.nextep.advertising.model.impl.AdvertisingRequestTypes;
import com.nextep.geo.model.Place;
import com.nextep.json.model.impl.JsonStatistic;
import com.nextep.proto.action.base.AbstractAction;
import com.nextep.proto.action.model.JsonProvider;
import com.nextep.proto.blocks.CurrentUserSupport;
import com.nextep.proto.builders.JsonBuilder;
import com.nextep.proto.services.RightsManagementService;
import com.nextep.statistic.model.ItemView;
import com.nextep.statistic.model.impl.StatisticPeriod;
import com.nextep.statistic.model.impl.StatisticRequestType;
import com.nextep.users.model.User;
import com.videopolis.apis.factory.ApisFactory;
import com.videopolis.apis.factory.SearchRestriction;
import com.videopolis.apis.model.ApisCriterion;
import com.videopolis.apis.model.ApisRequest;
import com.videopolis.apis.service.ApiCompositeResponse;
import com.videopolis.calm.factory.CalmFactory;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.cals.factory.ContextFactory;

import net.sf.json.JSONArray;

public class AdminOwnerReportAction extends AbstractAction implements JsonProvider {

	private static final long serialVersionUID = 1L;
	private static final String APIS_ALIAS_PLACE = "place";

	@Autowired
	private CurrentUserSupport currentUserSupport;
	@Autowired
	private RightsManagementService rightsManagementService;
	@Autowired
	private JsonBuilder jsonBuilder;

	// Action arguments
	private String placeKey;
	private int reportPeriodDays;

	// Internal vars
	private List<JsonStatistic> statistics;

	@Override
	protected String doExecute() throws Exception {
		final ItemKey placeItemKey = CalmFactory.parseKey(placeKey);

		final ApisRequest request = (ApisRequest) ApisFactory.createCompositeRequest()
				.addCriterion((ApisCriterion) currentUserSupport.createApisCriterionFor(getNxtpUserToken(), true)
						.with(Subscription.class, AdvertisingRequestTypes.USER_CURRENT_SUBSCRIPTIONS))
				.addCriterion((ApisCriterion) SearchRestriction.uniqueKey(placeItemKey).aliasedBy(APIS_ALIAS_PLACE)
						.with(ItemView.class, StatisticRequestType.fromDaysPeriod(reportPeriodDays)));

		final ApiCompositeResponse response = (ApiCompositeResponse) getApiService().execute(request,
				ContextFactory.createContext(getLocale()));

		// Checking user
		final User user = response.getUniqueElement(User.class, CurrentUserSupport.APIS_ALIAS_CURRENT_USER);
		checkCurrentUser(user);

		// Checking whether he has the right to check statistics for that
		// element
		boolean isOwned = false;
		final List<? extends Subscription> subscriptions = user.get(Subscription.class);
		for (Subscription subscription : subscriptions) {
			if (subscription.getStartDate().getTime() < System.currentTimeMillis()
					&& subscription.getEndDate().getTime() > System.currentTimeMillis()) {
				if (subscription.getRelatedItemKey().equals(placeItemKey)) {
					isOwned = true;
					break;
				}
			}
		}
		if (isOwned || rightsManagementService.isAdministrator(user)) {
			final Place place = response.getUniqueElement(Place.class, APIS_ALIAS_PLACE);
			final List<? extends ItemView> views = place.get(ItemView.class);
			final StatisticPeriod period = StatisticPeriod.fromDays(reportPeriodDays);
			statistics = buildStatistics(views, period);
		}

		return SUCCESS;
	}

	private List<JsonStatistic> buildStatistics(List<? extends ItemView> views, StatisticPeriod period) {
		Date startDate, endDate;
		final long incTime = period.getIncrementTime();
		final Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -period.getRangeDuration());
		// If increment is in days or above, we round the date to 00:00:00 to
		// consider full days
		if (period.getIncrementTime() >= 86400000) {
			cal.set(Calendar.HOUR_OF_DAY, 0);
		}
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		startDate = cal.getTime();
		endDate = new Date();

		// Hashing stats by time
		final Map<String, Map<Long, JsonStatistic>> typedStatsMap = new HashMap<>();
		final Set<String> statTypes = new HashSet<>();
		for (ItemView view : views) {
			final JsonStatistic stat = jsonBuilder.buildJsonStatistic(view);

			// Getting typed map
			final String statType = stat.getStatType();
			Map<Long, JsonStatistic> statsMap = typedStatsMap.get(statType);

			// If none yet we create it and store it
			if (statsMap == null) {
				statsMap = new HashMap<>();
				typedStatsMap.put(statType, statsMap);
				statTypes.add(statType);
			}
			statsMap.put(stat.getStatDate() * 1000, stat);
		}

		// Now building resulting list filled with 0 for each missing increment
		final List<JsonStatistic> stats = new ArrayList<>();

		for (String statType : statTypes) {
			final Map<Long, JsonStatistic> statsMap = typedStatsMap.get(statType);
			long statTime = startDate.getTime();
			long endTime = endDate.getTime();
			while (statTime < endTime) {
				// Getting stat if defined
				JsonStatistic stat = statsMap.get(statTime);

				// Generating a 0-valued stat for that point if missing (this is
				// the whole point of this process)
				if (stat == null) {
					stat = new JsonStatistic();
					stat.setCount(0);
					stat.setStatDate(statTime / 1000);
					stat.setStatType(statType);
				}

				// Filling the stat
				stats.add(stat);
				statTime += incTime;
			}
		}
		return stats;
	}

	@Override
	public String getJson() {
		return JSONArray.fromObject(statistics).toString();
	}

	public void setPlaceKey(String placeKey) {
		this.placeKey = placeKey;
	}

	public String getPlaceKey() {
		return placeKey;
	}

	public void setReportPeriodDays(int reportPeriodDays) {
		this.reportPeriodDays = reportPeriodDays;
	}

	public int getReportPeriodDays() {
		return reportPeriodDays;
	}
}
