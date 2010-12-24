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

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.hadoop.io.WritableComparable;
import org.deri.iris.api.basics.IPredicate;

/**
 * @author vroman
 *
 */
public class PredicateWritable implements WritableComparable<PredicateWritable>, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1505159005042393445L;
	
	private String uri;
	private int arity = 0;
	
	public PredicateWritable() {
	}
	
	public PredicateWritable(IPredicate predicate) {
		this.uri = predicate.getPredicateSymbol();
		this.arity = predicate.getArity();
	}
	
	public String getURI() {
		return uri;
	}

	public void setURI(String value) {
		uri = value;
	}

	public int getArity() {
		return arity;
	}

	public void setArity(int arity) {
		this.arity = arity;
	}

	@Override
	public void write(DataOutput out) throws IOException {
		out.writeUTF(uri);
		out.writeInt(arity);
	}

	@Override
	public void readFields(DataInput in) throws IOException {
		uri = in.readUTF();
		arity = in.readInt();
	}

	public static PredicateWritable read(DataInput in) throws IOException {
		PredicateWritable predicateWritable = new PredicateWritable();
		String uri = in.readUTF();
		int arity = in.readInt();
		predicateWritable.uri = uri;
		predicateWritable.arity = arity;
		return predicateWritable;
	}

	@Override
	public int compareTo(PredicateWritable o) {
		int uriCompare = uri.compareTo(o.uri);
		if (uriCompare != 0) {
			return uriCompare;
		}
		return new Integer(arity).compareTo(new Integer(o.arity));
	}

	@Override
	public String toString() {
		return "predicate[" + uri + ", " + arity + "]";
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof PredicateWritable)) {
			return false;
		}
		PredicateWritable p = (PredicateWritable) obj;
		return new EqualsBuilder().append(uri, p.uri).append(arity, p.arity).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(uri).append(arity).hashCode();
	}

}
