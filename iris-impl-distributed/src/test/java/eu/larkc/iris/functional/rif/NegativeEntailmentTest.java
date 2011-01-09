/**
 * 
 */
package eu.larkc.iris.functional.rif;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Executes all negative entailment test cases of the Rif-Core Test suite.
 * 
 * @author Florian Fischer
 */
@RunWith(Parameterized.class)
public class NegativeEntailmentTest extends RifCoreTest {

	private static final String NEGATIVE_ENTAILMENT_DIR = "rif/NegativeEntailmentTest/";

	private static final boolean EXPECTED_EVALUATION = false;

	public NegativeEntailmentTest(String testDirectory, String testCaseName,
			boolean expectedEvaluation) {
		super(testCaseName, testDirectory, expectedEvaluation);
	}	

	@Parameters
	public static Collection<Object[]> data() throws FileNotFoundException {
		List<Object[]> data = new ArrayList<Object[]>();

		File directory = new File("src/test/resources/"
				+ NEGATIVE_ENTAILMENT_DIR);

		if (!directory.exists()) {
			throw new FileNotFoundException(
					"Could not locate directory containing the test cases: "
							+ directory);
		}

		File[] testCaseDirectories = directory.listFiles();

		for (File testCaseDirectory : testCaseDirectories) {
			String testCaseName = testCaseDirectory.getName();

			if (testCaseName.startsWith(".")) {
				continue;
			}

			data.add(new Object[] { "/" + NEGATIVE_ENTAILMENT_DIR, testCaseName,
					EXPECTED_EVALUATION });
		}

		return data;
	}

}
