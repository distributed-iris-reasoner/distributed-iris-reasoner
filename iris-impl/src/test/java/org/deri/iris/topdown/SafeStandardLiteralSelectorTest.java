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
package org.deri.iris.topdown;

import junit.framework.TestCase;

import org.deri.iris.api.basics.ILiteral;
import org.deri.iris.api.basics.IQuery;
import org.deri.iris.compiler.Parser;
import org.deri.iris.compiler.ParserException;
import org.deri.iris.evaluation.topdown.ILiteralSelector;
import org.deri.iris.evaluation.topdown.SafeStandardLiteralSelector;

/**
 * Test class for SafeStandardLiteralSelector
 * 
 * @author gigi
 * @see SafeStandardLiteralSelector
 */
public class SafeStandardLiteralSelectorTest extends TestCase {

	public void testSinglePositive() throws ParserException {
		String program = "?- p(?X).";
		
		Parser parser = new Parser();
		parser.parse(program);
		
		IQuery query = parser.getQueries().get(0);
		
		ILiteralSelector standardSelector = new SafeStandardLiteralSelector();
		ILiteral selectedLiteral = standardSelector.select(query.getLiterals());
		
		assertEquals("p(?X)", selectedLiteral.toString());
	}
	
	public void testSingleNegative() throws ParserException {
		String program = "?- not p(1).";
		
		Parser parser = new Parser();
		parser.parse(program);
		
		IQuery query = parser.getQueries().get(0);
		
		ILiteralSelector standardSelector = new SafeStandardLiteralSelector();
		ILiteral selectedLiteral = standardSelector.select(query.getLiterals());
		
		assertEquals("!p(1)", selectedLiteral.toString());
	}
	
	public void testSingleNegativeNotPossible() throws ParserException {
		String program = "?- not p(?X).";
		
		Parser parser = new Parser();
		parser.parse(program);
		
		IQuery query = parser.getQueries().get(0);
		
		ILiteralSelector standardSelector = new SafeStandardLiteralSelector();
		ILiteral selectedLiteral = standardSelector.select(query.getLiterals());
		
		assertEquals(null, selectedLiteral);
	}
	
}
