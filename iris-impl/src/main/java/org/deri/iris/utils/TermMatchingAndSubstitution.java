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
package org.deri.iris.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.deri.iris.api.basics.IAtom;
import org.deri.iris.api.basics.ITuple;
import org.deri.iris.api.terms.IConstructedTerm;
import org.deri.iris.api.terms.ITerm;
import org.deri.iris.api.terms.IVariable;
import org.deri.iris.factory.Factory;
import org.deri.iris.utils.equivalence.IEquivalentTerms;
import org.deri.iris.utils.equivalence.IgnoreTermEquivalence;

/**
 * A collection of utility methods for term/tuple matching and variable substitution.
 */
public class TermMatchingAndSubstitution
{
	/**
	 * Extract the variables in the same order that they are discovered during term matching.
	 * @param subGoalTuple The tuple as it appears in the sub-goal
	 * @param unique true, if only unique variables are required
	 * @return The list of variables occurring in subGoalTuple
	 */
	public static List<IVariable> getVariables( ITuple subGoalTuple, boolean unique )
	{
		List<IVariable> variables;
		if( unique )
			variables = new UniqueList<IVariable>();
		else
			variables = new ArrayList<IVariable>();
		
		for( ITerm term : subGoalTuple )
			getVariables( term, variables );
		
		return variables;
	}
	
	/**
	 * Recursive helper for constructed terms.
	 * @param constructed The constructed term to find the variables for.
	 * @param variables The map to put the variables as they are found.
	 */
	private static void getVariables( ITerm term, List<IVariable> variables )
	{
		if( term instanceof IVariable )
		{
			variables.add( (IVariable) term );
		}
		else if( term instanceof IConstructedTerm )
		{
			IConstructedTerm constructed = (IConstructedTerm) term;
			
			for( ITerm cterm : constructed.getParameters() )
			{
				getVariables( cterm, variables );
			}
		}
	}

	/**
	 * Extract variables from a term (could be constructed term).
	 * @param term The term to extract the variables from.
	 * @param unique If false, include each variable every time it appears.
	 * @return The list of variables.
	 */
	public static List<IVariable> getVariables( ITerm term, boolean unique )
	{
		List<IVariable> variables;
		if( unique )
			variables = new UniqueList<IVariable>();
		else
			variables = new ArrayList<IVariable>();
		
		getVariables( term, variables );

		return variables;
	}

	/**
	 * Match a tuple to view criteria.
	 * If a match occurs, return a tuple with values for each distinct variable in the view criteria.
	 * @param viewCriteria The tuple from a sub-goal instance.
	 * @param relation The tuple from an EDB relation.
	 * @return The tuple of values for the view's variables or null if a match did not occur.
	 */
	public static ITuple matchTuple( ITuple viewCriteria, ITuple relation )
	{
		return matchTuple(viewCriteria, relation, new IgnoreTermEquivalence());
	}
	
	/**
	 * Match a tuple to view criteria.
	 * If a match occurs, return a tuple with values for each distinct variable in the view criteria.
	 * @param viewCriteria The tuple from a sub-goal instance.
	 * @param relation The tuple from an EDB relation.
	 * @param equivalentTerms The equivalent terms.
	 * @return The tuple of values for the view's variables or null if a match did not occur.
	 */
	public static ITuple matchTuple( ITuple viewCriteria, ITuple relation, IEquivalentTerms equivalentTerms )
	{
		Map<IVariable, ITerm> variableMap = new HashMap<IVariable, ITerm>();
		List<ITerm> terms = new ArrayList<ITerm>();
		
		for( int i = 0; i < viewCriteria.size(); ++i )
		{
			ITerm bodyTerm = viewCriteria.get( i );
			ITerm relationTerm = relation.get( i );
			
			if( ! matchTermOfTuple( bodyTerm, relationTerm, equivalentTerms, variableMap, terms ) )
				return null;
		}

		return Factory.BASIC.createTuple( terms );
	}
	
