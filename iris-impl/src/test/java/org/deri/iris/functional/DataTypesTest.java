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
package org.deri.iris.functional;

import junit.framework.TestCase;

import org.deri.iris.compiler.ParserException;

/**
 * Tests for IRIS supported data types.
 */
public class DataTypesTest extends TestCase
{
	/**
	 * Create (valid) facts using all possible data types.
	 * @throws Exception 
	 */
	public void testValidLiterals() throws Exception
	{
		String allDataTypes =
			"p( _string( 'a string' ) )." +
			"p( 'literal string' )." +
			
			"p( _decimal( -1.11 ) )." +
			"p( 2.22 )." +
			
			"p( _integer( 333 ) )." +
			"p( -444 )." +
			
			"p( _float( 5.55 ) )." +
			
			"p( _double( 6.66 ) )." +
			
			"p( _iri( 'http://example.org/PersonOntology#Person' ) )." +
			"p( _'http://example.org/PersonOntology#Human' )." +
			
			"p( dc#title )." +
			"p( _sqname( foaf#name ) )." +

			"p( _boolean( 'trUE' ) )." +	// Mixed case is ok
			"p( _boolean( 'falSE' ) )." +

			"p( _boolean( '1' ) )." +	// Valid values are also '1' and '0'
			"p( _boolean( '0' ) )." +

			"p( _duration( 1970, 1, 1, 23, 15, 30 ) )." +
			"p( _duration( 1970, 1, 1, 23, 15, 29, 99 ) )." +

			"p( _datetime( 1980, 2, 2, 1, 2, 1.337 ) )." +
			"p( _datetime( 1980, 2, 2, 1, 2, 3, 1, 30 ) )." +
			"p( _datetime( 1980, 2, 2, 1, 2, 3, 99, 1, 30 ) )." +
			
			"p( _date( 1981, 3, 3 ) )." +
			"p( _date( 1982, 4, 4, 13, 30 ) )." +
			
			"p( _time( 1, 2, 3 ) )." +
			"p( _time( 1, 2, 3, 1, 30 ) )." +
			"p( _time( 1, 2, 3, 99, 1, 30 ) )." +
			
			"p( _gyear( 1991 ) )." +
			"p( _gyearmonth( 1992, 2 ) )." +
			"p( _gmonth( 3 ) )." +
			"p( _gmonthday( 2, 28 ) )." +
			"p( _gday( 31 ) )." +
			
			"p( _hexbinary( '0FB7abcd' ) )." +
			"p( _base64binary( 'QmFycnkgQmlzaG9w' ) )." +
			
			"p( _xmlliteral( '<tag>Text</tag>' ) )." +
			"p( _xmlliteral( '<tag>Text</tag>', 'en' ) )." +
			
			"p( _plainliteral( 'Good day to you, sir.', 'en' ) )." +
			"p( _plainliteral( 'Guten Tag, mein Herr.', 'de' ) )." +
			
			"?- p( ?X ).";

       	String expectedResults = allDataTypes;

       	Helper.evaluateWithAllStrategies( allDataTypes, expectedResults );
	}
	
	public void testFloatingPointSeconds() throws Exception
	{
		String allDataTypes =
//			"p( _duration( 1970, 1, 1, 23, 15, 30 ) )." +
//			"p( _duration( 1970, 1, 1, 23, 15, 29, 99 ) )." +
//			"p( _duration( 1970, 1, 1, 23, 15, 31.12345 ) )." +

//			"p( _datetime( 1980, 2, 2, 1, 2, 1 ) )." +
//			"p( _datetime( 1980, 2, 2, 1, 2, 2, 1, 30 ) )." +
//			"p( _datetime( 1980, 2, 2, 1, 2, 3, 99, 1, 30 ) )." +
//			"p( _datetime( 1980, 2, 2, 1, 2, 4.1234567 ) )." +
//			"p( _datetime( 1980, 2, 2, 1, 2, 5.1234567, 1, 30 ) )." +
			
			"p( _date( 1981, 3, 3 ) )." +
			"p( _date( 1982, 4, 4, 13, 30 ) )." +
			
			"p( _time( 1, 2, 3 ) )." +
			"p( _time( 1, 2, 3, 1, 30 ) )." +
			"p( _time( 1, 2, 3, 99, 1, 30 ) )." +
			"p( _time( 1, 2, 1.1234567 ) )." +
			"p( _time( 1, 2, 2.1234567, 1, 30 ) ).";

       	String expectedResults = allDataTypes;

       	Helper.evaluateWithAllStrategies( allDataTypes + "?- p( ?X ).", expectedResults );
	}
	
