/*
D * Copyright 2010 Softgress - http://www.softgress.com/
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
package eu.larkc.iris.rules.compiler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.NotImplementedException;
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
import org.deri.iris.api.terms.concrete.IIri;
import org.deri.iris.utils.TermMatchingAndSubstitution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cascading.operation.Debug;
import cascading.operation.Identity;
import cascading.operation.Insert;
import cascading.operation.aggregator.Count;
import cascading.operation.filter.FilterNotNull;
import cascading.pipe.CoGroup;
import cascading.pipe.Each;
import cascading.pipe.Every;
import cascading.pipe.GroupBy;
import cascading.pipe.Pipe;
import cascading.pipe.cogroup.InnerJoin;
import cascading.pipe.cogroup.RightJoin;
import cascading.tap.Hfs;
import cascading.tap.Tap;
import cascading.tuple.Fields;
import eu.larkc.iris.Utils;
import eu.larkc.iris.evaluation.ConstantFilter;
import eu.larkc.iris.evaluation.PredicateFilter;
import eu.larkc.iris.storage.FieldsVariablesMapping;
import eu.larkc.iris.storage.PredicateWritable;

/**
 * 
 * Knows about facts to set up taps for now. This is non-finally code and not
 * functional yet.
 * 
 * @history Oct 1, 2010, fisf, creation
 * @author Florian Fischer
 */
public class CascadingRuleCompiler implements IDistributedRuleCompiler {

	private static final Logger logger = LoggerFactory.getLogger(CascadingRuleCompiler.class);
	
	/*
	 * Field name used for head predicate
	 */
	private static final String HEAD_PREDICATE_FIELD = "HPF";

	private FieldsVariablesMapping fieldsVariablesMapping = new FieldsVariablesMapping();
	private IAtom head;
	
