package neXtep.web_proto;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import junit.framework.Assert;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

import com.nextep.events.model.MutableEvent;
import com.nextep.events.model.impl.EventImpl;
import com.nextep.events.model.impl.EventSeriesImpl;
import com.nextep.proto.services.EventManagementService;
import com.nextep.proto.services.impl.EventManagementServiceImpl;

public class EventSeriesTest {
	private static final Log LOGGER = LogFactory.getLog(EventSeriesTest.class);

	@Test
	public void testEventSeries() {
		final EventManagementService eventManagementService = new EventManagementServiceImpl();
		Calendar c = Calendar.getInstance();
		c.set(Calendar.DATE, 6);
		c.set(Calendar.MONTH, 5);
		c.set(Calendar.YEAR, 2012);
		c.set(Calendar.HOUR_OF_DAY, 22);
		c.set(Calendar.MINUTE, 0);
		c.add(Calendar.MONTH, -1);
		EventSeriesImpl series = new EventSeriesImpl();
		series.setStartDate(c.getTime());
		c.add(Calendar.MONTH, 4);
		c.set(Calendar.HOUR_OF_DAY, 7);
		c.set(Calendar.MINUTE, 30);
		series.setEndDate(c.getTime());

		series.setTuesday(true);
		series.setWeekOfMonthOffset(1);

		System.out.println("Computing next date 1st tuesday of month");
		c.set(Calendar.DATE, 6);
		c.set(Calendar.MONTH, 5);
		c.set(Calendar.YEAR, 2012);
		Date d = eventManagementService.computeNextStart(series, c.getTime(),
				true);
		System.out.println("  => " + d);

		series.setWeekOfMonthOffset(null);
		System.out.println("Computing next date every tuesday");
		d = eventManagementService.computeNextStart(series, c.getTime(), true);
		System.out.println("  => " + d);

		series.setWednesday(true);
		System.out
				.println("Computing next date every tuesday & wednesdays of month");
		d = eventManagementService.computeNextStart(series, c.getTime(), true);
		System.out.println("  => " + d);

		series.setWeekOfMonthOffset(3);
		System.out
				.println("Computing next END date 3rd tuesday & wednesdays of month");
		d = eventManagementService.computeNextStart(series, c.getTime(), false);
		System.out.println("  => " + d);

		// Computing series
		c = Calendar.getInstance();
		c.set(Calendar.DATE, 6);
		c.set(Calendar.MONTH, 5);
		c.set(Calendar.YEAR, 2012);
		Date date = c.getTime();
		Date nextEventStart = date;
		System.out.println("Computing full series from " + date);
		final Calendar timeCal = Calendar.getInstance();
		while (nextEventStart != null) {
			nextEventStart = eventManagementService.computeNextStart(series,
					date, true);
			if (nextEventStart != null) {
				Date nextEventEnd = eventManagementService.computeNextStart(
						series, date, false);
				System.out.println(" -> Next event starts on " + nextEventStart
						+ " / ends on " + nextEventEnd);
				// Creating next event in serie
				final MutableEvent newEvent = new EventImpl();
				EventManagementServiceImpl.fillEventFromSeries(series,
						newEvent, nextEventStart, nextEventEnd);
				// Persisting
				timeCal.setTime(nextEventEnd);
				timeCal.add(Calendar.HOUR, 1);
				date = timeCal.getTime();
			}
		}
	}

