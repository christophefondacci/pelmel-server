package com.nextep.proto.action.impl;

import com.nextep.descriptions.model.Description;
import com.nextep.geo.model.GeographicItem;
import com.nextep.media.model.Media;
import com.nextep.proto.action.base.AbstractAction;
import com.nextep.proto.action.model.QuickInfoAware;
import com.nextep.proto.blocks.QuickInfoSupport;
import com.nextep.tags.model.Tag;
import com.videopolis.apis.factory.ApisFactory;
import com.videopolis.apis.helper.ApisRegistry;
import com.videopolis.apis.model.ApisRequest;
import com.videopolis.apis.service.ApiResponse;
import com.videopolis.calm.factory.CalmFactory;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.cals.factory.ContextFactory;

public class AjaxMapInfoAction extends AbstractAction implements QuickInfoAware {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8856423331155118610L;

	private QuickInfoSupport quickInfoSupport;

	private String geoKey;

	@Override
	protected String doExecute() throws Exception {
		final ItemKey geoItemKey = CalmFactory.parseKey(geoKey);
		final Class<? extends CalmObject> calClass = ApisRegistry
				.getModelFromType(geoItemKey.getType());

		final ApisRequest request = (ApisRequest) ApisFactory
				.createRequest(
						calClass == null ? GeographicItem.class : calClass)
				.uniqueKey(geoItemKey.getId()).with(Description.class)
				.with(Media.class).with(Tag.class);

		// Invoking APIS
		final ApiResponse response = getApiService().execute(request,
				ContextFactory.createContext(getLocale()));

		final CalmObject o = response.getUniqueElement();
		quickInfoSupport.initialize(getUrlService(), o);
		return SUCCESS;
	}

	public void setGeoKey(String geoKey) {
		this.geoKey = geoKey;
	}

	public String getGeoKey() {
		return geoKey;
	}

	@Override
	public void setQuickInfoSupport(QuickInfoSupport quickInfoSupport) {
		this.quickInfoSupport = quickInfoSupport;
	}

	@Override
	public QuickInfoSupport getQuickInfoSupport() {
		return quickInfoSupport;
	}
}
