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

import org.deri.iris.api.terms.concrete.IUnsignedLong;

/**
 * <p>
 * A simple implementation of UnsignedLong.
 * </p>
 * 
 * @author Adrian Marte
 */
public class UnsignedLong extends NonNegativeInteger implements IUnsignedLong {

	/**
	 * Creates a new UnsignedLong for the specified BigInteger.
	 * 
	 * @param value The BigInteger representing a number not less than 0 and not
	 *            greater than 18446744073709551615.
	 * @throws IllegalArgumentException If the specified BigInteger is less than
	 *             0 or greater than 18446744073709551615.
	 */
	public UnsignedLong(BigInteger value) {
		super(value);

		if (value.compareTo(IUnsignedLong.MAX_INCLUSIVE) >= 1) {
			throw new IllegalArgumentException(
					"Value must not be greater than "
							+ IUnsignedLong.MAX_INCLUSIVE);
		}
	}

	@Override
	public URI getDatatypeIRI() {
		return URI.create(IUnsignedLong.DATATYPE_URI);
	}

}
