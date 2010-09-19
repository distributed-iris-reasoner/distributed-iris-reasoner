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
package org.deri.iris.facts;

import org.deri.iris.api.basics.IPredicate;
import org.deri.iris.api.basics.ITuple;
import org.deri.iris.storage.IRelation;

/**
 * <p>
 * Interface for pluggable datasources for iris.
 * </p>
 * @author Richard Pöttler (richard dot poettler at deri dot at)
 */
public interface IDataSource {

	/**
	 * <p>
	 * Retrieves some tuples for a given predicate from the data source and
	 * adds it to a given relation.
	 * </p>
	 * <p>
	 * The terms in <code>from</code> and
	 * <code>to</code> set the lower and upper bounds for the terms in the
	 * corresponding columns of the tuples, which should be added to the
	 * tuple collection. <code>null</code> in the <code>from</code> or 
	 * <code>to</code> list, stands for the smallest, respectively 
	 * biggest possible term for this column.
	 * </p>
	 * @param p the predicate for which to retrieve the tuples (because one
	 * data source might hold tuples for multiple predicates)
	 * @param from the lower bound for the tuples which should be added to
	 * the relation (<code>null</code> is equivalent to a tuple containing
	 * only <code>null</code>s)
	 * @param to the upper bound for the tuples which should be added to
	 * the relation (<code>null</code> is equivalent to a tuple containing
	 * only <code>null</code>s)
	 * @param r the relation where to add the tuples
	 */
	public void get(final IPredicate p, final ITuple from, final ITuple to, final IRelation r);
}
