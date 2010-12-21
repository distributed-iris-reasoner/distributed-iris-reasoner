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
package org.deri.iris.builtins;

import static org.deri.iris.factory.Factory.BASIC;
import static org.deri.iris.factory.Factory.CONCRETE;
import static org.deri.iris.factory.Factory.TERM;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.deri.iris.KnowledgeBaseFactory;
import org.deri.iris.api.IKnowledgeBase;
import org.deri.iris.api.basics.ILiteral;
import org.deri.iris.api.basics.IPredicate;
import org.deri.iris.api.basics.IQuery;
import org.deri.iris.api.basics.IRule;
import org.deri.iris.api.terms.ITerm;
import org.deri.iris.api.terms.IVariable;
import org.deri.iris.factory.Factory;
import org.deri.iris.storage.IRelation;
import org.deri.iris.storage.simple.SimpleRelationFactory;

/**
 * <p>
 * Tests for the {@code LessBuiltin Evaluation}.
 * </p>
 * 
 * @author Darko Anicic, DERI Innsbruck
 * @date   23.04.2007 16:47:07
 */
public class LessBuiltinEvaluationTest extends TestCase {

	public static Test suite() {
		return new TestSuite(LessBuiltinEvaluationTest.class, LessBuiltinEvaluationTest.class
				.getSimpleName());
	}

	public void testEvaluate0() throws Exception{
		// constructing the rules
		List<IRule> rules = new ArrayList<IRule>(3);
		// p(X) :- r(X)
		IRule r = Factory.BASIC.createRule(Arrays.asList(createLiteral(
				"p", "X")), Arrays.asList(createLiteral("r", "X")));
		rules.add(r);
		// p(X) :- s(X), less(?X, ?Y), r(?Y).
		List<ILiteral> h = Arrays.asList(createLiteral("p", "X"));
		List<ILiteral> b = Arrays.asList(
				createLiteral("s", "X"),
				Factory.BASIC.createLiteral(true, Factory.BUILTIN.
				createLess(
						TERM.createVariable("X"),
						TERM.createVariable("Y"))),
				createLiteral("r", "Y"));
		
		r = Factory.BASIC.createRule(h, b);
		rules.add(r);

		// create facts
		Map<IPredicate, IRelation> facts = new HashMap<IPredicate, IRelation>();
		// s(1), s(2), s(9)
		IPredicate p = Factory.BASIC.createPredicate("s", 1);
		IRelation rel = new SimpleRelationFactory().createRelation();

		rel.add(BASIC.createTuple(CONCRETE.createInteger(1)));
		rel.add(BASIC.createTuple(CONCRETE.createInteger(2)));
		rel.add(BASIC.createTuple(CONCRETE.createInteger(9)));
		facts.put(p, rel);

		// r(3)
		p = Factory.BASIC.createPredicate("r", 1);
		rel = new SimpleRelationFactory().createRelation();
		rel.add(BASIC.createTuple(CONCRETE.createInteger(3)));
		facts.put(p, rel);
		
		IQuery q = Factory.BASIC.createQuery(createLiteral("p", "X"));
		Set<IQuery> queries = new HashSet<IQuery>(1);
		queries.add(q);
		final IKnowledgeBase pr = KnowledgeBaseFactory.createKnowledgeBase( facts, rules );
		
		// Result: p(1), p(2), p(3)
		IRelation res = new SimpleRelationFactory().createRelation();
		res.add(BASIC.createTuple(CONCRETE.createInteger(1)));
		res.add(BASIC.createTuple(CONCRETE.createInteger(2)));
		res.add(BASIC.createTuple(CONCRETE.createInteger(3)));
		
		System.out.println("******** TEST 0: ********");
		ExecutionHelper.executeTest(pr, q, res);
	}
	
