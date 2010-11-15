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
import java.util.Map.Entry;
import java.util.Properties;

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
import org.deri.iris.utils.TermMatchingAndSubstitution;

import cascading.flow.Flow;
import cascading.flow.FlowConnector;
import cascading.operation.DebugLevel;
import cascading.operation.Identity;
import cascading.operation.Insert;
import cascading.pipe.CoGroup;
import cascading.pipe.Each;
import cascading.pipe.Pipe;
import cascading.pipe.cogroup.InnerJoin;
import cascading.tap.Tap;
import cascading.tuple.Fields;
import eu.larkc.iris.evaluation.distributed.ConstantFilter;
import eu.larkc.iris.storage.FactsFactory;
import eu.larkc.iris.storage.FieldsVariablesMapping;

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

		FieldsVariablesMapping fieldsVariableMapping = new FieldsVariablesMapping();

		List<ILiteral> body = rule.getBody();
		PipeFielded bodyPipe = compileBody(fieldsVariableMapping, body);

		// tell the planner remove all Debug operations
		Properties properties = new Properties();
		FlowConnector.setDebugLevel(properties, DebugLevel.NONE);

		Flow compiledCascadingRule = attachTaps(fieldsVariableMapping, bodyPipe, rule);

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
	protected PipeFielded compileBody(FieldsVariablesMapping fieldsVariableMapping, Collection<ILiteral> bodyLiterals) {
		List<ILiteral> literals = new ArrayList<ILiteral>(bodyLiterals);
		Map<IAtom, Pipe> subGoals = new HashMap<IAtom, Pipe>();

		PipeFielded result;
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
			
			fieldsVariableMapping.loadAtom(atom);
			
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

		result = setupJoins(fieldsVariableMapping, subGoals);

		return result;
	}
	
	public PipeFielded buildJoin(FieldsVariablesMapping fieldsVariablesMapping, PipeFielded lhsJoin, Iterator<Entry<IAtom, Pipe>> subgoalsIterator) {
		if (!subgoalsIterator.hasNext()) {
			return lhsJoin;
		}
		Entry<IAtom, Pipe> subgoal = subgoalsIterator.next();
		IAtom atom = subgoal.getKey();
		Pipe pipe = subgoal.getValue();
		
		if (lhsJoin == null) {
			PipeFielded pipeFielded = new PipeFielded(fieldsVariablesMapping, pipe, atom);
			return buildJoin(fieldsVariablesMapping, pipeFielded, subgoalsIterator);
		}
		
		IAtom previousAtom = lhsJoin.getAtom();
		List<IVariable> previousVariables = TermMatchingAndSubstitution.getVariables(previousAtom.getTuple(), true);
		List<IVariable> variables = TermMatchingAndSubstitution.getVariables(atom.getTuple(), true);
		Map<String, String> equalityFields = new HashMap<String, String>();
		for (IVariable previousVariable : previousVariables) {
			for (IVariable variable : variables) {
				if (previousVariable.equals(variable)) {
					equalityFields.put(fieldsVariablesMapping.getField(atom, variable), 
							fieldsVariablesMapping.getField(previousAtom, previousVariable));
				}
			}
		}

		Fields lhsFields = new Fields();
		Fields rhsFields = new Fields();
		for (Entry<String, String> equalityEntry : equalityFields.entrySet()) {
			rhsFields = rhsFields.append(new Fields(equalityEntry.getKey()));
			lhsFields = lhsFields.append(new Fields(equalityEntry.getValue()));
		}

		List<String> outputFieldsList = lhsJoin.getFields();
		outputFieldsList.add(atom.getPredicate().getPredicateSymbol());
		for (IVariable variable : variables) {
			String field = fieldsVariablesMapping.getField(atom, variable);
			outputFieldsList.add(field);
		}
		Fields outputFields = new Fields();
		for (String field : outputFieldsList) {
			outputFields = outputFields.append(new Fields(field));
		}

		Pipe join = new CoGroup(lhsJoin.getPipe(), lhsFields, pipe, rhsFields, outputFields, new InnerJoin());
		PipeFielded pipeFielded = new PipeFielded(fieldsVariablesMapping, join, atom, outputFieldsList);
		return buildJoin(fieldsVariablesMapping, pipeFielded, subgoalsIterator);
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
	protected PipeFielded setupJoins(FieldsVariablesMapping fieldsVariablesMapping, Map<IAtom, Pipe> subGoals) {

		if (subGoals.isEmpty()) {
			throw new IllegalArgumentException(
				"Cannot setup joins with no subgoals.");			
		}

		return buildJoin(fieldsVariablesMapping, null, subGoals.entrySet().iterator());
		
		/*
		Pipe join = null;
		Entry<IAtom, Pipe> previousSubgoal = null;
		Fields prevOutputFields = new Fields();
		for (Entry<IAtom, Pipe> subgoal : subGoals.entrySet()) {
			IAtom atom = subgoal.getKey();
			Pipe pipe = subgoal.getValue();
			
			List<IVariable> variables = TermMatchingAndSubstitution.getVariables(atom.getTuple(), true);
			
			if (previousSubgoal != null) {
				IAtom previousAtom = previousSubgoal.getKey();
				Pipe previousPipe = previousSubgoal.getValue();
				List<IVariable> previousVariables = TermMatchingAndSubstitution.getVariables(previousAtom.getTuple(), true);
				Map<String, String> equalityFields = new HashMap<String, String>();
				List<String> outputFields = new ArrayList<String>();
				for (IVariable previousVariable : previousVariables) {
					for (IVariable variable : variables) {
						if (previousVariable.equals(variable)) {
							equalityFields.put(fieldsVariablesMapping.getField(previousAtom, previousVariable), 
									fieldsVariablesMapping.getField(atom, variable));
						}
					}
				}
				Fields lhsFields = new Fields();
				Fields rhsFields = new Fields();
				for (Entry<String, String> equalityEntry : equalityFields.entrySet()) {
					lhsFields = lhsFields.append(new Fields(equalityEntry.getKey()));
					rhsFields = rhsFields.append(new Fields(equalityEntry.getValue()));
				}
				Fields outputFields = new Fields();
				if (prevOutputFields.size() == 0) {
					for (IVariable variable : previousVariables) {
						outputFields = outputFields.append(new Fields(fieldsVariablesMapping.getField(previousAtom, variable)));
					}
					for (IVariable variable : variables) {
						if (variable)
						outputFields = outputFields.append(new Fields(fieldsVariablesMapping.getField(previousAtom, variable)));
					}
				}
				join = new CoGroup(previousPipe, lhsFields, pipe, rhsFields, new InnerJoin());
			}
			
			previousSubgoal = subgoal;
		}
		return join;
		*/
		
		/*
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
						join1.add(i1 + 1);
						join2.add(i2 + 1);

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
			String[] vars = new String[1 + previousVariables.size() + 1 + variables.size()];
			int k = 1;
			
			vars[0] = "http://larkc.eu/q";
			for(IVariable var : variables) {
				//actually it would be more descriptive if the suffix that we use here would
				//e.g. be the name of the predicate
				vars[k] = variables.get(k - 1).getValue() + "1";
				k++;
			}
			vars[k++] = "http://larkc.eu/r";
			int offset = k;
			for(IVariable var : previousVariables) {
				vars[k] = previousVariables.get(k -offset).getValue() + "2";
				k++;
			}
			
			Fields declared = new Fields(vars );
			Pipe join = new CoGroup( previousPipe, lhsFields, pipe, rhsFields, declared, new InnerJoin() );
			
			// debugging, this can be removed through the flowplanner for
			// production use
			join = new Each(join, DebugLevel.VERBOSE, new Debug(true));

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
		*/
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
	protected Flow attachTaps(FieldsVariablesMapping fieldsVariablesMapping, PipeFielded rulePipeFielded, IRule originalRule) {

		Pipe rulePipe = rulePipeFielded.getPipe();
		
		Map<String, Tap> sources = new HashMap<String, Tap>();
		
		List<ILiteral> lits = originalRule.getBody();
		for (ILiteral literal : lits) {
			IAtom atom = literal.getAtom();
			Tap tap = mFacts.getFacts(fieldsVariablesMapping, atom);
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

		/*
		rulePipe = new Each( rulePipe, new Insert( new Fields("P3"), 
				"http://larkc.eu/" + head.get(0).getAtom().getPredicate().getPredicateSymbol()), new Fields("P3", "X1", "Y1"));
				*/
		//rulePipe = new Each( rulePipe , new Fields("P3", "X1", "Y1"), new Identity(new Fields( "P3", "X1", "Y1" )));

		//rulePipe = new Each( rulePipe, new Insert(Fields.size(1), head.get(0).getAtom().getPredicate().getPredicateSymbol()));
		//rulePipe = new Each( rulePipe, new Fields(2, 3, 7), new Identity(), Fields.RESULTS );
		
		//rulePipe = new Each( rulePipe, new Fields(1, 2, 3), new Identity(), Fields.RESULTS );
			
		rulePipe = new Each( rulePipe, new Insert( new Fields("HEAD_PREDICATE"), head.get(0).getAtom().getPredicate().getPredicateSymbol()), Fields.ALL );
		
		List<IVariable> variables = TermMatchingAndSubstitution.getVariables(head.get(0).getAtom().getTuple(), true);		
		List<String> headFieldsList = new ArrayList<String>();
		headFieldsList.add("HEAD_PREDICATE");
		for (IVariable variable : variables) {
			for (String field : rulePipeFielded.getFields()) {
				if (field.startsWith(variable.getValue())) {
					headFieldsList.add(field);
					break;
				}
			}
		}
		Fields headFields = new Fields();
		for (String field : headFieldsList) {
			headFields = headFields.append(new Fields(field));
		}

		rulePipe = new Each( rulePipe, headFields, new Identity(headFields));

		//this should go by ordinal
		//IAtom headAtom = head.get(0).getAtom();
		//Tap headSink = mFacts.getFacts(headAtom);
		Tap headSink = mFacts.getFacts();
		
		Flow flow = new FlowConnector().connect(originalRule.toString(),
					sources, headSink, rulePipe);
		
		if(flow != null) {
			//flow.writeDOT(rulePipe.getName() + ".dot");
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