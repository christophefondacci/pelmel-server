package com.nextep.proto.action.impl.tools;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.nextep.cal.util.services.CalPersistenceService;
import com.nextep.events.model.CalendarType;
import com.nextep.events.model.Event;
import com.nextep.events.model.EventSeries;
import com.nextep.geo.model.Admin;
import com.nextep.geo.model.City;
import com.nextep.geo.model.GeographicItem;
import com.nextep.geo.model.Place;
import com.nextep.media.model.Media;
import com.nextep.media.model.MediaRequestTypes;
import com.nextep.proto.action.base.AbstractAction;
import com.nextep.proto.apis.adapters.ApisEventLocationAdapter;
import com.nextep.proto.helpers.DisplayHelper;
import com.nextep.proto.helpers.LocalizationHelper;
import com.nextep.proto.helpers.MediaHelper;
import com.nextep.proto.helpers.SearchHelper;
import com.nextep.proto.services.EventManagementService;
import com.nextep.proto.services.NotificationService;
import com.nextep.proto.spring.ContextHolder;
import com.nextep.users.model.MutableUser;
import com.nextep.users.model.User;
import com.nextep.users.model.UserLastLoginRequestType;
import com.videopolis.apis.exception.ApisException;
import com.videopolis.apis.factory.ApisFactory;
import com.videopolis.apis.factory.SearchRestriction;
import com.videopolis.apis.model.ApisCriterion;
import com.videopolis.apis.model.ApisRequest;
import com.videopolis.apis.model.WithCriterion;
import com.videopolis.apis.service.ApiCompositeResponse;
import com.videopolis.calm.exception.CalException;
import com.videopolis.calm.model.Sorter;
import com.videopolis.cals.factory.ContextFactory;
import com.videopolis.smaug.common.model.SearchScope;

public class EmailBatchAction extends AbstractAction {

	private static final long serialVersionUID = 1L;
	private static final String APIS_ALIAS_USERS = "users";
	private static final String APIS_ALIAS_PLACES = "places";
	private static final String APIS_ALIAS_EVENTS = "events";
	private static final String EMAIL_STATUS_BUG = "EMAIL_DATES_BUG";
	private static final Log LOGGER = LogFactory.getLog(EmailBatchAction.class);
	private static final String URL_APPSTORE = "https://itunes.apple.com/us/app/pelmel-guide-your-mobile-guide/id603515989?mt=8";

	private final DateFormat timeFormat = new SimpleDateFormat("hh:mmaa", Locale.ENGLISH);
	@Autowired
	private NotificationService notificationService;

	@Autowired
	@Qualifier("mobileUsersService")
	private CalPersistenceService usersService;

	@Autowired
	private EventManagementService eventManagementService;

	@Resource(mappedName = "email/daysFromLastLogin")
	private Integer daysFromLastLogin;

	@Resource(mappedName = "email/daysFromLastEmail")
	private Integer daysFromLastEmail;

	@Resource(mappedName = "email/maxSentEmails")
	private Integer maxSentEmails;

	@Resource(mappedName = "email/maxNearbyUsers")
	private Integer maxNearbyUsers;

	@Resource(mappedName = "email/newsletterTemplateId")
	private String newsletterTemplateId;

	@Resource(mappedName = "email/erratumNewsletterTemplateId")
	private String erratumNewsletterTemplateId;

	@Resource(mappedName = "email/dryRun")
	private Boolean dryRun;

	@Resource(mappedName = "mobile/nearbyPlacesRadius")
	private Double nearbyPlacesRadius;

	@Resource(mappedName = "mobile/nearbyPlacesCount")
	private Integer nearbyPlacesCount;

	private List<String> messages;

