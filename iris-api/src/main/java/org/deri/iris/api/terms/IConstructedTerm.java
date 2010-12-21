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

import java.util.List;
import java.util.Set;

/**
 * <p>
 * An interface for representing a constructed term (function symbol). 
 * A constructed term is a term built from function-s and subter-s.
 * </p>
 * 
 * @author Darko Anicic, DERI Innsbruck
 * @date   14.11.2005 11:34:59
 */
public interface IConstructedTerm extends ITerm{

	public List<ITerm> getValue();
	
	/**
	 * Get the name of the constructed term (function symbol).
	 * 
	 * @return	The name of the constructed term.
	 */
	public String getFunctionSymbol();
	
	/**
	 * Returns a list of all terms from this constructed term (function symbol).
	 * 
	 * @return	List of all terms from this constructed term.
	 */
	public List<ITerm> getParameters();
	
	/**
	 * Returns all distinct variables from this tuple.
	 * 
	 * @return All distinct variables from this tuple.
	 */
	public Set<IVariable> getVariables();
}
