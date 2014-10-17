package com.nextep.proto.action.model;

import com.nextep.proto.blocks.MapSupport;

/**
 * Interface that needs to be implemented for pages providing maps
 * 
 * @author cfondacci
 * 
 */
public interface MapAware {

	void setMapSupport(MapSupport mapSupport);

	MapSupport getMapSupport();
}
