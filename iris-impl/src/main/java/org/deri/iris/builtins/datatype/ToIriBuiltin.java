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

import org.deri.iris.api.basics.IPredicate;
import org.deri.iris.api.terms.IStringTerm;
import org.deri.iris.api.terms.ITerm;
import org.deri.iris.api.terms.concrete.IIri;
import org.deri.iris.api.terms.concrete.IPlainLiteral;
import org.deri.iris.api.terms.concrete.IXMLLiteral;

/**
 * Represents a data type conversion function, which converts supported data
 * type instances to IRI instances. The following data types are supported:
 * <ul>
 * <li>String</li>
 * <li>Text</li>
 * <li>XMLLiteral</li>
 * </ul>
 */
public class ToIriBuiltin extends ConversionBuiltin {

	private static final IPredicate PREDICATE = BASIC.createPredicate("TO_IRI",
			2);

	/**
	 * Creates a new instance of this builtin.
	 * 
	 * @param terms The term representing the data type instance to be
	 *            converted.
	 */
	public ToIriBuiltin(ITerm... terms) {
		super(PREDICATE, terms);
	}

	@Override
	protected ITerm convert(ITerm term) {
		if (term instanceof IIri) {
			return term;
		} else if (term instanceof IStringTerm) {
			return toIri((IStringTerm) term);
		} else if (term instanceof IXMLLiteral) {
			return toText((IXMLLiteral) term);
		} else if (term instanceof IPlainLiteral) {
			return toText((IPlainLiteral) term);
		}

		return null;
	}

	/**
	 * Converts a Text term to an IRI term.
	 * 
	 * @param term The Text term to be converted.
	 * @return A new IRI term representing the result of the conversion, or
	 *         <code>null</code> if the conversion fails.
	 */
	public static IIri toText(IPlainLiteral term) {
		return toIri(term.getString());
	}

	/**
	 * Converts a XMLLiteral term to an IRI term.
	 * 
	 * @param term The Text term to be converted.
	 * @return A new IRI term representing the result of the conversion, or
	 *         <code>null</code> if the conversion fails.
	 */
	public static IIri toText(IXMLLiteral term) {
		return toIri(term.getString());
	}

	/**
	 * Converts a String term to an IRI term.
	 * 
	 * @param term The String term to be converted.
	 * @return A new IRI term representing the result of the conversion, or
	 *         <code>null</code> if the conversion fails.
	 */
	public static IIri toIri(IStringTerm term) {
		String value = term.getValue();
		return toIri(value);
	}

	private static IIri toIri(String iri) {
		try {
			return CONCRETE.createIri(iri);
		} catch (Exception e) {
			throw new IllegalArgumentException(
					"The specified string can not be cast to IRI", e);
		}
	}

}
