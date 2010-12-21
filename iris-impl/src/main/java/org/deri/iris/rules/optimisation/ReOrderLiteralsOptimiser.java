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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.deri.iris.api.basics.ILiteral;
import org.deri.iris.api.basics.IRule;
import org.deri.iris.api.builtins.IBuiltinAtom;
import org.deri.iris.api.terms.IVariable;
import org.deri.iris.factory.Factory;
import org.deri.iris.rules.IRuleOptimiser;

/**
 * Re-order literals.
 * 
 * The RuleCompiler will be forced to re-order literals when it fails to set
 * up the variable bindings between consecutive sub-goals.
 * However, it might be possible to speed things up by selecting the most restrictive
 * sub-goals (in the search sense) first.
 *
 * e.g. p(x,y) :- z!=x, x<y, q(x), r(y), s(z)
 * ==>> p(x,y) :- q(x), r(y), x<y, s(z), z!=x
 */
public class ReOrderLiteralsOptimiser implements IRuleOptimiser
{
	/**
	 * First version. It works, but it is not very efficient.
	 * TODO
	 * Improve the whole thing to make it more elegant.
	 * This might interfere with magic sets, which orders sub-goals appropriately.
	 */
	public IRule optimise( IRule rule )
	{
		List<ILiteral> positiveNoVariables = new ArrayList<ILiteral>();
		List<ILiteral> builtinNoVariables = new ArrayList<ILiteral>();
		List<ILiteral> positive = new ArrayList<ILiteral>();
		List<ILiteral> builtin = new ArrayList<ILiteral>();
		List<ILiteral> negative = new ArrayList<ILiteral>();

		Set<IVariable> variablesNotInNegatedOrdinaries = new HashSet<IVariable>();
		
		// Divide the literals in to categories 
		for( ILiteral literal : rule.getBody() )
		{
			Set<IVariable> variables = literal.getAtom().getTuple().getVariables();

			if( literal.getAtom() instanceof IBuiltinAtom )
			{
				variablesNotInNegatedOrdinaries.addAll( variables );
				
				if( literal.getAtom().getTuple().getVariables().size() == 0 )
					builtinNoVariables.add( literal );
				else
					builtin.add( literal );
			}
			else if( literal.isPositive() )
			{
				variablesNotInNegatedOrdinaries.addAll( variables );

				if( literal.getAtom().getTuple().getVariables().size() == 0 )
					positiveNoVariables.add( literal );
				else
					positive.add( literal );
			}
			else if( ! literal.isPositive() )
			{
				negative.add( literal );
			}
			else
				assert false;
		}
		
		// Don't forget the head
		variablesNotInNegatedOrdinaries.addAll( rule.getHead().get( 0 ).getAtom().getTuple().getVariables() );

		List<ILiteral> newBody = new ArrayList<ILiteral>();
		
		// Do the easy ones first
		newBody.addAll( positiveNoVariables );
		newBody.addAll( builtinNoVariables );

		Set<IVariable> boundVariables = new HashSet<IVariable>();
		
		while( newBody.size() < rule.getBody().size() )
		{
			boolean somethingAdded = false;
			
			// Try and add a built-in with all variables bound
			for( ILiteral literal : builtin )
			{
				if( boundVariables.containsAll( literal.getAtom().getTuple().getVariables() ) )
				{
					builtin.remove( literal );
					newBody.add( literal );
					somethingAdded = true;
					break;
				}
			}
			if( somethingAdded )
				continue;
			
			// Add a negative if all variables are either bound or appear nowhere else
			for( ILiteral literal : negative )
			{
				Set<IVariable> literalVariables = literal.getAtom().getTuple().getVariables();
				
				boolean add = true;
				for( IVariable literalVariable : literalVariables )
				{
					if( ! boundVariables.contains( literalVariable ) && variablesNotInNegatedOrdinaries.contains( literalVariable ) )
					{
						add = false;
						break;
					}
				}
				if( add )
				{
					negative.remove( literal );
					newBody.add( literal );
					somethingAdded = true;
					break;
				}
			}
			if( somethingAdded )
				continue;
			
			// Try and add a positive ordinary with one or more bound variables
			for( ILiteral literal : positive )
			{
				Set<IVariable> literalVariables = literal.getAtom().getTuple().getVariables();
				
				boolean add = false;
				for( IVariable literalVariable : literalVariables )
				{
					if( boundVariables.contains( literalVariable ) )
					{
						add = true;
						break;
					}
				}
				if( add )
				{
					positive.remove( literal );
					newBody.add( literal );
					boundVariables.addAll( literalVariables );
					somethingAdded = true;
					break;
				}
			}
			if( somethingAdded )
				continue;

			// Try and add any positive ordinary
			if( positive.size() > 0 )
			{
				ILiteral literal = positive.get( 0 );
				Set<IVariable> literalVariables = literal.getAtom().getTuple().getVariables();
				
				positive.remove( literal );
				newBody.add( literal );
				boundVariables.addAll( literalVariables );
				somethingAdded = true;
			}
			if( somethingAdded )
				continue;

			// Try and add a built-in with enough variables bound
			for( ILiteral literal : builtin )
			{
				IBuiltinAtom builtinAtom = (IBuiltinAtom) literal.getAtom();
				
				if( literal.isPositive() )
				{
					List<IVariable> allLiteralVariables = literal.getAtom().getTuple().getAllVariables();
					Set<IVariable> literalVariables = literal.getAtom().getTuple().getVariables();
					
					Set<IVariable> unboundVariables = difference( literalVariables, boundVariables );
					
					allLiteralVariables.removeAll( boundVariables );
	
					if( allLiteralVariables.size() <= builtinAtom.maxUnknownVariables() )
					{
						builtin.remove( literal );
						newBody.add( literal );
						boundVariables.addAll( unboundVariables );
						somethingAdded = true;
						break;
					}
				}
			}
			
			if( ! somethingAdded )
			{
				// Failed to add anything, so abandon by adding all remaining sub-goals.
				// If this creates an unsafe rule then it will get picked up later during rule-safety checks.
				newBody.addAll( positive );
				newBody.addAll( builtin );
				newBody.addAll( negative );
				break;
			}
		}

		return Factory.BASIC.createRule( rule.getHead(), newBody );
	}
	
	private <E,F> Set<E> difference( Collection<E> first, Collection<F> second )
	{
		Set<E> result = new HashSet<E>();
		
		for( E e : first )
		{
			if( ! second.contains( e ) )
				result.add( e );
		}
		
		return result;
	}
}
