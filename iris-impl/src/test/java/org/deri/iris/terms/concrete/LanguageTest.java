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

import org.deri.iris.api.terms.IConcreteTerm;
import org.deri.iris.api.terms.concrete.ILanguage;
import org.deri.iris.terms.AbstractConcreteTermTest;

/**
 * <p>
 * Test the implementation of the Language data-type.
 * </p>
 * 
 * @author Adrian Marte
 */
public class LanguageTest extends AbstractConcreteTermTest {

	@Override
	protected IConcreteTerm createBasic() {
		return new Language("   en");
	}

	@Override
	protected String createBasicString() {
		return "en";
	}

	@Override
	protected IConcreteTerm createEqual() {
		return new Language("en");
	}

	@Override
	protected String createEqualString() {
		return "en";
	}

	@Override
	protected IConcreteTerm createGreater() {
		return new Language("es");
	}

	@Override
	protected String createGreaterString() {
		return "es";
	}

	@Override
	protected URI getDatatypeIRI() {
		return URI.create(ILanguage.DATATYPE_URI);
	}

	public void testValidity() {
		try {
			new Language("123");
			fail("Did not recognize invalid language");
		} catch (IllegalArgumentException e) {
		}
		
		try {
			new Language("de1");
			fail("Did not recognize invalid language");
		} catch (IllegalArgumentException e) {
		}

		try {
			new Language("abcdefghi");
			fail("Did not recognize invalid language");
		} catch (IllegalArgumentException e) {
		}

		try {
			new Language("abcdefgh-abcdefgh");
		} catch (IllegalArgumentException e) {
			fail("Did not recognize valid language");
		}

		try {
			new Language("abcdefgh-abcdefghi");
			fail("Did not recognize invalid language");
		} catch (IllegalArgumentException e) {
		}

		try {
			new Language("de-de");
		} catch (IllegalArgumentException e) {
			fail("Did not recognize valid language");
		}
	}

}
