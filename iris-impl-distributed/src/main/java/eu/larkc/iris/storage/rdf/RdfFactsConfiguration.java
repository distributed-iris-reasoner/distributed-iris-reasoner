/**
 * 
 */
package eu.larkc.iris.storage.rdf;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.hadoop.mapred.InputFormat;
import org.apache.hadoop.mapred.InputSplit;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.OutputFormat;
import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.model.Model;
import org.openrdf.rdf2go.RepositoryModel;
import org.openrdf.repository.Repository;
import org.openrdf.repository.http.HTTPRepository;

import cascading.tuple.Tuple;
import eu.larkc.iris.storage.AtomRecord;
import eu.larkc.iris.storage.FactsConfiguration;

/**
 * @author valer
 *
 */
public class RdfFactsConfiguration extends FactsConfiguration {

	public RdfFactsConfiguration() {}
	
	public enum RDF2GO_IMPL {
		SESAME
	}

	public static final String RDF2GO_IMPL_PROPERTY = "mapred.rdf2go.impl";
	public static final String RDF2GO_SERVER_URL_PROPERTY = "mapred.rdf2go.server.url";
	public static final String RDF2GO_REPOSITORY_ID_PROPERTY = "mapred.rdf2go.repository.id";
	
	private RDF2GO_IMPL rdfRepositoryImplementation = RDF2GO_IMPL.SESAME;
	private String rdf2GoServerURL = "http://localhost:8080/openrdf-sesame";
	private String repositoryID = "humans";
	
	@Override
	public void configureInput(JobConf jobConf) {
		jobConf.set(RDF2GO_IMPL_PROPERTY, rdfRepositoryImplementation.name());
		if (rdfRepositoryImplementation == RDF2GO_IMPL.SESAME) {
			//RDF2Go.register(new org.openrdf.rdf2go.RepositoryModelFactory());
			RDF2Go.register("org.openrdf.rdf2go.RepositoryModelFactory");
		}
		try {
			jobConf.set(RDF2GO_SERVER_URL_PROPERTY, new URL(rdf2GoServerURL).toExternalForm());
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		jobConf.set(RDF2GO_REPOSITORY_ID_PROPERTY, repositoryID);

		super.configureInput(jobConf);
	}

	@Override
	public void configureOutput(JobConf jobConf) {
		jobConf.set(RDF2GO_IMPL_PROPERTY, rdfRepositoryImplementation.name());
		if (rdfRepositoryImplementation == RDF2GO_IMPL.SESAME) {
			//RDF2Go.register(new org.openrdf.rdf2go.RepositoryModelFactory());
			RDF2Go.register("org.openrdf.rdf2go.RepositoryModelFactory");
		}
		try {
			jobConf.set(RDF2GO_SERVER_URL_PROPERTY, new URL(rdf2GoServerURL).toExternalForm());
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		jobConf.set(RDF2GO_REPOSITORY_ID_PROPERTY, repositoryID);
		
		super.configureOutput(jobConf);
	}

	public static void configure(RDF2GO_IMPL rdfRepositoryImplementation, URL rdf2GoServerURL, String repositoryID) {
				
	}
	
	public static Model getModel(JobConf job) {
		Model model = null;
		if (job.get(RDF2GO_IMPL_PROPERTY).equals(RDF2GO_IMPL.SESAME.name())) {
			String sesameServer = job.get(RDF2GO_SERVER_URL_PROPERTY);
			String repositoryID = job.get(RDF2GO_REPOSITORY_ID_PROPERTY);

			Repository myRepository = new HTTPRepository(sesameServer, repositoryID);
			
			model = new RepositoryModel(myRepository);
			model.open();			
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
