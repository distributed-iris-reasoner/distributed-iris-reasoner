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
package org.deri.iris.optimisations.magicsets;

import static org.deri.iris.factory.Factory.BASIC;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.deri.iris.api.IProgramOptimisation;
import org.deri.iris.api.basics.IAtom;
import org.deri.iris.api.basics.ILiteral;
import org.deri.iris.api.basics.IPredicate;
import org.deri.iris.api.basics.IQuery;
import org.deri.iris.api.basics.IRule;
import org.deri.iris.api.basics.ITuple;
import org.deri.iris.api.builtins.IBuiltinAtom;
import org.deri.iris.api.terms.ITerm;
import org.deri.iris.api.terms.IVariable;
import org.deri.iris.graph.LabeledEdge;
import org.deri.iris.optimisations.magicsets.AdornedProgram.AdornedPredicate;
import org.deri.iris.optimisations.magicsets.AdornedProgram.AdornedRule;

/**
 * <p>
 * Simple implementation of the &quot;Generalized Magic Sets&quot; according to
 * the &quot;The Power of Magic&quot; paper.
 * </p>
 * 
 * @author Richard Pöttler (richard dot poettler at deri sti2 at)
 */
public final class MagicSets implements IProgramOptimisation {

	/**
	 * The prefix for the magic predicates. Should prevent parser users to
	 * collide with our internal naming.
	 */
	static final String MAGIC_PREFIX = "magic_xXx_prefix_xXx";

	/**
	 * The prefix for the labeled predicates. Should prevent parser users to
	 * collide with our internal naming.
	 */
	static final String LABEL_PREFIX = "label_xXx_prefix_xXx";

	public Result optimise(final Collection<IRule> rules, final IQuery query) {
		if (rules == null) {
			throw new IllegalArgumentException("The rules must not be null");
		}
		if (query == null) {
			throw new IllegalArgumentException("The query must not be null");
		}

		// check, whether the query contains constants
		int constants = 0;
		for (final ILiteral literal : query.getLiterals()) {
			final IAtom atom = literal.getAtom();
			if (!atom.isBuiltin()) { // we count only constants in ordinary literals
				for (final ITerm term : atom.getTuple()) {
					if (term.isGround()) {
						constants++;
					}
				}
			}
		}
		// if there aren't any constants -> return null
		if (constants == 0) {
			return null;
		}

		final AdornedProgram adornedProg = new AdornedProgram(rules, query);

		// creating the magic query
		final IQuery unadornedQuery = unadornQuery(adornedProg.getQuery());

		// setting the rules
		final List<IRule> magicRules = new ArrayList<IRule>();

		for (final AdornedRule rule : adornedProg.getAdornedRules()) {
			if (rule.getRule().getHead().size() != 1) {
				throw new IllegalArgumentException("At the moment only heads "
						+ "with length of 1 are allowed");
			}

			for (final ILiteral literal : rule.getRule().getBody()) {
				if (literal.getAtom().getPredicate() instanceof AdornedPredicate) {
					// creating a magic rule for the literal
					magicRules.addAll(createMagicRules(literal, rule));
				}
			}
			// adding the rewritten rule
			magicRules.add(createRewrittenRule(rule));
		}

		// adding the remaining rules
		magicRules.addAll(filterRemainingRules(adornedProg.getNormalRules(),
						adornedProg.getAdornedRules()));
		// adding the rules for the conjunctive query
		magicRules.addAll(createConjunctiveRules(adornedProg.getQuery()));
		// adding the rule for the seed
		final IAtom seed = createSeed(unadornedQuery);
		// construct the seed rule
		if (seed != null) {
			magicRules.add(BASIC.createRule(Arrays.asList(BASIC.createLiteral(true, seed)),
						Collections.<ILiteral>emptyList()));
		}

		// unadorn the rules
		final List<IRule> unadornedRules = unadornRules(magicRules);

		return new Result(unadornedRules, unadornedQuery);
	}

