package org.mcemperor.time;

import java.io.Serializable;
import java.time.*;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.TemporalField;
import java.time.temporal.UnsupportedTemporalTypeException;
import java.util.Objects;

/**
 * A combination of a day-of-week and time without timezone, in the ISO-8601 calendar system.
 * <p>
 * {@code DayOfWeekTime} is an immutable object representing a day-of-week and time combination, such as "Monday at
 * 12:30". This class does not store or represent a date or a timezone. Instead, it describes the combination
 * day-of-week and the time as seen on the wall clock, and could be used for weekly schedule entries. It cannot
 * represent an instant on the time-line without additional information such as a date, a timezone offset or timezone.
 * <p>
 * This is a <em>value-based</em> class; use of identity-sensitive operations (including reference equality
 * ({@code ==}), identity hash code, or synchronization) on instances of {@code DayOfWeekTime} may have unpredictable
 * results and should be avoided. The {@code equals} method should be used for comparisons.
 *
 * @implSpec This class is immutable and thread-safe.
 *
 * @author Basil Bourque, Maurits de Jong
 */
public class DayOfWeekTime implements TemporalAccessor, Comparable<DayOfWeekTime>, Serializable {

	/**
	 * The day of week.
	 */
	private final DayOfWeek dayOfWeek;

	/**
	 * The time.
	 */
	private final LocalTime localTime;

	/**
	 * We do the date-time math by picking a date arbitrarily to use as a `LocalDateTime`. For convenience, we might as
	 * well pick a year that starts on a Monday. See https://en.wikipedia.org/wiki/Common_year_starting_on_Monday. Let
	 * us go with 2001-01-01.
	 */
	private static final LocalDateTime BASELINE = LocalDateTime.of(2001, 1, 1, 0, 0, 0, 0);

	/**
	 * Constructs a DayOfWeekTime instance with the given day-of-week and local time.
	 *
	 * @param dayOfWeek the day of week, not null
	 * @param localTime the local time, not null
	 */
	private DayOfWeekTime(DayOfWeek dayOfWeek, LocalTime localTime) {
		Objects.requireNonNull(dayOfWeek, "dayOfWeek");
		Objects.requireNonNull(localTime, "localTime");
		this.dayOfWeek = dayOfWeek;
		this.localTime = localTime;
	}

	/**
	 * Obtains an instance of DayOfWeekTime from a day-of-week and time.
	 *
	 * This returns a DayOfWeekTime with the specified day-of-week and time.
	 *
	 * @param dayOfWeek the day of week
	 * @param localTime the local time
	 * @return an instance wrapping the combination of day-of-week and time, not null
	 */
	public static DayOfWeekTime of(DayOfWeek dayOfWeek, LocalTime localTime) {
		Objects.requireNonNull(dayOfWeek, "dayOfWeek");
		Objects.requireNonNull(localTime, "localTime");
		return new DayOfWeekTime(dayOfWeek, localTime);
	}

	/**
	 * Obtains an instance of DayOfWeekTime from a day-of-week, an hour and a minute.This returns a DayOfWeekTime with
	 * the specified day-of-week, hour and minute.
	 *
	 * All fields representing a smaller unit than a minute are set to zero.
	 *
	 * @param dayOfWeek the day of week
	 * @param hour the hour
	 * @param minute the minute
	 * @return an instance wrapping the combination of day-of-week, hour and minute, not null
	 */
	public static DayOfWeekTime of(DayOfWeek dayOfWeek, int hour, int minute) {
		return of(dayOfWeek, LocalTime.of(hour, minute));
	}

	/**
	 * Obtains the current day-of-week-time from the system clock.
	 *
	 * This will query the system clock to obtain the current date.
	 *
	 * Using this method will prevent the ability to use an alternate clock for testing because the clock is hard-coded.
	 *
	 * @return the current day-of-week-time using the system clock, not null
	 */
	public static DayOfWeekTime now() {
		LocalDateTime now = LocalDateTime.now();
		return DayOfWeekTime.of(now.getDayOfWeek(), now.toLocalTime());
	}

