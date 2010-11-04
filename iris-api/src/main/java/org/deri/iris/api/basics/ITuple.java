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
package org.deri.iris.api.basics;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.deri.iris.api.terms.ITerm;
import org.deri.iris.api.terms.IVariable;

/**
 * <p>
 * Represents a tuple. A tuple is a list of terms which represents a record in a
 * relation.
 * </p>
 * <p>
 * $Id: ITuple.java,v 1.14 2007-10-19 07:37:15 poettler_ric Exp $
 * </p>
 * 
 * @author Darko Anicic, DERI Innsbruck
 * @author Richard Pöttler (richard dot poettler at deri dot at)
 * @version $Revision: 1.14 $
 */

public interface ITuple extends List<ITerm>, Comparable<ITuple>, Serializable {

	/**
	 * Checks whether this tuple contains only ground terms.
	 * 
	 * @return <code>true</code> if all terms of this tuple are grounded;
	 *         <code>false</code> otherwise.
	 */
	public boolean isGround();

	/**
	 * Returns all distinct variables from this tupple.
	 * 
	 * @return All distinct variables from this tupple.
	 */
	public Set<IVariable> getVariables();
	
	/**
	 * Returns all variables from this tupple.
	 * 
	 * @return All variables from this tupple.
	 */
	public List<IVariable> getAllVariables();

	/**
	 * Creates a new tupel with the tuples of <code>this</code> one and
	 * appends the tuples of the submitted list at the end.
	 * @param t the tuples to add
	 * @return the newly created tuple
	 * @throws IllegalArgumentException if the list is <code>null</code>
	 */
	public ITuple append(final Collection<? extends ITerm> t);
}
