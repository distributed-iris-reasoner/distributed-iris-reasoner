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

import static org.deri.iris.factory.Factory.CONCRETE;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;

import org.deri.iris.Configuration;
import org.deri.iris.ConfigurationThreadLocalStorage;
import org.deri.iris.EvaluationException;
import org.deri.iris.api.basics.ITuple;
import org.deri.iris.api.terms.IConcreteTerm;
import org.deri.iris.api.terms.INumericTerm;
import org.deri.iris.api.terms.ITerm;
import org.deri.iris.api.terms.concrete.IDateTerm;
import org.deri.iris.api.terms.concrete.IDateTime;
import org.deri.iris.api.terms.concrete.IDayTimeDuration;
import org.deri.iris.api.terms.concrete.IDecimalTerm;
import org.deri.iris.api.terms.concrete.IDoubleTerm;
import org.deri.iris.api.terms.concrete.IDuration;
import org.deri.iris.api.terms.concrete.IFloatTerm;
import org.deri.iris.api.terms.concrete.IIntegerTerm;
import org.deri.iris.api.terms.concrete.IList;
import org.deri.iris.api.terms.concrete.ITime;
import org.deri.iris.api.terms.concrete.IYearMonthDuration;
import org.deri.iris.builtins.datatype.ToDayTimeDurationBuiltin;
import org.deri.iris.builtins.datatype.ToDoubleBuiltin;
import org.deri.iris.builtins.datatype.ToFloatBuiltin;
import org.deri.iris.builtins.datatype.ToYearMonthDurationBuiltin;
import org.deri.iris.factory.Factory;
import org.deri.iris.terms.concrete.XmlDurationWorkAroundHelper;
import org.deri.iris.utils.StandardFloatingPointComparator;
import org.deri.iris.utils.equivalence.IEquivalentTerms;
import org.deri.iris.utils.equivalence.IgnoreTermEquivalence;

/**
 * <p>
 * Some helper methods common to some Builtins.
 * </p>
 */
public class BuiltinHelper {

	/**
	 * Empty tuple (with arity 0) to avoid some timeconsuming creation of
	 * tuples.
	 */
	public static final ITuple EMPTY_TUPLE = org.deri.iris.factory.Factory.BASIC
			.createTuple(new ITerm[] {});

	/**
	 * Calendar with all fields set to 0. Used to get the milliseconds out of a
	 * {@link javax.xml.datatype.Duration} object.
	 */
	private static final Calendar ZERO;

	static {
		ZERO = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
		ZERO.clear();
	}

	private BuiltinHelper() {
		// prevent subclassing
	}

	/**
	 * Compares two numerics by casting them to doubles and then comparing them
	 * for equality. <b>This method assumes that only numbers are stored in
	 * <code>INumericTerm</code>.</b>
	 * 
	 * @param n0 the first number
	 * @param n1 the second number
	 * @return whether the numbers are equal
	 * @throws NullPointerException if one of the numbers is null
	 * @see Number
	 */
	public static boolean numbersEqual(final INumericTerm n0,
			final INumericTerm n1) {
		if ((n0 == null) || (n1 == null)) {
			throw new NullPointerException("The numbers must not be null");
		}
		return numbersCompare(n0, n1) == 0;
	}

	/**
	 * Compares two numeric terms using the <code>compareTo</code> method of the
	 * terms. Respects floating point round off errors.
	 * 
	 * @param n0 the first number
	 * @param n1 the second number
	 * @return <code>0</code> if they are equal, a value <code>&lt; 0</code> if
	 *         n0 is smaller than n1 and a value <code>&gt; 0</code> if n0 is
	 *         bigger than n1
	 * @throws NullPointerException if one of the numbers is null
	 * @see Number
	 */
	public static int numbersCompare(final INumericTerm n0,
			final INumericTerm n1) {
		if ((n0 == null) || (n1 == null)) {
			throw new NullPointerException("The numbers must not be null");
		}

		if (n0 instanceof IFloatTerm && n1 instanceof IFloatTerm) {
			Float f0 = n0.getValue().floatValue();
			Float f1 = n1.getValue().floatValue();

			return StandardFloatingPointComparator.getFloat().compare(f0, f1);
		}

		return n0.compareTo(n1);
	}

	/**
	 * Compares two terms to each other. A value <code>&lt;0</code>,
	 * <code>0</code> or <code>&gt;0</code> will be returned, if the first term
	 * is smaller, equal or greater than the second one.
	 * 
	 * @param t0 the first term
	 * @param t1 the second term
	 * @return an Integer determining which one of the terms is bigger
	 * @throws NullPointerException if one of the terms is <code>null</code>
	 * @throws IllegalArgumentException if the two terms couldn't be compared
	 */
	public static int compare(final ITerm t0, final ITerm t1) {
		if ((t0 == null) || (t1 == null)) {
			throw new NullPointerException("The terms must not be null");
		}
		if ((t0 instanceof INumericTerm) && (t1 instanceof INumericTerm)) {
			return numbersCompare((INumericTerm) t0, (INumericTerm) t1);
		} else if (t0.getClass().isAssignableFrom(t1.getClass())) {
			return t0.compareTo(t1);
		}
		throw new IllegalArgumentException("Couldn't compare "
				+ t0.getClass().getName() + " and " + t1.getClass().getName());
	}

	/**
	 * Evaluate a < operation.
	 * 
	 * @param t0 A term
	 * @param t1 A term
	 * @return true if the two terms can be compared and t0 is less than t1
	 */
	static boolean less(final ITerm t0, final ITerm t1) {
		assert t0 != null;
		assert t1 != null;

		if ((t0 instanceof INumericTerm) && (t1 instanceof INumericTerm))
			return numbersCompare((INumericTerm) t0, (INumericTerm) t1) < 0;

		if (t0.getClass().isAssignableFrom(t1.getClass()))
			return t0.compareTo(t1) < 0;

		return false;
	}

