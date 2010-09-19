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

import java.net.URI;

import org.deri.iris.api.terms.ITerm;
import org.deri.iris.api.terms.concrete.INormalizedString;

/**
 * <p>
 * A simple implementation of NormalizedString.
 * </p>
 * 
 * @author Adrian Marte
 */
public class NormalizedString implements INormalizedString {

	protected String value;

	private static String[] removePatterns = new String[] { "\\t", "\\n", "\\r" };

	/**
	 * Creates a new NormalizedString instance. The specified string is
	 * normalized if it is not normalized already.
	 * 
	 * @param string The normalized or non-normalized string.
	 */
	public NormalizedString(String string) {
		value = normalize(string);
	}

	public static String normalize(String string) {
		String normalizedString = string;

		for (String pattern : removePatterns) {
			normalizedString = normalizedString.replaceAll(pattern, "");
		}

		return normalizedString;
	}

	public String getValue() {
		return value;
	}

	public URI getDatatypeIRI() {
		return URI.create(INormalizedString.DATATYPE_URI);
	}

	public String toCanonicalString() {
		return value;
	}

	@Override
	public String toString() {
		return toCanonicalString();
	}

	public boolean isGround() {
		return true;
	}

	public int compareTo(ITerm o) {
		if (!(o instanceof INormalizedString)) {
			return 1;
		}

		INormalizedString thatString = (INormalizedString) o;
		return value.compareTo(thatString.getValue());
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof INormalizedString)) {
			return false;
		}

		INormalizedString thatString = (INormalizedString) obj;
		return value.equals(thatString.getValue());
	}

	@Override
	public int hashCode() {
		return value.hashCode();
	}

}
