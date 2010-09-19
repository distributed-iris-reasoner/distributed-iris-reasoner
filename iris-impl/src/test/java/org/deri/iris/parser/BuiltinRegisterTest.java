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
package org.deri.iris.parser;

import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.deri.iris.api.basics.ILiteral;
import org.deri.iris.api.basics.IRule;
import org.deri.iris.compiler.BuiltinRegister;
import org.deri.iris.compiler.Parser;
import org.deri.iris.compiler.ParserException;

/**
 * <p>
 * Tests for the <code>BuiltinRegister</code>.
 * </p>
 * <p>
 * $Id: BuiltinRegisterTest.java,v 1.3 2007-10-12 14:34:56 bazbishop237 Exp $
 * </p>
 * @author Richard Pöttler (richard dot poettler at deri dot at)
 * @version $Revision: 1.3 $
 */
public class BuiltinRegisterTest extends TestCase {

	/** The register on which to operate. */
	private BuiltinRegister reg;
	
	/** The parser. */
	private Parser parser;

	public BuiltinRegisterTest(String name) {
		super(name);
	}

	public static Test suite() {
		return new TestSuite(BuiltinRegisterTest.class, BuiltinRegisterTest.class.getSimpleName());
	}

	public void setUp() {
		reg = new BuiltinRegister();
		parser = new Parser();
	}

