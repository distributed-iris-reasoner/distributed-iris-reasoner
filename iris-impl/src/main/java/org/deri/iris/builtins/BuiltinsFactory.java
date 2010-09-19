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

import org.deri.iris.api.builtins.IBuiltinAtom;
import org.deri.iris.api.factory.IBuiltinsFactory;
import org.deri.iris.api.terms.ITerm;
import org.deri.iris.builtins.datatype.*;
import org.deri.iris.builtins.date.AddDayTimeDurationToDateBuiltin;
import org.deri.iris.builtins.date.AddDayTimeDurationToDateTimeBuiltin;
import org.deri.iris.builtins.date.AddDayTimeDurationsBuiltin;
import org.deri.iris.builtins.date.AddDayTimeDurationToTimeBuiltin;
import org.deri.iris.builtins.date.AddYearMonthDurationToDateBuiltin;
import org.deri.iris.builtins.date.AddYearMonthDurationToDateTimeBuiltin;
import org.deri.iris.builtins.date.AddYearMonthDurationsBuiltin;
import org.deri.iris.builtins.date.DateEqualBuiltin;
import org.deri.iris.builtins.date.DateGreaterBuiltin;
import org.deri.iris.builtins.date.DateGreaterEqualBuiltin;
import org.deri.iris.builtins.date.DateLessBuiltin;
import org.deri.iris.builtins.date.DateLessEqualBuiltin;
import org.deri.iris.builtins.date.DateNotEqualBuiltin;
import org.deri.iris.builtins.date.DateSubtractBuiltin;
import org.deri.iris.builtins.date.DateTimeEqualBuiltin;
import org.deri.iris.builtins.date.DateTimeGreaterBuiltin;
import org.deri.iris.builtins.date.DateTimeGreaterEqualBuiltin;
import org.deri.iris.builtins.date.DateTimeLessBuiltin;
import org.deri.iris.builtins.date.DateTimeLessEqualBuiltin;
import org.deri.iris.builtins.date.DateTimeNotEqualBuiltin;
import org.deri.iris.builtins.date.DateTimeSubtractBuiltin;
import org.deri.iris.builtins.date.DayFromDateBuiltin;
import org.deri.iris.builtins.date.DayFromDateTimeBuiltin;
import org.deri.iris.builtins.date.DayPartBuiltin;
import org.deri.iris.builtins.date.DayTimeDurationAddBuiltin;
import org.deri.iris.builtins.date.DayTimeDurationDivideBuiltin;
import org.deri.iris.builtins.date.DayTimeDurationDivideByDayTimeDurationBuiltin;
import org.deri.iris.builtins.date.DayTimeDurationGreaterBuiltin;
import org.deri.iris.builtins.date.DayTimeDurationGreaterEqualBuiltin;
import org.deri.iris.builtins.date.DayTimeDurationLessBuiltin;
import org.deri.iris.builtins.date.DayTimeDurationLessEqualBuiltin;
import org.deri.iris.builtins.date.DayTimeDurationMultiplyBuiltin;
import org.deri.iris.builtins.date.DayTimeDurationSubtractBuiltin;
import org.deri.iris.builtins.date.DaysFromDurationBuiltin;
import org.deri.iris.builtins.date.DurationEqualBuiltin;
import org.deri.iris.builtins.date.DurationNotEqualBuiltin;
import org.deri.iris.builtins.date.HourPartBuiltin;
import org.deri.iris.builtins.date.HoursFromDateTimeBuiltin;
import org.deri.iris.builtins.date.HoursFromDurationBuiltin;
import org.deri.iris.builtins.date.HoursFromTimeBuiltin;
import org.deri.iris.builtins.date.MinutePartBuiltin;
import org.deri.iris.builtins.date.MinutesFromDateTimeBuiltin;
import org.deri.iris.builtins.date.MinutesFromDurationBuiltin;
import org.deri.iris.builtins.date.MinutesFromTimeBuiltin;
import org.deri.iris.builtins.date.MonthFromDateBuiltin;
import org.deri.iris.builtins.date.MonthFromDateTimeBuiltin;
import org.deri.iris.builtins.date.MonthPartBuiltin;
import org.deri.iris.builtins.date.MonthsFromDurationBuiltin;
import org.deri.iris.builtins.date.SecondPartBuiltin;
import org.deri.iris.builtins.date.SecondsFromDateTimeBuiltin;
import org.deri.iris.builtins.date.SecondsFromDurationBuiltin;
import org.deri.iris.builtins.date.SecondsFromTimeBuiltin;
import org.deri.iris.builtins.date.SubtractDayTimeDurationFromDateBuiltin;
import org.deri.iris.builtins.date.SubtractDayTimeDurationFromDateTimeBuiltin;
import org.deri.iris.builtins.date.SubtractDayTimeDurationFromTimeBuiltin;
import org.deri.iris.builtins.date.SubtractYearMonthDurationFromDateBuiltin;
import org.deri.iris.builtins.date.SubtractYearMonthDurationFromDateTimeBuiltin;
import org.deri.iris.builtins.date.TimeEqualBuiltin;
import org.deri.iris.builtins.date.TimeGreaterBuiltin;
import org.deri.iris.builtins.date.TimeGreaterEqualBuiltin;
import org.deri.iris.builtins.date.TimeLessBuiltin;
import org.deri.iris.builtins.date.TimeLessEqualBuiltin;
import org.deri.iris.builtins.date.TimeNotEqualBuiltin;
import org.deri.iris.builtins.date.TimeSubtractBuiltin;
import org.deri.iris.builtins.date.TimezoneFromDateBuiltin;
import org.deri.iris.builtins.date.TimezoneFromDateTimeBuiltin;
import org.deri.iris.builtins.date.TimezoneFromTimeBuiltin;
import org.deri.iris.builtins.date.TimezonePartBuiltin;
import org.deri.iris.builtins.date.YearFromDateBuiltin;
import org.deri.iris.builtins.date.YearFromDateTimeBuiltin;
import org.deri.iris.builtins.date.YearMonthDurationAddBuiltin;
import org.deri.iris.builtins.date.YearMonthDurationDivideBuiltin;
import org.deri.iris.builtins.date.YearMonthDurationDivideByYearMonthDurationBuiltin;
import org.deri.iris.builtins.date.YearMonthDurationGreaterBuiltin;
import org.deri.iris.builtins.date.YearMonthDurationGreaterEqualBuiltin;
import org.deri.iris.builtins.date.YearMonthDurationLessBuiltin;
import org.deri.iris.builtins.date.YearMonthDurationLessEqualBuiltin;
import org.deri.iris.builtins.date.YearMonthDurationMultiplyBuiltin;
import org.deri.iris.builtins.date.YearMonthDurationSubtractBuiltin;
import org.deri.iris.builtins.date.YearPartBuiltin;
import org.deri.iris.builtins.date.YearsFromDurationBuiltin;
import org.deri.iris.builtins.numeric.NumericIntegerDivideBuiltin;
import org.deri.iris.builtins.numeric.NumericAddBuiltin;
import org.deri.iris.builtins.numeric.NumericDivideBuiltin;
import org.deri.iris.builtins.numeric.NumericEqualBuiltin;
import org.deri.iris.builtins.numeric.NumericGreaterBuiltin;
import org.deri.iris.builtins.numeric.NumericGreaterEqualBuiltin;
import org.deri.iris.builtins.numeric.NumericLessBuiltin;
import org.deri.iris.builtins.numeric.NumericLessEqualBuiltin;
import org.deri.iris.builtins.numeric.NumericMultiplyBuiltin;
import org.deri.iris.builtins.numeric.NumericNotEqualBuiltin;
import org.deri.iris.builtins.numeric.NumericSubtractBuiltin;
import org.deri.iris.builtins.string.LangFromPlainLiteralBuiltin;
import org.deri.iris.builtins.string.PlainLiteralCompareBuiltin;
import org.deri.iris.builtins.string.PlainLiteralFromStringBuiltin;
import org.deri.iris.builtins.string.PlainLiteralFromStringLangBuiltin;
import org.deri.iris.builtins.string.PlainLiteralLengthBuiltin;
import org.deri.iris.builtins.string.StringCompareBuiltin;
import org.deri.iris.builtins.string.StringConcatBuiltin;
import org.deri.iris.builtins.string.StringContainsBuiltin;
import org.deri.iris.builtins.string.StringContainsWithoutCollationBuiltin;
import org.deri.iris.builtins.string.StringEndsWithBuiltin;
import org.deri.iris.builtins.string.StringEndsWithWithoutCollationBuiltin;
import org.deri.iris.builtins.string.StringEscapeHtmlUriBuiltin;
import org.deri.iris.builtins.string.StringFromPlainLiteralBuiltin;
import org.deri.iris.builtins.string.StringIriToUriBuiltin;
import org.deri.iris.builtins.string.StringJoinBuiltin;
import org.deri.iris.builtins.string.StringLengthBuiltin;
import org.deri.iris.builtins.string.StringMatchesBuiltin;
import org.deri.iris.builtins.string.StringMatchesWithoutFlagsBuiltin;
import org.deri.iris.builtins.string.StringReplaceBuiltin;
import org.deri.iris.builtins.string.StringReplaceWithoutFlagsBuiltin;
import org.deri.iris.builtins.string.StringStartsWithBuiltin;
import org.deri.iris.builtins.string.StringStartsWithWithoutCollationBuiltin;
import org.deri.iris.builtins.string.StringSubstringAfterBuiltin;
import org.deri.iris.builtins.string.StringSubstringAfterWithoutCollationBuiltin;
import org.deri.iris.builtins.string.StringSubstringBeforeBuiltin;
import org.deri.iris.builtins.string.StringSubstringBeforeWithoutCollationBuiltin;
import org.deri.iris.builtins.string.StringSubstringBuiltin;
import org.deri.iris.builtins.string.StringSubstringUntilEndBuiltin;
import org.deri.iris.builtins.string.StringToLowerBuiltin;
import org.deri.iris.builtins.string.StringToUpperBuiltin;
import org.deri.iris.builtins.string.StringUriEncodeBuiltin;
import org.deri.iris.builtins.string.XMLLiteralEqualBuiltin;
import org.deri.iris.builtins.string.XMLLiteralNotEqualBuiltin;

