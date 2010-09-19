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
package org.deri.iris.api.terms.concrete;

import org.deri.iris.api.terms.IConcreteTerm;

/**
 * Represents the rdf:PlainLiteral data type, formerly known as rdf:text.
 * rdf:PlainLiteral is an internationalized string value that contains a
 * language tag indicating it's spoken language, e.g. "Padre de familia@es".
 */
public interface IPlainLiteral extends IConcreteTerm {

	/**
	 * Returns the wrapped type. The first element of this array is the string
	 * and the second is the language.
	 * 
	 * @return The wrapped type.
	 */
	public String[] getValue();

	/**
	 * Returns the string, e.g. "Padre de familia", if this text represents
	 * "Padre de familia@es".
	 * 
	 * @return The text.
	 */
	public String getString();

	/**
	 * Returns the language tag, e.g. "es", if this text represents
	 * "Padre de familia@es".
	 * 
	 * @return The language tag.
	 */
	public String getLang();

}
