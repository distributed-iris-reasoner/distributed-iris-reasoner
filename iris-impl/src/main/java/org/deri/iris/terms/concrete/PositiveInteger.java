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
package org.deri.iris.terms.concrete;

import java.math.BigInteger;
import java.net.URI;

import org.deri.iris.api.terms.concrete.IPositiveInteger;

/**
 * <p>
 * A simple implementation of PositiveInteger.
 * </p>
 * 
 * @author Adrian Marte
 */
public class PositiveInteger extends IntegerTerm implements IPositiveInteger {

	/**
	 * Creates a new PositiveInteger for the specified integer.
	 * 
	 * @param value The integer representing a number not less than 1.
	 * @throws IllegalArgumentException If the specified integer is less than 1.
	 */
	public PositiveInteger(int value) {
		this(BigInteger.valueOf(value));
	}

	/**
	 * Creates a new PositiveInteger for the specified BigInteger.
	 * 
	 * @param value The BigInteger representing a number not less than 1.
	 * @throws IllegalArgumentException If the specified BigInteger is less than
	 *             1.
	 */
	public PositiveInteger(BigInteger value) {
		super(value);

		if (value.compareTo(BigInteger.ONE) < 0) {
			throw new IllegalArgumentException("Value must not be less than 1");
		}
	}

	@Override
	public URI getDatatypeIRI() {
		return URI.create(IPositiveInteger.DATATYPE_URI);
	}

}
