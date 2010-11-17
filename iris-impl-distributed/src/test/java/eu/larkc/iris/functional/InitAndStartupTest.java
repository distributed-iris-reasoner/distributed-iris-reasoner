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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.hadoop.mapred.JobConf;
import org.deri.iris.storage.IRelation;
import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.impl.StatementImpl;
import org.ontoware.rdf2go.model.impl.TriplePatternImpl;
import org.ontoware.rdf2go.model.node.NodeOrVariable;
import org.ontoware.rdf2go.model.node.ResourceOrVariable;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.openrdf.rdf2go.RepositoryModel;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.memory.MemoryStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cascading.flow.Flow;
import cascading.flow.MultiMapReducePlanner;
import cascading.tuple.TupleEntryIterator;
import eu.larkc.iris.evaluation.distributed.ProgramEvaluationTest;
import eu.larkc.iris.storage.FactsConfigurationFactory;
import eu.larkc.iris.storage.FactsFactory;
import eu.larkc.iris.storage.rdf.RdfFactsConfiguration;

/**
 * 
 * @history Oct 26, 2010, fisf, creation
 * @history Nov 09, 2010, vroman, implement a first version
 * @author Florian Fischer
 */
public class InitAndStartupTest extends ProgramEvaluationTest {

	private static final Logger logger = LoggerFactory.getLogger(InitAndStartupTest.class);

	transient private static JobConf jobConf;
	
	int numMapTasks = 4;
	int numReduceTasks = 1;

	//p( ?X, ?Y ) :- q( ?X, ?Y ), r( ?Y, ?Z ), s( ?X, ?Z ).
	//q( 1, 2 ).
	//r( 2, 3 ).
	//s( 1, 3 ).
	//?- p(?X, ?Y).

	transient private static Map<Object, Object> properties = new HashMap<Object, Object>();
	
	public Map<Object, Object> getProperties() {
		return new HashMap<Object, Object>(properties);
	}

	private Model model = createStorage("default");
	
	public InitAndStartupTest(String name) {
		super(name);
	}
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		jobConf = new JobConf();

		jobConf.setNumMapTasks(numMapTasks);
		jobConf.setNumReduceTasks(numReduceTasks);

		Flow.setJobPollingInterval(properties, 500); // should speed up tests

		MultiMapReducePlanner.setJobConf( properties, jobConf );
		
		FactsFactory.PROPERTIES = "/facts-configuration-test.properties";
		
		FactsConfigurationFactory.STORAGE_PROPERTIES = "/facts-storage-configuration-test.properties";
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
	protected  void createFacts() {
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
		//expressions.add("p( ?X, ?Y ) :- q( ?X, ?Y ), r( ?Y, ?Z ).");
		//later arbitrary joins
		expressions.add("p( ?X, ?Y ) :- q( ?X, ?Y ), r( ?Y, ?Z ), s( ?X, ?Z ).");

		return expressions;
	}

	public void testEvaluation() throws Exception {
		IRelation relation = evaluate("?- p(?X, ?Y).");
		
		ClosableIterator<Statement> iterator = model.findStatements(new TriplePatternImpl((ResourceOrVariable) null, 
				new URIImpl("http://larkc.eu/p"), (NodeOrVariable) null));
		assertTrue("no data", iterator.hasNext());
		Statement statement = iterator.next();
		logger.info("result : " + statement);
		
		/*
		IPredicate qPredicate = BasicFactory.getInstance().createPredicate("http://larkc.eu/q", 2);
		ITuple qTuple = BasicFactory.getInstance().createTuple(TermFactory.getInstance().createVariable("X"), 
				TermFactory.getInstance().createVariable("Y"));
		IAtom qAtom = BasicFactory.getInstance().createAtom(qPredicate, qTuple);

		IPredicate rPredicate = BasicFactory.getInstance().createPredicate("http://larkc.eu/r", 2);
		ITuple rTuple = BasicFactory.getInstance().createTuple(TermFactory.getInstance().createVariable("Y1"), 
				TermFactory.getInstance().createVariable("Z"));
		IAtom rAtom = BasicFactory.getInstance().createAtom(rPredicate, rTuple);

		FactsFactory defaultFactsFactory = FactsFactory.getInstance("default");
		FactsTap qFactsTap = defaultFactsFactory.getFacts(qAtom);
		FactsTap rFactsTap = defaultFactsFactory.getFacts(rAtom);
		
		Tap sink = defaultFactsFactory.getFacts();

		Map<String, Tap> sources = new HashMap<String, Tap>();
		sources.put("q", qFactsTap);
		sources.put("r", rFactsTap);
		
		Pipe qPipe = new Pipe("q");
		Pipe rPipe = new Pipe("r");
		Pipe join = new CoGroup( qPipe, new Fields("Y"), rPipe, new Fields("Y1"), new Fields("P1", "X1", "Y1", "P2", "Y2", "Z2"), new InnerJoin() );
		
		join = new Each( join , new Insert( new Fields("P3"), "http://larkc.eu/p"), Fields.ALL );
		
		join = new Each( join, new Fields("P3", "X1", "Y1"), new Identity(new Fields( "P3", "X1", "Y1" )));
		
		//Pipe identity = new Each(qPipe, new Fields("X", "Y", "Z"), new Identity(new Fields("X", "Y", "Z")));
		//join = new Each(join, DebugLevel.VERBOSE, new Debug(true));
		
		Flow aFlow = new FlowConnector(getProperties()).connect(sources, sink, join);
		aFlow.complete();
		
		verifySink(aFlow, 4);
		*/

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


}