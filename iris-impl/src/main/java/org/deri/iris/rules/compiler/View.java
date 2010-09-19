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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.deri.iris.api.basics.ITuple;
import org.deri.iris.api.terms.ITerm;
import org.deri.iris.api.terms.IVariable;
import org.deri.iris.storage.IRelation;
import org.deri.iris.storage.IRelationFactory;
import org.deri.iris.utils.TermMatchingAndSubstitution;
import org.deri.iris.utils.equivalence.IEquivalentTerms;
import org.deri.iris.utils.equivalence.IgnoreTermEquivalence;

/**
 * <p>
 * A view on a relation, which represents how an instance of a sub-goal
 * interprets a relation.
 * </p>
 * <p>
 * Essentially, this class is an adaptor accepts tuples from a relation
 * and attempts to term match every term in the relation with the
 * corresponding term in the tuple in the sub-goal.
 * </p>
 * <p>
 * Therefore, the relation and the viewCriteria tuple must have the same arity.
 * However, the output (a relation) will have an arity corresponding to the
 * number of unique variables in the view criteria.
 * </p>
 * <p>
 * e.g. for this sub-goal: p( ?X, 'c', f(?Y), ?Y ) applied to this relation
 * </p>
 * <p>
 * 1, 'c', f(3), 4
 * </p>
 * <p>
 * 1, 'c', f(4), 4
 * </p>
 * <p>
 * 2, 'b', f(5), 5
 * </p>
 * <p>
 * 3, 'c', f(g(1)), g(1)
 * </p>
 * <p>
 * would filter and produce a view that appears as:
 * </p>
 * <p>
 * X  Y
 * </p>
 * <p>
 * =  =
 * </p>
 * <p>
 * 1, 4
 * </p>
 * <p>
 * 3, g(1)
 * </p>
 * <p>
 */
public class View implements IRelation
{
	/**
	 * Constructor.
	 * @param relation The relation to create the view over.
	 * @param viewCriteria The criteria to filter with.
	 */
	public View( IRelation relation, ITuple viewCriteria, IRelationFactory relationFactory )
	{
		this(relation, viewCriteria, new IgnoreTermEquivalence(), relationFactory);
	}

	/**
	 * Constructor.
	 * @param relation The relation to create the view over.
	 * @param viewCriteria The criteria to filter with.
	 * @param equivalentTerms The equivalent terms.
	 */
	public View( IRelation relation, ITuple viewCriteria, IEquivalentTerms equivalentTerms, IRelationFactory relationFactory ) {
		mViewCriteria = viewCriteria;
		mVariables = TermMatchingAndSubstitution.getVariables( mViewCriteria, true );
		mInputRelation = relation;
		mRelationFactory = relationFactory;
		mEquivalentTerms = equivalentTerms;

		// Check if simple view, i.e. only unique variables
		mSimple = isSimpleView( viewCriteria );
		if( mSimple )
			mViewTuples = relation;
		else
			mViewTuples = relationFactory.createRelation();
	}
	
	/**
	 * Kind of copy constructor.
	 * @param relation The viewed relation.
	 * @param viewCriteria The view criteria.
	 * @param variables The computed output variables.
	 * @param simple Indicates of the view is simple (pass thorugh)
	 */
	public View( IRelation relation, ITuple viewCriteria, List<IVariable> variables, boolean simple, IRelationFactory relationFactory )
	{
		this(relation, viewCriteria, variables, simple, new IgnoreTermEquivalence(), relationFactory);
	}
	
	/**
	 * Kind of copy constructor.
	 * @param relation The viewed relation.
	 * @param viewCriteria The view criteria.
	 * @param variables The computed output variables.
	 * @param simple Indicates of the view is simple (pass thorugh)
	 * @param equivalentTerms The equivalent terms.
	 */
	public View( IRelation relation, ITuple viewCriteria, List<IVariable> variables, boolean simple, 
			IEquivalentTerms equivalentTerms, IRelationFactory relationFactory )
	{
		mViewCriteria = viewCriteria;
		mVariables = variables;
		mInputRelation = relation;
		mSimple = simple;
		mRelationFactory = relationFactory;
		mEquivalentTerms = equivalentTerms;

		if( mSimple )
			mViewTuples = relation;
		else
			mViewTuples = mRelationFactory.createRelation();
	}
	
