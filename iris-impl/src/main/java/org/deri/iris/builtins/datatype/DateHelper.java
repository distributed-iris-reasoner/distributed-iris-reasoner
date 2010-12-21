/*
 * Integrated Rule Inference System (IRIS):
 * An extensible rule inference system for datalog with extensions.
 * 
 * Copyright (C) 2009 Semantic Technology Institute (STI) Innsbruck, 
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
package org.deri.iris.builtins.datatype;

import java.util.TimeZone;

/**
 * A helper class for handling with dates, time zones, etc.
 */
class DateHelper {

	/**
	 * Returns the hour part of a time zone.
	 * 
	 * @param timeZone The time zone.
	 * @return The hour part of the given time zone.
	 */
	static int getHourPart(TimeZone timeZone) {
		int offset = timeZone.getRawOffset();
		int tzHour = offset / 3600000;

		return tzHour;
	}

	/**
	 * Returns the minute part of a time zone.
	 * 
	 * @param timeZone The time zone.
	 * @return The minute part of the given time zone.
	 */
	static int getMinutePart(TimeZone timeZone) {
		int offset = timeZone.getRawOffset();
		int tzMinute = (Math.abs(offset) % 3600000) / 60000;

		if (offset < 0) {
			tzMinute *= -1;
		}

		return tzMinute;
	}

}