	/**
	 * Check that badly formatted literals cause failures.
	 * @throws Exception 
	 */
	public void testInvalidLiteral_String()
	{
		Helper.checkFailureWithAllStrategies( "p( _string( 'a', 'b' ) ).", ParserException.class );
	}
	
	/**
	 * Check that badly formatted literals cause failures.
	 * @throws Exception 
	 */
	public void testInvalidLiteral_Decimal()
	{
		Helper.checkFailureWithAllStrategies( "p( _decimal( -1.A1 ) ).", ParserException.class );
		Helper.checkFailureWithAllStrategies( "p( 1.2B ).", ParserException.class );
	}
	
	/**
	 * Check that badly formatted literals cause failures.
	 * @throws Exception 
	 */
	public void testInvalidLiteral_Integer()
	{
		Helper.checkFailureWithAllStrategies( "p( _integer( -B ) ).", ParserException.class );
		Helper.checkFailureWithAllStrategies( "p( -C ).", ParserException.class );
	}
	
	/**
	 * Check that badly formatted literals cause failures.
	 * @throws Exception 
	 */
	public void testInvalidLiteral_Float()
	{
		Helper.checkFailureWithAllStrategies( "p( _float( 3.r3) ).", ParserException.class );
	}
	
	/**
	 * Check that badly formatted literals cause failures.
	 * @throws Exception 
	 */
	public void testInvalidLiteral_Double()
	{
		Helper.checkFailureWithAllStrategies( "p( _double( -2.3u ) ).", ParserException.class );
	}
	
	/**
	 * Check that badly formatted literals cause failures.
	 * @throws Exception 
	 */
	public void testInvalidLiteral_IRI()
	{
		Helper.checkFailureWithAllStrategies( "p( _iri( 'http://example.org/PersonOntology #Person' ) ).", null );
		Helper.checkFailureWithAllStrategies( "p( _'http://example.org/ PersonOntology#Human' ).", null );
	}
	
	/**
	 * Check that badly formatted literals cause failures.
	 * @throws Exception 
	 */
	public void testInvalidLiteral_SQName()
	{
		Helper.checkFailureWithAllStrategies( "p( dc #title ).", ParserException.class );
		Helper.checkFailureWithAllStrategies( "p( _sqname( foaf name ) ).", ParserException.class );
	}
	
	/**
	 * Check that badly formatted literals cause failures.
	 * @throws Exception 
	 */
	public void testInvalidLiteral_Boolean()
	{
		Helper.checkFailureWithAllStrategies( "p( _boolean( 'tr_ue' ) ).", ParserException.class );
		Helper.checkFailureWithAllStrategies( "p( _boolean( 'fals' ) ).", ParserException.class );
		Helper.checkFailureWithAllStrategies( "p( _boolean( '2' ) ).", ParserException.class );
		Helper.checkFailureWithAllStrategies( "p( _boolean( '-1' ) ).", ParserException.class );
		Helper.checkFailureWithAllStrategies( "p( _boolean( 'one' ) ).", ParserException.class );
	}
	
