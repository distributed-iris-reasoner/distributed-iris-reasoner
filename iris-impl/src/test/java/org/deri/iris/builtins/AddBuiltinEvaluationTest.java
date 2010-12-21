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
import java.util.List;
import java.util.Map;

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
 * Tests for the {@code AddBuiltin Evaluation}.
 * </p>
 * 
 * @author Darko Anicic, DERI Innsbruck
 * @date   12.04.2007 09:58:07
 */
public class AddBuiltinEvaluationTest extends TestCase {

	public static Test suite() {
		return new TestSuite(AddBuiltinEvaluationTest.class, AddBuiltinEvaluationTest.class
				.getSimpleName());
	}

	public void testEvaluate0() throws Exception{
		// constructing the rules
		List<IRule> rules = new ArrayList<IRule>(3);
		// p(X) :- r(X)
		IRule r = Factory.BASIC.createRule(Arrays.asList(createLiteral(
				"p", "X")), Arrays.asList(createLiteral("r", "X")));
		rules.add(r);
		// p(X) :- s(X), add(3, 4, X)
		List<ILiteral> h = Arrays.asList(createLiteral("p", "X"));
		List<ILiteral> b = Arrays.asList(createLiteral("s", "X"),
				Factory.BASIC.createLiteral(true, Factory.BUILTIN.
						createAddBuiltin(
								CONCRETE.createInteger(3),
								CONCRETE.createInteger(4),
								TERM.createVariable("X"))));
		
		r = Factory.BASIC.createRule(h, b);
		rules.add(r);

		// create facts
		Map<IPredicate, IRelation> facts = new HashMap<IPredicate, IRelation>();
		// s(3), s(7), s(11), s(15)
		IPredicate p = Factory.BASIC.createPredicate("s", 1);
		IRelation rel = new SimpleRelationFactory().createRelation();

		rel.add(BASIC.createTuple(CONCRETE.createInteger(3)));
		rel.add(BASIC.createTuple(CONCRETE.createInteger(7)));
		rel.add(BASIC.createTuple(CONCRETE.createInteger(11)));
		rel.add(BASIC.createTuple(CONCRETE.createInteger(15)));
		facts.put(p, rel);

		// r(1), r(2)
		p = Factory.BASIC.createPredicate("r", 1);
		rel = new SimpleRelationFactory().createRelation();
		rel.add(BASIC.createTuple(CONCRETE.createInteger(1)));
		rel.add(BASIC.createTuple(CONCRETE.createInteger(2)));
		facts.put(p, rel);
		
		IQuery q = Factory.BASIC.createQuery(createLiteral("p", "X"));

		final IKnowledgeBase pr = KnowledgeBaseFactory.createKnowledgeBase( facts, rules );
		
		// Result: p(1), p(2), p(7)
		IRelation res = new SimpleRelationFactory().createRelation();
		res.add(BASIC.createTuple(CONCRETE.createInteger(1)));
		res.add(BASIC.createTuple(CONCRETE.createInteger(2)));
		res.add(BASIC.createTuple(CONCRETE.createInteger(7)));
		
		System.out.println("******** TEST 0: ********");
		ExecutionHelper.executeTest(pr, q, res);
	}
	
	public void testEvaluate1() throws Exception{
		// constructing the rules
		List<IRule> rules = new ArrayList<IRule>(3);
		
		// q(X, Y, Z) :- s(X), p(Y), add(X, Y, Z)
		List<ILiteral> h = Arrays.asList(createLiteral("q", "X", "Y", "Z"));
		List<ILiteral> b = Arrays.asList(
				createLiteral("s", "X"),
				createLiteral("p", "Y"),
				Factory.BASIC.createLiteral(true, Factory.BUILTIN.
						createAddBuiltin(
								TERM.createVariable("X"),
								TERM.createVariable("Y"),
								TERM.createVariable("Z"))));
		
		IRule r = Factory.BASIC.createRule(h, b);
		rules.add(r);

		// create facts
		Map<IPredicate, IRelation> facts = new HashMap<IPredicate, IRelation>();
		// s(3), s(7)
		IPredicate p = Factory.BASIC.createPredicate("s", 1);
		IRelation rel = new SimpleRelationFactory().createRelation();

		rel.add(BASIC.createTuple(CONCRETE.createInteger(3)));
		rel.add(BASIC.createTuple(CONCRETE.createInteger(7)));
		facts.put(p, rel);

		/// p(1), p(2)
		p = Factory.BASIC.createPredicate("p", 1);
		rel = new SimpleRelationFactory().createRelation();
		rel.add(BASIC.createTuple(CONCRETE.createInteger(1)));
		rel.add(BASIC.createTuple(CONCRETE.createInteger(2)));
		facts.put(p, rel);
		
		IQuery q = Factory.BASIC.createQuery(createLiteral("q", "X", "Y", "Z"));

		final IKnowledgeBase pr = KnowledgeBaseFactory.createKnowledgeBase( facts, rules );
		
		// Result: q(3,1,4), q(3,2,5), q(7,1,8), q(7,2,9)
		IRelation res = new SimpleRelationFactory().createRelation();
		res.add(BASIC.createTuple(CONCRETE.createInteger(3),CONCRETE.createInteger(1),CONCRETE.createInteger(4)));
		res.add(BASIC.createTuple(CONCRETE.createInteger(3),CONCRETE.createInteger(2),CONCRETE.createInteger(5)));
		res.add(BASIC.createTuple(CONCRETE.createInteger(7),CONCRETE.createInteger(1),CONCRETE.createInteger(8)));
		res.add(BASIC.createTuple(CONCRETE.createInteger(7),CONCRETE.createInteger(2),CONCRETE.createInteger(9)));
		
		System.out.println("******** TEST 1: ********");
		ExecutionHelper.executeTest(pr, q, res);
	}

