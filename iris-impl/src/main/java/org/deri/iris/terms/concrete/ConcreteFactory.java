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
package org.deri.iris.terms.concrete;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;

import org.deri.iris.api.factory.IConcreteFactory;
import org.deri.iris.api.terms.IConcreteTerm;
import org.deri.iris.api.terms.concrete.IAnyURI;
import org.deri.iris.api.terms.concrete.IBase64Binary;
import org.deri.iris.api.terms.concrete.IBooleanTerm;
import org.deri.iris.api.terms.concrete.IByteTerm;
import org.deri.iris.api.terms.concrete.IDateTerm;
import org.deri.iris.api.terms.concrete.IDateTime;
import org.deri.iris.api.terms.concrete.IDayTimeDuration;
import org.deri.iris.api.terms.concrete.IDecimalTerm;
import org.deri.iris.api.terms.concrete.IDoubleTerm;
import org.deri.iris.api.terms.concrete.IDuration;
import org.deri.iris.api.terms.concrete.IENTITY;
import org.deri.iris.api.terms.concrete.IFloatTerm;
import org.deri.iris.api.terms.concrete.IGDay;
import org.deri.iris.api.terms.concrete.IGMonth;
import org.deri.iris.api.terms.concrete.IGMonthDay;
import org.deri.iris.api.terms.concrete.IGYear;
import org.deri.iris.api.terms.concrete.IGYearMonth;
import org.deri.iris.api.terms.concrete.IHexBinary;
import org.deri.iris.api.terms.concrete.IID;
import org.deri.iris.api.terms.concrete.IIDREF;
import org.deri.iris.api.terms.concrete.IIntTerm;
import org.deri.iris.api.terms.concrete.IIntegerTerm;
import org.deri.iris.api.terms.concrete.IIri;
import org.deri.iris.api.terms.concrete.ILanguage;
import org.deri.iris.api.terms.concrete.IList;
import org.deri.iris.api.terms.concrete.ILocal;
import org.deri.iris.api.terms.concrete.ILongTerm;
import org.deri.iris.api.terms.concrete.INCName;
import org.deri.iris.api.terms.concrete.INMTOKEN;
import org.deri.iris.api.terms.concrete.INOTATION;
import org.deri.iris.api.terms.concrete.IName;
import org.deri.iris.api.terms.concrete.INegativeInteger;
import org.deri.iris.api.terms.concrete.INonNegativeInteger;
import org.deri.iris.api.terms.concrete.INonPositiveInteger;
import org.deri.iris.api.terms.concrete.INormalizedString;
import org.deri.iris.api.terms.concrete.IPlainLiteral;
import org.deri.iris.api.terms.concrete.IPositiveInteger;
import org.deri.iris.api.terms.concrete.IQName;
import org.deri.iris.api.terms.concrete.IShortTerm;
import org.deri.iris.api.terms.concrete.ISqName;
import org.deri.iris.api.terms.concrete.ITime;
import org.deri.iris.api.terms.concrete.IToken;
import org.deri.iris.api.terms.concrete.IUnsignedByte;
import org.deri.iris.api.terms.concrete.IUnsignedInt;
import org.deri.iris.api.terms.concrete.IUnsignedLong;
import org.deri.iris.api.terms.concrete.IUnsignedShort;
import org.deri.iris.api.terms.concrete.IXMLLiteral;
import org.deri.iris.api.terms.concrete.IYearMonthDuration;

/**
 * <p>
 * Factory to create concrete terms.
 * </p>
 */
public class ConcreteFactory implements IConcreteFactory {

	private static final IConcreteFactory FACTORY = new ConcreteFactory();

	private ConcreteFactory() {
		// this is a singleton
	}

	public static IConcreteFactory getInstance() {
		return FACTORY;
	}

	public IBase64Binary createBase64Binary(final String s) {
		return new Base64Binary(s);
	}

	public IBooleanTerm createBoolean(final boolean b) {
		return new BooleanTerm(b);
	}

	public IBooleanTerm createBoolean(final String value) {
		return new BooleanTerm(value);
	}

	public IDateTerm createDate(final int year, final int month, final int day) {
		return new DateTerm(year, month, day);
	}

	public IDateTerm createDate(final int year, final int month, final int day,
			final int tzHour, final int tzMinute) {
		return new DateTerm(year, month, day, tzHour, tzMinute);
	}

	public IDateTime createDateTime(int year, int month, int day, int hour,
			int minute, int second, int millisecond, int tzHour, int tzMinute) {
		return new DateTime(year, month, day, hour, minute, second,
				millisecond, tzHour, tzMinute);
	}

	public IDateTime createDateTime(int year, int month, int day, int hour,
			int minute, double second, int tzHour, int tzMinute) {
		return new DateTime(year, month, day, hour, minute, second, tzHour,
				tzMinute);
	}

	public ITime createTime(final int hour, final int minute, final int second,
			final int millisecond, final int tzHour, final int tzMinute) {
		return new Time(hour, minute, second, millisecond, tzHour, tzMinute);
	}

	public ITime createTime(int hour, int minute, double second, int tzHour,
			int tzMinute) {
		return new Time(hour, minute, second, tzHour, tzMinute);
	}

	public IDecimalTerm createDecimal(double d) {
		return new DecimalTerm(d);
	}
	
	public IDecimalTerm createDecimal(BigDecimal value) {
		return new DecimalTerm(value);
	}

	public IDoubleTerm createDouble(final double d) {
		return new DoubleTerm(d);
	}

