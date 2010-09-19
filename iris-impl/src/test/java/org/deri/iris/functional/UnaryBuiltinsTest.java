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

/**
 * Tests for IRIS built-in predicates.
 */
public class UnaryBuiltinsTest extends TestCase
{
	final String mAllDataTypes =
		"p( _string( 'a string' ) )." +
		
		"p( _decimal( -1.11 ) )." +
		"p( _double( 6.66 ) )." +
		"p( _float( 5.55 ) )." +
		"p( _integer( 333 ) )." +
		
		"p( _iri( 'http://example.org/PersonOntology#Person' ) )." +
		
		"p( _sqname( foaf#name ) )." +

		"p( _boolean( 'true' ) )." +
		"p( _boolean( 'false' ) )." +

		"p( _duration( 1970, 1, 1, 23, 15, 30 ) )." +

		"p( _datetime( 1980, 2, 2, 1, 2, 3 ) )." +
		
		"p( _date( 1981, 3, 3 ) )." +
		
		"p( _time( 1, 2, 3 ) )." +
		
		"p( _gyear( 1991 ) )." +
		"p( _gyearmonth( 1992, 2 ) )." +
		"p( _gmonth( 3 ) )." +
		"p( _gmonthday( 2, 28 ) )." +
		"p( _gday( 31 ) )." +
		
		"p( _hexbinary( '0FB7abcd' ) )." +
		"p( _base64binary( 'QmFycnkgQmlzaG9w' ) ).";
	
	public void testIsString() throws Exception
	{
		String program = mAllDataTypes +
			"?- p( ?X ), IS_STRING( ?X ).";
		
       	String expectedResults = 
       		"p( _string( 'a string' ) ).";

       	Helper.evaluateWithAllStrategies( program, expectedResults );
	}

	public void testIsBoolean() throws Exception
	{
		String program = mAllDataTypes +
			"?- p( ?X ), IS_BOOLEAN( ?X ).";
		
       	String expectedResults = 
       		"p( _boolean( 'true' ) )." +
    		"p( _boolean( 'false' ) ).";

       	Helper.evaluateWithAllStrategies( program, expectedResults );
	}
	
	public void testIsDecimal() throws Exception
	{
		String program = mAllDataTypes +
			"?- p( ?X ), IS_DECIMAL( ?X ).";
		
       	String expectedResults = 
       		"p( _decimal( -1.11 ) )." +
       		"p( _integer( 333 ) ).";

       	Helper.evaluateWithAllStrategies( program, expectedResults );
	}

	public void testIsDouble() throws Exception
	{
		String program = mAllDataTypes +
			"?- p( ?X ), IS_DOUBLE( ?X ).";
		
       	String expectedResults = 
       		"p( _double( 6.66 ) ).";
       	
       	Helper.evaluateWithAllStrategies( program, expectedResults );
	}

	public void testIsFloat() throws Exception
	{
		String program = mAllDataTypes +
			"?- p( ?X ), IS_FLOAT( ?X ).";
		
       	String expectedResults = 
       		"p( _float( 5.55 ) ).";
       	
       	Helper.evaluateWithAllStrategies( program, expectedResults );
	}

	public void testIsInteger() throws Exception
	{
		String program = mAllDataTypes +
			"?- p( ?X ), IS_INTEGER( ?X ).";
		
       	String expectedResults =
       		"p( _integer( 333 ) ).";

       	Helper.evaluateWithAllStrategies( program, expectedResults );
	}

	public void testIsIri() throws Exception
	{
		String program = mAllDataTypes +
			"?- p( ?X ), IS_IRI( ?X ).";
		
       	String expectedResults =
       		"p( _iri( 'http://example.org/PersonOntology#Person' ) ).";

       	Helper.evaluateWithAllStrategies( program, expectedResults );
	}

	public void testIsSqName() throws Exception
	{
		String program = mAllDataTypes +
			"?- p( ?X ), IS_SQNAME( ?X ).";
		
       	String expectedResults = 
       		"p( _sqname( foaf#name ) ).";

       	Helper.evaluateWithAllStrategies( program, expectedResults );
	}

