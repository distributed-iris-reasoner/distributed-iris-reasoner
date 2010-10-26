/**
 * 
 */
package eu.larkc.iris.rules.compiler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
import org.deri.iris.storage.IRelation;

import cascading.flow.Flow;

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
		// we have to analyze the body in order to identify
		List<ILiteral> body = rule.getBody();
		Flow cascadingFlow = compileBody(body);

		IPredicate headPredicate = rule.getHead().get(0).getAtom()
				.getPredicate();
		return new CascadingCompiledRule(headPredicate, cascadingFlow,
				mConfiguration);
	}

	// TODO: Finish, test
	private Flow compileBody(Collection<ILiteral> bodyLiterals) {
		List<ILiteral> literals = new ArrayList<ILiteral>(bodyLiterals);

		int processedRuleElements = 0;
		List<IVariable> previousVariables = new ArrayList<IVariable>();

		// loop over all rule elements
		while (processedRuleElements < bodyLiterals.size()) {
			EvaluationException lastException = null;

			for (int l = 0; l < literals.size(); ++l) {
				ILiteral literal = literals.get(l);
				IAtom atom = literal.getAtom();

				// we shouldn't even have to check for that if we do not deal
				// with negation, this is basically a double check for the
				// parser
				assert literal.isPositive() == true : "No negation supported.";

				if (atom instanceof IBuiltinAtom) {
					IBuiltinAtom builtinAtom = (IBuiltinAtom) atom;

					// Tell the builtin atom the term equivalence relation,
					// so that it can also take the equivalent terms into
					// account when evaluating.

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
					} else {
						// ordinary builtin
					}
					throw new NotImplementedException(
							"Builtins not implemented yet");

				} else {
					// used to identify taps
					// TODO: set them up
					IPredicate predicate = atom.getPredicate();
					IRelation relation = mFacts.get(predicate);
					ITuple viewCriteria = atom.getTuple();

					// set up cascading join with cogroup
				}

				literals.remove(l);

				processedRuleElements++;
				break;

			}

		}

		return null; // for now
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
}