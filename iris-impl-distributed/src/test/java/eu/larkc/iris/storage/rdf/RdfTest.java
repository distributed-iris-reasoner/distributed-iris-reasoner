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
import java.io.FileInputStream;
import java.io.IOException;

import org.ontoware.rdf2go.ModelFactory;
import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.model.Model;
import org.openrdf.rio.RDFFormat;

import cascading.ClusterTestCase;
import cascading.flow.Flow;
import cascading.flow.FlowConnector;
import cascading.operation.Identity;
import cascading.operation.regex.RegexSplitter;
import cascading.pipe.Each;
import cascading.pipe.Pipe;
import cascading.scheme.TextLine;
import cascading.tap.Lfs;
import cascading.tap.SinkMode;
import cascading.tap.Tap;
import cascading.tuple.Fields;
import cascading.tuple.TupleEntryIterator;
import eu.larkc.iris.storage.rdf.RdfScheme;
import eu.larkc.iris.storage.rdf.RdfTap;

/**
 *
 */
public class RdfTest extends ClusterTestCase {
	String inputFile = "output/humans.txt";
	private Model repositoryModel = null; 
		
	public RdfTest() {
		super("rdf tap test", false);
	}

	@Override
	public void setUp() throws IOException {
		super.setUp();

		/*
		RDF2Go.register("org.openrdf.rdf2go.RepositoryModelFactory");
		ModelFactory modelFactory = RDF2Go.getModelFactory();
		repositoryModel = (RepositoryModel) modelFactory
				.createModel();
		// RepositoryModel repositoryModel = new RepositoryModel(repository);
		repositoryModel.open();

		repositoryModel.readFrom(new FileInputStream(new File(inputFile)),
				RDFFormat.RDFXML, "");
		*/
	}

	@Override
	public void tearDown() throws IOException {
		super.tearDown();

		//repositoryModel.close();
	}

	public void testRdf() throws IOException {

		// CREATE NEW TABLE FROM SOURCE

		Tap source = new Lfs(new TextLine(), inputFile);

		Pipe parsePipe = new Each("insert", new Fields("line"),
				new RegexSplitter(new Fields("value1", "value2", "value3"), "\\t"));

		Tap replaceTap = new RdfTap("name", new RdfScheme(new Fields("value1", "value2", "value3")), SinkMode.REPLACE);

		Flow parseFlow = new FlowConnector(getProperties()).connect(source,
				replaceTap, parsePipe);

		parseFlow.complete();

		verifySink(parseFlow, 14);

		// READ DATA FROM TABLE INTO TEXT FILE

		// create flow to read from hbase and save to local file
		/*
		Tap sink = new Lfs(new TextLine(), "build/test/jdbc", SinkMode.REPLACE);

		Pipe copyPipe = new Each("read", new Identity());

		Flow copyFlow = new FlowConnector(getProperties()).connect(replaceTap,
				sink, copyPipe);

		copyFlow.complete();

		verifySink(copyFlow, 13);
		*/
		
		// READ DATA FROM TEXT FILE AND UPDATE TABLE
		/*
		RdfScheme rdfScheme = new RdfScheme();
		Tap updateTap = new RdfTap("name", rdfScheme, SinkMode.UPDATE);

		Flow updateFlow = new FlowConnector(getProperties()).connect(sink,
				updateTap, parsePipe);

		updateFlow.complete();

		verifySink(updateFlow, 13);

		// READ DATA FROM TABLE INTO TEXT FILE, USING CUSTOM QUERY

		Tap sourceTap = new RdfTap("name", new RdfScheme(), SinkMode.UPDATE);

		Pipe readPipe = new Each("read", new Identity());

		Flow readFlow = new FlowConnector(getProperties()).connect(sourceTap,
				sink, readPipe);

		readFlow.complete();

		verifySink(readFlow, 13);
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