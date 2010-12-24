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

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.hadoop.io.WritableComparable;
import org.deri.iris.api.terms.concrete.IIri;

/**
 * @author vroman
 *
 */
public class IRIWritable implements WritableComparable<IRIWritable> {

	private String iri;
	
	public IRIWritable() {
	}
	
	public IRIWritable(IIri iri) {
		this.iri = iri.getValue();
	}
	
	public String getValue() {
		return iri;
	}

	public void setValue(String value) {
		this.iri = value;
	}

	@Override
	public int compareTo(IRIWritable o) {
		return iri.compareTo(o.iri);
	}

	@Override
	public void write(DataOutput out) throws IOException {
		out.writeUTF(iri);
	}

	@Override
	public void readFields(DataInput in) throws IOException {
		iri = in.readUTF();
	}
	
	public static IRIWritable read(DataInput in) throws IOException {
		IRIWritable iriWritable = new IRIWritable();
		String iri = in.readUTF();
		iriWritable.iri = iri;
		return iriWritable;
	}

	@Override
	public String toString() {
		return "iri[" + iri + "]";
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof IRIWritable)) {
			return false;
		}
		IRIWritable i = (IRIWritable) obj;
		return new EqualsBuilder().append(iri, i.iri).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(iri).hashCode();
	}

}
