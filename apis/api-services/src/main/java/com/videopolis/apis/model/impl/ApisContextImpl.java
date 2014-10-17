package com.videopolis.apis.model.impl;

import com.videopolis.apis.model.ApisContext;
import com.videopolis.apis.service.ApiMutableResponse;
import com.videopolis.cals.model.CalContext;
import com.videopolis.smaug.service.SearchService;

/**
 * Default basic implementation of the {@link ApisContext}
 * 
 * @author Christophe Fondacci
 * 
 */
public class ApisContextImpl implements ApisContext {

    private ApiMutableResponse apiResponse;
    private CalContext calContext;
    private SearchService searchService;

    @Override
    public ApiMutableResponse getApiResponse() {
	return apiResponse;
    }

    @Override
    public CalContext getCalContext() {
	return calContext;
    }

    @Override
    public SearchService getSearchService() {
	return searchService;
    }

    public void setApiResponse(ApiMutableResponse apiResponse) {
	this.apiResponse = apiResponse;
    }

    public void setCalContext(CalContext calContext) {
	this.calContext = calContext;
    }

    public void setSearchService(SearchService searchService) {
	this.searchService = searchService;
    }
}
