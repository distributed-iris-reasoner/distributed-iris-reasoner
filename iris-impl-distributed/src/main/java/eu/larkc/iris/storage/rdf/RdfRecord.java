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
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.PlainLiteralImpl;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cascading.tuple.Tuple;
import eu.larkc.iris.storage.AtomRecord;
import eu.larkc.iris.storage.FactsStorage;
import eu.larkc.iris.storage.IRIWritable;
import eu.larkc.iris.storage.StringTermWritable;

public class RdfRecord extends AtomRecord {

	private static final Logger logger = LoggerFactory.getLogger(RdfRecord.class);
	
	public RdfRecord() {}
	
	public RdfRecord(Tuple tuple) {
		super(tuple);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void write(FactsStorage storage) {
		RdfStorage rdfStorage = (RdfStorage) storage;
		ModelSet model = rdfStorage.getModel();
		if (!model.isOpen()) {
			model.open();
		}
		URI predicate = new URIImpl(((IRIWritable) tuple.get(0)).getValue());
		Resource subject = new URIImpl(((IRIWritable) tuple.get(1)).getValue());
		Class[] types = tuple.getTypes();
		//String objectTuple = null;
		Node object = null;
		if (types[2].isAssignableFrom(StringTermWritable.class)) {
			object = new PlainLiteralImpl(((StringTermWritable) tuple.get(2)).getValue());
		} else {
			object = new URIImpl(((IRIWritable) tuple.get(2)).getValue());
		}
		
		/*
		if (objectTuple.startsWith("'") && objectTuple.endsWith("'")) {
			object = new PlainLiteralImpl(objectTuple.replace("'", ""));
		} else {
			object = new URIImpl(objectTuple);
		}
		*/
		Statement statement = model.createStatement(subject, predicate, object);
		model.addStatement(statement);
		logger.info("added statement " + statement);
		model.commit();
	}

}
