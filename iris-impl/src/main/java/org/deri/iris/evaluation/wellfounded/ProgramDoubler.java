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
package org.deri.iris.evaluation.wellfounded;

import java.util.ArrayList;
import java.util.List;

import org.deri.iris.Configuration;
import org.deri.iris.api.basics.ILiteral;
import org.deri.iris.api.basics.IPredicate;
import org.deri.iris.api.basics.IRule;
import org.deri.iris.api.basics.ITuple;
import org.deri.iris.factory.Factory;
import org.deri.iris.facts.Facts;
import org.deri.iris.facts.IFacts;
import org.deri.iris.facts.OriginalFactsPreservingFacts;
import org.deri.iris.storage.IRelationFactory;

/**
 * Program doubler for well-founded semantics evaluation strategy.
 * This class generates the rule-sets and facts for the alternating fixed point algorithm,
 * which is used to compute a well-founded model.
 * The rules and facts for the original logic program are used to instantiate the ProgramDoubler.
 * After which, the extractXXX and getXXX methods are used to retrieve the program fragments
 * used during the alternating fixed point loop.
 */
public class ProgramDoubler
{
	/**
	 * The suffix used to name the 'primed' predicates, i.e. those predicate that
	 * represent definitely false facts of their unprimed counterparts.
	 */
	public static final String NEGATED_PREDICATE_SUFFIX = "_$PRIMED$";
	
	/**
	 * Constructor.
	 * @param rules The rules from the original program.
	 * @param facts The facts from the original program.
	 * @param configuration The configuration object for the knowledge base.
	 */
	public ProgramDoubler( List<IRule> rules, IFacts facts, Configuration configuration )
	{
		mOriginalRules = rules;
		mOriginalFacts = facts;
		mConfiguration = configuration;
		
		calculateStartingRuleBase();
		calculateNegativeRuleBase();
		calculatePositiveRuleBase();
	}
	
	/**
	 * Extract all the facts associated with unprimed predicates from the facts provided.
	 * @param mixed The input collection of facts tha can contain a mixture of primed and
	 * unprimed predicates.
	 * @return The facts associated with unprimed predicates only.
	 */
	public IFacts extractPositiveFacts( IFacts mixed )
	{
		IFacts result = new Facts( mConfiguration.relationFactory );
		
		for( IPredicate predicate : mixed.getPredicates() )
		{
			if( ! predicate.getPredicateSymbol().endsWith( NEGATED_PREDICATE_SUFFIX ) )
				result.get( predicate ).addAll( mixed.get( predicate ) );
		}
		
		return result;
	}
	
	/**
	 * Extract all the facts associated with primed predicates from the facts provided.
	 * @param mixed The input collection of facts that can contain a mixture of primed and
	 * unprimed predicates.
	 * @return The facts associated with primed predicates only.
	 */
	public IFacts extractNegativeFacts( IFacts mixed )
	{
		IFacts result = new Facts( mConfiguration.relationFactory );
		
		for( IPredicate predicate : mixed.getPredicates() )
		{
			if( predicate.getPredicateSymbol().endsWith( NEGATED_PREDICATE_SUFFIX ) )
				result.get( predicate ).addAll( mixed.get( predicate ) );
		}
		
		return result;
	}
	
	/**
	 * Get the  positive facts used in the alternating fixed point algorithm.
	 * @return The original positive facts, wrapped such that modifications to the returned collection
	 * does not modify the original facts.
	 */
	public IFacts getPositiveStartingFacts()
	{
		return new OriginalFactsPreservingFacts( mOriginalFacts, mConfiguration.relationFactory );
	}
	
	/**
	 * Get the negative facts used in the alternating fixed point algorithm.
	 * @return The original positive facts, wrapped such that they are associated with primed
	 * predicates and such that modifications to the returned collection
	 * does not modify the original facts.
	 */
	public IFacts getNegativeStartingFacts()
	{
		return new InvertingFacts(
						new OriginalFactsPreservingFacts( mOriginalFacts, mConfiguration.relationFactory ),
						mConfiguration.relationFactory );
	}
	
	/**
	 * Get the rules used to start the AFP algorithm.
	 * @return The starting rules. These contain only those original rules with no
	 * negative body literals.  
	 */
	public List<IRule> getStartingRuleBase()
	{
		return mStartingRules;
	}

	/**
	 * Get the rules used in the 'negative' part of the AFP loop.
	 * @return The entire rule set modified such that each rule head is primed and
	 * each positive body literal is primed.
	 */
	public List<IRule> getNegativeRuleBase()
	{
		return mNegativeRules;
	}
	