	public void testIsDuration() throws Exception
	{
		String program = mAllDataTypes +
			"?- p( ?X ), IS_DURATION( ?X ).";
		
       	String expectedResults = 
       		"p( _duration( 1970, 1, 1, 23, 15, 30 ) ).";

       	Helper.evaluateWithAllStrategies( program, expectedResults );
	}

	public void testIsDateTime() throws Exception
	{
		String program = mAllDataTypes +
			"?- p( ?X ), IS_DATETIME( ?X ).";
		
       	String expectedResults = 
       		"p( _datetime( 1980, 2, 2, 1, 2, 3 ) ).";

       	Helper.evaluateWithAllStrategies( program, expectedResults );
	}

	public void testIsDate() throws Exception
	{
		String program = mAllDataTypes +
			"?- p( ?X ), IS_DATE( ?X ).";
		
       	String expectedResults =
       		"p( _date( 1981, 3, 3 ) ).";

       	Helper.evaluateWithAllStrategies( program, expectedResults );
	}

	public void testIsTime() throws Exception
	{
		String program = mAllDataTypes +
			"?- p( ?X ), IS_TIME( ?X ).";
		
       	String expectedResults = 
       		"p( _time( 1, 2, 3 ) ).";

       	Helper.evaluateWithAllStrategies( program, expectedResults );
	}

	public void testIsGYear() throws Exception
	{
		String program = mAllDataTypes +
			"?- p( ?X ), IS_GYEAR( ?X ).";
		
       	String expectedResults = 
       		"p( _gyear( 1991 ) ).";

       	Helper.evaluateWithAllStrategies( program, expectedResults );
	}

	public void testIsGYearMonth() throws Exception
	{
		String program = mAllDataTypes +
			"?- p( ?X ), IS_GYEARMONTH( ?X ).";
		
       	String expectedResults = 
       		"p( _gyearmonth( 1992, 2 ) ).";

       	Helper.evaluateWithAllStrategies( program, expectedResults );
	}

	public void testIsGMonth() throws Exception
	{
		String program = mAllDataTypes +
			"?- p( ?X ), IS_GMONTH( ?X ).";
		
       	String expectedResults = 
       		"p( _gmonth( 3 ) ).";

       	Helper.evaluateWithAllStrategies( program, expectedResults );
	}

	public void testIsGMonthDay() throws Exception
	{
		String program = mAllDataTypes +
			"?- p( ?X ), IS_GMONTHDAY( ?X ).";
		
       	String expectedResults = 
       		"p( _gmonthday( 2, 28 ) ).";

       	Helper.evaluateWithAllStrategies( program, expectedResults );
	}

	public void testIsGDay() throws Exception
	{
		String program = mAllDataTypes +
			"?- p( ?X ), IS_GDAY( ?X ).";
		
       	String expectedResults = 
       		"p( _gday( 31 ) ).";

       	Helper.evaluateWithAllStrategies( program, expectedResults );
	}

	public void testIsHexBinary() throws Exception
	{
		String program = mAllDataTypes +
			"?- p( ?X ), IS_HEXBINARY( ?X ).";
		
       	String expectedResults = 
       		"p( _hexbinary( '0FB7abcd' ) ).";

       	Helper.evaluateWithAllStrategies( program, expectedResults );
	}

	public void testIsBase64Binary() throws Exception
	{
		String program = mAllDataTypes +
			"?- p( ?X ), IS_BASE64BINARY( ?X ).";
		
       	String expectedResults = 
       		"p( _base64binary( 'QmFycnkgQmlzaG9w' ) ).";

       	Helper.evaluateWithAllStrategies( program, expectedResults );
	}

	public void testIsNumeric() throws Exception
	{
		String program = mAllDataTypes +
			"?- p( ?X ), IS_NUMERIC( ?X ).";
		
       	String expectedResults = 
    		"p( _decimal( -1.11 ) )." +
    		"p( _double( 6.66 ) )." +
    		"p( _float( 5.55 ) )." +
    		"p( _integer( 333 ) ).";

       	Helper.evaluateWithAllStrategies( program, expectedResults );
	}
}