	/**
	 * Check that badly formatted literals cause failures.
	 * @throws Exception 
	 */
	public void testInvalidLiteral_DateTime()
	{
		// Too few parameters
		Helper.checkFailureWithAllStrategies( "p( _datetime( 1982, 3, 4, 12, 30 ) ).", ParserException.class );
		Helper.checkFailureWithAllStrategies( "p( _datetime( 1982, 3, 4, 12, 30, 0, 1, 2, 3, 4 ) ).", ParserException.class );

		// Bad month
		Helper.checkFailureWithAllStrategies( "p( _datetime( 1982, 13, 4, 12, 30, 0 ) ).", ParserException.class );
		Helper.checkFailureWithAllStrategies( "p( _datetime( 1982, 0, 4, 12, 30, 0 ) ).", ParserException.class );

		// Bad day
		Helper.checkFailureWithAllStrategies( "p( _datetime( 1982, 12, 32, 12, 30, 0 ) ).", ParserException.class );
		Helper.checkFailureWithAllStrategies( "p( _datetime( 1982, 1, 0, 12, 30, 0 ) ).", ParserException.class );

		// Bad hour
		Helper.checkFailureWithAllStrategies( "p( _datetime( 1982, 12, 31, 24, 30, 0 ) ).", ParserException.class );
		Helper.checkFailureWithAllStrategies( "p( _datetime( 1982, 1, 1, -1, 30, 0 ) ).", ParserException.class );

		// Bad minute
		Helper.checkFailureWithAllStrategies( "p( _datetime( 1982, 12, 31, 23, 60, 0 ) ).", ParserException.class );
		Helper.checkFailureWithAllStrategies( "p( _datetime( 1982, 1, 1, 23, -1, 0 ) ).", ParserException.class );

		// Bad second, NB There can be leap seconds!
		Helper.checkFailureWithAllStrategies( "p( _datetime( 1982, 12, 31, 23, 59, 61 ) ).", ParserException.class );
		Helper.checkFailureWithAllStrategies( "p( _datetime( 1982, 1, 1, 23, 0, -1 ) ).", ParserException.class );

		// Bad millisecond
		Helper.checkFailureWithAllStrategies( "p( _datetime( 1982, 12, 31, 23, 59, 59, 1000 ) ).", ParserException.class );
		Helper.checkFailureWithAllStrategies( "p( _datetime( 1982, 1, 1, 23, 0, 0, -1 ) ).", ParserException.class );

		// Bad time zone hour
		Helper.checkFailureWithAllStrategies( "p( _datetime( 1982, 12, 31, 23, 59, 59, 999, 15, 0 ) ).", ParserException.class );
		Helper.checkFailureWithAllStrategies( "p( _datetime( 1982, 1, 1, 23, 0, 0, 0, -15, 0 ) ).", ParserException.class );

		// Bad time zone minute
		Helper.checkFailureWithAllStrategies( "p( _datetime( 1982, 12, 31, 23, 59, 59, 999, 0, 60 ) ).", ParserException.class );
		Helper.checkFailureWithAllStrategies( "p( _datetime( 1982, 1, 1, 23, 0, 0, 0, 0, -60 ) ).", ParserException.class );
		Helper.checkFailureWithAllStrategies( "p( _datetime( 1982, 12, 31, 23, 59, 59, 999, 14, 1 ) ).", ParserException.class );
		Helper.checkFailureWithAllStrategies( "p( _datetime( 1982, 1, 1, 23, 0, 0, 0, -14, -1 ) ).", ParserException.class );

		// Sign mismatch between time zone hours and minutes
		Helper.checkFailureWithAllStrategies( "p( _datetime( 1982, 12, 31, 23, 59, 59, 999, 1, -1 ) ).", ParserException.class );
		Helper.checkFailureWithAllStrategies( "p( _datetime( 1982, 1, 1, 23, 0, 0, 0, -1, 1 ) ).", ParserException.class );
	}
	