	/**
	 * Evaluate a <= operation.
	 * 
	 * @param t0 A term
	 * @param t1 A term
	 * @return true if the two terms can be compared and t0 is less than or
	 *         equal to t1
	 */
	static boolean lessEquals(final ITerm t0, final ITerm t1) {
		assert t0 != null;
		assert t1 != null;

		if ((t0 instanceof INumericTerm) && (t1 instanceof INumericTerm))
			return numbersCompare((INumericTerm) t0, (INumericTerm) t1) <= 0;

		if (t0.getClass().isAssignableFrom(t1.getClass()))
			return t0.compareTo(t1) <= 0;

		return false;
	}

	/**
	 * Checks whether the values of two terms are the same.
	 * 
	 * @param t0 the first term
	 * @param t1 the second term
	 * @return <code>true</code> if the terms are comparable and if their values
	 *         are equal, otherwise <code>false</code>
	 * @throws NullPointerException if one of the terms is <code>null</code>
	 */
	static boolean equal(final ITerm t0, final ITerm t1) {
		return equal(t0, t1, new IgnoreTermEquivalence());
	}

	/**
	 * Checks whether the values of two terms are the same according to the
	 * specified equivalence classes.
	 * 
	 * @param t0 the first term
	 * @param t1 the second term
	 * @param equivalenceClasses The equivalence classes.
	 * @return <code>true</code> if the terms are comparable and if their values
	 *         are equal, otherwise <code>false</code>
	 * @throws NullPointerException if one of the terms is <code>null</code>
	 */
	static boolean equal(final ITerm t0, final ITerm t1, IEquivalentTerms equivalenceClasses) {
		assert t0 != null;
		assert t1 != null;

		// Also take the term equivalence into account, when comparing two terms
		// for equality.
		if (equivalenceClasses.areEquivalent(t0, t1)) {
			return true;
		}
		
		// If the terms are ILists, we also use the equivalent classes when
		// checking for equality of the elements of the list.
		if (t0 instanceof IList && t1 instanceof IList) {
			return ((IList) t0).equals(t1, equivalenceClasses);
		}
		
		if ((t0 instanceof INumericTerm) && (t1 instanceof INumericTerm))
			return numbersEqual((INumericTerm) t0, (INumericTerm) t1);
		
		return t0.equals(t1);
	}
	
	/**
	 * Two terms are exactly equal if they:
	 * <ol>
	 * <li>have exactly the same type</li>
	 * <li>have the same value.</li>
	 * </ol>
	 * 
	 * @param t0
	 * @param t1
	 * @return
	 */
	static boolean exactlyEqual(ITerm t0, ITerm t1) {
		assert t0 != null;
		assert t1 != null;

		if (t0 instanceof IConcreteTerm && t1 instanceof IConcreteTerm) {
			IConcreteTerm concreteTerm0 = (IConcreteTerm) t0;
			IConcreteTerm concreteTerm1 = (IConcreteTerm) t1;

			// System.err.println(" (" + concreteTerm0.equals(concreteTerm1)+
			// ")\t" + concreteTerm0 + "\t" + concreteTerm1);

			if (!concreteTerm0.getDatatypeIRI().equals(
					concreteTerm1.getDatatypeIRI())) {
				return false;
			}
		}

		return t0.equals(t1);
	}

	/**
	 * Creates a duration out of a given amount of milliseconds.
	 * 
	 * @param millis the milliseconds for which to create the duration
	 * @return the duration
	 */
	private static IDuration createDuration(final long millis) {
		return CONCRETE.createDuration(millis);
	}

	/**
	 * Creates a datetime object out of a <code>XMLGregorianCalendar</code>.
	 * 
	 * @param dt the <code>XMLGregorianCalendar</code>
	 * @return the datetime
	 */
	private static IDateTime createDateTime(final XMLGregorianCalendar dt) {
		assert dt != null : "The datetime must not be null";

		return CONCRETE.createDateTime(dt.getYear(), dt.getMonth(),
				dt.getDay(), dt.getHour(), dt.getMinute(),
				XmlDurationWorkAroundHelper.getSeconds(dt).doubleValue(),
				tzHours(dt), tzMinutes(dt));
	}

	/**
	 * Creates a date object out of a <code>XMLGregorianCalendar</code>.
	 * 
	 * @param dt the <code>XMLGregorianCalendar</code>
	 * @return the date
	 */
	private static IDateTerm createDate(final XMLGregorianCalendar dt) {
		assert dt != null : "The date must not be null";

		return CONCRETE.createDate(dt.getYear(), dt.getMonth(), dt.getDay(),
				tzHours(dt), tzMinutes(dt));
	}

	private static int tzHours(XMLGregorianCalendar dt) {
		return dt.getTimezone() / 60;
	}

	private static int tzMinutes(XMLGregorianCalendar dt) {
		int tz = dt.getTimezone();
		if (tz < 0)
			return -((-tz) % 60);
		else
			return tz % 60;
	}

	/**
	 * Creates a time object out of a <code>XMLGregorianCalendar</code>.
	 * 
	 * @param dt the <code>XMLGregorianCalendar</code>
	 * @return the time
	 */
	private static ITime createTime(final XMLGregorianCalendar dt) {
		assert dt != null : "The time must not be null";

		return CONCRETE
				.createTime(dt.getHour(), dt.getMinute(), dt.getSecond(), dt
						.getMillisecond(), tzHours(dt), tzMinutes(dt));
	}

