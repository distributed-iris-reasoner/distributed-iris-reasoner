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
package org.deri.iris.utils;

/**
 * A floating point comparator that uses the default java behaviour.
 * This class will make floating point comparison faster,
 * but will not allow for any round-off errors at all.
 */
public class StrictFloatingPointComparator implements IFloatingPointComparator
{
	public int compare( double a, double b )
	{
		return Double.compare( a, b );
	}

	public boolean equals( double a, double b )
	{
		return a == b;
	}

	public boolean greater( double a, double b )
	{
		return a > b;
	}

	public boolean greaterOrEquals( double a, double b )
	{
		return a >= b;
	}

	public boolean isIntValue( double value )
	{
		return value == Math.rint( value );
	}

	public boolean less( double a, double b )
	{
		return a < b;
	}

	public boolean lessOrEquals( double a, double b )
	{
		return a <= b;
	}

	public boolean notEquals( double a, double b )
	{
		return a != b;
	}
}