	/**
	 * Computes the magic rules needed to evaluate conjunctive queries.
	 * @param query the query
	 * @return the computed rules
	 */
	private static Set<IRule> createConjunctiveRules(final IQuery query) {
		assert query != null: "The query must not be null";

		final Set<IRule> result = new HashSet<IRule>();
		final List<ILiteral> queryLiterals = query.getLiterals();
		// every binding of one literal of the query depends on the
		// bindings of it's preceding literal -> create the magic rules
		// for the literals
		// the loop starts at 1, because the first literals doesn't have
		// preceding literals
		for (int i = 1, max = queryLiterals.size(); i < max; i++) {
			final IAtom atom = queryLiterals.get(i).getAtom();
			// there should no magic rules be created for builtins
			if (atom instanceof IBuiltinAtom) {
				continue;
			}
			final IAtom magicAtom = createBoundAtom(atom, null, MAGIC_PREFIX, null);
			if (!magicAtom.getTuple().isEmpty()) {
				// the rule head got to be positive, no matter
				// whether the literal was positive, or not,
				// because the relation resulting out of this
				// rule will be inverted anyway, if used in a
				// rule's body
				result.add(BASIC.createRule(Arrays.asList(BASIC.createLiteral(true, magicAtom)),
							slice(queryLiterals, 0, i)));
			}
		}
		return result;
	}

	/**
	 * Returns a sublist of a list.
	 * @param orig the original list
	 * @param from from where to copy the elements (inclusive)
	 * @param to to which element to copy (exclusive)
	 * @return the sublist
	 */
	private static <Type> List<Type> slice(final List<Type> orig, final int from, final int to) {
		assert orig != null: "The list must not be null";

		// adjust the input variables for the origianl list size
		final int nfrom = (from < 0) ? 0 : from;
		final int nto = ((to < 0) || (to > orig.size())) ? orig.size() : to;

		assert (nto > nfrom): "nfrom (" + nfrom + ") must be smaller than nto(" + nto + ")";

		// copy the range
		final List<Type> res = new ArrayList<Type>(nto - nfrom);
		for (int i = nfrom; i < nto; i++) {
			res.add(orig.get(i));
		}
		return res;
	}

	/**
	 * <p>
	 * Creates a rewritten rule for the adorned one. This method simply
	 * rewrites a rule my adding the "magic_" literal at the beginning of
	 * the rule.
	 * </p>
	 * 
	 * @param rule the rule which to rewrite
	 * @return the rewritten rule
	 */
	private static IRule createRewrittenRule(final AdornedRule rule) {
		assert rule != null: "The rule must not be null";
		assert rule.getRule().getHead().size() == 1: 
			"The head must have a length of 1, but was " + rule.getRule().getHead().size();

		final ILiteral headL = rule.getRule().getHead().get(0);

		// computing the rewritten body
		final List<ILiteral> rewrittenBody = new ArrayList<ILiteral>(rule.getRule().getBody());
		Collections.sort(rewrittenBody, rule.getSip().getLiteralComparator());

		final ILiteral magicLiteral = createMagicLiteral(headL);
		if (magicLiteral == headL) { // the head literal is not adorned 
			// -> the query was not adorned -> nothing to exchange
			return rule.getRule();
		}
		if (magicLiteral.getAtom().getTuple().isEmpty()) { // the literal wouldn't produce 
			// any bindings, so it is better to leave it out (since
			// with the new sip we wouldn't have the rules for this
			// literal constructed anyway
			return rule.getRule();
		}
		rewrittenBody.add(0, magicLiteral);

		return BASIC.createRule(rule.getRule().getHead(), rewrittenBody);
	}

	/**
	 * <p>
	 * Creates a seed out of the first literal of a query.
	 * </p>
	 * <p>
	 * The predicate of the literal will be adorned according to it's bound and 
	 * free terms and the terms of the literal of the query will be limited to 
	 * it's bound arguments.
	 * </p>
	 * 
	 * @param query for which to create the seed
	 * @return the seed or <code>null</code> if the query didn't any
	 * literals
	 */
	private static IAtom createSeed(final IQuery query) {
		assert query != null: "The query must not be null";

		return (query.getLiterals().isEmpty())
			? null
			: createBoundAtom(query.getLiterals().get(0).getAtom(), null, MAGIC_PREFIX, null);
	}

