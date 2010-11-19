/**
 * 
 */
package eu.larkc.iris;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.hadoop.mapred.JobConf;
import org.ontoware.rdf2go.model.Model;
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
 * @author valer
 *
 */
public abstract class CascadingTest extends ProgramEvaluationTest {

	private static final Logger logger = LoggerFactory.getLogger(CascadingTest.class);
	
	transient private static JobConf jobConf;
	
	int numMapTasks = 4;
	int numReduceTasks = 1;

	transient private static Map<Object, Object> properties = new HashMap<Object, Object>();
	
	public CascadingTest(String name) {
		super(name);
	}
	
	public Map<Object, Object> getProperties() {
		return new HashMap<Object, Object>(properties);
	}

	protected Model createStorage(String storageId) {
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

	protected void verifySink(Flow flow, int expects) throws IOException {
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