	@Override
	protected String doExecute() throws Exception {
		messages = new ArrayList<String>();
		int page = 0;
		int emailsSent = 0;
		boolean noMoreUsers = false;
		final Date emailDate = new Date();
		final List<String> usersEmails = new ArrayList<String>();
		final List<Sorter> userSorters = SearchHelper.getUserDefaultSorters();
		ContextHolder.toggleWrite();

		int waCount = 0;
		int caCount = 0;
		int orCount = 0;
		while (emailsSent < maxSentEmails && !noMoreUsers) {

			// Executing query
			final ApiCompositeResponse response = (ApiCompositeResponse) getApiService()
					.execute(buildRequestForPage(page), ContextFactory.createContext(getLocale()));

			// Getting users
			final List<? extends User> users = response.getElements(User.class, APIS_ALIAS_USERS);
			noMoreUsers = users.isEmpty();
			for (User user : users) {
				// Preparing HTML buffer
				final StringBuilder buf = new StringBuilder();

				// For now, only sending if we have not send anything
				LOGGER.info("Processing USER " + user.getKey() + " with email '" + user.getEmail() + "'");
				final City city = user.getUnique(City.class);
				final Admin adm1 = city == null ? null : city.getAdm1();
				if (city != null && adm1 != null) {
					if ("WA".equals(adm1.getAdminCode())) {
						waCount++;
					} else if ("CA".equals(adm1.getAdminCode())) {
						caCount++;
					} else if ("OR".equals(adm1.getAdminCode())) {
						orCount++;
					}
				}
				if (dryRun && !user.getKey().toString().equals("USER1")) {
					// && !user.getKey().toString().equals("USER308")) {
					continue;
				}
				if (adm1 != null && user != null && (user.getLastEmailDate() == null
						|| user.getLastEmailDate()
								.getTime() < (System.currentTimeMillis() - daysFromLastEmail * 24 * 60 * 60 * 1000)
						|| dryRun)
						&& ("WA".equals(adm1.getAdminCode()) || "CA".equals(adm1.getAdminCode())
								|| "OR".equals(adm1.getAdminCode()))) {

					// Querying nearby users for email customization
					final ApisRequest userRequest = (ApisRequest) ApisFactory.createCompositeRequest()
							.addCriterion((ApisCriterion) SearchRestriction
									.searchNear(User.class, SearchScope.NEARBY_BLOCK, user.getLatitude(),
											user.getLongitude(), 100, maxNearbyUsers + 10, 0)
									.sortBy(userSorters).aliasedBy(APIS_ALIAS_USERS)
									.with(Media.class, MediaRequestTypes.THUMB))
							.addCriterion((ApisCriterion) SearchRestriction
									.searchNear(Place.class, SearchScope.NEARBY_BLOCK, user.getLatitude(),
											user.getLongitude(), nearbyPlacesRadius, nearbyPlacesCount, 0)
									.aliasedBy(APIS_ALIAS_PLACES)
									.with((WithCriterion) SearchRestriction.with(EventSeries.class).with(Media.class,
											MediaRequestTypes.THUMB))
									.with(Media.class, MediaRequestTypes.THUMB))
							.addCriterion(
									(ApisCriterion) SearchRestriction
											.searchNear(Event.class, SearchScope.EVENTS, user.getLatitude(),
													user.getLongitude(), nearbyPlacesRadius, 20, 0)
											.aliasedBy(APIS_ALIAS_EVENTS).with(Media.class, MediaRequestTypes.THUMB)
											.addCriterion((ApisCriterion) SearchRestriction
													.adaptKey(new ApisEventLocationAdapter())
													.with(Media.class, MediaRequestTypes.THUMB)));

					// Executing nearby query
					final ApiCompositeResponse userResponse = (ApiCompositeResponse) getApiService()
							.execute(userRequest, ContextFactory.createContext(getLocale()));

					// Preparing subject / template
					String subject = "Your events this week";
					String templateId = newsletterTemplateId;
					String newStatus = null;
					// Filling nearby users section
					fillNearbyUsers(buf, user, userResponse);

					fillNearbyEvents(buf, user, userResponse);
					usersEmails.add(user.getEmail());

					// Flagging last email date for this user
					((MutableUser) user).setLastEmailDate(emailDate);
					((MutableUser) user).setStatus(newStatus);
					usersService.saveItem(user);

					// Sending message
					messages.add("Sending message to " + user.getEmail());

					notificationService.notifyByEmail(subject, buf.toString(), Arrays.asList(user.getEmail()),
							templateId);

					// notificationService.not
					emailsSent++;
					// Breaking loop if we reach the limit
					if (emailsSent >= maxSentEmails) {
						break;
					}
				}
			}

			// Incrementing page
			page++;
		}
		LOGGER.info(waCount + " users in Washington");
		LOGGER.info(caCount + " users in California");
		LOGGER.info(orCount + " users in Oregon");
		if (!usersEmails.isEmpty()) {
			LOGGER.info("Sending messages to " + usersEmails.toString());
			// notificationService.notifyByEmail("PELMEL: New Version
			// Available",
			// "", usersEmails, newsletterTemplateId);
		}

		return SUCCESS;
	}

