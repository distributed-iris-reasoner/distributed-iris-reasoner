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

package eu.larkc.iris.storage.rdf;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.mapred.InputSplit;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.RecordReader;
import org.apache.hadoop.mapred.Reporter;
import org.ontoware.rdf2go.model.ModelSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.larkc.iris.storage.AtomRecord;
import eu.larkc.iris.storage.FactsConfigurationFactory;
import eu.larkc.iris.storage.FactsInputFormat;
import eu.larkc.iris.storage.FactsInputSplit;

public class RdfInputFormat<T extends AtomRecord> extends FactsInputFormat<T> {

	private static final Logger logger = LoggerFactory.getLogger(RdfInputFormat.class);
	
	@Override
	public long getTotalSize(JobConf jobConf) {
		RdfFactsConfiguration rdfFactsConfiguration = (RdfFactsConfiguration) FactsConfigurationFactory.getFactsConfiguration(jobConf);
		
		ModelSet modelSet = rdfFactsConfiguration.getModelSet(true);
		if (!modelSet.isOpen()) {
			modelSet.open();
		}

		long totalSize = modelSet.size();
		
		if (modelSet.isOpen()) {
			modelSet.close();
		}

		return totalSize;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public RecordReader<LongWritable, T> getRecordReader(InputSplit split,
			JobConf job, Reporter reporter) throws IOException {
		Class inputClass = job.getClass(RdfFactsConfiguration.INPUT_CLASS_PROPERTY, FactsInputFormat.NullAtomWritable.class);
		return new RdfFactsRecordReader((FactsInputSplit) split, inputClass, job);
	}

}
