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
import java.util.List;

import cascading.tuple.Fields;

/**
 * @author valer
 *
 */
public class FieldsList extends ArrayList<String> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5866763926324502409L;

	public FieldsList() {}
	
	public FieldsList(FieldsList fieldsList) {
		this.addAll(fieldsList);
	}
	
	public Fields getFieldsNames() {
		Fields fields = new Fields();
		for (String field : this) {
			fields = fields.append(new Fields(field));
		}
		return fields;
	}
	
	public Fields getFields(FieldsList fieldsList) {
		List<Integer> indexes = new ArrayList<Integer>();
		for (String aField : fieldsList) {
			for (String field : this) {
				if (field.equals(aField)) {
					indexes.add(this.indexOf(field));
				}
			}
		}
		return new Fields(indexes.toArray(new Integer[0]));
	}
	
	public Fields getFields() {
		Integer[] indexes = new Integer[this.size()];
		for (int i = 0 ; i < this.size(); i++) {
			indexes[i] = i;
		}
		return new Fields(indexes);
	}

}
