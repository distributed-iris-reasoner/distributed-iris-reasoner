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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.hadoop.mapred.InputFormat;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.OutputFormat;
import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.model.Model;
import org.openrdf.rdf2go.RepositoryModel;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.http.HTTPRepository;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.memory.MemoryStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cascading.tuple.Tuple;
import eu.larkc.iris.storage.AtomRecord;
import eu.larkc.iris.storage.FactsConfiguration;

/**
 * @author valer
 *
 */
public class RdfFactsConfiguration extends FactsConfiguration {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -8910614508067701482L;

	private static final Logger logger = LoggerFactory.getLogger(RdfFactsConfiguration.class);
	
	public RdfFactsConfiguration() {}
	
	public enum RDF2GO_ADAPTER {
		SESAME
	}

	public enum REPOSITORY_TYPE {
		MEMORY,
		REMOTE
	}

	public static final String RDF2GO_ADAPTER_PROPERTY = "mapred.rdf.rdf2go.adapter";
	public static final String REPOSITORY_ID_PROPERTY = "mapred.rdf.repository.id";
	public static final String REPOSITORY_TYPE_PROPERTY = "mapred.rdf.repository.type";
	public static final String SERVER_URL_PROPERTY = "mapred.rdf.server.url";
	
	private RDF2GO_ADAPTER rdf2GoAdapter = RDF2GO_ADAPTER.SESAME;
	private REPOSITORY_TYPE repositoryType = REPOSITORY_TYPE.MEMORY;
	private String serverURL = null;
	private String repositoryId = null;

	//the memory repositories work only for jobs running in same jvm (only for tests)
	public static Map<String, Model> memoryRepositoryModels = new HashMap<String, Model>();
	
	private void readStorageProperties(JobConf jobConf) {
		rdf2GoAdapter = RDF2GO_ADAPTER.valueOf(storageProperties.getProperty(getStorageId(jobConf) + "." + "rdf2go.adapter"));
		repositoryType = REPOSITORY_TYPE.valueOf(storageProperties.getProperty(getStorageId(jobConf) + "." + "repository.type"));
		serverURL = storageProperties.getProperty(getStorageId(jobConf) + "." + "server.url");
		repositoryId = storageProperties.getProperty(getStorageId(jobConf) + "." + "repository.id");
	}
	
	private void configureStorage(JobConf jobConf) {
		readStorageProperties(jobConf);
		jobConf.setEnum(RDF2GO_ADAPTER_PROPERTY, rdf2GoAdapter);
		jobConf.setEnum(REPOSITORY_TYPE_PROPERTY, repositoryType);
		if (jobConf.getEnum(RDF2GO_ADAPTER_PROPERTY, RDF2GO_ADAPTER.SESAME) == RDF2GO_ADAPTER.SESAME) {
			//RDF2Go.register(new org.openrdf.rdf2go.RepositoryModelFactory());
			RDF2Go.register("org.openrdf.rdf2go.RepositoryModelFactory");
		}
		if (jobConf.getEnum(REPOSITORY_TYPE_PROPERTY, REPOSITORY_TYPE.MEMORY) == REPOSITORY_TYPE.REMOTE) {
			try {
				jobConf.set(SERVER_URL_PROPERTY, new URL(serverURL).toExternalForm());
			} catch (MalformedURLException e) {
				logger.error("malformed url", e);
			}
			jobConf.set(REPOSITORY_ID_PROPERTY, repositoryId);
		}
	}
	
	@Override
	public void configureInput(JobConf jobConf) {
		configureStorage(jobConf);
		
		super.configureInput(jobConf);
	}

	@Override
	public void configureOutput(JobConf jobConf) {
		configureStorage(jobConf);
		
		super.configureOutput(jobConf);
	}

	public static void configure(RDF2GO_ADAPTER rdfRepositoryImplementation, URL rdf2GoServerURL, String repositoryID) {
		
	}
	
	public Model getModel(JobConf jobConf) {
		Model model = null;
		if (jobConf.getEnum(RDF2GO_ADAPTER_PROPERTY, RDF2GO_ADAPTER.SESAME) == RDF2GO_ADAPTER.SESAME) {
			if (jobConf.getEnum(REPOSITORY_TYPE_PROPERTY, REPOSITORY_TYPE.MEMORY) == REPOSITORY_TYPE.REMOTE) {
				String sesameServer = jobConf.get(SERVER_URL_PROPERTY);
				String repositoryID = jobConf.get(REPOSITORY_ID_PROPERTY);
	
				Repository myRepository = new HTTPRepository(sesameServer, repositoryID);
				model = new RepositoryModel(myRepository);
				model.open();			
			} else if (jobConf.getEnum(REPOSITORY_TYPE_PROPERTY, REPOSITORY_TYPE.MEMORY) == REPOSITORY_TYPE.MEMORY) {
				if (!RdfFactsConfiguration.memoryRepositoryModels.containsKey(getStorageId(jobConf))) {
					Repository repository = new SailRepository(new MemoryStore());
					try {
						repository.initialize();
					} catch (RepositoryException e) {
						logger.error("error initializing repository" ,e);
						throw new RuntimeException("error initializing repository" ,e);
					}
					model = new RepositoryModel(repository);
					model.open();
					RdfFactsConfiguration.memoryRepositoryModels.put(getStorageId(jobConf), model);
				} else {
					model = RdfFactsConfiguration.memoryRepositoryModels.get(getStorageId(jobConf));
				}
			}
		}
		return model;
	}

	@Override
	public Class<? extends AtomRecord> getInputClass() {
		return RdfRecord.class;
	}

	@Override
	public Class<? extends InputFormat> getInputFormat() {
		return RdfInputFormat.class;
	}

	@Override
	public Class<? extends OutputFormat> getOutputFormat() {
		return RdfOutputFormat.class;
	}

	@Override
	public AtomRecord newRecordInstance(Tuple tuple) {
		return new RdfRecord(tuple);
	}

}
