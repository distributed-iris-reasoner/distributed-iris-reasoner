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

import org.deri.iris.api.terms.concrete.ILanguage;

/**
 * <p>
 * A simple implementation of a language tag.
 * </p>
 * 
 * @author Adrian Marte
 */
public class Language extends Token implements ILanguage {

	/**
	 * Defines the pattern of all conformant language tags.
	 */
	private static String pattern = "[a-zA-Z]{1,8}(-[a-zA-Z0-9]{1,8})*";

	/**
	 * Creates a new Language instance for the specified language tag and checks
	 * for validity of the language tag.
	 * 
	 * @param language The language tag.
	 * @throws IllegalArgumentException If the specified language is no valid
	 *             language tag.
	 */
	public Language(String language) {
		this(language, true);
	}

	/**
	 * Creates a new Language instance for the specified language tag.
	 * 
	 * @param language The language tag.
	 * @param isValidating If set to true the specified language is tested for
	 *            validity.
	 * @throws IllegalArgumentException If isValidating is set to true and the
	 *             specified language is no valid language tag.
	 */
	public Language(String language, boolean isValidating)
			throws IllegalArgumentException {
		super(language);

		if (isValidating && !validate(value)) {
			throw new IllegalArgumentException("Invalid language tag");
		}
	}

	public static boolean validate(String language) {
		return language.matches(pattern);
	}

	@Override
	public URI getDatatypeIRI() {
		return URI.create(ILanguage.DATATYPE_URI);
	}

}
