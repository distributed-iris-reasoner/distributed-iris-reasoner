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
package org.deri.iris.topdown.oldt;

import java.util.Map;

import junit.framework.TestCase;

import org.deri.iris.RuleUnsafeException;
import org.deri.iris.api.basics.IQuery;
import org.deri.iris.api.basics.IRule;
import org.deri.iris.api.terms.ITerm;
import org.deri.iris.api.terms.IVariable;
import org.deri.iris.compiler.Parser;
import org.deri.iris.compiler.ParserException;
import org.deri.iris.evaluation.topdown.TopDownHelper;

/**
 * Test class for TopDownHelper.getVariableMapForVariableRenaming(rule, query)
 * which is used by SLDNF and OLDT evaluation strategies.
 * 
 * @author gigi
 * @see TopDownHelper.getVariableMapForVariableRenaming(IRule rule, IQuery query)
 */
public class VariableRenamingTest extends TestCase {

	/**
	 * Tests if variables get substituted correctly when the same variable appears
	 * in the rule head and the current evaluation body.
	 * 
	 * @throws ParserException on failure
	 * @throws RuleUnsafeException when the program contains unsafe rules
	 */
	public void testVariableClash() throws ParserException, RuleUnsafeException {
		
		String program = 
			"p(?x, ?y) :- ?x > ?y." +
			"?- 2 + ?y = ?x, p(2, ?x).";
		
		Parser parser = new Parser();
		parser.parse(program);
		IQuery query = parser.getQueries().get(0);
		IRule rule = parser.getRules().get(0);
		
		Map<IVariable, ITerm> map = TopDownHelper.getVariableMapForVariableRenaming(rule, query);
		
		System.out.println(map);
		assertEquals(false, map.isEmpty());
	}
	
}
