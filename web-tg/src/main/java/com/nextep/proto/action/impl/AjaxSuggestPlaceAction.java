//package com.nextep.proto.action.impl;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import net.sf.json.JSONArray;
//
//import com.nextep.geo.model.Place;
//import com.nextep.geo.services.GeoService;
//import com.nextep.json.model.impl.JsonSuggest;
//import com.nextep.proto.action.base.AbstractAction;
//import com.nextep.proto.action.model.SuggestProvider;
//import com.nextep.proto.helpers.DisplayHelper;
//import com.nextep.proto.helpers.GeoHelper;
//
//public class AjaxSuggestPlaceAction extends AbstractAction implements
//		SuggestProvider {
//
//	private static final long serialVersionUID = -3897665410499160971L;
//	private GeoService geoService;
//	private String term;
//	private List<Place> places;
//
//	@Override
//	protected String doExecute() throws Exception {
//		places = geoService.findPlaces(term);
//		return SUCCESS;
//	}
//
//	public void setTerm(String term) {
//		this.term = term;
//	}
//
//	public String getTerm() {
//		return term;
//	}
//
//	public void setGeoService(GeoService geoService) {
//		this.geoService = geoService;
//	}
//
//	@Override
//	public String getSuggestionsAsJSON() {
//		final List<JsonSuggest> placesArray = new ArrayList<JsonSuggest>();
//		for (Place place : places) {
//			// Building fully qualified city name
//			final String placeName = GeoHelper
//					.buildShortPlaceLocalizationString(place);
//			final String url = getUrlService().getOverviewUrl(
//					DisplayHelper.getDefaultAjaxContainer(), place);
//			final JsonSuggest suggest = new JsonSuggest(placeName, placeName,
//					place.getKey().toString(), url);
//			placesArray.add(suggest);
//		}
//		return JSONArray.fromObject(placesArray).toString();
//	}
// }
