package com.nextep.proto.services.impl;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;

import org.springframework.context.MessageSource;

import com.nextep.proto.services.DistanceDisplayService;
import com.videopolis.apis.model.SearchStatistic;
import com.videopolis.apis.service.ApiResponse;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.unit.model.MeasuredValue;
import com.videopolis.unit.model.Order;
import com.videopolis.unit.model.UnitSystem;
import com.videopolis.unit.service.DistanceService;

public class DistanceDisplayServiceImpl implements DistanceDisplayService {

	private final static BigDecimal BIG_ONE = BigDecimal.ONE;
	private final static BigDecimal BIG_THOUSAND = new BigDecimal("1000");
	private final static String KEY_SECONDS = "time.seconds";
	private final static String KEY_MINUTES = "time.minutes";
	private final static String KEY_HOURS = "time.hours";
	private final static String KEY_DAYS = "time.days";
	private DistanceService distanceService;
	private final static String KEY_SECONDS_FULL = "time.full.seconds";
	private final static String KEY_MINUTES_FULL = "time.full.minutes";
	private final static String KEY_HOURS_FULL = "time.full.hours";
	private final static String KEY_DAYS_FULL = "time.full.days";
	private final static String KEY_MONTHS_FULL = "time.full.months";
	private final static String KEY_YEARS_FULL = "time.full.years";
	private final static String KEY_SECOND_FULL = "time.full.second";
	private final static String KEY_MINUTE_FULL = "time.full.minute";
	private final static String KEY_HOUR_FULL = "time.full.hour";
	private final static String KEY_DAY_FULL = "time.full.day";
	private final static String KEY_MONTH_FULL = "time.full.month";
	private final static String KEY_YEAR_FULL = "time.full.year";
	private MessageSource messageSource;

	@Override
	public String getDistanceToDisplay(Number pivotDistance, Locale locale) {
		final UnitSystem unitSystem = getUnitSystem(locale);

		// Distance conversion : Pivot => Measurement from tld
		final BigDecimal distance = new BigDecimal(pivotDistance.toString());

		// Retrieving ascending values
		Collection<MeasuredValue> convertDistances = distanceService
				.convertToSystem(distanceService.getPivot(), unitSystem,
						distance, Order.RELATIVE_VALUE_ASCENDING);
		String displayedDistance = null;
		for (MeasuredValue d : convertDistances) {
			// Since we got ascending values, the very first one superior to 1
			// is the one we want to display
			if (d.getValue().compareTo(BIG_ONE) > 0) {
				displayedDistance = getDisplayableDistance(d, locale);
				break;
			}
		}
		// We should never fall here, but if so we return the first match
		if (displayedDistance == null) {
			if (!convertDistances.isEmpty()) {
				displayedDistance = getDisplayableDistance(convertDistances
						.iterator().next(), locale);
			} else {
				// Conversion failed, returning distance string alone
				displayedDistance = distance.toString();
			}
		}
		return displayedDistance;
	}

	private static String getDisplayableDistance(MeasuredValue distance,
			Locale l) {
		final String unit = distance.getUnit().getCode();
		NumberFormat nf = NumberFormat.getNumberInstance(l);
		// Small rates : using 2 digits, else 0 (this avoids to rely on
		// explicit units, but we still need a "magic" 1000 number
		if (distance.getUnit().getRate().compareTo(BIG_THOUSAND) < 0) {
			nf.setMaximumFractionDigits(1);
		} else {
			nf.setMaximumFractionDigits(0);
		}
		final String value = nf.format(distance.getValue());
		return (value + ' ' + unit);
	}

	@Override
	public String getDistanceFromItem(ItemKey key, ApiResponse response,
			Locale locale) {
		final SearchStatistic distanceStat = response.getStatistic(key,
				SearchStatistic.DISTANCE);
		if (distanceStat != null) {
			final String displayedStr = getDistanceToDisplay(
					distanceStat.getNumericValue(), locale);
			return displayedStr;
		} else {
			return "";
		}
	}

	@Override
	public UnitSystem getUnitSystem(Locale locale) {
		return distanceService.getSystem("si");
	}

	public void setDistanceService(DistanceService distanceService) {
		this.distanceService = distanceService;
	}

	@Override
	public String getTimeBetweenDates(Date fromDate, Date toDate, Locale locale) {
		return getTimeBetweenDates(fromDate, toDate, locale, true);
	}

	@Override
	public String getTimeBetweenDates(Date fromDate, Date toDate,
			Locale locale, boolean isCompact) {
		if (fromDate == null || toDate == null) {
			return null;
		}
		long delta = Math.abs(fromDate.getTime() - toDate.getTime());

		long value;
		String label = null;
		if (delta < 60000) {
			value = delta / 1000;
			final String fullLabel = value == 1 ? KEY_SECOND_FULL
					: KEY_SECONDS_FULL;
			label = messageSource.getMessage(isCompact ? KEY_SECONDS
					: fullLabel, new Object[] { value }, locale);
		} else if (delta < 3600000) {
			// Display in minutes
			value = delta / 60000;
			final String fullLabel = value == 1 ? KEY_MINUTE_FULL
					: KEY_MINUTES_FULL;
			label = messageSource.getMessage(isCompact ? KEY_MINUTES
					: fullLabel, new Object[] { value }, locale);
		} else if (delta < 86400000) {
			// Display in hours
			value = delta / 3600000;
			final String fullLabel = value == 1 ? KEY_HOUR_FULL
					: KEY_HOURS_FULL;
			label = messageSource.getMessage(isCompact ? KEY_HOURS : fullLabel,
					new Object[] { value }, locale);
		} else if (delta < 2592000000l) {
			// Display in days
			value = delta / 86400000;
			final String fullLabel = value == 1 ? KEY_DAY_FULL : KEY_DAYS_FULL;
			label = messageSource.getMessage(isCompact ? KEY_DAYS : fullLabel,
					new Object[] { value }, locale);
		} else if (delta < 31557600000l) {
			// Display in months
			value = delta / 2592000000l;
			final String fullLabel = value == 1 ? KEY_MONTH_FULL
					: KEY_MONTHS_FULL;
			label = messageSource.getMessage(fullLabel, new Object[] { value },
					locale);
		} else {
			// Display in years
			value = delta / 31557600000l;
			final String fullLabel = value == 1 ? KEY_YEAR_FULL
					: KEY_YEARS_FULL;
			label = messageSource.getMessage(fullLabel, new Object[] { value },
					locale);

		}
		return label;
	}

	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}
}
