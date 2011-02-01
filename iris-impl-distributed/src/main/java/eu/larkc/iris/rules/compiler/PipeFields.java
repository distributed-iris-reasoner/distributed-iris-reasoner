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

import java.util.HashSet;
import java.util.Set;

import org.deri.iris.api.basics.IPredicate;
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
public class PipeFields extends Fields {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1900290399559901633L;

	private static final Logger logger = LoggerFactory.getLogger(PipeFields.class);
	
	protected Pipe pipe = null;
	
	protected PipeFields() {}
	
	public PipeFields(Fields fields) {
		super(fields);
	}

	public PipeFields(Pipe pipe, Fields fields) {
		super(fields);
		this.pipe = pipe;
	}

	public PipeFields(Pipe pipe, Fields leftFields, Fields rightFields) {
		this(pipe, leftFields);
		addAll(rightFields);
	}

	public Pipe getPipe() {
		return pipe;
	}

	public PipeFields innerJoin(PipeFields fields) {
		FieldPairs fieldGroup = getCommonFields(fields);
		
		Pipe join = new CoGroup(getPipe(), fieldGroup.getLeftFields().getFields(), fields.getPipe(), fieldGroup.getRightFields().getFields(), new InnerJoin());
		
		return new PipeFields(join, this, fields);
	}

	public PipeFields getUniqueVariableFields() {
		PipeFields keepFieldsList = new PipeFields();
		Set<Comparable> fieldTerms = new HashSet<Comparable>();
		for (Field field : this) {
			Comparable source = field.getSource();
			if (!(source instanceof IVariable)) {
				continue;
			}
			if (fieldTerms.contains(source)) {
				continue;
			}
			fieldTerms.add(source);
			keepFieldsList.add(field);
		}
		logger.info("fields to keep : " + keepFieldsList);
		
		keepFieldsList.pipe = new Each( getPipe(), keepFieldsList.getFields(), new Identity());	// outgoing -> "keepField"
		
		return keepFieldsList;
	}

	public PipeFields eliminateDuplicates() {
		Pipe join = new GroupBy(getPipe(), getFields()); //eliminate duplicates
		join = new Every(join, new Count(), getFields());
		PipeFields fields = new PipeFields(this);
		fields.pipe = join;
		return fields;
	}
	
	public PipeFields eliminateExistingResults(PipeFields headFields) {
		FieldPairs fieldGroup = getCommonFields(headFields);
		
		Pipe leftJoin = new CoGroup(getPipe(), fieldGroup.getLeftFields().getFields(), headFields.getPipe(), fieldGroup.getRightFields().getFields(), new LeftJoin());
		
		leftJoin = new Each( leftJoin, headFields.getFields(), new FilterNotNull());	// outgoing -> "keepField"
		leftJoin = new Each( leftJoin, this.getFields(), new Identity());	// outgoing -> "keepField"
		
		return new PipeFields(leftJoin, this);
	}

	public PipeFields generateHeadVariablesInStream(PipeFields headFields) {
		eu.larkc.iris.rules.compiler.Fields inHeadHeadFields = headFields.getCommonFields(this).getLeftFields();
		eu.larkc.iris.rules.compiler.Fields inBodyHeadFields = headFields.getCommonFields(this).getRightFields();
		
		if (headFields.size() != (inBodyHeadFields.size() + 1)) { //+1 is for predicate
			eu.larkc.iris.rules.compiler.FieldPairs extraFields = new eu.larkc.iris.rules.compiler.FieldPairs();
			for (Field field : headFields) {
				if ((field.getSource() instanceof IPredicate) || inHeadHeadFields.contains(field)) {
					continue;
				}
				for (Field aField : inBodyHeadFields) {
					if (field.getSource().equals(aField.getSource())) {
						extraFields.add(field, aField);
					}
				}
			}
			eu.larkc.iris.rules.compiler.Fields leftFields = extraFields.getLeftFields();
			eu.larkc.iris.rules.compiler.Fields rightFields = extraFields.getRightFields();
			
			Pipe rulePipe = new Each(getPipe(), rightFields.getFields(), new Identity(rightFields.getFields()));
			eu.larkc.iris.rules.compiler.Fields declaredFields = new eu.larkc.iris.rules.compiler.Fields(rightFields);
			declaredFields.addAll(leftFields);
			rulePipe = new CoGroup(rulePipe, rightFields.getFields(), 1, declaredFields.getFields());
			return new PipeFields(rulePipe, declaredFields);
		}
		return this;		
	}
}