	/**
	 * Creates a magic rule for the given literal. It takes into account, how
	 * many arcs are entering the specific literal and constructs the
	 * corresponding rules.
	 * 
	 * @param literal literal for which to create the rule
	 * @param adornedRule the original rule containing the given literal
	 * @return the set of generated rules
	 */
	private static Set<IRule> createMagicRules(final ILiteral literal, final AdornedRule adornedRule) {
		assert literal != null: "The literal must not be null";
		assert adornedRule != null: "The rule must not be null";
		assert literal.getAtom().getPredicate() instanceof AdornedPredicate: 
			"The predicate of the literal must be adorned";
		assert adornedRule.getRule().getHead().size() == 1: 
			"The head must have a size of 1, but was " + adornedRule.getRule().getHead().size();

		final Set<LabeledEdge<ILiteral, Set<IVariable>>> enteringEdges = 
			adornedRule.getSip().getEdgesEnteringLiteral(literal);

		if (enteringEdges.size() == 1) {
			// only on arc is entering this literal
			return Collections.singleton(createMagicRule(enteringEdges.iterator().next(), adornedRule));
		} else if (enteringEdges.size() > 1) {
			// multible arcs entering this literal
			final Set<IRule> rules = new HashSet<IRule>(enteringEdges.size() + 1);
			// creating the labeled rules
			int counter = 1;
			for (final LabeledEdge<ILiteral, Set<IVariable>> e : enteringEdges) {
				rules.add(createLabeledRule(e, adornedRule, counter++));
			}
			// computing the body for the magic rule
			final Set<ILiteral> bodyLiterals = new HashSet<ILiteral>(rules.size());
			for (final IRule rule : rules) {
				bodyLiterals.add(rule.getHead().get(0));
			}
			final ILiteral headLiteral = createMagicLiteral(true, literal);
			rules.add(BASIC.createRule(Arrays.asList(headLiteral),
						new ArrayList<ILiteral>(bodyLiterals)));
			return rules;
		}
		// there are no edges entering this literal -> all would be free
		return Collections.<IRule>emptySet();
	}

	/**
	 * Creates a magic rule for the given literal.
	 * 
	 * @param edge the edge to the literal
	 * @param rule the adorned rule which contain the literal
	 * @return the magic rule
	 */
	private static IRule createMagicRule(final LabeledEdge<ILiteral, Set<IVariable>> edge,
			final AdornedRule rule) {
		assert edge != null: "The edge must not be null";
		assert rule != null: "The rule must not be null";

		return BASIC.createRule(Arrays.asList(createMagicLiteral(true, edge.getTarget())),
				createRestrictedBody(edge.getSource(), rule));
	}

	/**
	 * Creates a labeled rule for the given literal.
	 * 
	 * @param edge the edge to the literal
	 * @param rule the adorned rule which contain the literal
	 * @param index the index of this labeled rule
	 * @return the labeled rule
	 */
	private static IRule createLabeledRule(final LabeledEdge<ILiteral, Set<IVariable>> edge,
			final AdornedRule rule,
			final int index) {
		assert edge != null: "The edge must not be null";
		assert rule != null: "The rule must not be null";
		assert index > 0: "The index must be greater than 0";

		return BASIC.createRule(Arrays.asList(createLabeledLiteral(true,
						edge.getTarget(),
						edge.getLabel(),
						index)),
				createRestrictedBody(edge.getSource(), rule));
	}

