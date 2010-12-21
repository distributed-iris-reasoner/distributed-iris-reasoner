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
import org.deri.iris.api.terms.concrete.IBase64Binary;
import org.deri.iris.api.terms.concrete.IHexBinary;

/**
 * Represents a data type conversion function, which converts supported data
 * type instances to HexBinary instances. The following data types are
 * supported:
 * <ul>
 * <li>Base64</li>
 * <li>String</li>
 * </ul>
 */
public class ToHexBinaryBuiltin extends ConversionBuiltin {

	private static final IPredicate PREDICATE = BASIC.createPredicate(
			"TO_HEXBINARY", 2);

	/**
	 * Creates a new instance of this builtin.
	 * 
	 * @param terms The term representing the data type instance to be
	 *            converted.
	 */
	public ToHexBinaryBuiltin(ITerm... terms) {
		super(PREDICATE, terms);
	}

	@Override
	protected ITerm convert(ITerm term) {
		if (term instanceof IHexBinary) {
			return term;
		} else if (term instanceof IBase64Binary) {
			return toHexBinary((IBase64Binary) term);
		} else if (term instanceof IStringTerm) {
			return toHexBinary((IStringTerm) term);
		}

		return null;
	}

	/**
	 * Converts a String term to a HexBinary term.
	 * 
	 * @param term The String term to be converted.
	 * @return A new HexBinary term representing the result of the conversion,
	 *         or <code>null</code> if the conversion fails.
	 */
	public static IHexBinary toHexBinary(IStringTerm term) {
		String binary = term.getValue();
		return toHexBinary(binary);
	}

	/**
	 * Converts a Base64 term to a HexBinary term.
	 * 
	 * @param term The Base64 term to be converted.
	 * @return A new HexBinary term representing the result of the conversion.
	 */
	public static IHexBinary toHexBinary(IBase64Binary term) {
		String binary = term.getValue();
		return toHexBinary(binary);
	}

	private static IHexBinary toHexBinary(String binary) {
		try {
			return CONCRETE.createHexBinary(binary);
		} catch (Exception e) {
			throw new IllegalArgumentException(
					"The specified string does not represent a hex binary value",
					e);
		}
	}

}
