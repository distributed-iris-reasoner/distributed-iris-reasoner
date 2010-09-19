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
package org.deri.iris.api;

import java.util.Collection;
import java.util.List;

import org.deri.iris.api.basics.IQuery;
import org.deri.iris.api.basics.IRule;

/**
 * <p>
 * Interface for a rule optimisation algorithm.
 * </p>
 * @author Richard Pöttler (richard dot poettler at deri dot at)
 * @since 0.6
 */
public interface IProgramOptimisation {

	/**
	 * Applies the optimisation algorithm to a set of rules. A 
	 * {@link IProgramOptimisation.Result Result} which 
	 * contains the transformed rules and the adjusted query.  
	 * @param rules the rules to transform 
	 * @param query the query for which to transform the rules
	 * @return a optimisation result or <code>null</code>, if the
	 * transformation failed.
	 */
	public Result optimise(final Collection<IRule> rules, final IQuery query);

	/**
	 * Represents the result of a rule optimisation. <b>The
	 * <code>rules</code> and <code>query</code> of this class 
	 * are non-final mutable fields.</b>
	 * @author Richard Pöttler (richard dot poettler at sti2 dot at)
	 * @since 0.6
	 */
	public static class Result {

		/**
		 * Constructor to create and initialize a result in one line.
		 * @param r the rules to set
		 * @param q the query to set
		 */
		public Result(final List<IRule> r, final IQuery q) {
			rules = r;
			query = q;
		}

		/** The transformed rules. */
		public List<IRule> rules;

		/** The adjusted query. */
		public IQuery query;
	}
}
