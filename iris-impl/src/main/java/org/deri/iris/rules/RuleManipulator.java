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
package org.deri.iris.rules;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.deri.iris.api.basics.IAtom;
import org.deri.iris.api.basics.ILiteral;
import org.deri.iris.api.basics.IPredicate;
import org.deri.iris.api.basics.IQuery;
import org.deri.iris.api.basics.IRule;
import org.deri.iris.api.basics.ITuple;
import org.deri.iris.api.builtins.IBuiltinAtom;
import org.deri.iris.api.terms.IConstructedTerm;
import org.deri.iris.api.terms.ITerm;
import org.deri.iris.api.terms.IVariable;
import org.deri.iris.basics.Tuple;
import org.deri.iris.builtins.EqualBuiltin;
import org.deri.iris.builtins.ExactEqualBuiltin;
import org.deri.iris.builtins.NotEqualBuiltin;
import org.deri.iris.builtins.NotExactEqualBuiltin;
import org.deri.iris.factory.Factory;

/**
 * A collection of useful rule manipulation operations.
 */
public class RuleManipulator
{
	/**
	 * Append an equality term to the rule body.
	 * @param rule The rule to process.
	 * @param term1 The first argument to the equality.
	 * @param term2 The second argument to the equality.
	 * @return The new rule.
	 */
	public IRule addEquality( IRule rule, ITerm term1, ITerm term2 )
	{
//		ILiteral literal = Factory.BASIC.createLiteral( true, new EqualBuiltin( term1, term2 ) );
		ILiteral literal = Factory.BASIC.createLiteral( true, new ExactEqualBuiltin( term1, term2 ) );
		
		return addBodyLiteral( rule, literal );
	}
	
	/**
	 * Append an inequality term to the rule body.
	 * @param rule The rule to process.
	 * @param term1 The first argument to the inequality.
	 * @param term2 The second argument to the inequality.
	 * @return The new rule.
	 */
	public IRule addInequality( IRule rule, ITerm term1, ITerm term2 )
	{
//		ILiteral literal = Factory.BASIC.createLiteral( true, new UnEqualBuiltin( term1, term2 ) );
		ILiteral literal = Factory.BASIC.createLiteral( true, new NotExactEqualBuiltin( term1, term2 ) );
		
		return addBodyLiteral( rule, literal );
	}
	
	/**
	 * Add a literal to a rule body.
	 * @param rule The rule to process.
	 * @param literal The literal to add.
	 * @return The new rule.
	 */
	public IRule addBodyLiteral( IRule rule, ILiteral literal )
	{
		List<ILiteral> body = new ArrayList<ILiteral>( rule.getBody() );
		
		body.add( literal );
		
		return Factory.BASIC.createRule( rule.getHead(), body );
	}

	/**
	 * Creates a new list with all duplicates removed.
	 * @param list the list from where to take the elements
	 * @return the newly created list with all duplicates removed
	 */
	private static <Type> List<Type> removeDuplicates(final List<Type> list) {
		assert list != null: "The list must not be null";

		final List<Type> result = new ArrayList<Type>(list.size());
		final Set<Type> uniqueSet = new HashSet<Type>(list.size());

		for (final Type item : list) {
			if (uniqueSet.add(item)) {
				result.add(item);
			}
		}
		return result;
	}

	/**
	 * Creates a new query with all duplicates removed.
	 * @param query the query from where to take the literals
	 * @return the newly created query with all duplicates removed
	 */
	public static IQuery removeDuplicateLiterals(final IQuery query) {
		if (query == null) {
			throw new IllegalArgumentException("The query must not be null");
		}

		return Factory.BASIC.createQuery(removeDuplicates(query.getLiterals()));
	}
	
	/**
	 * Traverse the body literals and remove any duplicates.
	 * @param rule The rule to examine.
	 * @return The modified rule.
	 */
	public IRule removeDuplicateLiterals( IRule rule )
	{
		return Factory.BASIC.createRule(rule.getHead(), removeDuplicates(rule.getBody()));
	}
	
