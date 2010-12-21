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
package org.deri.iris.rules.stratification;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.deri.iris.api.basics.IAtom;
import org.deri.iris.api.basics.ILiteral;
import org.deri.iris.api.basics.IRule;
import org.deri.iris.api.basics.ITuple;
import org.deri.iris.api.terms.INumericTerm;
import org.deri.iris.api.terms.ITerm;
import org.deri.iris.api.terms.IVariable;
import org.deri.iris.api.terms.concrete.IDoubleTerm;
import org.deri.iris.api.terms.concrete.IFloatTerm;
import org.deri.iris.api.terms.concrete.IIntegerTerm;
import org.deri.iris.basics.Tuple;
import org.deri.iris.builtins.EqualBuiltin;
import org.deri.iris.builtins.ExactEqualBuiltin;
import org.deri.iris.builtins.GreaterBuiltin;
import org.deri.iris.builtins.GreaterEqualBuiltin;
import org.deri.iris.builtins.LessBuiltin;
import org.deri.iris.builtins.LessEqualBuiltin;
import org.deri.iris.builtins.NotEqualBuiltin;
import org.deri.iris.builtins.NotExactEqualBuiltin;
import org.deri.iris.factory.Factory;
import org.deri.iris.rules.IRuleStratifier;
import org.deri.iris.rules.RuleManipulator;
import org.deri.iris.rules.stratification.LocalStratificationDecorator.Adornment;
import org.deri.iris.rules.stratification.LocalStratificationDecorator.MatchType;
import org.deri.iris.utils.StandardFloatingPointComparator;

/**
 * A local stratification algorithm.
 * This algorithm will 'split' rules and so will likely return more rules
 * than were provided.
 * @see org.deri.iris.rules.IRuleStratifier#stratify()
 */
public class LocalStratifier implements IRuleStratifier
{
	/**
	 * Constructor.
	 * @param strict true, if the stratifier is permitted to substitute variables with constants
	 * when they appear in equality built-ins.
	 * false, if the substitution may only occur when the built-in is an exact equality
	 */
	public LocalStratifier( boolean strict )
	{
		mStrict = strict;
	}
	
	/**
	 * Indicates if the stratifier is applying strict variable-constant substitutions only.
	 * @return true, if strict, false otherwise.
	 */
	public boolean isStrict()
	{
		return mStrict;
	}
	
	public List<List<IRule>> stratify( List<IRule> rules )
	{
		mRules.clear();
		
		adornRules( rules );
		splitRules();
		
		int ruleStratum[] = new int[ mRules.size() ];
		
		final int ruleCount = mRules.size();
		int highest = 0;
		boolean change = true;
		RuleManipulator rm = new RuleManipulator();


		while ((highest <= ruleCount ) && change)
		{
			change = false;
			for( int r = 0; r < mRules.size(); ++r )
			{
				IRule currentRule = mRules.get( r ).getRule();
				
				currentRule = rm.replaceVariablesWithConstants( currentRule, mStrict );
				currentRule = rm.removeUnnecessaryEqualityBuiltins( currentRule );

				if( currentRule.getBody().size() == 0 )
					ruleStratum[ r ] = 0;
				else
				{
					for (final ILiteral bl : currentRule.getBody())
					{
						for( int r2 = 0; r2 < mRules.size(); ++r2 )
						{
							// We even check for a negative dependency on self!
							LocalStratificationDecorator adaptor = mRules.get( r2 );
	
							if( adaptor.getRule().getHead().get( 0 ).getAtom().getPredicate().equals( bl.getAtom().getPredicate() ) )
							{
								MatchType match = adaptor.match( bl.getAtom().getTuple() );
								if( match != MatchType.NONE )
								{							
									if( bl.isPositive() )
									{
										if( ruleStratum[ r ] < ruleStratum[ r2 ] )
										{
											ruleStratum[ r ] = ruleStratum[ r2 ];
											change = true;
										}
									}
									else
									{
										if( ruleStratum[ r ] <= ruleStratum[ r2 ] )
										{
											ruleStratum[ r ] = ruleStratum[ r2 ] + 1;
											change = true;
										}
									}
									highest = Math.max( ruleStratum[ r ], highest );
								}
							}
						}
					}
				}
			}
		}
		
		if( highest < ruleCount )
		{
			List<List<IRule>> result = new ArrayList<List<IRule>>();
			
			for( int stratum = 0; stratum <= highest; ++stratum )
				result.add( new ArrayList<IRule>() );

			for( int r = 0; r < mRules.size(); ++r ) {
				IRule rule = mRules.get( r ).getRule();
				
				// Check if rule with rule head equality is in stratum 0.
				if (!GlobalStratifier.checkRuleHeadEquality(rule, ruleStratum[r])) {
					return null;
				}
				
				result.get( ruleStratum[ r ] ).add( rule );
			}
			
			return result;
		}
		else
			return null;
	}
	
