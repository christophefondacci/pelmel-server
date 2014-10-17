package com.nextep.proto.action.mobile.impl;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import net.sf.json.JSONArray;

import com.nextep.json.model.impl.JsonMedia;
import com.nextep.media.model.Media;
import com.nextep.proto.action.base.AbstractAction;
import com.nextep.proto.action.model.JsonProvider;
import com.nextep.proto.builders.JsonBuilder;
import com.videopolis.apis.factory.ApisFactory;
import com.videopolis.apis.factory.SearchRestriction;
import com.videopolis.apis.model.ApisCriterion;
import com.videopolis.apis.model.ApisRequest;
import com.videopolis.apis.service.ApiCompositeResponse;
import com.videopolis.calm.factory.CalmFactory;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.cals.factory.ContextFactory;

public class MobileGalleryAction extends AbstractAction implements JsonProvider {

	private static final long serialVersionUID = 126526076507686401L;
	private static final String APIS_ALIAS_PARENT = "parent";

	// Injected helpers
	private JsonBuilder jsonBuilder;

	// Dynamic arguments
	private String id;
	private boolean highRes;

	// Internal variables
	private List<JsonMedia> jsonMedia;

	@Override
	protected String doExecute() throws Exception {
		final ItemKey parentKey = CalmFactory.parseKey(id);
		final ApisRequest request = (ApisRequest) ApisFactory
				.createCompositeRequest()
				.addCriterion(
						(ApisCriterion) SearchRestriction
								.uniqueKeys(Arrays.asList(parentKey))
								.aliasedBy(APIS_ALIAS_PARENT).with(Media.class));
		final ApiCompositeResponse response = (ApiCompositeResponse) getApiService()
				.execute(request, ContextFactory.createContext(getLocale()));
		final CalmObject parent = response.getUniqueElement(CalmObject.class,
				APIS_ALIAS_PARENT);
		final List<? extends Media> media = parent.get(Media.class);
		jsonMedia = new LinkedList<JsonMedia>();
		for (Media m : media) {
			final JsonMedia jsonMedium = jsonBuilder.buildJsonMedia(m, highRes);
			if (jsonMedium != null) {
				jsonMedia.add(jsonMedium);
			}
		}

		return SUCCESS;
	}

	public void setJsonBuilder(JsonBuilder jsonBuilder) {
		this.jsonBuilder = jsonBuilder;
	}

	@Override
	public String getJson() {
		return JSONArray.fromObject(jsonMedia).toString();
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setHighRes(boolean highRes) {
		this.highRes = highRes;
	}

	public boolean isHighRes() {
		return highRes;
	}

}
