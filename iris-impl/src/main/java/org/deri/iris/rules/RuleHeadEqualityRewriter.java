/*
 * Integrated Rule Inference System (IRIS):
 * An extensible rule inference system for datalog with extensions.
 * 
 * Copyright (C) 2009 Semantic Technology Institute (STI) Innsbruck, 
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

import static org.deri.iris.factory.Factory.BASIC;
import static org.deri.iris.factory.Factory.TERM;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.deri.iris.api.basics.IAtom;
import org.deri.iris.api.basics.ILiteral;
import org.deri.iris.api.basics.IPredicate;
import org.deri.iris.api.basics.IRule;
import org.deri.iris.api.basics.ITuple;
import org.deri.iris.api.builtins.IBuiltinAtom;
import org.deri.iris.api.terms.ITerm;
import org.deri.iris.api.terms.IVariable;
import org.deri.iris.factory.Factory;
import org.deri.iris.facts.IFacts;

/**
 * <p>
 * An utility class that creates new rules to realize support for rule head
 * equality. This class replaces all occurrences of rule head equality with a
 * special predicate. This class creates 2 unsafe rules (see rule 1 and rule 2),
 * therefore any reasoner, that uses rules created by this class must support
 * unsafe rules. Here, rule head equality is denoted by the predicate
 * <code>equivalent</code>.
 * </p>
 * <p>
 * New rules are created to define rule head equality. Note that rule 1 and 2
 * are unsafe rules, since the property <i>
 * "each variable in the rule head appears in a non-negated, relational subgoal"
 * </i> is violated.
 * </p>
 * <ol>
 * <li><code>equivalent(?X, ?X).</code></li>
 * <li><code>equivalent(?X, ?Y) :- ?X = ?Y.</code></li>
 * <li><code>equivalent(?Y, ?X) :- equivalent(?X, ?Y).</code></li>
 * <li>
 * <code>equivalent(?X, ?Z) :- equivalent(?X, ?Y), equivalent(?Y, ?Z).</code></li>
 * </ol>
 * <p>
 * For each predicate occurring in a given set of facts (relations) or in any
 * rule of a given collection of rules, <i>n</i> additional rules are created
 * where <i>n</i> is the arity of the predicate. Assume a predicate
 * <code>hasName(?X, ?Y, ?Z)</code> with arity 3. For this predicate the
 * following three rules are created:
 * </p>
 * <ol>
 * <li>
 * <code>hasName(?U, ?Y, ?Z) :- hasName(?X, ?Y, ?Z), equivalent(?X, ?U).</code></li>
 * <li>
 * <code>hasName(?X, ?U, ?Z) :- hasName(?X, ?Y, ?Z), equivalent(?Y, ?U).</code></li>
 * <li>
 * <code>hasName(?X, ?Y, ?U) :- hasName(?X, ?Y, ?Z), equivalent(?Z, ?U).</code></li>
 * </ol>
 * 
 * @author Adrian Marte
 */
public class RuleHeadEqualityRewriter implements IRuleHeadEqualityPreProcessor {

	/**
	 * The predicate replacing the rule head equality predicate. The predicate
	 * symbol is <code>$EQUIVALENT$</code>.
	 */
	public static final IPredicate PREDICATE = BASIC.createPredicate(
			"$EQUIVALENT$", 2);

	/**
	 * If set to <code>true</code> this rewriter creates new rules for given
	 * rules if and only if the given rules contain rules with rule head
	 * equality. If set to <code>false</code> this rewriter will create new
	 * rules no matter what.
	 */
	private final boolean checkForOccurrence;

	/**
	 * If set to <code>true</code> this rewriter creates unsafe rules. If set to
	 * <code>false</code> this rewriter will only create safe rules, and
	 * therefore may be incomplete.
	 */
	private final boolean isUsingUnsafeRules;

	/**
	 * Default constructor. Per default, the rewriter checks for occurrences of
	 * rules with rule head equality and creates unsafe rules.
	 */
	public RuleHeadEqualityRewriter() {
		this(true, true);
	}

