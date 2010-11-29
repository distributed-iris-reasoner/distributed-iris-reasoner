/**
 * 
 */
package eu.larkc.iris.rules.compiler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

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

	/*
	 * Field name used for head predicate
	 */
	private static final String HEAD_PREDICATE_FIELD = "HPF";
	
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
		IAtom head = rule.getHead().get(0).getAtom();
		PipeFielded bodyPipe = compileBody(fieldsVariableMapping, head, body);
		
		// tell the planner remove all Debug operations
		Properties properties = new Properties();
		FlowConnector.setDebugLevel(properties, DebugLevel.NONE);

		Flow compiledCascadingRule = attachTaps(fieldsVariableMapping, bodyPipe, rule);

		IPredicate headPredicate = head.getPredicate();

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
	protected PipeFielded compileBody(FieldsVariablesMapping fieldsVariableMapping, IAtom head, Collection<ILiteral> bodyLiterals) {
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

		result = setupJoins(fieldsVariableMapping, head, subGoals);

		return result;
	}
	
	/*
	 * From two atoms return the field names correesponding with the common variables
	 */
	private Map<String, String> getCommonFields(FieldsVariablesMapping fieldsVariablesMapping, IAtom atom1, IAtom atom2) {
		List<IVariable> previousVariables = TermMatchingAndSubstitution.getVariables(atom1.getTuple(), true);
		List<IVariable> variables = TermMatchingAndSubstitution.getVariables(atom2.getTuple(), true);
		Map<String, String> equalityFields = new HashMap<String, String>();
		for (IVariable previousVariable : previousVariables) {
			for (IVariable variable : variables) {
				if (previousVariable.equals(variable)) {
					equalityFields.put(fieldsVariablesMapping.getField(atom2, variable), 
							fieldsVariablesMapping.getField(atom1, previousVariable));
				}
			}
		}
		return equalityFields;
	}
	
	private FieldsList fieldsToKeep(FieldsVariablesMapping fieldsVariablesMapping, FieldsList outputFields) {
		FieldsList keepFieldsList = new FieldsList();
		Set<ITerm> fieldTerms = new HashSet<ITerm>();
		for (String field : outputFields) {
			ITerm term = fieldsVariablesMapping.getVariable(field);
			if (term == null) {
				continue;
			}
			if (fieldTerms.contains(term)) {
				continue;
			}
			fieldTerms.add(term);
			keepFieldsList.add(field);
		}
		return keepFieldsList;
	}
	
	private FieldsList composeOutputFields(FieldsVariablesMapping fieldsVariablesMapping, FieldsList initialFields, IAtom atom) {
		FieldsList outputFieldsList = new FieldsList(initialFields);
		outputFieldsList.add(atom.getPredicate().getPredicateSymbol());
		for (int i = 0; i < atom.getTuple().size(); i++) {
			ITerm term = atom.getTuple().get(i);
			String field = fieldsVariablesMapping.getField(atom, term);
			outputFieldsList.add(field);
		}
		return outputFieldsList;
	}
	
	/**
	 * Build a join between all the literals of the rule
	 * 
	 * @param fieldsVariablesMapping
	 * @param lhsJoin
	 * @param subgoalsIterator
	 * @return
	 */
	public PipeFielded buildJoin(FieldsVariablesMapping fieldsVariablesMapping, IAtom head, PipeFielded lhsJoin, Iterator<Entry<IAtom, Pipe>> subgoalsIterator) {
		//if not subgoals left return the last join
		if (!subgoalsIterator.hasNext()) {
			return lhsJoin;
		}
		Entry<IAtom, Pipe> subgoal = subgoalsIterator.next();
		IAtom atom = subgoal.getKey();
		Pipe pipe = subgoal.getValue();
		
		//first call, create pipe for the first subgoal, nothing to join
		if (lhsJoin == null) {
			PipeFielded pipeFielded = new PipeFielded(fieldsVariablesMapping, pipe, atom);
			return buildJoin(fieldsVariablesMapping, head, pipeFielded, subgoalsIterator);
		}
		
		// check whether all head's variable fields are in the stream, do the outer join if so
		FieldsList headFieldsList = identifyHeadVariableFields(fieldsVariablesMapping, head, lhsJoin.getFields());
		if (headFieldsList != null) {
			
		}
		//
		
		IAtom previousAtom = lhsJoin.getAtom();
		Map<String, String> commonFields = getCommonFields(fieldsVariablesMapping, previousAtom, atom);

		//compose the group fields for the left pipe and the right pipe
		Fields lhsFields = new Fields();
		Fields rhsFields = new Fields();
		for (Entry<String, String> commonFieldEntry : commonFields.entrySet()) {
			rhsFields = rhsFields.append(new Fields(commonFieldEntry.getKey()));
			lhsFields = lhsFields.append(new Fields(commonFieldEntry.getValue()));
		}

		//compose the output fields list
		FieldsList outputFieldsList = composeOutputFields(fieldsVariablesMapping, lhsJoin.getFields(), atom);
		
		//join the previous join's pipe with the pipe for this literal
		Pipe join = new CoGroup(lhsJoin.getPipe(), lhsFields, pipe, rhsFields, outputFieldsList.getFields(), new InnerJoin());
		
		FieldsList keepFieldsList = fieldsToKeep(fieldsVariablesMapping, outputFieldsList);
		join = new Each( join, keepFieldsList.getFields(), new Identity());	// outgoing -> "keepField"
		
		PipeFielded pipeFielded = new PipeFielded(fieldsVariablesMapping, join, atom, keepFieldsList);
		return buildJoin(fieldsVariablesMapping, head, pipeFielded, subgoalsIterator);
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
	protected PipeFielded setupJoins(FieldsVariablesMapping fieldsVariablesMapping, IAtom head, Map<IAtom, Pipe> subGoals) {

		if (subGoals.isEmpty()) {
			throw new IllegalArgumentException(
				"Cannot setup joins with no subgoals.");			
		}

		return buildJoin(fieldsVariablesMapping, head, null, subGoals.entrySet().iterator());
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
				constantTerms.put(i + 1, term.getValue()); //added one because of the predicate field
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
		
		IAtom headAtom = head.get(0).getAtom();
		
		rulePipe = new Each( rulePipe, new Insert( new Fields(HEAD_PREDICATE_FIELD), headAtom.getPredicate().getPredicateSymbol()), Fields.ALL );
		
		FieldsList headFieldsList = identifyHeadVariableFields(fieldsVariablesMapping, headAtom, rulePipeFielded.getFields());
		headFieldsList.add(0, HEAD_PREDICATE_FIELD);
		Fields headFields = headFieldsList.getFields();

		rulePipe = new Each( rulePipe, headFields, new Identity(headFields));

		//this should go by ordinal
		//IAtom headAtom = head.get(0).getAtom();
		//Tap headSink = mFacts.getFacts(headAtom);
		Tap headSink = mFacts.getFacts();
		
		Flow flow = new FlowConnector().connect(originalRule.toString(),
					sources, headSink, rulePipe);
		
		if(flow != null) {
			flow.writeDOT("flow.dot");
		}

		return flow;
	}

	/*
	 * Returns the fields list that correspond to head variables, null in case not all variables were identified in the result stream
	 */
	private FieldsList identifyHeadVariableFields(FieldsVariablesMapping fieldsVariablesMapping, IAtom head, FieldsList resultStreamFields) {
		List<IVariable> variables = TermMatchingAndSubstitution.getVariables(head.getTuple(), true);		
		FieldsList headFieldsList = new FieldsList();
		for (IVariable variable : variables) {
			boolean identified = false;
			for (String field : resultStreamFields) {
				if (variable.equals(fieldsVariablesMapping.getVariable(field))) {
					headFieldsList.add(field);
					identified = true;
					break;
				}
			}
			if (!identified) {
				return null;
			}
		}
		return headFieldsList;
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