	public void testEvaluate1() throws Exception{
		// constructing the rules
		List<IRule> rules = new ArrayList<IRule>(3);
		// p(X) :- r(X)
		IRule r = Factory.BASIC.createRule(Arrays.asList(createLiteral(
				"p", "X")), Arrays.asList(createLiteral("r", "X")));
		rules.add(r);
		// p(X) :- s(X), less(4, 3), r(?Y).
		List<ILiteral> h = Arrays.asList(createLiteral("p", "X"));
		List<ILiteral> b = Arrays.asList(
				createLiteral("s", "X"),
				Factory.BASIC.createLiteral(true, Factory.BUILTIN.
				createLess(
						CONCRETE.createInteger(4),
						CONCRETE.createInteger(3))),
				createLiteral("r", "Y"));
		
		r = Factory.BASIC.createRule(h, b);
		rules.add(r);

		// create facts
		Map<IPredicate, IRelation> facts = new HashMap<IPredicate, IRelation>();
		// s(1), s(2), s(9)
		IPredicate p = Factory.BASIC.createPredicate("s", 1);
		IRelation rel = new SimpleRelationFactory().createRelation();
		rel.add(BASIC.createTuple(CONCRETE.createInteger(1)));
		rel.add(BASIC.createTuple(CONCRETE.createInteger(2)));
		rel.add(BASIC.createTuple(CONCRETE.createInteger(9)));
		facts.put(p, rel);

		// r(3)
		p = Factory.BASIC.createPredicate("r", 1);
		rel = new SimpleRelationFactory().createRelation();
		rel.add(BASIC.createTuple(CONCRETE.createInteger(3)));
		facts.put(p, rel);
		
		IQuery q = Factory.BASIC.createQuery(createLiteral("p", "X"));
		Set<IQuery> queries = new HashSet<IQuery>(1);
		queries.add(q);
		final IKnowledgeBase pr = KnowledgeBaseFactory.createKnowledgeBase( facts, rules );
		
		// Result: p(3)
		IRelation res = new SimpleRelationFactory().createRelation();
		res.add(BASIC.createTuple(CONCRETE.createInteger(3)));
		
		System.out.println("******** TEST 1: ********");
		ExecutionHelper.executeTest(pr, q, res);
	}
	
	public void testEvaluate2() throws Exception{
		// constructing the rules
		List<IRule> rules = new ArrayList<IRule>(3);
		// p(?X,?Y) :- s(?X,?Y), less(?Y,?X).
		List<ILiteral> h = Arrays.asList(createLiteral("p", "X", "Y"));
		List<ILiteral> b = Arrays.asList(
				createLiteral("s", "X", "Y"),
				Factory.BASIC.createLiteral(true, Factory.BUILTIN.
				createLess(
						TERM.createVariable("Y"),
						TERM.createVariable("X"))));
		
		IRule r = Factory.BASIC.createRule(h, b);
		rules.add(r);

		// create facts
		Map<IPredicate, IRelation> facts = new HashMap<IPredicate, IRelation>();
		// s(1,1), s(9,2), s(2,9)
		IPredicate p = Factory.BASIC.createPredicate("s", 2);
		IRelation rel = new SimpleRelationFactory().createRelation();
		rel.add(BASIC.createTuple(CONCRETE.createInteger(1),CONCRETE.createInteger(1)));
		rel.add(BASIC.createTuple(CONCRETE.createInteger(9),CONCRETE.createInteger(2)));
		rel.add(BASIC.createTuple(CONCRETE.createInteger(2),CONCRETE.createInteger(9)));
		facts.put(p, rel);

		IQuery q = Factory.BASIC.createQuery(createLiteral("p", "X", "Y"));
		Set<IQuery> queries = new HashSet<IQuery>(1);
		queries.add(q);
		final IKnowledgeBase pr = KnowledgeBaseFactory.createKnowledgeBase( facts, rules );
		
		// Result: p(9,2)
		IRelation res = new SimpleRelationFactory().createRelation();
		res.add(BASIC.createTuple(CONCRETE.createInteger(9),CONCRETE.createInteger(2)));
		
		System.out.println("******** TEST 2: ********");
		ExecutionHelper.executeTest(pr, q, res);
	}
	
