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
package org.deri.iris.api.terms;

/**
 * <p>
 * An interface which defines a term. A term is a name for an object
 * in the universe of discourse. There are three types of terms:
 * <ul>
 * <li> variables</li>
 * <li> constants</li>
 * <li> constructed terms (functional symbols)</li>
 * </ul>
 * </p>
 * <p>
 * By convention <code>null</code> is the smalles possible term of all types.
 * So if you compare a term using the compare method you will always recieve
 * a positive number.
 * </p>
 * <p>
 * $Id: ITerm.java,v 1.15 2007-10-15 15:20:38 bazbishop237 Exp $
 * </p>
 * @author Darko Anicic, DERI Innsbruck
 * @author Richard Pöttler (richard dot poettler at deri dot at)
 */

public interface ITerm extends Comparable<ITerm>{

	/**
	 * Checks whether the term is ground (a term with no variables).
	 * 
	 * @return	<code>true</code> if the term is ground, 
	 * 			otherwise <code>false</code>.
	 */
	public boolean isGround();
	
	/**
	 * Returns a vale of the term.
	 * 
	 * @return	The term value.
	 */
	public Object getValue();
}
