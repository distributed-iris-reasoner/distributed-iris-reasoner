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
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 * IRIS duration objects are implemented using the javax.xml.datatype.Duration class.
 * However, there are a number of problems/bugs in the implementation of this class,
 * specifically with the add(), subtract(), equals() and compareTo() methods.
 * These methods do not work correctly when created using BigDecimal seconds,
 * even though the seconds component may be a whole number of seconds.
 */
public class XmlDurationWorkAroundHelper
{
	/**
	 * Add one Duration to another, avoiding the runtime library bug that gives
	 * incorrect results when using decimal seconds.
	 * @param d1 The first duration
	 * @param d2 The second duration
	 * @return The result of adding d1 to d2
	 */
	public static Duration add( Duration d1, Duration d2 )
	{
		boolean sign1 = d1.getSign() >= 0;
		boolean sign2 = d2.getSign() >= 0;
		
		if( sign1 && sign2 )
			return addPositiveDurations( d1, d2 );
		
		if( ! sign1 && ! sign2 )
			return addPositiveDurations( d1.negate(), d2.negate() ).negate();
		
		if( sign1 && ! sign2 )
			return subtract( d1, d2.negate() );

		//if( ! sign1 && sign2 )
			return subtract( d2, d1.negate() );
	}

	/**
	 * Subtract one Duration from another, avoiding the runtime library bug that gives
	 * incorrect results when using decimal seconds.
	 * @param d1 The first duration
	 * @param d2 The second duration
	 * @return The result of subtracting d2 from d1
	 */
	public static Duration subtract( Duration d1, Duration d2 )
	{
		boolean sign1 = d1.getSign() >= 0;
		boolean sign2 = d2.getSign() >= 0;

		if( sign1 && sign2 )
		{
			int comparison = d1.compare( d2 );
			comparison = compare( d1, d2 );
			if( comparison >= 0 )
				return subtractSmallerPositiveDurationFromLargerPositiveDuration( d1, d2 );
			else
				return subtractSmallerPositiveDurationFromLargerPositiveDuration( d2, d1 ).negate();
		}
		
		if( ! sign1 && ! sign2 )
		{
			d1 = d1.negate();
			d2 = d2.negate();
			
			int comparison = d1.compare( d2 );
			comparison = compare( d1, d2 );
			if( comparison < 0 )
				return subtractSmallerPositiveDurationFromLargerPositiveDuration( d2, d1 );
			else
				return subtractSmallerPositiveDurationFromLargerPositiveDuration( d1, d2 ).negate();
		}
		
		if( sign1 && ! sign2 )
			return add( d1, d2.negate() );

		//if( ! sign1 && sign2 )
			return add( d2, d1.negate() ).negate();
	}
	
	/**
	 * Special equality method that gets around the problem in java 1.5,
	 * where years and months are converted to days after arithmetic.
	 * @param d1 First duration
	 * @param d2 Second duration
	 * @return true if the two duration objects are equal in either raw or day-normalised form.
	 */
	public static boolean equals( Duration d1, Duration d2 )
	{
		if( d1.equals( d2 ) )
			return true;

		// Else normalise in case we are running with java 1.5 runtime
		javax.xml.datatype.Duration n1 = normaliseDays( d1 );
		javax.xml.datatype.Duration n2 = normaliseDays( d2 );
		
		if( n1.getSign() != n2.getSign() )
			return false;
		if( n1.getDays() != n2.getDays() )
			return false;
		if( n1.getHours() != n2.getHours() )
			return false;
		if( n1.getMinutes() != n2.getMinutes() )
			return false;
		
		BigDecimal s1 = (BigDecimal) n1.getField( DatatypeConstants.SECONDS );
		BigDecimal s2 = (BigDecimal) n2.getField( DatatypeConstants.SECONDS );
		if( ! equals( s1, s2 ) )
			return false;
		
		return true;
	}
	