	/**
	 * For every literal of every rule, if the literal is negative and not a built-in:
	 * split any dependent rules
	 */
	private void splitRules()
	{
		boolean changed;
		
		do
		{
			changed = false;

			for( LocalStratificationDecorator ruleAdaptor : mRules )
			{
				IRule rule = ruleAdaptor.getRule();
				
				if( splitRulesForLiteralsFromOneRule( rule ) )
				{
					// At least one rule has been split, so start again at the beginning
					changed = true;
					break;
				}
			}
		} while( changed );
	}

	/**
	 * Split rules 'currentRule' depends on
	 * @param currentRule The rule to examine.
	 * @return true, if any dependent rules were split.
	 */
	private boolean splitRulesForLiteralsFromOneRule( IRule currentRule )
	{
		// For each literal in the body
		for( ILiteral literal : currentRule.getBody() )
		{
			// If the literal is negated
			if( ! literal.isPositive() )
			{
				IAtom atom = literal.getAtom();

				// If it is not a built-in (can't split built-ins!)
				if( ! atom.isBuiltin() )
				{
					ITuple tuple = atom.getTuple();
					
					List<IVariable> variables = tuple.getAllVariables();
					boolean hasConstants = variables.size() != tuple.size();
					
					if( hasConstants )
					{
						if( splitRulesForAtom( atom ) )
							return true;
					}
				}
			}
		}
		
		return false;
	}

	/**
	 * Split rules that can produce tuples for 'atom'.
	 * @param atom The atom to examine.
	 * @return true, if any rules were split.
	 */
	private boolean splitRulesForAtom( IAtom atom )
	{
		boolean somethingSplit = false;
		boolean changed;
		
		do
		{
			changed = false;

			for( int ruleIndex = 0; ruleIndex < mRules.size(); ++ruleIndex )
			{
				LocalStratificationDecorator decorator = mRules.get( ruleIndex );
				IRule rule = decorator.getRule();
				
				// Predicate has same name and arity?
				if( atom.getPredicate().equals( rule.getHead().get( 0 ).getAtom().getPredicate() ) )
				{
					ITuple negatedSubGoalTuple = atom.getTuple();
					
					// If we have a partial match (subset)
					if( decorator.match( negatedSubGoalTuple ) == MatchType.CONSUMES_SUBSET )
					{
						// Remove the current rule
						mRules.remove( ruleIndex );
						
						LocalStratificationDecorator exactMatchRule = makeExactMatchRule( decorator, negatedSubGoalTuple );
						LocalStratificationDecorator noMatchRule = makeNoMatchRule( decorator, negatedSubGoalTuple );
						
						if( exactMatchRule != null )
							mRules.add( exactMatchRule );
						
						if( noMatchRule != null )
							mRules.add( noMatchRule );
						
						if( exactMatchRule != null || noMatchRule != null )
						{
							somethingSplit = true;
							changed = true;
							break;
						}
					}
				}
			}
		} while( changed );
		
		return somethingSplit;
	}
	
