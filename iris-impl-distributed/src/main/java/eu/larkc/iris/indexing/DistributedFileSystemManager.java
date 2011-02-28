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
package eu.larkc.iris.indexing;

import java.io.EOFException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.deri.iris.api.basics.IPredicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.larkc.iris.Configuration;
import eu.larkc.iris.rules.compiler.LiteralFields;
import eu.larkc.iris.storage.IRIWritable;

/**
 * A manager used when predicate indexing is enabled.
 * Facilitates access to storage by providing functions to read/write the data from a predicate indexed structure
 * 
 * @author valer.roman@softgress.com
 *
 */
public class DistributedFileSystemManager {

	private static final Logger logger = LoggerFactory.getLogger(DistributedFileSystemManager.class);
	
	/**
	 * The minimum number of records for a predicate, to allow it to be stored on a location of it own
	 */
	public final static Long BLOCK_MIN_RECORDS = new Long(1024);

	public static final String FACTS_FOLDER = "facts";
	public static final String PREDICATES_FOLDER = "predicates";
	public static final String PREDICATES_CONFIG_FILE= "predicates_config";
	public static final String PREDICATE_COUNT_FOLDER = "count";
	public static final String TMP_FOLDER = "_tmp";
	public static final String INFERENCES_FOLDER = "inferences";
	
	private Configuration configuration;
	
	private List<PredicateData> predicatesConfig;
	
	public DistributedFileSystemManager(Configuration configuration) {
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
					PredicateData predicateData = PredicateData.read(predicatesConfigInputStream);
					logger.info("read predicate data : " + predicateData);
					aPredicatesConfig.add(predicateData);
				}
			} catch (EOFException e) {
				logger.info("eof exception no more data");
			}
			predicatesConfigInputStream.close();
			this.predicatesConfig = aPredicatesConfig;
		} catch (IOException e) {
			logger.error("io exception", e);
			throw new RuntimeException("io exception", e);
		}
	}
	
	/**
	 * Returns a list of {@code PredicateData} read from the configuration
	 * 
	 * @return list of {@code PredicateData}
	 */
	public List<PredicateData> getPredicateData() {
		return predicatesConfig;
	}
	
	/**
	 * Return the path to the facts folder
	 * 
	 * @return path to facts folder
	 */
	public String getFactsPath() {
		return configuration.project + "/" + FACTS_FOLDER + "/";
	}

	/**
	 * Returns the facts path for the predicate of a {@code LiteralFields}
	 * 
	 * @param fields the literal fields
	 * @return path to facts
	 */
	public String getFactsPath(LiteralFields fields) {
		IPredicate predicate = fields.getPredicate();
		PredicateData predicateData = getPredicateData(new IRIWritable(predicate));
		return configuration.project + "/" + FACTS_FOLDER + "/" + predicateData.getLocation().toString() + "/";
	}

	/**
	 * Returns the path to all inferences
	 * 
	 * @return path to all inferences
	 */
	public String getInferencesPath() {
		return configuration.project + "/" + INFERENCES_FOLDER + "/";
	}

	/**
	 * Returns the path to inferences of a {@code PredicateData} location
	 * 
	 * @param predicateData the predicate data
	 * @return path to inferences
	 */
	public String getInferencesPath(PredicateData predicateData) {
		if (predicateData == null) {
			return getInferencesPath();
		} else {
			return configuration.project + "/" + INFERENCES_FOLDER + "/" + predicateData.getLocation().toString() + "/";
		}		
	}
	
	/**
	 * Returns the inferences path for the predicate of a {@code LiteralFields}
	 * 
	 * @param fields the literal fields
	 * @return path to inferences
	 */
	public String getInferencesPath(LiteralFields fields) {
		IPredicate predicate = fields.getPredicate();
		PredicateData predicateData = null;
		if (predicate != null) {
			predicateData = getPredicateData(new IRIWritable(predicate));
		}
		return getInferencesPath(predicateData);
	}

	/**
	 * Returns a path where to store temporary inferences for a flow
	 * 
	 * @param resultName the results name
	 * @param flowIdentificator the flow identificator
	 * @return temporary path to inferences
	 */
	public String getTempInferencesPath(String resultName, String flowIdentificator) {
		return configuration.project + "/" + TMP_FOLDER + "/" + INFERENCES_FOLDER + "/" + resultName + "/" + resultName + flowIdentificator + "/";
	}

	public String getInferencesPath(LiteralFields fields, String resultName, String flowIdentificator) {
		return getInferencesPath(fields) + resultName + "/" + resultName + flowIdentificator + "/";
	}

	public String getPredicateInferencesPath(Integer predicateLocation) {
		return configuration.project + "/" + DistributedFileSystemManager.INFERENCES_FOLDER + "/" + String.valueOf(predicateLocation) + "/";
	}

	public String getPredicateInferencesPath(Integer predicateLocation, String resultName, String flowIdentificator) {
		return getPredicateInferencesPath(predicateLocation) + resultName + "/" + resultName + flowIdentificator + "/";
	}

	public String getImportPath(String importName) {
		if (!configuration.doPredicateIndexing) {
			return configuration.project + "/" + DistributedFileSystemManager.FACTS_FOLDER + "/" + importName;
		} else {
			return configuration.project + "/" + DistributedFileSystemManager.TMP_FOLDER + "/" + DistributedFileSystemManager.FACTS_FOLDER + "/" + importName;
		}
	}
	
	public String getPredicateGroupsTempPath(String importName) {
		return configuration.project + "/" + DistributedFileSystemManager.TMP_FOLDER + "/" + DistributedFileSystemManager.PREDICATES_FOLDER + "/" + importName;
	}
	
	public String getPredicatePath(List<PredicateData> predicatesConfig, IRIWritable predicate) {
		return configuration.project + "/" + DistributedFileSystemManager.FACTS_FOLDER + "/" + getPredicateData(predicate).getLocation();
	}
	
	public String getPredicateCountPath(List<PredicateData> predicatesConfig, IRIWritable predicate) {
		return getPredicatePath(predicatesConfig, predicate) + "/" + DistributedFileSystemManager.PREDICATE_COUNT_FOLDER; 
	}

	public String getPredicatesConfigFilePath(String project) {
		return configuration.project + "/" + DistributedFileSystemManager.PREDICATES_CONFIG_FILE;
	}
	
	public String getPredicateFactsPath(Integer predicateLocation) {
		return configuration.project + "/" + DistributedFileSystemManager.FACTS_FOLDER + "/" + String.valueOf(predicateLocation) + "/";
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

	public void savePredicateConfig() {
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
