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
package eu.larkc.iris.functional;

import java.util.ArrayList;
import java.util.Collection;

import org.deri.iris.storage.IRelation;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.impl.StatementImpl;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.openrdf.rdf2go.RepositoryModel;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.memory.MemoryStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.larkc.iris.storage.FactsConfigurationFactory;
import eu.larkc.iris.storage.FactsFactory;
import eu.larkc.iris.storage.rdf.RdfFactsConfiguration;

/**
 * 
 * @history Oct 26, 2010, fisf, creation
 * @history Nov 09, 2010, vroman, implement a first version
 * @author Florian Fischer
 */
public class InitAndStartupTest extends org.deri.iris.evaluation.ProgramEvaluationTest {

	private static final Logger logger = LoggerFactory.getLogger(InitAndStartupTest.class);

	//p( ?X, ?Y ) :- q( ?X, ?Y ), r( ?Y, ?Z ), s( ?X, ?Z ).
	//q( 1, 2 ).
	//r( 2, 3 ).
	//s( 1, 3 ).
	//?- p(?X, ?Y).

	public InitAndStartupTest(String name) {
		super(name);
	}
	
	private Model createStorage(String storageId) {
		Repository repository = new SailRepository(new MemoryStore());
		try {
			repository.initialize();
		} catch (RepositoryException e) {
			logger.error("error initializing repository" ,e);
			throw new RuntimeException("error initializing repository" ,e);
		}
		Model model = new RepositoryModel(repository);
		model.open();
		RdfFactsConfiguration.memoryRepositoryModels.put(storageId, model);
		
		return model;
	}

	@Override
	protected void createFacts() {
		FactsFactory.PROPERTIES = "/facts-configuration-test.properties";
		
		FactsConfigurationFactory.STORAGE_PROPERTIES = "/facts-storage-configuration-test.properties";
		
		Model model = createStorage("default");
				
		Statement statement = new StatementImpl(null, new URIImpl("http://larkc.eu/1"), 
				new URIImpl("http://larkc.eu/q"), new URIImpl("http://larkc.eu/2"));
		model.addStatement(statement);
		statement = new StatementImpl(null, new URIImpl("http://larkc.eu/2"), 
				new URIImpl("http://larkc.eu/r"), new URIImpl("http://larkc.eu/3"));
		model.addStatement(statement);
		statement = new StatementImpl(null, new URIImpl("http://larkc.eu/1"), 
				new URIImpl("http://larkc.eu/s"), new URIImpl("http://larkc.eu/3"));
		model.addStatement(statement);
		
		model.commit();
	}

	@Override
	protected Collection<String> createExpressions() {
		Collection<String> expressions = new ArrayList<String>();

		// Create rules.
		expressions
				.add("p( ?X, ?Y ) :- q( ?X, ?Y ), r( ?Y, ?Z ), s( ?X, ?Z ).");

		return expressions;
	}

	public void testEvaluation() throws Exception {
		IRelation relation = evaluate("?- p(?X, ?Y).");
	}
	
}
