/**
 * 
 */
package eu.larkc.iris.rules.compiler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.lang.NotImplementedException;
import org.deri.iris.Configuration;
import org.deri.iris.EvaluationException;
import org.deri.iris.api.basics.IAtom;
import org.deri.iris.api.basics.ILiteral;
import org.deri.iris.api.basics.IPredicate;
import org.deri.iris.api.basics.IRule;
import org.deri.iris.api.basics.ITuple;
import org.deri.iris.api.builtins.IBuiltinAtom;
import org.deri.iris.api.terms.IConstructedTerm;
import org.deri.iris.api.terms.ITerm;
import org.deri.iris.api.terms.IVariable;
import org.deri.iris.facts.IFacts;
import org.deri.iris.rules.compiler.ICompiledRule;
import org.deri.iris.rules.compiler.IRuleCompiler;
import org.deri.iris.rules.compiler.Utils;
import org.deri.iris.utils.TermMatchingAndSubstitution;

import cascading.flow.Flow;
import cascading.flow.FlowConnector;
import cascading.flow.PlannerException;
import cascading.operation.Debug;
import cascading.operation.DebugLevel;
import cascading.pipe.CoGroup;
import cascading.pipe.Each;
import cascading.pipe.Pipe;
import cascading.pipe.cogroup.InnerJoin;
import cascading.scheme.TextDelimited;
import cascading.scheme.TextLine;
import cascading.tap.Hfs;
import cascading.tap.SinkMode;
import cascading.tap.Tap;
import cascading.tuple.Fields;
import eu.larkc.iris.evaluation.distributed.ConstantFilter;
import eu.larkc.iris.storage.FactsFactory;

/**
 * 
 * Knows about facts to set up taps for now. This is non-finally code and not
 * functional yet.
 * 
 * @history Oct 1, 2010, fisf, creation
 * @author Florian Fischer
 */
public class CascadingRuleCompiler implements IRuleCompiler {

	/**
	 * Sets up a CascadingRuleCompiler with a specific configuration, taking
	 * relevant datasources for rule execution into account.
	 * 
	 * @param configuration
	 * @param facts
	 */
	public CascadingRuleCompiler(Configuration configuration, IFacts facts) {
		// this.mFacts = facts;
		this.mConfiguration = configuration;
	}

	/**
	 * Compiles an individual rule to a Cascading Flow. TODO (fisf,
	 * optimization): Maybe this should only be compiled to an pipe assembly so
	 * that taps are bound at a later stage, allowing more optimizations.
	 * 
	 * @param rule
	 * @return
	 * @throws EvaluationException
	 */
	public ICompiledRule compile(IRule rule) throws EvaluationException {

		List<ILiteral> body = rule.getBody();
		Pipe bodyPipe = compileBody(body);

		// tell the planner remove all Debug operations
		Properties properties = new Properties();
		FlowConnector.setDebugLevel(properties, DebugLevel.NONE);

		Flow compiledCascadingRule = attachTaps(bodyPipe, rule);

		IPredicate headPredicate = rule.getHead().get(0).getAtom()
				.getPredicate();

		return new CascadingCompiledRule(headPredicate, compiledCascadingRule,
				mConfiguration);
	}

	/**
	 * In this function we have to actually compile the body. For that purpose
	 * we iterate over all the literals in the body and chain pipes accordingly.
	 * We then have to handle several cases: 1.) We encounter a built-in. This
	 * needs to be mapped to a suitable built-in on the cascading side. 2.) We
	 * encounter constants, e.g. p(?x, 1). In that case we want to install a
	 * filter on the pipe. This is accomplished with cascadings EACH and FILTER
	 * primitives. 3.) we encounter a join, e.g.: p(?x,?y), r(?y,?z) would for
	 * example require a join of the two pipes on ?y. This also entails renaming
	 * fields. For details see
	 * http://www.cascading.org/1.2/userguide/html/ch03s02.html#N2035D
	 * 
	 * @param bodyLiterals
	 * @return
	 */
	protected Pipe compileBody(Collection<ILiteral> bodyLiterals) {
		List<ILiteral> literals = new ArrayList<ILiteral>(bodyLiterals);
		Map<IAtom, Pipe> subGoals = new HashMap<IAtom, Pipe>();

		Pipe result;

		for (int l = 0; l < literals.size(); ++l) {
			ILiteral literal = literals.get(l);
			// we shouldn't even have to check for that if we do not deal
			// with negation, this is basically a double check for the
			// parser
			if (!literal.isPositive()) {
				throw new IllegalArgumentException(
						"Negation is not supported: " + literal);
			}

			IAtom atom = literal.getAtom(); // get predicate and tuple
			// (variables and constants)
			if (atom instanceof IBuiltinAtom) {
				processBuiltin((IBuiltinAtom) atom);
			} else {
				// construct pipe assembly, one pipe per atom
				IPredicate predicate = atom.getPredicate();

				// IRelation relation = mFacts.get(predicate);
				// ITuple viewCriteria = atom.getTuple();

				Pipe pipe = new Pipe(predicate.getPredicateSymbol());
				ITuple tuple = atom.getTuple();
				// filter for constants
				pipe = filterConstants(pipe, tuple);
				// at this point the basic information for a subgoal has been
				// process and filters for constants are completely set up, only
				// joins left
				subGoals.put(atom, pipe);
			}
		}

		result = setupJoins(subGoals);

		return result;
	}

