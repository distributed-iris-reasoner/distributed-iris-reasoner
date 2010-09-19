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
package org.deri.iris.rules.compiler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.deri.iris.Configuration;
import org.deri.iris.EvaluationException;
import org.deri.iris.RuleUnsafeException;
import org.deri.iris.api.basics.ITuple;
import org.deri.iris.api.builtins.IBuiltinAtom;
import org.deri.iris.api.terms.ITerm;
import org.deri.iris.api.terms.IVariable;
import org.deri.iris.builtins.EqualBuiltin;
import org.deri.iris.builtins.ExactEqualBuiltin;
import org.deri.iris.builtins.NotEqualBuiltin;
import org.deri.iris.builtins.NotExactEqualBuiltin;
import org.deri.iris.factory.Factory;
import org.deri.iris.storage.IRelation;
import org.deri.iris.utils.TermMatchingAndSubstitution;
import org.deri.iris.utils.equivalence.IEquivalentTerms;


/**
 * A compiled rule element representing a built-in predicate with constructed terms arguments.
 */
public class BuiltinForConstructedTermArguments extends RuleElement
{
	/**
	 * Constructor.
	 * @param inputVariables The variables from proceeding literals. Can be null if this is the first literal.
	 * @param builtinAtom The built-in atom object at this position in the rule.
	 * @param positive true, if the built-in is positive, false if it is negative.
	 * @param equivalentTerms The equivalent terms..
	 * @throws EvaluationException If constructed terms are used with a built-in or there are unbound variables.
	 */
	public BuiltinForConstructedTermArguments( List<IVariable> inputVariables, IBuiltinAtom builtinAtom, boolean positive, IEquivalentTerms equivalentTerms, Configuration configuration ) throws EvaluationException
	{
		assert inputVariables != null;
		assert builtinAtom != null;
		assert configuration != null;
		
		mBuiltinAtom = builtinAtom;
		mPositive = positive;
		mEquivalentTerms = equivalentTerms;
		mConfiguration = configuration;
		
		if( mBuiltinAtom instanceof EqualBuiltin ||
			mBuiltinAtom instanceof ExactEqualBuiltin )
			mType = TYPE.UNIFICATION;
		else if(	mBuiltinAtom instanceof NotEqualBuiltin ||
					mBuiltinAtom instanceof NotExactEqualBuiltin )
			mType = TYPE.INEQUALITY;
		else
			throw new EvaluationException(
							"Only equality, inequality and assignment built-in predicates can " +
							"have constructed terms as arguments. The problem atom is: " + builtinAtom );

		// So at this point, we know that we have a built-in that can use constructed terms as arguments.
		// Next, we have to classify the arguments as either:
		// 		a) grounded or groundable (so need indices from input relation for each variable)
		//		b) a simple variable term
		//		c) something else => can't handle, e.g. a constructed term with unbound variables
		
		ITuple builtinTuple = mBuiltinAtom.getTuple();
		
		// One more check, all these built-ins are binary
		assert builtinTuple.size() == 2;

		List<IVariable> variablesInBuiltinTuple = TermMatchingAndSubstitution.getVariables( builtinTuple, false );
		List<IVariable> unboundVariables = new ArrayList<IVariable>( variablesInBuiltinTuple );
		unboundVariables.removeAll( inputVariables );
		
		if( mType == TYPE.INEQUALITY )
		{
			if( unboundVariables.size() > 0 )
				throw new EvaluationException(
								"Not enough grounded variables for in-equality with constructed terms. The problem atom is: " + builtinAtom );
		}
		
		List<Integer> indicesOfBuiltinVariablesFromInputRelation = new ArrayList<Integer>();
		
		for( IVariable builtinVariable : variablesInBuiltinTuple )
		{
			int index = inputVariables.indexOf( builtinVariable );
			indicesOfBuiltinVariablesFromInputRelation.add( index );
		}
		
		// This is what we need to substitute for as many variable as possible.
		mIndicesOfBuiltinVariablesFromInputRelation = Utils.integerListToArray( indicesOfBuiltinVariablesFromInputRelation );


		// Now find out what variable bindings we will produce 
		mOutputVariables = new ArrayList<IVariable>( inputVariables );
		if( mType == TYPE.UNIFICATION )
		{
			List<IVariable> uniqueVariablesInBuiltinTuple = TermMatchingAndSubstitution.getVariables( builtinTuple, true );
			mUniqueUnboundVariables = new ArrayList<IVariable>( uniqueVariablesInBuiltinTuple );
			mUniqueUnboundVariables.removeAll( inputVariables );

			mOutputVariables.addAll( mUniqueUnboundVariables );
			
			if( ! mPositive )
			{
				if( mUniqueUnboundVariables.size() > 0 )
					throw new RuleUnsafeException(
									"Negated unify is not safe when some variables are unbound. The problem atom is: " + builtinAtom );
			}
		}
		else
			mUniqueUnboundVariables = new ArrayList<IVariable>();
	}

