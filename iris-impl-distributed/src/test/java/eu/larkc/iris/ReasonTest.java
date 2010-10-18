package eu.larkc.iris;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.StringTokenizer;

import org.deri.iris.evaluation.ProgramEvaluationTest;
import org.deri.iris.rules.compiler.Helper;
import org.deri.iris.storage.IRelation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.larkc.iris.storage.rdf.ExtractRdfTriples;

/**
 * @author valer
 *
 */
public class ReasonTest extends ProgramEvaluationTest {

	private static final Logger logger = LoggerFactory.getLogger(ReasonTest.class);
	
	public ReasonTest(String name) {
		super(name);
	}

	@Override
	protected Collection<String> createExpressions() {
		
		Collection<String> expressions = new ArrayList<String>();

		// Create facts.
		File file = new File("/home/valer/Projects/eu.larkc.reasoner/workspace/pariris/iris-impl-distributed/output/humans.txt");
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(file));
		} catch (FileNotFoundException e) {
			throw new RuntimeException("file not found", e);
		}
		String line = null;
		try {
			while ((line = br.readLine()) != null) {
				StringTokenizer st = new StringTokenizer(line, "\t");
				String subject = prepareForDatalog(st.nextToken());
				String predicate = prepareForDatalog(st.nextToken());
				String object = prepareForDatalog(st.nextToken());
				
				logger.info(predicate + "('" + subject + "', '" + object + "').");
				
				expressions.add(predicate + "('" + subject + "', '" + object + "').");
			}
			br.close();
		} catch (IOException e) {
			throw new RuntimeException("io exception", e);
		}

		// Create rules.
		expressions.add("son(?X, ?Y) :- parent(?Y, ?X), sex(?X, 'male').");
		
		return expressions;
	}

	private String prepareForDatalog(String input) {
		input = input.replace("://", ".c.");
		input = input.replace("/", ".s.");
		input = input.replace("\"", ".g.");
		return input;
	}
	
	public void testAtOffSite() throws Exception {
		/*
			each filter brother(y, x) - > brian_connery, sean_connery
		 */
		IRelation relation = evaluate("?- son(?X, ?Y).");
		
		assertNotNull(relation);
		/*
		assertTrue("A1 not in relation.", relation.contains(Helper
				.createConstantTuple("A1")));
		assertTrue("B1 not in relation.", relation.contains(Helper
				.createConstantTuple("B1")));
		assertTrue("B2 not in relation.", relation.contains(Helper
				.createConstantTuple("B2")));

		assertEquals("Relation does not have correct size", 3, relation.size());
		*/
	}

}
