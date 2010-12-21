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
package org.deri.iris.rules.safety;

import java.util.ArrayList;
import java.util.List;

import org.deri.iris.RuleUnsafeException;
import org.deri.iris.api.basics.ILiteral;
import org.deri.iris.api.basics.IRule;
import org.deri.iris.api.terms.IVariable;
import org.deri.iris.factory.Factory;
import org.deri.iris.facts.FiniteUniverseFacts;
import org.deri.iris.rules.IRuleSafetyProcessor;
import org.deri.iris.rules.RuleValidator;

/**
 * Uses the trick of augmenting rules to artificially limit variables.
 * If any head variable is found that does not occur in a positive ordinary predicate
 * then a literal is added to the rule body $UNIVERSE$( variable ).
 * This has the effect of binding the variable to the 'universe' of known ground terms.
 */
public class AugmentingRuleSafetyProcessor implements IRuleSafetyProcessor
{
	public IRule process( IRule rule ) throws RuleUnsafeException
	{
		RuleValidator validator = new RuleValidator( rule, true, true );
		
		List<IVariable> unlimitedVariables = validator.getAllUnlimitedVariables();
		
		if( unlimitedVariables.size() > 0 )
		{
			List<ILiteral> body = new ArrayList<ILiteral>();
			
			for( ILiteral literal : rule.getBody() )
				body.add( literal );
			
			for( IVariable variable : unlimitedVariables )
			{
				ILiteral newLiteral = Factory.BASIC.createLiteral( true, FiniteUniverseFacts.UNIVERSE, Factory.BASIC.createTuple( variable ) );
				body.add( newLiteral );
			}
			
			return Factory.BASIC.createRule( rule.getHead(), body );
		}
		else
			return rule;
	}
}
