package com.nextep.proto.action.base;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import com.nextep.descriptions.model.Description;
import com.nextep.descriptions.model.DescriptionRequestType;
import com.nextep.geo.model.GeographicItem;
import com.nextep.geo.model.Place;
import com.nextep.geo.model.impl.GeoRequestTypes;
import com.nextep.media.model.Media;
import com.nextep.proto.action.model.LocalizationAware;
import com.nextep.proto.action.model.MediaAware;
import com.nextep.proto.action.model.PopularAware;
import com.nextep.proto.action.model.SearchAware;
import com.nextep.proto.action.model.TagAware;
import com.nextep.proto.blocks.LocalizationSupport;
import com.nextep.proto.blocks.MediaProvider;
import com.nextep.proto.blocks.PopularSupport;
import com.nextep.proto.blocks.SearchSupport;
import com.nextep.proto.blocks.TagSupport;
import com.nextep.proto.exceptions.UserLoginTimeoutException;
import com.nextep.proto.helpers.SearchHelper;
import com.nextep.proto.model.SearchType;
import com.nextep.tags.model.Tag;
import com.nextep.users.model.User;
import com.videopolis.apis.exception.ApisException;
import com.videopolis.apis.factory.ApisFactory;
import com.videopolis.apis.factory.SearchRestriction;
import com.videopolis.apis.helper.ApisRegistry;
import com.videopolis.apis.model.ApisRequest;
import com.videopolis.apis.model.WithCriterion;
import com.videopolis.apis.service.ApiResponse;
import com.videopolis.calm.exception.CalException;
import com.videopolis.calm.factory.CalmFactory;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.calm.model.RequestType;
import com.videopolis.cals.factory.ContextFactory;
import com.videopolis.smaug.common.factory.FacetFactory;
import com.videopolis.smaug.common.model.Facet;
import com.videopolis.smaug.common.model.FacetCategory;
import com.videopolis.smaug.common.model.FacetRange;
import com.videopolis.smaug.common.model.SearchScope;
import com.videopolis.smaug.common.model.impl.FacetImpl;

