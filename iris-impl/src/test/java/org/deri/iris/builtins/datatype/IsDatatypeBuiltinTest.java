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
 * Test for IsDatatypeBuiltin.
 */
public class IsDatatypeBuiltinTest extends TestCase {

	private static final ITerm X = TERM.createVariable("X");

	private static final ITerm Y = TERM.createVariable("Y");

	public IsDatatypeBuiltinTest(String name) {
		super(name);
	}

	public void testBase64() throws EvaluationException {
		String iri = "http://www.w3.org/2001/XMLSchema#base64Binary";

		check(true, CONCRETE.createBase64Binary(""), iri);
		check(false, CONCRETE.createInteger(0), iri);
	}

	public void testBoolean() throws EvaluationException {
		String iri = "http://www.w3.org/2001/XMLSchema#boolean";

		check(true, CONCRETE.createBoolean(true), iri);
		check(false, CONCRETE.createInteger(0), iri);
	}

	public void testDate() throws EvaluationException {
		String iri = "http://www.w3.org/2001/XMLSchema#date";

		check(true, CONCRETE.createDate(2009, 04, 21), iri);
		check(false, CONCRETE.createInteger(0), iri);
	}

	public void testDateTime() throws EvaluationException {
		String iri = "http://www.w3.org/2001/XMLSchema#dateTime";

		check(true, CONCRETE.createDateTime(2009, 04, 21, 12, 31, 0, 0, 0), iri);
		check(false, CONCRETE.createInteger(0), iri);
	}

	public void testDayTimeDuration() throws EvaluationException {
		String iri = "http://www.w3.org/2001/XMLSchema#dayTimeDuration";

		check(true, CONCRETE.createDayTimeDuration(true, 21, 12, 21, 0), iri);
		check(false, CONCRETE.createInteger(0), iri);
	}

	public void testDecimal() throws EvaluationException {
		String iri = "http://www.w3.org/2001/XMLSchema#decimal";

		check(true, CONCRETE.createDecimal(1.337), iri);
		check(false, CONCRETE.createInteger(0), iri);
	}

	public void testDouble() throws EvaluationException {
		String iri = "http://www.w3.org/2001/XMLSchema#double";

		check(true, CONCRETE.createDouble(0.0), iri);
		check(false, CONCRETE.createInteger(0), iri);
	}

	public void testDuration() throws EvaluationException {
		String iri = "http://www.w3.org/2001/XMLSchema#duration";

		check(true, CONCRETE.createDuration(true, 2, 1, 0, 5, 4, 2.3), iri);
		check(false, CONCRETE.createInteger(0), iri);
	}

	public void testFloat() throws EvaluationException {
		String iri = "http://www.w3.org/2001/XMLSchema#float";

		check(true, CONCRETE.createFloat(0.0f), iri);
		check(false, CONCRETE.createInteger(0), iri);
	}

	public void testGDay() throws EvaluationException {
		String iri = "http://www.w3.org/2001/XMLSchema#gDay";

		check(true, CONCRETE.createGDay(21), iri);
		check(false, CONCRETE.createInteger(0), iri);
	}

	public void testGMonth() throws EvaluationException {
		String iri = "http://www.w3.org/2001/XMLSchema#gMonth";

		check(true, CONCRETE.createGMonth(4), iri);
		check(false, CONCRETE.createInteger(0), iri);
	}

	public void testGMonthDay() throws EvaluationException {
		String iri = "http://www.w3.org/2001/XMLSchema#gMonthDay";

		check(true, CONCRETE.createGMonthDay(4, 21), iri);
		check(false, CONCRETE.createInteger(0), iri);
	}

	public void testGYear() throws EvaluationException {
		String iri = "http://www.w3.org/2001/XMLSchema#gYear";

		check(true, CONCRETE.createGYear(2009), iri);
		check(false, CONCRETE.createInteger(0), iri);
	}

	public void testGYearMonth() throws EvaluationException {
		String iri = "http://www.w3.org/2001/XMLSchema#gYearMonth";

		check(true, CONCRETE.createGYearMonth(2009, 4), iri);
		check(false, CONCRETE.createInteger(0), iri);
	}

	public void testHexBinary() throws EvaluationException {
		String iri = "http://www.w3.org/2001/XMLSchema#hexBinary";

		check(true, CONCRETE.createHexBinary("0FB7"), iri);
		check(false, CONCRETE.createInteger(0), iri);
	}

	public void testInteger() throws EvaluationException {
		String iri = "http://www.w3.org/2001/XMLSchema#integer";

		check(true, CONCRETE.createInteger(1337), iri);
		check(false, CONCRETE.createDouble(0.0), iri);
	}

	public void testIri() throws EvaluationException {
		String iri = "http://www.w3.org/2007/rif#iri";

		check(true, CONCRETE.createIri(iri), iri);
		check(false, CONCRETE.createInteger(0), iri);
	}

	public void testSqName() throws EvaluationException {
		String iri = "http://www.wsmo.org/wsml/wsml-syntax#sQName";

		ISqName name = CONCRETE.createSqName(CONCRETE
				.createIri("http://www.w3.org/2002/07/owl#"), "owl");

		check(true, name, iri);
		check(false, CONCRETE.createInteger(0), iri);
	}

	public void testPlainLiteral() throws EvaluationException {
		String iri = "http://www.w3.org/1999/02/22-rdf-syntax-ns#PlainLiteral";

		check(true, CONCRETE.createPlainLiteral("Ein Text@de"), iri);
		check(false, CONCRETE.createInteger(0), iri);
	}

	public void testTime() throws EvaluationException {
		String iri = "http://www.w3.org/2001/XMLSchema#time";

		check(true, CONCRETE.createTime(12, 45, 0, 0, 0), iri);
		check(false, CONCRETE.createInteger(0), iri);
	}

	public void testXMLLiteral() throws EvaluationException {
		String iri = "http://www.w3.org/1999/02/22-rdf-syntax-ns#XMLLiteral";

		check(true, CONCRETE.createXMLLiteral("<quote>Bam!</quote>"), iri);
		check(false, CONCRETE.createInteger(0), iri);
	}

	public void testYearMonthDuration() throws EvaluationException {
		String iri = "http://www.w3.org/2001/XMLSchema#yearMonthDuration";

		check(true, CONCRETE.createYearMonthDuration(true, 2009, 4), iri);
		check(false, CONCRETE.createInteger(0), iri);
	}

	private void check(boolean expected, ITerm term, String datatypeIRI)
			throws EvaluationException {
		IsDatatypeBuiltin builtin = new IsDatatypeBuiltin(X, Y);

		ITerm datatypeIRITerm = CONCRETE.createIri(datatypeIRI);
		ITuple arguments = BASIC.createTuple(term, datatypeIRITerm);

		ITuple actualTuple = builtin.evaluate(arguments);

		if (expected) {
			assertNotNull(actualTuple);
		} else {
			assertNull(actualTuple);
		}
	}

}
