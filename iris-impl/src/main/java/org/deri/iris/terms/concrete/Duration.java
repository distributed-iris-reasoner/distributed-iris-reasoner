/*
 * Integrated Rule Inference System (IRIS):
 * An extensible rule inference system for datalog with extensions.
 * 
 * Copyright (C) 2008 Semantic Technology Institute (STI) Innsbruck, 
 * University of Innsbruck, Technikerstrasse 21a, 6020 Innsbruck, Austria.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, 
 * MA  02110-1301, USA.
 */
package org.deri.iris.terms.concrete;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;

import org.deri.iris.api.terms.ITerm;
import org.deri.iris.api.terms.concrete.IDuration;

/**
 * <p>
 * Simple implementation of IDuration.
 * </p>
 */
public class Duration implements IDuration {

	/** Calendar used to calculate the milliseconds. */
	private static final Calendar ZERO;

	/** Factory used to create the xml durations. */
	private static final DatatypeFactory FACTORY;

	/** The inner duration object. */
	private final javax.xml.datatype.Duration mDuration;

	static {
		// creating the calendar
		ZERO = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
		ZERO.clear();

		try {
			FACTORY = DatatypeFactory.newInstance();
		} catch (DatatypeConfigurationException e) {
			throw new RuntimeException(
					"Couldn't create the factory for the duration", e);
		}
	}

	/**
	 * Constructs a new duration.
	 * 
	 * @param positive
	 *            <code>true</code>if the duration is positive, otherwise
	 *            <code>false</code>
	 * @param year
	 *            the yearspan
	 * @param month
	 *            the monthspan
	 * @param day
	 *            the dayspan
	 * @param hour
	 *            the hourspan
	 * @param minute
	 *            the minutespan
	 * @param second
	 *            the secondspan
	 * @param millisecond
	 *            the millisecondspan
	 */
	Duration(boolean positive, int year, int month, int day, int hour,
			int minute, int second, int millisecond) {
		this(positive, year, month, day, hour, minute, second
				+ (millisecond / 1000.0));

		if (millisecond < 0 || millisecond >= 1000)
			throw new IllegalArgumentException(
					"Millisecond value is out of range: " + second);

		if (second < 0 || second >= 60)
			throw new IllegalArgumentException("Second value is out of range: "
					+ second);
	}

	/**
	 * Constructs a new duration.
	 * 
	 * @param positive
	 *            <code>true</code>if the duration is positive, otherwise
	 *            <code>false</code>
	 * @param year
	 *            the year
	 * @param month
	 *            the month
	 * @param day
	 *            the day
	 * @param hour
	 *            the hour
	 * @param minute
	 *            the minute
	 * @param second
	 *            the second
	 */
	Duration(boolean positive, int year, int month, int day, int hour,
			int minute, double second) {
		if (year < 0) {
			throw new IllegalArgumentException("The year must not be negative");
		}
		if (month < 0) {
			throw new IllegalArgumentException("The month must not be negative");
		}
		if (day < 0) {
			throw new IllegalArgumentException("The day must not be negative");
		}
		if (hour < 0) {
			throw new IllegalArgumentException("The hour must not be negative");
		}
		if (minute < 0) {
			throw new IllegalArgumentException(
					"The minute must not be negative");
		}
		if (second < 0) {
			throw new IllegalArgumentException(
					"The second must not be negative");
		}

		mDuration = FACTORY.newDuration(positive, BigInteger.valueOf(year),
				BigInteger.valueOf(month), BigInteger.valueOf(day), BigInteger
						.valueOf(hour), BigInteger.valueOf(minute),
				new BigDecimal(Double.toString(second)));
	}

	/**
	 * Constructs a new duration out of a given amount of milliseconds. The
	 * milliseconds will be round down to the next second.
	 * 
	 * @param millis
	 *            the millisecondspan
	 */
	Duration(final long millis) {
		mDuration = FACTORY.newDuration(millis);
	}

	public int getYear() {
		return mDuration.getYears();
	}

	public int getMonth() {
		return mDuration.getMonths();
	}

	public int getDay() {
		return mDuration.getDays();
	}

	public int getHour() {
		return mDuration.getHours();
	}

	public int getMinute() {
		return mDuration.getMinutes();
	}

	public int getSecond() {
		return mDuration.getSeconds();
	}

	public int getMillisecond() {
		return Long.valueOf(mDuration.getTimeInMillis(ZERO) % 1000).intValue();
	}

	public double getDecimalSecond() {
		// The following approach would discard the fractional part
		Number seconds = mDuration.getField(DatatypeConstants.SECONDS);
		return seconds.doubleValue();

		// return getSecond() + ((double) getMillisecond()) / 1000.0;
	}

	public int hashCode() {
		return XmlDurationWorkAroundHelper.computeHashCode(mDuration);
	}

	/**
	 * <p>
	 * Returns a short string representation of this object. <b>The format of
	 * the returned string is subject to change.</b>
	 * </p>
	 * <p>
	 * The resutl is formatted according to the XML 1.0 specification.
	 * </p>
	 * 
	 * @return the string representation
	 */
	public String toString() {
		return mDuration.toString();
	}

	public boolean equals(final Object obj) {
		if (!(obj instanceof IDuration)) {
			return false;
		}

		javax.xml.datatype.Duration thatDuration = ((IDuration) obj).getValue();

		return XmlDurationWorkAroundHelper.equals(mDuration, thatDuration);
	}

	public int compareTo(ITerm o) {
		if (o == null || !(o instanceof IDuration))
			return 1;

		javax.xml.datatype.Duration thatDuration = ((IDuration) o).getValue();

		return XmlDurationWorkAroundHelper.compare(mDuration, thatDuration);
	}

	public boolean isGround() {
		return true;
	}

	public javax.xml.datatype.Duration getValue() {
		return mDuration;
	}

	public URI getDatatypeIRI() {
		return URI.create("http://www.w3.org/2001/XMLSchema#duration");
	}

	public boolean isPositive() {
		return mDuration.getSign() >= 0;
	}

	public String toCanonicalString() {
		return mDuration.toString();
	}
}