	/**
	 * Creates the body of a magic rule. This method is intended to operate
	 * with the source of a edge entering the literal for which you want to
	 * create the magic rule's body.
	 * @param passer the literal passing the variables
	 * @param rule the rule from where to take the passings
	 * @return the list of body literals for the magic rule
	 */
	private static List<ILiteral> createRestrictedBody(final ILiteral passer, final AdornedRule rule) {
		assert passer != null: "The passing literal must not be null";
		assert rule != null: "The rule must not be null";

		final Set<ILiteral> passers = rule.getSip().getDepends(passer);
		passers.add(passer);
		final ILiteral head = rule.getRule().getHead().get(0);

		// add all passings from the body of the rule
		final List<ILiteral> body = new ArrayList<ILiteral>(passers);
		body.remove(head);
		Collections.sort(body, rule.getSip().getLiteralComparator());

		// if there is a variable passing from the head of the rule, get
		// them from it's magic relation
		if (passers.contains(head)) {
			body.add(0, createMagicLiteral(head));
		}
		return body;
	}

	/**
	 * Constructs a new atom containing only it's bound terms. If bound
	 * variables are given or the predicate of the atom is not adorned the
	 * bound terms of the atom will be determined according to it's ground
	 * terms and given bound variables. Otherwise the adornments of the
	 * predicate are taken.
	 * @param atom the atom from which to create the adorned version
	 * @param bound the bound variables (might be <code>null</code>)
	 * @param prefix the prefix for the predicate symbol
	 * @param suffix the suffix for the predicate symbol
	 * @return the newly created atom
	 */
	private static IAtom createBoundAtom(final IAtom atom, final Collection<IVariable> bound,
			final String prefix, final String suffix) {
		assert atom != null: "The atom must not be null";

		final AdornedPredicate ap;
		// if we got bound variables, or the predicate isn't already
		// adorned, create a new adorned predicate
		if ((bound != null) || !(atom.getPredicate() instanceof AdornedPredicate)) {
			ap = new AdornedPredicate(atom, bound);
		} else { // the predicate was already adorned
			ap = (AdornedPredicate) atom.getPredicate();
		}

		// constructing the tuple

		final ITuple boundTuple = BASIC.createTuple(getBounds(ap, atom));

		// constructing the new predicate symbol
		final StringBuilder newPredicateSymbol = new StringBuilder();

		if ((prefix != null) && prefix.length() > 0) {
			newPredicateSymbol.append(prefix).append("_");
		}

		newPredicateSymbol.append(ap.getPredicateSymbol()).append("_");

		if ((suffix != null) && (suffix.length() > 0)) {
			newPredicateSymbol.append(suffix).append("_");
		}

		for (final Adornment adornment : ap.getAdornment()) {
			newPredicateSymbol.append(adornment);
		}

		// constructing the new predicate
		final IPredicate newPredicate = BASIC.createPredicate(newPredicateSymbol.toString(),
				Collections.frequency(Arrays.asList(ap.getAdornment()), Adornment.BOUND));

		return BASIC.createAtom(newPredicate, boundTuple);
	}

	/**
	 * <p>
	 * Creates a magic literal out of an adorned one. The terms of the
	 * literal will only consist of the bound terms.
	 * </p>
	 * <p>
	 * Note that not adorned literals are taken as if they would have only
	 * bounds.
	 * </p>
	 * 
	 * @param literal the literal for which to create the adorned one
	 * @return the magic literal or the same literal again, if the predicate
	 * of the literal wasn't adorned (and so no bound variables could be
	 * determined)
	 */
	private static ILiteral createMagicLiteral(final ILiteral literal) {
		return createMagicLiteral(literal.isPositive(), literal);
	}

	/**
	 * <p>
	 * Creates a magic literal out of an adorned one. The terms of the
	 * literal will only consist of the bound terms.
	 * </p>
	 * <p>
	 * Note that not adorned literals are taken as if they would have only
	 * bounds.
	 * </p>
	 * 
	 * @param positive whether the resulting literal should be positive, or
	 * not
	 * @param literal the literal for which to create the adorned one
	 * @return the magic literal or the same literal again, if the predicate
	 * of the literal wasn't adorned (and so no bound variables could be
	 * determined)
	 */
	private static ILiteral createMagicLiteral(final boolean positive, final ILiteral literal) {
		assert literal != null: "The literal must not be null";

		// if the literal isn't adorned then there isn't anything to do,
		// because we can't determine any bound variables
		if (!(literal.getAtom().getPredicate() instanceof AdornedPredicate)) {
			return literal;
		}
		return BASIC.createLiteral(positive,
				createBoundAtom(literal.getAtom(), null, MAGIC_PREFIX, null));
	}

