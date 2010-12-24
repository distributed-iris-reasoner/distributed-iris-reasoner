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

import org.ontoware.rdf2go.model.ModelSet;

import eu.larkc.iris.storage.AtomRecord;
import eu.larkc.iris.storage.FactsRecordWriter;

/** A RecordWriter that writes the reduce output to a SQL table */
public class RdfRecordWriter<K extends AtomRecord, V> extends FactsRecordWriter<K, V> {
	
	protected RdfRecordWriter(ModelSet model) {
		super();
		factsStorage = new RdfStorage();
		((RdfStorage) factsStorage).setModel(model);
	}
	
}