	/**
	 * Optimization: When joining two streams via a CoGroup Pipe, attempt to
	 * place the largest of the streams in the left most argument to the
	 * CoGroup. Joining multiple streams requires some accumulation of values
	 * before the join operator can begin, but the left most stream will not be
	 * accumulated. This should improve the performance of most joins.
	 * 
	 * The basic algorithm works as following; <code>
	 * Iterate over all subgoals
	 * 	For each subgoal keep track of its output variables and the associated pipe
	 * 		For each subgoal after the first one, inspect previous variables
	 * 			If there is an overlap in variables, construct indices,
	 * 				get hold of the corresponding pipe and construct CoGroup (join) with current element,
	 * 				based on indices
	 * 				Construct new set of output variables based on remaining indices
	 * 				Set previous pipe to new CoGroup pipe
	 * </code> This is basically a standard textbook algorithm, apart from the
	 * fact that a special Cascading element needs to be constructed to perform
	 * the join in parallel, and that cascading pipes/tuple streams replace the
	 * standard notion of a relation.
	 * 
	 * @param subGoals
	 * @return
	 */
	protected Pipe setupJoins(Map<IAtom, Pipe> subGoals) {

		List<IVariable> previousVariables = null;
		Pipe previousPipe = null;

		Set<Entry<IAtom, Pipe>> entries = subGoals.entrySet();
		Iterator<Entry<IAtom, Pipe>> it = entries.iterator();

		// first element
		if (it.hasNext()) {
			Entry<IAtom, Pipe> firstEntry = it.next();
			IAtom atom = firstEntry.getKey();
			List<IVariable> variables = TermMatchingAndSubstitution
					.getVariables(atom.getTuple(), true);
			previousVariables = variables;
			previousPipe = firstEntry.getValue();
			// debugging, this can be removed through the flowplanner for
			// production use
			previousPipe = new Each(previousPipe, DebugLevel.VERBOSE,
					new Debug());
		} else {
			throw new IllegalArgumentException(
					"Cannot setup joins with no subgoals.");
		}

		// main loop
		while (it.hasNext()) {
			Entry<IAtom, Pipe> entry = it.next();

			IAtom atom = entry.getKey();
			Pipe pipe = entry.getValue();
			// debugging, this can be removed through the flowplanner for
			// production use
			pipe = new Each(pipe, DebugLevel.VERBOSE, new Debug());

			List<IVariable> variables = TermMatchingAndSubstitution
					.getVariables(atom.getTuple(), true);

			// computation of indices, see old IRIS code
			List<Integer> join1 = new ArrayList<Integer>();
			List<Integer> join2 = new ArrayList<Integer>();

			for (int i1 = 0; i1 < previousVariables.size(); ++i1) {
				IVariable var1 = previousVariables.get(i1);

				for (int i2 = 0; i2 < variables.size(); ++i2) {
					IVariable var2 = variables.get(i2);

					if (var1.equals(var2)) {
						join1.add(i1);
						join2.add(i2);

						// variables unique
						break;
					}
				}
			}

			Integer[] joinIndicesPreviousElement = new Integer[join1.size()];
			joinIndicesPreviousElement = join1
					.toArray(joinIndicesPreviousElement);

			Integer[] joinIndicesThisElement = new Integer[join2.size()];
			joinIndicesThisElement = join2.toArray(joinIndicesThisElement);

			// TODO: There should be an optimization later on, in case no real
			// join is required.
			// This is the most obvious point of optimization now and needs to
			// be tested.

			// cascading
			Fields lhsFields = new Fields(joinIndicesPreviousElement);
			Fields rhsFields = new Fields(joinIndicesThisElement);

			//renaming of variables - cascading does not allow duplicate fieldnames
			//for now we only append 1...N
			//practically we only work with indices and do not care about field names anyway
			String[] vars = new String[previousVariables.size() + variables.size()];
			int k = 0;
			
			for(IVariable var : variables) {
				//actually it would be more descriptive if the suffix that we use here would
				//e.g. be the name of the predicate
				vars[k] = variables.get(k).getValue() + "1";
				k++;
			}
			int offset = k;
			for(IVariable var : previousVariables) {
				vars[k] = previousVariables.get(k-offset).getValue() + "2";
				k++;
			}
			
			Fields declared = new Fields(vars );
			Pipe join = new CoGroup( previousPipe, lhsFields, pipe, rhsFields, declared, new InnerJoin() );
			
			// debugging, this can be removed through the flowplanner for
			// production use
			join = new Each(join, DebugLevel.VERBOSE, new Debug());

			previousPipe = join;

			// Now find the indices of variables that are not used in the
			// join
			List<Integer> remainder1 = new ArrayList<Integer>();
			List<Integer> remainder2 = new ArrayList<Integer>();

			for (int i1 = 0; i1 < previousVariables.size(); ++i1) {
				if (!join1.contains(i1))
					remainder1.add(i1);
			}

			for (int i2 = 0; i2 < variables.size(); ++i2) {
				if (!join2.contains(i2))
					remainder2.add(i2);
			}

			int[] remainderIndicesPreviousElement = Utils
					.integerListToArray(remainder1);
			int[] remainderIndicesThisElement = Utils
					.integerListToArray(remainder2);

			// Lastly, build the list of output variables
			ArrayList<IVariable> newPreviousVariables = new ArrayList<IVariable>();

			for (int i : joinIndicesPreviousElement)
				newPreviousVariables.add(previousVariables.get(i));
			for (int i : remainderIndicesPreviousElement)
				newPreviousVariables.add(previousVariables.get(i));
			for (int i : remainderIndicesThisElement)
				newPreviousVariables.add(variables.get(i));

			previousVariables = newPreviousVariables;
		}

		return previousPipe;
	}