	private void fillNearbyEvents(StringBuilder buf, User user, ApiCompositeResponse userResponse)
			throws ApisException, CalException {
		final DateFormat format = new SimpleDateFormat("yyyyMMdd");
		final DateFormat fullFormat = new SimpleDateFormat("EEEE, MMMM dd", Locale.ENGLISH);

		// Preparing hash maps storing events
		final Map<String, List<Event>> seriesDayMap = new HashMap<String, List<Event>>();
		final Map<String, Date> seriesKeysStartMap = new HashMap<String, Date>();

		// One time events
		final List<? extends Event> events = userResponse.getElements(Event.class, APIS_ALIAS_EVENTS);
		for (Event event : events) {
			final Place p = event.getUnique(Place.class);
			final Date startDate = eventManagementService.convertDate(event.getStartDate(),
					TimeZone.getDefault().getID(), p.getCity().getTimezoneId());
			final String eventDay = format.format(startDate);
			List<Event> dayEvents = seriesDayMap.get(eventDay);
			if (dayEvents == null) {
				dayEvents = new ArrayList<Event>();
				seriesDayMap.put(eventDay, dayEvents);
			}
			dayEvents.add(event);
		}

		// Extracting places
		final List<? extends Place> places = userResponse.getElements(Place.class, APIS_ALIAS_PLACES);
		for (Place place : places) {
			// Extracting place recurring events
			final List<? extends EventSeries> series = place.get(EventSeries.class);
			for (EventSeries serie : series) {

				// Only considering theme nights
				if (serie.getCalendarType() == CalendarType.THEME) {

					// Computing next date
					final Date nextStart = eventManagementService.computeNext(serie, place.getCity().getTimezoneId(),
							true);

					// Isolating the day
					final String startDay = format.format(nextStart);

					// Storing in map for later sort / filter
					List<Event> dayEvents = seriesDayMap.get(startDay);
					if (dayEvents == null) {
						dayEvents = new ArrayList<Event>();
						seriesDayMap.put(startDay, dayEvents);
					}
					seriesKeysStartMap.put(serie.getKey().toString(), nextStart);
					serie.add(place);
					dayEvents.add(serie);
				}
			}
		}

		if (!seriesDayMap.isEmpty()) {
			List<String> days = new ArrayList<String>(seriesDayMap.keySet());
			Collections.sort(days);
			int padding = 50;
			if (EMAIL_STATUS_BUG.equals(user.getStatusMessage())) {
				padding = 10;
			}
			buf.append("<p style='margin-top:0;padding-top:" + padding
					+ "px;'><span style='text-align: center; font-size:36px;'>Coming up</span></p>");

			int dayCount = 0;
			for (String dayStr : days) {
				try {
					final Date day = format.parse(dayStr);
					String htmlDay = obfuscateDate(fullFormat.format(day));

					buf.append(
							"<p style='margin-top:0;padding-top:10px;'><span style='text-align: center; font-size:26px;'>"
									+ htmlDay + "</span></p>");
					buf.append("<table cellpadding='5' cellspacing='0' style='margin:auto;'><tbody>");
					for (Event e : seriesDayMap.get(dayStr)) {

						// Extracting event place
						final Place place = e.getUnique(Place.class);

						// final Date eventDate = seriesKeysStartMap.get(e
						// .getKey().toString());

						// Building URL
						String url = getUrlService().getOverviewUrl(DisplayHelper.getDefaultAjaxContainer(), e);
						url = getFullUrl(user, url, true);

						// Building a 2 column table, image on the left and info
						// on the right
						buf.append("<tr><td><a href='" + url + "'><img height='100px' width='100px' src='");
						Media m = MediaHelper.getSingleMedia(e);
						String mediaUrl = null;
						if (m == null) {
							m = MediaHelper.getSingleMedia(place);
							mediaUrl = (m == null) ? MediaHelper.getNoThumbUrl(e)
									: getFullUrl(user, m.getThumbUrl(), false);
						} else {
							mediaUrl = getFullUrl(user, m.getThumbUrl(), false);
						}
						buf.append(mediaUrl + "'></a></td>");

						// Second column: title, location, time
						buf.append("<td><div><a href='" + url + "'>" + e.getName() + "</a></div>");
						if (place != null) {
							buf.append("<div style='color:#F4D400;'>@ " + place.getName() + "</div>");
						}
						String timeRange = getTimeRange(e);
						buf.append("<div>" + timeRange + "</div>");
						buf.append("</td></tr>");
					}
					buf.append("</tbody></table>");
				} catch (ParseException e) {
					LOGGER.error("Unable to parse '" + dayStr + "' as date");
				}
			}
		}

	}

