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

import java.net.URI;

import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.Duration;

import org.deri.iris.api.terms.concrete.IDayTimeDuration;

/*
 * W3C specification: http://www.w3.org/TR/xpath-functions/#dt-dayTimeDuration
 */

/**
 * <p>
 * An interface for representing the dayTimeDuration data-type.
 * xs:dayTimeDuration is derived from xs:duration by restricting its lexical
 * representation to contain only the days, hours, minutes and seconds
 * components.
 * </p>
 * <p>
 * Example: _dayTimeDuration(1, 10, 31, 15.5) represents the duration of <b>one
 * day, 10 hours, 31 minutes and 15.5 seconds</b>.
 * </p>
 * <p>
 * Remark: IRIS supports datatypes according to the standard specification for
 * primitive XML Schema datatypes.
 * </p>
 * 
 * @author gigi
 * 
 */
public class DayTimeDuration extends org.deri.iris.terms.concrete.Duration
		implements IDayTimeDuration {

	/**
	 * All values except from sign, day, hours, minutes and seconds are
	 * initialized with zero.
	 */
	DayTimeDuration(boolean positive, int day, int hour, int minute,
			double second) {
		super(positive, 0, 0, day, hour, minute, second);
	}

	DayTimeDuration(boolean positive, int day, int hour, int minute,
			int second, int ms) {
		super(positive, 0, 0, day, hour, minute, second, ms);
	}

	/**
	 * Shortened representation
	 * 
	 * For negative durations, the canonical form is calculated using the
	 * absolute value of the duration and a negative sign is prepended to it. If
	 * a component has the value zero (0) then the number and the designator for
	 * that component must be omitted. However, if all the components of the
	 * lexical form are zero (0), the canonical form is "PT0S".
	 * 
	 * source: http://www.w3.org/TR/xpath-functions/#dt-dayTimeDuration
	 */
	public String toCanonicalString() {
		Duration duration = this.getValue();
		if (duration.getSign() == 0)
			return "PT0S";

		StringBuffer repr = new StringBuffer();

		if (duration.getSign() == -1)
			repr.append("-");

		repr.append("P");

		int days = duration.getDays();
		if (days > 0) {
			repr.append(days);
			repr.append("D");
		}

		int hours = duration.getHours();
		int minutes = duration.getMinutes();
		int seconds = duration.getSeconds();

		Number secondsNumber = duration.getField(DatatypeConstants.SECONDS);
		double secondsFloat = secondsNumber.doubleValue();

		if ((hours == 0) && (minutes == 0) && (seconds == 0)
				&& (secondsFloat == 0.0))
			return repr.toString();

		repr.append("T");

		if (hours > 0) {
			repr.append(hours);
			repr.append("H");
		}

		if (minutes > 0) {
			repr.append(minutes);
			repr.append("M");
		}

		if ((secondsFloat == (double) seconds) && (seconds > 0)) {
			repr.append(seconds);
			repr.append("S");
		} else if (secondsFloat > 0) {
			repr.append(secondsFloat);
			repr.append("S");
		}

		return repr.toString();
	}

	public URI getDatatypeIRI() {
		return URI.create("http://www.w3.org/2001/XMLSchema#dayTimeDuration");
	}

	public IDayTimeDuration toCanonical() {
		double seconds = getFractionalSeconds();

		int day = (int) (seconds / 86400);
		int hour = (int) ((seconds % 86400) / 3600);
		int minute = (int) (((seconds % 86400) % 3600) / 60);
		double second = ((seconds % 86400.0) % 3600.0) % 60.0;

		return new DayTimeDuration(isPositive(), day, hour, minute, second);
	}

	private double getFractionalSeconds() {
		double sum = 0;

		sum += getDecimalSecond() + 60 * getMinute() + 3600 * getHour() + 86400
				* getDay();
		
		return sum;
	}

	public String toString() {
		return toCanonicalString();
	}

}
