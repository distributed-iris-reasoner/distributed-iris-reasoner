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

package org.deri.iris.api.terms;

import java.net.URI;

/**
 * <p>
 * An interface representing a concrete term. A concrete term has a
 * corresponding data type URI. For instance, a term representing a double data
 * type should return the URI "http://www.w3.org/2001/XMLSchema#double".
 * </p>
 * <p>
 * Remark: IRIS supports data types according to the standard specification for
 * primitive XML Schema data types and additional data types required in RIF.
 * </p>
 * 
 * @see <a href="http://www.w3.org/TR/xmlschema-2/">XML Schema: Datatypes</a>
 * @see <a href="http://www.w3.org/2005/rules/wiki/DTB">RIF Datatypes and
 *      Built-Ins</a>
 */
public interface IConcreteTerm extends ITerm {

	/**
	 * Returns the fully qualified identifier for the data type corresponding to
	 * this term. For instance, a terms representing a double data type should
	 * return the URI "http://www.w3.org/2001/XMLSchema#double".
	 * 
	 * @return The fully qualified identifier for the data type corresponding to
	 *         this term.
	 */
	public URI getDatatypeIRI();

	/**
	 * Returns a canonical string representation of this term.
	 * 
	 * @return A canonical string representation of this term.
	 */
	public String toCanonicalString();

}
