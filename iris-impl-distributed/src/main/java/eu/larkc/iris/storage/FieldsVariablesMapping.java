/**
 * 
 */
package eu.larkc.iris.storage;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.deri.iris.api.basics.IAtom;
import org.deri.iris.api.terms.IVariable;

/**
 * @author vroman
 *
 */
public class FieldsVariablesMapping {

	private Map<IAtom, Map<IVariable, String>> atoms = new HashMap<IAtom, Map<IVariable, String>>();
	private Map<String, String> fields = new HashMap<String, String>();
	
	public void loadAtom(IAtom atom) {
		if (atoms.containsKey(atom)) {
			throw new RuntimeException("atom " + atom + " already loaded!");
		}
		Random random = new Random(System.currentTimeMillis());
		for (IVariable variable : atom.getTuple().getAllVariables()) {
			String field = null;
			while (field == null || fields.containsKey(field)) {
				field = variable.getValue() + String.valueOf(random.nextInt(9999));
			}
			fields.put(field, variable.getValue());
			if (!atoms.containsKey(atom)) {
				atoms.put(atom, new HashMap<IVariable, String>());
			}
			atoms.get(atom).put(variable, field);
		}
	}
	
	public String getField(IAtom atom, IVariable variable) {
		return atoms.get(atom).get(variable);
	}
	
	public String getVariable(String field) {
		return fields.get(field);
	}
}
