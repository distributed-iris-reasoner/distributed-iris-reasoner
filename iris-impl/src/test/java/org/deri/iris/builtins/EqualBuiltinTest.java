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
package org.deri.iris.builtins;

import static org.deri.iris.factory.Factory.BASIC;
import static org.deri.iris.factory.Factory.CONCRETE;
import static org.deri.iris.factory.Factory.TERM;
import junit.framework.TestCase;

import org.deri.iris.EvaluationException;
import org.deri.iris.api.basics.ITuple;

/**
 * <p>
 * Tests for the equals builtin.
 * </p>
 * <p>
 * $Id: EqualBuiltinTest.java,v 1.6 2007-07-25 08:16:57 poettler_ric Exp $
 * </p>
 * 
 * @author Richard Pöttler (richard dot poettler at deri dot org)
 * @version $Revision: 1.6 $
 */
public class EqualBuiltinTest extends TestCase {

	private EqualBuiltin xy;

	public EqualBuiltinTest(String name) {
		super(name);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		xy = new EqualBuiltin(TERM.createVariable("X"), TERM
				.createVariable("Y"));
	}

	private void succeeds(String message, ITuple tuple) {
		try {
			assertNotNull(message, xy.evaluate(tuple));
		} catch (EvaluationException e) {
			fail("Evaluation exception");
		}
	}

	private void fails(String message, ITuple tuple) {
		try {
			assertNull(message, xy.evaluate(tuple));
		} catch (EvaluationException e) {
			fail("Evaluation exception");
		}
	}

	public void testNumerics() throws Exception {
		succeeds("5 should be equal to 5", BASIC.createTuple(CONCRETE
				.createInteger(5), CONCRETE.createInteger(5)));
		succeeds("5 should be equal to 5.0", BASIC.createTuple(CONCRETE
				.createInteger(5), CONCRETE.createDouble(5d)));

		succeeds("+0.0 should be equal to -0.0", BASIC.createTuple(CONCRETE
				.createDecimal(+0.0), CONCRETE.createDecimal(-0.0)));
		
		succeeds("+0.0 should be equal to -0.0", BASIC.createTuple(CONCRETE
				.createDouble(+0.0), CONCRETE.createDouble(-0.0)));
		succeeds("+Inf should be equal to +Inf", BASIC.createTuple(CONCRETE
				.createDouble(Double.POSITIVE_INFINITY), CONCRETE
				.createDouble(Double.POSITIVE_INFINITY)));
		succeeds("-Inf should be equal to -Inf", BASIC.createTuple(CONCRETE
				.createDouble(Double.NEGATIVE_INFINITY), CONCRETE
				.createDouble(Double.NEGATIVE_INFINITY)));

		succeeds("+0.0 should be equal to -0.0", BASIC.createTuple(CONCRETE
				.createFloat(+0.0f), CONCRETE.createFloat(-0.0f)));
		succeeds("+Inf should be equal to +Inf", BASIC.createTuple(CONCRETE
				.createDouble(Float.POSITIVE_INFINITY), CONCRETE
				.createDouble(Float.POSITIVE_INFINITY)));
		succeeds("-Inf should be equal to -Inf", BASIC.createTuple(CONCRETE
				.createDouble(Float.NEGATIVE_INFINITY), CONCRETE
				.createDouble(Float.NEGATIVE_INFINITY)));

		fails("5 should not equal to 2", BASIC.createTuple(CONCRETE
				.createInteger(2), CONCRETE.createInteger(5)));
		fails("5 should not be equal to a", BASIC.createTuple(CONCRETE
				.createInteger(5), TERM.createString("a")));
	}

	public void testIsBuiltin() {
		assertTrue("buitin predicates should be identifiable as builtins",
				(new EqualBuiltin(CONCRETE.createInteger(5), CONCRETE
						.createInteger(5)).isBuiltin()));
	}
}
