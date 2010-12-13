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

import org.apache.hadoop.io.WritableComparable;
import org.deri.iris.api.terms.IStringTerm;
import org.deri.iris.terms.TermFactory;

/**
 * @author vroman
 *
 */
public class StringTermWritable implements WritableComparable<StringTermWritable> {

	private IStringTerm stringTerm;
	
	public StringTermWritable() {
		TermFactory.getInstance().createString("");
	}
	
	public StringTermWritable(IStringTerm stringTerm) {
		this.stringTerm = stringTerm;
	}
	
	public IStringTerm getValue() {
		return stringTerm;
	}

	@Override
	public void write(DataOutput out) throws IOException {
		out.writeUTF(stringTerm.getValue());
	}

	@Override
	public void readFields(DataInput in) throws IOException {
		stringTerm = TermFactory.getInstance().createString(in.readUTF());
	}

	public static StringTermWritable read(DataInput in) throws IOException {
		StringTermWritable stringTermWritable = new StringTermWritable(TermFactory.getInstance().createString(in.readUTF()));
		return stringTermWritable;
	}

	@Override
	public int compareTo(StringTermWritable o) {
		return stringTerm.compareTo(o.stringTerm);
	}

}
