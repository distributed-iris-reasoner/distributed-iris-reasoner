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
package org.deri.iris.querycontainment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.deri.iris.EvaluationException;
import org.deri.iris.KnowledgeBaseFactory;
import org.deri.iris.api.IKnowledgeBase;
import org.deri.iris.api.basics.IAtom;
import org.deri.iris.api.basics.ILiteral;
import org.deri.iris.api.basics.IPredicate;
import org.deri.iris.api.basics.IQuery;
import org.deri.iris.api.basics.IRule;
import org.deri.iris.api.querycontainment.IQueryContainment;
import org.deri.iris.api.terms.ITerm;
import org.deri.iris.api.terms.IVariable;
import org.deri.iris.factory.Factory;
import org.deri.iris.storage.IRelation;
import org.deri.iris.storage.simple.SimpleRelationFactory;

/**
 * <p>
 * Checks two queries for query containment, based on a given 
 * knowledge base.
 * </p>
 * 
 * @author Nathalie Steinmetz, DERI Innsbruck
 */
public class QueryContainment implements IQueryContainment {

	private final List<IRule> mRules;
	
	/** The substitution resulting from the query evaluation */
	private IRelation result;
	
	/** The query variable bindings */
	private List<IVariable> mVariableBindings;
	
	/** Namespace containing the constants that are used to replace variables */
//	private final String namespace = "http://queryContainment/constants#";
	
	/**
	 * <p>
	 * Creates a new query containment checker with a given knowledge base.
	 * </p>
	 * 
	 * @param program
	 *            The program
	 * @throws IllegalArgumentException
	 *             If the program or the evaluator is {@code null}, or
	 *             If the program contains queries
	 */
	public QueryContainment( List<IRule> rules ) {
		// Make a knowledge base with the given rules.
		mRules = rules;
	}

	public QueryContainment( IKnowledgeBase kb ) {
		if( kb == null )
			throw new IllegalArgumentException( "Parameter 'kb' must not be null in QueryContainment constructor." );
		
		// Make a knowledge base with the given rules.
		mRules = new ArrayList<IRule>( kb.getRules() );
	}

	/*
	 * We use the Frozen Fact Algorithm for checking query containment. The 
	 * algorithm can be summarized as follows:
	 * Input: query1 and query2.
	 * Output: true if query1 is contained within query2, false otherwise.
	 * Algorithm: we 'freeze' query1 by substituting each of its variables 
	 * with fresh constants. Then we add this 'frozen' query to the knowledge 
	 * base and evaluate query2. If the result contains the frozen terms, 
	 * query1 is contained within query2. Otherwise we get an empty result. 
	 * 
	 * (non-Javadoc)
	 * @see org.deri.iris.api.IQueryContainment#checkQueryContainment(org.deri.iris.api.basics.IQuery, org.deri.iris.api.basics.IQuery)
	 */
	public boolean checkQueryContainment(IQuery query1, IQuery query2) throws EvaluationException 
	{
		if (query1 == null || query2 == null) {
			throw new IllegalArgumentException(
					"The two queries must not be null");
		}

		// query 1 is 'frozen' in to facts
		Map<IPredicate, IRelation> facts = new HashMap<IPredicate, IRelation>();
		freezeQuery( query1, facts );
		
		// Create the knowledge-base with these facts
		IKnowledgeBase knowledgebase = KnowledgeBaseFactory.createKnowledgeBase( facts, mRules );

		// run query 2 against the knowledge base to be evaluated
		mVariableBindings = new ArrayList<IVariable>();
		result = knowledgebase.execute( query2, mVariableBindings );
		
		return result.size() > 0;
	}
	
	public IRelation getContainmentMappings() {
		return result;
	}
	
	public List<IVariable> getVariableBindings()
	{
		return mVariableBindings;
	}

	
//	/*
//	 * Method to substitute the variables in query1 by constants
//	 */
//	private void freezeQuery(IQuery query, Map<IPredicate, IRelation> facts) {
//		Map<IVariable, ITerm> variableMapping = new HashMap<IVariable, ITerm>();
//		IAtom atom = null;
//		List<ITerm> terms = new Vector<ITerm>();
//		for (ILiteral literal : query.getLiterals()) {
//			
//			// build mapping for all variables
//			for (IVariable variable : literal.getAtom().getTuple().getVariables()) {
//				if (! variableMapping.containsKey(variable))
//					variableMapping.put(variable, Factory.CONCRETE.createIri(
//								namespace + new java.rmi.dgc.VMID().toString()));
//			}
//			
//			// build 'frozen' Atom by actually substituting the variables
//			for (ITerm term : literal.getAtom().getTuple()) {
//				if (term instanceof IVariable)
//					terms.add(variableMapping.get((IVariable) term));
//				else
//					terms.add(term);
//			}
//			atom = Factory.BASIC.createAtom(literal.getAtom().getPredicate(), 
//					Factory.BASIC.createTuple(terms));
//			IRelation relation = facts.get( atom.getPredicate() );
//			
//			if( relation == null )
//			{
//				relation = new SimpleRelation( true );
//				facts.put( atom.getPredicate(), relation );
//			}
//			
//			relation.add( atom.getTuple());
//		}
//	}

	/*
	 * Method to substitute the variables in query1 by constants
	 */
	private void freezeQuery(IQuery query, Map<IPredicate, IRelation> facts) {
		for (ILiteral literal : query.getLiterals()) {
			List<ITerm> terms = new Vector<ITerm>();
			// build 'frozen' Atom by actually substituting the variables
			for (ITerm term : literal.getAtom().getTuple()) {
				if (term instanceof IVariable)
					terms.add(Factory.TERM.createString( "FROZEN_VARIABLE_"+ ((IVariable) term).getValue() ) );
				else
					terms.add(term);
			}
			IAtom atom = Factory.BASIC.createAtom(literal.getAtom().getPredicate(), 
					Factory.BASIC.createTuple(terms));
			IRelation relation = facts.get( atom.getPredicate() );
			
			if( relation == null )
			{
				relation = new SimpleRelationFactory().createRelation();
				facts.put( atom.getPredicate(), relation );
			}
			
			relation.add( atom.getTuple());
		}
	}
}
