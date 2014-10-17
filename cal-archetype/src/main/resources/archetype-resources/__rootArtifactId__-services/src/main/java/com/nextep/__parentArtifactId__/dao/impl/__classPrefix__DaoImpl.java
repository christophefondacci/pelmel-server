#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package com.nextep.${parentArtifactId}.dao.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.nextep.cal.util.model.base.AbstractCalDao;
import com.nextep.cal.util.model.CalDao;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;

public class ${classPrefix}DaoImpl extends AbstractCalDao<CalmObject> {

	@PersistenceContext(unitName="nextep-${parentArtifactId}")
	private EntityManager entityManager;

	@Override
	public CalmObject getById(long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<CalmObject> getItemsFor(ItemKey key) {
		// TODO Auto-generated method stub
		return null;
	}

}
