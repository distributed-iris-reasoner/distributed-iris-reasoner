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
package org.deri.iris.rules.compiler;

import static org.deri.iris.factory.Factory.BASIC;
import static org.deri.iris.factory.Factory.CONCRETE;
import static org.deri.iris.factory.Factory.TERM;

import java.util.ArrayList;
import java.util.List;

import org.deri.iris.api.basics.ILiteral;
import org.deri.iris.api.basics.ITuple;
import org.deri.iris.api.terms.ITerm;

/**
 * Helper methods for the tests in this package.
 */
public class Helper {
	public static ITerm createTerm(Object t) {
		if (t instanceof Integer)
			return CONCRETE.createInteger((Integer) t);
		else if (t instanceof String)
			return TERM.createVariable((String) t);
		else if (t instanceof ITerm)
			return (ITerm) t;

		throw new RuntimeException(
				"Unsupported term type in Helper.createTerm()");
	}

	public static List<ITerm> createTerms(Object... termObjects) {
		List<ITerm> terms = new ArrayList<ITerm>();

		for (Object o : termObjects)
			terms.add(createTerm(o));

		return terms;
	}

	public static ITuple createTuple(Object... termObjects) {
		return BASIC.createTuple(createTerms(termObjects));
	}

	public static ITerm createConstant(Object t) {
		if (t instanceof Integer)
			return CONCRETE.createInteger((Integer) t);
		else if (t instanceof String)
			return TERM.createString((String) t);
		else if (t instanceof ITerm)
			return (ITerm) t;

		throw new RuntimeException(
				"Unsupported term type in Helper.createTerm()");
	}

	public static List<ITerm> createConstants(Object... termObjects) {
		List<ITerm> terms = new ArrayList<ITerm>();

		for (Object o : termObjects)
			terms.add(createConstant(o));

		return terms;
	}

	public static ITuple createConstantTuple(Object... termObjects) {
		return BASIC.createTuple(createConstants(termObjects));
	}

	public static ITerm createConstructedTerm(String symbol,
			Object... termObjects) {
		return TERM.createConstruct(symbol, createTerms(termObjects));
	}

	public static ILiteral createLiteral(boolean positive, String predicate,
			Object... termObjects) {
		ITuple tuple = createTuple(termObjects);
		return BASIC.createLiteral(positive, BASIC.createPredicate(predicate,
				tuple.size()), tuple);
	}
}
