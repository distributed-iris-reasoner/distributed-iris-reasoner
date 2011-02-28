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
package eu.larkc.iris.rules.compiler;

import java.util.List;
import java.util.ListIterator;

import org.deri.iris.EvaluationException;
import org.deri.iris.api.basics.ILiteral;
import org.deri.iris.api.basics.IPredicate;
import org.deri.iris.api.basics.IRule;

import cascading.operation.Insert;
import cascading.operation.aggregator.Count;
import cascading.pipe.CoGroup;
import cascading.pipe.Each;
import cascading.pipe.Every;
import cascading.pipe.GroupBy;
import cascading.pipe.Pipe;
import cascading.pipe.cogroup.InnerJoin;
import cascading.tuple.Fields;
import eu.larkc.iris.storage.IRIWritable;

/**
 * 
 * Knows about facts to set up taps for now. This is non-finally code and not
 * functional yet.
 * 
 * @history Oct 1, 2010, fisf, creation
 * @author Florian Fischer
 */
public class CascadingRuleCompiler implements IDistributedRuleCompiler {

	//private static final Logger logger = LoggerFactory.getLogger(CascadingRuleCompiler.class);
	
	/*
	 * Field name used for head predicate
	 */
	private static final String HEAD_PREDICATE_FIELD = "HPF";

	private RuleStreams ruleStreams = null;
	private eu.larkc.iris.rules.compiler.PipeFields headFields = null;
	
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
		List<ILiteral> head = rule.getHead();
		// Only one literal in head
		if (head.size() != 1) {
			throw new IllegalArgumentException(
					"Input rule has more than two literals in head. Setup IRIS' optimizations correctly. Rule: "
							+ rule.toString());
		}

		if (!mConfiguration.doPredicateIndexing) {
			mainPipe = new Pipe("main");
			ruleStreams = new RuleStreams(mConfiguration, mainPipe, rule);
		} else {
			ruleStreams = new RuleStreams(mConfiguration, rule);
		}
		
		headFields = ruleStreams.getHeadStream();
		
		eu.larkc.iris.rules.compiler.PipeFields bodyPipe = compileBody(ruleStreams);
		
		FlowAssembly compiledCascadingRuleFlowAssembly = attachTaps(ruleStreams, bodyPipe);

		return new CascadingCompiledRule(rule, compiledCascadingRuleFlowAssembly,
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
	protected eu.larkc.iris.rules.compiler.PipeFields compileBody(RuleStreams ruleStreams) {

		eu.larkc.iris.rules.compiler.PipeFields result;

		result = setupJoins(ruleStreams);

		return result;
	}
		
	private eu.larkc.iris.rules.compiler.PipeFields eliminateOldInferencedData(eu.larkc.iris.rules.compiler.PipeFields lhsJoin) {
		if (lhsJoin == null) {
			return null;
		}
		
		eu.larkc.iris.rules.compiler.FieldPairs commonFields = headFields.getCommonFields(lhsJoin);
		if (!(commonFields.size() == headFields.getVariableFields().size())) {
			return null;
		}

		return lhsJoin.eliminateExistingResults(headFields);
	}

	public eu.larkc.iris.rules.compiler.PipeFields buildJoin(boolean leftJoinApplied, eu.larkc.iris.rules.compiler.PipeFields lhsJoin, ListIterator<eu.larkc.iris.rules.compiler.LiteralFields> fieldsIterator) {
		eu.larkc.iris.rules.compiler.PipeFields leftJoin = null;
		if (!leftJoinApplied) {
			leftJoin = eliminateOldInferencedData(lhsJoin);
			leftJoinApplied = leftJoin != null;
		}
		
		//if not subgoals left return the last join
		if (!fieldsIterator.hasNext()) {
			if (leftJoin != null) {
				return leftJoin;
			}
			return lhsJoin;
		}
		eu.larkc.iris.rules.compiler.PipeFields stream = fieldsIterator.next();
		lhsJoin = (leftJoin == null) ? lhsJoin : leftJoin;
		
		eu.larkc.iris.rules.compiler.PipeFields join = null;
		if (lhsJoin.canBeInnerJoined(stream)) {
			join = lhsJoin.innerJoin(stream);
		} else {
			//this is not effective, joining without common fields, but it has to be done for some unoptimized rules
			lhsJoin.add(new Field("LCF", new Integer(1)));
			Pipe lhsPipe = new Each(lhsJoin.getPipe(), new Insert(new Fields("LCF"), new Integer(1)), lhsJoin.getFields());
			
			stream.add(new Field("RCF", new Integer(1)));
			Pipe rhsPipe = new Each(stream.getPipe(), new Insert(new Fields("RCF"), new Integer(1)), stream.getFields());

			Pipe joinPipe = new CoGroup(lhsPipe, new Fields("LCF"), rhsPipe, new Fields("RCF"), new InnerJoin());
			
			join = new PipeFields(joinPipe, lhsJoin, stream, lhsJoin.getCount()*stream.getCount());
		}
		
		join = join.getUniqueVariableFields();
		
		join = join.eliminateDuplicates();
		
		return buildJoin(leftJoinApplied, join, fieldsIterator);
		
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
	protected eu.larkc.iris.rules.compiler.PipeFields setupJoins(RuleStreams ruleStreams) {

		if (ruleStreams.getBodyStreams().isEmpty()) {
			throw new IllegalArgumentException(
				"Cannot setup joins with no subgoals.");			
		}

		ListIterator<eu.larkc.iris.rules.compiler.LiteralFields> listIteratorFields = ruleStreams.getBodyStreamIterator();
		return buildJoin(false, listIteratorFields.next(), listIteratorFields);
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
	protected FlowAssembly attachTaps(RuleStreams ruleStreams, eu.larkc.iris.rules.compiler.PipeFields pipeFields) {
		LiteralFields headStream = ruleStreams.getHeadStream();
		
		//it could be that the head literal has several times the same variable p(x,x)
		//we have to insert in the result stream another field for the second variable x, to be able to select then both x
		pipeFields = pipeFields.generateHeadVariablesInStream(headFields);
		
		eu.larkc.iris.rules.compiler.Fields inBodyHeadFields = headFields.getCommonFields(false, pipeFields).getRightFields();
		
		Pipe rulePipe = pipeFields.getPipe();
		IPredicate predicate = headStream.getPredicate();
		if (predicate != null) {
			inBodyHeadFields.add(0, new Field(HEAD_PREDICATE_FIELD, predicate));
			rulePipe = new Each( rulePipe, new Insert( new Fields(HEAD_PREDICATE_FIELD), new IRIWritable(predicate)), inBodyHeadFields.getFields());
		}
		
		Fields resultFields = inBodyHeadFields.getFields();
		
		rulePipe = new GroupBy(rulePipe, resultFields); //eliminate duplicates
		rulePipe = new Every(rulePipe, new Count(), resultFields);

		FlowAssembly flowAssembly = new FlowAssembly(mConfiguration, ruleStreams, resultFields, rulePipe);//, countPipe);
		return flowAssembly;
	}
	
	/**
	 * Central configuration object
	 */
	private final eu.larkc.iris.Configuration mConfiguration;
	
	private Pipe mainPipe = null;
	
}