	public IDuration createDuration(boolean positive, int year, int month,
			int day, int hour, int minute, int second, int millisecond) {
		return new Duration(positive, year, month, day, hour, minute, second,
				millisecond);
	}

	public IDuration createDuration(boolean positive, int year, int month,
			int day, int hour, int minute, double second) {
		return new Duration(positive, year, month, day, hour, minute, second);
	}

	public IDuration createDuration(final long millis) {
		return new Duration(millis);
	}

	public IFloatTerm createFloat(final float f) {
		return new FloatTerm(f);
	}

	public IGDay createGDay(final int day) {
		return new GDay(day);
	}

	public IGMonthDay createGMonthDay(final int month, final int day) {
		return new GMonthDay(month, day);
	}

	public IGMonth createGMonth(final int month) {
		return new GMonth(month);
	}

	public IGYearMonth createGYearMonth(final int year, final int month) {
		return new GYearMonth(year, month);
	}

	public IGYear createGYear(final int year) {
		return new GYear(year);
	}

	public IHexBinary createHexBinary(final String s) {
		return new HexBinary(s);
	}

	public IIntegerTerm createInteger(int i) {
		return new IntegerTerm(i);
	}
	
	public IIntegerTerm createInteger(BigInteger i) {
		return new IntegerTerm(i);
	}

	public IIri createIri(final String s) {
		return new Iri(s);
	}

	public ISqName createSqName(final String s) {
		return new SqName(s);
	}

	public ISqName createSqName(final IIri iri, final String name) {
		return new SqName(iri, name);
	}

	public IPlainLiteral createPlainLiteral(final String string,
			final String language) {
		return new PlainLiteral(string, language);
	}

	public IPlainLiteral createPlainLiteral(final String string) {
		return new PlainLiteral(string);
	}

	public IXMLLiteral createXMLLiteral(String string) {
		return new XMLLiteral(string);
	}

	public IXMLLiteral createXMLLiteral(String string, String lang) {
		return new XMLLiteral(string, lang);
	}

	public IYearMonthDuration createYearMonthDuration(boolean positive,
			int year, int month) {
		return new YearMonthDuration(positive, year, month);
	}

	public IDayTimeDuration createDayTimeDuration(boolean positive, int day,
			int hour, int minute, double second) {
		return new DayTimeDuration(positive, day, hour, minute, second);
	}

	public IDayTimeDuration createDayTimeDuration(boolean positive, int day,
			int hour, int minute, int second, int millisecond) {
		return new DayTimeDuration(positive, day, hour, minute, second,
				millisecond);
	}

	public IAnyURI createAnyURI(URI uri) {
		return new AnyURI(uri);
	}

	public IByteTerm createByte(byte value) {
		return new ByteTerm(value);
	}

	public IENTITY createEntity(String entity) {
		return new ENTITY(entity);
	}

	public IID createID(String id) {
		return new ID(id);
	}

	public IIDREF createIDREF(String idRef) {
		return new IDREF(idRef);
	}

	public ILanguage createLanguage(String language) {
		return new Language(language);
	}
	
	public IList createList(IConcreteTerm... terms) {
		if (terms == null) {
			return new List();
		} else {
			return new List(terms);
		}
	}

	public ILocal createLocal(String value, Object context) {
		return new Local(value, context);
	}
	
	public ILongTerm createLong(long value) {
		return new LongTerm(value);
	}

	public INCName createNCName(String name) {
		return new NCName(name);
	}

	public INMTOKEN createNMTOKEN(String token) {
		return new NMTOKEN(token);
	}

	public IName createName(String name) {
		return new Name(name);
	}

	public INegativeInteger createNegativeInteger(BigInteger value) {
		return new NegativeInteger(value);
	}

	public INonNegativeInteger createNonNegativeInteger(BigInteger value) {
		return new NonNegativeInteger(value);
	}

	public INonPositiveInteger createNonPositiveInteger(BigInteger value) {
		return new NonPositiveInteger(value);
	}

	public INormalizedString createNormalizedString(String string) {
		return new NormalizedString(string);
	}

	public IPositiveInteger createPositiveInteger(BigInteger value) {
		return new PositiveInteger(value);
	}

	public IShortTerm createShort(short value) {
		return new ShortTerm(value);
	}

	public IToken createToken(String token) {
		return new Token(token);
	}

	public IUnsignedByte createUnsignedByte(short value) {
		return new UnsignedByte(value);
	}

	public IUnsignedInt createUnsignedInt(long value) {
		return new UnsignedInt(value);
	}

	public IUnsignedLong createUnsignedLong(long value) {
		return new UnsignedLong(BigInteger.valueOf(value));
	}
	
	public IUnsignedLong createUnsignedLong(BigInteger value) {
		return new UnsignedLong(value);
	}

	public IUnsignedShort createUnsignedShort(int value) {
		return new UnsignedShort(value);
	}

	public IIntTerm createInt(int value) {
		return new IntTerm(value);
	}

	public IDateTime createDateTimeStamp(int year, int month, int day,
			int hour, int minute, double second, int tzHour, int tzMinute) {
		return new DateTimeStamp(year, month, day, hour, minute, second,
				tzHour, tzMinute);
	}

	public INOTATION createNOTATION(String namespaceName, String localPart) {
		return new NOTATION(namespaceName, localPart);
	}

	public IQName createQName(String namespaceName, String localPart) {
		return new QName(namespaceName, localPart);
	}
}
