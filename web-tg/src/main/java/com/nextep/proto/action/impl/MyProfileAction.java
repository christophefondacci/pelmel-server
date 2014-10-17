package com.nextep.proto.action.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.nextep.advertising.model.AdvertisingBooster;
import com.nextep.advertising.model.impl.AdvertisingRequestTypes;
import com.nextep.descriptions.model.Description;
import com.nextep.geo.model.City;
import com.nextep.media.model.Media;
import com.nextep.media.model.MediaRequestTypes;
import com.nextep.messages.model.Message;
import com.nextep.properties.model.Property;
import com.nextep.proto.action.base.AbstractAction;
import com.nextep.proto.action.model.CurrentUserAware;
import com.nextep.proto.action.model.DescriptionsEditionAware;
import com.nextep.proto.action.model.LocalizationAware;
import com.nextep.proto.action.model.MediaAware;
import com.nextep.proto.action.model.MessagingAware;
import com.nextep.proto.action.model.PropertiesEditionAware;
import com.nextep.proto.action.model.SponsorshipAware;
import com.nextep.proto.action.model.TagAware;
import com.nextep.proto.apis.adapters.ApisAdvertisingRelatedItemKeyAdapter;
import com.nextep.proto.blocks.CurrentUserSupport;
import com.nextep.proto.blocks.DescriptionsEditionSupport;
import com.nextep.proto.blocks.LocalizationSupport;
import com.nextep.proto.blocks.MediaProvider;
import com.nextep.proto.blocks.MessagingSupport;
import com.nextep.proto.blocks.PropertiesEditionSupport;
import com.nextep.proto.blocks.SelectableTagSupport;
import com.nextep.proto.blocks.SponsorshipSupport;
import com.nextep.proto.blocks.TagSupport;
import com.nextep.proto.helpers.GeoHelper;
import com.nextep.proto.model.Constants;
import com.nextep.proto.model.SearchType;
import com.nextep.tags.model.Tag;
import com.nextep.tags.model.impl.TagTypeRequestType;
import com.nextep.users.model.User;
import com.videopolis.apis.factory.ApisFactory;
import com.videopolis.apis.factory.SearchRestriction;
import com.videopolis.apis.model.ApisCriterion;
import com.videopolis.apis.model.ApisItemKeyAdapter;
import com.videopolis.apis.model.ApisRequest;
import com.videopolis.apis.model.WithCriterion;
import com.videopolis.apis.service.ApiCompositeResponse;
import com.videopolis.calm.exception.CalException;
import com.videopolis.calm.model.RequestType;
import com.videopolis.cals.factory.ContextFactory;

