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
package org.deri.iris.basics;

import static org.deri.iris.factory.Factory.BASIC;
import static org.deri.iris.factory.Factory.CONCRETE;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.deri.iris.MiscHelper;
import org.deri.iris.ObjectTests;
import org.deri.iris.api.basics.ILiteral;

/**
 * <p>
 * Tests for the rule.
 * </p>
 * <p>
 * $Id$
 * </p>
 *
 * @author Richard Pöttler (richard dot poettler at deri dot at)
 * @version $Revision$
 */
public class RuleTest extends TestCase {

	private List<ILiteral> HEAD;

	private List<ILiteral> BODY;

	/**
	 * setup for Rule2Relation tests
	 */
	public void setUp() {
		HEAD = new ArrayList<ILiteral>();
		ILiteral literal = BASIC.createLiteral(true, 
				BASIC.createAtom(
					BASIC.createPredicate("sin", 1), 
					BASIC.createTuple(CONCRETE.createInteger(1))));
		HEAD.add(literal);
		HEAD.add(MiscHelper.createLiteral("cos", "X"));
		literal = BASIC.createLiteral(true, 
				BASIC.createAtom(
					BASIC.createPredicate("date", 3),
					BASIC.createTuple(CONCRETE.createInteger(2005), 
						CONCRETE.createInteger(12), 
						CONCRETE.createInteger(24))));
		HEAD.add(literal);

		BODY = new ArrayList<ILiteral>();
		literal = BASIC.createLiteral(true, 
				BASIC.createAtom(
					BASIC.createPredicate("sin", 1), 
					BASIC.createTuple(CONCRETE.createInteger(1))));
		BODY.add(literal);
		BODY.add(MiscHelper.createLiteral("cos", "X"));
		BODY.add(MiscHelper.createLiteral("date", "J", "K", "L"));
	}

	public static Test suite() {
		return new TestSuite(RuleTest.class, RuleTest.class.getSimpleName());
	}
	
	public void testEquals() {
		ObjectTests.runTestEquals(new Rule(HEAD, BODY), new Rule(HEAD, BODY),
				new Rule(HEAD, HEAD));
		ObjectTests.runTestEquals(new Rule(HEAD, BODY), new Rule(HEAD, BODY),
				new Rule(BODY, BODY));
	}

	public void testHashCode() {
		ObjectTests.runTestHashCode(new Rule(HEAD, BODY), new Rule(HEAD, BODY));
	}

}
