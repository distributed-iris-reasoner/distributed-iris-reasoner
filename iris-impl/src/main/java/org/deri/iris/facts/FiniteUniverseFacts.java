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
package org.deri.iris.facts;

import java.util.Collection;
import java.util.Set;

import org.deri.iris.api.basics.ILiteral;
import org.deri.iris.api.basics.IPredicate;
import org.deri.iris.api.basics.IRule;
import org.deri.iris.api.basics.ITuple;
import org.deri.iris.api.terms.IConstructedTerm;
import org.deri.iris.api.terms.ITerm;
import org.deri.iris.api.terms.IVariable;
import org.deri.iris.factory.Factory;
import org.deri.iris.storage.IRelation;
import org.deri.iris.storage.simple.SimpleRelationFactory;

/**
 * Definition: Unsafe negation
 * A rule has a variable in a negated sub-goal that also occurs in the head, but nowhere else.
 * 
 * Definition: Unsafe
 * A rule has a variable that does not occur in a positive ordinary literal.
 *
 * Create a $UNIVERSE$(?X) relation.
 * 
 * To handle unsafe negation (Gelder, Well-founded semantics, p.24):
 * 	1.	For every rule, extract every occurrence of a non-ground-term, e.g. ?X or f(?Y)
 * 	2.	Substitute a distinct ground term (not found anywhere else) for the variable(s), e.g. '$X', f('$Y')
 * 	3.	Add this term to $UNIVERSE$
 * 
 * To handle all unsafe rules (with free variables in head or built-ins):
 * 	5.	Add all concrete terms that occur anywhere in the program (facts and rules) to $UNIVERSE$
 * 
 * For both:
 * 	6.	Add $UNIVERSE$(?variable) sub-goals for each free variable in each rule
 */
public class FiniteUniverseFacts implements IFacts
{
	/**
	 * Constructor.
	 * Extract all ground terms from starting facts and rules.
	 * @param facts The starting facts of the knowledge-base.
	 * @param rules The rules of the knowledge-base.
	 */
	public FiniteUniverseFacts( IFacts facts, Collection<IRule> rules )
	{
		if( facts == null )
			throw new IllegalArgumentException( "Argument 'facts' must not be null.");
		
		if( rules == null )
			throw new IllegalArgumentException( "Argument 'rules' must not be null.");
		
		mFacts = facts;
		
//		mUniverse = mFacts.get( UNIVERSE );
		mUniverse = new SimpleRelationFactory().createRelation();
		
		// Extract all possible ground values from starting facts and rules.
		extractGroundTerms( rules );
		
		Set<IPredicate> startPredicates = mFacts.getPredicates();
		
		for( IPredicate predicate : startPredicates )
		{
			IRelation relation = mFacts.get( predicate );
			addToUniverse( relation );
		}
	}
	
	/**
	 * Extract ground terms from rules and create unique values for each variable found.
	 * @param rules The rules of the knowledge-base.
	 */
	private void extractGroundTerms( Collection<IRule> rules )
	{
		assert rules != null;
		
		for( IRule rule : rules )
		{
			for( ILiteral literal : rule.getHead() )
			{
				for( ITerm term : literal.getAtom().getTuple() )
				{
					extractGroundTermsFromTerm( term );
				}
			}

			for( ILiteral literal : rule.getBody() )
			{
				for( ITerm term : literal.getAtom().getTuple() )
				{
					extractGroundTermsFromTerm( term );
				}
			}
		}
	}

	/**
	 * Extract ground terms from a term (the term could be a constructed term).
	 * @param term
	 */
	private void extractGroundTermsFromTerm( ITerm term )
	{
		assert term != null;
		
		if( term instanceof IVariable )
		{
			// Need one unique value per variable.

			IVariable variable = (IVariable) term;
			
			addToUniverse( Factory.TERM.createString( variable.getValue() + UNIQUE_VARIABLE_SUFFIX ) );
			return;
		}
		
		if( term.isGround() )
		{
			addToUniverse( term );
			return;
		}

		if( term instanceof IConstructedTerm )
		{
			IConstructedTerm constructed = (IConstructedTerm) term;
			for( ITerm param : constructed.getValue() )
				extractGroundTermsFromTerm( param );
		}
	}

