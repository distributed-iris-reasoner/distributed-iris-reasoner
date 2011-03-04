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

import org.apache.hadoop.mapred.InputSplit;
import org.apache.hadoop.mapred.JobConf;
import org.ontoware.rdf2go.model.ModelSet;

import eu.larkc.iris.storage.AtomRecord;
import eu.larkc.iris.storage.FactsConfigurationFactory;
import eu.larkc.iris.storage.FactsInputSplit;
import eu.larkc.iris.storage.FactsRecordReader;
import eu.larkc.iris.storage.IFactsConfiguration;

public class RdfFactsRecordReader<LongWritable, T extends AtomRecord> extends FactsRecordReader<T> {
	
	public RdfFactsRecordReader(InputSplit split, Class<T> inputClass, JobConf job) {
		super(split, inputClass, job);
		
		FactsInputSplit factsInputSplit = (FactsInputSplit) split;
		factsStorage = new RdfStorage();
		RdfFactsConfiguration rdfFactsConfiguration = (RdfFactsConfiguration) FactsConfigurationFactory.getFactsConfiguration(job);
		ModelSet modelSet = rdfFactsConfiguration.getModelSet(true);
		((RdfStorage) factsStorage).setModel(modelSet);
		((RdfStorage) factsStorage).setPredicateFilter(job.get(IFactsConfiguration.PREDICATE_FILTER));
		((RdfStorage) factsStorage).setLimit(factsInputSplit.getLimit());
		((RdfStorage) factsStorage).setOffset(factsInputSplit.getOffset());
	}
}
