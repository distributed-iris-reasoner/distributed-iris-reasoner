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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import junit.framework.TestCase;

import org.deri.iris.EvaluationException;
import org.deri.iris.api.basics.ITuple;
import org.deri.iris.api.terms.ITerm;

public abstract class AbstractBooleanBuiltinTest extends TestCase {
	
	public AbstractBooleanBuiltinTest(String name) {
		super(name);
	}

	private void checkBooleanBuiltin(boolean expected, ITerm term,
			String datatypeIRI, String builtinName) throws EvaluationException,
			ClassNotFoundException, SecurityException, NoSuchMethodException,
			IllegalArgumentException, InstantiationException,
			IllegalAccessException, InvocationTargetException {

		ITerm datatypeIRITerm = CONCRETE.createIri(datatypeIRI);
		ITuple arguments = BASIC.createTuple(term, datatypeIRITerm);

		Class<?> builtinC = Class.forName(builtinName);
		Constructor<?> builtinO = builtinC
				.getConstructor(new Class[] { ITerm[].class });

		Object[] params = new Object[1];
		params[0] = new ITerm[] { term };
		Object builtinobject = builtinO.newInstance(params);

		Class<?>[] params2 = new Class[1];
		params2[0] = ITuple.class;
		Method em = builtinC.getMethod("evaluate", params2);
		ITuple actualTuple = (ITuple) em.invoke(builtinobject, arguments);

		if (expected) {
			assertNotNull("Error builtin should be true: " + builtinName,
					actualTuple);
		} else {
			assertNull("Error builtin should be false: " + builtinName,
					actualTuple);
		}
	}

