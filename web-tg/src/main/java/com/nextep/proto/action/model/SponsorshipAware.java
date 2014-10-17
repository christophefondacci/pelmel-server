package com.nextep.proto.action.model;

import com.nextep.proto.blocks.SponsorshipSupport;

/**
 * An interface defining actions that can provide sponsorhsip information
 * 
 * @author cfondacci
 * 
 */
public interface SponsorshipAware {

	/**
	 * Exposes the sponsorship support
	 * 
	 * @return the {@link SponsorshipSupport} implementation
	 */
	SponsorshipSupport getSponsorshipSupport();

	/**
	 * Installs the support for sponsorship
	 * 
	 * @param support
	 *            the {@link SponsorshipSupport} implementation
	 */
	void setSponsorshipSupport(SponsorshipSupport support);
}
