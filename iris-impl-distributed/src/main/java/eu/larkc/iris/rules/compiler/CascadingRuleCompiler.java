/**
 * 
 */
package eu.larkc.iris.rules.compiler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import cascading.flow.Flow;
import cascading.flow.FlowConnector;
import cascading.pipe.Each;
import cascading.pipe.Pipe;
import cascading.scheme.TextLine;
import cascading.tap.Hfs;
import cascading.tap.Tap;
import eu.larkc.iris.evaluation.distributed.ConstantFilter;

/**
 * 
 * Knows about facts to set up taps for now.
 * This is non-finally code and not functional yet.
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
		this.mFacts = facts;
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
		Pipe rulePipe = compileBody(body);		
		Flow compiledCascadingRule = attachTaps(rulePipe, rule);
		
		IPredicate headPredicate = rule.getHead().get(0).getAtom().getPredicate();
		
		return new CascadingCompiledRule(headPredicate, compiledCascadingRule, mConfiguration);
	}

	/**
	 * In this function we have to actually compile the body. For that purpose we iterate over all the literals in the body and chain pipes accordingly. We then have to handle several cases:
	 * 1.) We encounter a built-in. This needs to be mapped to a suitable built-in on the cascading side.
	 * 2.) We encounter constants, e.g. p(?x, 1). In that case we want to install a filter on the pipe. This is accomplished with cascadings EACH and FILTER primitives.
	 * 3.) we encounter a join, e.g.: p(?x,?y), r(?y,?z) would for example require a join of the two pipes on ?y. This also entails renaming fields. 
	 * For details see http://www.cascading.org/1.2/userguide/html/ch03s02.html#N2035D 
	 * 
	 * @param bodyLiterals
	 * @return
	 */
	protected Pipe compileBody(Collection<ILiteral> bodyLiterals) {		
		List<ILiteral> literals = new ArrayList<ILiteral>(bodyLiterals);
		Map<IAtom, Pipe> subGoals = new HashMap<IAtom, Pipe>();
		
		Pipe result = new Pipe(mCompleteBodyPipe);
		
		for (int l = 0; l < literals.size(); ++l) {
			ILiteral literal = literals.get(l);
			// we shouldn't even have to check for that if we do not deal
			// with negation, this is basically a double check for the
			// parser
			assert literal.isPositive() == true : "No negation supported.";
			
			IAtom atom = literal.getAtom(); //get predicate and tuple (variables and constants)
			if (atom instanceof IBuiltinAtom) {
				processBuiltin((IBuiltinAtom)atom);
			} else {
				// construct pipe assembly, one pipe per atom
				IPredicate predicate = atom.getPredicate();
				
				//TODO (fisf): This requires a unit test for predicates with the same symbol but different arity to see if things work fine on the cascading side
				//iff it causes a problem then the identifier simply becomes predicatename$arity as in other iris parts			
				Pipe pipe = new Pipe(predicate.getPredicateSymbol()); 
				ITuple tuple = atom.getTuple();
				
				//filter for constants
				pipe = filterConstants(pipe, tuple);
				
				//at this point the basic information for a subgoal has been process and filters for constants are completely set up, only joins left
				subGoals.put(atom, pipe);			
			}			
		}
		
		//iterate over all subgoals
		//for each subgoal keep track of its output variables
			//for each subgoal after the first one, inspect all previous variables
				//if one of them matches, get hold of the corresponding pipe and construct CoGroup
				//rename fields if required
		
		List<IVariable> previousVariables = new ArrayList<IVariable>();
	// joins the lhs and rhs
//		Pipe join = new CoGroup( lhs, rhs );
//
//		
//		Fields common = new Fields( "url" );
//		Fields declared = new Fields( "url1", "word", "wd_count", "url2", "sentence", "snt_count" );
//		Pipe join = new CoGroup( lhs, common, rhs, common, declared, new InnerJoin() );
//		
//		CoGroup(Pipe[] pipes, Fields[] groupFields, Fields declaredFields, Joiner joiner)
//          Constructor CoGroup creates a new CoGroup instance.
//          
//          
//      
			
		//TODO (fisf): refactor: set up joins: Mixed joins instead of simpl 2-way joins
	
		return result;
	}
	
	/**
	 * 
	 * @param attachTo
	 * @param tuple
	 * @return
	 */
	protected Pipe filterConstants(Pipe attachTo, ITuple tuple) {
				
		Map<Integer, Object> constantTerms = new HashMap<Integer, Object>();
		
		for (int i = 0; i < tuple.size(); i++) {
			ITerm term = tuple.get(i);
		
			//not a variable, we filter the tuples
			if(term.isGround()) {				
				constantTerms.put(i, term.getValue()); 
			}
		}
		
		//did we actually find at least one constant?
		if(!constantTerms.isEmpty()) {
			Pipe filter = new Each(attachTo, new ConstantFilter(constantTerms));
			return filter;
		}
		
		//nothing changed
		return attachTo;
	}
	
	
	
	/**
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
			// ordinary builtin, those WILL be handled
			throw new NotImplementedException("Builtins not implemented yet");
		}
	}
		
	/**
	 * 
	 * @param rulePipe
	 * @param originalRule
	 * @return
	 */
	protected Flow attachTaps(Pipe rulePipe, IRule originalRule) {
		
		//source taps
		Map<String, Tap> sources = new HashMap<String, Tap>();
		
		List<IPredicate> bodyPredicates = new ArrayList<IPredicate>();	
		List<ILiteral> lits = originalRule.getBody();
		for (ILiteral literal : lits) {
			bodyPredicates.add(literal.getAtom().getPredicate());
		}
		
		for (IPredicate predicate : bodyPredicates) {
			Tap predicateSource = new Hfs(new TextLine(), "somefile"); //TODO: getting the tap should be abstracted in some factory
			sources.put(predicate.getPredicateSymbol(), predicateSource);
		}		
		
		Tap headSink =  new Hfs(new TextLine(), "somefile"); //TODO: the tap where we right should also be obtained through a factory
		
		//matching is done by names, we use the original rule's string as identifier for the flow, the head/last operation is the tail of the assembly
		Flow flow = new FlowConnector().connect( originalRule.toString(), sources, headSink, rulePipe );
		
		return flow;
	}

	/**
	 * The knowledge-base facts used to attach to the compiled rule elements.
	 * This keeps encapsulates the access to external datasources and results in
	 * corresponding Cascading Taps.
	 * 
	 */
	private final IFacts mFacts;

	/**
	 * Central configuration object
	 */
	private final Configuration mConfiguration;
	
	/**
	 * This is a dummy name for the pipe that is constructed to express the complete body of a rule (since it does not have a dedicated predicate name).
	 */
	public static final String mCompleteBodyPipe = "BODY_PIPE";
}