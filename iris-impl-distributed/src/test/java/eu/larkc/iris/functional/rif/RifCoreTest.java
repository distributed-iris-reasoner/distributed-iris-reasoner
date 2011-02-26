/**
 * 
 */
package eu.larkc.iris.functional.rif;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import junit.framework.Assert;

import org.deri.iris.EvaluationException;
import org.deri.iris.api.basics.IQuery;
import org.deri.iris.storage.IRelation;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.sti2.rif4j.condition.Formula;
import at.sti2.rif4j.parser.xml.XmlParser;
import at.sti2.rif4j.translator.iris.RifToIrisTranslator;
import at.sti2.rif4j.translator.iris.visitor.DocumentTranslator;

import cascading.tuple.TupleEntry;
import cascading.tuple.TupleEntryIterator;

import eu.larkc.iris.CascadingTest;
import eu.larkc.iris.evaluation.EvaluationContext;
import eu.larkc.iris.functional.InitAndStartupTest;
import eu.larkc.iris.imports.Importer;
import eu.larkc.iris.rules.compiler.CascadingRuleCompiler;
import eu.larkc.iris.rules.compiler.FlowAssembly;
import eu.larkc.iris.rules.compiler.IDistributedCompiledRule;

/**
 * @author Florian Fischer
 */
public abstract class RifCoreTest extends CascadingTest {

	private static final Logger logger = LoggerFactory
			.getLogger(RifCoreTest.class);

	private static final String PREMISE_SUFFIX = "-premise";

	private static final String CONCLUSION_SUFFIX = "-conclusion";

	private static final String NONCONCLUSION_SUFFIX = "-nonconclusion";

	private static final String FILE_EXTENSION = ".rif";

	private String testDirectory;

	private String testCaseName;

	private boolean expectedEvaluation;

	private String premiseRulesFile;

	private String conclusionFile;

	public RifCoreTest(String testName, String directory, boolean expectedResult) {
		super(testName, false);
		this.testCaseName = testName;
		this.testDirectory = directory + testCaseName;
		this.expectedEvaluation = expectedResult;

		String premiseFileName = testCaseName + PREMISE_SUFFIX + FILE_EXTENSION;
		premiseRulesFile = testDirectory + "/" + premiseFileName;

		String conclusionSuffix = expectedEvaluation ? CONCLUSION_SUFFIX
				: NONCONCLUSION_SUFFIX;
		String conclusionFileName = testCaseName + conclusionSuffix
				+ FILE_EXTENSION;
		conclusionFile = testDirectory + "/" + conclusionFileName;
	}

	@Override
	@Before
	public void setUp() throws Exception {
		super.setUp();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * eu.larkc.iris.evaluation.distributed.ProgramEvaluationTest#getRulesFile()
	 */
	@Override
	protected String getRulesFile() {
		return premiseRulesFile;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see eu.larkc.iris.evaluation.distributed.EvaluationTest#createFacts()
	 */
	@Override
	protected void createFacts() throws IOException {
		// TODO: I think there is currently an issue in RIF4J concerned with
		// processing of imported facts.

		// E.g.: For a document containing
		// Import(
		// <http://www.w3.org/2005/rules/test/repository/tc/RDF_Combination_Constant_Equivalence_2/RDF_Combination_Constant_Equivalence_2-import001>
		// <http://www.w3.org/ns/entailment/Simple> )
		// RifToIrisTranslator does return facts correctly because the
		// DocumentTranslator does not handle imports at the moment. The problem
		// might involve the XMLExctractor class as well.
		// The best solution would be if imports could just be extracted as
		// strings since they need to be pushed to Hadoop anyway.

		// For now we would have to set that up manually.
		// So for now we have to hardcode that.

		// defaultConfiguration.project = "test";
		// if (enableCluster) {
		// new Importer().importFromFile(defaultConfiguration,
		// defaultConfiguration.project,
		// this.getClass().getResource("/facts/default.nt").getPath(),
		// "import");
		// } else {
		// new Importer().processNTriple(defaultConfiguration,
		// this.getClass().getResource("/facts/default.nt").getPath(),
		// defaultConfiguration.project, "import");
		// }
	}

	@Test
	public void testEntailment() throws Exception {

		CascadingRuleCompiler crc = new CascadingRuleCompiler(
				defaultConfiguration);
		IDistributedCompiledRule dcr;
		try {
			dcr = crc.compile(rules.get(0));

			dcr.evaluate(new EvaluationContext(1, 1, 1));
			FlowAssembly fa = dcr.getFlowAssembly();

			// parse conclusion to a Formula object
			Formula conclusion = parseFormula(conclusionFile);
			List<IQuery> queries = toQueries(conclusion);

			boolean contained = entails(queries);
			// check if conclusion is contained in facts after inference
			Assert.assertEquals(testCaseName, expectedEvaluation, contained);		 
		} catch (Exception e) {
			logger.error("Testcase failed: " + testCaseName, e);
			throw e;
		}	
	}

	private boolean entails(List<IQuery> queries) throws Exception {
		for (IQuery iQuery : queries) {
			IRelation result = evaluate(iQuery);

			if (result.size() == 0) {
				return false; // realistically we do not need to process the
								// whole collection
			}
			return true;
		}
		return false;
	}

	private List<IQuery> toQueries(Formula formula) {
		RifToIrisTranslator translator = new RifToIrisTranslator();
		translator.translate(formula);

		List<IQuery> queries = translator.getQueries();

		return queries;
	}

	private Formula parseFormula(String fileName) {
		XmlParser parser = new XmlParser();

		Reader reader = getFileReader(fileName);
		Formula formula = null;

		try {
			formula = parser.parseFormula(reader);
		} catch (Exception e) {
			logger.error("Failed to parse " + fileName, e);
		}

		return formula;
	}

	private Reader getFileReader(String fileName) {
		URI uri = getFileUri(fileName);

		if (uri == null) {
			return null;
		}

		try {
			InputStream input = uri.toURL().openStream();
			return new InputStreamReader(input);
		} catch (IOException e) {
			logger.error("Could not load " + fileName);
		}

		return null;
	}

	private URI getFileUri(String fileName) {
		try {
			URL url = new URL(fileName);
			return url.toURI();
		} catch (MalformedURLException e) {
		} catch (URISyntaxException e) {
		}

		URL url = this.getClass().getClassLoader().getResource(fileName);

		if (url == null) {
			logger.error("Could not find " + fileName);
			return null;
		}

		try {
			return url.toURI();
		} catch (URISyntaxException e) {
			logger.error("Invalid URI", e);
		}

		return null;
	}

}