/**
 * <p>
 * Factory to create all sorts of builtins.
 * </p>
 * <p>
 * $Id: BuiltinsFactory.java,v 1.4 2007-10-12 12:40:58 bazbishop237 Exp $
 * </p>
 * 
 * @author Richard Pöttler, richard dot poettler at deri dot org
 * @version $Revision: 1.4 $
 */
public class BuiltinsFactory implements IBuiltinsFactory {

	private static IBuiltinsFactory INSTANCE = new BuiltinsFactory();

	private BuiltinsFactory() {
		// this is a singelton
	}

	/**
	 * Returns the singelton instance of this factory.
	 * 
	 * @return a instane of this factory
	 */
	public static IBuiltinsFactory getInstance() {
		return INSTANCE;
	}

	public IBuiltinAtom createAddBuiltin(ITerm t0, ITerm t1, ITerm t2) {
		return new AddBuiltin(t0, t1, t2);
	}

	public IBuiltinAtom createSubtractBuiltin(ITerm t0, ITerm t1, ITerm t2) {
		return new SubtractBuiltin(t0, t1, t2);
	}

	public IBuiltinAtom createMultiplyBuiltin(ITerm t0, ITerm t1, ITerm t2) {
		return new MultiplyBuiltin(t0, t1, t2);
	}

	public IBuiltinAtom createDivideBuiltin(ITerm t0, ITerm t1, ITerm t2) {
		return new DivideBuiltin(t0, t1, t2);
	}

	public IBuiltinAtom createModulusBuiltin(ITerm t0, ITerm t1, ITerm t2) {
		return new ModulusBuiltin(t0, t1, t2);
	}

