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
package org.deri.iris.api.terms.concrete;

import javax.xml.datatype.Duration;

import org.deri.iris.api.terms.IConcreteTerm;

/**
 * 
 * <p>
 * This is a interface to represent durations from seconds up to years.
 * </p>
 * <p>
 * Remark: IRIS supports datatypes according to the standard specification for
 * primitive XML Schema datatypes.
 * </p>
 */
public interface IDuration extends IConcreteTerm {
	/**
	 * Return the wrapped type.
	 */
	Duration getValue();

	/**
	 * Returns <code>true</code> if this is a positive duration,
	 * <code>false</code> otherwise. Also returns <code>true</code> if this is a
	 * duration of length 0.
	 * 
	 * @return <code>true</code> if this is a positive duration,
	 *         <code>false</code> otherwise.
	 */
	boolean isPositive();

	/**
	 * Returns the years.
	 * 
	 * @return the years
	 */
	int getYear();

	/**
	 * Returns the months
	 * 
	 * @return the months
	 */
	int getMonth();

	/**
	 * Returns the days
	 * 
	 * @return the days
	 */
	int getDay();

	/**
	 * Returns the hours
	 * 
	 * @return the hours
	 */
	int getHour();

	/**
	 * returns the minutes
	 * 
	 * @return the minutes
	 */
	int getMinute();

	/**
	 * Returns the seconds
	 * 
	 * @return the seconds
	 */
	int getSecond();

	/**
	 * Returns the milliseconds.
	 * 
	 * @return the milliseconds
	 */
	int getMillisecond();

	/**
	 * Return the complete floating point representation of the seconds
	 * components.
	 * 
	 * @return Decimal seconds
	 */
	double getDecimalSecond();
}