	/**
	 * Iterate the rule looking for positive variable=variable equality built-ins.
	 * For each one found, traverse the whole rule and replace one variable with the other. 
	 * @param rule The rule to process.
	 * @return The new rule.
	 */
	public IRule replaceVariablesWithVariables( IRule rule )
	{
		boolean changed;
		
		do
		{
			changed = false;
			
			for( ILiteral literal : rule.getBody() )
			{
				if( literal.isPositive() )
				{
					if( literal.getAtom() instanceof EqualBuiltin )
					{
						Tuple tuple = (Tuple) literal.getAtom().getTuple();
						
						IVariable v1 = null;
						IVariable v2 = null;
						
						ITerm term = tuple.get( 0 );
						if( term instanceof IVariable )
	                        v1 = (IVariable) term;
						
						term = tuple.get( 1 );
						if( term instanceof IVariable )
	                        v2 = (IVariable) term;
						
						// If a positive equality between a variable and variable then...
						if( v1 != null && v2 != null )
						{
							if( ! v1.equals( v2 ) )
							{
								// ... re-write the rule replacing the variable with the other variable
								IRule rule2 = replace( rule, true, v1, v2 );
								
								if( ! rule2.equals( rule ) )
								{
									rule = rule2;
									changed = true;
									
									// Start again at the beginning
									break;
								}
							}
						}
					}
				}
			}
			
		} while( changed );
		
		return rule;
	}
	
	/**
	 * Iterate the rule looking for positive variable==constant exact equalities.
	 * For each one found, replace the variable with the constant in the other rule literals.
	 * @param rule The rule to process
	 * @param strict True, do replacements only for exact equalities.
	 * False, use any equality.
	 * @return A new rule
	 */
	public IRule replaceVariablesWithConstants( IRule rule, boolean strict )
	{
		boolean changed;
		
		do
		{
			changed = false;
			
			for( ILiteral literal : rule.getBody() )
			{
				boolean positive = literal.isPositive();
				
				boolean equality = literal.getAtom() instanceof EqualBuiltin;
				boolean in_equality = literal.getAtom() instanceof NotEqualBuiltin;
				boolean is = literal.getAtom() instanceof ExactEqualBuiltin;
				boolean is_not = literal.getAtom() instanceof NotExactEqualBuiltin;
				
				boolean canReplace = (is && positive) || (is_not && ! positive);
				
				if( ! strict && ! canReplace )
				{
					canReplace = (equality && positive) || (in_equality && ! positive);
				}
				
				if( canReplace )
				{
					Tuple tuple = (Tuple) literal.getAtom().getTuple();
					
					assert tuple.size() == 2;
					
					IVariable variable = null;
					ITerm constant = null;
					
					for( ITerm term : tuple )
					{
						if( term instanceof IVariable )
	                        variable = (IVariable) term;
						if( term.isGround() )
							constant = term;
					}
					
					// If a equality between a variable and constant then...
					if( variable != null && constant != null )
					{
						// ... re-write the rule replacing the variable with the constant
						IRule rule2 = replace( rule, true, variable, constant );
						
						if( ! rule2.equals( rule ) )
						{
							rule = rule2;
							changed = true;
							
							// Start again at the beginning
							break;
						}
					}
				}
			}
			
		} while( changed );
		
		return rule;
	}
	
	/**
	 * Remove positive equalities that equate something to itself.
	 * @param rule The rule to process.
	 * @return The new rule.
	 */
	public IRule removeUnnecessaryEqualityBuiltins( IRule rule )
	{
		List<ILiteral> body = new ArrayList<ILiteral>();
		
		for( ILiteral literal : rule.getBody() )
		{
			boolean add = true;
			boolean positive = literal.isPositive();
			boolean equality = literal.getAtom() instanceof EqualBuiltin || literal.getAtom() instanceof ExactEqualBuiltin;
			boolean in_equality = literal.getAtom() instanceof NotEqualBuiltin || literal.getAtom() instanceof NotExactEqualBuiltin;

			boolean canReplace = (positive && equality) || (!positive && in_equality);
			
			if( canReplace )
			{
				Tuple tuple = (Tuple) literal.getAtom().getTuple();
				
				assert tuple.size() == 2;
				
				ITerm t1 = tuple.get( 0 );
				ITerm t2 = tuple.get( 1 );
				
				if( t1.equals( t2 ) )
					add = false;	// Don't add it!
			}
			
			if( add )
				body.add( literal );
		}
		
		return Factory.BASIC.createRule( rule.getHead(), body );
	}

