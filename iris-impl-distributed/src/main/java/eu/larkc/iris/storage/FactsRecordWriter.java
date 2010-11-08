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

import java.io.IOException;

import org.apache.hadoop.mapred.RecordWriter;
import org.apache.hadoop.mapred.Reporter;


/** A RecordWriter that writes the reduce output to a SQL table */
public class FactsRecordWriter<K extends AtomRecord, V> implements RecordWriter<K, V> {
	protected FactsStorage factsStorage;

	protected FactsRecordWriter() {
	}

	/** {@inheritDoc} */
	public void close(Reporter reporter) throws IOException {
	}

	/** {@inheritDoc} */
	public synchronized void write(K key, V value) throws IOException {
		//FIXME should never be null, but when I write to text file it is, I guess I 
		//should not set in the configuration the output when tap is not sink
		if (key == null) {
			return;
		}
		key.write(this.factsStorage);
	}
}
