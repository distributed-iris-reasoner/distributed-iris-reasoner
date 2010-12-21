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

import java.math.BigDecimal;

import junit.framework.TestCase;

import org.deri.iris.EvaluationException;
import org.deri.iris.api.basics.ITuple;
import org.deri.iris.api.terms.ITerm;
import org.deri.iris.api.terms.concrete.ISqName;

/**
 * Test for ToDecimalBuiltin.
 */
public class ToDecimalBuiltinTest extends TestCase {

	private static final ITerm X = TERM.createVariable("X");

	private static final ITerm Y = TERM.createVariable("Y");

	public ToDecimalBuiltinTest(String name) {
		super(name);
	}

	public void testBase64() throws EvaluationException {
		fails(CONCRETE.createBase64Binary("c3VyZS4="));
	}

	public void testBoolean() throws EvaluationException {
		equals(0.0, CONCRETE.createBoolean(false));
		equals(1.0, CONCRETE.createBoolean(true));
	}

	public void testDate() throws EvaluationException {
		fails(CONCRETE.createDate(2009, 04, 27));
	}

	public void testDateTime() throws EvaluationException {
		fails(CONCRETE.createDateTime(2009, 04, 27, 10, 10, 0, -5, 0));
	}

	public void testDayTimeDuration() throws EvaluationException {
		fails(CONCRETE.createDayTimeDuration(false, 2, 2, 3, 0));
	}

	public void testDecimal() throws EvaluationException {
		BigDecimal value = new BigDecimal(
				"123423429340239420342341029412412410294812094124.234209435692034092394020934209");
		equals(value, CONCRETE.createDecimal(value));
	}

	public void testDouble() throws EvaluationException {
		equals(1.337, CONCRETE.createDouble(1.337));
	}

	public void testDuration() throws EvaluationException {
		fails(CONCRETE.createDuration(true, 2, 1, 0, 5, 4, 2.3));
	}

	public void testFloat() throws EvaluationException {
		equals(1.337, CONCRETE.createFloat(1.337f));
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
		equals(1337.0, CONCRETE.createInteger(1337));
	}

	public void testIri() throws EvaluationException {
		fails(CONCRETE.createIri("http://www.w3.org/2007/rif#iri"));
	}

	public void testSqName() throws EvaluationException {
		ISqName name = CONCRETE.createSqName(CONCRETE
				.createIri("http://www.w3.org/2002/07/owl#"), "owl");
		fails(name);
	}

	public void testString() throws EvaluationException {
		equals(1.337, TERM.createString("1.337"));
		fails(TERM.createString("abcd"));
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

	private void equals(BigDecimal expected, ITerm term)
			throws EvaluationException {
		ITuple expectedTuple = BASIC.createTuple(CONCRETE
				.createDecimal(expected));
		ITuple actualTuple = compute(term);

		assertEquals(expectedTuple, actualTuple);
	}

	private void equals(double expected, ITerm term) throws EvaluationException {
		equals(new BigDecimal(Double.toString(expected)), term);
	}

	private void fails(ITerm term) throws EvaluationException {
		assertNull(compute(term));
	}

	private ITuple compute(ITerm term) throws EvaluationException {
		ToDecimalBuiltin builtin = new ToDecimalBuiltin(term, Y);

		ITuple arguments = BASIC.createTuple(X, Y);
		ITuple actualTuple = builtin.evaluate(arguments);

		return actualTuple;
	}

}