	/**
	 * Helper for matching terms of a tuple.
	 * @param viewTerm The term from the rule's sub-goal tuple. 
	 * @param relationTerm The term from the relation's tuple.
	 * @param equivalentTerms The equivalent terms.
	 * @param variableMap The current map of variable-constant bindings.
	 * @param terms The bound variable values in the order in which they are found.
	 * @return true, if these terms match and are consistent with previous macthed term pairs.
	 */
	private static boolean matchTermOfTuple( ITerm viewTerm, ITerm relationTerm, IEquivalentTerms equivalentTerms, Map<IVariable, ITerm> variableMap, List<ITerm> terms ) {
		if( viewTerm instanceof IVariable )
		{
			IVariable variable = (IVariable) viewTerm;
			
			ITerm mappedGroundTerm = variableMap.get( variable );
			if( mappedGroundTerm == null )
			{
				// First time for this variable, matches whatever it finds
				variableMap.put( variable, relationTerm );
				terms.add( relationTerm );
				return true;
			}
			else if( mappedGroundTerm.equals( relationTerm ) ||
					equivalentTerms.areEquivalent(mappedGroundTerm, relationTerm))
			{
				// Mapped to something we found already
				return true;
			}
			else
			{
				// Maps to something else, so not consistent
				return false;
			}
		}
		else if( viewTerm instanceof IConstructedTerm )
		{
			IConstructedTerm bodyConstructedTerm = (IConstructedTerm) viewTerm;
			
			if( relationTerm instanceof IConstructedTerm )
			{
				IConstructedTerm relationConstructedTerm = (IConstructedTerm) relationTerm;
				
				if( ! bodyConstructedTerm.getFunctionSymbol().equals( relationConstructedTerm.getFunctionSymbol() ) )
					return false;
				
				List<ITerm> bodyTerms = bodyConstructedTerm.getParameters();
				List<ITerm> relationTerms = relationConstructedTerm.getParameters();
				
				if( bodyTerms.size() != relationTerms.size() )
					return false;
				
				for( int i = 0; i < bodyTerms.size(); ++i )
				{
					if( ! matchTermOfTuple( bodyTerms.get( i ), relationTerms.get( i ), equivalentTerms, variableMap, terms ) )
						return false;
				}
				return true;
			}
			else
				return false;
		}
		else
		{
			// The only other option is that bodyTerm is a concrete term (constant).
			return viewTerm.equals( relationTerm ) || equivalentTerms.areEquivalent(viewTerm, relationTerm);
		}
	}

	/**
	 * Indicates if bodyTerm (containing >= 0 variables) matches groundTerm and is
	 * also consistent with previous matched terms, i.e. variables map to the same ground terms. 
	 * @param bodyTerm The term from the sub-goal predicate
	 * @param relationTerm The term from the relation tuple
	 * @param variableMap The map of variable to ground term map
	 * @return true if the match succeeds, false otherwise
	 */
	public static boolean match( ITerm bodyTerm, ITerm relationTerm, Map<IVariable, ITerm> variableMap )
	{
		if( bodyTerm instanceof IVariable )
		{
			IVariable variable = (IVariable) bodyTerm;
			
			ITerm mappedGroundTerm = variableMap.get( variable );
			if( mappedGroundTerm == null )
			{
				// First time for this variable, matches whatever it finds
				variableMap.put( variable, relationTerm );
				return true;
			}
			else if( mappedGroundTerm.equals( relationTerm ) )
			{
				// Mapped to something we found already
				return true;
			}
			else
			{
				// Maps to something else, so not consistent
				return false;
			}
		}
		else if( bodyTerm instanceof IConstructedTerm )
		{
			IConstructedTerm bodyConstructedTerm = (IConstructedTerm) bodyTerm;
			
			if( relationTerm instanceof IConstructedTerm )
			{
				IConstructedTerm relationConstructedTerm = (IConstructedTerm) relationTerm;
				
				if( ! bodyConstructedTerm.getFunctionSymbol().equals( relationConstructedTerm.getFunctionSymbol() ) )
					return false;
				
				List<ITerm> bodyTerms = bodyConstructedTerm.getParameters();
				List<ITerm> relationTerms = relationConstructedTerm.getParameters();
				
				if( bodyTerms.size() != relationTerms.size() )
					return false;
				
				for( int i = 0; i < bodyTerms.size(); ++i )
				{
					if( ! match( bodyTerms.get( i ), relationTerms.get( i ), variableMap ) )
						return false;
				}
				return true;
			}
			else
				return false;
		}
		else
		{
			// The only other options is that bodyTerm is a concrete term (constant).
			return bodyTerm.equals(  relationTerm );
		}
	}

