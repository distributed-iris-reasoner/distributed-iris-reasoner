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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.deri.iris.api.basics.IAtom;
import org.deri.iris.api.basics.IPredicate;
import org.deri.iris.api.basics.ITuple;
import org.deri.iris.basics.BasicFactory;
import org.deri.iris.terms.TermFactory;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.impl.StatementImpl;
import org.ontoware.rdf2go.model.node.impl.PlainLiteralImpl;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.openrdf.rdf2go.RepositoryModel;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.memory.MemoryStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cascading.flow.Flow;
import cascading.flow.FlowConnector;
import cascading.operation.Identity;
import cascading.operation.text.FieldJoiner;
import cascading.pipe.Each;
import cascading.pipe.Pipe;
import cascading.tap.Hfs;
import cascading.tap.Tap;
import cascading.tuple.Fields;
import cascading.tuple.TupleEntryIterator;
import eu.larkc.iris.storage.FactsConfigurationFactory;
import eu.larkc.iris.storage.FactsFactory;
import eu.larkc.iris.storage.FactsTap;

/**
 * Tests for The Facts Tap
 */
public class RdfFactsTapTest extends TestCase {
	
	private static final Logger logger = LoggerFactory.getLogger(RdfFactsTapTest.class);
	
	public RdfFactsTapTest() {
		super("Facts Tap Tests");

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
	public void setUp() throws Exception {
		super.setUp();

		FactsFactory.PROPERTIES = "/facts-configuration-test.properties";
		
		FactsConfigurationFactory.STORAGE_PROPERTIES = "/facts-storage-configuration-test.properties";
		
		Model model = createStorage("humans");
				
		Statement statement = new StatementImpl(new URIImpl("http://larkc.eu/humans"), new URIImpl("http://larkc.eu/humans/person#gerard_butler"), 
				new URIImpl("http://larkc.eu/humans/name"), new PlainLiteralImpl("Gerard Butler"));
		model.addStatement(statement);
		
		model.commit();		
	}

	@Override
	public void tearDown() throws Exception {
		super.tearDown();		
	}

	private void verifySink(Flow flow, int expects) throws IOException {
		int count = 0;

		TupleEntryIterator iterator = flow.openSink();

		while (iterator.hasNext()) {
			count++;
			System.out.println("iterator.next() = " + iterator.next());
		}

		iterator.close();

		assertEquals("wrong number of values", expects, count);
	}

	public void testSource() throws IOException {
		IPredicate predicate = BasicFactory.getInstance().createPredicate("http://larkc.eu/humans/name", 3);
		ITuple tuple = BasicFactory.getInstance().createTuple(TermFactory.getInstance().createVariable("X"), 
				TermFactory.getInstance().createVariable("Y"), TermFactory.getInstance().createVariable("Z"));
		IAtom atom = BasicFactory.getInstance().createAtom(predicate, tuple);
		
		FactsFactory humansFactsFactory = FactsFactory.getInstance("humans");
		FactsTap nameFactsTap = humansFactsFactory.getFacts(atom);
		
		String output = "/home/valer/Projects/eu.larkc.reasoner/workspace/pariris/iris-impl-distributed/build/test/output/";
		Tap sink = new Hfs( new Fields("F"), output , true );

		Map<String, Tap> sources = new HashMap<String, Tap>();
		sources.put("source", nameFactsTap);

		Pipe sourcePipe = new Pipe("source");
		Pipe identity = new Each(sourcePipe, new Fields("X", "Y", "Z"), new FieldJoiner(new Fields("F"), ";"));
		
		Flow aFlow = new FlowConnector().connect(sources, sink, identity);
		aFlow.complete();
		
		verifySink(aFlow, 1);
	}

	public void testSink() throws IOException {
		IPredicate predicate = BasicFactory.getInstance().createPredicate("http://larkc.eu/humans/name", 3);
		ITuple tuple = BasicFactory.getInstance().createTuple(TermFactory.getInstance().createVariable("X"), 
				TermFactory.getInstance().createVariable("Y"), TermFactory.getInstance().createVariable("Z"));
		IAtom atom = BasicFactory.getInstance().createAtom(predicate, tuple);
		
		FactsFactory humansFactsFactory = FactsFactory.getInstance("humans");
		FactsTap nameFactsTap = humansFactsFactory.getFacts(atom);
		
		Tap sink = humansFactsFactory.getFacts();

		Map<String, Tap> sources = new HashMap<String, Tap>();
		sources.put("source", nameFactsTap);

		Pipe sourcePipe = new Pipe("source");
		Pipe identity = new Each(sourcePipe, new Fields("X", "Y", "Z"), new Identity(new Fields("X", "Y", "Z")));
		
		Flow aFlow = new FlowConnector().connect(sources, sink, identity);
		aFlow.complete();
		
		verifySink(aFlow, 1);
	}

}