	/**
	 * Creates a labeled literal out of an adorned one. The predicate of the
	 * literal must be adorned. The terms of the literal will only consist of
	 * the bound terms.
	 * 
	 * @param positive <code>true</code> the resulting literal should be 
	 * positive, otherwise <code>false</code>
	 * @param literal for which to create the labeled literal
	 * @param passings the variables passed by this edge
	 * @param index to append to the literal
	 * @return the labeled literal
	 */
	private static ILiteral createLabeledLiteral(final boolean positive, 
			final ILiteral literal, final Set<IVariable> passings, final int index) {
		assert literal != null: "The literal must not be null";
		assert passings != null: "The passings must not be null";
		assert index >= 0 : "The index must not be negative";

		return BASIC.createLiteral(positive,
				createBoundAtom(literal.getAtom(), passings, LABEL_PREFIX, Integer.toString(index)));
	}

	/**
	 * Returns the list of bound terms of the literal according to the bounds of
	 * the adornment of the predicate. The order of the terms won't be changed.
	 * 
	 * @param perdicate3 where to take the adornments from
	 * @param atom the atom from where to take the terms
	 * @return the list of bound terms
	 */
	private static List<ITerm> getBounds(final AdornedPredicate predicate,
			final IAtom atom) {
		assert predicate != null: "The predicate must not be null";
		assert atom != null: "The atom must not be null";
		assert predicate.hasSameSignature(atom.getPredicate()):
			"The signature of the predicate and the predicate of the atom must match";

		final List<ITerm> bounds = new ArrayList<ITerm>(predicate.getAdornment().length);
		final Iterator<ITerm> terms = atom.getTuple().iterator();
		for (Adornment adornment : predicate.getAdornment()) {
			final ITerm term = terms.next();
			if (adornment == Adornment.BOUND) {
				bounds.add(term);
			}
		}
		return bounds;
	}

	/**
	 * Filters the given normal rules (unadorned ones) and removes every occurrence
	 * where an adorned one (with or without a guardian literal) exist.
	 * 
	 * @param normalRules the normal rules to filter
	 * @param adornedRules the adorned rules
	 * @return a set of filtered rules
	 */
	private static Set<IRule> filterRemainingRules(final Collection<IRule> normalRules,
			final Collection<AdornedRule> adornedRules) {
		assert normalRules != null: "The normal rules must not be null";
		assert adornedRules != null: "The adorned rules must not be null";

		final Set<IRule> remaining = new HashSet<IRule>();
		for (final IRule normalRule : normalRules) {
			boolean toAdd = true;
			for (final AdornedRule adornedRule : adornedRules) {
				if (isSameRule(normalRule, adornedRule.getRule())) {
					toAdd = false;
					break;
				}
			}
			if (toAdd) {
				remaining.add(normalRule);
			}
		}
		return remaining;
	}

	/**
	 * Checks two rules whether they might be the same. If one rule is the
	 * adorned (with or without a guardian literal) version of the other the
	 * method will return {@code true}.
	 * 
	 * @param r0 the first rule to compare
	 * @param r1 the second rule to compare
	 * @return {@code true} if the rules express the same
	 */
	private static boolean isSameRule(final IRule r0, final IRule r1) {
		assert r0 != null: "The first rule must not be null";
		assert r1 != null: "The second rule must not be null";

		// comparing the head literals
		final Iterator<ILiteral> h0 = r0.getHead().iterator();
		final Iterator<ILiteral> h1 = r1.getHead().iterator();
		while (h0.hasNext() && h1.hasNext()) {
			if (!isSameLiteral(h0.next(), h1.next())) {
				return false;
			}
		}
		if (h0.hasNext() || h1.hasNext()) {
			return false;
		}

		// comparing the body literals
		final Iterator<ILiteral> b0 = r0.getBody().iterator();
		final Iterator<ILiteral> b1 = r1.getBody().iterator();
		while (b0.hasNext() && b1.hasNext()) {
			ILiteral l0 = b0.next();
			while (l0.getAtom().getPredicate().getPredicateSymbol().startsWith(
					MAGIC_PREFIX)
					&& b0.hasNext()) {
				l0 = b0.next();
			}
			ILiteral l1 = b1.next();
			while (l1.getAtom().getPredicate().getPredicateSymbol().startsWith(
					MAGIC_PREFIX)
					&& b1.hasNext()) {
				l1 = b1.next();
			}
			if (!isSameLiteral(l0, l1)) {
				return false;
			}
		}
		if (b0.hasNext() || b1.hasNext()) {
			return false;
		}
		return true;
	}