	public IBuiltinAtom createEqual(ITerm t0, ITerm t1) {
		return new EqualBuiltin(t0, t1);
	}

	public IBuiltinAtom createGreater(ITerm t0, ITerm t1) {
		return new GreaterBuiltin(t0, t1);
	}

	public IBuiltinAtom createGreaterEqual(ITerm t0, ITerm t1) {
		return new GreaterEqualBuiltin(t0, t1);
	}

	public IBuiltinAtom createLess(ITerm t0, ITerm t1) {
		return new LessBuiltin(t0, t1);
	}

	public IBuiltinAtom createLessEqual(ITerm t0, ITerm t1) {
		return new LessEqualBuiltin(t0, t1);
	}

	public IBuiltinAtom createUnequal(ITerm t0, ITerm t1) {
		return new NotEqualBuiltin(t0, t1);
	}

	public IBuiltinAtom createExactEqual(ITerm t0, ITerm t1) {
		return new ExactEqualBuiltin(t0, t1);
	}

	public IBuiltinAtom createNotExactEqual(ITerm t0, ITerm t1) {
		return new NotExactEqualBuiltin(t0, t1);
	}

	// check D3.1.4 Defining the features of the WSML-Rule v2.0 language

	public IBuiltinAtom createNumericModulus(ITerm... terms) {
		return new ModulusBuiltin(terms);
	}

	public IBuiltinAtom createStringCompare(ITerm... terms) {
		return new StringCompareBuiltin(terms);
	}

	public IBuiltinAtom createStringConcat(ITerm... terms) {
		return new StringConcatBuiltin(terms);
	}

	public IBuiltinAtom createStringJoin(ITerm... terms) {
		return new StringJoinBuiltin(terms);
	}

	public IBuiltinAtom createStringSubstring(ITerm... terms) {
		if (terms.length <= 3) {
			return new StringSubstringUntilEndBuiltin(terms);
		} else {
			return new StringSubstringBuiltin(terms);
		}
	}

	public IBuiltinAtom createStringLength(ITerm... terms) {
		return new StringLengthBuiltin(terms);
	}

	public IBuiltinAtom createStringToUpper(ITerm... terms) {
		return new StringToUpperBuiltin(terms);
	}

	public IBuiltinAtom createStringToLower(ITerm... terms) {
		return new StringToLowerBuiltin(terms);
	}

	public IBuiltinAtom createStringUriEncode(ITerm... terms) {
		return new StringUriEncodeBuiltin(terms);
	}

	public IBuiltinAtom createStringIriToUri(ITerm... terms) {
		return new StringIriToUriBuiltin(terms);
	}

	public IBuiltinAtom createStringEscapeHtmlUri(ITerm... terms) {
		return new StringEscapeHtmlUriBuiltin(terms);
	}

	public IBuiltinAtom createStringSubstringBefore(ITerm... terms) {
		if (terms.length <= 3) {
			return new StringSubstringBeforeWithoutCollationBuiltin(terms);
		} else {
			return new StringSubstringBeforeBuiltin(terms);
		}
	}

	public IBuiltinAtom createStringSubstringAfter(ITerm... terms) {
		if (terms.length <= 3) {
			return new StringSubstringAfterWithoutCollationBuiltin(terms);
		} else {
			return new StringSubstringAfterBuiltin(terms);
		}
	}

	public IBuiltinAtom createStringReplace(ITerm... terms) {
		if (terms.length <= 4) {
			return new StringReplaceWithoutFlagsBuiltin(terms);
		} else {
			return new StringReplaceBuiltin(terms);
		}
	}

	public IBuiltinAtom createStringContains(ITerm... terms) {
		if (terms.length <= 2) {
			return new StringContainsWithoutCollationBuiltin(terms);
		} else {
			return new StringContainsBuiltin(terms);
		}
	}

	public IBuiltinAtom createStringStartsWith(ITerm... terms) {
		if (terms.length <= 2) {
			return new StringStartsWithWithoutCollationBuiltin(terms);
		} else {
			return new StringStartsWithBuiltin(terms);
		}
	}

	public IBuiltinAtom createStringEndsWith(ITerm... terms) {
		if (terms.length <= 2) {
			return new StringEndsWithWithoutCollationBuiltin(terms);
		} else {
			return new StringEndsWithBuiltin(terms);
		}
	}

	public IBuiltinAtom createStringMatches(ITerm... terms) {
		if (terms.length <= 2) {
			return new StringMatchesWithoutFlagsBuiltin(terms);
		} else {
			return new StringMatchesBuiltin(terms);
		}
	}

	public IBuiltinAtom createYearPart(ITerm... terms) {
		return new YearPartBuiltin(terms);
	}

	public IBuiltinAtom createMonthPart(ITerm... terms) {
		return new MonthPartBuiltin(terms);
	}

	public IBuiltinAtom createDayPart(ITerm... terms) {
		return new DayPartBuiltin(terms);
	}

	public IBuiltinAtom createHourPart(ITerm... terms) {
		return new HourPartBuiltin(terms);
	}

	public IBuiltinAtom createMinutePart(ITerm... terms) {
		return new MinutePartBuiltin(terms);
	}

	public IBuiltinAtom createSecondPart(ITerm... terms) {
		return new SecondPartBuiltin(terms);
	}

	public IBuiltinAtom createTimezonePart(ITerm... terms) {
		return new TimezonePartBuiltin(terms);
	}

	public IBuiltinAtom createTextFromStringLang(ITerm... terms) {
		return new PlainLiteralFromStringLangBuiltin(terms);
	}

	public IBuiltinAtom createStringFromText(ITerm... terms) {
		return new StringFromPlainLiteralBuiltin(terms);
	}

	public IBuiltinAtom createLangFromText(ITerm... terms) {
		return new LangFromPlainLiteralBuiltin(terms);
	}

	public IBuiltinAtom createTextFromString(ITerm... terms) {
		return new PlainLiteralFromStringBuiltin(terms);
	}

	public IBuiltinAtom createTextCompare(ITerm... terms) {
		return new PlainLiteralCompareBuiltin(terms);
	}

