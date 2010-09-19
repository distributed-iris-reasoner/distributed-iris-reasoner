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

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.deri.iris.api.basics.ILiteral;
import org.deri.iris.api.basics.IPredicate;
import org.deri.iris.api.basics.IQuery;
import org.deri.iris.api.basics.IRule;

public class CommonPredicateTagger implements IPredicateTagger {
	
	private List<IRule> mRules;
	private Set<IPredicate> mMemoPredicates;

	public CommonPredicateTagger(List<IRule> rules, IQuery query) {
		mRules = rules;
		mMemoPredicates = tagMemoPredicates(query);
	}
	
	private Set<IPredicate> tagMemoPredicates(IQuery query) {
		Set<IPredicate> headPredicates = new HashSet<IPredicate>();
		Set<IPredicate> memoPredicates = new HashSet<IPredicate>();
		List<IPredicate> bodyPredicates = new LinkedList<IPredicate>();
		
		for (ILiteral queryLiteral : query.getLiterals()) {
			headPredicates.add(queryLiteral.getAtom().getPredicate());
		}
		
		for (IPredicate hp : headPredicates) {
			bodyPredicates.addAll( getBodyPredicatesAsList(hp) );
		}
		
		if (!bodyPredicates.isEmpty()) {
			Map<IPredicate, Integer> occurrence = new HashMap<IPredicate, Integer>(); 
			for (IPredicate bp : bodyPredicates) {
				Integer value = occurrence.get(bp);
				value = (value == null) ? 0 : value;
				occurrence.put(bp, ++value);
			}
			
			Map<Integer, IPredicate> counts = new HashMap<Integer, IPredicate>();
			for (Entry<IPredicate, Integer> entry : occurrence.entrySet()) {
				counts.put(entry.getValue(), entry.getKey());
			}
			
			Integer max = Collections.max(counts.keySet());
			if (counts.containsKey( max )) {
				IPredicate mostCommonPredicate = counts.get( max );		
				memoPredicates.add(mostCommonPredicate);
			}
		}
		
		return memoPredicates;
	}
	
	/**
	 * Get body predicates of all rules that match the given predicate.
	 * @param headPredicate predicate
	 * @return list of predicates that depend on <code>headPredicate</code>
	 */
	private List<IPredicate> getBodyPredicatesAsList(IPredicate headPredicate) {
		List<IPredicate> bodyPredicates = new LinkedList<IPredicate>();
		for (IRule rule : mRules) {
			if (getHeadPredicate(rule).equals( headPredicate )) { // Matching rule
				for (ILiteral bodyLiteral : rule.getBody()) { // Add predicates
					bodyPredicates.add( bodyLiteral.getAtom().getPredicate() );
				}
			}
		}		
		return bodyPredicates;
	}
	
	private IPredicate getHeadPredicate(IRule rule) {
		return rule.getHead().get(0).getAtom().getPredicate();
	}

	public List<IRule> getRules() {
		return mRules;
	}

	public Set<IPredicate> getMemoPredicates() {
		return mMemoPredicates;
	}

}