	/**
	 * Creates a duration object out of a <code>Duration</code>.
	 * 
	 * @param dt the <code>Duration</code>
	 * @return the duration
	 */
	private static IDuration createDuration(final Duration d) {
		assert d != null : "The duration must not be null";

		return CONCRETE.createDuration(
				d.getSign() >= 0, 
				d.getYears(), 
				d.getMonths(), 
				d.getDays(), 
				d.getHours(), 
				d.getMinutes(),
				((BigDecimal) d.getField(DatatypeConstants.SECONDS))
						.doubleValue());
	}

	/**
	 * Creates a duration object out of a <code>Duration</code>.
	 * 
	 * @param dt the <code>Duration</code>
	 * @return the duration
	 */
	private static IDayTimeDuration createDayTimeDuration(final Duration d) {
		assert d != null : "The duration must not be null";
		
		// TODO move this to xml workaround helper?
		Duration dtd = d.normalizeWith(ZERO);
		
		assert dtd.getYears() == 0 : "The duration must not have years value set";
		assert dtd.getMonths() == 0 : "The duration must not have months value set";
		
		
		return CONCRETE.createDayTimeDuration(
				d.getSign() >= 0, 
				dtd.getDays(),
				d.getHours(),
				d.getMinutes(),
				((BigDecimal) d.getField(DatatypeConstants.SECONDS))
						.doubleValue());
	}

	/**
	 * Creates a duration object out of a <code>Duration</code>.
	 * 
	 * @param dt the <code>Duration</code>
	 * @return the duration
	 */
	private static IYearMonthDuration createYearMonthDuration(final Duration d) {
		assert d != null : "The duration must not be null";
//		assert d.getDays() == 0 : "The duration must not have days value set";
//		assert d.getHours() == 0 : "The duration must not have hours value set";
//		assert d.getMinutes() == 0 : "The duration must not have minutes value set";
//		assert d.getSeconds() == 0 : "The duration must not have seconds value set";
		
		
		return CONCRETE.createYearMonthDuration(
				d.getSign() >= 0, 
				d.getYears(),
				d.getMonths());
	}

	/**
	 * <p>
	 * Produces the sum of two terms. The resulting term will be of the most
	 * accurate type of the submitted ones.
	 * </p>
	 * 
	 * @param t0 the first summand
	 * @param t1 the second summand
	 * @return the sum
	 * @throws NullPointerException if one of the terms is <code>null</code>
	 * @throws IllegalArgumentException if the operation couldn't be applied to
	 *             the terms
	 */
	public static ITerm add(final ITerm t0, final ITerm t1) {
		if ((t0 == null) || (t1 == null)) {
			throw new NullPointerException("The terms must not be null");
		}

		// number + number = number
		if ((t0 instanceof INumericTerm) && (t1 instanceof INumericTerm)) {
			BigDecimal sum = ((INumericTerm) t0).getValue().add(
					((INumericTerm) t1).getValue());
			return toAppropriateType(sum, t0, t1);

		}
		
		/*
		 * op:add-yearMonthDuration-to-dateTime( $arg1 as xs:dateTime, $arg2 as xs:yearMonthDuration) as xs:dateTime
		 * op:add-dayTimeDuration-to-dateTime( $arg1 as xs:dateTime, $arg2 as xs:dayTimeDuration) as xs:dateTime
		 */
		else if ((t0 instanceof IDateTime) && (t1 instanceof IDuration)) {
			final XMLGregorianCalendar cal0 = (XMLGregorianCalendar) ((IDateTime) t0)
					.getValue().clone();
			cal0.add(((IDuration) t1).getValue());
			return createDateTime(cal0);

		}
		else if ((t0 instanceof IDuration) && (t1 instanceof IDateTime)) {
			final XMLGregorianCalendar cal1 = (XMLGregorianCalendar) ((IDateTime) t1)
					.getValue().clone();
			cal1.add(((IDuration) t0).getValue());
			return createDateTime(cal1);
		}
		
		/*
		 * op:add-yearMonthDuration-to-date( $arg1 as xs:date, $arg2 as xs:yearMonthDuration) as xs:date
		 * op:add-dayTimeDuration-to-date( $arg1 as xs:date, $arg2 as xs:dayTimeDuration) as xs:date
		 */
		else if ((t0 instanceof IDateTerm) && (t1 instanceof IDuration)) {
			final XMLGregorianCalendar cal0 = (XMLGregorianCalendar) ((IDateTerm) t0)
					.getValue().clone();
			cal0.add(((IDuration) t1).getValue());
			return createDate(cal0);
		}
		else if ((t0 instanceof IDuration) && (t1 instanceof IDateTerm)) {
			final XMLGregorianCalendar cal1 = (XMLGregorianCalendar) ((IDateTerm) t1)
					.getValue().clone();
			cal1.add(((IDuration) t0).getValue());
			return createDate(cal1);
		}
		
		/*
		 * op:add-dayTimeDuration-to-time( $arg1 as xs:time, $arg2 as xs:dayTimeDuration) as xs:time
		 */
		 // TODO yearMonthDuration allowed in implementation, not deterministic?
		else if ((t0 instanceof ITime) && (t1 instanceof IDuration)) {
			final XMLGregorianCalendar cal0 = (XMLGregorianCalendar) ((ITime) t0)
					.getValue().clone();
			cal0.add(((IDuration) t1).getValue());
			return createTime(cal0);
		}
		else if ((t0 instanceof IDuration) && (t1 instanceof ITime)) {
			final XMLGregorianCalendar cal1 = (XMLGregorianCalendar) ((ITime) t1)
					.getValue().clone();
			cal1.add(((IDuration) t0).getValue());
			return createTime(cal1);
		}
		
		else if ((t0 instanceof IDuration) && (t1 instanceof IDuration)) {
			final Duration d0 = ((IDuration) t0).getValue();
			final Duration d1 = ((IDuration) t1).getValue();


			if (t0 instanceof IYearMonthDuration && t1 instanceof IYearMonthDuration) {
				/*
				 * op:add-yearMonthDurations( $arg1 as xs:yearMonthDuration, $arg2 as xs:yearMonthDuration) as xs:yearMonthDuration
				 */
				return createYearMonthDuration( XmlDurationWorkAroundHelper.add(d0, d1) );
			} else if (t0 instanceof IDayTimeDuration && t1 instanceof IDayTimeDuration) {
				/*
				 * op:add-dayTimeDurations( $arg1 as xs:dayTimeDuration, $arg2 as xs:dayTimeDuration) as xs:dayTimeDuration
				 */
				return createDayTimeDuration( XmlDurationWorkAroundHelper.add(d0, d1) );
			} else {
				/*
				 * TODO not defined; allow?
				 * duration + duration = duration
				 */
				return createDuration(XmlDurationWorkAroundHelper.add(d0, d1));
			}
		}
		return null;
	}

