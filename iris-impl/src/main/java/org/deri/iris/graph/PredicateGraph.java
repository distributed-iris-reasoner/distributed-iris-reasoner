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
package org.deri.iris.graph;

// TODO: implement equals, hashCode an clone.

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import org.deri.iris.api.basics.ILiteral;
import org.deri.iris.api.basics.IPredicate;
import org.deri.iris.api.basics.IRule;
import org.deri.iris.api.graph.ILabeledEdge;
import org.deri.iris.api.graph.IPredicateGraph;
import org.jgrapht.DirectedGraph;
import org.jgrapht.EdgeFactory;
import org.jgrapht.Graphs;
import org.jgrapht.alg.ConnectivityInspector;
import org.jgrapht.alg.CycleDetector;
import org.jgrapht.graph.DirectedMultigraph;

/**
 * <p>
 * A graph to determine the dependencies of rules and predicates to each other.
 * </p>
 * <p>
 * $Id$
 * </p>
 * 
 * @author Richard Pöttler (richard dot poettler at deri dot at)
 * @version $Revision$
 */
public class PredicateGraph implements IPredicateGraph {

	/** Comparator to order rules according to their dependencies. */
	private final RuleComparator rc = new RuleComparator();

	/** Comparator to order predicates according to their dependencies. */
	private final PredicateComparator pc = new PredicateComparator();

	/** Graph to represent the dependencies of the predicates. */
	private final DirectedGraph<IPredicate, LabeledEdge<IPredicate, Boolean>> g = 
		new DirectedMultigraph<IPredicate, LabeledEdge<IPredicate, Boolean>>(new PredicateEdgeFactory());

	/** Cycle detector, to determine, whether the rules are recursive. */
	private final CycleDetector<IPredicate, LabeledEdge<IPredicate, Boolean>> cd = 
		new CycleDetector<IPredicate, LabeledEdge<IPredicate, Boolean>>(g);

	/**
	 * Connectivity inspector to determine, whether paths between vertices
	 * exists.
	 */
	private final ConnectivityInspector ci = new ConnectivityInspector(g);

	/**
	 * Constructs an empty graph object.
	 */
	PredicateGraph() {
	}

	/**
	 * Constructs a new graph with a given set of rules.
	 * @param r the rules with which to initialize the graph
	 */
	PredicateGraph(final Collection<IRule> r) {
		if (r != null) {
			for (final IRule rule : r) {
				_addRule(rule);
			}
		}
	}

	public void addRule(final IRule rule) {
		_addRule(rule);
	}

	/**
	 * Adds a rule to this graph.
	 * @param rule the rule to add
	 * @throws NullPointerException if the rule is <code>null</code>
	 */
	private void _addRule(final IRule rule) {
		if (rule == null) {
			throw new NullPointerException("The rule must not be null");
		}

		for (final ILiteral h : rule.getHead()) {
			final IPredicate hp = h.getAtom().getPredicate();
			g.addVertex(hp);

			for (final ILiteral l : rule.getBody()) {
				final IPredicate p = l.getAtom().getPredicate();
				final LabeledEdge<IPredicate, Boolean> e = 
					new LabeledEdge<IPredicate, Boolean>(p, hp, l.isPositive());

				g.addVertex(p);

				// if there is no such edge, add the new one
				if (!g.edgeSet().contains(e)) {
					g.addEdge(p, hp, e);
				}
			}
		}
	}

	public void addRule(final Collection<IRule> r) {
		if ((r == null) || r.contains(null)) {
			throw new NullPointerException(
					"The rules must not be, or contain null");
		}
		for (final IRule rule : r) {
			addRule(rule);
		}
	}

	public boolean detectCycles() {
		return cd.detectCycles();
	}

	public Set<IPredicate> findVertexesForCycle() {
		return cd.findCycles();
	}

	public Set<ILabeledEdge<IPredicate, Boolean>> findEdgesForCycle() {
		final Set<IPredicate> cycle = findVertexesForCycle();
		final Set<ILabeledEdge<IPredicate, Boolean>> edges = 
			new HashSet<ILabeledEdge<IPredicate, Boolean>>();
		for (final IPredicate v : cycle) {
			for (final IPredicate p : Graphs.successorListOf(g, v)) {
				if (cycle.contains(p)) {
					edges.add(g.getEdge(v, p));
					break;
				}
			}
		}
		assert (edges.size() == cycle.size()) : "the number of edges and vertexes must be equal";
		return edges;
	}

