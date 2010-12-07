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
import org.deri.iris.api.factory.IBasicFactory;
import org.deri.iris.api.factory.IConcreteFactory;
import org.deri.iris.api.factory.ITermFactory;
import org.deri.iris.api.terms.ITerm;
import org.deri.iris.basics.BasicFactory;
import org.deri.iris.terms.TermFactory;
import org.deri.iris.terms.concrete.ConcreteFactory;
import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.impl.TriplePatternImpl;
import org.ontoware.rdf2go.model.node.NodeOrVariable;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.ResourceOrVariable;
import org.ontoware.rdf2go.model.node.impl.URIImpl;

import eu.larkc.iris.storage.FactsStorage;

/**
 * @author valer
 *
 */
public class RdfStorage implements FactsStorage {

	private Model model;
	private String predicateFilter;

	private ClosableIterator<Statement> iterator;
	
	@Override
	public IAtom next() {
		if (!model.isOpen()) {
			model.open();
		}
		if (iterator == null) {
			if (predicateFilter != null) {
				iterator = model.findStatements(new TriplePatternImpl((ResourceOrVariable) null, new URIImpl(predicateFilter), (NodeOrVariable) null));
			} else {
				iterator = model.iterator();
			}
		}
		if (!iterator.hasNext()) {
			if (model.isOpen()) {
				model.close();
			}
			return null;
		}
		Statement statement = iterator.next();
		ITermFactory termFactory = TermFactory.getInstance();
		IBasicFactory basicFactory = BasicFactory.getInstance();
		IConcreteFactory concreteFactory = ConcreteFactory.getInstance();
		ITerm object = null;
		if (statement.getObject() instanceof Resource) {
			object = concreteFactory.createIri(statement.getObject().toString());
		} else {
			object = termFactory.createString(statement.getObject().toString());
		}
		RdfAtom rdfAtom = new RdfAtom(basicFactory.createPredicate(statement.getPredicate().toString(), 2), 
				concreteFactory.createIri(statement.getSubject().toString()), object);
		return rdfAtom;
	}

	public Model getModel() {
		return model;
	}

	public void setModel(Model model) {
		this.model = model;
	}

	public String getPredicateFilter() {
		return predicateFilter;
	}

	public void setPredicateFilter(String predicatefilter) {
		this.predicateFilter = predicatefilter;
	}
	
}
