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

import java.util.Set;

import org.deri.iris.api.basics.IPredicate;
import org.deri.iris.storage.IRelation;

public interface IFacts
{

	/**
	 * Get the relation associated with the given predicate and create one
	 * if one does not already exist.
	 * @param predicate The predicate identifying the relation.
	 * @return The relation associated with the given predicate.
	 */
	IRelation get( IPredicate predicate );

	/**
	 * Get the set of predicate identifying all relations known to this facts object.
	 * @return
	 */
	Set<IPredicate> getPredicates();

}
