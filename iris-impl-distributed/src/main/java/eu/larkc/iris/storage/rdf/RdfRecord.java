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

import org.deri.iris.api.basics.IAtom;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.impl.PlainLiteralImpl;
import org.ontoware.rdf2go.model.node.impl.URIImpl;

import cascading.tuple.Tuple;
import eu.larkc.iris.storage.AtomRecord;
import eu.larkc.iris.storage.FactsStorage;

public class RdfRecord extends AtomRecord {

	public RdfRecord() {}
	
	public RdfRecord(Tuple tuple) {
		super(tuple);
	}
	
	@Override
	public void write(FactsStorage storage) {
		RdfStorage rdfStorage = (RdfStorage) storage;
		Model model = rdfStorage.getModel();
		Resource subject = new URIImpl((String) tuple.get(1));
		String objectTuple = ((String) tuple.get(2));
		Node object = null;
		if (objectTuple.startsWith("'") && objectTuple.endsWith("'")) {
			object = new PlainLiteralImpl(objectTuple.replace("'", ""));
		} else {
			object = new URIImpl(objectTuple);
		}
		Statement statement = model.createStatement(subject, new URIImpl((String) tuple.get(0)), object);
		model.addStatement(statement);
		model.commit();
	}

	@Override
	public void read(IAtom atom) {
		RdfAtom rdfAtom = (RdfAtom) atom;
		tuple = new Tuple();
		tuple.add(rdfAtom.getPredicate().toString());
		for(int i= 0 ; i < rdfAtom.getTuple().size(); i++) {
			tuple.add(rdfAtom.getTuple().get(i).toString());
		}
	}

}