	public int countNegativesForCycle() {
		int neg = 0;
		for (final ILabeledEdge<IPredicate, Boolean> e : findEdgesForCycle()) {
			if (!e.getLabel()) {
				neg++;
			}
		}
		return neg;
	}

	public Comparator<IRule> getRuleComparator() {
		return rc;
	}

	public Comparator<IPredicate> getPredicateComparator() {
		return pc;
	}

	public Set<IPredicate> getDepends(final IPredicate p) {
		if (p == null) {
			throw new NullPointerException("The predicate must not be null");
		}
		if (!g.containsVertex(p)) {
			return Collections.EMPTY_SET;
		}

		final Set<IPredicate> todo = new HashSet<IPredicate>();
		todo.add(p);
		final Set<IPredicate> deps = new HashSet<IPredicate>();

		while (!todo.isEmpty()) {
			final IPredicate act = todo.iterator().next();
			todo.remove(act);

			for (final IPredicate depends : Graphs.predecessorListOf(g, act)) {
				if (deps.add(depends)) {
					todo.add(depends);
				}
			}
		}

		return deps;
	}

	/**
	 * <p>
	 * Computes a short description of this object. <b>The format of the
	 * returned string is undocumented and subject to change.</b>.
	 * </p>
	 * <p>
	 * And example return string could be:
	 * </p>
	 * <p>
	 * <pre><code>
	 * a-&gt;(false)-&gt;b
	 * b-&gt;(true)-&gt;c
	 * </code></pre>
	 * </p>
	 * @return the string description
	 */
	public String toString() {
		final StringBuilder b = new StringBuilder();
		for (final LabeledEdge<IPredicate, Boolean> e : g.edgeSet()) {
			b.append(e).append(System.getProperty("line.separator"));
		}
		return b.toString();
	}
			

	/**
	 * <p>
	 * Compares two rules depending on their dependencies of each other.
	 * </p>
	 * <p>
	 * The rules will compared according to their headpredicates.
	 * </p>
	 * <p>
	 * $Id$
	 * </p>
	 * 
	 * @author richi
	 * @version $Revision$
	 * @see PredicateComparator
	 */
	private class RuleComparator implements Comparator<IRule> {

		public int compare(final IRule o1, final IRule o2) {
			if ((o1 == null) || (o2 == null)) {
				throw new NullPointerException("None of the rule must be null");
			}
			if ((o1.getHead().size() != 1) || (o2.getHead().size() != 1)) {
				throw new IllegalArgumentException(
						"Only rules with a headlength of 1 are allowed.");
			}
			return pc.compare(o1.getHead().get(0).getAtom().getPredicate(), 
					o2.getHead().get(0).getAtom().getPredicate());
		}
	}

	/**
	 * <p>
	 * Compares two predicates depending on their dependencies of their rules.
	 * </p>
	 * <p>
	 * If one of the compared predicate isn't in the graph, or there isn't a
	 * path from one predicate to the other {@code 0} will be returned. If the
	 * first predicate depends on the second one, the first one will be
	 * determined to be bigger, and vice versa.
	 * </p>
	 * <p>
	 * $Id$
	 * </p>
	 * 
	 * @author richi
	 * @version $Revision$
	 */
	private class PredicateComparator implements Comparator<IPredicate> {

		public int compare(final IPredicate o1, final IPredicate o2) {
			if ((o1 == null) || (o2 == null)) {
				throw new NullPointerException(
						"None of the predicates must be null");
			}

			// one of the vertices is not in the graph, or there is no
			// connection of the vertices -> return 0
			if (!g.containsVertex(o1) || !g.containsVertex(o2)
					|| !ci.pathExists(o1, o2)) {
				return 0;
			}
			// determine who depends on who
			return getDepends(o1).contains(o2) ? 1 : -1;
		}
	}

	/**
	 * <p>
	 * The simple factory to create default edges for the PredicateGraph.
	 * The label of the edge will be <code>true</code>.
	 * </p>
	 * <p>
	 * $Id$
	 * </p>
	 * @author Richard Pöttler (richard dot poettler at deri dot org)
	 * @version $Revision$
	 * @since 0.3
	 */
	private static class PredicateEdgeFactory implements EdgeFactory<IPredicate, LabeledEdge<IPredicate, Boolean>> {

		public LabeledEdge<IPredicate, Boolean> createEdge(final IPredicate s, final IPredicate t) {
			if ((s == null) || (t == null)) {
				throw new NullPointerException("The vertices must not be null");
			}
			return new LabeledEdge<IPredicate, Boolean>(s, t, true);
		}
	}
}
