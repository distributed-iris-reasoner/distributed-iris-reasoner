/**
 * 
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
import org.deri.iris.rules.IRuleOptimiser;
import org.deri.iris.utils.TermMatchingAndSubstitution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author valer
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
				literalsList = findPath(++aDepth, aLiteral, aProcessedLiterals);
			} else {
				IAtom aAtom = aLiteral.getAtom();
				ITuple aTuple = aAtom.getTuple();
				
				List<IVariable> aVariables = TermMatchingAndSubstitution.getVariables(aTuple, true);
				
				List<IVariable> commonVariables = commonVariables(variables, aVariables);
				
				int commonVariablesNb = commonVariables.size();
				
				aScore += commonVariablesNb > 0 ? (commonVariablesNb * depth) : -depth;
				
				literalsList = findPath(++aDepth, aLiteral, aProcessedLiterals);
			}
			for (Literals literals : literalsList) {
				logger.info("depth : " + aDepth + " add : " + aLiteral + " score : " + aScore);
				literals.add(aLiteral);
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
		literals = rule.getBody();
		List<Literals> resultLiterals = findPath(0, null, new ArrayList<ILiteral>());
		if (!resultLiterals.isEmpty()) {
			rule.getBody().clear();
			rule.getBody().addAll(resultLiterals.get(0));
		}
		
		return rule;
	}

}
