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
import org.deri.iris.api.terms.concrete.INormalizedString;
import org.deri.iris.terms.AbstractConcreteTermTest;

/**
 * <p>
 * Test the implementation of the NormalizedString data-type.
 * </p>
 * 
 * @author Adrian Marte
 */
public class NormalizedStringTest extends AbstractConcreteTermTest {

	@Override
	protected IConcreteTerm createBasic() {
		return new NormalizedString("\nbla bla \tbla  \r     foo");
	}

	@Override
	protected String createBasicString() {
		return "bla bla bla       foo";
	}

	@Override
	protected IConcreteTerm createEqual() {
		return new NormalizedString("bla bla bla       foo");
	}

	@Override
	protected String createEqualString() {
		return "bla bla bla       foo";
	}

	@Override
	protected IConcreteTerm createGreater() {
		return new NormalizedString("lulz");
	}

	@Override
	protected String createGreaterString() {
		return "lulz";
	}

	@Override
	protected URI getDatatypeIRI() {
		return URI.create(INormalizedString.DATATYPE_URI);
	}

}
