package com.videopolis.smaug.common.model.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.videopolis.smaug.common.model.FacetRange;
import com.videopolis.smaug.common.model.base.AbstractFacet;

/**
 * Default implementation of {@link FacetRange}
 *
 * @author julien
 *
 */
public class FacetRangeImpl extends AbstractFacet implements FacetRange {

	private static final Log LOGGER = LogFactory.getLog(FacetRangeImpl.class);

	/** Lower bound of the range */
	private String lowerBound = "*";

	/** Higher bound of the range */
	private String higherBound = "*";

	/**
	 * @param lowerBound
	 *            the lowerBound to set
	 */
	public void setLowerBound(final long lowerBound) {
		this.lowerBound = String.valueOf(lowerBound);
	}

	public void setLowerBoundExpr(String lowerBound) {
		this.lowerBound = lowerBound;
	}

	/**
	 * @param higherBound
	 *            the higherBound to set
	 */
	public void setHigherBound(final long higherBound) {
		this.higherBound = String.valueOf(higherBound + 1);
	}

	public void setHigherBoundExpr(String higherBound) {
		this.higherBound = higherBound;
	}

	@Override
	public String getRangeFormat() {
		return "[%s TO %s]";
	}

	/**
	 * @param rangeFormat
	 *            the rangeFormat to set
	 */
	public void setRangeFormat(final String rangeFormat) {
		// Does nothing
	}

	@Override
	public String getFacetCode() {
		return String.format(getRangeFormat(), lowerBound, higherBound);
	}

	@Override
	public long getLowerBound() {
		try {
			return Long.valueOf(lowerBound);
		} catch (Exception e) {
			LOGGER.error("Unable to convert FacetRange lowerBound '"
					+ lowerBound + "' to long: " + e.getMessage(), e);
		}
		return 0;
	}

	@Override
	public long getHigherBound() {
		try {
			return Long.valueOf(higherBound);
		} catch (Exception e) {
			LOGGER.error("Unable to convert FacetRange higherBound '"
					+ higherBound + "' to long: " + e.getMessage(), e);
		}
		return 0;
	}

	@Override
	public String getLowerBoundCode() {
		return lowerBound;
	}

	@Override
	public String getHigherBoundCode() {
		return higherBound;
	}

	@Override
	public String toString() {
		return super.toString() + " range from " + lowerBound + " to "
				+ higherBound + " (" + getFacetCode() + ")";
	}
}
