package com.nextep.proto.action.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.nextep.advertising.model.Subscription;
import com.nextep.descriptions.model.Description;
import com.nextep.geo.model.City;
import com.nextep.geo.model.GeographicItem;
import com.nextep.geo.model.Place;
import com.nextep.properties.model.Property;
import com.nextep.proto.action.base.AbstractAction;
import com.nextep.proto.action.model.DescriptionsEditionAware;
import com.nextep.proto.action.model.PlaceEditionAware;
import com.nextep.proto.action.model.PropertiesEditionAware;
import com.nextep.proto.blocks.CurrentUserSupport;
import com.nextep.proto.blocks.DescriptionsEditionSupport;
import com.nextep.proto.blocks.PlaceEditionSupport;
import com.nextep.proto.blocks.PropertiesEditionSupport;
import com.nextep.proto.blocks.SelectableTagSupport;
import com.nextep.proto.blocks.TagSupport;
import com.nextep.proto.model.Constants;
import com.nextep.proto.model.SearchType;
import com.nextep.proto.services.RightsManagementService;
import com.nextep.tags.model.Tag;
import com.nextep.tags.model.impl.TagTypeRequestType;
import com.nextep.users.model.User;
import com.videopolis.apis.factory.ApisFactory;
import com.videopolis.apis.factory.SearchRestriction;
import com.videopolis.apis.model.ApisCriterion;
import com.videopolis.apis.model.ApisRequest;
import com.videopolis.apis.service.ApiCompositeResponse;
import com.videopolis.calm.factory.CalmFactory;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.calm.model.RequestType;
import com.videopolis.cals.factory.ContextFactory;

public class GetPlaceFormAction extends AbstractAction implements
		PlaceEditionAware, PropertiesEditionAware, DescriptionsEditionAware {

	private static final long serialVersionUID = -6947000149621794819L;
	private static final String APIS_ALIAS_PLACE = "place";
	private static final String APIS_ALIAS_TAGS = "tags";
	private static final RequestType REQUEST_TYPE_PLACE_TAGS = new TagTypeRequestType(
			Place.CAL_TYPE);
	private CurrentUserSupport currentUserSupport;
	private PlaceEditionSupport placeEditionSupport;
	private DescriptionsEditionSupport descriptionsEditionSupport;
	private PropertiesEditionSupport propertiesEditionSupport;
	private RightsManagementService rightsManagementService;
	private SelectableTagSupport tagSupport;
	private TagSupport amenitiesTagSupport;
	private String id;
	private String placeType;

	@Override
	public PlaceEditionSupport getPlaceEditionSupport() {
		return placeEditionSupport;
	}

	@Override
	public void setPlaceEditionSupport(PlaceEditionSupport placeEditionSupport) {
		this.placeEditionSupport = placeEditionSupport;
	}

	@Override
	protected String doExecute() throws Exception {
		final ItemKey placeKey = CalmFactory.parseKey(id);
		final ApisRequest request = ApisFactory.createCompositeRequest();
		final ApisCriterion placeCriterion = (ApisCriterion) SearchRestriction
				.uniqueKeys(Arrays.asList(placeKey))
				.aliasedBy(APIS_ALIAS_PLACE).with(Subscription.class);
		if (Place.CAL_TYPE.equals(placeKey.getType())) {
			placeCriterion.with(Description.class).with(Property.class)
					.with(Tag.class);
		}
		request.addCriterion(placeCriterion);
		request.addCriterion(currentUserSupport.createApisCriterionFor(
				getNxtpUserToken(), true));
		request.addCriterion(SearchRestriction.list(Tag.class,
				REQUEST_TYPE_PLACE_TAGS).aliasedBy(APIS_ALIAS_TAGS));
		final ApiCompositeResponse response = (ApiCompositeResponse) getApiService()
				.execute(request, ContextFactory.createContext(getLocale()));
		final User currentUser = response.getUniqueElement(User.class,
				CurrentUserSupport.APIS_ALIAS_CURRENT_USER);
		// Initializing header support
		CalmObject placeOrCity = response.getUniqueElement(CalmObject.class,
				APIS_ALIAS_PLACE);
		// If we have a geo item which is not a city, we blank it so that the
		// user
		// will need to specify the city explicitly
		if ((placeOrCity instanceof GeographicItem)
				&& !(placeOrCity instanceof City)
				&& !(placeOrCity instanceof Place)) {
			placeOrCity = null;
		}
		final Place p = response
				.getUniqueElement(Place.class, APIS_ALIAS_PLACE);
		getHeaderSupport().initialize(getLocale(), placeOrCity, null,
				SearchType.fromPlaceType(placeType));
		checkCurrentUser(currentUser);
		if (!rightsManagementService.canModify(currentUser, p)) {
			return FORBIDDEN;
		}
		placeEditionSupport.initialize(getLocale(), placeOrCity, placeType,
				currentUser);
		propertiesEditionSupport.initialize(getLocale(), p, Place.CAL_TYPE);
		descriptionsEditionSupport.initialize(getLocale(), p, getUrlService());

		// Initializing tag supports
		final List<Tag> tags = new ArrayList<Tag>();
		final List<Tag> amenities = new ArrayList<Tag>();
		final List<? extends Tag> allTags = response.getElements(Tag.class,
				APIS_ALIAS_TAGS);
		for (Tag tag : allTags) {
			if (Constants.TAG_DISPLAY_FACET.equals(tag.getDisplayMode())) {
				tags.add(tag);
			} else {
				amenities.add(tag);
			}
		}
		tagSupport.initialize(getLocale(), tags);
		if (p != null) {
			tagSupport.initializeSelection(getUrlService(), placeKey, p);
		}
		amenitiesTagSupport.initialize(getLocale(), amenities);

		return SUCCESS;
	}

	@Override
	protected int getLoginRequiredErrorCode() {
		return 200;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setPlaceType(String placeType) {
		this.placeType = placeType;
	}

	public String getPlaceType() {
		return placeType;
	}

	@Override
	public void setPropertiesEditionSupport(
			PropertiesEditionSupport propertiesEditionSupport) {
		this.propertiesEditionSupport = propertiesEditionSupport;
	}

	@Override
	public PropertiesEditionSupport getPropertiesEditionSupport() {
		return propertiesEditionSupport;
	}

	@Override
	public void setDescriptionsEditionSupport(
			DescriptionsEditionSupport descriptionsEditionSupport) {
		this.descriptionsEditionSupport = descriptionsEditionSupport;
	}

	@Override
	public DescriptionsEditionSupport getDescriptionsEditionSupport() {
		return descriptionsEditionSupport;
	}

	public void setCurrentUserSupport(CurrentUserSupport currentUserSupport) {
		this.currentUserSupport = currentUserSupport;
	}

	public void setTagSupport(SelectableTagSupport tagSupport) {
		this.tagSupport = tagSupport;
	}

	public SelectableTagSupport getTagSupport() {
		return tagSupport;
	}

	public void setAmenitiesTagSupport(TagSupport amenitiesTagSupport) {
		this.amenitiesTagSupport = amenitiesTagSupport;
	}

	public TagSupport getAmenitiesTagSupport() {
		return amenitiesTagSupport;
	}

	@Override
	public void setRightsManagementService(
			RightsManagementService rightsManagementService) {
		this.rightsManagementService = rightsManagementService;
	}
}