	/**
	 * An adaptor that adds all ground terms to the UNIVERSE relation.
	 */
	private class UniverseAddingRelationAdaptor implements IRelation
	{
		/**
		 * Constructor.
		 * @param child The wrapped underlying facts object.
		 */
		public UniverseAddingRelationAdaptor( IRelation child )
		{
			assert child != null;
			mChild = child;
		}
		
		public boolean add( ITuple tuple )
        {
			assert tuple != null;
			
			boolean result = mChild.add( tuple );

			// If this is a tuple not seen before then it might have new terms in it.
			if( ! result )
				addToUniverse( tuple );

			return result;
        }

		public boolean addAll( IRelation relation )
        {
			assert relation != null;

			boolean added = false;
			
			for( int t = 0; t < relation.size(); ++t )
			{
				ITuple tuple = relation.get( t );
				if( add( tuple ) )
					added = true;
			}

	        return added;
        }

		public ITuple get( int index )
        {
			assert mChild != null;

			return mChild.get( index );
        }

		public int size()
        {
			assert mChild != null;
			
	        return mChild.size();
        }
		
		public boolean contains( ITuple tuple )
        {
			assert tuple != null;
			assert mChild != null;
			
	        return mChild.contains( tuple );
        }

		private final IRelation mChild;
	}
	
	public IRelation get( IPredicate predicate )
	{
		if( predicate == null )
			throw new IllegalArgumentException( "Argument 'predicate' must not be null." );
		
		assert mUniverse != null;
		assert mFacts != null;
			
		if( predicate.equals( UNIVERSE ) )
			return mUniverse;
		
		return new UniverseAddingRelationAdaptor( mFacts.get( predicate ) );
	}

	public Set<IPredicate> getPredicates()
	{
		assert mFacts != null;
		
		return mFacts.getPredicates();
	}
	
	private void addToUniverse( IRelation relation )
	{
		assert relation != null;

		for( int t = 0; t < relation.size(); ++t )
			addToUniverse( relation.get( t ) );
	}
	
	/**
	 * Add all the terms of a tuple to the universe relation.
	 * @param tuple The tuple whose terms are to be added.
	 */
	private void addToUniverse( ITuple tuple )
	{
		assert tuple != null;

		for( ITerm term : tuple )
			addToUniverse( term );
	}

	/**
	 * Add a term (recursively in the case of constructed terms) to the universe relation.
	 * @param term The term to add.
	 */
	private void addToUniverse( ITerm term )
	{
		assert !( term instanceof IVariable );
		assert mUniverse != null;
		
		if( term instanceof IConstructedTerm )
		{
			IConstructedTerm constructed = (IConstructedTerm) term;
			for( ITerm param : constructed.getValue() )
				addToUniverse( param );
		}

		// NOTE
		// If term = f(g(h(1))) then 1, h(1), g(h(1)) and f(g(h(1))) will get added to the universe.
		mUniverse.add( Factory.BASIC.createTuple( term ) );
	}

	@Override
    public String toString()
    {
		assert mFacts != null;
	    return mFacts.toString();
    }

	/** The underlying facts object. */
	private final IFacts mFacts;
	
	/** The predicate name that identifies the universe relation. */
	private final IRelation mUniverse;
	
	/** The suffix to append to variables in rules in order to give them a 'unique' value. */
	private static final String UNIQUE_VARIABLE_SUFFIX = "_$UNIQUE$";
	
	/** The universe predicate. */
	public static final IPredicate UNIVERSE = Factory.BASIC.createPredicate( "$UNIVERSE$", 1 );
}
