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
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.mapred.InputSplit;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.RecordReader;
import org.apache.hadoop.mapred.Reporter;
import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.ModelSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.larkc.iris.storage.AtomRecord;
import eu.larkc.iris.storage.FactsConfigurationFactory;
import eu.larkc.iris.storage.FactsInputFormat;

public class RdfInputFormat<T extends AtomRecord> extends FactsInputFormat<T> {

	private static final Logger logger = LoggerFactory.getLogger(RdfInputFormat.class);
	
	@Override
	public InputSplit[] getSplits(JobConf job, int numSplits)
			throws IOException {
		RdfFactsConfiguration rdfFactsConfiguration = (RdfFactsConfiguration) FactsConfigurationFactory.getFactsConfiguration(job);
		
		List<RdfInputSplit> splits = new ArrayList<RdfInputSplit>();
		
		ModelSet modelSet = rdfFactsConfiguration.getModelSet(true);
		if (!modelSet.isOpen()) {
			modelSet.open();
		}
		splits.add(new RdfInputSplit(null, modelSet.getDefaultModel().size()));
		ClosableIterator<Model> modelsIterator = modelSet.getModels();
		while (modelsIterator.hasNext()) {
			Model model = modelsIterator.next();
			splits.add(new RdfInputSplit(model.getContextURI().toString(), model.size()));
		}
		if (modelSet.isOpen()) {
			modelSet.close();
		}
		logger.info("returning " + splits.size() + " splits");
		return splits.toArray(new RdfInputSplit[0]);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public RecordReader<LongWritable, T> getRecordReader(InputSplit split,
			JobConf job, Reporter reporter) throws IOException {
		logger.info("getRecordReader()");
		Class inputClass = job.getClass(RdfFactsConfiguration.INPUT_CLASS_PROPERTY, FactsInputFormat.NullAtomWritable.class);
		return new RdfFactsRecordReader((RdfInputSplit) split, inputClass, job);
	}

}
