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
package org.deri.iris.rules.optimisation;

import org.deri.iris.api.basics.IRule;
import org.deri.iris.rules.IRuleOptimiser;
import org.deri.iris.rules.RuleManipulator;

/**
 * Replace variables with constants where possible:
 * e.g. p(?X,?Y) :- q(?X, ?Z), ?Z = 't'
 * ==>> p(?X,?Y) :- q(?X, 't')
 * 
 * This should have the effect of pushing selection criteria in to the evaluation of a relation,
 * such that fewer tuples are processed.
 * 
 * Problem!
 * p(?X), ?X = 2 is not the same as p(2), at least at the moment,
 * because with p(2) the evaluator looks for only p( integer( 2 ) ) in the relation,
 * whereas p(?X), ?X = 2 will return the entire relation and filter out what equals 2,
 * i.e. any numeric value.
 * 
 * Not sure how to fix this, but it will involve changing MixedDatatypeRelation.
 */
public class ReplaceVariablesWithConstantsOptimiser implements IRuleOptimiser
{
	public IRule optimise( IRule rule )
	{
		rule = mManipulator.replaceVariablesWithConstants( rule, true );
		rule = mManipulator.removeDuplicateLiterals( rule );
		rule = mManipulator.removeUnnecessaryEqualityBuiltins( rule );
		
		return rule;
	}

	private static RuleManipulator mManipulator = new RuleManipulator();
}