	/**
	 * Special compare method that gets around the problem in java 1.5,
	 * where years and months are converted to days after arithmetic.
	 * @param d1 First duration
	 * @param d2 Second duration
	 * @return -1 if d1 < d2, 0 if d1 == d2 and +1 if d1 > d2
	 */
	public static int compare( Duration d1, Duration d2 )
	{
		if (d1 == null && d2 == null)
			return 0;

		if (d1 == null)
			return -1;
		
		if (d2 == null)
			return 1;

		boolean b1 = d1.getSign() >= 0;
		boolean b2 = d2.getSign() >= 0;
		
		if( ! b1 && b2 )
			return -1;
		
		if( b1 && ! b2 )
			return 1;

		// Now normalise in case we are running with java 1.5 runtime
		javax.xml.datatype.Duration n1 = normaliseDays( d1 );
		javax.xml.datatype.Duration n2 = normaliseDays( d2 );

		if( n1.getDays() < n2.getDays() )
			return -1;
		if( n1.getDays() > n2.getDays() )
			return 1;
		
		if( n1.getHours() < n2.getHours() )
			return -1;
		if( n1.getHours() > n2.getHours() )
			return 1;

		if( n1.getMinutes() < n2.getMinutes() )
			return -1;
		if( n1.getMinutes() > n2.getMinutes() )
			return 1;

		BigDecimal s1 = (BigDecimal) n1.getField( DatatypeConstants.SECONDS );
		BigDecimal s2 = (BigDecimal) n2.getField( DatatypeConstants.SECONDS );
		
		return s1.compareTo( s2 );
	}
	
	public static int computeHashCode( Duration duration )
	{
		Duration normalised = normaliseSeconds( normaliseDays( duration ) );
		return normalised.hashCode();
	}
	
	public static BigDecimal getSeconds( XMLGregorianCalendar x )
	{
		BigDecimal fractional = x.getFractionalSecond();
		if( fractional == null )
			fractional = BigDecimal.ZERO;
		
		BigDecimal whole = BigDecimal.valueOf( x.getSecond() );
		
		return whole.add( fractional );
	}

//	private static int[] forwardFields = { Calendar.YEAR, Calendar.MONTH, Calendar.DAY_OF_MONTH, Calendar.HOUR_OF_DAY, Calendar.MINUTE, Calendar.SECOND, Calendar.MILLISECOND };
	private static int[] reverseFields = { Calendar.MILLISECOND, Calendar.SECOND, Calendar.MINUTE, Calendar.HOUR_OF_DAY, Calendar.DAY_OF_MONTH, Calendar.MONTH, Calendar.YEAR };
	
	public static Duration subtract( XMLGregorianCalendar x1, XMLGregorianCalendar x2 )
	{
		boolean positive = x1.compare( x2 ) >= 0;
		
		if( ! positive )
		{
			XMLGregorianCalendar temp = x1;
			x1 = x2;
			x2 = temp;
		}
		
		BigDecimal s1 = getSeconds( x1 );
		BigDecimal s2 = getSeconds( x2 );
		BigDecimal seconds = s1.subtract( s2 );
		if( seconds.compareTo( BigDecimal.ZERO ) < 0 )
			seconds = seconds.add( BigDecimal.valueOf( 60 ) );
		
		GregorianCalendar g1 = x1.toGregorianCalendar();
		GregorianCalendar g2 = x2.toGregorianCalendar();
		
		int year = 0;
		for( int f : reverseFields )
		{
			if( f == Calendar.YEAR )
			{
				int year1 = g1.get( f );
				int year2 = g2.get( f );
				year = year1 - year2;
			}
			else
			{
				subtractField( g1, g2, f );
			}
		}
		
		return FACTORY.newDuration(
						positive,
						BigInteger.valueOf( year ),
						BigInteger.valueOf( g1.get( Calendar.MONTH ) ),
						BigInteger.valueOf( g1.get( Calendar.DAY_OF_MONTH ) - 1 ),
						BigInteger.valueOf( g1.get( Calendar.HOUR_OF_DAY ) ),
						BigInteger.valueOf( g1.get( Calendar.MINUTE ) ),
						seconds );
	}

	protected static String toString( GregorianCalendar g1 )
	{
		StringBuilder buffer = new StringBuilder();
		
		buffer.append( g1.get( Calendar.YEAR ) + "/" + (g1.get( Calendar.MONTH ) + 1) + "/" + g1.get( Calendar.DAY_OF_MONTH ) );
		buffer.append( "  " );
		buffer.append( g1.get( Calendar.HOUR_OF_DAY ) + ":" + g1.get( Calendar.MINUTE ) + ":" + g1.get( Calendar.SECOND )+ "." + g1.get( Calendar.MILLISECOND ) );
		
		return buffer.toString();
	}
	