	/**
	 * <p>
	 * Produces the difference of two terms. The resulting term will be of the
	 * most accurate type of the submitted ones.
	 * </p>
	 * 
	 * @param t0 the minuend
	 * @param t1 the subtrahend
	 * @return the differnece
	 * @throws NullPointerException if one of the terms is <code>null</code>
	 * @throws IllegalArgumentException if the operation couldn't be applied to
	 *             the terms
	 */
	public static ITerm subtract(final ITerm t0, final ITerm t1) {
		if ((t0 == null) || (t1 == null)) {
			throw new NullPointerException("The terms must not be null");
		}

		// number - number = number
		if ((t0 instanceof INumericTerm) && (t1 instanceof INumericTerm)) {
			BigDecimal result = ((INumericTerm) t0).getValue().subtract(
					((INumericTerm) t1).getValue());
			return toAppropriateType(result, t0, t1);
		}
		else if (t0 instanceof IDateTime) {
			/*
			 * op:subtract-dateTimes( $arg1 as xs:dateTime, $arg2 as xs:dateTime) as xs:dayTimeDuration?
			 */
			if (t1 instanceof IDateTime) {
				final XMLGregorianCalendar cal0 = ((IDateTime) t0).getValue();
				final XMLGregorianCalendar cal1 = ((IDateTime) t1).getValue();
				
				return createDayTimeDuration(XmlDurationWorkAroundHelper.subtract(cal0, cal1));
			}
			
			/*
			 * op:subtract-yearMonthDuration-from-dateTime( $arg1 as xs:dateTime, $arg2 as xs:yearMonthDuration) as
			 * xs:dateTime
			 *
			 * op:subtract-dayTimeDuration-from-dateTime( $arg1 as xs:dateTime, $arg2 as xs:dayTimeDuration) as
			 * xs:dateTime
			 */
			else if (t1 instanceof IDuration) {
				final XMLGregorianCalendar cal0 = (XMLGregorianCalendar) ((IDateTime) t0)
				.getValue().clone();
				cal0.add(((IDuration) t1).getValue().negate());
				return createDateTime(cal0);
			}
			
			
		}
		else if (t0 instanceof IDateTerm) {
			/*
			 * op:subtract-dates($arg1 as xs:date, $arg2 as xs:date) as xs:dayTimeDuration?
			 */
			if (t1 instanceof IDateTerm) {
				final XMLGregorianCalendar cal0 = ((IDateTerm) t0).getValue();
				final XMLGregorianCalendar cal1 = ((IDateTerm) t1).getValue();
				
				return createDayTimeDuration(XmlDurationWorkAroundHelper.subtract(cal0, cal1));
			}
			
			/*
			 * op:subtract-dayTimeDuration-from-date( $arg1 as xs:date, $arg2 as xs:dayTimeDuration) as xs:date
			 * 
			 * op:subtract-yearMonthDuration-from-date( $arg1 as xs:date, $arg2 as xs:yearMonthDuration) as xs:date
			 */
			else if (t1 instanceof IDuration) {
				final XMLGregorianCalendar cal0 = (XMLGregorianCalendar) ((IDateTerm) t0)
				.getValue().clone();
				cal0.add(((IDuration) t1).getValue().negate());
				return createDate(cal0);
			}
		}
		
		else if (t0 instanceof ITime) {

			/*
			 * op:subtract-times($arg1 as xs:time, $arg2 as xs:time) as xs:dayTimeDuration
			 */
			if ((t0 instanceof ITime) && (t1 instanceof ITime)) {
				final XMLGregorianCalendar cal0 = ((ITime) t0).getValue();
				final XMLGregorianCalendar cal1 = ((ITime) t1).getValue();
				return createDayTimeDuration(XmlDurationWorkAroundHelper.subtract(cal0, cal1));
			}

			/*
			 * op:subtract-dayTimeDuration-from-time( $arg1 as xs:time, $arg2 as xs:dayTimeDuration) as xs:time
			 */
			else if ((t0 instanceof ITime) && (t1 instanceof IDuration)) {
				final XMLGregorianCalendar cal0 = (XMLGregorianCalendar) ((ITime) t0)
				.getValue().clone();
				cal0.add(((IDuration) t1).getValue().negate());
				return createTime(cal0);
			}
		}
		
		// duration - duration = duration
		else if ((t0 instanceof IDuration) && (t1 instanceof IDuration)) {
			
			/*
			 * not defined!
			 */
			final Duration d0 = ((IDuration) t0).getValue();
			final Duration d1 = ((IDuration) t1).getValue();

			/*
			 * op:subtract-yearMonthDurations( $arg1 as xs:yearMonthDuration, $arg2 as xs:yearMonthDuration) as
			 * xs:yearMonthDuration
			 */
			if (t0 instanceof IYearMonthDuration
					&& t1 instanceof IYearMonthDuration) {
				return createYearMonthDuration( XmlDurationWorkAroundHelper.subtract(d0, d1) );
			}

			/*
			 * op:subtract-dayTimeDurations( $arg1 as xs:dayTimeDuration, $arg2 as xs:dayTimeDuration) as
			 * xs:dayTimeDuration
			 */
			else if (t0 instanceof IDayTimeDuration
					&& t1 instanceof IDayTimeDuration) {
				return createDayTimeDuration( XmlDurationWorkAroundHelper.subtract(d0, d1) );
			} else {
				return createDuration(XmlDurationWorkAroundHelper.subtract(d0, d1));
			}
		}

		return null;
	}