	/**
	 * Checks whether two literals are the same. The possible adornments won't
	 * be taken into account for equality.
	 * 
	 * @param l0 the first literal to compare
	 * @param l1 the second literal to compare
	 * @return {@code true} if the literals are the same
	 */
	private static boolean isSameLiteral(final ILiteral l0, final ILiteral l1) {
		assert l0 != null: "The first literal must not be null";
		assert l1 != null: "The second literal must not be null";

		if (!isSamePredicate(l0.getAtom().getPredicate(), l1.getAtom().getPredicate())) {
			return false;
		}
		// compare the terms
		return l0.getAtom().getTuple().equals(l1.getAtom().getTuple());
	}

	/**
	 * Checks whether two predicates are the same only according to their
	 * symbol and arity.
	 * @param p0 the first predicate to check
	 * @param p1 the second predicate to check
	 * @return <code>true</code> if the symbol and the arity matches,
	 * otherswise <code>false</code>
	 */
	private static boolean isSamePredicate(final IPredicate p0, final IPredicate p1) {
		assert p0 != null: "The first predicate must not be null";
		assert p1 != null: "The second predicate must not be null";

		return p0.getPredicateSymbol().equals(p1.getPredicateSymbol()) && (p0.getArity() == p1.getArity());
	}

	/**
	 * Converts a list of rules containing adorned predicates to rules
	 * consisting only of basic iris objects.
	 * @param rules the rules to unadorn
	 * @return the unadorned rules
	 * @see #unadornPredicate(IPredicate)
	 */
	private static List<IRule>unadornRules(final Collection<IRule> rules) {
		assert rules != null: "The rules must not be null";

		final List<IRule> result = new ArrayList<IRule>(rules.size());
		for (final IRule r : rules) {
			result.add(BASIC.createRule(unadornLiterals(r.getHead()),
						unadornLiterals(r.getBody())));
		}
		return result;
	}

	/**
	 * Converts a query containing adorned predicates to a query
	 * consisting only of basic iris objects.
	 * @param query the query to unadorn
	 * @return the unadorned query
	 * @see #unadornPredicate(IPredicate)
	 */
	private static IQuery unadornQuery(final IQuery query) {
		assert query != null: "The rule must not be null";

		return BASIC.createQuery(unadornLiterals(query.getLiterals()));
	}

	/**
	 * Converts a list of literals containing adorned predicates to a
	 * list of literals consisting only of basic iris objects.
	 * @param literals the literals to unadorn
	 * @return the unadorned literals
	 * @see #unadornPredicate(IPredicate)
	 */
	private static List<ILiteral> unadornLiterals(final Collection<ILiteral> literals) {
		assert literals != null: "The literals must not be null";

		final List<ILiteral> result = new ArrayList<ILiteral>(literals.size());
		for (final ILiteral literal : literals) {
			final IPredicate predicate = literal.getAtom().getPredicate();
			if (predicate instanceof AdornedPredicate) {
				result.add(BASIC.createLiteral(literal.isPositive(),
							BASIC.createPredicate(predicate.getPredicateSymbol(), predicate.getArity()),
							literal.getAtom().getTuple()));
			} else {
				result.add(literal);
			}
		}
		return result;
	}
}
