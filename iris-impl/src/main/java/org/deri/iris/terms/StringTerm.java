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
package org.deri.iris.terms;

import java.net.URI;

import org.deri.iris.api.terms.IStringTerm;
import org.deri.iris.api.terms.ITerm;

/**
 * <p>
 * Simple implementation of the IStringTerm.
 * </p>
 * <p>
 * $Id$
 * </p>
 * 
 * @author Richard Pöttler (richard dot poettler at deri dot at)
 * @version $Revision$
 */
public class StringTerm implements IStringTerm {

	private String value = "";

	StringTerm(final String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public boolean isGround() {
		return true;
	}

	public int compareTo(ITerm o) {
		if (o == null || !(o instanceof IStringTerm)) {
			return 1;
		}

		IStringTerm st = (IStringTerm) o;
		return value.compareTo(st.getValue());
	}

	public int hashCode() {
		return value.hashCode();
	}

	public boolean equals(final Object o) {
		if (!(o instanceof IStringTerm)) {
			return false;
		}
		IStringTerm st = (IStringTerm) o;
		return value.equals(st.getValue());
	}

	/**
	 * Simple toString() method wich only returns the holded value surrounded by
	 * &quot;'&quot;.
	 * 
	 * @return the containing String
	 */
	public String toString() {
		return "'" + value + "'";
	}

	public URI getDatatypeIRI() {
		return URI.create("http://www.w3.org/2001/XMLSchema#string");
	}

	public String toCanonicalString() {
		return new String(value);
	}
}
