package com.nextep.proto.action.impl.tools;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.nextep.advertising.model.AdvertisingBanner;
import com.nextep.proto.action.base.AbstractAction;
import com.nextep.proto.spring.ContextHolder;
import com.nextep.smaug.service.SearchPersistenceService;
import com.videopolis.apis.exception.ApisException;
import com.videopolis.apis.factory.ApisFactory;
import com.videopolis.apis.factory.SearchRestriction;
import com.videopolis.apis.model.ApisRequest;
import com.videopolis.apis.service.ApiCompositeResponse;
import com.videopolis.cals.factory.ContextFactory;
import com.videopolis.cals.model.PaginationInfo;
import com.videopolis.smaug.common.model.SearchScope;

public class IndexBannersAction extends AbstractAction {

	private static final long serialVersionUID = 1L;
	private static final String APIS_ALIAS_BANNERS = "banners";
	private static final int PAGE_SIZE = 100;
	private static final Log LOGGER = LogFactory
			.getLog(IndexBannersAction.class);

	@Autowired
	private SearchPersistenceService searchPersistenceService;

	private boolean clearBanners;

	@Override
	protected String doExecute() throws Exception {

		// Clearing if needed
		if (clearBanners) {
			searchPersistenceService.removeAll(AdvertisingBanner.CAL_ID);
		}

		// Init
		int pageOffset = 0;
		boolean hasMore = true;
		int i = 1;

		// Processing one page
		while (hasMore) {
			final ApisRequest request = buildRequest(pageOffset++);
			ContextHolder.toggleReadonly();

			// Executing
			final ApiCompositeResponse response = (ApiCompositeResponse) getApiService()
					.execute(request, ContextFactory.createContext(getLocale()));

			// Getting banners
			final List<? extends AdvertisingBanner> banners = response
					.getElements(AdvertisingBanner.class, APIS_ALIAS_BANNERS);
			ContextHolder.toggleWrite();

			// Getting pagination
			final PaginationInfo paginationInfo = response
					.getPaginationInfo(APIS_ALIAS_BANNERS);
			int total = paginationInfo.getItemCount();
			hasMore = total > pageOffset * PAGE_SIZE;

			// Iterating over all banners
			for (AdvertisingBanner banner : banners) {
				// Indexing in SOLR
				LOGGER.info("Indexing banner [" + i + "/" + total + "]: "
						+ banner.getKey());
				searchPersistenceService.storeCalmObject(banner,
						SearchScope.NEARBY_BLOCK);
				i++;
			}
		}
		return SUCCESS;
	}

	private ApisRequest buildRequest(int pageOffset) throws ApisException {

		// Listing all banners (there should be a reasonable size for now)
		final ApisRequest request = (ApisRequest) ApisFactory
				.createCompositeRequest().addCriterion(
						SearchRestriction.list(AdvertisingBanner.class, null)
								.paginatedBy(PAGE_SIZE, pageOffset)
								.aliasedBy(APIS_ALIAS_BANNERS));
		return request;
	}

	public void setClearBanners(boolean clearBanners) {
		this.clearBanners = clearBanners;
	}

	public boolean isClearBanners() {
		return clearBanners;
	}
}
