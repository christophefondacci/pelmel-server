package com.nextep.proto.action.impl;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONArray;

import com.nextep.geo.model.GeographicItem;
import com.nextep.json.model.impl.JsonSuggest;
import com.nextep.media.model.Media;
import com.nextep.proto.action.base.AbstractAction;
import com.nextep.proto.action.model.SuggestProvider;
import com.nextep.proto.helpers.DisplayHelper;
import com.nextep.proto.helpers.GeoHelper;
import com.nextep.proto.helpers.MediaHelper;
import com.nextep.users.model.User;
import com.videopolis.apis.factory.ApisFactory;
import com.videopolis.apis.factory.SearchRestriction;
import com.videopolis.apis.model.ApisCriterion;
import com.videopolis.apis.model.ApisRequest;
import com.videopolis.apis.model.SearchStatistic;
import com.videopolis.apis.service.ApiCompositeResponse;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.cals.factory.ContextFactory;
import com.videopolis.smaug.common.model.SuggestScope;

public class AjaxSuggestUserAction extends AbstractAction implements
		SuggestProvider {

	private static final long serialVersionUID = -3897665410499160971L;
	private static final String APIS_ALIAS_USER = "user";
	private static final String KEY_PREFIX_ICON = "suggest.icon.";

	private String userName;
	private List<? extends CalmObject> proposals;
	private ApiCompositeResponse response;

	@Override
	protected String doExecute() throws Exception {
		final ApisRequest request = (ApisRequest) ApisFactory
				.createCompositeRequest().addCriterion(
						(ApisCriterion) SearchRestriction
								.searchFromText(User.class, SuggestScope.USER,
										userName, 15)
								.aliasedBy(APIS_ALIAS_USER)
								.with(GeographicItem.class).with(Media.class));
		response = (ApiCompositeResponse) getApiService().execute(request,
				ContextFactory.createContext(getLocale()));
		proposals = response.getElements(CalmObject.class, APIS_ALIAS_USER);
		return SUCCESS;
	}

	public void setTerm(String cityName) {
		this.userName = cityName;
	}

	public String getTerm() {
		return userName;
	}

	@Override
	public String getSuggestionsAsJSON() {
		// First we build a map that categorizes results by type
		final List<JsonSuggest> suggests = new ArrayList<JsonSuggest>();
		for (CalmObject object : proposals) {
			// Extracting highlight
			final SearchStatistic stat = response.getStatistic(object.getKey(),
					SearchStatistic.MATCHED_TEXT);
			final GeographicItem geoItem = GeoHelper
					.extractLocalization(object);
			final Media userMedia = MediaHelper.getSingleMedia(object);
			final String userName = DisplayHelper.getName(object);
			final StringBuilder buf = new StringBuilder();

			// Building suggest HTML entry prefix SPAN with image
			buf.append("<span class=\"suggest-user-entry\">");
			// Extracting image
			String mediaUrl = "";
			if (userMedia != null) {
				mediaUrl = MediaHelper.getImageUrl(userMedia.getMiniThumbUrl());
			} else {
				mediaUrl = MediaHelper.getImageUrl(getMessageSource()
						.getMessage(
								KEY_PREFIX_ICON + object.getKey().getType(),
								null, getLocale()));
			}
			buf.append("<img class=\"user-icon\" src=\"" + mediaUrl + "\">");
			buf.append("<span class=\"user-text\">");
			// If we got the highlight we use it as HTML text
			String htmlName;
			if (stat != null) {
				htmlName = stat.getStringValue();
			} else {
				htmlName = userName;
			}
			buf.append(htmlName);
			buf.append(" <i>[");

			// Building fully qualified city name (no HTML)
			buf.append(GeoHelper.buildShortLocalizationString(geoItem,
					DisplayHelper.getName(geoItem)));
			buf.append("]</i>");
			buf.append("</span></span>");

			// Buiding object's overview URL
			final String url = getUrlService().getOverviewUrl(
					DisplayHelper.getDefaultAjaxContainer(), object);
			final JsonSuggest suggest = new JsonSuggest(buf.toString(),
					userName, object.getKey().toString(), url);

			// Adding to our suggest list
			suggests.add(suggest);
		}
		return JSONArray.fromObject(suggests).toString();

	}
}
