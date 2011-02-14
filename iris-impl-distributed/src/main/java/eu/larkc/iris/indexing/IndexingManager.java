/**
 * 
 */
package eu.larkc.iris.indexing;

import java.io.EOFException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.larkc.iris.Configuration;
import eu.larkc.iris.storage.IRIWritable;

/**
 * @author valer
 *
 */
public class IndexingManager {

	private static final Logger logger = LoggerFactory.getLogger(IndexingManager.class);
	
	public final static Long BLOCK_MIN_RECORDS = new Long(1024);

	public static final String FACTS_FOLDER = "facts";
	public static final String PREDICATES_FOLDER = "predicates";
	public static final String PREDICATES_CONFIG_FILE= "predicates_config";
	public static final String PREDICATE_COUNT_FOLDER = "count";
	public static final String TMP_FOLDER = "_tmp";

	private Configuration configuration;
	
	private List<PredicateData> predicatesConfig;
	
	public IndexingManager(Configuration configuration) {
		this.configuration = configuration;
		try {
			List<PredicateData> aPredicatesConfig = new ArrayList<PredicateData>();
			FileSystem fs = FileSystem.get(configuration.hadoopConfiguration);
			Path predicatesConfigFilePath = new Path(getPredicatesConfigFilePath(configuration.project));
			if (!fs.exists(predicatesConfigFilePath)) {
				this.predicatesConfig = aPredicatesConfig;
				return;
			}
			FSDataInputStream predicatesConfigInputStream = fs.open(predicatesConfigFilePath);
			try {
				while (true) {
					aPredicatesConfig.add(PredicateData.read(predicatesConfigInputStream));
				}
			} catch (EOFException e) {
				logger.info("eof exception no more data");
			}
			this.predicatesConfig = aPredicatesConfig;
		} catch (IOException e) {
			logger.error("io exception", e);
			throw new RuntimeException("io exception", e);
		}
	}
	
	public String getImportPath(String importName) {
		if (!configuration.doPredicateIndexing) {
			return configuration.project + "/" + IndexingManager.FACTS_FOLDER + "/" + importName;
		} else {
			return configuration.project + "/" + IndexingManager.TMP_FOLDER + "/" + IndexingManager.FACTS_FOLDER + "/" + importName;
		}
	}
	
	public String getPredicateGroupsTempPath(String importName) {
		return configuration.project + "/" + IndexingManager.TMP_FOLDER + "/" + IndexingManager.PREDICATES_FOLDER + "/" + importName;
	}
	
	public String getPredicatePath(List<PredicateData> predicatesConfig, IRIWritable predicate) {
		return configuration.project + "/" + IndexingManager.FACTS_FOLDER + "/" + getPredicateData(predicate).getLocation();
	}
	
	public String getPredicateCountPath(List<PredicateData> predicatesConfig, IRIWritable predicate) {
		return getPredicatePath(predicatesConfig, predicate) + "/" + IndexingManager.PREDICATE_COUNT_FOLDER; 
	}

	public String getPredicatesConfigFilePath(String project) {
		return configuration.project + "/" + IndexingManager.PREDICATES_CONFIG_FILE;
	}
	
	public String getPredicateFactsPath(Integer predicateId) {
		return configuration.project + "/" + IndexingManager.FACTS_FOLDER + "/" + String.valueOf(predicateId) + "/";
	}
	
	public String getPredicateFactsImportPath(Integer predicateLocation, String importName) {
		return getPredicateFactsPath(predicateLocation) + importName + "/";
	}

	public PredicateData getPredicateData(IRIWritable predicate) {
		for (PredicateData predicateData : predicatesConfig) {
			if (predicateData.getValue().equals(predicate.getValue())) {
				return predicateData;
			}
		}
		return null;
	}

	private void savePredicateConfig() {
		try {
			FileSystem fs = FileSystem.get(configuration.hadoopConfiguration);
			String predicatesConfigFileTemp = getPredicatesConfigFilePath(configuration.project);
			Path predicatesConfigFileTempPath = new Path(predicatesConfigFileTemp + "_"); 
			FSDataOutputStream predicatesConfigOutputStream = null;
			if (fs.exists(predicatesConfigFileTempPath)) {
				logger.error("path " + predicatesConfigFileTemp + " exists already!");
				throw new RuntimeException("path " + predicatesConfigFileTemp + " exists already!");
			}
			predicatesConfigOutputStream = fs.create(predicatesConfigFileTempPath);
			for (PredicateData predicateData : predicatesConfig) {
				predicateData.write(predicatesConfigOutputStream);
			}
			predicatesConfigOutputStream.close();
			fs.delete(new Path(predicatesConfigFileTemp), true);
			fs.rename(predicatesConfigFileTempPath, new Path(predicatesConfigFileTemp));
		} catch (IOException e) {
			logger.error("io exception", e);
			throw new RuntimeException("io exception", e);
		}
	}

	public Integer getMaxLocationId() {
		Integer maxId = new Integer(0);
		for (PredicateData predicateData : predicatesConfig) {
			maxId = Math.max(maxId, predicateData.getLocation());
		}
		return maxId;
	}

	public Integer getMaxId() {
		Integer maxId = new Integer(0);
		for (PredicateData predicateData : predicatesConfig) {
			maxId = Math.max(maxId, predicateData.getId());
		}
		return maxId;
	}

	public void addPredicates(List<PredicateCount> predicateCounts) {
		for (PredicateCount predicateCount : predicateCounts) {
			IRIWritable predicate = predicateCount.getPredicate();
			Long count = predicateCount.getCount();
			boolean found = false;
			for (PredicateData predicateData : predicatesConfig) {
				if (predicateData.getValue().equals(predicate.getValue())) {
					predicateData.setCount(predicateData.getCount() + count);
					found = true;
					break;
				}
			}
			if (!found) {
				Integer locationId = new Integer(0);
				if (count >= BLOCK_MIN_RECORDS) {
					locationId = getMaxLocationId() + 1;
				}
				PredicateData predicateData = new PredicateData(predicate.getValue(), getMaxId() + 1, locationId, count);
				predicatesConfig.add(predicateData);				
			}
		}
		savePredicateConfig();
	}

	/*
	public void group(int[] values) {
		int size = values.length;
		for (int i = 1; i <= size; i++) {
			
		}
	}
	
	public void combinations(int[] values, int n, int k) {
		boolean finished = false, changed = false;
		if (k > 0) {
			for (int i = k - 1; !finished && !changed; i--) {
				
			}
		}
	}
	*/

}
