package com.videopolis.smaug.model.impl;

import com.videopolis.smaug.common.model.Facet;
import com.videopolis.smaug.model.FacetCount;

/**
 * Implementation of {@link FacetCount}
 * 
 * @author mehdi BEN HAJ ABBES
 * @since 05 Jan 2011
 * 
 */
public class FacetCountImpl implements FacetCount {

	/** The facet. */
	private Facet facet;
	/** The count of facet. */
	private int count;

	/**
	 * Default Ctor
	 */
	public FacetCountImpl() {
		super();
	}

	/**
	 * Ctor with parameters
	 * 
	 * @param facet
	 *            The facet to set
	 * @param count
	 *            The count of given facet
	 */
	public FacetCountImpl(final Facet facet, final int count) {
		super();
		this.facet = facet;
		this.count = count;
	}

	@Override
	public Facet getFacet() {
		return facet;
	}

	/**
	 * Setter for facet
	 * 
	 * @param facet
	 *            The facet to set
	 */
	public void setFacet(final Facet facet) {
		this.facet = facet;
	}

	@Override
	public int getCount() {
		return count;
	}

	/**
	 * Setter for count of facet
	 * 
	 * @param count
	 *            The count to set
	 */
	public void setCount(final int count) {
		this.count = count;
	}

	@Override
	public String toString() {
		return "[facet=" + facet + ", count=" + count + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + count;
		result = prime * result + ((facet == null) ? 0 : facet.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final FacetCountImpl other = (FacetCountImpl) obj;
		if (count != other.count) {
			return false;
		}
		if (facet == null) {
			if (other.facet != null) {
				return false;
			}
		} else if (!facet.equals(other.facet)) {
			return false;
		}
		return true;
	}

}