	/**
	 * <p>
	 * Produces the product of two terms. The resulting term will be of the most
	 * accurate type of the submitted ones.
	 * </p>
	 * <p>
	 * At the moment only INumericTerms are supported.
	 * </p>
	 * 
	 * @param t0 the first factor
	 * @param t1 the second factor
	 * @return the product
	 * @throws NullPointerException if one of the terms is <code>null</code>
	 * @throws IllegalArgumentException if one of the terms is not a
	 *             INumericTerm
	 */
	public static ITerm multiply(final ITerm t0, final ITerm t1) {
		if ((t0 == null) || (t1 == null)) {
			throw new NullPointerException("The terms must not be null");
		}

		// number * number = number
		if (t0 instanceof INumericTerm && t1 instanceof INumericTerm) {
			BigDecimal result = ((INumericTerm) t0).getValue().multiply(
					((INumericTerm) t1).getValue());
			return toAppropriateType(result, t0, t1);
		}

		// duration * number = duration
		if (t0 instanceof IDuration && t1 instanceof INumericTerm) {
			IDuration duration = (IDuration) t0;
			INumericTerm number = (INumericTerm) t1;

			Duration result = duration.getValue().multiply(new BigDecimal(number.getValue().doubleValue()));
			
			/*
			 * op:multiply-yearMonthDuration( $arg1 as xs:yearMonthDuration, $arg2 as xs:double) as xs:yearMonthDuration
			 */
			if (duration instanceof IYearMonthDuration) {
				return createYearMonthDuration(result);
			}
			/*
			 * op:multiply-dayTimeDuration( $arg1 as xs:dayTimeDuration, $arg2 as xs:double) as xs:dayTimeDuration
			 */
			else if (duration instanceof IDayTimeDuration) {
				return createDayTimeDuration(result);
			}

			/*
			 * not defined!
			 */
			return createDuration(result);
		}

		return null;
	}

	/**
	 * <p>
	 * Produces the quotient of two terms. The resulting term will be of the
	 * most accurate type of the submitted ones.
	 * </p>
	 * <p>
	 * At the moment only INumericTerms are supported.
	 * </p>
	 * 
	 * @param t0 the dividend
	 * @param t1 the divisor
	 * @return the quotient
	 * @throws EvaluationException
	 * @throws NullPointerException if one of the terms is <code>null</code>
	 */
	public static ITerm divide(final ITerm t0, final ITerm t1)
			throws EvaluationException {
		if ((t0 == null) || (t1 == null)) {
			throw new NullPointerException("The terms must not be null");
		}

		// number / number = number
		if ((t0 instanceof INumericTerm) && (t1 instanceof INumericTerm)) {
			BigDecimal denominator = ((INumericTerm) t1).getValue();

			if (denominator.compareTo(BigDecimal.ZERO) == 0) {
				handleDivideByZero();
				return null;
			}
			
			BigDecimal result = ((INumericTerm) t0).getValue().divide(
					denominator);

			// integer / integer = decimal
			if (t0 instanceof IIntegerTerm && t1 instanceof IIntegerTerm) {
				return CONCRETE.createDecimal(result);
			} else {
				return toAppropriateType(result, t0, t1);
			}
		}

		/*
		 * op:divide-yearMonthDuration-by-yearMonthDuration( $arg1 as xs:yearMonthDuration, $arg2 as xs:yearMonthDuration) as xs:decimal
		 * op:divide-dayTimeDuration-by-dayTimeDuration( $arg1 as xs:dayTimeDuration, $arg2 as xs:dayTimeDuration) as xs:decimal
		 */
		if (t0 instanceof IDuration && t1 instanceof IDuration) {
			Duration duration0 = ((IDuration) t0).getValue();
			Duration duration1 = ((IDuration) t1).getValue();

			double value0 = duration0.getTimeInMillis(ZERO);
			double value1 = duration1.getTimeInMillis(ZERO);

			double quotient = value0 / value1;
			return CONCRETE.createDecimal(quotient);
		}

		/*
		 * op:divide-yearMonthDuration( $arg1 as xs:yearMonthDuration, $arg2 as xs:double) as xs:yearMonthDuration
		 * op:divide-dayTimeDuration( $arg1 as xs:dayTimeDuration, $arg2 as xs:double) as xs:dayTimeDuration
		 */
		if (t0 instanceof IDuration && t1 instanceof INumericTerm) {
			double number = ((INumericTerm) t1).getValue().doubleValue();

			if (number == 0.0) {
				handleDivideByZero();
				return null;
			}

			double invertedNumber = 1.0 / number;
			IDoubleTerm invertedTerm = CONCRETE.createDouble(invertedNumber);

			return multiply(t0, invertedTerm);
		}

		return null;
	}

	/**
	 * Method to call whenever a divide by zero occurs.
	 * 
	 * @throws EvaluationException
	 */
	private static void handleDivideByZero() throws EvaluationException {
		if (ConfigurationThreadLocalStorage.getConfiguration().evaluationDivideByZeroBehaviour == Configuration.DivideByZeroBehaviour.STOP)
			throw new EvaluationException("Divide by zero error");
	}

