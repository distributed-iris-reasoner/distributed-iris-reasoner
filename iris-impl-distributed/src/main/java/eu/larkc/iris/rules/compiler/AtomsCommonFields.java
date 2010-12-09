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
	@SuppressWarnings("rawtypes")
	private void constructCommonFields() {
		List<IVariable> variables = TermMatchingAndSubstitution.getVariables(atom.getTuple(), true);
		commonFields = new HashMap<String, String>();
		for (String field : lhsFields) {
			for (IVariable variable : variables) {
				Comparable previousVariable = fieldsVariablesMapping.getComparable(field);
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
