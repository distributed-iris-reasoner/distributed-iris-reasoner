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
package org.deri.iris.evaluation.topdown;

import java.util.Map;

import org.deri.iris.api.basics.IQuery;
import org.deri.iris.api.terms.ITerm;
import org.deri.iris.api.terms.IVariable;

/**
 * A query with an associated substitution form a branch/subgoal in
 * a top-down evaluation tree.
 * 
 * @author gigi
 *
 */
public class QueryWithSubstitution {

	private IQuery query;
	private Map<IVariable, ITerm> substitution;
	
	public QueryWithSubstitution() { }
	
	public QueryWithSubstitution(IQuery query, Map<IVariable, ITerm> substitution) {
		super();
		this.query = query;
		this.substitution = substitution;
	}

	public IQuery getQuery() {
		return query;
	}

	public Map<IVariable, ITerm> getSubstitution() {
		return substitution;
	}
	
	@Override
	public String toString() {
		return query.toString() + " | " + substitution.toString();
	}
	
}
