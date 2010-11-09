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

import java.io.Serializable;
import java.util.Properties;

import org.apache.hadoop.mapred.InputFormat;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.OutputFormat;

import cascading.tuple.Tuple;


/**
 * 
 * All the specific configuration for a certain facts storage and the configuration of the haddop are going here
 * 
 * @History 03.11.2010, creation
 * @author Valer Roman
 *
 */
public interface IFactsConfiguration extends Serializable {

	public static final String FACTS_CONFIGURATION_CLASS = "facts.configuration.class";
	
	public static final String PREDICATE_FILTER = "facts.predicate.filter";
	
	/** Class name implementing RdfRepositoryWritable which will hold input tuples */
	public static final String INPUT_CLASS_PROPERTY = "mapred.facts.input.class";

	public static final String SOURCE_STORAGE_ID_PROPERTY = "mapred.facts.source.storage.id";
	
	public static final String SINK_STORAGE_ID_PROPERTY = "mapred.facts.sink.storage.id";
	
	public void setSourceStorageId(JobConf jobConf, String storageId);
	
	public String getSourceStorageId(JobConf jobConf);

	public void setSinkStorageId(JobConf jobConf, String storageId);
	
	public String getSinkStorageId(JobConf jobConf);
	
	public void setStorageProperties(Properties properties);
	
	public void configureInput(JobConf jobConf);
	
	public void configureOutput(JobConf jobConf);
	
	public Class<? extends AtomRecord> getInputClass();
	
	public Class<? extends InputFormat> getInputFormat();
	
	public Class<? extends OutputFormat> getOutputFormat();
	
	public AtomRecord newRecordInstance(Tuple tuple);
	
}
