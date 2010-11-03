/**
 * 
 */
package eu.larkc.iris.storage.rdf;

import org.deri.iris.api.basics.IPredicate;
import org.deri.iris.api.terms.ITerm;
import org.deri.iris.builtins.AbstractBuiltin;

/**
 * @author valer
 *
 */
public class RdfAtom extends AbstractBuiltin {

	public RdfAtom(IPredicate predicate, ITerm... terms) {
		super(predicate, terms);
	}
	
}