	public IBuiltinAtom createTextLength(ITerm... terms) {
		return new PlainLiteralLengthBuiltin(terms);
	}

	public IBuiltinAtom createFalse() {
		return new FalseBuiltin(new ITerm[0]);
	}

	public IBuiltinAtom createTrue() {
		return new TrueBuiltin(new ITerm[0]);
	}

	public IBuiltinAtom createToBase64Binary(ITerm... terms) {
		return new ToBase64Builtin(terms);
	}

	public IBuiltinAtom createToBoolean(ITerm... terms) {
		return new ToBooleanBuiltin(terms);
	}

	public IBuiltinAtom createToDate(ITerm... terms) {
		return new ToDateBuiltin(terms);
	}

	public IBuiltinAtom createToDateTime(ITerm... terms) {
		return new ToDateTimeBuiltin(terms);
	}

	public IBuiltinAtom createToDayTimeDuration(ITerm... terms) {
		return new ToDayTimeDurationBuiltin(terms);
	}

	public IBuiltinAtom createToDecimal(ITerm... terms) {
		return new ToDecimalBuiltin(terms);
	}

	public IBuiltinAtom createToDouble(ITerm... terms) {
		return new ToDoubleBuiltin(terms);
	}

	public IBuiltinAtom createToDuration(ITerm... terms) {
		return new ToDurationBuiltin(terms);
	}

	public IBuiltinAtom createToFloat(ITerm... terms) {
		return new ToFloatBuiltin(terms);
	}

	public IBuiltinAtom createToGDay(ITerm... terms) {
		return new ToGDayBuiltin(terms);
	}

	public IBuiltinAtom createToGMonth(ITerm... terms) {
		return new ToGMonthBuiltin(terms);
	}

	public IBuiltinAtom createToGMonthDay(ITerm... terms) {
		return new ToGMonthDayBuiltin(terms);
	}

	public IBuiltinAtom createToGYear(ITerm... terms) {
		return new ToGYearBuiltin(terms);
	}

	public IBuiltinAtom createToGYearMonth(ITerm... terms) {
		return new ToGYearMonthBuiltin(terms);
	}

	public IBuiltinAtom createToHexBinary(ITerm... terms) {
		return new ToHexBinaryBuiltin(terms);
	}

	public IBuiltinAtom createToInteger(ITerm... terms) {
		return new ToIntegerBuiltin(terms);
	}

	public IBuiltinAtom createToIRI(ITerm... terms) {
		return new ToIriBuiltin(terms);
	}

	public IBuiltinAtom createToString(ITerm... terms) {
		return new ToStringBuiltin(terms);
	}

	public IBuiltinAtom createToText(ITerm... terms) {
		return new ToPlainLiteralBuiltin(terms);
	}

	public IBuiltinAtom createToTime(ITerm... terms) {
		return new ToTimeBuiltin(terms);
	}

	public IBuiltinAtom createToXMLLiteral(ITerm... terms) {
		return new ToXMLLiteralBuiltin(terms);
	}

	public IBuiltinAtom createToYearMonthDuration(ITerm... terms) {
		return new ToYearMonthDurationBuiltin(terms);
	}

	public IBuiltinAtom createIsBase64Binary(ITerm... terms) {
		return new IsBase64BinaryBuiltin(terms);
	}

	public IBuiltinAtom createIsBoolean(ITerm... terms) {
		return new IsBooleanBuiltin(terms);
	}

	public IBuiltinAtom createIsDate(ITerm... terms) {
		return new IsDateBuiltin(terms);
	}

	public IBuiltinAtom createIsDateTime(ITerm... terms) {
		return new IsDateTimeBuiltin(terms);
	}

	public IBuiltinAtom createIsDayTimeDuration(ITerm... terms) {
		return new IsDayTimeDurationBuiltin(terms);
	}

	public IBuiltinAtom createIsDecimal(ITerm... terms) {
		return new IsDecimalBuiltin(terms);
	}

	public IBuiltinAtom createIsDouble(ITerm... terms) {
		return new IsDoubleBuiltin(terms);
	}

	public IBuiltinAtom createIsDuration(ITerm... terms) {
		return new IsDurationBuiltin(terms);
	}

	public IBuiltinAtom createIsFloat(ITerm... terms) {
		return new IsFloatBuiltin(terms);
	}

	public IBuiltinAtom createIsGDay(ITerm... terms) {
		return new IsGDayBuiltin(terms);
	}

	public IBuiltinAtom createIsGMonth(ITerm... terms) {
		return new IsGMonthBuiltin(terms);
	}

	public IBuiltinAtom createIsGMonthDay(ITerm... terms) {
		return new IsGMonthBuiltin(terms);
	}

	public IBuiltinAtom createIsGYear(ITerm... terms) {
		return new IsGYearBuiltin(terms);
	}

	public IBuiltinAtom createIsGYearMonth(ITerm... terms) {
		return new IsGYearMonthBuiltin(terms);
	}

	public IBuiltinAtom createIsHexBinary(ITerm... terms) {
		return new IsHexBinaryBuiltin(terms);
	}

	public IBuiltinAtom createIsInteger(ITerm... terms) {
		return new IsIntegerBuiltin(terms);
	}

	public IBuiltinAtom createIsIRI(ITerm... terms) {
		return new IsIriBuiltin(terms);
	}

	public IBuiltinAtom createIsString(ITerm... terms) {
		return new IsStringBuiltin(terms);
	}

	public IBuiltinAtom createIsPlainLiteral(ITerm... terms) {
		return new IsPlainLiteralBuiltin(terms);
	}

	public IBuiltinAtom createIsTime(ITerm... terms) {
		return new IsTimeBuiltin(terms);
	}

	public IBuiltinAtom createIsXMLLiteral(ITerm... terms) {
		return new IsXMLLiteralBuiltin(terms);
	}

	public IBuiltinAtom createIsYearMonthDuration(ITerm... terms) {
		return new IsYearMonthDurationBuiltin(terms);
	}

