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
 * Tests for the {@code MultiplyBuiltin Evaluation} coupled with LessBuiltin.
 * </p>
 * 
 * @author Darko Anicic, DERI Innsbruck
 * @date   10.05.2007 10:23:07
 */
public class MultiplyBuiltinEvaluationTest extends TestCase {

	public static Test suite() {
		return new TestSuite(MultiplyBuiltinEvaluationTest.class, MultiplyBuiltinEvaluationTest.class
				.getSimpleName());
	}

	public void testEvaluate0() throws Exception{
		// constructing the rules
		List<IRule> rules = new ArrayList<IRule>(1);
		// p(?S,?K) :- p1(?S,?I), less(?I, 10), multiply(?I,?I,?K), p2(?S).
		List<ILiteral> h = Arrays.asList(createLiteral("p", "S", "K"));
		List<ILiteral> b = Arrays.asList(
				createLiteral("p1", "S", "I"),
				Factory.BASIC.createLiteral(true, Factory.BUILTIN.
				createLess(
						TERM.createVariable("I"),
						CONCRETE.createInteger(10))),
				Factory.BASIC.createLiteral(true, Factory.BUILTIN.
						createMultiplyBuiltin(
								TERM.createVariable("I"),
								TERM.createVariable("I"),
								TERM.createVariable("K"))),
				createLiteral("p2", "S"));
		
		IRule r = Factory.BASIC.createRule(h, b);
		rules.add(r);

		// create facts
		Map<IPredicate, IRelation> facts = new HashMap<IPredicate, IRelation>();
		// p1(a,1), p1(b,2), p1(c,3), p1(d,12)
		IPredicate p = Factory.BASIC.createPredicate("p1", 2);
		IRelation rel = new SimpleRelationFactory().createRelation();
		rel.add(BASIC.createTuple(TERM.createString("a"), CONCRETE.createInteger(1)));
		rel.add(BASIC.createTuple(TERM.createString("b"), CONCRETE.createInteger(2)));
		rel.add(BASIC.createTuple(TERM.createString("c"), CONCRETE.createInteger(3)));
		rel.add(BASIC.createTuple(TERM.createString("d"), CONCRETE.createInteger(12)));
		facts.put(p, rel);

		// p2(b)
		p = Factory.BASIC.createPredicate("p2", 1);
		rel = new SimpleRelationFactory().createRelation();
		rel.add(BASIC.createTuple(TERM.createString("b")));
		facts.put(p, rel);
		
		// p(?S,?K)
		IQuery q = Factory.BASIC.createQuery(createLiteral("p", "S", "K"));
		Set<IQuery> queries = new HashSet<IQuery>(1);
		queries.add(q);
		final IKnowledgeBase pr = KnowledgeBaseFactory.createKnowledgeBase( facts, rules );
		
		// Result: p(b,4)
		IRelation res = new SimpleRelationFactory().createRelation();
		res.add(BASIC.createTuple(TERM.createString("b"), CONCRETE.createInteger(4)));
		
		System.out.println("******** TEST 0: ********");
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