	/**
	 * Sets up a CascadingRuleCompiler with a specific configuration, taking
	 * relevant datasources for rule execution into account.
	 * 
	 * @param configuration
	 * @param facts
	 */
	public CascadingRuleCompiler(eu.larkc.iris.Configuration configuration) {
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
	public IDistributedCompiledRule compile(IRule rule) throws EvaluationException {

		List<ILiteral> body = rule.getBody();
		head = rule.getHead().get(0).getAtom();
		fieldsVariablesMapping.loadAtom(head); //load also the heads fields, to do the left outer join
		PipeFielded bodyPipe = compileBody(body);
		
		// tell the planner remove all Debug operations
		//Properties properties = new Properties();
		//FlowConnector.setDebugLevel(properties, DebugLevel.NONE);

		FlowAssembly compiledCascadingRuleFlowAssembly = attachTaps(bodyPipe, rule);

		IPredicate headPredicate = head.getPredicate();

		return new CascadingCompiledRule(headPredicate, compiledCascadingRuleFlowAssembly,
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
	protected PipeFielded compileBody(Collection<ILiteral> bodyLiterals) {
		List<ILiteral> literals = new ArrayList<ILiteral>(bodyLiterals);
		List<SubGoal> subGoals = new ArrayList<SubGoal>();

		mainPipe = new Pipe("main");
		
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
			
			fieldsVariablesMapping.loadAtom(atom);

			// (variables and constants)
			if (atom instanceof IBuiltinAtom) {
				processBuiltin((IBuiltinAtom) atom);
			} else {
				// construct pipe assembly, one pipe per atom
				/**/
				Pipe pipe = new Pipe(atom.toString(), mainPipe);
				pipe = new Each(pipe, new Fields(0, 1, 2), new PredicateFilter(atom.getPredicate()));
				
				pipe = filterConstants(pipe, atom.getTuple());
				/**/
				//Pipe pipe = AtomPipeFactory.getInstance(mainPipe).getPipe(atom);
				
				//Fields atomFields = Utils.getFieldsForAtom(fieldsVariablesMapping, atom);
				//pipe = new Each(pipe, Fields.ALL, new Identity(atomFields));
				
				subGoals.add(new SubGoal(atom, pipe));
			}
		}

		result = setupJoins(subGoals);

		return result;
	}
		
	/*
	 * Remove unwanted fields from the join output fields (predicates, diuplicated variables)
	 */
	@SuppressWarnings("rawtypes")
	private FieldsList fieldsToKeep(FieldsList outputFields) {
		FieldsList keepFieldsList = new FieldsList();
		Set<Comparable> fieldTerms = new HashSet<Comparable>();
		for (String field : outputFields) {
			Comparable term = fieldsVariablesMapping.getComparable(field);
			if (!(term instanceof IVariable)) {
				continue;
			}
			if (fieldTerms.contains(term)) {
				continue;
			}
			fieldTerms.add(term);
			keepFieldsList.add(field);
		}
		logger.info("fields to keep : " + keepFieldsList);
		return keepFieldsList;
	}
	
	/*
	 * Creates the output fields of the join based on the left join's fields and the new atom to be joined fields
	 */
	private FieldsList composeOutputFields(FieldsList initialFields, IAtom atom) {
		FieldsList outputFieldsList = new FieldsList(initialFields);
		outputFieldsList.add(fieldsVariablesMapping.getField(atom, atom.getPredicate()));
		for (int i = 0; i < atom.getTuple().size(); i++) {
			ITerm term = atom.getTuple().get(i);
			String field = fieldsVariablesMapping.getField(atom, term);
			outputFieldsList.add(field);
		}
		return outputFieldsList;
	}
	
	private Pipe eliminateOldInferencedData(PipeFielded lhsJoin) {
		if (lhsJoin == null) {
			return null;
		}
		// check whether all head's variable fields are in the stream, do the outer join if so
		FieldsList lhsFieldsList = lhsJoin.getFieldsList();
		FieldsList identifiedHeadFieldsList = identifyHeadVariableFields(fieldsVariablesMapping, head, lhsFieldsList);
		Pipe leftJoin = null;
		if (identifiedHeadFieldsList != null) {
			Pipe headPipe = new Pipe(head.toString(), mainPipe);
			headPipe = new Each(headPipe, Fields.ALL, new PredicateFilter(head.getPredicate()));
			
			FieldsList headFieldsList = Utils.getFieldsFromAtom(fieldsVariablesMapping, head);
			AtomsCommonFields headCommonFields = new AtomsCommonFields(fieldsVariablesMapping, lhsFieldsList, head);
			Fields lhsFields = lhsFieldsList.getFields(headCommonFields.getLhsFields());
			Fields rhsFields = headFieldsList.getFields(headCommonFields.getRhsFields());
			//Pipe headPipe = new Pipe(head.toString());
			
			//headPipe = new Each(headPipe, Fields.ALL, new Identity(Utils.getFieldsForAtom(fieldsVariablesMapping, head)));
			//Fields headFields = Utils.getFieldsForAtom(fieldsVariablesMapping, head);
			//int[] groups = {2, 1, 3};
			//RegexParser parser = new RegexParser(headFields, "^(<[^\\s]+>)\\s*(<[^\\s]+>)\\s*([<\"].*[^\\s])\\s*.\\s*$", groups);
			//headPipe = new Each(headPipe, new Fields("line"), parser);
			
			//new Each(lhsJoin.getPipe(), new Debug(true));
			
			//Pipe aPipe = new Each( lhsJoin.getPipe(), new Fields(0, 1, 2), new Identity(new Fields(0, 1, 2)));
			//aPipe = new Each(aPipe, new Debug(true));
						
			//FIX ME must set the field names cause there is some bug in cascading, if using indexes no null values are added instead the tuple size is shrunked
			//headPipe = new Each(headPipe, headFieldsList.getFields(), new Identity(headFieldsList.getFieldsNames()));
			//Pipe aPipe = new Each(lhsJoin.getPipe(), lhsFieldsList.getFields(), new Identity(lhsFieldsList.getFieldsNames()));
			//~
			
			leftJoin = new CoGroup(headPipe, rhsFields, lhsJoin.getPipe(), lhsFields, new RightJoin());
			
			FieldsList allFields = new FieldsList(headFieldsList);
			allFields.addAll(lhsFieldsList);
			leftJoin = new Each( leftJoin, allFields.getFields(headFieldsList), new FilterNotNull());	// outgoing -> "keepField"
			leftJoin = new Each( leftJoin, allFields.getFields(lhsFieldsList), new Identity());	// outgoing -> "keepField"
			
			//leftJoin = new Each(leftJoin, new Debug(true));
			
		}
		return leftJoin;
	}
	
	/**
	 * Build a join between all the literals of the rule
	 * 
	 * @param fieldsVariablesMapping
	 * @param lhsJoin
	 * @param subgoalsIterator
	 * @return
	 */
	public PipeFielded buildJoin(boolean leftJoinApplied, PipeFielded lhsJoin, 
			ListIterator<SubGoal> subgoalsIterator) {
		Pipe leftJoin = null;
		if (!leftJoinApplied) {
			leftJoin = eliminateOldInferencedData(lhsJoin);
			leftJoinApplied = leftJoin != null;
		}
		
		//if not subgoals left return the last join
		if (!subgoalsIterator.hasNext()) {
			if (leftJoin != null) {
				return new PipeFielded(leftJoin, lhsJoin.getFieldsList());
			}
			return lhsJoin;
		}
		SubGoal subgoal = subgoalsIterator.next();
		IAtom atom = subgoal.getAtom();
		Pipe pipe = subgoal.getPipe();
		
		//first call, create pipe for the first subgoal, nothing to join
		if (lhsJoin == null) {
			PipeFielded pipeFielded = new PipeFielded(pipe, Utils.getFieldsFromAtom(fieldsVariablesMapping, atom));
			return buildJoin(leftJoinApplied, pipeFielded, subgoalsIterator);
		}

		FieldsList lhsFieldsList = lhsJoin.getFieldsList();
		FieldsList rhsFieldsList = Utils.getFieldsFromAtom(fieldsVariablesMapping, atom);
		AtomsCommonFields atomsCommonFields = new AtomsCommonFields(fieldsVariablesMapping, lhsFieldsList, atom);
		
		FieldsList commonLhsFieldsList = atomsCommonFields.getLhsFields();
		FieldsList commonRhsFieldsList = atomsCommonFields.getRhsFields();
		
		Pipe lhsPipe = (leftJoin == null) ? lhsJoin.getPipe() : leftJoin;
		//compose the output fields list
		FieldsList outputFieldsList = composeOutputFields(lhsJoin.getFieldsList(), atom);

		Pipe join = null;
		if (!commonLhsFieldsList.isEmpty()) {
			//join the previous join's pipe with the pipe for this literal
			Fields lhsFields = lhsFieldsList.getFields(commonLhsFieldsList);
			Fields rhsFields = rhsFieldsList.getFields(commonRhsFieldsList);
			
			join = new CoGroup(lhsPipe, lhsFields, pipe, rhsFields, new InnerJoin());
		} else {
			//this is not effective, joining without common fields, but it has to be done for some unoptimized rules
			lhsFieldsList.add("LCF");
			lhsPipe = new Each(lhsPipe, new Insert(new Fields("LCF"), new Integer(1)), lhsFieldsList.getFields());
			
			rhsFieldsList.add("RCF");
			pipe = new Each(pipe, new Insert(new Fields("RCF"), new Integer(1)), rhsFieldsList.getFields());

			join = new CoGroup(lhsPipe, new Fields(lhsFieldsList.size() - 1), pipe, new Fields(rhsFieldsList.size() - 1), new InnerJoin());

			outputFieldsList.add(lhsJoin.getFieldsList().size(), "LCF");
			outputFieldsList.add("RCF");
		}
		
		FieldsList keepFieldsList = fieldsToKeep(outputFieldsList);
		Fields keepFields = outputFieldsList.getFields(keepFieldsList);
		join = new Each( join, keepFields, new Identity());	// outgoing -> "keepField"
		
		join = new GroupBy(join, keepFieldsList.getFields()); //eliminate duplicates
		join = new Every(join, new Count(), keepFieldsList.getFields());
		
		PipeFielded pipeFielded = new PipeFielded(join, keepFieldsList);
		return buildJoin(leftJoinApplied, pipeFielded, subgoalsIterator);
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
	protected PipeFielded setupJoins(List<SubGoal> subGoals) {

		if (subGoals.isEmpty()) {
			throw new IllegalArgumentException(
				"Cannot setup joins with no subgoals.");			
		}

		return buildJoin(false, null, subGoals.listIterator());
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
				if (term instanceof IIri) {
					constantTerms.put(i + 1, (IIri) term); //added one because of the predicate field
				} else {
					constantTerms.put(i + 1, term.getValue()); //added one because of the predicate field
				}
					
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
	protected FlowAssembly attachTaps(PipeFielded rulePipeFielded, IRule originalRule) {

		Pipe rulePipe = rulePipeFielded.getPipe();
		
		//Map<String, Tap> sources = new HashMap<String, Tap>();
		//Map<String, Tap> sinks = new HashMap<String, Tap>();
		
		List<ILiteral> head = originalRule.getHead();
		// Only one literal in head
		if (head.size() != 1) {
			throw new IllegalArgumentException(
					"Input rule has more than two literals in head. Setup IRIS' optimizations correctly. Rule: "
							+ originalRule.toString());
		}

		IAtom headAtom = head.get(0).getAtom();

		Pipe resultPipe = new Pipe("resultTail", rulePipe);

		FieldsList ruleFieldsList = rulePipeFielded.getFieldsList();
		FieldsList headFieldsList = identifyHeadVariableFields(fieldsVariablesMapping, headAtom, ruleFieldsList);
		
		//Fields resultFields = ruleFieldsList.getFields(resultFieldsList);
		//resultPipe = new GroupBy(resultPipe, resultFields); //eliminate duplicates
		//resultPipe = new Every(resultPipe, new Count(), resultFields);
		
		resultPipe = new Each(resultPipe, new Debug(true));
		
		headFieldsList.add(0, HEAD_PREDICATE_FIELD);
		ruleFieldsList.add(HEAD_PREDICATE_FIELD);
		Fields headFields = ruleFieldsList.getFields(headFieldsList);
		resultPipe = new Each( resultPipe, new Insert( new Fields(HEAD_PREDICATE_FIELD), new PredicateWritable(headAtom.getPredicate())), headFields);
		
		String input = mConfiguration.project + "/data/";
		Tap source = new Hfs(headFieldsList.getFields(), input, true ); //we can assume that the number of fields are the same as the head;s tuple size + 1 (the predicate)

		FlowAssembly flowAssembly = new FlowAssembly(mConfiguration, source, headFieldsList.getFields(), resultPipe);//, countPipe);
		return flowAssembly;
	}

	/*
	 * Returns the fields list that correspond to head variables, null in case not all variables were identified in the result stream
	 */
	private FieldsList identifyHeadVariableFields(FieldsVariablesMapping fieldsVariablesMapping, IAtom head, FieldsList resultStreamFields) {
		List<IVariable> variables = TermMatchingAndSubstitution.getVariables(head.getTuple(), true);		
		FieldsList headFieldsList = new FieldsList();
		//headFieldsList.add(fieldsVariablesMapping.getField(head, head.getPredicate()));
		for (IVariable variable : variables) {
			boolean identified = false;
			for (String field : resultStreamFields) {
				if (variable.equals(fieldsVariablesMapping.getComparable(field))) {
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
	 * Central configuration object
	 */
	private final eu.larkc.iris.Configuration mConfiguration;
	
	private Pipe mainPipe = null;
	
}