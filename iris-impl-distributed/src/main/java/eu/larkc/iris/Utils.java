/**
 * 
 */
package eu.larkc.iris;

import org.deri.iris.api.basics.IAtom;
import org.deri.iris.api.basics.ITuple;
import org.deri.iris.api.terms.ITerm;
import org.deri.iris.api.terms.IVariable;

import cascading.tuple.Fields;
import eu.larkc.iris.storage.FieldsVariablesMapping;

/**
 * @author valer
 *
 */
public class Utils {

	public static Fields getFieldsForAtom(FieldsVariablesMapping fieldsVariablesMapping, IAtom atom) {
		ITuple tuple = atom.getTuple();
		Fields sourceFields = new Fields();
		if (fieldsVariablesMapping != null) {
			sourceFields = sourceFields.append(new Fields(fieldsVariablesMapping.getField(atom, atom.getPredicate())));
		} else {
			sourceFields = sourceFields.append(new Fields(atom.getPredicate().getPredicateSymbol()));
		}
		for (int i = 0; i < tuple.size(); i++) {
			ITerm term = tuple.get(i);
			String field = null;
			if (fieldsVariablesMapping != null) {
				field = fieldsVariablesMapping.getField(atom, term);
			} else {
				//when no field variable mapping is not give (normally this should not happen in real distributed iris usage)
				if (term instanceof IVariable) {
					field = ((IVariable) term).getValue();
				} else {
					field ="CNST";
				}
			}
			// TODO check which types can the value have. also decide what field
			// name should we give for constants
			sourceFields = sourceFields.append(new Fields(field));
		}
		return sourceFields;
	}
}
