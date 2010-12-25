package org.deri.iris.builtins;

import static org.deri.iris.factory.Factory.BASIC;

import org.deri.iris.EvaluationException;
import org.deri.iris.api.basics.IPredicate;
import org.deri.iris.api.terms.IConcreteTerm;
import org.deri.iris.api.terms.ITerm;
import org.deri.iris.builtins.string.PercentEncoder;
import org.deri.iris.factory.Factory;

/**
 * Represents the RIF built-in function func:encode-for-uri.
 */
public class EncodeForUriBuiltin extends FunctionalBuiltin {

	/** The predicate for this built-in. */
	private static final IPredicate PREDICATE = BASIC.createPredicate(
			"ENCODE_FOR_URI", 2);

	/**
	 * Constructor. Two terms must be passed to the constructor, otherwise an
	 * exception will be thrown. The last term represents the result of this
	 * built-in.
	 * 
	 * @param terms
	 *            The terms.
	 * @throws IllegalArgumentException
	 *             If one of the terms is {@code null}.
	 * @throws IllegalArgumentException
	 *             If the number of terms submitted is not 2.
	 * @throws IllegalArgumentException
	 *             If terms is <code>null</code>.
	 */
	public EncodeForUriBuiltin(ITerm... terms) {
		super(PREDICATE, terms);
	}

	@Override
	protected ITerm computeResult(ITerm[] terms) throws EvaluationException {
		if (terms[0] instanceof IConcreteTerm) {
			IConcreteTerm concreteTerm = (IConcreteTerm) terms[0];
			String canonicalString = concreteTerm.toCanonicalString();

			PercentEncoder encoder = new PercentEncoder();
			String encodedString = encoder.encode(canonicalString);

			return Factory.TERM.createString(encodedString);
		}

		return null;
	}

}
