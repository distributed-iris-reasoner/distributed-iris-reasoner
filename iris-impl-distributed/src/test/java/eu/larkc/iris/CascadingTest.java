/**
 * 
 */
package eu.larkc.iris;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MiniMRCluster;
import org.ontoware.rdf2go.model.Model;
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
import cascading.operation.DebugLevel;
import cascading.tuple.TupleEntryIterator;
import eu.larkc.iris.evaluation.bottomup.DistributedBottomUpEvaluationStrategyFactory;
import eu.larkc.iris.evaluation.bottomup.naive.DistributedNaiveEvaluatorFactory;
import eu.larkc.iris.evaluation.distributed.ProgramEvaluationTest;
import eu.larkc.iris.storage.FactsConfigurationFactory;
import eu.larkc.iris.storage.FactsFactory;
import eu.larkc.iris.storage.rdf.RdfFactsConfiguration;

/**
 * @author valer
 *
 */
public abstract class CascadingTest extends ProgramEvaluationTest {

	public static final String CLUSTER_TESTING_PROPERTY = "test.cluster.enabled";
	
	private static final Logger logger = LoggerFactory.getLogger(CascadingTest.class);
	
	transient private static MiniDFSCluster dfs;
	protected static FileSystem fileSys;
	transient private static MiniMRCluster mr;
	protected JobConf jobConf;
	protected boolean enableCluster;
	
	int numMapTasks = 1;
	int numReduceTasks = 1;
	
	private String log;
	
	public CascadingTest(String name, boolean enableCluster) {
		super(name);
		
		if (!enableCluster)
			this.enableCluster = false;
		else
			this.enableCluster = Boolean.parseBoolean(System.getProperty(
					CLUSTER_TESTING_PROPERTY, Boolean.toString(enableCluster)));

		this.log = System.getProperty("log4j.logger");
	}
	
	public CascadingTest(String string, boolean enableCluster,
			int numMapTasks, int numReduceTasks) {
		this(string, enableCluster);
		this.numMapTasks = numMapTasks;
		this.numReduceTasks = numReduceTasks;
	}

	public CascadingTest(String string) {
		super(string);
	}

	public boolean isEnableCluster() {
		return enableCluster;
	}

	protected ModelSet createStorage(String storageId) {
		Repository repository = new SailRepository(new MemoryStore());
		try {
			repository.initialize();
		} catch (RepositoryException e) {
			logger.error("error initializing repository" ,e);
			throw new RuntimeException("error initializing repository" ,e);
		}
		ModelSet modelSet = new RepositoryModelSet(repository);
		RdfFactsConfiguration.memoryRepositoryModelSets.put(storageId, modelSet);
		
		return modelSet;
	}

	@Override
	protected void setUp() throws Exception {
		// Create the default configuration.
		defaultConfiguration = new eu.larkc.iris.Configuration();
		defaultConfiguration.evaluationStrategyFactory = new DistributedBottomUpEvaluationStrategyFactory(new DistributedNaiveEvaluatorFactory());

		if (jobConf != null)
			return;

	    if( !enableCluster ) {
	    	jobConf = new JobConf();
	    } else {
			System.setProperty("test.build.data", "build");
			new File("build/test/log").mkdirs();
			System.setProperty("hadoop.log.dir", "build/test/log");
			Configuration conf = new Configuration();

			defaultConfiguration.hadoopConfiguration = conf;
			
			dfs = new MiniDFSCluster(conf, 1, true, null);
			fileSys = dfs.getFileSystem();
			mr = new MiniMRCluster(1, fileSys.getUri().toString(), 1);

			jobConf = mr.createJobConf();

			jobConf.set("mapred.child.java.opts", "-Xmx512m");
			jobConf.setMapSpeculativeExecution(false);
			jobConf.setReduceSpeculativeExecution(false);
	    }
	    
	    jobConf.setBoolean("mapred.input.dir.recursive", true);
		jobConf.setNumMapTasks(numMapTasks);
		jobConf.setNumReduceTasks(numReduceTasks);

		defaultConfiguration.flowProperties.put("cascading.serialization.tokens", "130=eu.larkc.iris.storage.IRIWritable,131=eu.larkc.iris.storage.PredicateWritable,132=eu.larkc.iris.storage.StringTermWritable");
	    if( log != null )
	    	defaultConfiguration.flowProperties.put( "log4j.logger", log );

		Flow.setJobPollingInterval(defaultConfiguration.flowProperties, 500); // should speed up tests
		FlowConnector.setDebugLevel(defaultConfiguration.flowProperties, DebugLevel.VERBOSE);
		MultiMapReducePlanner.setJobConf( defaultConfiguration.flowProperties, jobConf );
		
		FactsFactory.PROPERTIES = "/facts-configuration-test.properties";
		
		FactsConfigurationFactory.STORAGE_PROPERTIES = "/facts-storage-configuration-test.properties";
		
		super.setUp();
	}

	  protected void copyFromLocal( String inputFile, String toFile ) throws IOException
	    {
	    if( !enableCluster )
	      return;

	    Path path = new Path( toFile );

	    if( !fileSys.exists( path ) )
	      FileUtil.copy( new File( inputFile ), fileSys, path, false, jobConf );
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
