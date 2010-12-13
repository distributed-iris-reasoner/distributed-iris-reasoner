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
import org.deri.iris.api.basics.IPredicate;
import org.deri.iris.api.terms.IStringTerm;
import org.deri.iris.api.terms.ITerm;
import org.deri.iris.api.terms.concrete.IIri;
import org.ontoware.rdf2go.model.Model;
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
import eu.larkc.iris.storage.PredicateWritable;
import eu.larkc.iris.storage.StringTermWritable;

public class RdfRecord extends AtomRecord {

	private static final Logger logger = LoggerFactory.getLogger(RdfRecord.class);
	
	public RdfRecord() {}
	
	public RdfRecord(Tuple tuple) {
		super(tuple);
	}
	
	@Override
	public void write(FactsStorage storage) {
		RdfStorage rdfStorage = (RdfStorage) storage;
		Model model = rdfStorage.getModel();
		URI predicate = new URIImpl(((PredicateWritable) tuple.get(0)).getValue().getPredicateSymbol());
		Resource subject = new URIImpl(((IRIWritable) tuple.get(1)).getValue().getValue());
		Class[] types = tuple.getTypes();
		//String objectTuple = null;
		Node object = null;
		if (types[2].isAssignableFrom(StringTermWritable.class)) {
			object = new PlainLiteralImpl(((StringTermWritable) tuple.get(2)).getValue().getValue());
		} else {
			object = new URIImpl(((IRIWritable) tuple.get(2)).getValue().getValue());
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
		logger.info("added statement " + statement + " on contextURI " + model.getContextURI());
		model.commit();
	}

	@Override
	public void read(IAtom atom) {
		RdfAtom rdfAtom = (RdfAtom) atom;
		tuple = new Tuple();
		tuple.add(new PredicateWritable(rdfAtom.getPredicate()));
		for(int i= 0 ; i < rdfAtom.getTuple().size(); i++) {
			ITerm term = rdfAtom.getTuple().get(i);
			if (term instanceof IIri) {
				tuple.add(new IRIWritable((IIri) term));
			} else {
				tuple.add(new StringTermWritable((IStringTerm) term));
			}
		}
	}

}