	public IBuiltinAtom createIsAnyURI(ITerm... terms) {
		return new IsAnyURIBuiltin(terms);
	}

	public IBuiltinAtom createIsByte(ITerm... terms) {
		return new IsByteBuiltin(terms);
	}

	public IBuiltinAtom createBooleanNot(ITerm... terms) {
		return new BooleanNotBuiltin(terms);
	}

	public IBuiltinAtom createIsLanguage(ITerm... terms) {
		return new IsLanguageBuiltin(terms);
	}

	public IBuiltinAtom createIsLong(ITerm... terms) {
		return new IsLongBuiltin(terms);
	}

	public IBuiltinAtom createIsNCName(ITerm... terms) {
		return new IsNCNameBuiltin(terms);
	}

	public IBuiltinAtom createIsNMTOKEN(ITerm... terms) {
		return new IsNMTOKENBuiltin(terms);
	}

	public IBuiltinAtom createIsName(ITerm... terms) {
		return new IsNameBuiltin(terms);
	}

	public IBuiltinAtom createIsNegativeInteger(ITerm... terms) {
		return new IsNegativeIntegerBuiltin(terms);
	}

	public IBuiltinAtom createIsNonNegativeInteger(ITerm... terms) {
		return new IsNonNegativeIntegerBuiltin(terms);
	}

	public IBuiltinAtom createIsNonPositiveInteger(ITerm... terms) {
		return new IsNonPositiveIntegerBuiltin(terms);
	}

	public IBuiltinAtom createIsNormalizedString(ITerm... terms) {
		return new IsNormalizedStringBuiltin(terms);
	}

	public IBuiltinAtom createIsPositiveInteger(ITerm... terms) {
		return new IsPositiveIntegerBuiltin(terms);
	}

	public IBuiltinAtom createIsShort(ITerm... terms) {
		return new IsShortBuiltin(terms);
	}

	public IBuiltinAtom createIsToken(ITerm... terms) {
		return new IsTokenBuiltin(terms);
	}

	public IBuiltinAtom createIsUnsignedByte(ITerm... terms) {
		return new IsUnsignedByteBuiltin(terms);
	}

	public IBuiltinAtom createIsUnsignedInt(ITerm... terms) {
		return new IsUnsignedIntBuiltin(terms);
	}

	public IBuiltinAtom createIsUnsignedLong(ITerm... terms) {
		return new IsUnsignedLongBuiltin(terms);
	}

	public IBuiltinAtom createIsUnsignedShort(ITerm... terms) {
		return new IsUnsignedShortBuiltin(terms);
	}

	public IBuiltinAtom createIsInt(ITerm... terms) {
		return new IsIntBuiltin(terms);
	}

	public IBuiltinAtom createNumericIntegerDivide(ITerm... terms) {
		return new NumericIntegerDivideBuiltin(terms);
	}

	public IBuiltinAtom createIsNotAnyURI(ITerm... terms) {
		return new IsNotAnyURIBuiltin(terms);
	}

	public IBuiltinAtom createIsNotBase64Binary(ITerm... terms) {
		return new IsNotBase64BinaryBuiltin(terms);
	}

	public IBuiltinAtom createIsNotBoolean(ITerm... terms) {
		return new IsNotBooleanBuiltin(terms);
	}

	public IBuiltinAtom createIsNotByte(ITerm... terms) {
		return new IsNotByteBuiltin(terms);
	}

	public IBuiltinAtom createIsNotDate(ITerm... terms) {
		return new IsNotDateBuiltin(terms);
	}

	public IBuiltinAtom createIsNotDateTime(ITerm... terms) {
		return new IsNotDateTimeBuiltin(terms);
	}

	public IBuiltinAtom createIsNotDayTimeDuration(ITerm... terms) {
		return new IsNotDayTimeDurationBuiltin(terms);
	}

	public IBuiltinAtom createIsNotDecimal(ITerm... terms) {
		return new IsNotDecimalBuiltin(terms);
	}

	public IBuiltinAtom createIsNotDouble(ITerm... terms) {
		return new IsNotDoubleBuiltin(terms);
	}

	public IBuiltinAtom createIsNotDuration(ITerm... terms) {
		return new IsNotDurationBuiltin(terms);
	}

	public IBuiltinAtom createIsNotFloat(ITerm... terms) {
		return new IsNotFloatBuiltin(terms);
	}

	public IBuiltinAtom createIsNotGDay(ITerm... terms) {
		return new IsNotGDayBuiltin(terms);
	}

	public IBuiltinAtom createIsNotGMonth(ITerm... terms) {
		return new IsNotGMonthBuiltin(terms);
	}

	public IBuiltinAtom createIsNotGMonthDay(ITerm... terms) {
		return new IsNotGMonthDayBuiltin(terms);
	}

	public IBuiltinAtom createIsNotGYear(ITerm... terms) {
		return new IsNotGYearBuiltin(terms);
	}

	public IBuiltinAtom createIsNotGYearMonth(ITerm... terms) {
		return new IsNotGYearMonthBuiltin(terms);
	}

	public IBuiltinAtom createIsNotHexBinary(ITerm... terms) {
		return new IsNotHexBinaryBuiltin(terms);
	}

	public IBuiltinAtom createIsNotIRI(ITerm... terms) {
		return new IsNotIriBuiltin(terms);
	}

	public IBuiltinAtom createIsNotInt(ITerm... terms) {
		return new IsNotIntBuiltin(terms);
	}

	public IBuiltinAtom createIsNotInteger(ITerm... terms) {
		return new IsNotIntegerBuiltin(terms);
	}

	public IBuiltinAtom createIsNotLanguage(ITerm... terms) {
		return new IsNotLanguageBuiltin(terms);
	}

	public IBuiltinAtom createIsNotLong(ITerm... terms) {
		return new IsNotLongBuiltin(terms);
	}

	public IBuiltinAtom createIsNotNCName(ITerm... terms) {
		return new IsNotNCNameBuiltin(terms);
	}

	public IBuiltinAtom createIsNotNMTOKEN(ITerm... terms) {
		return new IsNotNMTOKENBuiltin(terms);
	}

