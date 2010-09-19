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

/**
 * Test for ToTextBuiltin.
 */
public class ToTextBuiltinTest extends TestCase {

	private static final ITerm X = TERM.createVariable("X");

	private static final ITerm Y = TERM.createVariable("Y");

	public ToTextBuiltinTest(String name) {
		super(name);
	}

	public void testBase64() throws EvaluationException {
		equals("c3VyZS4=", CONCRETE.createBase64Binary("c3VyZS4="));
	}

	public void testBoolean() throws EvaluationException {
		equals("true", CONCRETE.createBoolean(true));
	}

	public void testDate() throws EvaluationException {
		equals("2009-04-27Z", CONCRETE.createDate(2009, 04, 27));
		equals("2009-04-27+03:00", CONCRETE.createDate(2009, 04, 27, 3, 0));
	}

	public void testDateTime() throws EvaluationException {
		equals("2009-04-27T10:10:00.0-05:00", CONCRETE.createDateTime(2009, 04,
				27, 10, 10, 0, -5, 0));
	}

	public void testDayTimeDuration() throws EvaluationException {
		equals("-P2DT2H3M", CONCRETE.createDayTimeDuration(false, 2, 2, 3, 0));
		equals("PT15H", CONCRETE.createDayTimeDuration(true, 0, 15, 0, 0));
	}

	public void testDecimal() throws EvaluationException {
		equals("1.337", CONCRETE.createDecimal(1.337));
	}

	public void testDouble() throws EvaluationException {
		equals("0.0", CONCRETE.createDouble(0.0));
	}

	public void testDuration() throws EvaluationException {
		equals("P2Y1M0DT5H4M2.3S", CONCRETE.createDuration(true, 2, 1, 0, 5, 4,
				2.3));
	}

	public void testFloat() throws EvaluationException {
		equals("0.0", CONCRETE.createFloat(0.0f));
	}

	public void testGDay() throws EvaluationException {
		equals("---27Z", CONCRETE.createGDay(27));
	}

	public void testGMonth() throws EvaluationException {
		equals("--04Z", CONCRETE.createGMonth(4));
	}

	public void testGMonthDay() throws EvaluationException {
		equals("--04-27Z", CONCRETE.createGMonthDay(4, 27));
	}

	public void testGYear() throws EvaluationException {
		equals("2009Z", CONCRETE.createGYear(2009));
	}

	public void testGYearMonth() throws EvaluationException {
		equals("2009-04Z", CONCRETE.createGYearMonth(2009, 4));
	}

	public void testHexBinary() throws EvaluationException {
		equals("0FB7", CONCRETE.createHexBinary("0FB7"));
	}

	public void testInteger() throws EvaluationException {
		equals("1337", CONCRETE.createInteger(1337));
	}

	public void testIri() throws EvaluationException {
		equals("http://www.w3.org/2007/rif#iri", CONCRETE
				.createIri("http://www.w3.org/2007/rif#iri"));
	}

	public void testSqName() throws EvaluationException {
		equals("http://www.w3.org/2007/rif#iri", CONCRETE
				.createSqName("http://www.w3.org/2007/rif#iri"));
	}

	public void testString() throws EvaluationException {
		equals("Ein Text", TERM.createString("Ein Text"));
	}

	public void testText() throws EvaluationException {
		equals("Ein Text@de", CONCRETE.createPlainLiteral("Ein Text@de"));
	}

	public void testTime() throws EvaluationException {
		equals("12:45:00.0Z", CONCRETE.createTime(12, 45, 0, 0, 0));
		equals("12:45:00.0+03:00", CONCRETE.createTime(12, 45, 0, 3, 0));
	}

	public void testXMLLiteral() throws EvaluationException {
		equals("<quote>Bam!</quote>@de", CONCRETE.createXMLLiteral(
				"<quote>Bam!</quote>", "de"));
	}

	public void testYearMonthDuration() throws EvaluationException {
		equals("P2009Y4M", CONCRETE.createYearMonthDuration(true, 2009, 4));
	}

	private void equals(String expected, ITerm term) throws EvaluationException {
		ITuple expectedTuple = BASIC.createTuple(CONCRETE.createPlainLiteral(expected));
		ITuple actualTuple = compute(term);

		assertEquals(expectedTuple, actualTuple);
	}

	private ITuple compute(ITerm term) throws EvaluationException {
		ToPlainLiteralBuiltin builtin = new ToPlainLiteralBuiltin(term, Y);

		ITuple arguments = BASIC.createTuple(X, Y);
		ITuple actualTuple = builtin.evaluate(arguments);

		return actualTuple;
	}

}
