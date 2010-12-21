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
package org.deri.iris.builtins.datatype;

import static org.deri.iris.factory.Factory.BASIC;
import static org.deri.iris.factory.Factory.CONCRETE;
import static org.deri.iris.factory.Factory.TERM;
import junit.framework.TestCase;

import org.deri.iris.EvaluationException;
import org.deri.iris.api.basics.ITuple;
import org.deri.iris.api.terms.ITerm;
import org.deri.iris.api.terms.concrete.ISqName;

/**
 * Test for ToBooleanBuiltin.
 */
public class ToBooleanBuiltinTest extends TestCase {

	private static final ITerm X = TERM.createVariable("X");

	private static final ITerm Y = TERM.createVariable("Y");

	public ToBooleanBuiltinTest(String name) {
		super(name);
	}

	public void testBase64() throws EvaluationException {
		fails(CONCRETE.createBase64Binary("c3VyZS4="));
	}

	public void testBoolean() throws EvaluationException {
		equals(true, CONCRETE.createBoolean(true));
	}

	public void testDate() throws EvaluationException {
		fails(CONCRETE.createDate(2009, 04, 27));
	}

	public void testDateTime() throws EvaluationException {
		fails(CONCRETE.createDateTime(2009, 04, 27, 10, 10, 0, -5, 0));
	}

	public void testDayTimeDuration() throws EvaluationException {
		fails(CONCRETE.createDayTimeDuration(false, 2, 2, 3, 0));
		fails(CONCRETE.createDayTimeDuration(true, 0, 15, 0, 0));
	}

	public void testDecimal() throws EvaluationException {
		equals(true, CONCRETE.createDecimal(1.337));
		equals(true, CONCRETE.createDecimal(-1.337));
		equals(false, CONCRETE.createDecimal(0.0));
	}

	public void testDouble() throws EvaluationException {
		equals(true, CONCRETE.createDouble(1.3));
		equals(true, CONCRETE.createDouble(-1.3));
		equals(false, CONCRETE.createDouble(0.0));
	}

	public void testDuration() throws EvaluationException {
		fails(CONCRETE.createDuration(true, 2, 1, 0, 5, 4, 2.3));
	}

	public void testFloat() throws EvaluationException {
		equals(true, CONCRETE.createFloat(1.3f));
		equals(true, CONCRETE.createFloat(-1.3f));
		equals(false, CONCRETE.createFloat(0.0f));
	}

	public void testGDay() throws EvaluationException {
		fails(CONCRETE.createGDay(27));
	}

	public void testGMonth() throws EvaluationException {
		fails(CONCRETE.createGMonth(4));
	}

	public void testGMonthDay() throws EvaluationException {
		fails(CONCRETE.createGMonthDay(4, 27));
	}

	public void testGYear() throws EvaluationException {
		fails(CONCRETE.createGYear(2009));
	}

	public void testGYearMonth() throws EvaluationException {
		fails(CONCRETE.createGYearMonth(2009, 4));
	}

	public void testHexBinary() throws EvaluationException {
		fails(CONCRETE.createHexBinary("0FB7"));
	}

	public void testInteger() throws EvaluationException {
		equals(true, CONCRETE.createInteger(1337));
		equals(false, CONCRETE.createInteger(0));
	}

	public void testIri() throws EvaluationException {
		fails(CONCRETE.createIri("http://www.w3.org/2007/rif#iri"));
	}

	public void testSqName() throws EvaluationException {
		ISqName name = CONCRETE.createSqName(CONCRETE
				.createIri("http://www.w3.org/2002/07/owl#"), "owl");
		fails(name);
	}

	public void testText() throws EvaluationException {
		fails(CONCRETE.createPlainLiteral("Ein Text@de"));
	}

	public void testTime() throws EvaluationException {
		fails(CONCRETE.createTime(12, 45, 0, 0, 0));
	}

	public void testXMLLiteral() throws EvaluationException {
		fails(CONCRETE.createXMLLiteral("<quote>Bam!</quote>", "de"));
	}

	public void testYearMonthDuration() throws EvaluationException {
		fails(CONCRETE.createYearMonthDuration(true, 2009, 4));
	}

	private void equals(boolean expected, ITerm term)
			throws EvaluationException {
		ITuple expectedTuple = BASIC.createTuple(CONCRETE
				.createBoolean(expected));
		ITuple actualTuple = compute(term);

		assertEquals(expectedTuple, actualTuple);
	}

	private void fails(ITerm term) throws EvaluationException {
		assertNull(compute(term));
	}

	private ITuple compute(ITerm term) throws EvaluationException {
		ToBooleanBuiltin builtin = new ToBooleanBuiltin(term, Y);

		ITuple arguments = BASIC.createTuple(X, Y);
		ITuple actualTuple = builtin.evaluate(arguments);

		return actualTuple;
	}

}