	/**
	 * A date obfuscation so that iOS stops its crazy auto-detection of dates
	 * 
	 * @param date
	 *            the date to obsfucate
	 * @return the obfuscated date
	 */
	private String obfuscateDate(String date) {
		String[] components = date.split(" ");
		final StringBuilder buf = new StringBuilder();

		int index = 0;
		for (String component : components) {
			buf.append(component.substring(0, 1));
			buf.append("<span></span>");
			buf.append(component.substring(1));
			if (index < components.length - 1) {
				buf.append(" ");
			}
		}
		return buf.toString();
	}

	private String getFullUrl(User user, String url, boolean isLink) {
		String fullUrl = LocalizationHelper.buildUrl("en", getDomainName(), url);
		// if (isLink) {
		// if (user.getDeviceInfo() == null) {
		// return URL_APPSTORE;
		// } else {
		// return "pelmel://open";
		// }
		// fullUrl = fullUrl.replace("http", "pelmel");
		// }
		return fullUrl;
	}

	private String getTimeRange(Event e) throws CalException {
		if (e instanceof EventSeries) {
			try {
				final Place p = e.getUnique(Place.class);
				final Date now = eventManagementService.getLocalizedNow(p.getCity().getTimezoneId());
				final Date nextStart = eventManagementService.computeNextStart((EventSeries) e, now, true);
				final Date nextEnd = eventManagementService.computeNextStart((EventSeries) e, now, false);
				return buildTimeRange(nextStart, nextEnd);

			} catch (CalException ex) {
				LOGGER.error("Unable to get place of event " + e.getKey() + ", skipping time range for email (reason: "
						+ ex.getMessage() + ")");
			}
		} else {
			final Place p = e.getUnique(Place.class);
			final Date startDate = eventManagementService.convertDate(e.getStartDate(), TimeZone.getDefault().getID(),
					p.getCity().getTimezoneId());
			final Date endDate = eventManagementService.convertDate(e.getEndDate(), TimeZone.getDefault().getID(),
					p.getCity().getTimezoneId());

			return buildTimeRange(startDate, endDate);
		}
		return "n/a";
	}

	private String buildTimeRange(Date nextStart, Date nextEnd) {
		final String startStr = timeFormat.format(nextStart);
		final String endStr = timeFormat.format(nextEnd);
		return startStr + " - " + endStr; // startStr.substring(0, 2) +
											// "<span></span>"
		// + startStr.substring(2) + "-" + endStr.substring(0, 2)
		// + "<span></span>" + endStr.substring(2);
	}

	private void fillNearbyUsers(StringBuilder buf, User user, ApiCompositeResponse userResponse) throws ApisException {
		// Getting users
		final List<? extends User> nearbyUsers = userResponse.getElements(User.class, APIS_ALIAS_USERS);

		// Processing nearby users
		final StringBuilder nearbyBuf = new StringBuilder();
		int usersCount = 0;
		for (User nearbyUser : nearbyUsers) {
			final Media m = MediaHelper.getSingleMedia(nearbyUser);
			// Only considering users with photos
			if (m != null && !nearbyUser.getKey().equals(user.getKey())) {
				final String thumbUrl = MediaHelper.getImageUrl(m.getThumbUrl());
				String url = getUrlService().getOverviewUrl(DisplayHelper.getDefaultAjaxContainer(), nearbyUser);
				url = LocalizationHelper.buildUrl("en", getDomainName(), url);
				url = url.replace("http", "pelmel");
				// Buidling the thumb for this user
				nearbyBuf.append("<div style='width:100px;height:100px; display:inline-block; padding:1px;'><a href='"
						+ url + "'><img height='100px' src='" + thumbUrl + "' width='100px' /></a></div>");
				usersCount++;
			}
			if (usersCount >= maxNearbyUsers) {
				break;
			}
		}

		// We display nearby users block when at least 2 nearby
		// users to display
		if (usersCount >= 2) {
			buf.append(
					"<p style='margin-top:0;padding-top:30px;'><span style='text-align: center; font-size:36px;'>Guys near you</span></p>");
			buf.append(nearbyBuf.toString());
		}
	}

	public List<String> getMessages() {
		return messages;
	}

	private ApisRequest buildRequestForPage(int page) throws ApisException {
		final ApisRequest request = (ApisRequest) ApisFactory.createCompositeRequest()
				.addCriterion((ApisCriterion) SearchRestriction
						.list(User.class, new UserLastLoginRequestType(daysFromLastLogin, true)).paginatedBy(100, page)
						.aliasedBy(APIS_ALIAS_USERS).with(GeographicItem.class));
		return request;
	}
}
