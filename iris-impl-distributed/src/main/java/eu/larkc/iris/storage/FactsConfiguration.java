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

import org.apache.hadoop.mapred.JobConf;

/**
 * Abstract class implementing the facts configuration
 * Hadoop jobconf is stored
 * 
 * @history 03.11.2010, creation
 * @author Valer Roman
 *
 */
public abstract class FactsConfiguration implements IFactsConfiguration {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 778519424591973524L;

	protected Properties storageProperties = null;
	public boolean newInferences;
	
	protected JobConf jobConf;
	
	public FactsConfiguration() {}

	@Override
	public void setJobConf(JobConf jobConf) {
		this.jobConf = jobConf;
	}

	@Override
	public JobConf getJobConf() {
		return jobConf;
	}

	@Override
	public void setSourceStorageId(String storageId) {
		if (storageId != null) {
			jobConf.set(IFactsConfiguration.SOURCE_STORAGE_ID_PROPERTY, storageId);
		}
	}

	@Override
	public String getSourceStorageId() {
		return jobConf.get(IFactsConfiguration.SOURCE_STORAGE_ID_PROPERTY, "default");
	}

	@Override
	public void setSinkStorageId(String storageId) {
		if (storageId != null) {
			jobConf.set(IFactsConfiguration.SINK_STORAGE_ID_PROPERTY, storageId);
		}
	}

	@Override
	public String getSinkStorageId() {
		return jobConf.get(IFactsConfiguration.SINK_STORAGE_ID_PROPERTY, "default");
	}

	@Override
	public void setStorageProperties(Properties properties) {
		this.storageProperties = properties;
	}

	@Override
	public void configureInput() {
		jobConf.setClass(IFactsConfiguration.INPUT_CLASS_PROPERTY, getInputClass(), AtomRecord.class);
		jobConf.setInputFormat(getInputFormat());
	}
	
	@Override
	public void configureOutput() {
		jobConf.setOutputFormat(getOutputFormat());
	
		// writing doesn't always happen in reduce
		jobConf.setReduceSpeculativeExecution(false);
		jobConf.setMapSpeculativeExecution(false);
	}

}
