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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.deri.iris.api.terms.IVariable;

/**
 * Represents the fields of a stream used in rule evaluation
 * It is a list of {@code Field}
 * 
 * @author valer
 * 
 */
public class Fields extends ArrayList<Field> {

	//private static final Logger logger = LoggerFactory.getLogger(Fields.class);
	
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1114028077151838545L;

	public Fields() {}
	
	public Fields(Fields fields) {
		this.addAll(fields);
	}

	/**
	 * Returns the cascading fields equivalent for this stream
	 * @return
	 */
	public cascading.tuple.Fields getFields() {
		java.util.List<String> fieldNames = new ArrayList<String>();
		for (int i = 0; i < this.size(); i++) {
			fieldNames.add(get(i).getName());
		}
		return new cascading.tuple.Fields(fieldNames.toArray(new String[fieldNames.size()]));
	}

	/**
	 * Returns the unique common fields of this stream the the stream {@code fields}
	 * A field is common on two stream if it represents the same iris variable and unique 
	 * if in the result there no other field representing the same iris variable
	 * 
	 * @param fields the other stream
	 * @return the field pairs of common fields
	 */
	protected FieldPairs getCommonFields(Fields fields) {
		return getCommonFields(true, fields);
	}
	
	/**
	 * Returns the common fields of this stream the the stream {@code fields}
	 * A field is common on two stream if it represents the same iris variable
	 * If unique is true then it returns only fields representing an unique set of variables.
	 * 
	 * @param uniques return only for unique variables
	 * @param fields the other stream
	 * @return the field pairs of common fields
	 */
	protected FieldPairs getCommonFields(boolean uniques, Fields fields) {
		FieldPairs commonItems = new FieldPairs();
		for (Field item : getVariableFields(uniques)) {
			for (Field anItem : fields.getVariableFields(uniques)) {
				if (commonItems.contains(anItem)) {
					continue;
				}
				if (item.getSource().equals(anItem.getSource())) {
					commonItems.add(item, anItem);
					break;
				}
			}
		}
		return commonItems;
	}
	
	/**
	 * Returns only the fields representing an iris variable.
	 * No two fields are representing the same variable
	 * 
	 * @return list of fields
	 */
	public List<Field> getVariableFields() {
		return getVariableFields(true);
	}
	
	/**
	 * Returns the fields representing iris variables
	 * 
	 * @param uniques return only unique fields, no two field represent the same variable
	 * @return
	 */
	public List<Field> getVariableFields(boolean uniques) {
		Set<IVariable> variables = new HashSet<IVariable>();
		List<Field> variableItems = new ArrayList<Field>();
		for (Field field : this) {
			if (!field.isVariable()) {
				continue;
			}
			if (uniques && variables.contains(field.getSource())) {
				continue;
			}
			variables.add((IVariable) field.source);
			variableItems.add(field);
		}
		return variableItems;
	}

	/**
	 * Tells if this stream can be joined with the stream {@code stream}
	 * Two stream can be joined if they share common variables
	 * 
	 * @param stream the other stream
	 * @return true if they can be joined, false otherwise
	 */
	public boolean canBeInnerJoined(Fields stream) {
		return !getCommonFields(stream).isEmpty();
	}
	
}
