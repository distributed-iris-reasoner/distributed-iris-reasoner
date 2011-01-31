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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cascading.operation.Identity;
import cascading.operation.aggregator.Count;
import cascading.operation.filter.FilterNotNull;
import cascading.pipe.CoGroup;
import cascading.pipe.Each;
import cascading.pipe.Every;
import cascading.pipe.GroupBy;
import cascading.pipe.Pipe;
import cascading.pipe.cogroup.InnerJoin;
import cascading.pipe.cogroup.LeftJoin;

/**
 * @author valer
 * 
 */
public class Fields extends ArrayList<Field> {

	private static final Logger logger = LoggerFactory.getLogger(Fields.class);
	
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1114028077151838545L;

	public Fields() {}
	
	public Fields(Fields fields) {
		this.addAll(fields);
	}
	
	public cascading.tuple.Fields getFields() {
		java.util.List<String> fieldNames = new ArrayList<String>();
		for (int i = 0; i < this.size(); i++) {
			fieldNames.add(get(i).getName());
		}
		return new cascading.tuple.Fields(fieldNames.toArray(new String[fieldNames.size()]));
	}

	protected FieldPairs getCommonFields(Fields fields) {
		FieldPairs commonItems = new FieldPairs();
		for (Field item : getVariableFields()) {
			for (Field anItem : fields.getVariableFields()) {
				if (item.getSource().equals(anItem.getSource())) {
					commonItems.add(item, anItem);
				}
			}
		}
		return commonItems;
	}
	
	public List<Field> getVariableFields() {
		List<Field> variableItems = new ArrayList<Field>();
		for (Field field : this) {
			if (!field.isVariable()) {
				continue;
			}
			variableItems.add(field);
		}
		return variableItems;
	}

	public boolean canBeInnerJoined(Fields stream) {
		return !getCommonFields(stream).isEmpty();
	}
	
}
