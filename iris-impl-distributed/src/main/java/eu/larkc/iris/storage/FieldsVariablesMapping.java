/**
 * 
 */
package eu.larkc.iris.storage;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.deri.iris.api.basics.IAtom;
import org.deri.iris.api.terms.ITerm;
import org.deri.iris.api.terms.IVariable;

/**
 * @author vroman
 *
 */
public class FieldsVariablesMapping {

	private Map<IAtom, Map<ITerm, String>> atoms = new HashMap<IAtom, Map<ITerm, String>>();
	private Map<String, ITerm> fields = new HashMap<String, ITerm>();
	
	public void loadAtom(IAtom atom) {
		if (atoms.containsKey(atom)) {
			throw new RuntimeException("atom " + atom + " already loaded!");
		}
		Random random = new Random(System.currentTimeMillis());
		for (int i = 0; i < atom.getTuple().size(); i++) {
			ITerm term = atom.getTuple().get(i);
			//IVariable variable : atom.getTuple().getAllVariables()
			String fieldPrefix = null;
			if (term instanceof IVariable) {
				fieldPrefix = ((IVariable) term).getValue();
			} else {
				fieldPrefix = "CNST";
			}
			String field = null;
			while (field == null || fields.containsKey(field)) {
				field = fieldPrefix + String.valueOf(random.nextInt(9999));
			}
			fields.put(field, term);
			if (!atoms.containsKey(atom)) {
				atoms.put(atom, new HashMap<ITerm, String>());
			}
			if (atoms.get(atom).containsKey(term)) {
				throw new RuntimeException("the term " + term + " was already processed on this atom, this is a limitation of the compilation, to be fixed ...");
			}
			atoms.get(atom).put(term, field);
		}
	}
	
	public String getField(IAtom atom, ITerm term) {
		return atoms.get(atom).get(term);
	}
	
	public ITerm getVariable(String field) {
		return fields.get(field);
	}
}
