package com.videopolis.apis.model;

public interface ItemKeyAdapterCriterion extends ApisCriterion,
	Aliasable<ApisCriterion> {

    ApisItemKeyAdapter getAdapter();
}