	/**
	 * Check that badly formatted literals cause failures.
	 * @throws Exception 
	 */
	public void testInvalidLiteral_Date()
	{
		// Wrong number of parameters
		Helper.checkFailureWithAllStrategies( "p( _date( 1982, 3 ) ).", ParserException.class );
		Helper.checkFailureWithAllStrategies( "p( _date( 1982, 3, 4, 12, 30, 1 ) ).", ParserException.class );

		// Bad time zone hour
		Helper.checkFailureWithAllStrategies( "p( _date( 1982, 12, 31, 15, 30 ) ).", ParserException.class );
		Helper.checkFailureWithAllStrategies( "p( _date( 1982, 1, 1, -15, 0 ) ).", ParserException.class );

		// Bad time zone minute
		Helper.checkFailureWithAllStrategies( "p( _date( 1982, 12, 31, 0, 60 ) ).", ParserException.class );
		Helper.checkFailureWithAllStrategies( "p( _date( 1982, 1, 1, 0, -60 ) ).", ParserException.class );
		Helper.checkFailureWithAllStrategies( "p( _date( 1982, 12, 31, 14, 1 ) ).", ParserException.class );
		Helper.checkFailureWithAllStrategies( "p( _date( 1982, 1, 1, 14, -1 ) ).", ParserException.class );

		// Sign mismatch between time zone hours and minutes
		Helper.checkFailureWithAllStrategies( "p( _date( 1982, 12, 31, 1, -1 ) ).", ParserException.class );
		Helper.checkFailureWithAllStrategies( "p( _date( 1982, 1, 1, -1, 1 ) ).", ParserException.class );
	}
	
	/**
	 * Check that badly formatted literals cause failures.
	 * @throws Exception 
	 */
	public void testInvalidLiteral_Time()
	{
		// Wrong number of parameters
		Helper.checkFailureWithAllStrategies( "p( _time( 12, 30 ) ).", ParserException.class );
		Helper.checkFailureWithAllStrategies( "p( _time( 12, 30, 0, 99, 13, 0, 1 ) ).", ParserException.class );

		// Bad hour
		Helper.checkFailureWithAllStrategies( "p( _time( 24, 30, 0 ) ).", ParserException.class );
		Helper.checkFailureWithAllStrategies( "p( _time( -1, 30, 0 ) ).", ParserException.class );

		// Bad minute
		Helper.checkFailureWithAllStrategies( "p( _time( 23, 60, 0 ) ).", ParserException.class );
		Helper.checkFailureWithAllStrategies( "p( _time( 23, -1, 0 ) ).", ParserException.class );

		// Bad second, NB There can be leap seconds!
		Helper.checkFailureWithAllStrategies( "p( _time( 23, 59, 61 ) ).", ParserException.class );
		Helper.checkFailureWithAllStrategies( "p( _time( 23, 0, -1 ) ).", ParserException.class );

		// Bad millisecond
		Helper.checkFailureWithAllStrategies( "p( _time( 23, 59, 59, 1000 ) ).", ParserException.class );
		Helper.checkFailureWithAllStrategies( "p( _time( 23, 0, 0, -1 ) ).", ParserException.class );

		// Bad time zone hour
		Helper.checkFailureWithAllStrategies( "p( _time( 1982, 12, 31, 23, 59, 59, 999, 15, 30 ) ).", ParserException.class );
		Helper.checkFailureWithAllStrategies( "p( _time( 1982, 1, 1, 23, 0, 0, 0, -15, 0 ) ).", ParserException.class );

		// Bad time zone minute
		Helper.checkFailureWithAllStrategies( "p( _time( 23, 59, 59, 999, 0, 60 ) ).", ParserException.class );
		Helper.checkFailureWithAllStrategies( "p( _time( 23, 0, 0, 0, 0, -60 ) ).", ParserException.class );
		Helper.checkFailureWithAllStrategies( "p( _time( 23, 59, 59, 999, 14, 1 ) ).", ParserException.class );
		Helper.checkFailureWithAllStrategies( "p( _time( 23, 0, 0, 0, -14, -1 ) ).", ParserException.class );

		// Sign mismatch between time zone hours and minutes
		Helper.checkFailureWithAllStrategies( "p( _time( 23, 59, 59, 999, 1, -1 ) ).", ParserException.class );
		Helper.checkFailureWithAllStrategies( "p( _time( 23, 0, 0, 0, -1, 1 ) ).", ParserException.class );
	}
	
