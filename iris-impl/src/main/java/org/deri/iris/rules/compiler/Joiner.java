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
import java.util.List;

import org.deri.iris.api.basics.IPredicate;
import org.deri.iris.api.basics.ITuple;
import org.deri.iris.api.terms.ITerm;
import org.deri.iris.api.terms.IVariable;
import org.deri.iris.factory.Factory;
import org.deri.iris.facts.IFacts;
import org.deri.iris.storage.IIndex;
import org.deri.iris.storage.IIndexFactory;
import org.deri.iris.storage.IRelation;
import org.deri.iris.storage.IRelationFactory;
import org.deri.iris.utils.equivalence.IEquivalentTerms;

/**
 * A compiled rule element representing a join from previous literals to this
 * one.
 */
class Joiner extends RuleElement {
	/**
	 * Constructor.
	 * 
	 * @param inputVariables The variable bindings from previous rule elements.
	 * @param predicate The predicate of this literal.
	 * @param thisLiteralsRelation The relation to use for this literal.
	 * @param viewCriteria The view criteria (tuple in the sub-goal instance in
	 *            the rule).
	 * @param equivalentTerms The equivalent terms.
	 */
	public Joiner(List<IVariable> inputVariables, IPredicate predicate,
			IRelation thisLiteralsRelation, ITuple viewCriteria,
			IEquivalentTerms equivalentTerms, IIndexFactory indexFactory,
			IRelationFactory relationFactory) {
		assert inputVariables != null;
		assert predicate != null;
		assert thisLiteralsRelation != null;
		assert indexFactory != null;
		assert relationFactory != null;

		mIndexFactory = indexFactory;
		mRelationFactory = relationFactory;

		mPredicate = predicate;
		mViewCriteria = viewCriteria;
		mEquivalentTerms = equivalentTerms;

		mView = new View(thisLiteralsRelation, viewCriteria, mEquivalentTerms,
				mRelationFactory);
		mViewVariables = mView.variables();

		assert mViewVariables != null;

		// Find the indices of the variables used in the natural join
		List<Integer> join1 = new ArrayList<Integer>();
		List<Integer> join2 = new ArrayList<Integer>();

		for (int i1 = 0; i1 < inputVariables.size(); ++i1) {
			IVariable var1 = inputVariables.get(i1);

			for (int i2 = 0; i2 < mViewVariables.size(); ++i2) {
				IVariable var2 = mViewVariables.get(i2);

				if (var1.equals(var2)) {
					join1.add(i1);
					join2.add(i2);

					// NB Variables in views occur only once
					break;
				}
			}
		}

		mJoinIndicesInput = Utils.integerListToArray(join1);
		mJoinIndicesThisLiteral = Utils.integerListToArray(join2);

		// Now find the indices of variables that are not used in the natural
		// join
		List<Integer> remainder1 = new ArrayList<Integer>();
		List<Integer> remainder2 = new ArrayList<Integer>();

		for (int i1 = 0; i1 < inputVariables.size(); ++i1) {
			if (!join1.contains(i1))
				remainder1.add(i1);
		}

		for (int i2 = 0; i2 < mViewVariables.size(); ++i2) {
			if (!join2.contains(i2))
				remainder2.add(i2);
		}

		mRemainderIndicesInput = Utils.integerListToArray(remainder1);
		mRemainderIndicesThisLiteral = Utils.integerListToArray(remainder2);

		// Lastly, build the list of output variables
		mOutputVariables = new ArrayList<IVariable>();

		for (int i : mJoinIndicesInput)
			mOutputVariables.add(inputVariables.get(i));

		for (int i : mRemainderIndicesInput)
			mOutputVariables.add(inputVariables.get(i));

		for (int i : mRemainderIndicesThisLiteral)
			mOutputVariables.add(mViewVariables.get(i));

		// Create the index for the second relation
		mIndexThisLiteral = mIndexFactory.createIndex(mView, mEquivalentTerms,
				mJoinIndicesThisLiteral);
	}

