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

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.deri.iris.api.basics.IAtom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author valer
 *
 */
public class FactsFactory {

	private static final Logger logger = LoggerFactory.getLogger(FactsFactory.class);
	
	public static String PROPERTIES = "/facts-configuration.properties";
	
	private String storageId;
	
	//this data is can be passed also through the file facts-configuration.properties
	private Properties properties = null;
	
	private FactsFactory(String storageId) {
		this.storageId = storageId;
		InputStream is = FactsFactory.class.getResourceAsStream(PROPERTIES);
		properties = new Properties();
		try {
			properties.load(is);
		} catch (IOException e) {
			logger.error("exception reading facts configuration properties file", e);
		}
	}

	private FactsFactory(String storageId, Properties properties) {
		this.storageId = storageId;
		this.properties = properties;
	}
	
	public static FactsFactory getInstance() {
		return new FactsFactory(null);
	}

	public static FactsFactory getInstance(String storageId) {
		return new FactsFactory(storageId);
	}

	public static FactsFactory getInstance(String storageId, Properties factsFactoryProperties) {
		return new FactsFactory(storageId, factsFactoryProperties);
	}
	
	public FactsTap getFacts(IAtom atom) {
		return getFacts(null, atom);
	}
	
	public FactsTap getFacts(FieldsVariablesMapping fieldsVariablesMapping, IAtom atom) {
		String factsConfigurationClass = properties.getProperty(IFactsConfiguration.FACTS_CONFIGURATION_CLASS);
		return new FactsTap(factsConfigurationClass, storageId, fieldsVariablesMapping, atom);
	}

	public FactsTap getFacts() {
		String factsConfigurationClass = properties.getProperty(IFactsConfiguration.FACTS_CONFIGURATION_CLASS);
		return new FactsTap(factsConfigurationClass, storageId);
	}

	public String getStorageId() {
		return storageId;
	}
}