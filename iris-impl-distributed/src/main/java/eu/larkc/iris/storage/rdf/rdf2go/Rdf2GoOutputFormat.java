/*
 * Copyright (c) 2009 Concurrent, Inc.
 *
 * This work has been released into the public domain
 * by the copyright holder. This applies worldwide.
 *
 * In case this is not legally possible:
 * The copyright holder grants any entity the right
 * to use this work for any purpose, without any
 * conditions, unless such conditions are required by law.
 */
/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.larkc.iris.storage.rdf.rdf2go;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.OutputFormat;
import org.apache.hadoop.mapred.RecordWriter;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.util.Progressable;
import org.ontoware.rdf2go.model.Model;

/**
 * A OutputFormat that sends the reduce output to a SQL table.
 * <p/>
 * {@link Rdf2GoOutputFormat} accepts &lt;key,value&gt; pairs, where key has a
 * type extending DBWritable. Returned {@link RecordWriter} writes <b>only the
 * key</b> to the database with a batch SQL query.
 */
public class Rdf2GoOutputFormat<K extends Rdf2GoWritable, V> implements
		OutputFormat<K, V> {
	private static final Log LOG = LogFactory.getLog(Rdf2GoOutputFormat.class);

	/** A RecordWriter that writes the reduce output to a SQL table */
	protected class Rdf2GoRecordWriter implements RecordWriter<K, V> {
		private Model model;

		protected Rdf2GoRecordWriter(Model model) {
			this.model = model;
		}

		/** {@inheritDoc} */
		public void close(Reporter reporter) throws IOException {
		}

		/** {@inheritDoc} */
		public synchronized void write(K key, V value) throws IOException {
			key.write(this.model);
		}
	}

	/** {@inheritDoc} */
	public void checkOutputSpecs(FileSystem filesystem, JobConf job)
			throws IOException {
	}

	/** {@inheritDoc} */
	public RecordWriter<K, V> getRecordWriter(FileSystem filesystem,
			JobConf job, String name, Progressable progress) throws IOException {
		Rdf2GoConfiguration rdf2GoConf = new Rdf2GoConfiguration(job);
		Model model = rdf2GoConf.getModel();
		return new Rdf2GoRecordWriter(model);
	}

	/**
	 * Initializes the reduce-part of the job with the appropriate output
	 * settings
	 * 
	 * @param job
	 *            The job
	 * @param rdf2GoOutputFormatClass
	 * @param tableName
	 *            The table to insert data into
	 * @param fieldNames
	 *            The field names in the table. If unknown, supply the
	 *            appropriate
	 */
	public static void setOutput(JobConf job,
			Class<? extends Rdf2GoOutputFormat> rdf2GoOutputFormatClass) {
		if (rdf2GoOutputFormatClass == null)
			job.setOutputFormat(Rdf2GoOutputFormat.class);
		else
			job.setOutputFormat(rdf2GoOutputFormatClass);

		// writing doesn't always happen in reduce
		job.setReduceSpeculativeExecution(false);
		job.setMapSpeculativeExecution(false);

		Rdf2GoConfiguration rdf2GoConf = new Rdf2GoConfiguration(job);
	}
}