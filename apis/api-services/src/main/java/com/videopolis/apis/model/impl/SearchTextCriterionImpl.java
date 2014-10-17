package com.videopolis.apis.model.impl;

import java.util.List;

import com.videopolis.apis.concurrent.impl.SearchTextTask;
import com.videopolis.apis.exception.ApisException;
import com.videopolis.apis.model.ApisContext;
import com.videopolis.apis.model.CriteriaContainer;
import com.videopolis.apis.model.base.AbstractWithCriterion;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.cals.model.ItemsResponse;
import com.videopolis.concurrent.model.Task;
import com.videopolis.smaug.common.model.SuggestScope;

/**
 * This criterion hold information and can provide a textual search task.
 * 
 * @author Christophe Fondacci
 * 
 */
public class SearchTextCriterionImpl extends AbstractWithCriterion {

	private final List<SuggestScope> scopes;
	private final String searchedText;
	private final String itemType;
	private final int itemsCount;

	/**
	 * Creates a new search text criterion from information provided
	 * 
	 * @param itemType
	 *            the CAL type of the expected elements to retrieve
	 * @param scopes
	 *            the {@link SuggestScope} to search in
	 * @param text
	 *            the text to search for
	 */
	public SearchTextCriterionImpl(String itemType, List<SuggestScope> scopes,
			String text, int itemsCount) {
		this.itemType = itemType;
		this.scopes = scopes;
		this.searchedText = text;
		this.itemsCount = itemsCount;
	}

	@Override
	public Task<ItemsResponse> getTask(CriteriaContainer parent,
			ApisContext context, CalmObject... parentObjects)
			throws ApisException {
		final SearchTextTask task = new SearchTextTask(itemType, scopes,
				searchedText, itemsCount, context);
		task.setSorters(getSorters());
		return task;
	}

	@Override
	public String getType() {
		return itemType;
	}

	@Override
	public String toString() {
		return "searchTextCriterion;" + itemType + ";" + searchedText;
	}

}