	/**
	 * Create a rule that will always produce tuples that are an exact match the given rule.
	 * @param decorator The decorated rule.
	 * @param negatedSubGoalTuple The tuple from the dependent rule's negated sub-goal.
	 * @return The new rule.
	 */
	private LocalStratificationDecorator makeExactMatchRule( LocalStratificationDecorator decorator, ITuple negatedSubGoalTuple )
	{
		IRule rule = decorator.getRule();
		List<LocalStratificationDecorator.Adornment> adornments = decorator.getAdornments();
		ITuple ruleHead = rule.getHead().get( 0 ).getAtom().getTuple();

		RuleManipulator rm = new RuleManipulator();
		
		assert ruleHead.size() == adornments.size();
		assert ruleHead.size() == negatedSubGoalTuple.size();
		
		for( int t = 0; t < adornments.size(); ++t )
		{
			ITerm headTerm = ruleHead.get( t );
			ITerm subGoalTerm = negatedSubGoalTuple.get( t );
			LocalStratificationDecorator.Adornment adornment = adornments.get( t );
			
			assert ! headTerm.isGround() || ! subGoalTerm.isGround() || headTerm.equals( subGoalTerm );
			assert 	( adornment.getPositiveConstant() != null &&   headTerm.isGround() ) ||
					( adornment.getPositiveConstant() == null && ! headTerm.isGround() );
			
			if( ! headTerm.isGround() && subGoalTerm.isGround() )
			{
				// Must add VAR = const term to rule and adorn
				IVariable variable = (IVariable) headTerm;
				
				rule = rm.addEquality( rule, variable, subGoalTerm );
//				rule = rm.replaceVariablesWithConstants( rule, mStrict );
//				rule = rm.removeUnnecessaryEqualityBuiltins( rule );
			}
		}
		
		return adornRule( rule );
	}
	
	/**
	 * Create a rule that will never produce tuples that are an exact match the given rule.
	 * @param decorator The decorated rule.
	 * @param negatedSubGoalTuple The tuple from the dependent rule's negated sub-goal.
	 * @return The new rule.
	 */
	private LocalStratificationDecorator makeNoMatchRule( LocalStratificationDecorator decorator, ITuple negatedSubGoalTuple )
	{
		IRule rule = decorator.getRule();
		List<LocalStratificationDecorator.Adornment> adornments = decorator.getAdornments();
		ITuple ruleHead = rule.getHead().get( 0 ).getAtom().getTuple();

		RuleManipulator rm = new RuleManipulator();
		
		assert ruleHead.size() == adornments.size();
		assert ruleHead.size() == negatedSubGoalTuple.size();
		
		for( int t = 0; t < adornments.size(); ++t )
		{
			ITerm headTerm = ruleHead.get( t );
			ITerm subGoalTerm = negatedSubGoalTuple.get( t );
			LocalStratificationDecorator.Adornment adornment = adornments.get( t );
			
			assert ! headTerm.isGround() || ! subGoalTerm.isGround() || headTerm.equals( subGoalTerm );
			assert 	( adornment.getPositiveConstant() != null &&   headTerm.isGround() ) ||
					( adornment.getPositiveConstant() == null && ! headTerm.isGround() );
			
			if( ! headTerm.isGround() && subGoalTerm.isGround() )
			{
				// Must add VAR != const term to rule and adorn
				IVariable variable = (IVariable) headTerm;
				
				rule = rm.addInequality( rule, variable, subGoalTerm );
			}
		}

		return adornRule( rule );
	}
	
	@Override
    public String toString()
    {
		StringBuilder buffer = new StringBuilder();
		
		for( LocalStratificationDecorator adaptor : mRules )
		{
			buffer.append( adaptor.toString() ).append( "\r\n" );
		}
		
		return buffer.toString();
    }

	/**
	 * Put the adornment decorations on the rules.
	 * @param rules The rules to stratify.
	 */
	private void adornRules( Collection<IRule> rules )
	{
		for( IRule rule : rules )
		{
			mRules.add( adornRule( rule ) );
		}
	}

	private LocalStratificationDecorator adornRule( final IRule rule )
	{
		RuleManipulator rm = new RuleManipulator();

		IRule r = rm.replaceVariablesWithConstants( rule, mStrict );
		r = rm.replaceVariablesWithVariables( r );
		r = rm.removeUnnecessaryEqualityBuiltins( r );
		r = rm.removeDuplicateLiterals( r );
		
		List<Adornment> adornments = new ArrayList<Adornment>();

		// TODO For the time being, this is only for rules with a single head predicate
		Tuple tuple = (Tuple) r.getHead().get( 0 ).getAtom().getTuple();
		for( ITerm term : tuple )
		{
			Adornment adornment = new Adornment();
			
			if( term.isGround() )
				adornment = adornment.setConstantTerm( term );
			else if( term instanceof IVariable )
			{
				IVariable headVariable = (IVariable) term;
				
				// Term is a variable
				adornment = extractNotEqualAdornmentsFromRuleBody( rule, headVariable );
			}

			adornments.add( adornment );
		}
		
		return new LocalStratificationDecorator( rule, adornments );
	}