	/**
	 * This filters constants by providing in tuple streams according to the
	 * original rule defintion.
	 * 
	 * @param attachTo
	 * @param tuple
	 * @return
	 */
	protected Pipe filterConstants(Pipe attachTo, ITuple tuple) {

		Map<Integer, Object> constantTerms = new HashMap<Integer, Object>();

		for (int i = 0; i < tuple.size(); i++) {
			ITerm term = tuple.get(i);

			// not a variable, we filter the tuples
			if (term.isGround()) {
				constantTerms.put(i, term.getValue());
			}
		}

		// did we actually find at least one constant?
		if (!constantTerms.isEmpty()) {
			Pipe filter = new Each(attachTo, new ConstantFilter(constantTerms));
			return filter;
		}

		// nothing changed
		return attachTo;
	}

	/**
	 * Converts a built-in atom to a suitable cascading operation per the RIF
	 * specs. This is currently a stub implementation.
	 * 
	 * @param atom
	 */
	protected void processBuiltin(IBuiltinAtom atom) {

		boolean constructedTerms = false;
		for (ITerm term : atom.getTuple()) {
			if (term instanceof IConstructedTerm) {
				constructedTerms = true;
				break;
			}
		}
		// TODO fisf: construct appropriate cascading operation
		// once they are supported
		if (constructedTerms) {
			// function symbol
			throw new NotImplementedException(
					"Function Symbols are not supported");
		} else {
			// ordinary built-in, those WILL be handled
			throw new NotImplementedException("Builtins not implemented yet");
		}
	}

	/**
	 * Attaches taps to the compiled rule, so that it can access external data
	 * sources and be evaluated. This method could in theory also be moved to
	 * another place outside of rule compilation, e.g. a custom evaluator
	 * implementation.
	 * 
	 * @param rulePipe
	 * @param originalRule
	 * @return
	 */
	protected Flow attachTaps(Pipe rulePipe, IRule originalRule) {

		Map<String, Tap> sources = new HashMap<String, Tap>();
		
		List<ILiteral> lits = originalRule.getBody();
		for (ILiteral literal : lits) {
			IAtom atom = literal.getAtom();
			Tap tap = mFacts.getFacts(atom);
			sources.put(atom.getPredicate().getPredicateSymbol(), tap);
		}

		List<ILiteral> head = originalRule.getHead();
		// Only one literal in head
		if (head.size() != 1) {
			throw new IllegalArgumentException(
					"Input rule has more than two literals in head. Setup IRIS' optimizations correctly. Rule: "
							+ originalRule.toString());
		}

		// TODO: here we could filter/do projection for the rule head. this is
		// not too important for now.
		// For that purpose we need 1.) take a look at the variables in the
		// head, 2.) determine the indices they have after the body has been
		// compiled
		// For now (debugging, testing) we return the complete result
		// bodyPipe = new Each( bodyPipe, new Fields(1, 2, 3), new Identity(),
		// Fields.RESULTS );

		//this should go by ordinal
		//IAtom headAtom = head.get(0).getAtom();
		//Tap headSink = mFacts.getFacts(headAtom);
		Tap headSink = mFacts.getFacts();
		
		Flow flow = new FlowConnector().connect(originalRule.toString(),
					sources, headSink, rulePipe);
		
		if(flow != null) {
			flow.writeDOT(rulePipe.getName() + ".dot");
		}

		return flow;
	}

	/**
	 * The knowledge-base facts used to attach to the compiled rule elements.
	 * This keeps encapsulates the access to external datasources and results in
	 * corresponding Cascading Taps.
	 */
	private final FactsFactory mFacts = FactsFactory.getInstance("default");

	/**
	 * Central configuration object
	 */
	private final Configuration mConfiguration;
}