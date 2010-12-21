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

import org.deri.iris.api.terms.concrete.IUnsignedInt;

/**
 * <p>
 * A simple implementation of UnsignedInt.
 * </p>
 * 
 * @author Adrian Marte
 */
public class UnsignedInt extends UnsignedLong implements IUnsignedInt {

	/**
	 * Creates a new UnsignedInt for the specified long.
	 * 
	 * @param value The long representing a number not less than 0 and not
	 *            greater than 4294967295.
	 * @throws IllegalArgumentException If the specified long is less than 0 or
	 *             greater than 4294967295.
	 */
	public UnsignedInt(long value) {
		super(BigInteger.valueOf(value));

		if (value > IUnsignedInt.MAX_INCLUSIVE) {
			throw new IllegalArgumentException(
					"Value must not be greater than "
							+ IUnsignedInt.MAX_INCLUSIVE);
		}
	}

	@Override
	public URI getDatatypeIRI() {
		return URI.create(IUnsignedInt.DATATYPE_URI);
	}

}