	private static void subtractField( GregorianCalendar g1, GregorianCalendar g2, int field )
	{
//		int value1 = g1.get( field );
		int value2 = g2.get( field );

		if( field == Calendar.DAY_OF_MONTH )
			value2 -= 1;
		g1.add( field, -value2 );
	}
	
	/**
	 * Test for mathematical equality of two BigDecimal objects
	 * (not representation equality).
	 * @param f1 The first value
	 * @param f2 The second value
	 * @return true if the two arguments are mathematically equal
	 */
	private static boolean equals( BigDecimal f1, BigDecimal f2 )
	{
		BigDecimal[] args = new BigDecimal[] { f1, f2 };
		
		matchScale( args );
		
		return args[ 0 ].equals( args[ 1 ] );
	}
	
	/**
	 * Helper to set the scale of two BigDecimal objects to be the same
	 * and equal to the higher of the two.
	 * @param val An array of two BigDecimal objects.
	 */
	private static void matchScale(BigDecimal[] val)
	{
		if (val[0].scale() < val[1].scale())
	    	val[0] = val[0].setScale(val[1].scale());
	    else if (val[1].scale() < val[0].scale())
	    	val[1] = val[1].setScale(val[0].scale());
	}
	
	/**
	 * Return just the fractional part of the seconds component of a duration object,
	 * i.e. 1:2:34.45 => 0.45
	 * @param duration The duration object to examine.
	 * @return The fractional part of the seconds field.
	 */
	private static BigDecimal fractionalSeconds( Duration duration )
	{
		BigDecimal seconds = (BigDecimal) duration.getField( DatatypeConstants.SECONDS );
		
		return seconds.subtract( new BigDecimal( seconds.toBigInteger() ) );
	}
	
	/**
	 * Create a new Duration object without any fractional second component.
	 * @param duration The duration object to strip
	 * @return The input Duration with the fractional second part stripped off.
	 */
	private static Duration stripFractionalSeconds( Duration duration )
	{
		int year   = duration.getYears();
		int month  = duration.getMonths();
		int day    = duration.getDays();
		int hour   = duration.getHours();
		int minute = duration.getMinutes();
		int second = duration.getSeconds();
		
		boolean positive = duration.getSign() >= 0;
		
		return FACTORY.newDuration(
						positive,
						year,
						month,
						day,
						hour,
						minute,
						second );
	}
	
	/**
	 * Add two positive Duration objects.
	 * @param d1 The first Duration.
	 * @param d2 The second Duration.
	 * @return The sum of the two durations.
	 */
	private static Duration addPositiveDurations( Duration d1, Duration d2 )
	{
		BigDecimal s1 = fractionalSeconds( d1 );
		BigDecimal s2 = fractionalSeconds( d2 );
		BigDecimal extraSeconds = s1.add( s2 );
		
		Duration strip1 = stripFractionalSeconds( d1 );
		Duration strip2 = stripFractionalSeconds( d2 );
		
		Duration stripResult = strip1.add( strip2 );
		
		if( extraSeconds.compareTo( BigDecimal.ONE ) >= 0 )
		{
			stripResult = stripResult.add( DURATION_1_SECOND );
			extraSeconds = extraSeconds.subtract( BigDecimal.ONE );
		}
		
		BigDecimal properSeconds = BigDecimal.valueOf( stripResult.getSeconds() ).add( extraSeconds );
		
		return FACTORY.newDuration( true,
						BigInteger.valueOf( stripResult.getYears() ),
						BigInteger.valueOf( stripResult.getMonths() ),
						BigInteger.valueOf( stripResult.getDays() ),
						BigInteger.valueOf( stripResult.getHours() ),
						BigInteger.valueOf( stripResult.getMinutes() ),
						properSeconds );
	}