	/**
	 * Helper to scan a rule body looking for built-in predicates that imply that the variable is
	 * not equal to some constant, e.g. ?X < 'a' implies that the rules can not produce any values
	 * for ?X equal to 'a'.
	 * @param rule The rule to examine.
	 * @param headVariable The variable in the head for which we are interested.
	 * @return An adornment containing all the negated constants (if any).
	 */
	private Adornment extractNotEqualAdornmentsFromRuleBody( IRule rule, IVariable headVariable )
	{
		Adornment adornment = new Adornment();
		
		// Scan rule body looking for BIN_OP( variable, constant )
		// !=, <, >, not =, not >=, not <=, imply variable != constant adornment
		// If constant is numeric, then can add adornment for not every numeric type
		
		for( ILiteral literal : rule.getBody() )
		{
			boolean positive = literal.isPositive();
			boolean not_equal = false;
			
			IAtom atom = literal.getAtom();
			if( atom.isBuiltin() )
			{
				if( positive )
				{
					// Positive literal
					if( atom instanceof NotEqualBuiltin ||
						atom instanceof NotExactEqualBuiltin ||
						atom instanceof LessBuiltin ||
						atom instanceof GreaterBuiltin )
						not_equal = true;
				}
				else
				{
					// Negative literal
					if( atom instanceof EqualBuiltin ||
						atom instanceof ExactEqualBuiltin ||
						atom instanceof GreaterEqualBuiltin ||
						atom instanceof LessEqualBuiltin )
						not_equal = true;
				}
			}
			
			if( not_equal )
			{
				ITuple tuple = atom.getTuple();
				
				assert tuple.size()== 2;
				
				IVariable variable = null;
				ITerm constant = null;
				for( ITerm term : tuple )
				{
					if( term instanceof IVariable )
						variable = (IVariable) term;
					if( term.isGround() )
						constant = term;
				}
				
				if( variable != null && constant != null )
				{
					if( headVariable.equals( variable ) )
					{
						adornment = addNegatedConstant( adornment, constant );
					}
				}
			}
		}
		
		return adornment;
	}
	
	/**
	 * If we know that a variable can not be equal to a given constant term, then we can add
	 * the adornment.
	 * However, if the constant term one of the numeric types, then we can add an adornment for
	 * every numeric type.
	 * @param adornment
	 * @param constant
	 * @return
	 */
	private Adornment addNegatedConstant( Adornment adornment, ITerm constant )
	{
		if( constant instanceof IIntegerTerm ||
			constant instanceof IFloatTerm ||
			constant instanceof IDoubleTerm ||
			constant instanceof INumericTerm )
		{
			Number value = (Number)constant.getValue();
			
			if( constant instanceof IIntegerTerm )
				adornment = adornment.addNegatedConstant( Factory.CONCRETE.createInteger( value.intValue() ) );
			else
			{
				double v = value.doubleValue();
				if( StandardFloatingPointComparator.getDouble().isIntValue( v ) )
					adornment = adornment.addNegatedConstant( Factory.CONCRETE.createInteger( value.intValue() ) );
			}
			
			adornment = adornment.addNegatedConstant( Factory.CONCRETE.createFloat( value.floatValue() ) );
			adornment = adornment.addNegatedConstant( Factory.CONCRETE.createDouble( value.doubleValue() ) );
			adornment = adornment.addNegatedConstant( Factory.CONCRETE.createDecimal( value.doubleValue() ) );
		}
		else
		{
			adornment = adornment.addNegatedConstant( constant );
		}
		
		return adornment;
	}
	
	/** The list of rules to process. */
	private final List<LocalStratificationDecorator> mRules = new ArrayList<LocalStratificationDecorator>();
	
	/** The strictness flag. */
	private boolean mStrict;
}