	public IBuiltinAtom createIsNotName(ITerm... terms) {
		return new IsNotNameBuiltin(terms);
	}

	public IBuiltinAtom createIsNotNegativeInteger(ITerm... terms) {
		return new IsNotNegativeIntegerBuiltin(terms);
	}

	public IBuiltinAtom createIsNotNonNegativeInteger(ITerm... terms) {
		return new IsNotNonNegativeIntegerBuiltin(terms);
	}

	public IBuiltinAtom createIsNotNonPositiveInteger(ITerm... terms) {
		return new IsNotNonPositiveIntegerBuiltin(terms);
	}

	public IBuiltinAtom createIsNotNormalizedString(ITerm... terms) {
		return new IsNotNormalizedStringBuiltin(terms);
	}

	public IBuiltinAtom createIsNotPositiveInteger(ITerm... terms) {
		return new IsNotPositiveIntegerBuiltin(terms);
	}

	public IBuiltinAtom createIsNotShort(ITerm... terms) {
		return new IsNotShortBuiltin(terms);
	}

	public IBuiltinAtom createIsNotString(ITerm... terms) {
		return new IsNotStringBuiltin(terms);
	}

	public IBuiltinAtom createIsNotPlainLiteral(ITerm... terms) {
		return new IsNotPlainLiteralBuiltin(terms);
	}

	public IBuiltinAtom createIsNotTime(ITerm... terms) {
		return new IsNotTimeBuiltin(terms);
	}

	public IBuiltinAtom createIsNotToken(ITerm... terms) {
		return new IsNotTokenBuiltin(terms);
	}

	public IBuiltinAtom createIsNotUnsignedByte(ITerm... terms) {
		return new IsNotUnsignedByteBuiltin(terms);
	}

	public IBuiltinAtom createIsNotUnsignedInt(ITerm... terms) {
		return new IsNotUnsignedIntBuiltin(terms);
	}

	public IBuiltinAtom createIsNotUnsignedLong(ITerm... terms) {
		return new IsNotUnsignedLongBuiltin(terms);
	}

	public IBuiltinAtom createIsNotUnsignedShort(ITerm... terms) {
		return new IsNotUnsignedShortBuiltin(terms);
	}

	public IBuiltinAtom createIsNotXMLLiteral(ITerm... terms) {
		return new IsNotXMLLiteralBuiltin(terms);
	}

	public IBuiltinAtom createIsNotYearMonthDuration(ITerm... terms) {
		return new IsNotYearMonthDurationBuiltin(terms);
	}

	public IBuiltinAtom createIsDatatype(ITerm... terms) {
		return new IsDatatypeBuiltin(terms);
	}

	public IBuiltinAtom createIsNotDatatype(ITerm... terms) {
		return new IsNotDatatypeBuiltin(terms);
	}

	public IBuiltinAtom createIsDateTimeStamp(ITerm... terms) {
		return new IsDateTimeStampBuiltin(terms);
	}

	public IBuiltinAtom createIsNotDateTimeStamp(ITerm... terms) {
		return new IsNotDateTimeStampBuiltin(terms);
	}

	public IBuiltinAtom createAddDayTimeDurationToDate(ITerm... terms) {
		return new AddDayTimeDurationToDateBuiltin(terms);
	}

	public IBuiltinAtom createAddDayTimeDurationToDateTime(ITerm... terms) {
		return new AddDayTimeDurationToDateTimeBuiltin(terms);
	}

	public IBuiltinAtom createAddDayTimeDurationToTime(ITerm... terms) {
		return new AddDayTimeDurationToTimeBuiltin(terms);
	}
	
	public IBuiltinAtom createAddDayTimeDurations(ITerm... terms) {
		return new AddDayTimeDurationsBuiltin(terms);
	}
	
	public IBuiltinAtom createAddYearMonthDurations(ITerm... terms) {
		return new AddYearMonthDurationsBuiltin(terms);
	}

	public IBuiltinAtom createAddYearMonthDurationToDate(ITerm... terms) {
		return new AddYearMonthDurationToDateBuiltin(terms);
	}

	public IBuiltinAtom createAddYearMonthDurationToDateTime(ITerm... terms) {
		return new AddYearMonthDurationToDateTimeBuiltin(terms);
	}

	public IBuiltinAtom createBooleanEqual(ITerm... terms) {
		return new BooleanEqualBuiltin(terms);
	}

	public IBuiltinAtom createBooleanGreater(ITerm... terms) {
		return new BooleanGreaterBuiltin(terms);
	}

	public IBuiltinAtom createBooleanLess(ITerm... terms) {
		return new BooleanLessBuiltin(terms);
	}

	public IBuiltinAtom createDateEqual(ITerm... terms) {
		return new DateEqualBuiltin(terms);
	}

	public IBuiltinAtom createDateGreater(ITerm... terms) {
		return new DateGreaterBuiltin(terms);
	}

	public IBuiltinAtom createDateGreaterEqual(ITerm... terms) {
		return new DateGreaterEqualBuiltin(terms);
	}

	public IBuiltinAtom createDateLess(ITerm... terms) {
		return new DateLessBuiltin(terms);
	}

	public IBuiltinAtom createDateLessEqual(ITerm... terms) {
		return new DateLessEqualBuiltin(terms);
	}

	public IBuiltinAtom createDateNotEqual(ITerm... terms) {
		return new DateNotEqualBuiltin(terms);
	}

	public IBuiltinAtom createDateSubtract(ITerm... terms) {
		return new DateSubtractBuiltin(terms);
	}

	public IBuiltinAtom createDateTimeEqual(ITerm... terms) {
		return new DateTimeEqualBuiltin(terms);
	}

	public IBuiltinAtom createDateTimeGreater(ITerm... terms) {
		return new DateTimeGreaterBuiltin(terms);
	}

	public IBuiltinAtom createDateTimeGreaterEqual(ITerm... terms) {
		return new DateTimeGreaterEqualBuiltin(terms);
	}

