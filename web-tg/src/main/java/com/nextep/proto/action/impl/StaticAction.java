package com.nextep.proto.action.impl;

import java.util.Arrays;

import com.nextep.geo.model.GeographicItem;
import com.nextep.geo.model.Place;
import com.nextep.proto.action.base.AbstractAction;
import com.nextep.proto.action.model.HeaderAware;
import com.nextep.proto.action.model.LocalizationAware;
import com.nextep.proto.blocks.HeaderSupport;
import com.nextep.proto.blocks.LocalizationSupport;
import com.nextep.proto.helpers.DisplayHelper;
import com.nextep.proto.model.SearchType;
import com.videopolis.apis.factory.ApisFactory;
import com.videopolis.apis.factory.SearchRestriction;
import com.videopolis.apis.model.ApisRequest;
import com.videopolis.apis.service.ApiCompositeResponse;
import com.videopolis.calm.factory.CalmFactory;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.cals.factory.ContextFactory;

public class StaticAction extends AbstractAction implements HeaderAware,
		LocalizationAware {

	private static final String APIS_ALIAS_ITEM = "item";

	private HeaderSupport headerSupport;
	private LocalizationSupport localizationSupport;

	private static final long serialVersionUID = -2020420814477478126L;
	private String key;
	private String url;

	@Override
	protected String doExecute() throws Exception {
		if (key != null) {
			final ItemKey itemKey = CalmFactory.parseKey(key);
			final ApisRequest request = (ApisRequest) ApisFactory
					.createCompositeRequest().addCriterion(
							SearchRestriction
									.uniqueKeys(Arrays.asList(itemKey))
									.aliasedBy(APIS_ALIAS_ITEM));

			final ApiCompositeResponse response = (ApiCompositeResponse) getApiService()
					.execute(request, ContextFactory.createContext(getLocale()));

			// Retrieving item
			final CalmObject obj = response.getUniqueElement(CalmObject.class,
					APIS_ALIAS_ITEM);
			if (obj instanceof Place) {
				url = getUrlService().getOverviewUrl(
						DisplayHelper.getDefaultAjaxContainer(), obj);
			} else if (obj instanceof GeographicItem) {
				url = getUrlService().buildSearchUrl(
						DisplayHelper.getDefaultAjaxContainer(),
						(GeographicItem) obj, SearchType.BARS);
			}
		}

		headerSupport.initialize(getLocale(), null, null, SearchType.BARS);
		localizationSupport.initialize(SearchType.BARS, getUrlService(),
				getLocale(), null, null);
		return SUCCESS;
	}

	@Override
	public void setHeaderSupport(HeaderSupport headerSupport) {
		this.headerSupport = headerSupport;
	}

	@Override
	public HeaderSupport getHeaderSupport() {
		return headerSupport;
	}

	@Override
	public void setLocalizationSupport(LocalizationSupport localizationSupport) {
		this.localizationSupport = localizationSupport;
	}

	@Override
	public LocalizationSupport getLocalizationSupport() {
		return localizationSupport;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getKey() {
		return key;
	}

	@Override
	public String getUrl() {
		return url;
	}
}
