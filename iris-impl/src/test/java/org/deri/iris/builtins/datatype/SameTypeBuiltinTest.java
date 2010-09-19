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

import static org.deri.iris.factory.Factory.CONCRETE;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;

import org.deri.iris.EvaluationException;
import org.deri.iris.api.terms.ITerm;

/**
 */
public class SameTypeBuiltinTest extends AbstractBooleanBuiltinTest {

	public SameTypeBuiltinTest(String name) {
		super(name);
	}

	public void testBuiltin() throws URISyntaxException {

		LinkedList<ITerm> terms = new LinkedList<ITerm>();
		ITerm term;

		term = CONCRETE.createAnyURI(new URI("http://sti-is-the-best.com/"));
		terms.add(term);
		term = CONCRETE.createBase64Binary("");
		terms.add(term);
		term = CONCRETE.createBoolean(true);
		terms.add(term);
		term = CONCRETE.createByte((byte) 3);
		terms.add(term);
		term = CONCRETE.createDate(2001, 3, 9);
		terms.add(term);
		term = CONCRETE.createDateTime(2001, 5, 2, 12, 19, 2.0, 0, 0);
		terms.add(term);
		term = CONCRETE.createDateTimeStamp(2001, 5, 2, 12, 19, 2.0, 0, 0);
		terms.add(term);
		term = CONCRETE.createDayTimeDuration(true, 14, 10, 4, 3.0);
		terms.add(term);
		term = CONCRETE.createDecimal(BigDecimal.ONE);
		terms.add(term);
		term = CONCRETE.createDouble(5.6);
		terms.add(term);
		term = CONCRETE.createDuration((long) 234);
		terms.add(term);
		term = CONCRETE.createEntity("Entity");
		terms.add(term);
		term = CONCRETE.createFloat((float) 32);
		terms.add(term);
		term = CONCRETE.createGDay(23);
		terms.add(term);
		term = CONCRETE.createGMonth(9);
		terms.add(term);
		term = CONCRETE.createGMonthDay(3, 12);
		terms.add(term);
		term = CONCRETE.createGYear(2001);
		terms.add(term);
		term = CONCRETE.createGYearMonth(1982, 12);
		terms.add(term);
		term = CONCRETE.createHexBinary("d3");
		terms.add(term);
		term = CONCRETE.createID("id0815");
		terms.add(term);
		term = CONCRETE.createIDREF("id0815#ref");
		terms.add(term);
		term = CONCRETE.createInt(14);
		terms.add(term);
		term = CONCRETE.createInteger(BigInteger.ONE);
		terms.add(term);
		term = CONCRETE.createIri("");
		terms.add(term);
		term = CONCRETE.createLanguage("en");
		terms.add(term);
		term = CONCRETE.createLong((long) 23);
		terms.add(term);
		term = CONCRETE.createName("name");
		terms.add(term);
		term = CONCRETE.createNCName("name");
		terms.add(term);
		term = CONCRETE.createNegativeInteger(BigInteger.valueOf((long) -1));
		terms.add(term);
		term = CONCRETE.createNMTOKEN("token");
		terms.add(term);
		term = CONCRETE.createNonNegativeInteger(BigInteger.ZERO);
		terms.add(term);
		term = CONCRETE.createNonPositiveInteger(BigInteger.ZERO);
		terms.add(term);
		term = CONCRETE.createNormalizedString("string");
		terms.add(term);
		term = CONCRETE.createNOTATION("namespace", "localPart");
		terms.add(term);
		term = CONCRETE.createPlainLiteral("string");
		terms.add(term);
		term = CONCRETE.createPositiveInteger(BigInteger.ONE);
		terms.add(term);
		term = CONCRETE.createQName("namespace", "localPart");
		terms.add(term);
		term = CONCRETE.createShort((short) 1);
		terms.add(term);
		term = CONCRETE.createSqName("string#bla");
		terms.add(term);
		term = CONCRETE.createTime(10, 2, 45.8, 0, 0);
		terms.add(term);
		term = CONCRETE.createToken("string");
		terms.add(term);
		term = CONCRETE.createUnsignedByte((short) 34);
		terms.add(term);
		term = CONCRETE.createUnsignedInt((long) 3423);
		terms.add(term);
		term = CONCRETE.createUnsignedLong(BigInteger.valueOf(3423));
		terms.add(term);
		term = CONCRETE.createUnsignedShort(34);
		terms.add(term);
		term = CONCRETE.createXMLLiteral("string");
		terms.add(term);
		term = CONCRETE.createYearMonthDuration(true, 3, 9);
		terms.add(term);

		assertEquals(47, terms.size());

		int i = 0;
		int k = 0;
		while (i < terms.size()) {
			ITerm term1 = terms.get(i);
			k = 0;
			while (k < terms.size()) {
				ITerm term2 = terms.get(k);

				// System.out.println("TERM 1: " + term1.getClass().getName()
				// + " Value: " + term1.toString());
				// System.out.println("TERM 2: " + term2.getClass().getName()
				// + " Value: " + term2.toString());
				// System.out.println(" i = " + i + "; k = " + k + ";  Same: "
				// + sameType(term1, term2) + "\n");

				if (k != i) {
					assertFalse(sameType(term1, term2));
				} else {
					assertTrue(sameType(term1, term2));
				}
				k++;
			}
			i++;
		}

	}

