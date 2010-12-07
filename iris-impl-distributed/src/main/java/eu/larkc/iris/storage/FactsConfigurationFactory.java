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

package eu.larkc.iris.storage;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.hadoop.mapred.JobConf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Factory used to create a new facts configuration instance
 *
 * 
 * @History 03.11.2010 vroman, Creation
 * @author Valer Roman
 */
public class FactsConfigurationFactory {

	private static final Logger logger = LoggerFactory.getLogger(FactsConfigurationFactory.class);
	
	private static final Map<JobConf, IFactsConfiguration> factsConfigurations = new HashMap<JobConf, IFactsConfiguration>();

	public static String STORAGE_PROPERTIES = "/facts-storage-configuration.properties";
	
	public static IFactsConfiguration getFactsConfiguration(JobConf jobConf) {
		if (factsConfigurations.containsKey(jobConf.get(IFactsConfiguration.FACTS_CONFIGURATION_CLASS))) {
			return factsConfigurations.get(jobConf.get(IFactsConfiguration.FACTS_CONFIGURATION_CLASS));
		}
		return createFactsConfiguration(jobConf);
	}
	
	public static IFactsConfiguration createFactsConfiguration(JobConf jobConf) {
		if (factsConfigurations.containsKey(jobConf)) {
			return factsConfigurations.get(jobConf);
		}
		String factsConfigurationClass = jobConf.get(IFactsConfiguration.FACTS_CONFIGURATION_CLASS);
		Class<? extends IFactsConfiguration> clazz = null;
		try {
			clazz = (Class<? extends IFactsConfiguration>) Class.forName(factsConfigurationClass);
		} catch (ClassNotFoundException e) {
			logger.error("class not found " + factsConfigurationClass, e);
			throw new RuntimeException("class not found " + factsConfigurationClass, e);
		}
		try {
			IFactsConfiguration factsConfiguration = clazz.newInstance();
			Properties storageProperties = new Properties();
			storageProperties.load(FactsConfigurationFactory.class.getResourceAsStream(STORAGE_PROPERTIES));
			factsConfiguration.setJobConf(jobConf);
			factsConfiguration.setStorageProperties(storageProperties);
			factsConfigurations.put(jobConf, factsConfiguration);
			return factsConfiguration;
		} catch (Exception e) {
			logger.error("exception creating instance from class", e);
			throw new RuntimeException("exception creating instance from class", e);
		}
	}
	
}