	/**
	 * Given two tuples, check if one tuple sub-sums the other one. Used to 
	 * check if it is necessary to add a atom to the memo table or if an equivalent
	 * atom is already in the memo table. Unification is not sufficient for this task,
	 * since e.g. p(?X) and p(1) are unifiable, but are likely to have different answers.
	 *  
	 * @param tup1 tuple one
	 * @param tup2 tuple two
	 *  
	 * @return true if the tuples are equivalent, false if not
	 * 
	 * @author gigi
	 */
	public static boolean subsums( ITuple tup1, ITuple tup2 ) {

		Map<IVariable, ITerm> varMap = new HashMap<IVariable, ITerm>();
		
		if (tup2.getVariables().size() > tup1.getVariables().size())
			return false; // If the second tuple has more variables than the first one, the first tuple does not subsum the second one
		
		if (unify(tup1, tup2, varMap, false)) {
			
			for (ITerm mappedTerm : varMap.values()) {
				if (mappedTerm.isGround())
					return false; // unifiable, but unification mapped a variable to a ground term
			}
			
			if (varMap.isEmpty())
				return false;
			
			return true; // unifiable, only variable to variable mappings
		}
		
		return false;
	}
	
	public static boolean subsums( IAtom atom1, IAtom atom2 ) {
		if ( atom1.equals(atom2))
			return true;
		
		if ( !atom1.getPredicate().equals(atom2.getPredicate()) )
			return false;
		
		return subsums(atom1.getTuple(), atom2.getTuple());
	}
	
	/**
	 * Given two tuples, unify to give variable bindings for all variables.
	 * @param tup1 tuple one
	 * @param tup2 tuple two
	 * @param variableMap Map where variable bindings are saved
	 *  
	 * @return true if it is unifiable, false if not
	 * 
	 * @author gigi
	 */
	public static boolean unify( ITuple tup1, ITuple tup2, Map<IVariable, ITerm> variableMap) {
		return unify(tup1, tup2, variableMap, true);
	}
	/**
	 * @see unify( ITuple tup1, ITuple tup2, Map<IVariable, ITerm> variableMap)
	 * @param bothDirections Do the unification in both directions. Actually an additional flag used by the subsums() method.
	 */
	public static boolean unify( ITuple tup1, ITuple tup2, Map<IVariable, ITerm> variableMap, boolean bothDirections ) {

		if (tup1.size() != tup2.size())
			return false; // Arity-match failed
		
		if (tup1.size() == 0)
			return true; // Trivial case: Both tuples have no terms [ () unifies with () ]
		
		boolean unifyTerms = false;
		
		for ( int i = 0; i < tup1.size(); i++ ) {
			ITerm t1 = tup1.get(i);
			ITerm t2 = tup2.get(i);
			
			if (t1.isGround() && t2.isGround() && !t1.equals(t2))
				return false;
			
			unifyTerms = unify(t1, t2, variableMap, bothDirections);
		}
		
		return unifyTerms;	
	}
	
	/**
	 * Given two terms, unify to give variable bindings for all variables.
	 * Either, both or none of the input terms need be grounded.
	 * @param t1 The first term.
	 * @param t2 The second term.
	 * @param variableMap The variable bindings.
	 * @return true, if unifiable, false otherwise
	 */
	public static boolean unify( ITerm t1, ITerm t2, Map<IVariable, ITerm> variableMap ) {
		return unify(t1, t2, variableMap, new IgnoreTermEquivalence());
	}
	
	/**
	 * Given two terms, unify to give variable bindings for all variables.
	 * Either, both or none of the input terms need be grounded.
	 * @param t1 The first term.
	 * @param t2 The second term.
	 * @param variableMap The variable bindings.
	 * @param equivalentTerms The equivalent terms.
	 * @return true, if unifiable, false otherwise
	 */
	public static boolean unify( ITerm t1, ITerm t2, Map<IVariable, ITerm> variableMap, IEquivalentTerms equivalentTerms ) {
		return unify(t1, t2, variableMap, true, equivalentTerms);
	}
	
	/**
	 * Given two terms, unify to give variable bindings for all variables.
	 * Either, both or none of the input terms need be grounded.
	 * @param t1 The first term.
	 * @param t2 The second term.
	 * @param variableMap The variable bindings.
	 * @param bothDirections Do the unification in both directions. Actually an additional flag used by the subsums() method.
	 * @return true, if unifiable, false otherwise
	 * @see unify(ITerm t1, ITerm t2, Map<IVariable, ITerm> variableMap)
	 */
	public static boolean unify( ITerm t1, ITerm t2, Map<IVariable, ITerm> variableMap, boolean bothDirections ) {
		return unify(t1, t2, variableMap, bothDirections, new IgnoreTermEquivalence());
	}
	
