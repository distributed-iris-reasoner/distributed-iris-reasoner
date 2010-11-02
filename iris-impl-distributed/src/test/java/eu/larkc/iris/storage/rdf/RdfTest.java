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
import java.net.URL;
import java.util.Properties;

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
import cascading.operation.aggregator.Count;
import cascading.operation.regex.RegexSplitGenerator;
import cascading.operation.regex.RegexSplitter;
import cascading.pipe.Each;
import cascading.pipe.Every;
import cascading.pipe.GroupBy;
import cascading.pipe.Pipe;
import cascading.scheme.Scheme;
import cascading.scheme.TextLine;
import cascading.tap.Hfs;
import cascading.tap.Lfs;
import cascading.tap.SinkMode;
import cascading.tap.Tap;
import cascading.tuple.Fields;
import cascading.tuple.TupleEntryIterator;
import eu.larkc.iris.functional.DistributedReasoner;
import eu.larkc.iris.storage.RdfTripleScheme;
import eu.larkc.iris.storage.rdf.rdf2go.Rdf2GoConfiguration.RDF2GO_IMPL;

/**
 *
 */
public class RdfTest extends ClusterTestCase {
	
	String serverURL = "http://localhost:8080/openrdf-sesame";
	
	private Repository myRepository = null;
	
	public RdfTest() {
		super("rdf tap test", false);

		try {
			myRepository = new HTTPRepository(serverURL, "humans");
			myRepository.initialize();
		} catch (RepositoryException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void setUp() throws IOException {
		super.setUp();

		try {
			RepositoryConnection repConnection = myRepository.getConnection();
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
			RepositoryConnection repConnection = myRepository.getConnection();
			repConnection.remove((Resource) null, null, null, (Resource) null);
			repConnection.commit();
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void testRdf() throws IOException {

		// CREATE NEW TABLE FROM SOURCE

		Tap source = new RdfTap(RDF2GO_IMPL.SESAME, new URL(serverURL), "humans", 
				new RdfScheme(), SinkMode.KEEP);

		Tap sink = new Lfs(new TextLine(), "build/test/output", SinkMode.REPLACE);

		Pipe copyPipe = new Each("read", new Identity());

		Flow copyFlow = new FlowConnector(getProperties()).connect(source, sink, copyPipe);

		copyFlow.complete();

		verifySink(copyFlow, 14);

		// READ DATA FROM TEXT FILE AND UPDATE TABLE
		Tap updateTap = new RdfTap(RDF2GO_IMPL.SESAME, new URL(serverURL), "ouput-humans", 
				new RdfScheme(), SinkMode.KEEP);

		Pipe parsePipe = new Each("insert", new Fields("line"),
				new RegexSplitter(new Fields("value1", "value2", "value3"), "\\t"));

		Flow updateFlow = new FlowConnector(getProperties()).connect(sink,
				updateTap, parsePipe);

		updateFlow.complete();

		verifySink(updateFlow, 14);

		updateFlow.writeDOT("/home/valer/flow.dot");
		
		// READ DATA FROM TABLE INTO TEXT FILE, USING CUSTOM QUERY
		/*
		Tap sourceTap = new RdfTap("name", new RdfScheme(), SinkMode.UPDATE);

		Pipe readPipe = new Each("read", new Identity());

		Flow readFlow = new FlowConnector(getProperties()).connect(sourceTap,
				sink, readPipe);

		readFlow.complete();

		verifySink(readFlow, 13);
		*/
	}

	public void testWordCount() throws IOException {
		Tap sourceTap = new RdfTap(RDF2GO_IMPL.SESAME, new URL(serverURL), "ouput-humans", 
				new RdfScheme(), SinkMode.REPLACE);

		Tap sinkTap = new Hfs(new TextLine(), "build/test/output", SinkMode.REPLACE);

		Pipe wordCountPipe = new Each("identity tuple", new Fields("subject", "predicate", "object"), new Identity());
		
		Pipe value1Pipe = new Each(wordCountPipe, new Fields("subject"), new Identity(new Fields("value")));
		Pipe value2Pipe = new Each(wordCountPipe, new Fields("predicate"), new Identity(new Fields("value")));
		Pipe value3Pipe = new Each(wordCountPipe , new Fields("object"), new Identity(new Fields("value")));
		
		wordCountPipe = new GroupBy(new Pipe[]{value1Pipe, value2Pipe, value3Pipe}, new Fields("value"));
		wordCountPipe = new Every(wordCountPipe, new Count(new Fields("count")), new Fields("count", "value"));

		Flow wordCountFlow = new FlowConnector(getProperties()).connect(sourceTap, sinkTap, wordCountPipe);

		wordCountFlow.complete();
		
		//verifySink(updateFlow, 14);

		wordCountFlow.writeDOT("/home/valer/flow.dot");

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