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

import eu.larkc.iris.rules.compiler.FieldPairs.FieldPair;

/**
 * @author valer
 *
 */
public class FieldPairs extends ArrayList<FieldPair> {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 8702903574806836844L;

	public class FieldPair {
		private Field field1;
		private Field field2;
		
		public FieldPair(Field field1, Field field2) {
			this.field1 = field1;
			this.field2 = field2;
		}
	}
	
	public void add(Field field1, Field field2) {
		add(new FieldPair(field1, field2));
	}
	
	public Fields getLeftFields() {
		Fields fields = new Fields();
		for (FieldPair fieldPair : this) {
			fields.add(fieldPair.field1);
		}
		return fields;
	}
	
	public Fields getRightFields() {
		Fields fields = new Fields();
		for (FieldPair fieldPair : this) {
			fields.add(fieldPair.field2);
		}
		return fields;
	}

	public boolean contains(Field field) {
		for (FieldPair fieldPair : this) {
			if (fieldPair.field1.equals(field) || fieldPair.field2.equals(field)) {
				return true;
			}
		}
		return false;
	}
}
