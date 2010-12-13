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
package eu.larkc.iris.storage;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.Serializable;

import org.apache.hadoop.io.WritableComparable;
import org.deri.iris.api.basics.IPredicate;
import org.deri.iris.basics.BasicFactory;

/**
 * @author vroman
 *
 */
public class PredicateWritable implements WritableComparable<PredicateWritable>, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1505159005042393445L;
	
	private IPredicate predicate;
	
	public PredicateWritable() {
		BasicFactory.getInstance().createPredicate("", 1);
	}
	
	public PredicateWritable(IPredicate predicate) {
		this.predicate = predicate;
	}
	
	public IPredicate getValue() {
		return predicate;
	}
	
	@Override
	public void write(DataOutput out) throws IOException {
		out.writeUTF(predicate.getPredicateSymbol());
		out.writeInt(predicate.getArity());
	}

	@Override
	public void readFields(DataInput in) throws IOException {
		predicate = BasicFactory.getInstance().createPredicate(in.readUTF(), in.readInt());
	}

	public static PredicateWritable read(DataInput in) throws IOException {
		PredicateWritable predicateWritable = new PredicateWritable(BasicFactory.getInstance().createPredicate(in.readUTF(), in.readInt()));
		return predicateWritable;
	}

	@Override
	public int compareTo(PredicateWritable o) {
		return predicate.compareTo(o.predicate);
	}

	@Override
	public String toString() {
		return "predicate[" + predicate + "]";
	}

}
