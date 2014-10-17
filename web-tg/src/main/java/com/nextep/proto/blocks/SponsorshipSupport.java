package com.nextep.proto.blocks;

import java.util.List;
import java.util.Locale;

import com.nextep.geo.model.GeographicItem;
import com.nextep.proto.services.UrlService;
import com.nextep.users.model.User;
import com.videopolis.calm.model.CalmObject;

public interface SponsorshipSupport {

	void initialize(Locale locale, UrlService urlService,
			CalmObject sponsoredObject, User currentUser);

	/**
	 * Exposes the object being sponsored
	 * 
	 * @return
	 */
	CalmObject getObject();

	/**
	 * Informs whether the user can perform sponsorship
	 * 
	 * @return
	 */
	boolean canSponsor();

	/**
	 * Provides the label to display for sponsorship
	 * 
	 * @return
	 */
	String getSponsorshipLabel();

	/**
	 * Provides the label informing of the number of credits the user currently
	 * has
	 * 
	 * @return the credits label
	 */
	String getCreditsLabel();

	/**
	 * Provides a list of duration that the user can subscribe to
	 * 
	 * @return
	 */
	List<Integer> getAvailableSponsorshipDuration();

	/**
	 * Provides the label for a given duration option
	 * 
	 * @param duration
	 *            the duration in months
	 * @return the corresponding option label
	 */
	String getDurationLabel(int duration);

	/**
	 * Provides the URL of the form validation
	 * 
	 * @return
	 */
	String getValidationUrl();

	List<GeographicItem> getSampleItems();

	String getSampleItemPriceLabel(GeographicItem item);

	/**
	 * Provides the default cost of one month subscription as a list of
	 * available options (depends on the current credits of the user)
	 * 
	 * @return a list of available month credits
	 */
	List<Integer> getAvailableMonthCredits();

	/**
	 * Provides the label to display to the user as the credits spend for one
	 * month
	 * 
	 * @param credits
	 *            the number of credits
	 * @return the label of this option
	 */
	String getMonthCreditsLabel(int credits);

	/**
	 * Provides the transaction ID for this payment (avoids double payment of a
	 * same transaction ID)
	 * 
	 * @return the transaction ID
	 */
	String getTransactionId();

	/**
	 * Provides the list of sponsored elements
	 * 
	 * @return the list of currently sponsored {@link CalmObject}
	 */
	List<CalmObject> getSponsoredElements();

	/**
	 * Provides the name of the sponsored element
	 * 
	 * @param object
	 *            the element to sponsor
	 * @return the name to display
	 */
	String getSponsoredElementName(CalmObject object);

	/**
	 * Provides the link to the sponsored element
	 * 
	 * @param object
	 *            the sponsored{@link CalmObject}
	 * @return the URL
	 */
	String getSponsoredElementUrl(CalmObject object);

	/**
	 * Provides the URL of the icon for the specified sponsored element
	 * 
	 * @param object
	 *            the element to sponsor
	 * @return the URL of the icon of this element
	 */
	String getSponsoredElementIcon(CalmObject object);
}
