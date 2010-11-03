/*
 * Copyright (c) 2009 Concurrent, Inc.
 *
 * This work has been released into the public domain
 * by the copyright holder. This applies worldwide.
 *
 * In case this is not legally possible:
 * The copyright holder grants any entity the right
 * to use this work for any purpose, without any
 * conditions, unless such conditions are required by law.
 */

package eu.larkc.iris.storage.rdf;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.deri.iris.api.basics.IAtom;
import org.deri.iris.api.basics.IPredicate;
import org.deri.iris.api.basics.ITuple;
import org.deri.iris.basics.BasicFactory;
import org.deri.iris.terms.TermFactory;
import org.openrdf.model.Resource;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.http.HTTPRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;

import cascading.ClusterTestCase;
import cascading.flow.Flow;
import cascading.flow.FlowConnector;
import cascading.operation.Identity;
import cascading.pipe.Each;
import cascading.pipe.Pipe;
import cascading.tap.Tap;
import cascading.tuple.Fields;
import cascading.tuple.TupleEntryIterator;
import eu.larkc.iris.storage.FactsTap;

/**
 *
 */
public class FactsTapTest extends ClusterTestCase {
	
	String serverURL = "http://localhost:8080/openrdf-sesame";
	
	private Repository humansRepository = null;
	private Repository outputHumansRepository = null;
	
	public FactsTapTest() {
		super("facts tap test", false);

		try {
			humansRepository = new HTTPRepository(serverURL, "humans");
			humansRepository.initialize();
			outputHumansRepository = new HTTPRepository(serverURL, "output-humans");
			outputHumansRepository.initialize();
		} catch (RepositoryException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void setUp() throws IOException {
		super.setUp();

		try {
			RepositoryConnection repConnection = humansRepository.getConnection();
			repConnection.add(new File("/home/valer/Projects/eu.larkc.reasoner/workspace/pariris/iris-impl-distributed/src/test/resources/input/humans.rdf"), 
					"", //"http://www.know-center.at/ontologies/2009/2/software-project.owl", 
					RDFFormat.RDFXML, (Resource) null);
			repConnection.commit();
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RDFParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void tearDown() throws IOException {
		super.tearDown();
		
		try {
			RepositoryConnection humansRepConnection = humansRepository.getConnection();
			humansRepConnection.remove((Resource) null, null, null, (Resource) null);
			humansRepConnection.commit();
			RepositoryConnection outputHumansRepConnection = outputHumansRepository.getConnection();
			outputHumansRepConnection.remove((Resource) null, null, null, (Resource) null);
			outputHumansRepConnection.commit();
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void testTupleTapSink() throws IOException {
		IPredicate predicate = BasicFactory.getInstance().createPredicate("http://larkc.eu/humans#name", 3);
		ITuple tuple = BasicFactory.getInstance().createTuple(TermFactory.getInstance().createVariable("X"), 
				TermFactory.getInstance().createVariable("Y"), TermFactory.getInstance().createVariable("Z"));
		ITuple tuple1 = BasicFactory.getInstance().createTuple(TermFactory.getInstance().createVariable("X"),
				TermFactory.getInstance().createVariable("Y"), TermFactory.getInstance().createVariable("Z"));
		IAtom atom = BasicFactory.getInstance().createAtom(predicate, tuple);
		IAtom atom1 = BasicFactory.getInstance().createAtom(predicate, tuple1);
		
		IPredicate headPredicate = BasicFactory.getInstance().createPredicate("http://larkc.eu/humans#sex", 3);
		ITuple headTuple = BasicFactory.getInstance().createTuple(
				TermFactory.getInstance().createVariable("X1"), TermFactory.getInstance().createVariable("Y1"), 
				TermFactory.getInstance().createVariable("Z1"));
		IAtom headAtom = BasicFactory.getInstance().createAtom(headPredicate, headTuple);
		
		Tap sourceTap = new FactsTap(atom);
		Tap source1Tap = new FactsTap(atom1);
		
		Tap sinkTap = new FactsTap(headAtom);
		
		Map<String, Tap> sources = new HashMap<String, Tap>();
		sources.put("p1", sourceTap);
		//sources.put("p2", source1Tap);
		
		//String output = "/home/valer/Projects/eu.larkc.reasoner/workspace/pariris/iris-impl-distributed/build/test/output/";
		//Tap sink = new Hfs( new Fields("F"), output , true );
		//Tap sink = new Hfs( new SequenceFile( new Fields( "F" ) ), output, true );
		
		Pipe pipe1 = new Pipe("p1");
		//Pipe identity = new Each(pipe1, new Fields("X", "Y", "Z"), new FieldJoiner(new Fields("F"), ";"));
		Pipe pipe2 = new Pipe("p2");
		//Pipe identityPipe = new CoGroup("join", pipe1, new Fields("Y"), pipe2, new Fields("Y"), new Fields("X", "Y", "Y1", "Z"));
		Pipe identity = new Each(pipe1, new Fields("X", "Y", "Z"), new Identity(new Fields("X1", "Y1", "Z1")));
		Flow identityFlow = new FlowConnector(getProperties()).connect(sources, sinkTap, identity);

		identityFlow.complete();
		
		//verifySink(updateFlow, 14);

		//identityFlow.writeDOT("/home/valer/flow.dot");

		//parsedLogFlow.start();
		//parsedLogFlow.complete();		
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