	/**
	 * Checks whether the core builtins gets registered correctly.
	 * @throws ParserException 
	 * @see <a href="http://sourceforge.net/tracker/index.php?func=detail&aid=1784947&group_id=167309&atid=985821">bug #1784947: simple possibility to make new core builtins parseable</a>
	 */
	public void testRegisterCoreBuiltins() throws ParserException {
		checkRegisteredBuiltin("ADD", org.deri.iris.builtins.AddBuiltin.class, 3);
		checkRegisteredBuiltin("SUBTRACT", org.deri.iris.builtins.SubtractBuiltin.class, 3);
		checkRegisteredBuiltin("MULTIPLY", org.deri.iris.builtins.MultiplyBuiltin.class, 3);
		checkRegisteredBuiltin("DIVIDE", org.deri.iris.builtins.DivideBuiltin.class, 3);
		checkRegisteredBuiltin("MODULUS", org.deri.iris.builtins.ModulusBuiltin.class, 3);

		checkRegisteredBuiltin("EQUAL", org.deri.iris.builtins.EqualBuiltin.class, 2);
		checkRegisteredBuiltin("NOT_EQUAL", org.deri.iris.builtins.NotEqualBuiltin.class, 2);

		checkRegisteredBuiltin("LESS", org.deri.iris.builtins.LessBuiltin.class, 2);
		checkRegisteredBuiltin("LESS_EQUAL", org.deri.iris.builtins.LessEqualBuiltin.class, 2);
		checkRegisteredBuiltin("GREATER", org.deri.iris.builtins.GreaterBuiltin.class, 2);
		checkRegisteredBuiltin("GREATER_EQUAL", org.deri.iris.builtins.GreaterEqualBuiltin.class, 2);

		checkRegisteredBuiltin("IS_NUMERIC", org.deri.iris.builtins.datatype.IsNumericBuiltin.class, 1);
		checkRegisteredBuiltin("IS_BASE64BINARY", org.deri.iris.builtins.datatype.IsBase64BinaryBuiltin.class, 1 );
		checkRegisteredBuiltin("IS_BOOLEAN", org.deri.iris.builtins.datatype.IsBooleanBuiltin.class, 1);
		checkRegisteredBuiltin("IS_DATE", org.deri.iris.builtins.datatype.IsDateBuiltin.class, 1);
		checkRegisteredBuiltin("IS_DATETIME", org.deri.iris.builtins.datatype.IsDateTimeBuiltin.class, 1);
		checkRegisteredBuiltin("IS_DECIMAL", org.deri.iris.builtins.datatype.IsDecimalBuiltin.class, 1);
		checkRegisteredBuiltin("IS_DOUBLE", org.deri.iris.builtins.datatype.IsDoubleBuiltin.class, 1 );
		checkRegisteredBuiltin("IS_DURATION", org.deri.iris.builtins.datatype.IsDurationBuiltin.class, 1 );
		checkRegisteredBuiltin("IS_FLOAT", org.deri.iris.builtins.datatype.IsFloatBuiltin.class, 1 );
		checkRegisteredBuiltin("IS_GDAY", org.deri.iris.builtins.datatype.IsGDayBuiltin.class, 1 );
		checkRegisteredBuiltin("IS_GMONTH", org.deri.iris.builtins.datatype.IsGMonthBuiltin.class, 1 );
		checkRegisteredBuiltin("IS_GMONTHDAY", org.deri.iris.builtins.datatype.IsGMonthDayBuiltin.class, 1 );
		checkRegisteredBuiltin("IS_GYEAR", org.deri.iris.builtins.datatype.IsGYearBuiltin.class, 1 );
		checkRegisteredBuiltin("IS_GYEARMONTH", org.deri.iris.builtins.datatype.IsGYearMonthBuiltin.class, 1 );
		checkRegisteredBuiltin("IS_HEXBINARY", org.deri.iris.builtins.datatype.IsHexBinaryBuiltin.class, 1 );
		checkRegisteredBuiltin("IS_INTEGER", org.deri.iris.builtins.datatype.IsIntegerBuiltin.class, 1);
		checkRegisteredBuiltin("IS_IRI", org.deri.iris.builtins.datatype.IsIriBuiltin.class, 1 );
		checkRegisteredBuiltin("IS_SQNAME", org.deri.iris.builtins.datatype.IsSqNameBuiltin.class, 1 );
		checkRegisteredBuiltin("IS_STRING", org.deri.iris.builtins.datatype.IsStringBuiltin.class, 1 );
		checkRegisteredBuiltin("IS_TIME", org.deri.iris.builtins.datatype.IsTimeBuiltin.class, 1 );
		
		// Datatype check builtins for new datatypes DayTimeDuration, YearMonthDuration, Text and XMLLiteral.
		checkRegisteredBuiltin("IS_DAYTIMEDURATION", org.deri.iris.builtins.datatype.IsDayTimeDurationBuiltin.class, 1 );
		checkRegisteredBuiltin("IS_YEARMONTHDURATION", org.deri.iris.builtins.datatype.IsYearMonthDurationBuiltin.class, 1 );
		checkRegisteredBuiltin("IS_PLAINLITERAL", org.deri.iris.builtins.datatype.IsPlainLiteralBuiltin.class, 1 );
		checkRegisteredBuiltin("IS_XMLLITERAL", org.deri.iris.builtins.datatype.IsXMLLiteralBuiltin.class, 1 );		
		
		checkRegisteredBuiltin("IS_DATATYPE", org.deri.iris.builtins.datatype.IsDatatypeBuiltin.class, 2 );
		checkRegisteredBuiltin("IS_NOT_DATATYPE", org.deri.iris.builtins.datatype.IsNotDatatypeBuiltin.class, 2 );
		checkRegisteredBuiltin("SAME_TYPE", org.deri.iris.builtins.datatype.SameTypeBuiltin.class, 2);
		
		// Datatype conversion builtins.
		checkRegisteredBuiltin("TO_BASE64", org.deri.iris.builtins.datatype.ToBase64Builtin.class, 2 );
		checkRegisteredBuiltin("TO_BOOLEAN", org.deri.iris.builtins.datatype.ToBooleanBuiltin.class, 2 );
		checkRegisteredBuiltin("TO_DATE", org.deri.iris.builtins.datatype.ToDateBuiltin.class, 2 );
		checkRegisteredBuiltin("TO_DATETIME", org.deri.iris.builtins.datatype.ToDateTimeBuiltin.class, 2 );
		checkRegisteredBuiltin("TO_DAYTIMEDURATION", org.deri.iris.builtins.datatype.ToDayTimeDurationBuiltin.class, 2 );
		checkRegisteredBuiltin("TO_DECIMAL", org.deri.iris.builtins.datatype.ToDecimalBuiltin.class, 2 );
		checkRegisteredBuiltin("TO_DOUBLE", org.deri.iris.builtins.datatype.ToDoubleBuiltin.class, 2 );
		checkRegisteredBuiltin("TO_DURATION", org.deri.iris.builtins.datatype.ToDurationBuiltin.class, 2 );
		checkRegisteredBuiltin("TO_FLOAT", org.deri.iris.builtins.datatype.ToFloatBuiltin.class, 2 );
		checkRegisteredBuiltin("TO_GDAY", org.deri.iris.builtins.datatype.ToGDayBuiltin.class, 2 );
		checkRegisteredBuiltin("TO_GMONTH", org.deri.iris.builtins.datatype.ToGMonthBuiltin.class, 2 );
		checkRegisteredBuiltin("TO_GMONTHDAY", org.deri.iris.builtins.datatype.ToGMonthDayBuiltin.class, 2 );
		checkRegisteredBuiltin("TO_GYEAR", org.deri.iris.builtins.datatype.ToGYearBuiltin.class, 2 );
		checkRegisteredBuiltin("TO_GYEARMONTH", org.deri.iris.builtins.datatype.ToGYearMonthBuiltin.class, 2 );
		checkRegisteredBuiltin("TO_HEXBINARY", org.deri.iris.builtins.datatype.ToHexBinaryBuiltin.class, 2 );
		checkRegisteredBuiltin("TO_INTEGER", org.deri.iris.builtins.datatype.ToIntegerBuiltin.class, 2 );
		checkRegisteredBuiltin("TO_IRI", org.deri.iris.builtins.datatype.ToIriBuiltin.class, 2 );
		checkRegisteredBuiltin("TO_STRING", org.deri.iris.builtins.datatype.ToStringBuiltin.class, 2 );
		checkRegisteredBuiltin("TO_TEXT", org.deri.iris.builtins.datatype.ToPlainLiteralBuiltin.class, 2 );
		checkRegisteredBuiltin("TO_TIME", org.deri.iris.builtins.datatype.ToTimeBuiltin.class, 2 );
		checkRegisteredBuiltin("TO_XMLLITERAL", org.deri.iris.builtins.datatype.ToXMLLiteralBuiltin.class, 2 );
		checkRegisteredBuiltin("TO_YEARMONTHDURATION", org.deri.iris.builtins.datatype.ToYearMonthDurationBuiltin.class, 2 );
		
		// Date builtins.
		checkRegisteredBuiltin("DAY_PART", org.deri.iris.builtins.date.DayPartBuiltin.class, 2 );
		checkRegisteredBuiltin("HOUR_PART", org.deri.iris.builtins.date.HourPartBuiltin.class, 2 );
		checkRegisteredBuiltin("MINUTE_PART", org.deri.iris.builtins.date.MinutePartBuiltin.class, 2 );
		checkRegisteredBuiltin("MONTH_PART", org.deri.iris.builtins.date.MonthPartBuiltin.class, 2 );
		checkRegisteredBuiltin("SECOND_PART", org.deri.iris.builtins.date.SecondPartBuiltin.class, 2 );
		checkRegisteredBuiltin("TIMEZONE_PART", org.deri.iris.builtins.date.TimezonePartBuiltin.class, 2 );
		checkRegisteredBuiltin("YEAR_PART", org.deri.iris.builtins.date.YearPartBuiltin.class, 2 );
		
		// String builtins.
		checkRegisteredBuiltin("LANG_FROM_TEXT", org.deri.iris.builtins.string.LangFromPlainLiteralBuiltin.class, 2 );
		checkRegisteredBuiltin("STRING_COMPARE", org.deri.iris.builtins.string.StringCompareBuiltin.class, 3 );
		checkRegisteredBuiltin("STRING_CONCAT", org.deri.iris.builtins.string.StringConcatBuiltin.class, 3 );
		checkRegisteredBuiltin("STRING_CONTAINS3", org.deri.iris.builtins.string.StringContainsBuiltin.class, 3 );
		checkRegisteredBuiltin("STRING_CONTAINS2", org.deri.iris.builtins.string.StringContainsWithoutCollationBuiltin.class, 2 );
		checkRegisteredBuiltin("STRING_ENDS_WITH3", org.deri.iris.builtins.string.StringEndsWithBuiltin.class, 3 );
		checkRegisteredBuiltin("STRING_ENDS_WITH2", org.deri.iris.builtins.string.StringEndsWithWithoutCollationBuiltin.class, 2 );
		checkRegisteredBuiltin("STRING_ESCAPE_HTML_URI", org.deri.iris.builtins.string.StringEscapeHtmlUriBuiltin.class, 2 );
		checkRegisteredBuiltin("STRING_FROM_TEXT", org.deri.iris.builtins.string.StringFromPlainLiteralBuiltin.class, 2 );
		checkRegisteredBuiltin("STRING_IRI_TO_URI", org.deri.iris.builtins.string.StringIriToUriBuiltin.class, 2 );
		checkRegisteredBuiltin("STRING_JOIN", org.deri.iris.builtins.string.StringJoinBuiltin.class, 4 );
		checkRegisteredBuiltin("STRING_LENGTH", org.deri.iris.builtins.string.StringLengthBuiltin.class, 2 );
		checkRegisteredBuiltin("STRING_MATCHES2", org.deri.iris.builtins.string.StringMatchesWithoutFlagsBuiltin.class, 2 );
		checkRegisteredBuiltin("STRING_MATCHES3", org.deri.iris.builtins.string.StringMatchesBuiltin.class, 3 );
		checkRegisteredBuiltin("STRING_REPLACE3", org.deri.iris.builtins.string.StringReplaceWithoutFlagsBuiltin.class, 4 );
		checkRegisteredBuiltin("STRING_REPLACE4", org.deri.iris.builtins.string.StringReplaceBuiltin.class, 5 );
		checkRegisteredBuiltin("STRING_STARTS_WITH3", org.deri.iris.builtins.string.StringStartsWithBuiltin.class, 3 );
		checkRegisteredBuiltin("STRING_STARTS_WITH2", org.deri.iris.builtins.string.StringStartsWithWithoutCollationBuiltin.class, 2 );
		checkRegisteredBuiltin("STRING_SUBSTRING_AFTER3", org.deri.iris.builtins.string.StringSubstringAfterBuiltin.class, 4 );
		checkRegisteredBuiltin("STRING_SUBSTRING_AFTER2", org.deri.iris.builtins.string.StringSubstringAfterWithoutCollationBuiltin.class, 3 );
		checkRegisteredBuiltin("STRING_SUBSTRING_BEFORE3", org.deri.iris.builtins.string.StringSubstringBeforeBuiltin.class, 4 );
		checkRegisteredBuiltin("STRING_SUBSTRING_BEFORE2", org.deri.iris.builtins.string.StringSubstringBeforeWithoutCollationBuiltin.class, 3 );
		checkRegisteredBuiltin("STRING_SUBSTRING2", org.deri.iris.builtins.string.StringSubstringUntilEndBuiltin.class, 3 );
		checkRegisteredBuiltin("STRING_SUBSTRING3", org.deri.iris.builtins.string.StringSubstringBuiltin.class, 4 );
		checkRegisteredBuiltin("STRING_TO_LOWER", org.deri.iris.builtins.string.StringToLowerBuiltin.class, 2 );
		checkRegisteredBuiltin("STRING_TO_UPPER", org.deri.iris.builtins.string.StringToUpperBuiltin.class, 2 );
		checkRegisteredBuiltin("STRING_URI_ENCODE", org.deri.iris.builtins.string.StringUriEncodeBuiltin.class, 2 );
		checkRegisteredBuiltin("TEXT_COMPARE", org.deri.iris.builtins.string.PlainLiteralCompareBuiltin.class, 3 );
		checkRegisteredBuiltin("TEXT_FROM_STRING", org.deri.iris.builtins.string.PlainLiteralFromStringBuiltin.class, 2 );
		checkRegisteredBuiltin("TEXT_FROM_STRING_LANG", org.deri.iris.builtins.string.PlainLiteralFromStringLangBuiltin.class, 3 );
		checkRegisteredBuiltin("TEXT_LENGTH", org.deri.iris.builtins.string.PlainLiteralLengthBuiltin.class, 2 );
	}

