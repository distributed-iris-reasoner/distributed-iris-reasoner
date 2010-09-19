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

import org.deri.iris.api.terms.concrete.ILongTerm;

/**
 * <p>
 * A simple implementation of Long.
 * </p>
 * 
 * @author Adrian Marte
 */
public class LongTerm extends IntegerTerm implements ILongTerm {

	/**
	 * Creates a new LongTerm for the specified Long.
	 * 
	 * @param value The Long value.
	 */
	public LongTerm(long value) {
		super(BigInteger.valueOf(value));
	}

	@Override
	public URI getDatatypeIRI() {
		return URI.create(ILongTerm.DATATYPE_URI);
	}

}
