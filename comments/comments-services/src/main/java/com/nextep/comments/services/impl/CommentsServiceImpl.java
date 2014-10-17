package com.nextep.comments.services.impl;

import java.util.List;

import com.nextep.cal.util.services.CalPersistenceService;
import com.nextep.cal.util.services.base.AbstractDaoBasedCalServiceImpl;
import com.nextep.comments.dao.CommentsDao;
import com.nextep.comments.model.Comment;
import com.nextep.comments.model.impl.CommentImpl;
import com.videopolis.calm.exception.CalException;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.cals.model.CalContext;
import com.videopolis.cals.model.PaginatedItemsResponse;
import com.videopolis.cals.model.impl.PaginatedItemsResponseImpl;

public class CommentsServiceImpl extends AbstractDaoBasedCalServiceImpl
		implements CalPersistenceService {

	@Override
	public Class<? extends CalmObject> getProvidedClass() {
		return Comment.class;
	}

	@Override
	public String getProvidedType() {
		return Comment.CAL_ID;
	}

	@Override
	public void saveItem(CalmObject object) {
		getCalDao().save(object);
	}

	@Override
	public List<? extends CalmObject> setItemFor(ItemKey contributedItemKey,
			ItemKey... internalItemKeys) throws CalException {
		return null;
	}

	@Override
	public CalmObject createTransientObject() {
		return new CommentImpl();
	}

	@Override
	public PaginatedItemsResponse getPaginatedItemsFor(ItemKey itemKey,
			CalContext context, int resultsPerPage, int pageNumber)
			throws CalException {

		final List<? extends CalmObject> comments = getCalDao().getItemsFor(
				itemKey, resultsPerPage, pageNumber);
		final int commentsCount = ((CommentsDao) getCalDao())
				.getCommentsCount(itemKey);
		final PaginatedItemsResponseImpl response = new PaginatedItemsResponseImpl(
				resultsPerPage, pageNumber);
		response.setItemCount(commentsCount);
		response.setItems(comments);
		int pages = commentsCount / resultsPerPage;
		int pagesMod = commentsCount % resultsPerPage;
		response.setPageCount(pages + (pagesMod > 0 ? 1 : 0));
		return response;
	}

}
