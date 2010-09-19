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
package org.deri.iris.terms.concrete;

import java.net.URI;

import org.deri.iris.api.terms.ITerm;
import org.deri.iris.api.terms.concrete.IXMLLiteral;

/**
 * Implementation of the rdf:XMLLiteral data-type.
 * 
 * @author gigi
 */
public class XMLLiteral implements IXMLLiteral {

	/**
	 * The string representing the XML element content.
	 */
	private String string;

	/**
	 * The language identifier for this XML element.
	 */
	private String lang;

	/**
	 * Creates a new term representing a XMLLiteral.
	 * 
	 * @param string The string.
	 * @param lang The language.
	 * @throws NullPointerException If the value of <code>string</code> or
	 *             <code>lang</code> is <code>null</code>.
	 */
	XMLLiteral(String string, String lang) {
		if (string == null) {
			throw new NullPointerException("String value must not be null");
		}

		if (lang == null) {
			throw new NullPointerException("Language tag must not be null");
		}

		this.string = string;
		this.lang = lang;
	}

	/**
	 * Creates a new term representing a XMLLiteral.
	 * 
	 * @param string The string.
	 * @throws NullPointerException If the value of <code>string</code> is
	 *             <code>null</code>.
	 */
	XMLLiteral(String string) {
		this(string, "");
	}

	public String toString() {
		return toCanonicalString();
	}

	public URI getDatatypeIRI() {
		return URI
				.create("http://www.w3.org/1999/02/22-rdf-syntax-ns#XMLLiteral");
	}

	public String getString() {
		return string;
	}

	public String getLang() {
		return lang;
	}

	public String[] getValue() {
		return new String[] { string, lang };
	}

	public boolean isGround() {
		return true;
	}

	public boolean equals(Object thatObject) {
		if (!(thatObject instanceof IXMLLiteral)) {
			return false;
		}

		return compareTo((ITerm) thatObject) == 0;
	}

	public int hashCode() {
		int result = string.hashCode();

		if (lang != null) {
			result = result * 37 + lang.hashCode();
		}

		return result;
	}

	public int compareTo(ITerm thatObject) {
		if (thatObject == null) {
			return 1;
		}

		IXMLLiteral thatLiteral = (IXMLLiteral) thatObject;

		return this.getString().compareTo(thatLiteral.getString());
	}

	public String toCanonicalString() {
		return new String(string);
	}
}