	public IBuiltinAtom createDateTimeLess(ITerm... terms) {
		return new DateTimeLessBuiltin(terms);
	}

	public IBuiltinAtom createDateTimeLessEqual(ITerm... terms) {
		return new DateTimeLessEqualBuiltin(terms);
	}

	public IBuiltinAtom createDateTimeNotEqual(ITerm... terms) {
		return new DateTimeNotEqualBuiltin(terms);
	}

	public IBuiltinAtom createDateTimeSubtract(ITerm... terms) {
		return new DateTimeSubtractBuiltin(terms);
	}

	public IBuiltinAtom createDayFromDate(ITerm... terms) {
		return new DayFromDateBuiltin(terms);
	}

	public IBuiltinAtom createDayFromDateTime(ITerm... terms) {
		return new DayFromDateTimeBuiltin(terms);
	}

	public IBuiltinAtom createDayTimeDurationAdd(ITerm... terms) {
		return new DayTimeDurationAddBuiltin(terms);
	}

	public IBuiltinAtom createDayTimeDurationDivide(ITerm... terms) {
		return new DayTimeDurationDivideBuiltin(terms);
	}

	public IBuiltinAtom createDayTimeDurationDivideByDayTimeDuration(
			ITerm... terms) {
		return new DayTimeDurationDivideByDayTimeDurationBuiltin(terms);
	}

	public IBuiltinAtom createDayTimeDurationGreater(ITerm... terms) {
		return new DayTimeDurationGreaterBuiltin(terms);
	}

	public IBuiltinAtom createDayTimeDurationGreaterEqual(ITerm... terms) {
		return new DayTimeDurationGreaterEqualBuiltin(terms);
	}

	public IBuiltinAtom createDayTimeDurationLess(ITerm... terms) {
		return new DayTimeDurationLessBuiltin(terms);
	}

	public IBuiltinAtom createDayTimeDurationLessEqual(ITerm... terms) {
		return new DayTimeDurationLessEqualBuiltin(terms);
	}

	public IBuiltinAtom createDayTimeDurationMultiply(ITerm... terms) {
		return new DayTimeDurationMultiplyBuiltin(terms);
	}

	public IBuiltinAtom createDayTimeDurationSubtract(ITerm... terms) {
		return new DayTimeDurationSubtractBuiltin(terms);
	}

	public IBuiltinAtom createDaysFromDuration(ITerm... terms) {
		return new DaysFromDurationBuiltin(terms);
	}

	public IBuiltinAtom createDurationEqual(ITerm... terms) {
		return new DurationEqualBuiltin(terms);
	}

	public IBuiltinAtom createDurationNotEqual(ITerm... terms) {
		return new DurationNotEqualBuiltin(terms);
	}

	public IBuiltinAtom createHoursFromDateTime(ITerm... terms) {
		return new HoursFromDateTimeBuiltin(terms);
	}

	public IBuiltinAtom createHoursFromDuration(ITerm... terms) {
		return new HoursFromDurationBuiltin(terms);
	}

	public IBuiltinAtom createHoursFromTime(ITerm... terms) {
		return new HoursFromTimeBuiltin(terms);
	}

	public IBuiltinAtom createMinutesFromDateTime(ITerm... terms) {
		return new MinutesFromDateTimeBuiltin(terms);
	}

	public IBuiltinAtom createMinutesFromDuration(ITerm... terms) {
		return new MinutesFromDurationBuiltin(terms);
	}

	public IBuiltinAtom createMinutesFromTime(ITerm... terms) {
		return new MinutesFromTimeBuiltin(terms);
	}

	public IBuiltinAtom createMonthFromDate(ITerm... terms) {
		return new MonthFromDateBuiltin(terms);
	}

	public IBuiltinAtom createMonthFromDateTime(ITerm... terms) {
		return new MonthFromDateTimeBuiltin(terms);
	}

	public IBuiltinAtom createMonthsFromDuration(ITerm... terms) {
		return new MonthsFromDurationBuiltin(terms);
	}

	public IBuiltinAtom createNumericAdd(ITerm... terms) {
		return new NumericAddBuiltin(terms);
	}

	public IBuiltinAtom createNumericDivide(ITerm... terms) {
		return new NumericDivideBuiltin(terms);
	}

	public IBuiltinAtom createNumericEqual(ITerm... terms) {
		return new NumericEqualBuiltin(terms);
	}

	public IBuiltinAtom createNumericGreater(ITerm... terms) {
		return new NumericGreaterBuiltin(terms);
	}

	public IBuiltinAtom createNumericGreaterEqual(ITerm... terms) {
		return new NumericGreaterEqualBuiltin(terms);
	}

	public IBuiltinAtom createNumericLess(ITerm... terms) {
		return new NumericLessBuiltin(terms);
	}

	public IBuiltinAtom createNumericLessEqual(ITerm... terms) {
		return new NumericLessEqualBuiltin(terms);
	}

	public IBuiltinAtom createNumericMultiply(ITerm... terms) {
		return new NumericMultiplyBuiltin(terms);
	}

	public IBuiltinAtom createNumericNotEqual(ITerm... terms) {
		return new NumericNotEqualBuiltin(terms);
	}

	public IBuiltinAtom createNumericSubtract(ITerm... terms) {
		return new NumericSubtractBuiltin(terms);
	}

	public IBuiltinAtom createSecondsFromDateTime(ITerm... terms) {
		return new SecondsFromDateTimeBuiltin(terms);
	}

	public IBuiltinAtom createSecondsFromDuration(ITerm... terms) {
		return new SecondsFromDurationBuiltin(terms);
	}

	public IBuiltinAtom createSecondsFromTime(ITerm... terms) {
		return new SecondsFromTimeBuiltin(terms);
	}

	public IBuiltinAtom createSubtractDayTimeDurationFromDate(ITerm... terms) {
		return new SubtractDayTimeDurationFromDateBuiltin(terms);
	}

	public IBuiltinAtom createSubtractDayTimeDurationFromDateTime(
			ITerm... terms) {
		return new SubtractDayTimeDurationFromDateTimeBuiltin(terms);
	}