	public void testBuiltinValues() throws SecurityException,
			IllegalArgumentException, EvaluationException,
			ClassNotFoundException, NoSuchMethodException,
			InstantiationException, IllegalAccessException,
			InvocationTargetException {

		ITerm term_1, term_2;

		term_1 = CONCRETE.createNMTOKEN("nm Token");
		term_2 = CONCRETE.createNMTOKEN("nm Token");
		assertTrue(sameType(term_1, term_2));

		term_1 = CONCRETE.createNMTOKEN("A Token");
		term_2 = CONCRETE.createNMTOKEN("Another NM Token");
		assertTrue(sameType(term_1, term_2));

		term_1 = CONCRETE.createNMTOKEN("A Token");
		term_2 = CONCRETE.createSqName("SQ#Name!");
		assertFalse(sameType(term_1, term_2));

		term_1 = CONCRETE.createToken("A Token");
		term_2 = CONCRETE.createName("Name!");
		assertFalse(sameType(term_1, term_2));

		term_1 = CONCRETE.createDateTime(01, 01, 01, 10, 10, 10, 10, 0, 0);
		term_2 = CONCRETE.createDateTimeStamp(01, 01, 10, 10, 10, 10, 0, 0);
		assertFalse(sameType(term_1, term_2));

		term_1 = CONCRETE.createLong((long) 728213);
		term_2 = CONCRETE.createInteger(BigInteger.valueOf(1));
		assertFalse(sameType(term_1, term_2));

		term_1 = CONCRETE.createShort((short) 5);
		term_2 = CONCRETE.createByte((byte) 1);
		assertFalse(sameType(term_1, term_2));

		term_1 = CONCRETE.createShort((short) 5);
		term_2 = CONCRETE.createByte((byte) 1);
		assertFalse(sameType(term_1, term_2));

		term_1 = CONCRETE.createPositiveInteger(BigInteger.ONE);
		term_2 = CONCRETE.createUnsignedLong(BigInteger.TEN);
		assertFalse(sameType(term_1, term_2));

		term_1 = CONCRETE.createDouble(1.2);
		term_2 = CONCRETE.createFloat((float) 1.2);
		assertFalse(sameType(term_1, term_2));

		term_1 = CONCRETE.createNormalizedString("normalized String");
		term_2 = CONCRETE.createName("name");
		assertFalse(sameType(term_1, term_2));

		term_1 = CONCRETE.createNCName("bla bla");
		term_2 = CONCRETE.createEntity("bla bla");
		assertFalse(sameType(term_1, term_2));
	}

	public boolean sameType(ITerm one, ITerm two) {
		SameTypeBuiltin stb = new SameTypeBuiltin(one, two);
		return stb.computeResult(new ITerm[] { one, two });
	}
}
