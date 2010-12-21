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
package org.deri.iris.api.factory;

import java.util.Collection;

import org.deri.iris.api.basics.IRule;
import org.deri.iris.api.graph.IPredicateGraph;

/**
 * <p>
 * An interface that can be used to create a predicate graph.
 * </p>
 * <p>
 * $Id$
 * </p>
 * 
 * @author Richard Pöttler (richard dot poettler at deri dot at)
 * @version $Revision$
 */
public interface IGraphFactory {

	/**
	 * Constructs a new empty predicate graph.
	 * @return the constructed graph
	 */
	public IPredicateGraph createPredicateGraph();

	/**
	 * Constructs a new predicate graph initialized with a collection of
	 * rules.
	 * @param r the rules with which to initialize the graph
	 * @return the constructed graph
	 */
	public IPredicateGraph createPredicateGraph(final Collection<IRule> r);
}
