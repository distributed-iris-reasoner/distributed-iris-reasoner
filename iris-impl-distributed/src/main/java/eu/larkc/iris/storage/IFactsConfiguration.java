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

import java.util.Properties;

import org.apache.hadoop.mapred.InputFormat;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.OutputFormat;


/**
 * 
 * All the specific configuration for a certain facts storage and the configuration of the haddop are going here
 * 
 * @History 03.11.2010, creation
 * @author Valer Roman
 *
 */
public interface IFactsConfiguration {

	public static final String FACTS_STORAGE_TYPE = "facts.storage.type";
	
	public static final String FACTS_STORAGE_ID = "facts.storage.id";
	
	public static final String FACTS_CONFIGURATION_CLASS = "facts.configuration.class";
	
	public static final String PREDICATE_FILTER = "facts.predicate.filter";
	
	/** Class name implementing RdfRepositoryWritable which will hold input tuples */
	public static final String INPUT_CLASS_PROPERTY = "mapred.facts.input.class";

	public static final String SOURCE_STORAGE_ID_PROPERTY = "mapred.facts.source.storage.id";
	
	public static final String SINK_STORAGE_ID_PROPERTY = "mapred.facts.sink.storage.id";
	
	public void setJobConf(JobConf jobConf);
	
	public JobConf getJobConf();
	
	public void setSourceStorageId(String storageId);
	
	public String getSourceStorageId();

	public void setSinkStorageId(String storageId);
	
	public String getSinkStorageId();
	
	public void setStorageProperties(Properties properties);
	
	public void configureInput();
	
	public void configureOutput();
	
	public Class<? extends AtomRecord> getInputClass();
	
	@SuppressWarnings("rawtypes")
	public Class<? extends InputFormat> getInputFormat();
	
	@SuppressWarnings("rawtypes")
	public Class<? extends OutputFormat> getOutputFormat();
	
}
