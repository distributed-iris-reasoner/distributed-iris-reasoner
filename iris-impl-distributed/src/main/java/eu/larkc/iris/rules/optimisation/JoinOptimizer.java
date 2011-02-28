/*
 * Copyright 2010 Softgress - http://www.softgress.com/
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package eu.larkc.iris.rules.optimisation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.deri.iris.api.basics.IAtom;
import org.deri.iris.api.basics.ILiteral;
import org.deri.iris.api.basics.IRule;
import org.deri.iris.api.basics.ITuple;
import org.deri.iris.api.terms.IVariable;
import org.deri.iris.basics.BasicFactory;
import org.deri.iris.rules.IRuleOptimiser;
import org.deri.iris.utils.TermMatchingAndSubstitution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * It does a re-ordering of the rule body literals and find the best way to join the literals.
 * The main optimization is avoiding the cartesian joins.
 * The literals with the more common variables are set first in the body.
 * TODO for predicate indexing environment set to the left of the body the literals with smaller amount of records, 
 * to filter the date from the first joins. This should not be done by breaking the first two optimizations.
 * 
 * @author valer.roman@softgress
 *
 */
public class JoinOptimizer implements IRuleOptimiser {

	private static final Logger logger = LoggerFactory.getLogger(JoinOptimizer.class);
	
	private List<ILiteral> literals = new ArrayList<ILiteral>();
	
	private class LiteralsList extends ArrayList<Literals> {

		/**
		 * serialVersionUID
		 */
		private static final long serialVersionUID = -6262585543568013544L;

		public LiteralsList() {	
		}
		
		public LiteralsList(int size) {
			for (int i = 0; i < size; i++) {
				add(new Literals());
			}
		}
	}
	
	private class Literals extends ArrayList<ILiteral> implements Comparable<Literals> {
		/**
		 * serialVersionUID
		 */
		private static final long serialVersionUID = 182805402714142508L;
		
		private Integer score = 0;
		
		public Literals() {
			super();
		}
		
		public Literals(Collection<ILiteral> collection) {
			addAll(collection);
		}
		
		/* (non-Javadoc)
		 * @see java.util.AbstractCollection#toString()
		 */
		@Override
		public String toString() {
			return "score:" + score + super.toString();
		}

		/* (non-Javadoc)
		 * @see java.lang.Comparable#compareTo(java.lang.Object)
		 */
		@Override
		public int compareTo(Literals o) {
			return -(score.compareTo(o.score));
		}
		
	}

	private List<IVariable> commonVariables(List<IVariable> variables1, List<IVariable> variables2) {
		List<IVariable> result = new ArrayList<IVariable>();
		for (int i = 0; i < variables1.size(); i++) {
			IVariable variable1 = variables1.get(i);
			if (variables2.contains(variable1)) {
				result.add(variable1);
			}
		}
		return result;
	}

	private LiteralsList findPath(int depth, ILiteral literal, List<ILiteral> processedLiterals) {
		List<IVariable> variables = new ArrayList<IVariable>();
		List<ILiteral> aProcessedLiterals = new ArrayList<ILiteral>(processedLiterals);
		if (literal != null) {
			variables = TermMatchingAndSubstitution.getVariables(literal.getAtom().getTuple(), true);
			aProcessedLiterals.add(literal);
		}
		
		LiteralsList theLiteralsList = new LiteralsList();
		
		boolean hadLiteralsToProcess = false;
		Literals theLiterals = new Literals(literals);
		theLiterals.removeAll(aProcessedLiterals);
		for (Iterator<ILiteral> literalIterator = theLiterals.iterator(); literalIterator.hasNext();) {
			int aDepth = depth;
			ILiteral aLiteral = literalIterator.next();
			List<Literals> literalsList = null;
			int aScore = 0;
			if (literal == null) {
				literalsList = findPath(aDepth - 1, aLiteral, aProcessedLiterals);
			} else {
				IAtom aAtom = aLiteral.getAtom();
				ITuple aTuple = aAtom.getTuple();
				
				List<IVariable> aVariables = TermMatchingAndSubstitution.getVariables(aTuple, true);
				
				List<IVariable> commonVariables = commonVariables(variables, aVariables);
				
				int commonVariablesNb = commonVariables.size();
				
				aScore += commonVariablesNb > 0 ? (commonVariablesNb * depth) : -depth;
				
				literalsList = findPath(aDepth - 1, aLiteral, aProcessedLiterals);
			}
			for (Literals literals : literalsList) {
				logger.info("depth : " + aDepth + ", processed literals : " + aProcessedLiterals + ", literal : " + aLiteral + ", score : " + aScore + ", total score : " + (literals.score + aScore));
				literals.add(0, aLiteral);
				literals.score += aScore;
			}
			
			theLiteralsList.addAll(literalsList);
			
			hadLiteralsToProcess = !literalsList.isEmpty();
		}
		if (!hadLiteralsToProcess) {
			return new LiteralsList(1);
		} else {
			Collections.sort(theLiteralsList);
			return theLiteralsList;
		}
	}
	
	/* (non-Javadoc)
	 * @see org.deri.iris.rules.IRuleOptimiser#optimise(org.deri.iris.api.basics.IRule)
	 */
	@Override
	public IRule optimise(IRule rule) {
		logger.info("optimize rule : " + rule);
		
		IRule aRule = null;
		literals = rule.getBody();
		List<Literals> resultLiterals = findPath(literals.size(), null, new ArrayList<ILiteral>());
		if (!resultLiterals.isEmpty()) {
			logger.info("results : " + resultLiterals);
			//TODO pick from the to score rules the one having on the left side the predicates with 
			//fewer counts to filter most of the data from the beginning of the rule evaluation
			aRule = BasicFactory.getInstance().createRule(rule.getHead(), resultLiterals.get(0));
		}

		logger.info("optimized rule : " + aRule);
		
		return aRule;
	}

}