	/**
	 * Compares this DayOfWeekTime with the given one.
	 *
	 * The comparison is based on the time-line position of the combination of day-of-week and local times within the
	 * day. It is "consistent with equals", as defined by {@link Comparable}.
	 *
	 * @param other the other instance to compare, not null
	 * @return a negative integer, zero, or a positive integer as this object is less than, equal to, or greater than
	 * the specified object respectively
	 */
	@Override
	public int compareTo(DayOfWeekTime other) {
		int cmp = this.dayOfWeek.compareTo(other.dayOfWeek);
		return (cmp == 0 ? this.localTime.compareTo(other.localTime) : cmp);
	}

	/**
	 * Returns a copy of this day-of-week-time with the specified duration added.
	 *
	 * This instance is immutable and unaffected by this method call.
	 *
	 * @param duration the duration to add
	 * @return a new DayOfWeekTime instance with the duration added
	 */
	public DayOfWeekTime plus(Duration duration) {
		Objects.requireNonNull(duration, "duration");
		LocalDateTime ldt = DayOfWeekTime.BASELINE
			.with(TemporalAdjusters.nextOrSame(this.dayOfWeek))
			.with(this.localTime);
		LocalDateTime ldtSum = ldt.plus(duration);
		return DayOfWeekTime.of(ldtSum.getDayOfWeek(), ldtSum.toLocalTime());
	}

	/**
	 * Returns a copy of this day-of-week-time with the specified period added.
	 *
	 * This instance is immutable and unaffected by this method call.
	 *
	 * @param period the period to add
	 * @return a new DayOfWeekTime instance with the period added
	 */
	public DayOfWeekTime plus(Period period) {
		Objects.requireNonNull(period, "period");
		LocalDateTime ldt = DayOfWeekTime.BASELINE
			.with(TemporalAdjusters.nextOrSame(this.dayOfWeek))
			.with(this.localTime);
		LocalDateTime ldtSum = ldt.plus(period);
		return DayOfWeekTime.of(ldtSum.getDayOfWeek(), ldtSum.toLocalTime());
	}

	/**
	 * Returns a copy of this {@code DayOfWeek} with the specified number of days added.
	 *
	 * This method adds the specified amount to the days field, leaving the time unaffected.
	 *
	 * For example, MONDAY, 15:33 plus two days would result in WEDNESDAY, 15:33.
	 *
	 * @param daysToAdd the days to add, may be negative
	 * @return a {@code DayOfWeekTime} based on this date with the days added, not null
	 */
	public DayOfWeekTime plusDays(long daysToAdd) {
		long netDaysToAdd = daysToAdd % 7;
		return (netDaysToAdd == 0 ? this : of(this.dayOfWeek.plus(netDaysToAdd), this.localTime));
	}

	/**
	 * Returns a copy of this day-of-week-time with the specified duration subtracted.
	 *
	 * This instance is immutable and unaffected by this method call.
	 *
	 * @param duration the duration to subtract
	 * @return a new DayOfWeekTime instance with the duration subtracted
	 */
	public DayOfWeekTime minus(Duration duration) {
		Objects.requireNonNull(duration, "duration");
		LocalDateTime ldt = DayOfWeekTime.BASELINE
			.with(TemporalAdjusters.nextOrSame(this.dayOfWeek))
			.with(this.localTime);
		LocalDateTime ldtSum = ldt.minus(duration);
		return DayOfWeekTime.of(ldtSum.getDayOfWeek(), ldtSum.toLocalTime());
	}

	/**
	 * Returns a copy of this day-of-week-time with the specified period subtracted.
	 *
	 * This instance is immutable and unaffected by this method call.
	 *
	 * @param period the period to subtract
	 * @return a new DayOfWeekTime instance with the period subtracted
	 */
	public DayOfWeekTime minus(Period period) {
		Objects.requireNonNull(period, "period");
		LocalDateTime ldt = DayOfWeekTime.BASELINE
			.with(TemporalAdjusters.nextOrSame(this.dayOfWeek))
			.with(this.localTime);
		LocalDateTime ldtSum = ldt.minus(period);
		return DayOfWeekTime.of(ldtSum.getDayOfWeek(), ldtSum.toLocalTime());
	}

