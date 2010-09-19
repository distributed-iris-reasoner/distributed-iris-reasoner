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
package org.deri.iris.terms.concrete;

import java.net.URI;

import org.deri.iris.api.terms.concrete.IDateTimeStamp;

/**
 * <p>
 * A simple implementation of DateTimeStamp.
 * </p>
 * 
 * @author Adrian Marte
 */
public class DateTimeStamp extends DateTime implements IDateTimeStamp {

	/**
	 * Creates a new DateTimeStamp for the specified values.
	 * 
	 * @param year The year fragment.
	 * @param month The month fragment.
	 * @param day The Day of month fragment.
	 * @param hour The hour fragment.
	 * @param minute The minute fragment.
	 * @param second The second fragment.
	 * @param tzHour The timezone hour (relative to GMT) fragment.
	 * @param tzMinute The timezone minute (relative to GMT) fragment.
	 * @throws IllegalArgumentException If not both the tzHour and tzMinute are
	 *             positive or negative.
	 */
	public DateTimeStamp(int year, int month, int day, int hour, int minute,
			double second, int tzHour, int tzMinute) {
		super(year, month, day, hour, minute, second, tzHour, tzMinute);
	}

	@Override
	public URI getDatatypeIRI() {
		return URI.create(IDateTimeStamp.DATATYPE_URI);
	}

}