	public void testEvaluate3() throws Exception{
		// constructing the rules
		List<IRule> rules = new ArrayList<IRule>(3);
		// p(?X,?Y) :- s(?X,?Y), less(?X,?Y).
		List<ILiteral> h = Arrays.asList(createLiteral("p", "X", "Y"));
		List<ILiteral> b = Arrays.asList(
				createLiteral("s", "X", "Y"),
				Factory.BASIC.createLiteral(true, Factory.BUILTIN.
				createLess(
						TERM.createVariable("X"),
						TERM.createVariable("Y"))));
		
		IRule r = Factory.BASIC.createRule(h, b);
		rules.add(r);

		// create facts
		Map<IPredicate, IRelation> facts = new HashMap<IPredicate, IRelation>();
		// s(1,1), s(9,2), s(2,9)
		IPredicate p = Factory.BASIC.createPredicate("s", 2);
		IRelation rel = new SimpleRelationFactory().createRelation();
		rel.add(BASIC.createTuple(BASIC.createTuple(CONCRETE.createDate(2000, 5, 10), CONCRETE.createDate(2000, 5, 12))));
		rel.add(BASIC.createTuple(BASIC.createTuple(CONCRETE.createDate(2001, 5, 10), CONCRETE.createDate(2000, 5, 10))));
		rel.add(BASIC.createTuple(BASIC.createTuple(CONCRETE.createDate(2000, 4, 10), CONCRETE.createDate(2000, 5, 12))));
		facts.put(p, rel);

		IQuery q = Factory.BASIC.createQuery(createLiteral("p", "X", "Y"));
		Set<IQuery> queries = new HashSet<IQuery>(1);
		queries.add(q);
		final IKnowledgeBase pr = KnowledgeBaseFactory.createKnowledgeBase( facts, rules );
		
		// Result: p =
		// (org.deri.iris.terms.concrete.DateTerm[year=2000,month=4,day=5], org.deri.iris.terms.concrete.DateTerm[year=2000,month=5,day=5])
		// (org.deri.iris.terms.concrete.DateTerm[year=2000,month=5,day=5], org.deri.iris.terms.concrete.DateTerm[year=2000,month=5,day=5])
		IRelation res = new SimpleRelationFactory().createRelation();
		res.add(BASIC.createTuple(CONCRETE.createDate(2000, 5, 10), CONCRETE.createDate(2000, 5, 12)));
		res.add(BASIC.createTuple(CONCRETE.createDate(2000, 4, 10), CONCRETE.createDate(2000, 5, 12)));
		
		System.out.println("******** TEST 3: ********");
		ExecutionHelper.executeTest(pr, q, res);
	}
	
	/**
	 * Creates a positive literal out of a predicate name and a set of variable
	 * strings.
	 * 
	 * @param pred
	 *            the predicate name
	 * @param vars
	 *            the variable names
	 * @return the constructed literal
	 * @throws NullPointerException
	 *             if the predicate name or the set of variable names is
	 *             {@code null}
	 * @throws NullPointerException
	 *             if the set of variable names contains {@code null}
	 * @throws IllegalArgumentException
	 *             if the name of the predicate is 0 characters long
	 */
	private static ILiteral createLiteral(final String pred,
			final String... vars) {
		if ((pred == null) || (vars == null)) {
			throw new NullPointerException(
					"The predicate and the vars must not be null");
		}
		if (pred.length() <= 0) {
			throw new IllegalArgumentException(
					"The predicate name must be longer than 0 chars");
		}
		if (Arrays.asList(vars).contains(null)) {
			throw new NullPointerException("The vars must not contain null");
		}

		return BASIC.createLiteral(true, BASIC.createPredicate(pred,
				vars.length), BASIC.createTuple(new ArrayList<ITerm>(
				createVarList(vars))));
	}

	/**
	 * Creates a list of IVariables out of a list of strings.
	 * 
	 * @param vars
	 *            the variable names
	 * @return the list of correspoinding variables
	 * @throws NullPointerException
	 *             if the vars is null, or contains null
	 */
	private static List<IVariable> createVarList(final String... vars) {
		if ((vars == null) || Arrays.asList(vars).contains(null)) {
			throw new NullPointerException(
					"The vars must not be null and must not contain null");
		}
		final List<IVariable> v = new ArrayList<IVariable>(vars.length);
		for (final String var : vars) {
			v.add(TERM.createVariable(var));
		}
		return v;
	}
}
