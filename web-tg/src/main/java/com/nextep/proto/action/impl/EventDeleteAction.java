package com.nextep.proto.action.impl;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.nextep.cal.util.services.CalExtendedPersistenceService;
import com.nextep.events.model.EventSeries;
import com.nextep.json.model.impl.JsonStatus;
import com.nextep.proto.action.base.AbstractAction;
import com.nextep.proto.action.model.JsonProvider;
import com.nextep.proto.spring.ContextHolder;
import com.videopolis.calm.factory.CalmFactory;
import com.videopolis.calm.model.ItemKey;

/**
 * Deletes an event
 * 
 * @author cfondacci
 *
 */
public class EventDeleteAction extends AbstractAction implements JsonProvider {

	// Constants
	private static final long serialVersionUID = 1L;

	// Services
	@Autowired
	@Qualifier("eventSeriesService")
	private CalExtendedPersistenceService eventSeriesService;

	private String eventKey;

	@Override
	protected String doExecute() throws Exception {
		final ItemKey itemKey = CalmFactory.parseKey(eventKey);
		if (!EventSeries.SERIES_CAL_ID.equals(itemKey.getType())) {
			throw new UnsupportedOperationException(
					"Only events series can be removed with this method");
		}
		ContextHolder.toggleWrite();
		eventSeriesService.delete(itemKey);
		//
		return SUCCESS;
	}

	@Override
	public String getJson() {
		final JsonStatus status = new JsonStatus();
		status.setError(false);
		return JSONObject.fromObject(status).toString();
	}

	public void setEventKey(String eventKey) {
		this.eventKey = eventKey;
	}

	public String getEventKey() {
		return eventKey;
	}
}