	/**
	 * Subtract one positive Duration from another, larger positive Duration.
	 * @param d1 The larger positive duration
	 * @param d2 The smaller positive duration
	 * @return The difference
	 */
	private static Duration subtractSmallerPositiveDurationFromLargerPositiveDuration( Duration d1, Duration d2 )
	{
		BigDecimal s1 = fractionalSeconds( d1 );
		BigDecimal s2 = fractionalSeconds( d2 );
		BigDecimal extraSeconds = s1.subtract( s2 );
		
		Duration strip1 = stripFractionalSeconds( d1 );
		Duration strip2 = stripFractionalSeconds( d2 );
		
		Duration stripResult = strip1.subtract( strip2 );
		
		if( extraSeconds.compareTo( BigDecimal.ZERO ) < 0 )
		{
			stripResult = stripResult.subtract( DURATION_1_SECOND );
			extraSeconds = extraSeconds.add( BigDecimal.ONE );
		}
		
		BigDecimal properSeconds = BigDecimal.valueOf( stripResult.getSeconds() ).add( extraSeconds );
		
		return FACTORY.newDuration( true,
						BigInteger.valueOf( stripResult.getYears() ),
						BigInteger.valueOf( stripResult.getMonths() ),
						BigInteger.valueOf( stripResult.getDays() ),
						BigInteger.valueOf( stripResult.getHours() ),
						BigInteger.valueOf( stripResult.getMinutes() ),
						properSeconds );
	}	

	/**
	 * Java runtime 1.5 is inconsistent with its handling of days in Duration objects.
	 * @param duration A duration object to be normalised
	 * @return A day-normalised duration, i.e. all years and months converted to days,
	 * e.g. 1Y 3M 3D => 458 days
	 */
	private static javax.xml.datatype.Duration normaliseDays( javax.xml.datatype.Duration duration )
	{
		final long DAYS_PER_MONTH = 30;
		final long DAYS_PER_YEAR = 365;

		BigInteger days   = (BigInteger) duration.getField( DatatypeConstants.DAYS );
		BigInteger months = (BigInteger) duration.getField( DatatypeConstants.MONTHS );
		BigInteger years  = (BigInteger) duration.getField( DatatypeConstants.YEARS );
		
		BigInteger normalisedDays = years.multiply( BigInteger.valueOf( DAYS_PER_YEAR ) );
		normalisedDays = normalisedDays.add( months.multiply( BigInteger.valueOf( DAYS_PER_MONTH ) ) );
		normalisedDays = normalisedDays.add( days );
		
		BigInteger hours   = (BigInteger) duration.getField( DatatypeConstants.HOURS );
		BigInteger minutes = (BigInteger) duration.getField( DatatypeConstants.MINUTES );
		BigDecimal seconds = (BigDecimal) duration.getField( DatatypeConstants.SECONDS );
		
		boolean positive = duration.getSign() >= 0;
		
		return FACTORY.newDuration( positive, BigInteger.ZERO, BigInteger.ZERO, normalisedDays, hours, minutes, seconds );
	}

	private static javax.xml.datatype.Duration normaliseSeconds( javax.xml.datatype.Duration duration )
	{
		BigInteger years  = (BigInteger) duration.getField( DatatypeConstants.YEARS );
		BigInteger months = (BigInteger) duration.getField( DatatypeConstants.MONTHS );
		BigInteger days   = (BigInteger) duration.getField( DatatypeConstants.DAYS );
		
		BigInteger hours   = (BigInteger) duration.getField( DatatypeConstants.HOURS );
		BigInteger minutes = (BigInteger) duration.getField( DatatypeConstants.MINUTES );
		BigDecimal seconds = (BigDecimal) duration.getField( DatatypeConstants.SECONDS );
		
		seconds = seconds.stripTrailingZeros();
		
		boolean positive = duration.getSign() >= 0;
		
		return FACTORY.newDuration( positive, years, months, days, hours, minutes, seconds );
	}

	/** An XML data type factory for creating new objects. */
	private static DatatypeFactory FACTORY;
	
	static {
		try {
			FACTORY = DatatypeFactory.newInstance();
		} catch (DatatypeConfigurationException e) {
			throw new RuntimeException(
					"Couldn't create an XML data-type factory", e);
		}
	}
	
	/** A useful duration value of exactly 1 second. */
	private static final Duration DURATION_1_SECOND = FACTORY.newDuration( true, 0, 0, 0, 0, 0, 1 );
}