	/**
	 * Default constructor. Depending on the value of
	 * <code>checkForOccurence</code>, the rewriter checks for occurrences of
	 * rules with rule head equality and rewrites given rules only if rules with
	 * rule head equality are present.
	 * 
	 * @param checkForOccurrence If set to <code>true</code> this rewriter
	 *            creates new rules for given rules if and only if given rules
	 *            contain rules with rule head equality. If set to
	 *            <code>false</code> this rewriter will create new rules no
	 *            matter what.
	 * @param useUnsafeRules If set to <code>true</code> this rewriter creates
	 *            unsafe rules. If set to <code>false</code> this rewriter will
	 *            only create safe rules, and therefore may be incomplete.
	 */
	public RuleHeadEqualityRewriter(boolean checkForOccurrence,
			boolean useUnsafeRules) {
		this.checkForOccurrence = checkForOccurrence;
		this.isUsingUnsafeRules = useUnsafeRules;
	}

	/**
	 * Creates a positive literal representing rule head equality, e.g. ?X = ?Y
	 * where ?X is <code>x</code> and ?Y is <code>y</code>.
	 * 
	 * @param x The first term.
	 * @param y The second term.
	 * @return A positive literal representing rule head equality.
	 */
	protected static ILiteral createLiteral(ITerm x, ITerm y) {
		return createLiteral(true, x, y);
	}

	/**
	 * Creates a literal representing rule head equality, e.g. ?X = ?Y where ?X
	 * is <code>x</code> and ?Y is <code>y</code>.
	 * 
	 * @param isPositive <code>true</code> if the literal is positive,
	 *            <code>false</code> otherwise.
	 * @param x The first term.
	 * @param y The second term.
	 * @return A positive literal representing rule head equality.
	 */
	protected static ILiteral createLiteral(boolean isPositive, ITerm x, ITerm y) {
		return BASIC.createLiteral(true, PREDICATE, BASIC.createTuple(x, y));
	}

	/**
	 * Creates a positive literal representing rule head equality, e.g. ?X = ?Y
	 * where ?X is the first and ?Y is second element of the tuple.
	 * 
	 * @param tuple The tuple.
	 * @return A positive literal representing rule head equality.
	 * @throws IllegalArgumentException If the size of the tuple is not 2.
	 */
	protected static ILiteral createLiteral(ITuple tuple)
			throws IllegalArgumentException {
		return createLiteral(true, tuple);
	}

	/**
	 * Creates a literal representing rule head equality, e.g. ?X = ?Y where ?X
	 * is the first and ?Y is second element of the tuple.
	 * 
	 * @param isPositive <code>true</code> if the literal is positive,
	 *            <code>false</code> otherwise.
	 * @param tuple The tuple.
	 * @return A positive literal representing rule head equality.
	 * @throws IllegalArgumentException If the size of the tuple is not 2.
	 */
	protected static ILiteral createLiteral(boolean isPositive, ITuple tuple)
			throws IllegalArgumentException {
		if (tuple.size() != 2) {
			throw new IllegalArgumentException(
					"The size of the tuple must be 2.");
		}

		return createLiteral(isPositive, tuple.get(0), tuple.get(1));
	}

	/**
	 * Creates new rules for the specified collection of rules, depending on the
	 * value of <code>checkForOccurence</code>. Note that the specified
	 * collection of rules is <b>not</b> added to the resulting collection.
	 * 
	 * @param rules The rules for which new rules should be created.
	 * @return The new rules without the specified collection of rules, or an
	 *         empty set if no new rules have been created.
	 */
	public Collection<IRule> rewrite(Collection<IRule> rules) {
		return rewrite(rules, null);
	}

	/**
	 * Creates new rules for the specified collection of rules and facts,
	 * depending on the value of <code>checkForOccurence</code>. Note that the
	 * specified collection of rules is <b>not</b> added to the resulting
	 * collection.
	 * 
	 * @param rules The rules for which new rules should be created.
	 * @param facts The facts for which new rules should be created.
	 * @return The new rules without the specified collection of rules, or an
	 *         empty collection if no new rules have been created.
	 */
	public Collection<IRule> rewrite(Collection<IRule> rules, IFacts facts) {
		boolean hasRuleHeadEquality = false;

		// Check if rules contain a rule with rule head equality.
		for (IRule rule : rules) {
			if (RuleHeadEquality.hasRuleHeadEquality(rule)) {
				hasRuleHeadEquality = true;
				break;
			}
		}

		Collection<IRule> newRules = new ArrayList<IRule>();

		// If no rules with rule head equality have been found, we return an
		// empty collection.
		if (checkForOccurrence && !hasRuleHeadEquality) {
			return newRules;
		}

		// Create additional equality rules.
		newRules.addAll(createEqualityRules());
		newRules.addAll(createNewRules(rules, facts));

		return newRules;
	}