	static ITerm increment(ITerm argument) {
		assert argument != null;

		if (argument instanceof INumericTerm) {
			BigDecimal result = ((INumericTerm) argument).getValue().add(
					BigDecimal.ONE);
			return toAppropriateType(result, argument, argument);
		}

		return null;
	}

	/**
	 * <p>
	 * Produces the modulus of two terms. The resulting term will be of the most
	 * accurate type of the submitted ones.
	 * </p>
	 * <p>
	 * At the moment only INumericTerms are supported.
	 * </p>
	 * 
	 * @param t0 the first argument
	 * @param t1 the second argument
	 * @return the product = t0 % t1
	 * @throws EvaluationException
	 * @throws NullPointerException if one of the terms is <code>null</code>
	 * @throws IllegalArgumentException if one of the terms is not a
	 *             INumericTerm
	 */
	public static ITerm modulus(final ITerm t0, final ITerm t1)
			throws EvaluationException {
		if ((t0 == null) || (t1 == null)) {
			throw new NullPointerException("The terms must not be null");
		}

		if (t0 instanceof INumericTerm && t1 instanceof INumericTerm) {
			BigDecimal numerator = ((INumericTerm) t0).getValue();
			if (numerator.compareTo(BigDecimal.ZERO) < 0) {
				handleInvalidModulusNumerator();
				return null;
			}

			BigDecimal denominator = ((INumericTerm) t1).getValue();
			if (numerator.compareTo(BigDecimal.ZERO) < 1) {
				handleInvalidModulusDenominator();
				return null;
			}

			// We can use the remainder method here, because we check for valid
			// arguments.
			BigDecimal result = numerator.remainder(denominator);

			if (t0 instanceof IIntegerTerm && t1 instanceof IIntegerTerm) {
				return CONCRETE.createInteger(result.toBigInteger());
			}

			return toAppropriateType(numerator.remainder(denominator), t0, t1);
		}

		return null;
	}

	private static void handleInvalidModulusNumerator() {
	}

	private static void handleInvalidModulusDenominator() {
	}

	/**
	 * Promotes a term from one type to another according to the promotion rules
	 * defined in the XPath specification:
	 * <ol>
	 * <li>Numeric type promotion
	 * <ul>
	 * <li>A value of type xs:float (or any type derived by restriction from
	 * xs:float) can be promoted to the type xs:double. The result is the
	 * xs:double value that is the same as the original value.</li>
	 * <li>A value of type xs:decimal (or any type derived by restriction from
	 * xs:decimal) can be promoted to either of the types xs:float or xs:double.
	 * The result of this promotion is created by casting the original value to
	 * the required type. This kind of promotion may cause loss of precision.</li>
	 * </ul>
	 * </li>
	 * </ol>
	 * 
	 * @param t0 The term which should be promoted depending on the term
	 *            <code>t1</code>.
	 * @param t1 The term according to which the term <code>t0</code> is
	 *            promoted.
	 * @return The result of the the type promotion.
	 */
	public static ITerm promote(ITerm t0, ITerm t1) {
		// Numeric type promotion.
		if (t0 instanceof INumericTerm && t1 instanceof INumericTerm) {
			INumericTerm n0 = (INumericTerm) t0;
			INumericTerm n1 = (INumericTerm) t1;

			// any numeric type * double -> double
			if (n1 instanceof IDoubleTerm) {
				return ToDoubleBuiltin.toDouble(n0);
			}

			// any non double numeric type * float -> float
			if (!(n0 instanceof IDoubleTerm) && n1 instanceof IFloatTerm) {
				return ToFloatBuiltin.toFloat(n0);
			}

			// integer * integer -> integer
			if ((t0 instanceof IIntegerTerm) && (t1 instanceof IIntegerTerm)) {
				// TODO Implement this.
			}

			// decimal * decimal -> decimal
			if (n0 instanceof IDecimalTerm && n1 instanceof IDecimalTerm) {
				// TODO Implement this.
			}
		}

		return null;
	}

	/**
	 * Constructs a number term with of the most appropriate term type.
	 * 
	 * @param n the value for the returned term
	 * @param t0 the first term (only the type of this term will be taken into
	 *            account)
	 * @param t1 the first term (only the type of this term will be taken into
	 *            account)
	 * @return a term of the most appropriate type of the submitted ones with
	 *         the given value.
	 */
	private static ITerm toAppropriateType(BigDecimal n, ITerm t0, ITerm t1) {
		assert n != null;
		assert t0 != null;
		assert t1 != null;
		assert t0 instanceof INumericTerm;
		assert t1 instanceof INumericTerm;

		// double * any numeric type -> double
		if ((t0 instanceof IDoubleTerm) || (t1 instanceof IDoubleTerm))
			return CONCRETE.createDouble(n.doubleValue());

		// float * any non double numeric type -> float
		if ((t0 instanceof IFloatTerm) || (t1 instanceof IFloatTerm))
			return CONCRETE.createFloat(n.floatValue());

		// integer * integer -> integer
		if ((t0 instanceof IIntegerTerm) && (t1 instanceof IIntegerTerm))
			return CONCRETE.createInteger(n.toBigInteger());

		// decimal * decimal -> decimal
		if ((t0 instanceof IDecimalTerm) || (t1 instanceof IDecimalTerm))
			return CONCRETE.createDecimal(n);

		return CONCRETE.createDouble(n.doubleValue());
	}

