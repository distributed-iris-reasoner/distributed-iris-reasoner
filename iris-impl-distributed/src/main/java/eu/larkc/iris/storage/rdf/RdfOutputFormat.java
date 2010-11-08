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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.RecordWriter;
import org.apache.hadoop.util.Progressable;
import org.ontoware.rdf2go.model.Model;

import eu.larkc.iris.storage.AtomRecord;
import eu.larkc.iris.storage.FactsConfigurationFactory;
import eu.larkc.iris.storage.FactsOutputFormat;

/**
 * A OutputFormat that sends the reduce output to a SQL table.
 * <p/>
 * {@link RdfOutputFormat} accepts &lt;key,value&gt; pairs, where key has a
 * type extending DBWritable. Returned {@link RecordWriter} writes <b>only the
 * key</b> to the database with a batch SQL query.
 */
public class RdfOutputFormat<K extends AtomRecord, V> extends FactsOutputFormat<K, V> {
	private static final Log LOG = LogFactory.getLog(RdfOutputFormat.class);

	/** {@inheritDoc} */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public RecordWriter<K, V> getRecordWriter(FileSystem filesystem,
			JobConf job, String name, Progressable progress) throws IOException {
		RdfFactsConfiguration rdfFactsConfiguration = (RdfFactsConfiguration) FactsConfigurationFactory.getFactsConfiguration(job);
		Model model = rdfFactsConfiguration.getModel(job);
		return new RdfRecordWriter(model);
	}

}