	/**
	 * Check that badly formatted literals cause failures.
	 * @throws Exception 
	 */
	public void testInvalidLiteral_YearMonth()
	{
		Helper.checkFailureWithAllStrategies( "p( _yearmonth( 1980 ) ).", ParserException.class );
		Helper.checkFailureWithAllStrategies( "p( _yearmonth( 1980, 12, 1 ) ).", ParserException.class );
		Helper.checkFailureWithAllStrategies( "p( _yearmonth( 1980, 13 ) ).", ParserException.class );
		Helper.checkFailureWithAllStrategies( "p( _yearmonth( 1980, 0 ) ).", ParserException.class );
	}
	
	/**
	 * Check that badly formatted literals cause failures.
	 * @throws Exception 
	 */
	public void testInvalidLiteral_MonthDay()
	{
		Helper.checkFailureWithAllStrategies( "p( _monthday( 12 ) ).", ParserException.class );
		Helper.checkFailureWithAllStrategies( "p( _monthday( 12, 1, 1 ) ).", ParserException.class );
		Helper.checkFailureWithAllStrategies( "p( _monthday( 13, 1 ) ).", ParserException.class );
		Helper.checkFailureWithAllStrategies( "p( _monthday( 0, 1 ) ).", ParserException.class );
		Helper.checkFailureWithAllStrategies( "p( _monthday( 12, 32 ) ).", ParserException.class );
		Helper.checkFailureWithAllStrategies( "p( _monthday( 1, 0 ) ).", ParserException.class );
	}
	
	/**
	 * Check that badly formatted literals cause failures.
	 * @throws Exception 
	 */
	public void testInvalidLiteral_HexBinary()
	{
		// Invalid hexa-decimal
		Helper.checkFailureWithAllStrategies( "p( _hexbinary( '0FB7abcdG' ) ).", ParserException.class );
	}
	
	/**
	 * Check that badly formatted literals cause failures.
	 * @throws Exception 
	 */
	public void testInvalidLiteral_Base64Binary()
	{
		// Invalid base 64
		Helper.checkFailureWithAllStrategies( "p( _base64binary( 'QmFycnkgQmlzaG9wa' ) ).", ParserException.class );
	}

	/**
     * Check that tuples with various types happily co-exist in the same relation.
     */
    public void testMixedDataTypeRelation() throws Exception
    {
    	String facts =
    		"p( 'a', 'string' )." +
    		"p( 'a', 7 )." +
    		"p( 'a', _integer( 8 ) )." +
    		"p( 'a', -7.123 )." +
    		"p( 'a', _decimal( -8.123 ) )." +
    		"p( 'a', _float( -9.123 ) )." +
    		"p( 'a', 'true' )." +
    		"p( 'a', _boolean( 'false' ) )." +
    		"p( 'a', _gmonthday( 6, 7 ) )." +
    		"p( 'a', _gyearmonth( 4, 5 ) )." +
    		"p( 'a', _gyear( 5 ) )." +
    		"p( 'a', _gmonth( 4 ) )." +
    		"p( 'a', _gday( 3 ) )." +
    		"p( 'a', _duration( 1, 2, 3, 4, 5, 6) )." +
    		"p( 'a', _time( 1, 1, 1 ) )." +
    		"p( 'a', _date( 2001, 8, 1 ) )." +
    		"p( 'a', _datetime(2000,1,1,2,2,2) ).";
    		
    	String program = facts +
    		"?- p(?X, ?Y ).";
    	
       	String expectedResults = facts;
    
       	Helper.evaluateWithAllStrategies( program, expectedResults );
    }


}