	/**
	 * Returns a copy of this {@code DayOfWeek} with the specified number of days subtracted.
	 *
	 * This method subtracts the specified amount from the days field, leaving the time unaffected.
	 *
	 * For example, WEDNESDAY, 15:33 minus two days would result in MONDAY, 15:33.
	 *
	 * @param daysToSubtract the days to subtract, may be negative
	 * @return a {@code DayOfWeekTime} based on this date with the days subtracted, not null
	 */
	public DayOfWeekTime minusDays(long daysToSubtract) {
		long netDaysToSubtract = daysToSubtract % 7;
		return (netDaysToSubtract == 0 ? this : of(this.dayOfWeek.minus(netDaysToSubtract), this.localTime));
	}

	/**
	 * Checks if the specified field is supported.
	 *
	 * @param field the field to check
	 * @return true if the field is supported
	 */
	@Override
	public boolean isSupported(TemporalField field) {
		if (field instanceof ChronoField) {
			switch ((ChronoField) field) {
				case NANO_OF_SECOND:
				case NANO_OF_DAY:
				case MICRO_OF_SECOND:
				case MICRO_OF_DAY:
				case MILLI_OF_SECOND:
				case MILLI_OF_DAY:
				case SECOND_OF_MINUTE:
				case SECOND_OF_DAY:
				case MINUTE_OF_HOUR:
				case MINUTE_OF_DAY:
				case HOUR_OF_AMPM:
				case CLOCK_HOUR_OF_AMPM:
				case HOUR_OF_DAY:
				case CLOCK_HOUR_OF_DAY:
				case AMPM_OF_DAY:
				case DAY_OF_WEEK:
					return true;
			}
		}
		return false;
	}

	/**
	 * Gets the value of the specified field from this time as a {@code long}.
	 *
	 * This queries this time for the value of the specified field. If it is not possible to return the value, because
	 * the field is not supported or for some other reason, an exception is thrown.
	 *
	 * @param field the field to get, not null
	 * @return the value for the field
	 * @throws DateTimeException if a value for the field cannot be obtained
	 * @throws UnsupportedTemporalTypeException if the field is not supported
	 */
	@Override
	public long getLong(TemporalField field) {
		if (field instanceof ChronoField) {
			switch ((ChronoField) field) {
				case NANO_OF_SECOND:
				case NANO_OF_DAY:
				case MICRO_OF_SECOND:
				case MICRO_OF_DAY:
				case MILLI_OF_SECOND:
				case MILLI_OF_DAY:
				case SECOND_OF_MINUTE:
				case SECOND_OF_DAY:
				case MINUTE_OF_HOUR:
				case MINUTE_OF_DAY:
				case HOUR_OF_AMPM:
				case CLOCK_HOUR_OF_AMPM:
				case HOUR_OF_DAY:
				case CLOCK_HOUR_OF_DAY:
				case AMPM_OF_DAY:
					return this.localTime.getLong(field);
				case DAY_OF_WEEK:
					return this.dayOfWeek.getValue();
			}
		}
		throw new UnsupportedTemporalTypeException("Unsupported field: " + field);
	}

	/**
	 * Gets the day of week.
	 *
	 * @return the day of week
	 */
	public DayOfWeek getDayOfWeek() {
		return this.dayOfWeek;
	}

	/**
	 * Gets the local time.
	 *
	 * @return the local time
	 */
	public LocalTime getLocalTime() {
		return this.localTime;
	}

	/**
	 * Checks if this day-of-week-time is equal to another day-of-week-time.
	 *
	 * Compares this DayOfWeekTime with another ensuring that both day-of-week/time combinations are the same.
	 *
	 * Only objects of type DayOfWeekTime are compared, other types return false. To compare the dates of two
	 * TemporalAccessor instances, including dates in two different chronologies, use ChronoField.EPOCH_DAY as a
	 * comparator.
	 *
	 * @param o The object to check
	 * @return true if this is equal to the other day-of-week-time
	 */
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		else if (o == null || getClass() != o.getClass()) {
			return false;
		}
		else {
			DayOfWeekTime that = (DayOfWeekTime) o;
			return this.dayOfWeek == that.dayOfWeek && this.localTime.equals(that.localTime);
		}
	}

	/**
	 * Returns the hashcode for this instance.
	 *
	 * @return the hashcode.
	 */
	@Override
	public int hashCode() {
		return Objects.hash(this.dayOfWeek, this.localTime);
	}
}
