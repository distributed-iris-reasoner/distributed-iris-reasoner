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

import org.deri.iris.api.terms.concrete.IToken;

/**
 * <p>
 * A simple implementation of Token.
 * </p>
 * 
 * @author Adrian Marte
 */
public class Token extends NormalizedString implements IToken {

	private static String[] removePatterns = new String[] { "[\\x20]{2,}" };

	/**
	 * Creates a new Token for the specified string. The string is normalized if
	 * it is not normalized already.
	 * 
	 * @param string The normalized or non-normalized string.
	 */
	public Token(String string) {
		super(string);
		value = normalize(value);
	}

	public static String normalize(String string) {
		// Remove carriage-returns, line-feeds and tabs.
		String normalizedString = NormalizedString.normalize(string);

		// Remove any sequence of two or more spaces
		for (String pattern : removePatterns) {
			normalizedString = normalizedString.replaceAll(pattern, " ");
		}

		// Remove leading or trailing spaces.
		return normalizedString.trim();
	}

	@Override
	public URI getDatatypeIRI() {
		return URI.create(IToken.DATATYPE_URI);
	}

}
