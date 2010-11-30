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

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.JobConf;
import org.deri.iris.api.basics.IAtom;

import cascading.tap.Tap;
import cascading.tap.hadoop.TapCollector;
import cascading.tap.hadoop.TapIterator;
import cascading.tuple.TupleEntryCollector;
import cascading.tuple.TupleEntryIterator;

/**
 * Cascading tap for accesing/writing iris style atoms from different datasources
 * 
 * @history
 * @author valer
 *
 */
public class FactsTap extends Tap {
	
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -5174403815272616996L;
	
	private String factsConfigurationClass = null;
	private IAtom atom = null;
	private String storageId;
	
	private FactsScheme factsScheme = null;
	
	FactsTap(String factsConfigurationClass, String storageId, IAtom atom) {
		this(factsConfigurationClass, storageId, null, atom);
	}
	
	private FactsTap(FactsScheme scheme) {
		super(scheme);
		factsScheme = scheme;
	}
	
	FactsTap(String factsConfigurationClass, String storageId, FieldsVariablesMapping fieldsVariablesMapping, IAtom atom) {
		this(new FactsScheme(storageId, fieldsVariablesMapping, atom));
		this.factsConfigurationClass = factsConfigurationClass;
		this.atom = atom;
		this.storageId = storageId;
	}

	FactsTap(String factsConfigurationClass, String storageId) {
		this(new FactsScheme(storageId));
		this.factsConfigurationClass = factsConfigurationClass;
		this.storageId = storageId;
	}

	@Override
	public Path getPath() {
		if (atom != null) {
			return new Path(storageId + "/" + atom.getPredicate().getPredicateSymbol());
		} 		
		else {
			return new Path(storageId);
		}
	}

	@Override
	public TupleEntryIterator openForRead(JobConf conf) throws IOException {
		return new TupleEntryIterator(getSourceFields(), new TapIterator(this, conf));
	}

	@Override
	public TupleEntryCollector openForWrite(JobConf conf) throws IOException {
		return new TapCollector(this, conf);
	}

	@Override
	public boolean makeDirs(JobConf conf) throws IOException {
		return false;
	}

	@Override
	public boolean deletePath(JobConf conf) throws IOException {
		return false;
	}

	@Override
	public boolean pathExists(JobConf conf) throws IOException {
		return true;
	}

	@Override
	public long getPathModified(JobConf conf) throws IOException {
		return 0;
	}

	@Override
	public boolean isSink() {
		return atom == null;
	}

	@Override
	public boolean isSource() {
		return true;
	}

	@Override
	public void sourceInit(JobConf jobConf) throws IOException {		
		// a hack for MultiInputFormat to see that there is a child format
		FileInputFormat.setInputPaths(jobConf, getPath());

		jobConf.set(IFactsConfiguration.FACTS_CONFIGURATION_CLASS, factsConfigurationClass);
		if (isSource() && atom != null) {
			jobConf.set(IFactsConfiguration.PREDICATE_FILTER, atom.getPredicate().getPredicateSymbol());
		}
		
		IFactsConfiguration factsConfiguration = FactsConfigurationFactory.getFactsConfiguration(jobConf);
		factsConfiguration.setSourceStorageId(jobConf, storageId);
		
		//RdfFactsConfiguration.configure(conf, rdf2GoImpl, serverURL, repositoryID);

		super.sourceInit(jobConf);
	}

	@Override
	public void sinkInit(JobConf jobConf) throws IOException {
		if (!isSink())
			return;

		jobConf.set(IFactsConfiguration.FACTS_CONFIGURATION_CLASS, factsConfigurationClass);
		
		IFactsConfiguration factsConfiguration = FactsConfigurationFactory.getFactsConfiguration(jobConf);
		factsConfiguration.setSinkStorageId(jobConf, storageId);

		//RdfFactsConfiguration.configure(conf, rdf2GoImpl, serverURL, repositoryID);

		super.sinkInit(jobConf);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("facts[");
		sb.append((storageId == null ? "" : ("store:" + storageId)));
		if (atom != null) {
			sb.append(", " + "predicate:" + atom.getPredicate().getPredicateSymbol());
		}
		sb.append("]");
		return sb.toString();
	}

	public boolean hasNewInferences() {
		if (!isSink()) {
			return false;
		}
		return false;
	}

}