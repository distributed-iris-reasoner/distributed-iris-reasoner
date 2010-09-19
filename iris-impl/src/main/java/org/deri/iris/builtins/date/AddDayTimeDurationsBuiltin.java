package org.deri.iris.builtins.date;

import static org.deri.iris.factory.Factory.BASIC;

import org.deri.iris.api.basics.IPredicate;
import org.deri.iris.api.terms.ITerm;
import org.deri.iris.api.terms.concrete.IDayTimeDuration;
import org.deri.iris.builtins.AddBuiltin;

/**
 * <p>
 * Represents the RIF built-in function func:add-dayTimeDurations.
 * </p>
 */
public class AddDayTimeDurationsBuiltin extends AddBuiltin {

	/** The predicate defining this built-in. */
	private static final IPredicate PREDICATE = BASIC.createPredicate(
			"ADD_DAYTIMEDURATION", 3);

	public AddDayTimeDurationsBuiltin(ITerm... terms) {
		super(PREDICATE, terms);
	}

	@Override
	protected ITerm computeMissingTerm(int missingTermIndex, ITerm[] terms) {
		if (terms[0] instanceof IDayTimeDuration
				&& terms[1] instanceof IDayTimeDuration) {
			return super.computeMissingTerm(missingTermIndex, terms);
		}

		return null;
	}

}