	public void testEvaluate2() throws Exception{
		// constructing the rules
		List<IRule> rules = new ArrayList<IRule>(3);
		
		// p(X) :- add(3, 4, X)
		List<ILiteral> h = Arrays.asList(createLiteral("p", "X"));
		List<ILiteral> b = Arrays.asList(
				Factory.BASIC.createLiteral(true, Factory.BUILTIN.
						createAddBuiltin(
								CONCRETE.createInteger(3),
								CONCRETE.createInteger(4),
								TERM.createVariable("X"))));
		
		IRule r = Factory.BASIC.createRule(h, b);
		rules.add(r);

		// create facts
		Map<IPredicate, IRelation> facts = new HashMap<IPredicate, IRelation>();
		// p(3), p(7)
		IPredicate p = Factory.BASIC.createPredicate("p", 1);
		IRelation rel = new SimpleRelationFactory().createRelation();
		rel.add(BASIC.createTuple(CONCRETE.createInteger(3)));
		rel.add(BASIC.createTuple(CONCRETE.createInteger(9)));
		facts.put(p, rel);
		
		IQuery q = Factory.BASIC.createQuery(createLiteral("p", "X"));

		final IKnowledgeBase pr = KnowledgeBaseFactory.createKnowledgeBase( facts, rules );
		
		// Result: p(3), p(7), p(9)
		IRelation res = new SimpleRelationFactory().createRelation();
		res.add(BASIC.createTuple(CONCRETE.createInteger(3)));
		res.add(BASIC.createTuple(CONCRETE.createInteger(7)));
		res.add(BASIC.createTuple(CONCRETE.createInteger(9)));
		
		System.out.println("******** TEST 2: ********");
		ExecutionHelper.executeTest(pr, q, res);
	}
	
	public void testEvaluate3() throws Exception{
		// constructing the rules
		List<IRule> rules = new ArrayList<IRule>(3);
		
		// q(X, Y, Z) :- add(X, 4, Z), s(X), p(Y), add(X, Y, 10)
		List<ILiteral> h = Arrays.asList(createLiteral("q", "X", "Y", "Z"));
		List<ILiteral> b = Arrays.asList(
				Factory.BASIC.createLiteral(true, Factory.BUILTIN.
						createAddBuiltin(
								TERM.createVariable("X"),
								CONCRETE.createInteger(4),
								TERM.createVariable("Z"))),
				createLiteral("s", "X"),
				createLiteral("p", "Y"),
				Factory.BASIC.createLiteral(true, Factory.BUILTIN.
						createAddBuiltin(
								TERM.createVariable("X"),
								TERM.createVariable("Y"),
								CONCRETE.createInteger(10))));
		
		IRule r = Factory.BASIC.createRule(h, b);
		rules.add(r);

		// create facts
		Map<IPredicate, IRelation> facts = new HashMap<IPredicate, IRelation>();
		// s(3), s(6), s(9)
		IPredicate p = Factory.BASIC.createPredicate("s", 1);
		IRelation rel = new SimpleRelationFactory().createRelation();
		rel.add(BASIC.createTuple(CONCRETE.createInteger(3)));
		rel.add(BASIC.createTuple(CONCRETE.createInteger(6)));
		rel.add(BASIC.createTuple(CONCRETE.createInteger(9)));
		facts.put(p, rel);
		
		// p(2), p(4)
		p = Factory.BASIC.createPredicate("p", 1);
		rel = new SimpleRelationFactory().createRelation();
		rel.add(BASIC.createTuple(CONCRETE.createInteger(2)));
		rel.add(BASIC.createTuple(CONCRETE.createInteger(4)));
		facts.put(p, rel);
		
		IQuery q = Factory.BASIC.createQuery(createLiteral("q", "X", "Y", "Z"));

		final IKnowledgeBase pr = KnowledgeBaseFactory.createKnowledgeBase( facts, rules );
		
		// Result: q(6,4,10)
		IRelation res = new SimpleRelationFactory().createRelation();
		res.add(BASIC.createTuple(CONCRETE.createInteger(6),CONCRETE.createInteger(4),CONCRETE.createInteger(10)));
		
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