	public static ITerm yearPart(ITerm term) {
		int year = 0;

		if (term instanceof IDateTerm) {
			IDateTerm date = (IDateTerm) term;
			year = date.getYear();
		} else if (term instanceof IDateTime) {
			IDateTime dateTime = (IDateTime) term;
			year = dateTime.getYear();

			// Quick fix for "new year problem". year-from-dateTime of dateTime
			// "1999-12-31T24:00:00" should yield 2000 instead of 1999.
			if (dateTime.getMonth() == 12 && dateTime.getDay() == 31
					&& dateTime.getHour() == 24 || dateTime.getHour() == 0) {
				year++;
			}
		} else if (term instanceof IDuration) {
			IDuration duration = (IDuration) term;

			IYearMonthDuration yearMonth = ToYearMonthDurationBuiltin
					.toYearMonthDuration(duration);
			yearMonth = yearMonth.toCanonical();

			year = yearMonth.getYear();

			if (!yearMonth.isPositive()) {
				year *= -1;
			}
		} else {
			return null;
		}

		return Factory.CONCRETE.createInteger(year);
	}

	public static ITerm monthPart(ITerm term) {
		int month = 0;

		if (term instanceof IDateTerm) {
			IDateTerm date = (IDateTerm) term;
			month = date.getMonth();
		} else if (term instanceof IDateTime) {
			IDateTime dateTime = (IDateTime) term;
			month = dateTime.getMonth();
		} else if (term instanceof IDuration) {
			IDuration duration = (IDuration) term;

			IYearMonthDuration yearMonth = ToYearMonthDurationBuiltin
					.toYearMonthDuration(duration);
			yearMonth = yearMonth.toCanonical();

			month = yearMonth.getMonth();

			if (!yearMonth.isPositive()) {
				month *= -1;
			}
		} else {
			return null;
		}

		return Factory.CONCRETE.createInteger(month);
	}

	public static ITerm dayPart(ITerm term) {
		int day = 0;

		if (term instanceof IDateTerm) {
			IDateTerm date = (IDateTerm) term;
			day = date.getDay();
		} else if (term instanceof IDateTime) {
			IDateTime dateTime = (IDateTime) term;
			day = dateTime.getDay();
		} else if (term instanceof IDuration) {
			IDuration duration = (IDuration) term;

			IDayTimeDuration dayTime = ToDayTimeDurationBuiltin
					.toDayTimeDuration(duration);
			dayTime = dayTime.toCanonical();

			day = dayTime.getDay();

			if (!dayTime.isPositive()) {
				day *= -1;
			}
		} else {
			return null;
		}

		return Factory.CONCRETE.createInteger(day);
	}

	public static ITerm hourPart(ITerm term) {
		int hour = 0;

		if (term instanceof ITime) {
			ITime time = (ITime) term;

			hour = time.getHour();

			// Quick fix for "hour 24" problem.
			if (hour == 24) {
				hour = 0;
			}
		} else if (term instanceof IDateTime) {
			IDateTime dateTime = (IDateTime) term;
			hour = dateTime.getHour();

			// Quick fix for "hour 24" problem.
			if (hour == 24) {
				hour = 0;
			}
		} else if (term instanceof IDuration) {
			IDuration duration = (IDuration) term;

			IDayTimeDuration dayTime = ToDayTimeDurationBuiltin
					.toDayTimeDuration(duration);
			dayTime = dayTime.toCanonical();

			hour = dayTime.getHour();

			if (!dayTime.isPositive()) {
				hour *= -1;
			}
		} else {
			return null;
		}

		return Factory.CONCRETE.createInteger(hour);
	}

	public static ITerm minutePart(ITerm term) {
		int minute = 0;

		if (term instanceof ITime) {
			ITime time = (ITime) term;
			minute = time.getMinute();
		} else if (term instanceof IDateTime) {
			IDateTime dateTime = (IDateTime) term;
			minute = dateTime.getMinute();
		} else if (term instanceof IDuration) {
			IDuration duration = (IDuration) term;

			IDayTimeDuration dayTime = ToDayTimeDurationBuiltin
					.toDayTimeDuration(duration);
			dayTime = dayTime.toCanonical();

			minute = dayTime.getMinute();

			if (!dayTime.isPositive()) {
				minute *= -1;
			}
		} else {
			return null;
		}

		return Factory.CONCRETE.createInteger(minute);
	}

	public static ITerm secondPart(ITerm term) {
		double second = 0;

		if (term instanceof ITime) {
			ITime time = (ITime) term;
			second = time.getDecimalSecond();
		} else if (term instanceof IDateTime) {
			IDateTime dateTime = (IDateTime) term;
			second = dateTime.getDecimalSecond();
		} else if (term instanceof IDuration) {
			/*
			 * We do not convert to DayTimeDuration here, since we can not
			 * retrieve the second part of a duration without losing its
			 * fractional part.
			 */

			IDuration duration = (IDuration) term;

			double seconds = duration.getDecimalSecond() + duration.getMinute()
					* 60 + duration.getHour() * 3600 + duration.getDay()
					* 86400;
			double remainder = ((seconds % 86400.0) % 3600.0) % 60.0;

			second = remainder * duration.getValue().getSign();
		} else {
			return null;
		}

		return Factory.CONCRETE.createDecimal(second);
	}

	public static ITerm timezonePart(ITerm term) {
		GregorianCalendar calendar = null;
		TimeZone timeZone = null;

		if (term instanceof ITime) {
			ITime time = (ITime) term;
			calendar = time.getValue().toGregorianCalendar();
			timeZone = time.getTimeZone();
		} else if (term instanceof IDateTerm) {
			IDateTerm date = (IDateTerm) term;
			calendar = date.getValue().toGregorianCalendar();
			timeZone = date.getTimeZone();
		} else if (term instanceof IDateTime) {
			IDateTime dateTime = (IDateTime) term;
			calendar = dateTime.getValue().toGregorianCalendar();
			timeZone = dateTime.getTimeZone();
		} else {
			return null;
		}

		long time = calendar.getTimeInMillis();
		long offset = timeZone.getOffset(time);

		// Other than the specification suggests, we return a Duration instance
		// here, instead of DayTimeDuration.
		// FIXME Return DayTimeDuration.
		return Factory.CONCRETE.createDuration(offset);
	}

