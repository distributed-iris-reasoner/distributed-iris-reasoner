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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.deri.iris.Configuration;
import org.deri.iris.EvaluationException;
import org.deri.iris.api.basics.ITuple;
import org.deri.iris.api.builtins.IBuiltinAtom;
import org.deri.iris.api.terms.ITerm;
import org.deri.iris.api.terms.IVariable;
import org.deri.iris.factory.Factory;
import org.deri.iris.storage.IRelation;
import org.deri.iris.utils.equivalence.IEquivalentTerms;

/**
 * A compiled rule element representing a built-in predicate.
 */
public class Builtin extends RuleElement
{
	/**
	 * Constructor.
	 * @param inputVariables The variables from proceeding literals. Can be null if this is the first literal.
	 * @param builtinAtom The built-in atom object at this position in the rule.
	 * @param positive true, if the built-in is positive, false if it is negative.
	 * @param equivalentTerms The equivalent terms.
	 * @throws EvaluationException If constructed terms are used with a built-in or there are unbound variables.
	 */
	public Builtin( List<IVariable> inputVariables, IBuiltinAtom builtinAtom, boolean positive, IEquivalentTerms equivalentTerms, Configuration configuration ) throws EvaluationException
	{
		assert inputVariables != null;
		assert builtinAtom != null;
		assert configuration != null;
		
		mBuiltinAtom = builtinAtom;
		mPositive = positive;
		mEquivalentTerms = equivalentTerms;
		mConfiguration = configuration;
		
		// TODO Properly calculate output variables and indices for negative literals
		
		ITuple builtinTuple = mBuiltinAtom.getTuple();
		
		// Get variables in built-in literal
		List<IVariable> unboundBuiltInVariables = new ArrayList<IVariable>();
		
		// The indexes of terms from inputRelation to use to populate the tuple for the built-in predicate
		mIndicesFromInputRelationToMakeInputTuple = new int[ builtinTuple.size() ];
		
		List<Integer> indicesFromBuiltOutputTupleToCopyToOutputRelation = new ArrayList<Integer>();
		int indexOfBuiltinOutputTuple = 0;
		
		for( int t = 0; t < builtinTuple.size(); ++t )
		{
			// Assume not in input relation
			int indexFromInput = -1;

			ITerm term = builtinTuple.get( t );
			
//			assert ! (term instanceof IConstructedTerm);
			
			if( term instanceof IVariable )
			{
				IVariable builtinVariable = (IVariable) term;
				
				indexFromInput = inputVariables.indexOf( builtinVariable );
				
				// Is this variable unbound?
				if( indexFromInput == -1 )
				{
					unboundBuiltInVariables.add( builtinVariable );
					indicesFromBuiltOutputTupleToCopyToOutputRelation.add( indexOfBuiltinOutputTuple++ );
				}
			}

			mIndicesFromInputRelationToMakeInputTuple[ t ] = indexFromInput;
		}
		
		Set<IVariable> uniqueUnboundBuiltInVariables = new HashSet<IVariable>( unboundBuiltInVariables );
		
		if( uniqueUnboundBuiltInVariables.size() > mBuiltinAtom.maxUnknownVariables() )
			throw new EvaluationException( "Too many unbound variables for built-in '" + mBuiltinAtom + "' unbound variables: " + unboundBuiltInVariables );
		
		// The indexes of terms in the built-in output tuple to copy to the output relation
		mIndicesFromBuiltInOutputTupleToCopyToOutputRelation = Utils.integerListToArray( indicesFromBuiltOutputTupleToCopyToOutputRelation );

		// Make the output variable list
		if( unboundBuiltInVariables.size() == 0 )
		{
			mOutputVariables = inputVariables;
		}
		else
		{
			mOutputVariables = new ArrayList<IVariable>();
			
			for( IVariable variable : inputVariables )
				mOutputVariables.add( variable );
			
			for( IVariable variable : unboundBuiltInVariables )
				mOutputVariables.add( variable );
		}
	}

	@Override
	public IRelation process( IRelation leftRelation ) throws EvaluationException
	{
		assert leftRelation != null;
		
		IRelation result = mConfiguration.relationFactory.createRelation();
		
		for( int i = 0; i < leftRelation.size(); ++i )
		{
			ITuple input = leftRelation.get( i );
			
			// Make the tuple for input to the built-in predicate
			ITerm[] terms = new ITerm[ mIndicesFromInputRelationToMakeInputTuple.length ];
			
			for( int t = 0; t < mIndicesFromInputRelationToMakeInputTuple.length; ++t )
			{
				int index = mIndicesFromInputRelationToMakeInputTuple[ t ];
				terms[ t ] = index == -1 ? mBuiltinAtom.getTuple().get( t ) : input.get( index );
			}
			
			ITuple builtinInputTuple = Factory.BASIC.createTuple( terms );

			// FIXME Check if this is still necessary.
			List<ITuple> combinationsForBuiltin = Utils.createAllCombinations(
					builtinInputTuple, mEquivalentTerms);
			
			for (ITuple combinationForBuiltin : combinationsForBuiltin) {
				ITuple builtinOutputTuple = mBuiltinAtom.evaluate( combinationForBuiltin );
				
				if( mPositive )
				{
					if( builtinOutputTuple != null ) {
						ITuple concatenated = makeResultTuple(input, builtinOutputTuple);
						result.add(concatenated);
					}
				}
				else
				{
					if( builtinOutputTuple == null ) {
						result.add( input );
					}
				}
			}
		}
			
		return result;
	}

	/**
	 * Transform the input tuple (from previous rule elements) and the tuple produced by the
	 * built-in atom in to a tuple to pass on to the next rule element.
	 * @param inputTuple The tuple produced b previous literals.
	 * @param builtinOutputTuple The output of the built-in atom.
	 * @return The tuple to pass on to the next rule element.
	 */
	protected ITuple makeResultTuple( ITuple inputTuple, ITuple builtinOutputTuple )
	{
		assert builtinOutputTuple != null;
		
		if( builtinOutputTuple.size() == 0 )
			return inputTuple == null ? Factory.BASIC.createTuple() : inputTuple;
		
		ITerm[] terms = new ITerm[ ( inputTuple == null ? 0 : inputTuple.size() ) + mIndicesFromBuiltInOutputTupleToCopyToOutputRelation.length ];
		
		int index = 0;
		if( inputTuple != null )
			for( ITerm term : inputTuple )
				terms[ index++ ] = term;
		
		for( int i : mIndicesFromBuiltInOutputTupleToCopyToOutputRelation )
			terms[ index++ ] = builtinOutputTuple.get( i );

		return Factory.BASIC.createTuple( terms );
	}

	/** The equivalent terms. */
	private IEquivalentTerms mEquivalentTerms;
	
	/** The built-in atom at this positio in the rule. */
	private final IBuiltinAtom mBuiltinAtom;
	
	/** Indicator of this literal is positive or negated. */
	private final boolean mPositive;
	
	/** Indices from the input relation to pick term values from. */
	private final int[] mIndicesFromInputRelationToMakeInputTuple;
	
	/** Indices from the built-in atom to put in to the rule element's output tuple. */
	private final int[] mIndicesFromBuiltInOutputTupleToCopyToOutputRelation;
	
	/** The knowledge-base-wide configuration object. */
	private final Configuration mConfiguration;
}
