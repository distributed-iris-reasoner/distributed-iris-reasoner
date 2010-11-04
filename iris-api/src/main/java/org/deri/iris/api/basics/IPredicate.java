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

/**
 * <p>
 * A predicate is either a relation or the boolean-valued function that amounts
 * to the characteristic function or the indicator function of such a relation.
 * </p>
 * <p>
 * A predicate is characterized by a predicate symbol and an arity of the
 * predicate.
 * </p>
 * <p>
 * $Id: IPredicate.java,v 1.7 2007-07-25 08:16:56 poettler_ric Exp $
 * </p>
 * 
 * @author Darko Anicic, DERI Innsbruck
 * @author Richard Pöttler (richard dot poettler at deri dot at)
 * @version $Revision: 1.7 $
 */

public interface IPredicate extends Comparable<IPredicate>, Serializable {
	/**
	 * <p>
	 * Returns the predicate symbol. <p/>
	 * 
	 * @return The predicate symbol.
	 */
	public String getPredicateSymbol();

	/**
	 * <p>
	 * Returns the arity of the predicate. <p/>
	 * 
	 * @return The arity.
	 */
	public int getArity();
}
