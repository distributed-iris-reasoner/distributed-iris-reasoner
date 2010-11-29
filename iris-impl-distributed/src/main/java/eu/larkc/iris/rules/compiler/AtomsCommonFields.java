/**
 * 
 */
package eu.larkc.iris.rules.compiler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.deri.iris.api.basics.IAtom;
import org.deri.iris.api.terms.ITerm;
import org.deri.iris.api.terms.IVariable;
import org.deri.iris.utils.TermMatchingAndSubstitution;

import eu.larkc.iris.storage.FieldsVariablesMapping;

/**
 * @author valer
 *
 */
public class AtomsCommonFields {

	private FieldsVariablesMapping fieldsVariablesMapping;
	private FieldsList lhsFields;
	private IAtom atom;
	
	private Map<String, String> commonFields = null;
	
	public AtomsCommonFields(FieldsVariablesMapping fieldsVariablesMapping, FieldsList lhsFields, IAtom atom) {
		this.fieldsVariablesMapping = fieldsVariablesMapping;
		this.lhsFields = lhsFields;
		this.atom = atom;
	}
	
	/*
	 * From two atoms return the field names correesponding with the common variables
	 */
	private void constructCommonFields() {
		List<IVariable> variables = TermMatchingAndSubstitution.getVariables(atom.getTuple(), true);
		commonFields = new HashMap<String, String>();
		for (String field : lhsFields) {
			for (IVariable variable : variables) {
				ITerm previousVariable = fieldsVariablesMapping.getVariable(field);
				if (previousVariable != null && previousVariable.equals(variable)) {
					commonFields.put(fieldsVariablesMapping.getField(atom, variable), field);
				}
			}
		}
	}

	public FieldsList getLhsFields() {
		if (commonFields == null) {
			constructCommonFields();
		}
		FieldsList lhsFields = new FieldsList();
		for (Entry<String, String> commonFieldEntry : commonFields.entrySet()) {
			lhsFields.add(commonFieldEntry.getValue());
		}
		return lhsFields;
	}
	
	public FieldsList getRhsFields() {
		if (commonFields == null) {
			constructCommonFields();
		}
		FieldsList rhsFields = new FieldsList();
		for (Entry<String, String> commonFieldEntry : commonFields.entrySet()) {
			rhsFields.add(commonFieldEntry.getKey());
		}
		return rhsFields;
	}
}