	/**
	 * Determine of the view is simple.
	 * The view is simple if no filtering occurs.
	 * This only happens if every term of the view criteria is a plain variable and unique, e.g.
	 * p( ?X, ?Y, ?Z ) is simple, but p(?X, ?X ) is not.
	 * @param viewCriteria The vew criteria.
	 * @return true, if the view is simple.
	 */
	public static boolean isSimpleView( ITuple viewCriteria )
	{
		Set<IVariable> vars = new HashSet<IVariable>();
		
		for( ITerm term : viewCriteria )
		{
			if( term instanceof IVariable )
			{
				IVariable variable = (IVariable) term;

				if( ! vars.add( variable ) )
					return false;
			}
			else
				return false;
		}
		
		return true;
	}
	
	/**
	 * Indicates if the view is simple.
	 * @return true, if the view is simple.
	 */
	public boolean isSimple()
	{
		return mSimple;
	}
	
	/**
	 * Get the output variables in order.
	 * @return
	 */
	public List<IVariable> variables()
	{
		return mVariables;
	}

	/**
	 * Adding to a View does not make sense.
	 * @throws RuntimeException if this method is called.
	 */
	public boolean add( ITuple tuple )
    {
		throw new RuntimeException( "add() has been called on a View object." );
    }

	/**
	 * Adding to a View does not make sense.
	 * @throws RuntimeException if this method is called.
	 */
	public boolean addAll( IRelation relation )
    {
		throw new RuntimeException( "addAll() has been called on a View object." );
    }

	public boolean contains( ITuple tuple )
    {
		if( ! mSimple )
			update();
	    return mViewTuples.contains( tuple );
    }

	public ITuple get( int index )
    {
		if( ! mSimple )
			update();
		return mViewTuples.get( index );
    }

	public int size()
    {
		if( ! mSimple )
			update();
		return mViewTuples.size();
    }

	/**
	 * Update the view with previously unseen tuples from the underlying relation.
	 */
	private void update()
	{
		// The matching tuples may increase due to a change in the equivalence relation,
		// therefore we have to check all tuples again if any change to the term
		// equivalence relation has been done.
		int hashCode = mEquivalentTerms.hashCode();
		
		if (hashCode != mPreviousHashCode) {
			mLastIndex = 0;
			
			mPreviousHashCode = hashCode;
		}
		
		for( ; mLastIndex < mInputRelation.size(); ++mLastIndex )
		{
			ITuple tuple = mInputRelation.get( mLastIndex );
			
			// When matching terms we also use the equivalent terms.
			ITuple viewTuple = TermMatchingAndSubstitution.matchTuple( mViewCriteria, 
					tuple, mEquivalentTerms );
			
			if( viewTuple != null ) {
				mViewTuples.add( viewTuple );
			}
		}
	}
	
	@Override
    public String toString()
    {
		return mViewTuples.toString();
    }

	/** The equivalent terms. */
	private IEquivalentTerms mEquivalentTerms;
	
	/** The hash code of the previous equivalent terms. */
	private int mPreviousHashCode = 0;
	
	/** The filtered view of the relation. */
	private final IRelation mViewTuples;
	
	/** The last index in the underlying relation seen by this view. */
	private int mLastIndex = 0;
	
	/** The criteria used to filter the underlying relation. */
	private final ITuple mViewCriteria;
	
	/** The underlying relation being viewed. */
	private final IRelation mInputRelation;
	
	/** The list of output variables. */
	private final List<IVariable> mVariables;
	
	/** Simple view indicator. */
	private final boolean mSimple;
	
	private final IRelationFactory mRelationFactory;
}