	/**
	 * Replace all occurrences of 'remove' with 'replaceWith'.
	 * @param rule The rule to modify
	 * @param processHead true, to modifix the head as well as the body
	 * @param remove The term to remove
	 * @param replaceWith The term to replace with
	 * @return The new rule
	 */
	public IRule replace( IRule rule, boolean processHead, ITerm remove, ITerm replaceWith )
	{
		// Reconstruct a brand new rule.
		List<ILiteral> headLiterals = new ArrayList<ILiteral>();
		List<ILiteral> bodyLiterals = new ArrayList<ILiteral>();
		
		if( processHead )
			for( ILiteral literal : rule.getHead() )
				headLiterals.add( replace( literal, remove, replaceWith ) );
		
		for( ILiteral literal : rule.getBody() )
			bodyLiterals.add( replace( literal, remove, replaceWith ) );
		
		return Factory.BASIC.createRule( headLiterals, bodyLiterals );
	}
	
	private ILiteral replace( ILiteral literal, ITerm remove, ITerm replacewith )
	{
		boolean positive = literal.isPositive();
		IAtom atom = replace( literal.getAtom(), remove, replacewith );
		
		return Factory.BASIC.createLiteral( positive, atom );
	}
	
	// public visibility needed for SLDNF resolution
	public IAtom replace( IAtom atom, ITerm remove, ITerm replaceWith )
	{
		if( atom instanceof IBuiltinAtom )
			return replace( (IBuiltinAtom) atom, remove, replaceWith );
		
		IPredicate predicate = atom.getPredicate();
		
		ITuple tuple = replace( atom.getTuple(), remove, replaceWith );
		
		return Factory.BASIC.createAtom( predicate, tuple );
	}

	private ITuple replace( ITuple tuple, ITerm remove, ITerm replaceWith )
	{
		return Factory.BASIC.createTuple( replaceTerms( tuple, remove, replaceWith ) );
	}
	
	private ITerm[] replaceTerms( ITuple tuple, ITerm remove, ITerm replaceWith )
	{
		ITerm[] newTerms = new ITerm[ tuple.size() ];
		
		for( int t = 0; t < tuple.size(); ++t )
		{
			ITerm oldTerm = tuple.get( t );
			if( oldTerm.equals( remove ) )
				newTerms[ t ] = replaceWith;
			else if( oldTerm instanceof IConstructedTerm )
				newTerms[ t ] = replace( (IConstructedTerm) oldTerm, remove, replaceWith );
			else
				newTerms[ t ] = oldTerm;
		}
		
		return newTerms;
	}
	
	private IConstructedTerm replace( IConstructedTerm constructed, ITerm remove, ITerm replaceWith )
	{		
		String functionSymbol = constructed.getFunctionSymbol();
		
		List<ITerm> newTerms = new ArrayList<ITerm>();
		
		for( ITerm oldTerm : constructed.getParameters() )
		{
			if( oldTerm.equals( remove ) )
				newTerms.add( replaceWith );
			else if( oldTerm instanceof IConstructedTerm )
				newTerms.add( replace( (IConstructedTerm) oldTerm, remove, replaceWith ) );
			else
				newTerms.add( oldTerm );
		}
		
		return Factory.TERM.createConstruct( functionSymbol, newTerms );
	}

	/**
	 * Re-write an atom by replacing terms. Only bother with
	 * @param atom The atom to modify.
	 * @param remove The term to remove (if present).
	 * @param replaceWith The term to replace with.
	 * @return The new atom.
	 */
	private IAtom replace( IBuiltinAtom atom, ITerm remove, ITerm replaceWith )
	{
		ITerm[] newTerms = replaceTerms( atom.getTuple(), remove, replaceWith );

		try
		{
			Constructor<IBuiltinAtom> constructor = (Constructor<IBuiltinAtom>) atom.getClass().getConstructor( newTerms.getClass() );
			return constructor.newInstance( (Object) newTerms );
		}
		catch( Exception e )
		{
			// should never actually get here unless soemone's been fiddling about.
			throw new RuntimeException( "Unknown built in class type in RuleManipulator: " + atom.getClass().getName() );
		}
	}
}
