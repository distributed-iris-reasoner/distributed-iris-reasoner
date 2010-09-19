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
import static org.deri.iris.factory.Factory.TERM;

import org.deri.iris.api.basics.IPredicate;
import org.deri.iris.api.terms.IConcreteTerm;
import org.deri.iris.api.terms.IStringTerm;
import org.deri.iris.api.terms.ITerm;
import org.deri.iris.api.terms.concrete.IPlainLiteral;
import org.deri.iris.api.terms.concrete.IXMLLiteral;

/**
 * Represents a data type conversion function, which converts supported data
 * type instances to String instances. According to the XPath casting table, all
 * data types are supported to be converted to String.
 */
public class ToStringBuiltin extends ConversionBuiltin {

	private static final IPredicate PREDICATE = BASIC.createPredicate(
			"TO_STRING", 2);

	/**
	 * Creates a new instance of this builtin.
	 * 
	 * @param terms The term representing the data type instance to be
	 *            converted.
	 */
	public ToStringBuiltin(ITerm... terms) {
		super(PREDICATE, terms);
	}

	@Override
	protected ITerm convert(ITerm term) {
		if (term instanceof IConcreteTerm) {
			return toString(term);
		}

		return null;
	}

	/**
	 * Converts a XMLLiteral term to a String term. The language tag of the
	 * XMLLiteral term is omitted.
	 * 
	 * @param term The XMLLiteral term to be converted.
	 * @return A new String term representing the result of the conversion.
	 */
	public static IStringTerm toString(IXMLLiteral term) {
		String string = term.getString();
		return TERM.createString(string);
	}

	/**
	 * Converts a PlainLiteral term to a String term. The language tag of the
	 * PlainLiteral term is omitted.
	 * 
	 * @param term The PlainLiteral term to be converted.
	 * @return A new String term representing the result of the conversion.
	 */
	public static IStringTerm toString(IPlainLiteral term) {
		String string = term.getString();
		return TERM.createString(string);
	}

	/**
	 * Converts a constant term to a String term. The
	 * <code>toCanonicalString</code> method of the given term is used to
	 * convert to String term.
	 * 
	 * @param term The term to be converted.
	 * @return A new String term representing the result of the conversion, or
	 *         <code>null</code> if the data type represented by the given term
	 *         is not supported.
	 */
	public static IStringTerm toString(ITerm term) {
		if (term instanceof IConcreteTerm) {
			if (term instanceof IPlainLiteral) {
				return toString((IPlainLiteral) term);
			} else if (term instanceof IStringTerm) {
				return (IStringTerm) term;
			} else if (term instanceof IXMLLiteral) {
				return toString((IXMLLiteral) term);
			}

			String string = ((IConcreteTerm) term).toCanonicalString();
			return TERM.createString(string);
		}

		return null;
	}

}