	/**
	 * Creates new rules for the specified predicate.
	 * 
	 * @param predicate The predicate.
	 * @return A collection of new rules for the specified predicate.
	 */
	public Collection<IRule> rewrite(IPredicate predicate) {
		return rewritePredicates(Collections.singleton(predicate));
	}

	/**
	 * Creates new rules for the specified predicates.
	 * 
	 * @param predicates The collection of predicates.
	 * @return A collection of new rules for the specified collection of
	 *         predicates.
	 */
	protected Collection<IRule> rewritePredicates(
			Collection<IPredicate> predicates) {
		Collection<IRule> newRules = new LinkedList<IRule>();

		for (IPredicate predicate : predicates) {
			int arity = predicate.getArity();
			String prefix = "X";

			List<ILiteral> head;
			List<ILiteral> body;

			for (int i = 0; i < arity; i++) {
				IVariable equalVariable = null;
				IVariable fixedVariable = TERM.createVariable(prefix
						+ (arity + 1));

				IVariable[] variables1 = new IVariable[arity];
				IVariable[] variables2 = new IVariable[arity];

				for (int j = 0; j < arity; j++) {
					String name = prefix + (j + 1);
					variables1[j] = TERM.createVariable(name);

					if (i != j) {
						variables2[j] = TERM.createVariable(name);
					} else {
						variables2[j] = fixedVariable;
						equalVariable = variables1[j];
					}
				}

				// The following comments describe an example:
				// Given a predicate of arity 2 we have to add the following
				// rule.

				// q(?X1, ?X2)
				ILiteral firstLiteral = BASIC.createLiteral(true, predicate,
						BASIC.createTuple(variables1));

				// equivalent(?X1, ?X3)
				ILiteral equivalentLiteral = createLiteral(equalVariable,
						fixedVariable);

				// q(?X3, ?X2)
				ILiteral headLiteral = BASIC.createLiteral(true, predicate,
						BASIC.createTuple(variables2));

				// q(?X3, ?X2) :- q(?X1, ?X2), equivalent(?X1,X3).
				head = Arrays.asList(headLiteral);
				body = Arrays.asList(firstLiteral, equivalentLiteral);
				IRule rule = BASIC.createRule(head, body);

				newRules.add(rule);
			}
		}

		return newRules;
	}

	/**
	 * Creates new rules for the specified rule. Note that the specified rule is
	 * not added to the resulting collection of new rules.
	 * 
	 * @param rule The rule.
	 * @return A collection of new rules for the specified rule.
	 */
	public Collection<IRule> rewrite(IRule rule) {
		return rewriteRules(Collections.singleton(rule));
	}

	/**
	 * Creates new rules for the specified collection of rule. Note that the
	 * specified collection of rules is not added to the resulting collection of
	 * new rules.
	 * 
	 * @param rules The collection of rules.
	 * @return A collection of new rules for the specified rule.
	 */
	private Collection<IRule> rewriteRules(Collection<IRule> rules) {
		Set<IPredicate> predicates = extractPredicatesFromRules(rules);

		Collection<IRule> newRules = new LinkedList<IRule>();

		// For each predicate add additional rules with equality.
		for (IPredicate predicate : predicates) {
			newRules.addAll(rewrite(predicate));
		}

		return newRules;
	}

	/**
	 * Extracts all predicates occurring in the specified collection of rules.
	 * 
	 * @param rules The collection of rules.
	 * @return A set of all predicates occurring in the specified collection of
	 *         rules.
	 */
	protected Set<IPredicate> extractPredicatesFromRules(Collection<IRule> rules) {
		Set<IPredicate> predicates = new HashSet<IPredicate>();

		for (IRule rule : rules) {

			for (ILiteral literal : rule.getBody()) {
				IAtom atom = literal.getAtom();

				if (!(atom instanceof IBuiltinAtom)) {
					predicates.add(atom.getPredicate());
				}
			}

			if (!RuleHeadEquality.hasRuleHeadEquality(rule)) {
				for (ILiteral literal : rule.getHead()) {
					IAtom atom = literal.getAtom();

					if (!(atom instanceof IBuiltinAtom)) {
						predicates.add(atom.getPredicate());
					}
				}
			}
		}

		return predicates;
	}