	/**
	 * Checks if the boolean Builtin (Is... IsNot...) does right
	 * 
	 * @param iri
	 * @param builtin
	 * @param builtinNames the first builtinName is the of the testing class -
	 *            the additional ones for classes which also match (numeric,..)
	 * @throws SecurityException
	 * @throws IllegalArgumentException
	 * @throws EvaluationException
	 * @throws ClassNotFoundException
	 * @throws NoSuchMethodException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	protected void checkBuiltin(String iri, ITerm builtin,
			String... builtinNames) throws SecurityException,
			IllegalArgumentException, EvaluationException,
			ClassNotFoundException, NoSuchMethodException,
			InstantiationException, IllegalAccessException,
			InvocationTargetException {

		String builtinName = builtinNames[0];
		boolean b = true;
		String builtinNotName = null;
		if (builtinName.contains("Not")) { // if its a isNot builtin
			b = false;
			builtinNotName = builtinName.replace("IsNot", "Is");
		} else { // get out the is - builtin
			b = true;
			builtinNotName = builtinName.replace("Is", "IsNot");
		}

		// 2 lists for is and isNot builtins
		ArrayList<String> listIS = this.getISBuiltins();
		listIS.remove(builtinName);
		listIS.remove(builtinNotName);

		ArrayList<String> listISNot = this.getISNotBuiltins();
		listISNot.remove(builtinName);
		listISNot.remove(builtinNotName);

		// remove the other builtins which are also true - will be testet in
		// their own test
		for (int i = 1; i < builtinNames.length; i++) {
			checkBooleanBuiltin(b, builtin, iri, builtinNames[i]);
			listIS.remove(builtinNames[i]);
			listISNot.remove(builtinNames[i]);
			if (builtinNames[i].contains("Not")) {
				listIS.remove(builtinNames[i].replace("IsNot", "Is"));
			} else {
				listISNot.remove(builtinNames[i].replace("Is", "IsNot"));
			}
		}

		// at first check if the builtin checks the right one (or not if its a
		// NOT-builtin)
		// ex.: IsByteBuiltin - IsNotByteBuiltin
		checkBooleanBuiltin(b, builtin, iri, builtinName);
		checkBooleanBuiltin(!b, builtin, iri, builtinNotName);

		// check: all other should be false ...
		for (String name : listIS) {
			checkBooleanBuiltin(false, builtin, iri, name);
		}

		// check: all other should be true (not something) ...
		for (String name : listISNot) {
			checkBooleanBuiltin(true, builtin, iri, name);
		}

	}

	private ArrayList<String> getISBuiltins() {
		ArrayList<String> list = new ArrayList<String>();

		// list.add("org.deri.iris.builtins.datatype.IsDatatypeBuiltin");
		list.add(IsAnyURIBuiltin.class.getName());
		list.add(IsBase64BinaryBuiltin.class.getName());
		list.add(IsBooleanBuiltin.class.getName());
		list.add(IsByteBuiltin.class.getName());
		list.add(IsDateBuiltin.class.getName());
		list.add(IsDateTimeBuiltin.class.getName());
		list.add(IsDateTimeStampBuiltin.class.getName());
		list.add(IsDayTimeDurationBuiltin.class.getName());
		list.add(IsDecimalBuiltin.class.getName());
		list.add(IsDoubleBuiltin.class.getName());
		list.add(IsDurationBuiltin.class.getName());
		list.add(IsFloatBuiltin.class.getName());
		list.add(IsGDayBuiltin.class.getName());
		list.add(IsGMonthBuiltin.class.getName());
		list.add(IsGMonthDayBuiltin.class.getName());
		list.add(IsGYearMonthBuiltin.class.getName());
		list.add(IsHexBinaryBuiltin.class.getName());
		list.add(IsIDBuiltin.class.getName());
		list.add(IsIDREFBuiltin.class.getName());
		list.add(IsIntBuiltin.class.getName());
		list.add(IsIntegerBuiltin.class.getName());
		list.add(IsIriBuiltin.class.getName());
		list.add(IsLanguageBuiltin.class.getName());
		list.add(IsListBuiltin.class.getName());
		list.add(IsLongBuiltin.class.getName());
		list.add(IsNameBuiltin.class.getName());
		list.add(IsNCNameBuiltin.class.getName());
		list.add(IsNegativeIntegerBuiltin.class.getName());
		list.add(IsNMTOKENBuiltin.class.getName());
		list.add(IsNonNegativeIntegerBuiltin.class.getName());
		list.add(IsNonPositiveIntegerBuiltin.class.getName());
		list.add(IsNormalizedStringBuiltin.class.getName());

		list.add(IsNumericBuiltin.class.getName());
		list.add(IsPlainLiteralBuiltin.class.getName());
		list.add(IsPositiveIntegerBuiltin.class.getName());
		list.add(IsQNameBuiltin.class.getName());
		list.add(IsShortBuiltin.class.getName());
		list.add(IsSqNameBuiltin.class.getName());
		list.add(IsStringBuiltin.class.getName());
		list.add(IsTimeBuiltin.class.getName());
		list.add(IsTokenBuiltin.class.getName());
		list.add(IsUnsignedByteBuiltin.class.getName());
		list.add(IsUnsignedIntBuiltin.class.getName());
		list.add(IsUnsignedLongBuiltin.class.getName());
		list.add(IsUnsignedShortBuiltin.class.getName());
		list.add(IsXMLLiteralBuiltin.class.getName());
		list.add(IsYearMonthDurationBuiltin.class.getName());

		// list.add(SameTypeBuiltin");
		return list;
	}

	private ArrayList<String> getISNotBuiltins() {
		ArrayList<String> list = new ArrayList<String>();
		
		list.add(IsNotAnyURIBuiltin.class.getName());
//		list.add(IsNOTATIONBuiltin.class.getName());
		list.add(IsNotBase64BinaryBuiltin.class.getName());
		list.add(IsNotBooleanBuiltin.class.getName());
		list.add(IsNotByteBuiltin.class.getName());
		list.add(IsNotDateBuiltin.class.getName());
		list.add(IsNotDateTimeBuiltin.class.getName());
		list.add(IsNotDateTimeStampBuiltin.class.getName());
		list.add(IsNotDayTimeDurationBuiltin.class.getName());
		list.add(IsNotDecimalBuiltin.class.getName());
		list.add(IsNotDoubleBuiltin.class.getName());
		list.add(IsNotDurationBuiltin.class.getName());
		list.add(IsNotFloatBuiltin.class.getName());
		list.add(IsNotGDayBuiltin.class.getName());
		list.add(IsNotGMonthBuiltin.class.getName());
		list.add(IsNotGMonthDayBuiltin.class.getName());
		list.add(IsNotGYearBuiltin.class.getName());
		list.add(IsNotGYearMonthBuiltin.class.getName());
		list.add(IsNotHexBinaryBuiltin.class.getName());
		list.add(IsNotIDBuiltin.class.getName());
		list.add(IsNotIDREFBuiltin.class.getName());
		list.add(IsNotIntBuiltin.class.getName());
		list.add(IsNotIntegerBuiltin.class.getName());
		list.add(IsNotIriBuiltin.class.getName());
		list.add(IsNotLanguageBuiltin.class.getName());
		list.add(IsNotListBuiltin.class.getName());
		list.add(IsNotLongBuiltin.class.getName());
		list.add(IsNotNameBuiltin.class.getName());
		list.add(IsNotNCNameBuiltin.class.getName());
		list.add(IsNotNegativeIntegerBuiltin.class.getName());
		list.add(IsNotNMTOKENBuiltin.class.getName());
		list
				.add(IsNotNonNegativeIntegerBuiltin.class.getName());
		list
				.add(IsNotNonPositiveIntegerBuiltin.class.getName());
		list
				.add(IsNotNormalizedStringBuiltin.class.getName());
		list.add(IsNotNOTATIONBuiltin.class.getName());
		list.add(IsNotNumericBuiltin.class.getName());
		list.add(IsNotPlainLiteralBuiltin.class.getName());
		list.add(IsNotPositiveIntegerBuiltin.class.getName());
		list.add(IsNotQNameBuiltin.class.getName());
		list.add(IsNotShortBuiltin.class.getName());
		list.add(IsNotSqNameBuiltin.class.getName());
		list.add(IsNotStringBuiltin.class.getName());
		list.add(IsNotTimeBuiltin.class.getName());
		list.add(IsNotTokenBuiltin.class.getName());
		list.add(IsNotUnsignedByteBuiltin.class.getName());
		list.add(IsNotUnsignedIntBuiltin.class.getName());
		list.add(IsNotUnsignedLongBuiltin.class.getName());
		list.add(IsNotUnsignedShortBuiltin.class.getName());
		list.add(IsNotXMLLiteralBuiltin.class.getName());
		list
				.add(IsNotYearMonthDurationBuiltin.class.getName());

		return list;

	}

}
