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
import java.util.regex.Pattern;

import org.deri.iris.api.terms.ITerm;
import org.deri.iris.api.terms.concrete.IBase64Binary;

/**
 * <p>
 * Simple implementation of the IBase64Binary.
 * </p>
 * <p>
 * $Id$
 * </p>
 * 
 * @author Richard Pöttler (richard dot poettler at deri dot at)
 * @version $Revision$
 */
public class Base64Binary implements IBase64Binary {

	public static final Pattern PATTERN = Pattern.compile("([a-zA-Z0-9/+]{4})*"
			+ "(([a-zA-Z0-9/+]{2}[AEIMQUYcgkosw048]=)|"
			+ "([a-zA-Z0-9/+]{1}[AQgw]==))?");

	private String content = "";

	Base64Binary() {
	}

	Base64Binary(final String content) {
		this();
		_setValue(content);
	}

	public int compareTo(ITerm o) {
		if (o == null)
			return 1;

		Base64Binary b64 = (Base64Binary) o;
		return content.compareTo(b64.getValue());
	}

	public boolean equals(final Object obj) {
		if (!(obj instanceof Base64Binary)) {
			return false;
		}
		Base64Binary b64 = (Base64Binary) obj;
		return content.equals(b64.content);
	}

	public String getValue() {
		return content;
	}

	public int hashCode() {
		return content.hashCode();
	}

	private void _setValue(final String content) {
		if (PATTERN.matcher(content).matches()) {
			this.content = content;
		} else {
			throw new IllegalArgumentException("Couldn't parse " + content
					+ " to a valid Base64Binary");
		}
	}

	public String toString() {
		return getClass().getSimpleName() + "(" + getValue() + ")";
	}

	public boolean isGround() {
		return true;
	}

	public URI getDatatypeIRI() {
		return URI.create("http://www.w3.org/2001/XMLSchema#base64Binary");
	}

	public String toCanonicalString() {
		return new String(getValue());
	}
}
