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

import org.apache.hadoop.mapred.JobConf;
import org.deri.iris.api.basics.IAtom;
import org.deri.iris.api.basics.IPredicate;
import org.deri.iris.api.basics.ITuple;
import org.deri.iris.basics.BasicFactory;
import org.deri.iris.terms.TermFactory;
import org.ontoware.rdf2go.model.ModelSet;
import org.openrdf.rdf2go.RepositoryModelSet;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.memory.MemoryStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cascading.flow.Flow;
import cascading.flow.FlowConnector;
import cascading.flow.MultiMapReducePlanner;
import cascading.operation.Identity;
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
	
	private static final Logger log = LoggerFactory.getLogger(RdfFactsTapTest.class);
	
	transient private static Map<Object, Object> properties = new HashMap<Object, Object>();
	transient private static JobConf jobConf;
	
	int numMapTasks = 4;
	int numReduceTasks = 1;

	private String logger;
	
	public RdfFactsTapTest() {
		super("Facts Tap Tests");

		this.logger = System.getProperty( "log4j.logger" );
	}

	public static void main(String[] args) {
		
	}
	
	public Map<Object, Object> getProperties() {
		return new HashMap<Object, Object>(properties);
	}

	private ModelSet createStorage(String storageId) {
		Repository repository = new SailRepository(new MemoryStore());
		try {
			repository.initialize();
		} catch (RepositoryException e) {
			log.error("error initializing repository" ,e);
			throw new RuntimeException("error initializing repository" ,e);
		}
		ModelSet modelSet = new RepositoryModelSet(repository);
		RdfFactsConfiguration.memoryRepositoryModelSets.put(storageId, modelSet);
		
		return modelSet;
	}
	
	@Override
	public void setUp() throws Exception {
		super.setUp();

		properties.put("cascading.serialization.tokens", "130=eu.larkc.iris.storage.IRIWritable,131=eu.larkc.iris.storage.PredicateWritable,132=eu.larkc.iris.storage.StringTermWritable");
		
		jobConf = new JobConf();

		jobConf.setNumMapTasks(numMapTasks);
		jobConf.setNumReduceTasks(numReduceTasks);

		if (log != null)
			properties.put("log4j.logger", logger);

		Flow.setJobPollingInterval(properties, 500); // should speed up tests

		//jobConf.setJarByClass(FactsTap.class);
		//properties.put("cascading.flowconnector.appjar.path", "jar.jar");
		//jobConf.setJar("./target/iris-impl-distributed-0.7.2.jar");
		MultiMapReducePlanner.setJobConf( properties, jobConf );
		
		FactsFactory.PROPERTIES = "/facts-configuration-test.properties";
		
		FactsConfigurationFactory.STORAGE_PROPERTIES = "/facts-storage-configuration-test.properties";

		ModelSet model = createStorage("humans");

		model.open();
		
		model.readFrom(this.getClass().getResourceAsStream("/facts/humans.rdf"));
		
		model.commit();
		
		createStorage("humans_out");
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
		IPredicate predicate = BasicFactory.getInstance().createPredicate("name", 2);
		ITuple tuple = BasicFactory.getInstance().createTuple(TermFactory.getInstance().createVariable("X"), 
				TermFactory.getInstance().createVariable("Y"));
		IAtom atom = BasicFactory.getInstance().createAtom(predicate, tuple);
		
		FactsFactory humansFactsFactory = FactsFactory.getInstance("humans");
		FactsTap nameFactsTap = humansFactsFactory.getFacts(atom);
		
		String output = "./build/test/output/";
		Tap sink = new Hfs( new Fields("http://www.w3.org/2000/01/rdf-schema#name", "X", "Y"), output , true );

		Map<String, Tap> sources = new HashMap<String, Tap>();
		sources.put("source", nameFactsTap);

		Pipe sourcePipe = new Pipe("source");
		Pipe identity = new Each(sourcePipe, new Fields("http://www.w3.org/2000/01/rdf-schema#name", "X", "Y"), new Identity());
		
		Flow aFlow = new FlowConnector(getProperties()).connect(sources, sink, identity);
		aFlow.complete();
		
		verifySink(aFlow, 6);
	}

	public void testSink() throws IOException {
		//IPredicate predicate = BasicFactory.getInstance().createPredicate("name", 2);
		//ITuple tuple = BasicFactory.getInstance().createTuple(TermFactory.getInstance().createVariable("X"), TermFactory.getInstance().createVariable("Y"));
		//IAtom atom = BasicFactory.getInstance().createAtom(predicate, tuple);
		
		//FactsFactory humansFactsFactory = FactsFactory.getInstance("humans");
		//FactsTap nameFactsTap = humansFactsFactory.getFacts(atom);

		String input = "./build/test/output/";
		Tap source = new Hfs( new Fields("http://www.w3.org/2000/01/rdf-schema#name", "X", "Y"), input, true );

		FactsFactory humansOutFactsFactory = FactsFactory.getInstance("humans_out");
		Tap sink = humansOutFactsFactory.getFacts();

		Map<String, Tap> sources = new HashMap<String, Tap>();
		//sources.put("source", nameFactsTap);
		sources.put("source", source);
		
		Pipe sourcePipe = new Pipe("source");
		Pipe identity = new Each(sourcePipe, new Fields("http://www.w3.org/2000/01/rdf-schema#name", "X", "Y"), new Identity(new Fields("http://www.w3.org/2000/01/rdf-schema#name", "X", "Y")));
		
		Flow aFlow = new FlowConnector(getProperties()).connect(sources, sink, identity);
		aFlow.complete();
		
		verifySink(aFlow, 6);
	}

}