package org.mcemperor.time;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.UnsupportedTemporalTypeException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

/**
 * Tests the methods of the DayOfWeekTime class.
 *
 * @author Maurits de Jong
 */
public class DayOfWeekTimeTest {

	@Test
	public void testUntilMethod() {
		LocalDateTime start = LocalDateTime.of(2020, 4, 29, 23, 0, 0);
		LocalDateTime end = LocalDateTime.of(2020, 5, 10, 12, 0, 0);
		long length = start.until(end, ChronoUnit.HOURS);
		long expected = 253; // 10 days and 13 hours
		assertEquals(expected, length);
	}

	@Test
	public void testNowMethod() {
		LocalTime time1 = LocalDateTime.now().toLocalTime();
		LocalTime time2 = DayOfWeekTime.now().getLocalTime();
		assertTrue(ChronoUnit.SECONDS.between(time1, time2) < 1);
	}

	@Test
	@SuppressWarnings("ThrowableResultIgnored")
	public void testOfMethodWithNullArgs() {
		assertThrows(NullPointerException.class, () -> DayOfWeekTime.of(null, null));
	}

	@Test
	public void testPlusMethodWithDurationParam() {
		DayOfWeekTime dowt = DayOfWeekTime.of(DayOfWeek.MONDAY, 13, 37)
			.plus(Duration.ofDays(10));
		DayOfWeekTime expected = DayOfWeekTime.of(DayOfWeek.THURSDAY, 13, 37);
		assertEquals(expected, dowt);
	}

	@Test
	public void testPlusMethodWithPeriodParam() {
		DayOfWeekTime dowt = DayOfWeekTime.of(DayOfWeek.MONDAY, 13, 37)
			.plus(Period.ofDays(10));
		DayOfWeekTime expected = DayOfWeekTime.of(DayOfWeek.THURSDAY, 13, 37);
		assertEquals(expected, dowt);
	}

	@Test
	public void testPlusDaysMethodWith14() {
		DayOfWeekTime dowt = DayOfWeekTime.of(DayOfWeek.MONDAY, 13, 37);
		// Intentional identity comparison
		assertTrue(dowt.plusDays(14) == dowt);
	}

	@Test
	public void testPlusDaysMethodWith12() {
		DayOfWeekTime dowt = DayOfWeekTime.of(DayOfWeek.MONDAY, 13, 37);
		DayOfWeekTime expected = DayOfWeekTime.of(DayOfWeek.SATURDAY, 13, 37);
		assertEquals(expected, dowt.plusDays(12));
	}

	@Test
	public void testMinusMethodWithDurationParam() {
		DayOfWeekTime dowt = DayOfWeekTime.of(DayOfWeek.MONDAY, 13, 37)
			.minus(Duration.ofHours(14));
		DayOfWeekTime expected = DayOfWeekTime.of(DayOfWeek.SUNDAY, 23, 37);
		assertEquals(expected, dowt);
	}

	@Test
	public void testMinusMethodWithPeriodParam() {
		DayOfWeekTime dowt = DayOfWeekTime.of(DayOfWeek.MONDAY, 13, 37)
			.minus(Period.ofDays(16));
		DayOfWeekTime expected = DayOfWeekTime.of(DayOfWeek.SATURDAY, 13, 37);
		assertEquals(expected, dowt);
	}

	@Test
	public void testMinusDaysMethodWith12() {
		DayOfWeekTime dowt = DayOfWeekTime.of(DayOfWeek.MONDAY, 13, 37);
		DayOfWeekTime expected = DayOfWeekTime.of(DayOfWeek.WEDNESDAY, 13, 37);
		assertEquals(expected, dowt.minusDays(12));
	}

	@Test
	public void testMinusDaysMethodWith14() {
		DayOfWeekTime dowt = DayOfWeekTime.of(DayOfWeek.MONDAY, 13, 37);
		assertTrue(dowt.minusDays(14) == dowt);
	}

	@Test
	public void testGetDayOfWeekMethod() {
		DayOfWeekTime dowt = DayOfWeekTime.of(DayOfWeek.MONDAY, 13, 37);
		DayOfWeek expected = DayOfWeek.MONDAY;
		assertEquals(expected, dowt.getDayOfWeek());
	}

	@Test
	public void testGetLocalTimeMethod() {
		DayOfWeekTime dowt = DayOfWeekTime.of(DayOfWeek.MONDAY, 10, 39);
		LocalTime expected = LocalTime.of(10, 39);
		assertEquals(expected, dowt.getLocalTime());
	}

	@Test
	public void testIsSupported() {
		DayOfWeekTime dowt = DayOfWeekTime.of(DayOfWeek.WEDNESDAY, 13, 37);
		assertTrue(dowt.isSupported(ChronoField.DAY_OF_WEEK));
		assertFalse(dowt.isSupported(ChronoField.YEAR_OF_ERA));
	}

	@Test
	public void testGetLongWithDayOfWeekParam() {
		DayOfWeekTime dowt = DayOfWeekTime.of(DayOfWeek.WEDNESDAY, 13, 37);
		assertEquals(3L, dowt.getLong(ChronoField.DAY_OF_WEEK));
	}

	@Test
	public void testGetLongWithHourOfDayParam() {
		DayOfWeekTime dowt = DayOfWeekTime.of(DayOfWeek.WEDNESDAY, 13, 37);
		assertEquals(13L, dowt.getLong(ChronoField.HOUR_OF_DAY));
	}

	@Test
	@SuppressWarnings("ThrowableResultIgnored")
	public void testGetLongWithDayOfMonthParam() {
		DayOfWeekTime dowt = DayOfWeekTime.of(DayOfWeek.WEDNESDAY, 13, 37);
		assertThrows(UnsupportedTemporalTypeException.class, () -> dowt.getLong(ChronoField.DAY_OF_MONTH));
	}

	@Test
	public void testCompareTo() {
		DayOfWeekTime monday = DayOfWeekTime.of(DayOfWeek.MONDAY, 13, 37);
		DayOfWeekTime wednesday = DayOfWeekTime.of(DayOfWeek.WEDNESDAY, 13, 37);
		assertTrue(monday.compareTo(wednesday) < 0);
	}

	@Test
	public void testEquals() {
		DayOfWeekTime dowt = DayOfWeekTime.of(DayOfWeek.MONDAY, 13, 37);
		assertTrue(dowt.equals(dowt));
		assertFalse(dowt.equals(new Object()));
	}

	@Test
	public void testHashCode() {
		DayOfWeekTime dowt = DayOfWeekTime.of(DayOfWeek.WEDNESDAY, 13, 37);
		assertTrue(dowt.hashCode() != 0);
	}
}