public class MyProfileAction extends AbstractAction implements TagAware,
		CurrentUserAware, MessagingAware, PropertiesEditionAware,
		DescriptionsEditionAware, MediaAware, LocalizationAware,
		SponsorshipAware {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6377928816984806262L;
	private static final String APIS_ALL_TAGS_ALIAS = "all";
	private static final RequestType REQUEST_TYPE_USER_TAGS = new TagTypeRequestType(
			User.CAL_TYPE);
	private static final ApisItemKeyAdapter advertisingRelatedItemKeyAdapter = new ApisAdvertisingRelatedItemKeyAdapter();

	private SelectableTagSupport tagSupport;
	private CurrentUserSupport currentUserSupport;
	private MessagingSupport messagingSupport;
	private PropertiesEditionSupport propertiesEditionSupport;
	private DescriptionsEditionSupport descriptionsEditionSupport;
	private LocalizationSupport localizationSupport;
	private MediaProvider mediaProvider;
	private SponsorshipSupport sponsorshipSupport;

	private List<Tag> userTags;

	@SuppressWarnings("unchecked")
	@Override
	protected String doExecute() throws Exception {
		final ApisCriterion userCriterion = (ApisCriterion) currentUserSupport
				.createApisCriterionFor(getNxtpUserToken(), false)
				.with((WithCriterion) SearchRestriction.with(
						AdvertisingBooster.class,
						AdvertisingRequestTypes.USER_BOOSTERS).addCriterion(
						(ApisCriterion) SearchRestriction
								.adaptKey(advertisingRelatedItemKeyAdapter)
								.aliasedBy(
										Constants.APIS_ALIAS_ADVERTISING_ITEM)
								.with(Media.class, MediaRequestTypes.THUMB)));
		// Fetching user tags
		userCriterion.with(Tag.class).with(Property.class)
				.with(Description.class);

		ApisRequest request = (ApisRequest) ApisFactory
				.createCompositeRequest()
				.addCriterion(
						SearchRestriction.list(Tag.class,
								REQUEST_TYPE_USER_TAGS).aliasedBy(
								APIS_ALL_TAGS_ALIAS))
				.addCriterion(userCriterion);

		ApiCompositeResponse response = (ApiCompositeResponse) getApiService()
				.execute(request, ContextFactory.createContext(getLocale()));

		final List<Tag> tags = (List<Tag>) response.getElements(Tag.class,
				APIS_ALL_TAGS_ALIAS);
		final User user = response.getUniqueElement(User.class,
				CurrentUserSupport.APIS_ALIAS_CURRENT_USER);
		getHeaderSupport().initialize(getLocale(), user, null, SearchType.MAP);
		checkCurrentUser(user);
		userTags = (List<Tag>) user.get(Tag.class);
		tagSupport.initialize(getLocale(), tags);
		tagSupport.initializeSelection(getUrlService(), user.getKey(), user);

		currentUserSupport.initialize(getUrlService(), user);
		messagingSupport.initialize(getUrlService(), getLocale(),
				user.get(Message.class),
				response.getPaginationInfo(Message.class), user.getKey(),
				getHeaderSupport().getPageStyle());
		propertiesEditionSupport.initialize(getLocale(), user, User.CAL_TYPE);
		descriptionsEditionSupport.initialize(getLocale(), user);
		mediaProvider.initialize(user.getKey(), user.get(Media.class));
		localizationSupport.initialize(SearchType.MEN, getUrlService(),
				getLocale(), user.getUnique(City.class), null);
		sponsorshipSupport.initialize(getLocale(), getUrlService(), null, user);
		return SUCCESS;
	}

	public String getBirthDD(User user) {
		final Date birth = user.getBirthday();
		Calendar c = Calendar.getInstance();
		c.setTime(birth);
		return String.valueOf(c.get(Calendar.DAY_OF_MONTH));
	}

	public String getBirthMM(User user) {
		final Date birth = user.getBirthday();
		Calendar c = Calendar.getInstance();
		c.setTime(birth);
		return String.valueOf(c.get(Calendar.MONTH) + 1);
	}

	public String getBirthYYYY(User user) {
		final Date birth = user.getBirthday();
		Calendar c = Calendar.getInstance();
		c.setTime(birth);
		return String.valueOf(c.get(Calendar.YEAR));
	}

	public String getCSSClassSelected(Tag tag) {
		return isChecked(tag) ? "selected-" : "";
	}

	public boolean isChecked(Tag tag) {
		if (userTags != null) {
			return userTags.contains(tag);
		} else {
			return false;
		}
	}

	public String getCityValue() throws CalException {
		final User user = currentUserSupport.getCurrentUser();
		if (user != null) {
			final City city = user.getUnique(City.class);
			if (city != null) {
				return GeoHelper.buildShortLocalizationString(city);
			}
		}
		return "";
	}

	public String getCityDefinition() throws CalException {
		final User user = currentUserSupport.getCurrentUser();
		if (user != null) {
			final City city = user.getUnique(City.class);
			if (city != null) {
				return GeoHelper.buildFullLocalizationString(city);
			}
		}
		return "";
	}

	@Override
	public void setTagSupport(TagSupport tagSupport) {
		this.tagSupport = (SelectableTagSupport) tagSupport;
	}

	@Override
	public TagSupport getTagSupport() {
		return tagSupport;
	}

	@Override
	public void setCurrentUserSupport(CurrentUserSupport currentUserSupport) {
		this.currentUserSupport = currentUserSupport;
	}

	@Override
	public CurrentUserSupport getCurrentUserSupport() {
		return currentUserSupport;
	}

	@Override
	public void setMessagingSupport(MessagingSupport messagingSupport) {
		this.messagingSupport = messagingSupport;
	}

	@Override
	public MessagingSupport getMessagingSupport() {
		return messagingSupport;
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

	@Override
	public MediaProvider getMediaProvider() {
		return mediaProvider;
	}

	@Override
	public void setMediaProvider(MediaProvider mediaProvider) {
		this.mediaProvider = mediaProvider;
	}

	@Override
	public void setLocalizationSupport(LocalizationSupport localizationSupport) {
		this.localizationSupport = localizationSupport;
	}

	@Override
	public LocalizationSupport getLocalizationSupport() {
		return localizationSupport;
	}

	@Override
	public void setSponsorshipSupport(SponsorshipSupport sponsorshipSupport) {
		this.sponsorshipSupport = sponsorshipSupport;
	}

	@Override
	public SponsorshipSupport getSponsorshipSupport() {
		return sponsorshipSupport;
	}
}
