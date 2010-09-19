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
package org.deri.iris.evaluation.equivalence;

import java.util.List;

import org.deri.iris.api.basics.IQuery;
import org.deri.iris.api.basics.IRule;
import org.deri.iris.facts.IFacts;

/**
 * @author Adrian Marte
 */
public class Program {

	private List<IRule> rules;

	private IFacts facts;

	private List<IQuery> queries;

	public Program(IFacts facts, List<IRule> rules, List<IQuery> queries) {
		this.rules = rules;
		this.facts = facts;
		this.queries = queries;
	}

	public List<IRule> getRules() {
		return rules;
	}

	public void setRules(List<IRule> rules) {
		this.rules = rules;
	}

	public IFacts getFacts() {
		return facts;
	}

	public void setFacts(IFacts facts) {
		this.facts = facts;
	}

	public List<IQuery> getQueries() {
		return queries;
	}

	public void setQueries(List<IQuery> queries) {
		this.queries = queries;
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("Facts: ");
		buffer.append(facts);
		buffer.append("\n");
		
		buffer.append("Rules: ");
		buffer.append(rules);
		buffer.append("\n");
		
		buffer.append("Queries: ");
		buffer.append(queries);
		buffer.append("\n");
		
		return buffer.toString();
	}
	
}