public abstract class AbstractSearchUserAction extends AbstractAction implements
		SearchAware, LocalizationAware, PopularAware, MediaAware, TagAware {

	// Constants declaration
	private static final long serialVersionUID = -7514162123751575502L;
	private static final RequestType DESC_MAIN_TYPE = DescriptionRequestType.SINGLE_DESC;
	protected static final int USERS_PER_PAGE = 16;
	private static final SearchType searchType = SearchType.MEN;

	// Injected supports & services
	private SearchSupport searchSupport;
	private LocalizationSupport localizationSupport;
	// private PlacesSupport placesSupport;
	private SearchSupport placeSearchSupport;
	private PopularSupport popularSupport;
	private MediaProvider mediaProvider;
	private TagSupport tagSupport;

	// Dynamic action parameters
	private String rangeFormat;
	private String geoKey;
	private String facets;
	private String weight, height, birthyear, age;
	private int pageOffset = 0;

	@Override
	protected String doExecute() throws Exception {
		final ItemKey geoItemKey = CalmFactory.parseKey(geoKey);

		// Querying content
		ApisRequest request = buildApisRequest(geoItemKey);

		ApiResponse response = getApiService().execute(request,
				ContextFactory.createContext(getLocale()));

		initialize(response);

		return SUCCESS;
	}

	/**
	 * Builds the APIS request for the specified geo item key
	 * 
	 * @param geoItemKey
	 *            the {@link ItemKey} to search in
	 * @return the APIS request that searches user in the {@link GeographicItem}
	 * @throws ApisException
	 *             whenever we had troubles building the request
	 */
	protected ApisRequest buildApisRequest(ItemKey geoItemKey)
			throws ApisException, CalException, UserLoginTimeoutException {
		// Preparing facet categories
		final Collection<FacetCategory> userCategories = SearchHelper
				.buildUserFacetCategories();
		final Collection<FacetCategory> placeCategories = SearchHelper
				.buildPlaceFacetCategories(geoItemKey);
		// Building facet filters
		final List<Facet> facetFilters = buildFacetFilters(SearchHelper
				.getTagFacetCategory());

		final Class<? extends CalmObject> calClass = ApisRegistry
				.getModelFromType(geoItemKey.getType());
		final ApisRequest request = (ApisRequest) ApisFactory
				.createRequest(
						calClass == null ? GeographicItem.class : calClass)
				.alternateKey(geoItemKey.getType(), geoItemKey.getId())
				.with((WithCriterion) SearchRestriction
						.withContained(User.class, SearchScope.CHILDREN,
								USERS_PER_PAGE, pageOffset)
						.facettedBy(userCategories).filteredBy(facetFilters)
						.with(Media.class).with(Tag.class)
						.with(Description.class, DESC_MAIN_TYPE))
				.with(SearchRestriction.withContained(Place.class,
						SearchScope.POI, 0, 0).facettedBy(placeCategories))
				.with(GeographicItem.class, GeoRequestTypes.TOP_CITIES);
		return request;
	}

	/**
	 * Hook method to allow implementors custom initialization
	 * 
	 * @param response
	 */
	protected abstract void initialize(ApiResponse response)
			throws ApisException, UserLoginTimeoutException;

	protected List<Facet> buildFacetFilters(FacetCategory c) {
		final List<Facet> facetFilters = new ArrayList<Facet>();
		if (facets != null) {
			// Extracting regular facets
			final String[] facetList = facets.split(",");
			for (String facetCode : facetList) {
				final FacetImpl f = new FacetImpl();
				f.setFacetCategory(c);
				f.setFacetCode(facetCode.trim());
				facetFilters.add(f);
			}
		}
		// Extracting range facets
		if (weight != null && !"".equals(weight.trim())) {
			final String[] ranges = weight.split("to");
			final FacetCategory weightCategory = SearchHelper
					.getFacetCategory("weight_kg");
			final FacetRange weightRange = FacetFactory.createFacetRange(
					weightCategory, rangeFormat, Long.valueOf(ranges[0]),
					Long.valueOf(ranges[1]));
			facetFilters.add(weightRange);
		}
		if (height != null && !"".equals(height.trim())) {
			final String[] ranges = height.split("to");
			final FacetCategory heightCategory = SearchHelper
					.getFacetCategory("height_cm");
			final FacetRange heightRange = FacetFactory.createFacetRange(
					heightCategory, rangeFormat, Long.valueOf(ranges[0]),
					Long.valueOf(ranges[1]));
			facetFilters.add(heightRange);
		}
		if (birthyear == null && age != null && !"".equals(age.trim())) {
			final String[] ranges = age.split("to");
			final FacetCategory birthCategory = SearchHelper
					.getFacetCategory("birthyear");
			final Calendar calendar = Calendar.getInstance();
			final int currentYear = calendar.get(Calendar.YEAR);
			final FacetRange yearRange = FacetFactory.createFacetRange(
					birthCategory, rangeFormat,
					currentYear - Integer.valueOf(ranges[1]), currentYear
							- Integer.valueOf(ranges[0]));
			facetFilters.add(yearRange);
		}
		if (birthyear != null && !"".equals(birthyear.trim())) {
			final String[] ranges = birthyear.split("to");
			final FacetCategory birthCategory = SearchHelper
					.getFacetCategory("birthyear");
			final FacetRange yearRange = FacetFactory.createFacetRange(
					birthCategory, rangeFormat, Integer.valueOf(ranges[0]),
					Integer.valueOf(ranges[1]));
			facetFilters.add(yearRange);
		}
		return facetFilters;
	}

	@Override
	public SearchSupport getSearchSupport() {
		return searchSupport;
	}

	@Override
	public void setSearchSupport(SearchSupport searchSupport) {
		this.searchSupport = searchSupport;
	}

	public void setGeoKey(String geoKey) {
		this.geoKey = geoKey;
	}

	public String getGeoKey() {
		return geoKey;
	}

	public int getPageOffset() {
		return pageOffset;
	}

	public void setPageOffset(int pageOffset) {
		this.pageOffset = pageOffset;
	}

	@Override
	public LocalizationSupport getLocalizationSupport() {
		return localizationSupport;
	}

	@Override
	public void setLocalizationSupport(LocalizationSupport localizationSupport) {
		this.localizationSupport = localizationSupport;
	}

	public void setFacets(String facets) {
		this.facets = facets;
	}

	public String getFacets() {
		return facets;
	}

	// @Override
	// public void setPlacesSupport(PlacesSupport placesSupport) {
	// this.placesSupport = placesSupport;
	// }
	//
	// @Override
	// public PlacesSupport getPlacesSupport() {
	// return placesSupport;
	// }

	public void setPlaceSearchSupport(SearchSupport placeSearchSupport) {
		this.placeSearchSupport = placeSearchSupport;
	}

	public SearchSupport getPlaceSearchSupport() {
		return placeSearchSupport;
	}

	public void setHeight_cm(String height) {
		this.height = height;
	}

	public String getHeight_cm() {
		return height;
	}

	public void setWeight_kg(String weight) {
		this.weight = weight;
	}

	public String getWeight_kg() {
		return weight;
	}

	public void setBirthyear(String birthyear) {
		this.birthyear = birthyear;
	}

	public String getBirthyear() {
		return birthyear;
	}

	public void setAge(String age) {
		this.age = age;
	}

	public String getAge() {
		return age;
	}

	public void setRangeFormat(String rangeFormat) {
		this.rangeFormat = rangeFormat;
	}

	@Override
	public PopularSupport getPopularSupport() {
		return popularSupport;
	}

	@Override
	public void setPopularSupport(PopularSupport popularSupport) {
		this.popularSupport = popularSupport;
	}

	@Override
	public void setSearchType(SearchType searchType) {
	}

	@Override
	public SearchType getSearchType() {
		return searchType;
	}

	@Override
	public MediaProvider getMediaProvider() {
		return mediaProvider;
	}

	@Override
	public void setMediaProvider(MediaProvider mediaProvider) {
		this.mediaProvider = mediaProvider;
	}

	@Override
	public void setTagSupport(TagSupport tagSupport) {
		this.tagSupport = tagSupport;
	}

	@Override
	public TagSupport getTagSupport() {
		return tagSupport;
	}
}