	/**
	 * Get the rules used in the 'positive' part of the AFP loop.
	 * @return The entire rule set modified such that each negated body literal is primed.
	 */
	public List<IRule> getPositiveRuleBase()
	{
		return mPositiveRules;
	}
	
	/**
	 * Extract from the original rules all those that have no negative body literals.
	 */
	private void calculateStartingRuleBase()
	{
		for( IRule rule : mOriginalRules )
		{
			boolean containsNegatedLiteral = false;
			
			for( ILiteral literal : rule.getBody() )
			{
				if( ! literal.isPositive() && ! literal.getAtom().isBuiltin() )
				{
					containsNegatedLiteral = true;
					break;
				}
			}
			
			if( ! containsNegatedLiteral )
				mStartingRules.add( rule );
		}
	}

	/**
	 * Compute rules such that each head is primed and each positive body literal is primed.
	 * Negative body literals remain unprimed.
	 */
	private void calculateNegativeRuleBase()
	{
		for( IRule rule : mOriginalRules )
		{
			List<ILiteral> newHead = new ArrayList<ILiteral>();
			for( ILiteral literal : rule.getHead() )
			{
				newHead.add( makePrimedLiteral( literal ) );
			}
			
			List<ILiteral> newBody = new ArrayList<ILiteral>();
			for( ILiteral literal : rule.getBody() )
			{
				if( literal.isPositive() && ! literal.getAtom().isBuiltin() )
				{
					newBody.add( makePrimedLiteral( literal ) );
				}
				else
				{
					newBody.add( literal );
				}
			}
			
			mNegativeRules.add( Factory.BASIC.createRule( newHead, newBody ) );
		}
	}
	
	/**
	 * Compute rules such that each negative body literal is primed.
	 * Head and positive body literals remain unprimed.
	 */
	private void calculatePositiveRuleBase()
	{
		for( IRule rule : mOriginalRules )
		{
			List<ILiteral> newBody = new ArrayList<ILiteral>();
			
			for( ILiteral literal : rule.getBody() )
			{
				if( literal.isPositive() || literal.getAtom().isBuiltin() )
				{
					newBody.add( literal );
				}
				else
				{
					newBody.add( makePrimedLiteral( literal ) );
				}
			}
			
			mPositiveRules.add( Factory.BASIC.createRule( rule.getHead(), newBody ) );
		}
	}

	/**
	 * Create a primed literal from an unprimed literal.
	 * @param literal The unprimed literal
	 * @return The primed literal.
	 */
	private static ILiteral makePrimedLiteral( ILiteral literal )
	{
		IPredicate predicate = literal.getAtom().getPredicate();
		ITuple tuple = literal.getAtom().getTuple();
		
		return Factory.BASIC.createLiteral( literal.isPositive(), makePrimedPredicate( predicate ), tuple );
	}

	/**
	 * Create a primed predicate.
	 * @param predicate The unprimed predicate.
	 * @return The primed predicate.
	 */
	private static IPredicate makePrimedPredicate( IPredicate predicate )
	{
		String newPredicateSymbol = predicate.getPredicateSymbol() + NEGATED_PREDICATE_SUFFIX;
		return Factory.BASIC.createPredicate( newPredicateSymbol, predicate.getArity() );
	}

	/**
	 * A facts adaptor that makes all facts that are in the wrapped collection
	 * appear as primed facts.
	 */
	private static class InvertingFacts extends Facts
	{
		/**
		 * Constructor.
		 * @param positiveFacts The unprimed facts to make primed.
		 * @param relationFactory The factory used to create new relations.
		 */
		InvertingFacts( IFacts positiveFacts, IRelationFactory relationFactory )
		{
			super( relationFactory );

			for( IPredicate predicate : positiveFacts.getPredicates() )
			{
				mPredicateRelationMap.put( makePrimedPredicate( predicate ), positiveFacts.get( predicate ) );
			}
		}
	}
	
	/** The rules from the original program. */
	private final List<IRule> mOriginalRules;
	
	/** The facts from the original program. */
	private final IFacts mOriginalFacts;
	
	/** The knowledge base configuration object. */
	private final Configuration mConfiguration;
	
	/** The computed starting rules. */
	private final List<IRule> mStartingRules = new ArrayList<IRule>();
	
	/** The computed negative rules. */
	private final List<IRule> mNegativeRules = new ArrayList<IRule>();
	
	/** The computed positive rules. */
	private final List<IRule> mPositiveRules = new ArrayList<IRule>();
}