	/**
	 * Given two terms, unify to give variable bindings for all variables.
	 * Either, both or none of the input terms need be grounded.
	 * @param t1 The first term.
	 * @param t2 The second term.
	 * @param variableMap The variable bindings.
	 * @param bothDirections Do the unification in both directions. Actually an additional flag used by the subsums() method.
	 * @param equivalentTerms The equivalent terms.
	 * @return true, if unifiable, false otherwise
	 * @see unify(ITerm t1, ITerm t2, Map<IVariable, ITerm> variableMap)
	 */
	public static boolean unify( ITerm t1, ITerm t2, Map<IVariable, ITerm> variableMap, boolean bothDirections, IEquivalentTerms equivalentTerms )
	{
		if( t1.isGround() && t2.isGround() )
			return t1.equals( t2 ) || equivalentTerms.areEquivalent(t1, t2);
		
		if ( t1 instanceof IVariable && t2 instanceof IVariable )
		{
			if (!t1.equals(t2))
				variableMap.put((IVariable)t1, t2);
			return true;
		}
		
		if( t1 instanceof IVariable )
		{
			return unifyCheckBinding( (IVariable) t1, t2, variableMap, equivalentTerms );
		}
		
		if( t2 instanceof IVariable && bothDirections )
		{
			return unifyCheckBinding( (IVariable) t2, t1, variableMap, equivalentTerms );
		}
		
		// Here, we know that neither t1 nor t2 is a variable
		// t1 and t2 are not both ground
		// Therefore at least one is a constructed term
		
		if( t1 instanceof IConstructedTerm && t2 instanceof IConstructedTerm )
		{
			IConstructedTerm c1 = (IConstructedTerm) t1;
			IConstructedTerm c2 = (IConstructedTerm) t2;
			
			if( ! c1.getFunctionSymbol().equals( c2.getFunctionSymbol() ) )
				return false;
			
			List<ITerm> c1terms = c1.getValue();
			List<ITerm> c2terms = c2.getValue();
			
			if( c1terms.size() != c2terms.size() )
				return false;
			
			for( int i = 0; i < c1terms.size(); ++i )
			{
				ITerm c1term = c1terms.get( i );
				ITerm c2term = c2terms.get( i );
				
				boolean termUnifiy = unify( c1term, c2term, variableMap, bothDirections, equivalentTerms );
				if( ! termUnifiy )
					return false;
			}
			return true;
		}
		else
			return false;
	}
	
	private static boolean unifyCheckBinding( IVariable variable, ITerm term, Map<IVariable, ITerm> variableMap, IEquivalentTerms equivalentTerms )
	{
		// First version does not skip and retry
		if( ! term.isGround() && ! (term instanceof IConstructedTerm) ) // added by gigi for top-down evaluation: && ! (term instanceof IConstructedTerm), so e.g. X => f(X) is possible
			return false;
		
		ITerm existingMapping = variableMap.get( variable );
		if( existingMapping == null )
		{
			// Add a new mapping for (variable) t1 => t2
			variableMap.put( variable, term );
		}
		else
		{
			// check that the existing mapping is the same
			if( ! existingMapping.equals( term ) && 
					!equivalentTerms.areEquivalent(existingMapping, term) )
				return false;
		}
		
		return true;
	}

	/**
	 * Substitute the variable bindings in to a tuple to ground it.
	 * @param tuple The tuple containing variables to ground.
	 * @param variableMap The variable bindings to use.
	 * @return The grounded tuple.
	 */
	public static ITuple substituteVariablesInToTuple( ITuple tuple, Map<IVariable, ITerm> variableMap )
	{
		List<ITerm> substitutedTerms = new ArrayList<ITerm>();
		
		for( ITerm headTerm : tuple )
		{
			ITerm substitutedTerm = substituteVariablesInToTerm( headTerm, variableMap );
			substitutedTerms.add( substitutedTerm );
		}
		
		return Factory.BASIC.createTuple( substitutedTerms );
	}