	@Test
	public void testTimeZones() {
		Calendar calendar = Calendar.getInstance();
		System.out.println(calendar.getTime());
		System.out.println(calendar.getTime().getTime());
		final Date localDate = calendar.getTime();
		TimeZone fromTimeZone = calendar.getTimeZone();
		TimeZone toTimeZone = TimeZone.getTimeZone("America/Los_Angeles");

		calendar.setTimeZone(fromTimeZone);
		System.out.println(calendar.getTime());

		calendar.add(Calendar.MILLISECOND, fromTimeZone.getRawOffset() * -1);
		if (fromTimeZone.inDaylightTime(calendar.getTime())) {
			calendar.add(Calendar.MILLISECOND, calendar.getTimeZone()
					.getDSTSavings() * -1);
		}

		calendar.add(Calendar.MILLISECOND, toTimeZone.getRawOffset());
		if (toTimeZone.inDaylightTime(calendar.getTime())) {
			calendar.add(Calendar.MILLISECOND, toTimeZone.getDSTSavings());
		}

		// calendar.setTimeZone(toTimeZone);
		System.out.println(calendar.getTime());
		System.out.println(calendar.getTime().getTime());
		final Date laDate = calendar.getTime();

		EventSeriesImpl series = new EventSeriesImpl();
		series.setStartDate(new Date(System.currentTimeMillis() - 1000000000));
		series.setEndDate(new Date(System.currentTimeMillis() + 1000000000));
		System.out.println("Series valid from " + series.getStartDate()
				+ " to " + series.getEndDate());
		series.setMonday(true);
		series.setThursday(true);
		series.setFriday(true);
		series.setSunday(true);
		series.setStartHour(19);
		series.setStartMinute(0);
		series.setEndHour(22);
		series.setEndMinute(30);

		EventManagementService service = new EventManagementServiceImpl();
		System.out.println(service.buildReadableTimeframe(series,
				Locale.ENGLISH));
		final Date localStart = service.computeNextStart(series, localDate,
				true);
		final Date laStart = service.computeNextStart(series, laDate, true);
		System.out.println("Local next start:" + localStart
				+ " (Starting from " + localDate + ")");
		System.out.println("Los Angeles next start:" + laStart
				+ " (Starting from " + laDate + ")");

		System.out.println("");

		final Date next = service.computeNext(series, "America/Los_Angeles",
				true);
		System.out.println("Los Angeles next start IN LOCAL TIME:" + next);
	}

	@Test
	public void testCalendars() {
		final EventManagementService service = new EventManagementServiceImpl();
		final Date now = service.getLocalizedNow("America/Los_Angeles");
		LOGGER.info(now + " - " + now.getTime());
		final Date date = new Date();
		LOGGER.info(date + " - " + date.getTime());

	}

	@Test
	public void testTimes() {
		System.out.println(Calendar.getInstance().getTime().getTime());
		System.out.println(new Date().getTime());

		final Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 24);
		System.out.println(cal.getTime());
	}

	@Test
	public void fullSeriesTest() throws ParseException {
		final DateFormat f = new SimpleDateFormat("yyyy/MM/dd HH:mm");
		final EventManagementService eventManagementService = new EventManagementServiceImpl();

		// Date is 11pm
		Date currentDate = f.parse("2014/02/10 23:00");

		// Event series of event already started but ending the day after
		EventSeriesImpl series = new EventSeriesImpl();
		series.setMonday(true);
		series.setStartHour(20);
		series.setStartMinute(0);
		series.setEndHour(2);
		series.setEndMinute(0);

		// This should match, next start should be next monday 20:00, next end
		// should be day after at 2am
		Date expectedEnd = f.parse("2014/02/11 02:00");
		Date expectedStart = f.parse("2014/02/17 20:00");

		Date nextStart = eventManagementService.computeNextStart(series,
				currentDate, true);
		Date nextEnd = eventManagementService.computeNextStart(series,
				currentDate, false);

		Assert.assertEquals("Incorrect next start", f.format(expectedStart),
				f.format(nextStart));
		Assert.assertEquals("Incorrect next end", f.format(expectedEnd),
				f.format(nextEnd));

		// Now we set ourselves in a range where no day of series matches, but
		// the timeframe should match because of the end of previous period
		currentDate = f.parse("2014/02/11 01:00");

		// Recomputing
		nextStart = eventManagementService.computeNextStart(series,
				currentDate, true);
		nextEnd = eventManagementService.computeNextStart(series, currentDate,
				false);
		Assert.assertEquals("Incorrect next start", f.format(expectedStart),
				f.format(nextStart));
		Assert.assertEquals("Incorrect next end", f.format(expectedEnd),
				f.format(nextEnd));

		// Testing the midnight headache
		currentDate = f.parse("2014/02/10 23:00");
		series.setEndHour(24);
		expectedEnd = f.parse("2014/02/11 00:00");
		nextEnd = eventManagementService.computeNextStart(series, currentDate,
				false);
		Assert.assertEquals("Midnhight 24 : Incorrect next end",
				f.format(expectedEnd), f.format(nextEnd));

		// Starting at midnight ending at 2
		series.setStartHour(24);
		series.setEndHour(2);
		expectedStart = f.parse("2014/02/11 00:00");
		expectedEnd = f.parse("2014/02/11 02:00");

		nextStart = eventManagementService.computeNextStart(series,
				currentDate, true);
		nextEnd = eventManagementService.computeNextStart(series, currentDate,
				false);
		Assert.assertEquals("Incorrect next start", f.format(expectedStart),
				f.format(nextStart));
		Assert.assertEquals("Incorrect next end", f.format(expectedEnd),
				f.format(nextEnd));

	}
}