	/**
	 * Constructor used for iterative evaluation.
	 * 
	 * @param predicate The predicate of this literal.
	 * @param thisLiteralsRelation The relation to use for this literal.
	 * @param viewCriteria The view criteria (tuple in the sub-goal instance in
	 *            the rule).
	 * @param viewVariables The variables of the view.
	 * @param simple Indicates if the view is a simple one.
	 * @param joinIndicesInput Join indices from input tuples.
	 * @param joinIndicesThisLiteral Join indices for this literal's relation.
	 * @param remainderIndicesInput Indices of those terms not used in joining.
	 * @param remainderIndicesThisLiteral Indices of those terms not used in
	 *            joining.
	 * @param outputVariables The output variable bindings for this literal.
	 * @param equivalentTerms The equivalent terms.
	 */
	public Joiner(IPredicate predicate, IRelation thisLiteralsRelation,
			ITuple viewCriteria, List<IVariable> viewVariables, boolean simple,
			int[] joinIndicesInput, int[] joinIndicesThisLiteral,
			int[] remainderIndicesInput, int[] remainderIndicesThisLiteral,
			List<IVariable> outputVariables, IEquivalentTerms equivalentTerms,
			IIndexFactory indexFactory, IRelationFactory relationFactory) {
		mPredicate = predicate;

		mViewCriteria = viewCriteria;
		mViewVariables = viewVariables;

		mIndexFactory = indexFactory;
		mRelationFactory = relationFactory;
		mEquivalentTerms = equivalentTerms;

		mView = new View(thisLiteralsRelation, viewCriteria, viewVariables,
				simple, mEquivalentTerms, mRelationFactory);

		mJoinIndicesInput = joinIndicesInput;
		mJoinIndicesThisLiteral = joinIndicesThisLiteral;

		mRemainderIndicesInput = remainderIndicesInput;
		mRemainderIndicesThisLiteral = remainderIndicesThisLiteral;

		mIndexThisLiteral = mIndexFactory.createIndex(mView, mEquivalentTerms,
				mJoinIndicesThisLiteral);

		mOutputVariables = outputVariables;
	}

	@Override
	public IRelation process(IRelation leftRelation) {
		assert leftRelation != null;

		IRelation result = mRelationFactory.createRelation();
		
		for (int f = 0; f < leftRelation.size(); ++f) {
			ITuple leftTuple = leftRelation.get(f);

			List<ITerm> key = Utils.makeKey(leftTuple, mJoinIndicesInput);
			List<ITuple> matchingRightTuples = mIndexThisLiteral.get(key);

			// Must match because that's what the index does
			for (ITuple matchingRightTuple : matchingRightTuples) {
				ITuple concatenated = concatenate(leftTuple, matchingRightTuple);
				result.add(concatenated);
			}
		}

		return result;
	}

	/**
	 * Join two tuples using join and remainder indices.
	 * 
	 * @param first The input tuple
	 * @param second The tuple from this literal's relation
	 * @return The concatenated tuple.
	 */
	protected ITuple concatenate(ITuple first, ITuple second) {
		// TODO
		// Optimisation possible: If either remainder indices are zero length
		// then can just
		// return one or the other tuple. However, care must be taken during
		// initialisation
		// to ensure that the output variable order is correct.
		ITerm[] terms = new ITerm[mJoinIndicesInput.length
				+ mRemainderIndicesInput.length
				+ mRemainderIndicesThisLiteral.length];

		int index = 0;

		for (int i : mJoinIndicesInput)
			terms[index++] = first.get(i);

		for (int i : mRemainderIndicesInput)
			terms[index++] = first.get(i);

		for (int i : mRemainderIndicesThisLiteral)
			terms[index++] = second.get(i);

		return Factory.BASIC.createTuple(terms);
	}

	@Override
	public RuleElement getDeltaSubstitution(IFacts deltas) {
		IRelation delta = deltas.get(mPredicate);

		if (delta == null || delta.size() == 0)
			return null;

		return new Joiner(mPredicate, delta, mViewCriteria, mView.variables(),
				mView.isSimple(), mJoinIndicesInput, mJoinIndicesThisLiteral,
				mRemainderIndicesInput, mRemainderIndicesThisLiteral,
				mOutputVariables, mEquivalentTerms, mIndexFactory,
				mRelationFactory);
	}

	/** The predicate for this literal. */
	private final IPredicate mPredicate;

	/** The view on this literal's relation. */
	private final View mView;

	/** The view criteria on this literal's relation. */
	private final ITuple mViewCriteria;

	/** The variables from this literal's view. */
	private final List<IVariable> mViewVariables;

	/** The join indices from input tuples. */
	private final int[] mJoinIndicesInput;

	/** The join indices from this literal's view (on a relation). */
	private final int[] mJoinIndicesThisLiteral;

	/** The indices of terms from input tuples not used in joining. */
	private final int[] mRemainderIndicesInput;

	/** The indices of terms from this literal's view not used in joining. */
	private final int[] mRemainderIndicesThisLiteral;

	/** The index used for this literal. */
	private final IIndex mIndexThisLiteral;

	private final IIndexFactory mIndexFactory;

	private final IRelationFactory mRelationFactory;

	/** The equivalent terms. */
	private IEquivalentTerms mEquivalentTerms;
}
