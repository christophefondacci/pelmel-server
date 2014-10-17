package com.videopolis.apis.model.base;

import java.util.ArrayList;
import java.util.Collection;

import com.videopolis.apis.concurrent.impl.listener.SplittedSubProcessingListener;
import com.videopolis.apis.concurrent.impl.listener.SubprocessingTaskListener;
import com.videopolis.apis.exception.ApisException;
import com.videopolis.apis.factory.SearchRestriction;
import com.videopolis.apis.model.ApisContext;
import com.videopolis.apis.model.ApisCriterion;
import com.videopolis.apis.model.ApisCustomAdapter;
import com.videopolis.apis.model.CriteriaContainer;
import com.videopolis.apis.model.WithCriterion;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.RequestType;
import com.videopolis.calm.model.Sorter;
import com.videopolis.cals.model.ItemsResponse;
import com.videopolis.concurrent.event.TaskListener;
import com.videopolis.concurrent.service.TaskRunnerService;
import com.videopolis.smaug.common.model.SearchScope;
import com.videopolis.smaug.common.model.SuggestScope;

/**
 * Base implementation for all {@link ApisCriterion}.
 * 
 * @author Christophe Fondacci
 * 
 */
public abstract class AbstractApisCriterion implements ApisCriterion {

	private final Collection<ApisCriterion> criterion = new ArrayList<ApisCriterion>();

	private int itemsCount = 0;

	@Override
	public Collection<ApisCriterion> getApisCriteria() {
		return criterion;
	}

	@Override
	public CriteriaContainer addCriterion(final ApisCriterion criterion) {
		this.criterion.add(criterion);
		return this;
	}

	@Override
	public CriteriaContainer with(final Class<? extends CalmObject> type)
			throws ApisException {
		addCriterion(SearchRestriction.with(type));
		return this;
	}

	@Override
	public CriteriaContainer with(final Class<? extends CalmObject> type,
			final RequestType requestType) throws ApisException {
		addCriterion(SearchRestriction.with(type, requestType));
		return this;
	}

	@Override
	public CriteriaContainer with(final Class<? extends CalmObject> type,
			final int itemsPerPage, final int pageNb) throws ApisException {
		addCriterion(SearchRestriction.with(type, itemsPerPage, pageNb));
		return this;
	}

	@Override
	public CriteriaContainer with(final Class<? extends CalmObject> type,
			final Sorter.Order sortOrder, final String sortCriterion,
			final int itemsPerPage, final int pageNb) throws ApisException {
		addCriterion(SearchRestriction.with(type, sortOrder, sortCriterion,
				itemsPerPage, pageNb));
		return this;
	}

	@Override
	@Deprecated
	public CriteriaContainer withNearest(
			final Class<? extends CalmObject> type, final int itemsPerPage,
			final int pageNb, final double distance) throws ApisException {
		addCriterion(SearchRestriction.withNearest(type, itemsPerPage, pageNb,
				distance));
		return this;
	}

	@Override
	public CriteriaContainer with(final WithCriterion withCriterion)
			throws ApisException {
		addCriterion(withCriterion);
		return this;
	}

	@Override
	public CriteriaContainer forKey(final String type, final String id,
			final int itemsPerPage, final int pageNumber) throws ApisException {
		addCriterion(SearchRestriction.forKey(type, id, itemsPerPage,
				pageNumber));
		return this;
	}

	@Override
	public CriteriaContainer with(final Class<? extends CalmObject> type,
			final String name) throws ApisException {
		addCriterion(SearchRestriction.with(type, name));
		return this;
	}

	@Override
	public CriteriaContainer withContained(Class<? extends CalmObject> type,
			SearchScope scope, int pageSize, int pageOffset)
			throws ApisException {
		addCriterion(SearchRestriction.withContained(type, scope, pageSize,
				pageOffset));
		return this;
	}

	@Override
	public CriteriaContainer withNearest(Class<? extends CalmObject> type,
			SearchScope scope, int pageSize, int pageOffset, double distance)
			throws ApisException {
		addCriterion(SearchRestriction.withNearest(type, scope, pageSize,
				pageOffset, distance));
		return this;
	}

	@Override
	public CriteriaContainer searchFromText(
			Class<? extends CalmObject> itemType, SuggestScope scopes,
			String text, int itemsCount) throws ApisException {
		addCriterion(SearchRestriction.searchFromText(itemType, scopes, text,
				itemsCount));
		return this;
	}

	@Override
	public TaskListener<ItemsResponse> getSubprocessingListener(
			ApisCriterion criterion, ApisContext context,
			TaskRunnerService taskRunnerService) {
		if (itemsCount == 0) {
			return new SubprocessingTaskListener(criterion, context,
					taskRunnerService);
		} else {
			return new SplittedSubProcessingListener(itemsCount, criterion,
					context, taskRunnerService);
		}
	}

	@Override
	public CriteriaContainer split(int itemsCount) {
		this.itemsCount = itemsCount;
		return this;
	}

	@Override
	public CriteriaContainer customAdapt(ApisCustomAdapter adapter, String alias)
			throws ApisException {
		addCriterion(SearchRestriction.customAdapt(adapter, alias));
		return this;
	}
}
