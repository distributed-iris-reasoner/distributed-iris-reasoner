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
import org.deri.iris.api.factory.ITermFactory;
import org.deri.iris.basics.BasicFactory;
import org.deri.iris.terms.TermFactory;
import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Statement;

import eu.larkc.iris.storage.FactsStorage;

/**
 * @author valer
 *
 */
public class RdfStorage implements FactsStorage {

	private Model model;

	private ClosableIterator<Statement> iterator;
	
	@Override
	public IAtom next() {
		if (iterator == null) {
			iterator = model.iterator();
		}
		if (!iterator.hasNext()) {
			return null;
		}
		Statement statement = iterator.next();
		ITermFactory termFactory = TermFactory.getInstance();
		IBasicFactory basicFactory = BasicFactory.getInstance();
		RdfAtom rdfAtom = new RdfAtom(basicFactory.createPredicate(statement.getPredicate().toString(), 2), 
				termFactory.createString(statement.getSubject().toString()), termFactory.createString(statement.getObject().toString()));
		return rdfAtom;
	}

	public Model getModel() {
		return model;
	}

	public void setModel(Model model) {
		this.model = model;
	}
	
}
