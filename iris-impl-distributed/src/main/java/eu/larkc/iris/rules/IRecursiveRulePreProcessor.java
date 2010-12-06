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
package eu.larkc.iris.rules;

import java.util.List;

import org.deri.iris.EvaluationException;
import org.deri.iris.api.basics.IRule;
import org.deri.iris.facts.IFacts;

/**
 * An interface for detecting recursive rule sets possibly optimizing recursion.
 * 
 * @author fisf, Florian Fischer
 */
public interface IRecursiveRulePreProcessor {

	/**
	 * Pre-process the given rules and facts.
	 * 
	 * @param rules The rule to pre-process.
	 * @param facts The facts to pre-process.
	 * @throws EvaluationException
	 * 
	 * @return The processed and optimized input rules.
	 */
	public List<IRule> process(List<IRule> rules)
			throws EvaluationException;
	
	/**
	 * If possible returns an independent non-recursive subset of the rules that can be computed independently in an optimized fashion.
	 * 
	 * @return the independent, non-recursive subset of the processed input rules
	 * @throws EvaluationException
	 */
	public List<IRule> getNonrecursive() throws EvaluationException;
	
	/**
	 * Returns the set of recursive rules that require special treatment and are more expensive to compute.
	 * 
	 * @return the recursive subset of the processed input rules
	 * @throws EvaluationException
	 */
	public List<IRule> getRecursive() throws EvaluationException;
	
	/**
	 * Returns if an input rule set is recursive.
	 * 
	 * @return 
	 * @throws EvaluationException
	 */
	public Boolean getIsRecursive() throws EvaluationException;
}
