package com.nextep.proto.spring;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

public class ReadWriteDataSourceRouter extends AbstractRoutingDataSource {

	private static final Log LOGGER = LogFactory
			.getLog(ReadWriteDataSourceRouter.class);

	@Override
	protected Object determineCurrentLookupKey() {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("===== Transaction is "
					+ (!ContextHolder.isWritable() ? "READ-ONLY" : "WRITE")
					+ " ======");
		}
		return ContextHolder.isWritable() ? "WRITE" : "READ";
	}
}
