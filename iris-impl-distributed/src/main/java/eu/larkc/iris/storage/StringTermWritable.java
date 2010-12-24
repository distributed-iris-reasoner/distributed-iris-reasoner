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
import org.deri.iris.api.terms.IStringTerm;

/**
 * @author vroman
 *
 */
public class StringTermWritable implements WritableComparable<StringTermWritable> {

	private String stringTerm;
	
	public StringTermWritable() {
	}
	
	public StringTermWritable(IStringTerm stringTerm) {
		this.stringTerm = stringTerm.getValue();
	}
	
	public String getValue() {
		return stringTerm;
	}

	public void setValue(String value) {
		this.stringTerm = value;
	}

	@Override
	public void write(DataOutput out) throws IOException {
		out.writeUTF(stringTerm);
	}

	@Override
	public void readFields(DataInput in) throws IOException {
		stringTerm = in.readUTF();
	}

	public static StringTermWritable read(DataInput in) throws IOException {
		StringTermWritable stringTermWritable = new StringTermWritable();
		String stringTerm = in.readUTF();
		stringTermWritable.stringTerm = stringTerm;
		return stringTermWritable;
	}

	@Override
	public int compareTo(StringTermWritable o) {
		return stringTerm.compareTo(o.stringTerm);
	}

	@Override
	public String toString() {
		return "stringTerm[" + stringTerm + "]";
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof StringTermWritable)) {
			return false;
		}
		StringTermWritable i = (StringTermWritable) obj;
		return new EqualsBuilder().append(stringTerm, i.stringTerm).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(stringTerm).hashCode();
	}

}