	/**
	 * Determines the indexes of the ground terms in a collection.
	 * 
	 * @param t the collection where to search for ground terms
	 * @return the indexes of the ground terms (the first term has the index 0)
	 * @throws NullPointerException if the collection is <code>null</code>
	 */
	public static int[] determineGround(final Collection<ITerm> t) {
		if (t == null) {
			throw new NullPointerException("The tuple must not be null");
		}
		final int[] res = new int[t.size()];
		int i = 0;
		int pos = 0;
		for (final ITerm term : t) {
			if (term.isGround()) {
				res[i++] = pos;
			}
			pos++;
		}
		final int[] ret = new int[i];
		System.arraycopy(res, 0, ret, 0, i);
		return ret;
	}

	/**
	 * Returns the terms at a given position in a collection.
	 * 
	 * @param t the collection from where to retrieve the terms
	 * @param pos the positions of the terms to retrieve
	 * @return the retrieved terms
	 * @throws NullPointerException if the collection is <code>null</code>
	 * @throws NullPointerException if the position array is <code>null</code>
	 * @throws IllegalArgumentException if ther are more terms requested, than
	 *             given (pos.length &gt; t.size())
	 */
	public static ITerm[] getIndexes(final Collection<ITerm> t, final int[] pos) {
		if (pos == null) {
			throw new NullPointerException(
					"The position array must not be null");
		}
		if (t == null) {
			throw new NullPointerException("The tuple must not be null");
		}
		if (pos.length > t.size()) {
			throw new IllegalArgumentException("There are " + pos.length + " <"
					+ Arrays.asList(pos) + "> terms requested, but only "
					+ t.size() + " <" + t + "> terms given");
		}
		final ITerm[] ret = new ITerm[pos.length];
		final ITerm[] in = t.toArray(new ITerm[t.size()]);
		int idx = 0;

		for (final int i : pos) {
			ret[idx++] = in[i];
		}
		return ret;
	}

	/**
	 * Determines the indexes of the unground terms in a collection.
	 * 
	 * @param t the collection where to search for ground terms
	 * @return the indexes of the unground terms (the first term has the index
	 *         0)
	 * @throws NullPointerException if the collection is <code>null</code>
	 */
	public static int[] determineUnground(final Collection<ITerm> t) {
		if (t == null) {
			throw new NullPointerException("The tuple must not be null");
		}
		final int[] res = new int[t.size()];
		int i = 0;
		int pos = 0;
		for (final ITerm term : t) {
			if (!term.isGround()) {
				res[i++] = pos;
			}
			pos++;
		}
		final int[] ret = new int[i];
		System.arraycopy(res, 0, ret, 0, i);
		return ret;
	}

	/**
	 * <p>
	 * Computes the complement of an index array.
	 * </p>
	 * <p>
	 * E.g. we have computed an index array <code>[1, 3, 4]</code> of a
	 * collection with length 5 we would get <code>[0, 2]</code> as complement.
	 * </p>
	 * 
	 * @param i the array from which to compute the complement
	 * @param l the length of the collection whe took the index from
	 * @return the complement
	 * @throws NullPointerException if the index array was <code>null</code>
	 * @throws IllegalArgumentException if the lenght was nevative
	 * @see #determineGround(Collection<ITerm>)
	 * @see #determineUnground(Collection<ITerm>)
	 */
	public static int[] complement(final int[] i, final int l) {
		if (l < 0) {
			throw new IllegalArgumentException(
					"The length must not be negative");
		}
		// TODO: this method needs a rewrite (get rid of this
		// arraycopy)! WHY DON'T WE USE PERL!?!?!
		int[] ret = new int[l - i.length];
		int[] clone = new int[i.length];
		System.arraycopy(i, 0, clone, 0, i.length);
		Arrays.sort(clone);
		for (int j = 0, pos = 0; pos < ret.length; j++) {
			if (Arrays.binarySearch(clone, j) < 0) {
				ret[pos++] = j;
			}
		}
		return ret;
	}

	/**
	 * <p>
	 * Concats two term arrays taking an index array into account.
	 * </p>
	 * <p>
	 * E.g. we got an index of <code>[0, 3, 4]</code>, the firs array is
	 * <code>['a', 'b', 'c']</code> and the second <code>[12, 13, 14]</code> we
	 * will get <code>['a', 12, 13, 'b', 'c', 14]</code>.
	 * </p>
	 * 
	 * @param idx0 the index array
	 * @param t0 the first array (this terms will be put at he positions of the
	 *            index array)
	 * @param t1 the second array (with this terms the gaps will be filled up)
	 * @throws NullPointerException if the index array is <code>null</code>
	 * @throws NullPointerException if one of the term arrays is
	 *             <code>null</code>
	 * @throws IllegalArgumentException if the lenght of the index and the first
	 *             term array don't match
	 * @see #determineGround(Collection<ITerm>)
	 * @see #determineUnground(Collection<ITerm>)
	 */
	public static ITerm[] concat(final int[] idx0, final ITerm[] t0,
			final ITerm[] t1) {
		if (idx0 == null) {
			throw new NullPointerException("The index array must not be null");
		}
		if ((t0 == null) || (t1 == null)) {
			throw new NullPointerException("The term arrays must not be null");
		}
		if (idx0.length != t0.length) {
			throw new IllegalArgumentException(
					"The length of the index array and "
							+ "the first array must be equal, but was "
							+ idx0.length + " and " + t0.length);
		}
		final java.util.List<ITerm> res = new java.util.LinkedList<ITerm>();
		res.addAll(Arrays.asList(t1));
		for (int i = 0; i < idx0.length; i++) {
			res.add(idx0[i], t0[i]);
		}
		return res.toArray(new ITerm[res.size()]);
	}

}
