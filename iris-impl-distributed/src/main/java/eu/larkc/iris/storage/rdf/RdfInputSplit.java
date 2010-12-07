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

package eu.larkc.iris.storage.rdf;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import eu.larkc.iris.storage.FactsInputSplit;

public class RdfInputSplit extends FactsInputSplit {

	private String contextURI = null;
	private long size = 0;
	
	public RdfInputSplit() {
	}
	
	public RdfInputSplit(String contextURI, long size) {
		this.contextURI = contextURI;
		this.size = size;
	}

	public String getContextURI() {
		return contextURI;
	}

	public long getSize() {
		return size;
	}

	@Override
	public long getLength() throws IOException {
		return size;
	}

	@Override
	public void write(DataOutput out) throws IOException {
		out.writeUTF(contextURI == null ? "" : contextURI);
		out.writeLong(size);
	}

	@Override
	public void readFields(DataInput in) throws IOException {
		String inContextURI = in.readUTF();
		contextURI = inContextURI.equals("") ? null : inContextURI;
		size = in.readLong();
	}

}
