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
import java.util.Collection;
import java.util.List;

import org.deri.iris.Configuration;
import org.deri.iris.EvaluationException;
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
import org.deri.iris.facts.IFacts;
import org.deri.iris.rules.RuleHeadEquality;
import org.deri.iris.storage.IRelation;
import org.deri.iris.utils.equivalence.IEquivalentTerms;
import org.deri.iris.utils.equivalence.IgnoreTermEquivalence;

/**
 * A rule compiler for creating objects that compute new facts using
 * forward-chaining techniques.
 */
public class RuleCompiler implements IRuleCompiler {

	/**
	 * Constructor.
	 * 
	 * @param facts The facts that will be used by the compiled rules.
	 */
	public RuleCompiler(IFacts facts, Configuration configuration) {
		this(facts, new IgnoreTermEquivalence(), configuration);
	}

	/**
	 * Creates a new RuleCompiler.
	 * 
	 * @param facts The facts that will be used by the compiled rules.
	 * @param equivalentTerms The equivalent terms.
	 * @param configuration The configuration.
	 */
	public RuleCompiler(IFacts facts, IEquivalentTerms equivalentTerms,
			Configuration configuration) {
		mFacts = facts;
		mConfiguration = configuration;
		mEquivalentTerms = equivalentTerms;
	}

	/* (non-Javadoc)
	 * @see org.deri.iris.rules.compiler.IRuleCompiler#compile(org.deri.iris.api.basics.IRule)
	 */
	public ICompiledRule compile(IRule rule) throws EvaluationException {
		List<RuleElement> elements = compileBody(rule.getBody());

		List<IVariable> variables;

		if (elements.size() == 0)
			variables = new ArrayList<IVariable>();
		else {
			RuleElement lastElement = elements.get(elements.size() - 1);
			variables = lastElement.getOutputVariables();
		}

		IAtom headAtom = rule.getHead().get(0).getAtom();
		ITuple headTuple = headAtom.getTuple();

		HeadSubstituter substituter;

		// We create a special head substituter for rules with head
		// equality, that establishes equivalence relation between terms in the
		// equivalent terms data-structure.
		if (RuleHeadEquality.hasRuleHeadEquality(rule)) {
			substituter = new RuleHeadEqualitySubstituter(variables, headTuple,
					mEquivalentTerms, mConfiguration);
		} else {
			substituter = new HeadSubstituter(variables, headTuple,
					mConfiguration);
		}

		elements.add(substituter);

		return new CompiledRule(elements, headAtom.getPredicate(),
				mConfiguration);
	}

	/**
	 * Compile a query. No optimisations of any kind are attempted.
	 * 
	 * @param query The query to be compiled
	 * @return The compiled query, ready to be evaluated
	 * @throws EvaluationException If the query can not be compiled for any
	 *             reason.
	 */
	public ICompiledRule compile(IQuery query) throws EvaluationException {
		List<RuleElement> elements = compileBody(query.getLiterals());

		return new CompiledRule(elements, null, mConfiguration);
	}

	/**
	 * Compile a rule body (or query). The literals are compiled in the order
	 * given. However, if one literal can not be compiled, because one or more
	 * of its variables are not bound from the proceeding literal, then it is
	 * skipped an re-tried later.
	 * 
	 * @param bodyLiterals The list of literals to compile
	 * @return The compiled rule elements.
	 * @throws EvaluationException If a rule construct can not be compiled (e.g.
	 *             a built-in has constructed terms)
	 */
	private List<RuleElement> compileBody(Collection<ILiteral> bodyLiterals)
			throws EvaluationException {
		List<ILiteral> literals = new ArrayList<ILiteral>(bodyLiterals);

		List<RuleElement> elements = new ArrayList<RuleElement>();

		List<IVariable> previousVariables = new ArrayList<IVariable>();

		while (elements.size() < bodyLiterals.size()) {
			EvaluationException lastException = null;

			boolean added = false;
			for (int l = 0; l < literals.size(); ++l) {
				ILiteral literal = literals.get(l);
				IAtom atom = literal.getAtom();
				boolean positive = literal.isPositive();

				RuleElement element;

				try {
					if (atom instanceof IBuiltinAtom) {
						IBuiltinAtom builtinAtom = (IBuiltinAtom) atom;
						
						// Tell the builtin atom the term equivalence relation,
						// so that it can also take the equivalent terms into
						// account when evaluating. 
						builtinAtom.setEquivalenceClasses(mEquivalentTerms);

						boolean constructedTerms = false;
						for (ITerm term : atom.getTuple()) {
							if (term instanceof IConstructedTerm) {
								constructedTerms = true;
								break;
							}
						}

						if (constructedTerms)
							element = new BuiltinForConstructedTermArguments(
									previousVariables, builtinAtom, positive,
									mEquivalentTerms, mConfiguration);
						else
							element = new Builtin(previousVariables,
									builtinAtom, positive, mEquivalentTerms,
									mConfiguration);
					} else {
						IPredicate predicate = atom.getPredicate();
						IRelation relation = mFacts.get(predicate);
						ITuple viewCriteria = atom.getTuple();

						if (positive) {
							if (previousVariables.size() == 0) {
								// First sub-goal
								element = new FirstSubgoal(predicate, relation,
										viewCriteria, mEquivalentTerms,
										mConfiguration);
							} else {
								element = new Joiner(previousVariables,
										predicate, relation, viewCriteria,
										mEquivalentTerms,
										mConfiguration.indexFactory,
										mConfiguration.relationFactory);
							}
						} else {
							// This *is* allowed to be the first literal for
							// rules such as:
							// p('a') :- not q('b')
							// or even:
							// p('a') :- not q(?X)
							element = new Differ(previousVariables, relation,
									viewCriteria, mEquivalentTerms,
									mConfiguration);
						}
					}
					previousVariables = element.getOutputVariables();

					elements.add(element);

					literals.remove(l);
					added = true;
					break;
				} catch (EvaluationException e) {
					// Oh dear. Store the exception and try the next literal.
					lastException = e;
				}
			}
			if (!added) {
				// No more literals, so the last error really was serious.
				throw lastException;
			}
		}

		if (elements.size() > 0) {
			RuleElement lastElement = elements.get(elements.size() - 1);
			RuleElement element = new EquivalenceResolver(lastElement
					.getOutputVariables(), mEquivalentTerms, mConfiguration);
			elements.add(element);
		}

		return elements;
	}

	/** The equivalent terms. */
	private IEquivalentTerms mEquivalentTerms;

	/** The knowledge-base facts used to attach to the compiled rule elements. */
	private final IFacts mFacts;

	/** The knowledge-base configuration. */
	private final Configuration mConfiguration;
}
