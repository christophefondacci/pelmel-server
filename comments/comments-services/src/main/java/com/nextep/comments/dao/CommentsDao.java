package com.nextep.comments.dao;

import com.nextep.cal.util.model.CalDao;
import com.nextep.comments.model.Comment;
import com.videopolis.calm.model.ItemKey;

public interface CommentsDao extends CalDao<Comment> {

	int getCommentsCount(ItemKey forKey);

}