	public IBuiltinAtom createSubtractDayTimeDurationFromTime(ITerm... terms) {
		return new SubtractDayTimeDurationFromTimeBuiltin(terms);
	}

	public IBuiltinAtom createSubtractYearMonthDurationFromDate(ITerm... terms) {
		return new SubtractYearMonthDurationFromDateBuiltin(terms);
	}

	public IBuiltinAtom createSubtractYearMonthDurationFromDateTime(
			ITerm... terms) {
		return new SubtractYearMonthDurationFromDateTimeBuiltin(terms);
	}

	public IBuiltinAtom createTimeEqual(ITerm... terms) {
		return new TimeEqualBuiltin(terms);
	}

	public IBuiltinAtom createTimeGreater(ITerm... terms) {
		return new TimeGreaterBuiltin(terms);
	}

	public IBuiltinAtom createTimeGreaterEqual(ITerm... terms) {
		return new TimeGreaterEqualBuiltin(terms);
	}

	public IBuiltinAtom createTimeLess(ITerm... terms) {
		return new TimeLessBuiltin(terms);
	}

	public IBuiltinAtom createTimeLessEqual(ITerm... terms) {
		return new TimeLessEqualBuiltin(terms);
	}

	public IBuiltinAtom createTimeNotEqual(ITerm... terms) {
		return new TimeNotEqualBuiltin(terms);
	}

	public IBuiltinAtom createTimeSubtract(ITerm... terms) {
		return new TimeSubtractBuiltin(terms);
	}

	public IBuiltinAtom createTimezoneFromDate(ITerm... terms) {
		return new TimezoneFromDateBuiltin(terms);
	}

	public IBuiltinAtom createTimezoneFromDateTime(ITerm... terms) {
		return new TimezoneFromDateTimeBuiltin(terms);
	}

	public IBuiltinAtom createTimezoneFromTime(ITerm... terms) {
		return new TimezoneFromTimeBuiltin(terms);
	}

	public IBuiltinAtom createXMLLiteralEqual(ITerm... terms) {
		return new XMLLiteralEqualBuiltin(terms);
	}

	public IBuiltinAtom createXMLLiteralNotEqual(ITerm... terms) {
		return new XMLLiteralNotEqualBuiltin(terms);
	}

	public IBuiltinAtom createYearFromDate(ITerm... terms) {
		return new YearFromDateBuiltin(terms);
	}

	public IBuiltinAtom createYearFromDateTime(ITerm... terms) {
		return new YearFromDateTimeBuiltin(terms);
	}

	public IBuiltinAtom createYearMonthDurationAdd(ITerm... terms) {
		return new YearMonthDurationAddBuiltin(terms);
	}

	public IBuiltinAtom createYearMonthDurationDivide(ITerm... terms) {
		return new YearMonthDurationDivideBuiltin(terms);
	}

	public IBuiltinAtom createYearMonthDurationDivideByYearMonthDuration(
			ITerm... terms) {
		return new YearMonthDurationDivideByYearMonthDurationBuiltin(terms);
	}

	public IBuiltinAtom createYearMonthDurationGreater(ITerm... terms) {
		return new YearMonthDurationGreaterBuiltin(terms);
	}

	public IBuiltinAtom createYearMonthDurationGreaterEqual(ITerm... terms) {
		return new YearMonthDurationGreaterEqualBuiltin(terms);
	}

	public IBuiltinAtom createYearMonthDurationLess(ITerm... terms) {
		return new YearMonthDurationLessBuiltin(terms);
	}

	public IBuiltinAtom createYearMonthDurationLessEqual(ITerm... terms) {
		return new YearMonthDurationLessEqualBuiltin(terms);
	}

	public IBuiltinAtom createYearMonthDurationMultiply(ITerm... terms) {
		return new YearMonthDurationMultiplyBuiltin(terms);
	}

	public IBuiltinAtom createYearMonthDurationSubtract(ITerm... terms) {
		return new YearMonthDurationSubtractBuiltin(terms);
	}

	public IBuiltinAtom createYearsFromDuration(ITerm... terms) {
		return new YearsFromDurationBuiltin(terms);
	}

	public IBuiltinAtom createIriString(ITerm... terms) {
		return new IriStringBuiltin(terms);
	}

	@Override
	public IBuiltinAtom createAddBuiltin(ITerm... terms) {
		return new AddBuiltin(terms);
	}

	@Override
	public IBuiltinAtom createDivideBuiltin(ITerm... terms) {
		return new DivideBuiltin(terms);
	}

	@Override
	public IBuiltinAtom createEqual(ITerm... terms) {
		return new EqualBuiltin(terms);
	}

	@Override
	public IBuiltinAtom createExactEqual(ITerm... terms) {
		return new ExactEqualBuiltin(terms);
	}

	@Override
	public IBuiltinAtom createGreater(ITerm... terms) {
		return new GreaterBuiltin(terms);
	}

	@Override
	public IBuiltinAtom createGreaterEqual(ITerm... terms) {
		return new GreaterEqualBuiltin(terms);
	}

	@Override
	public IBuiltinAtom createLess(ITerm... terms) {
		return new LessBuiltin(terms);
	}

	@Override
	public IBuiltinAtom createLessEqual(ITerm... terms) {
		return new LessEqualBuiltin(terms);
	}

	@Override
	public IBuiltinAtom createModulusBuiltin(ITerm... terms) {
		return new ModulusBuiltin(terms);
	}

	@Override
	public IBuiltinAtom createMultiplyBuiltin(ITerm... terms) {
		return new MultiplyBuiltin(terms);
	}

	@Override
	public IBuiltinAtom createNotExactEqual(ITerm... terms) {
		return new NotExactEqualBuiltin(terms);
	}

	@Override
	public IBuiltinAtom createSubtractBuiltin(ITerm... terms) {
		return new SubtractBuiltin(terms);
	}

	@Override
	public IBuiltinAtom createUnequal(ITerm... terms) {
		return new NotEqualBuiltin(terms);
	}

}