	/**
	 * Creates new rules for the specified facts and the collection of rules.
	 * Note that the specified collection of rules is not added to the resulting
	 * collection of new rules.
	 * 
	 * @param rules The collection of rules.
	 * @param facts The facts.
	 * @return A collection of new rules for the specified facts and collection
	 *         of rules.
	 */
	private Collection<IRule> createNewRules(Collection<IRule> rules,
			IFacts facts) {
		Set<IPredicate> predicates = new HashSet<IPredicate>();

		// Add facts' predicates.
		if (facts != null) {
			predicates.addAll(facts.getPredicates());
		}

		// Add rules' predicates.
		if (rules != null) {
			predicates.addAll(extractPredicatesFromRules(rules));
		}

		Collection<IRule> newRules = new LinkedList<IRule>();

		// For each predicate, add additional rules with equality.
		for (IPredicate predicate : predicates) {
			newRules.addAll(rewrite(predicate));
		}

		return newRules;
	}

	/**
	 * <p>
	 * Creates the necessary equality rules, that is:
	 * </p>
	 * <ol>
	 * <li><code>equivalent(?X, ?X).</code></li>
	 * <li><code>equivalent(?X, ?Y) :- ?X = ?Y.</code></li>
	 * <li><code>equivalent(?Y, ?X) :- equivalent(?X, ?Y).</code></li>
	 * <li>
	 * <code>equivalent(?X, ?Z) :- equivalent(?X, ?Y), equivalent(?Y, ?Z).</code>
	 * </li>
	 * </ol>
	 * 
	 * @return A collection of new rules.
	 */
	protected Collection<IRule> createEqualityRules() {
		Collection<IRule> newRules = new ArrayList<IRule>(2);

		// Variables.
		IVariable x = TERM.createVariable("X");
		IVariable y = TERM.createVariable("Y");
		IVariable z = TERM.createVariable("Z");

		// equivalent(?Y, ?X) :- equivalent(?X, ?Y).
		List<ILiteral> body = Arrays.asList(createLiteral(x, y));
		List<ILiteral> head = Arrays.asList(createLiteral(y, x));

		IRule rule = BASIC.createRule(head, body);
		newRules.add(rule);

		// equivalent(?X, ?Z) :- equivalent(?X, ?Y), equivalent(?Y, ?Z).
		body = Arrays.asList(createLiteral(x, y), createLiteral(y, z));
		head = Arrays.asList(createLiteral(x, z));

		rule = BASIC.createRule(head, body);
		newRules.add(rule);

		// equivalent(?X, ?X). This is an unsafe rule.
		if (isUsingUnsafeRules) {
			body = new ArrayList<ILiteral>();
			head = Arrays.asList(createLiteral(x, x));
			rule = BASIC.createRule(head, body);
			newRules.add(rule);
		}

		// equivalent(?X, ?Y) :- ?X = ?Y. This is an unsafe rule.
		// This rule may be optimized to "equivalent(?Y, ?Y)." Useless?
		if (isUsingUnsafeRules) {
			body = Arrays.asList(BASIC.createLiteral(true, Factory.BUILTIN
					.createEqual(x, y)));
			head = Arrays.asList(createLiteral(x, y));
			rule = BASIC.createRule(head, body);
			newRules.add(rule);
		}

		return newRules;
	}

	public List<IRule> process(List<IRule> rules, IFacts facts) {
		Collection<IRule> newRules = rewrite(rules, facts);

		List<IRule> result = new ArrayList<IRule>(rules.size()
				+ newRules.size());

		// Replace the literal in the rule head for rules with rule head
		// equality.
		List<IRule> rulesWithReplacedPredicate = replaceHead(rules);

		result.addAll(rulesWithReplacedPredicate);
		result.addAll(newRules);

		return result;
	}

	public static List<IRule> replaceHead(List<IRule> rules) {
		List<IRule> result = new ArrayList<IRule>(rules.size());

		for (IRule rule : rules) {
			if (RuleHeadEquality.hasRuleHeadEquality(rule)) {
				List<ILiteral> head = rule.getHead();
				List<ILiteral> body = rule.getBody();

				// Create a new head with special literal.
				ITuple headTuple = head.get(0).getAtom().getTuple();
				ILiteral newHeadTuple = createLiteral(headTuple);
				List<ILiteral> newHead = Collections
						.singletonList(newHeadTuple);

				IRule newRule = BASIC.createRule(newHead, body);
				result.add(newRule);
			} else {
				result.add(rule);
			}
		}

		return result;
	}

}