	/**
	 * Asserts whether a builtin was registered with the correct name, class
	 * and arity.
	 * @param name the name of the builtin to check (predicate symbol)
	 * @param clazz the class of the builtin to check
	 * @param arity the arity of the builtin to check
	 */
	private void checkRegisteredBuiltin(final String name, final Class<?> clazz, final int arity) throws ParserException {
		assert name != null: "The name must not be null";
		assert clazz != null: "The class must not be null";
		assert arity > 0: "The arity must be greater than 0";

		assertEquals("Could not find the class of " + name, clazz, reg.getBuiltinClass(name));
		assertEquals("Could not find the arity for " + name, arity, reg.getBuiltinArity(name));
		
		// Check if the parser correctly instantiates the builtin. 
		// For this, create a simple formula containing the builtin.
		String program = createFormula(name, arity);
		parser.parse(program);
		
		List<IRule> rules = parser.getRules();
		IRule rule = rules.get(0);
		
		List<ILiteral> body = rule.getBody();
		Class<?> literalClass = body.get(0).getAtom().getClass();
		
		assertEquals("Parser did not correctly instantiate builtin.", clazz, literalClass);
	}

	private String createFormula(String name, int arity) {
		StringBuffer predicate = new StringBuffer();
		
		// Add predicate symbol and opening bracket.
		predicate.append(name + "(");

		// Add n variables, where n = arity.
		for (int i = 0; i < arity; i++) {
			if (i > 0) {
				predicate.append(",");
			}

			predicate.append("?X");
		}

		// Add closing bracket.
		predicate.append(")");

		String formula = "foo(?X) :- " + predicate.toString() + ".";
		
		return formula;
	}

}