	/**
	 * Substitute the variable bindings in to a term to ground it.
	 * @param term The term to ground.
	 * @param variableMap The variable bindings to use.
	 * @return The grounded term.
	 */
	public static ITerm substituteVariablesInToTerm( ITerm term, Map<IVariable, ITerm> variableMap )
	{
		// Recursion terminator. Probably inefficient as tree is traversed anyway.
		if( term.isGround() )
			return term;
		
		// Found a variable
		if( term instanceof IVariable )
		{
			IVariable variable = (IVariable) term;
			ITerm groundTerm = variableMap.get( variable );
			
			if ( groundTerm == null)
				return variable;
			
			return groundTerm;
		}
		
		// Else we have a constructed term with variables somewhere
		assert term instanceof IConstructedTerm;
		
		IConstructedTerm constructedTerm = (IConstructedTerm) term;
		List<ITerm> substitutedChildTerms = new ArrayList<ITerm>();
		
		for( ITerm childTerm : constructedTerm.getParameters() )
			substitutedChildTerms.add( substituteVariablesInToTerm( childTerm, variableMap ) );
		
		return Factory.TERM.createConstruct( constructedTerm.getFunctionSymbol(), substitutedChildTerms );
	}

	/**
	 * Silly helper to create a mutable integer that can be passed by reference.
	 */
	private static class MutableInteger
	{
		int mValue;
	}
	
	/**
	 * Substitute variable values in to a tuple to ground it
	 * using a list of terms with indices instead of a variable-term map.
	 * @param tuple The tuple containing variables to ground.
	 * @param variableValues The variable values to be substituted.
	 * @param indices The indices in to variableValues for each occurrence of a variable
	 * in the tuple IN THE ORDER IN WHICH THEY ARE FOUND. An index value that is less than zero
	 * indicates that there is no binding for this variable.
	 * @return The grounded tuple.
	 */
	public static ITuple substituteVariablesInToTuple( ITuple tuple, List<ITerm> variableValues, int[] indices )
	{
		List<ITerm> substitutedTerms = new ArrayList<ITerm>();
		MutableInteger bindingIndex = new MutableInteger();
		
		for( ITerm headTerm : tuple )
		{
			ITerm substitutedTerm = substituteVariablesInToTupleTerm( headTerm, variableValues, indices, bindingIndex );
			substitutedTerms.add( substitutedTerm );
		}
		
		return Factory.BASIC.createTuple( substitutedTerms );
	}

	/**
	 * Helper.
	 * @param term The term to substitute in to.
	 * @param variableValues The variable value list.
	 * @param indices The indices in to variableValues for each occurence of a variable.
	 * @param bindingIndex Indicates the next index to use.
	 * @return The grounded term.
	 */
	private static ITerm substituteVariablesInToTupleTerm( ITerm term, List<ITerm> variableValues, int[] indices, MutableInteger bindingIndex )
	{
		// Recursion terminator. Probably inefficient as tree is traversed anyway.
		if( term.isGround() )
			return term;
		
		// Found a variable
		if( term instanceof IVariable )
		{
			int variableIndex = indices[ bindingIndex.mValue++ ];
			
			// Check if we have a binding for this variable
			if( variableIndex >= 0 )
			{
				ITerm groundTerm = variableValues.get( variableIndex );
				
				assert groundTerm != null;
				
				return groundTerm;
			}
			else
				return term;	// Return the unsubstituted variable
		}
		
		// Else we have a constructed term with variables somewhere
		assert term instanceof IConstructedTerm;
		
		IConstructedTerm constructedTerm = (IConstructedTerm) term;
		List<ITerm> substitutedChildTerms = new ArrayList<ITerm>();
		
		for( ITerm childTerm : constructedTerm.getParameters() )
			substitutedChildTerms.add( substituteVariablesInToTupleTerm( childTerm, variableValues, indices, bindingIndex ) );
		
		return Factory.TERM.createConstruct( constructedTerm.getFunctionSymbol(), substitutedChildTerms );
	}
	
	/**
	 * Substitute variable values in to a term to ground it
	 * using variable bindings as a list of terms with indices instead of a variable-term map.
	 * @param term The term to ground.
	 * @param variableValues A list of variable values
	 * @param indices The indices in to variableValues for each occurrence of a variable
	 * in the term IN THE ORDER IN WHICH THEY ARE FOUND. An index value that is less than zero
	 * indicates that there is no binding for this variable.
	 * @return The grounded term.
	 */
	public static ITerm substituteVariablesInToTerm( ITerm term, List<ITerm> variableValues, int[] indices )
	{
		return substituteVariablesInToTupleTerm( term, variableValues, indices, new MutableInteger() );
	}
}