	@Override
	public IRelation process( IRelation leftRelation )
	{
		assert leftRelation != null;

		IRelation result = mConfiguration.relationFactory.createRelation();
		
		// For each input tuple
		for( int i = 0; i < leftRelation.size(); ++i )
		{
			ITuple inputTuple = leftRelation.get( i );
			
			// Substitute variable bindings from previous tuples
			ITuple builtinInputTuple =
				TermMatchingAndSubstitution.substituteVariablesInToTuple(
								mBuiltinAtom.getTuple(), inputTuple, mIndicesOfBuiltinVariablesFromInputRelation );
	
			ITerm t1 = builtinInputTuple.get( 0 );
			ITerm t2 = builtinInputTuple.get( 1 );

			if( mType == TYPE.INEQUALITY )
			{
				if( mPositive )
				{
					if( ! t1.equals( t2 ) && !mEquivalentTerms.areEquivalent(t1, t2) )
						result.add( inputTuple );
				}
				else
				{
					if( t1.equals( t2 ) || mEquivalentTerms.areEquivalent(t1, t2) )
						result.add( inputTuple );
				}
			}
			else
			{
				Map<IVariable, ITerm> variableMap = new HashMap<IVariable, ITerm>();
				
				boolean unified = TermMatchingAndSubstitution.unify( t1, t2, variableMap, mEquivalentTerms );
				
				if( mPositive )
				{
					if( unified )
					{
						List<ITerm> rightTerms = new ArrayList<ITerm>(mUniqueUnboundVariables.size());
						
						for( IVariable variable : mUniqueUnboundVariables )
						{
							rightTerms.add(variableMap.get( variable ));
						}
						
						ITuple rightTuple = Factory.BASIC.createTuple(rightTerms);
						List<ITerm> concatenatedTerms = new ArrayList<ITerm>(inputTuple.size() + rightTuple.size());

						concatenatedTerms.addAll(inputTuple);
						concatenatedTerms.addAll(rightTuple);
						
						ITuple concatenated = Factory.BASIC.createTuple(concatenatedTerms);
						result.add(concatenated);
					}
				}
				else
				{
					if( ! unified )
						result.add( inputTuple );
				}
			}
		}
		
		return result;
	}
	
	private final List<IVariable> mUniqueUnboundVariables;
	
	private static enum TYPE { UNIFICATION, INEQUALITY };
	private final TYPE mType;
	
	/** The equivalent terms. */
	private IEquivalentTerms mEquivalentTerms;
	
	/** The built-in atom at this position in the rule. */
	private final IBuiltinAtom mBuiltinAtom;
	
	/** Indicator of this literal is positive or negated. */
	private final boolean mPositive;
	
	/** The knowledge-base configuration object. */
	private final Configuration mConfiguration;
	
	/** Indices from input relation used to populate the built-in's input tuple. */
	private final int[] mIndicesOfBuiltinVariablesFromInputRelation;
}
