package eu.larkc.iris.storage.rdf.rdf2go;

import java.net.URL;

import org.apache.hadoop.mapred.JobConf;
import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.model.Model;
import org.openrdf.rdf2go.RepositoryModel;
import org.openrdf.repository.Repository;
import org.openrdf.repository.http.HTTPRepository;

public class Rdf2GoConfiguration {

	public enum RDF2GO_IMPL {
		SESAME
	}

	public static final String RDF2GO_IMPL_PROPERTY = "mapred.rdf2go.impl";
	public static final String RDF2GO_SERVER_URL_PROPERTY = "mapred.rdf2go.server.url";
	public static final String RDF2GO_REPOSITORY_ID_PROPERTY = "mapred.rdf2go.repository.id";
	
	/** Class name implementing RdfRepositoryWritable which will hold input tuples */
	public static final String INPUT_CLASS_PROPERTY = "mapred.rdf.input.class";

	public static void configure(JobConf job, RDF2GO_IMPL rdfRepositoryImplementation, URL rdf2GoServerURL, String repositoryID) {
		job.set(RDF2GO_IMPL_PROPERTY, rdfRepositoryImplementation.name());
		if (rdfRepositoryImplementation == RDF2GO_IMPL.SESAME) {
			//RDF2Go.register(new org.openrdf.rdf2go.RepositoryModelFactory());
			RDF2Go.register("org.openrdf.rdf2go.RepositoryModelFactory");
		}
		job.set(RDF2GO_SERVER_URL_PROPERTY, rdf2GoServerURL.toExternalForm());
		job.set(RDF2GO_REPOSITORY_ID_PROPERTY, repositoryID);
				
	}
	
	private JobConf job;
	
	Rdf2GoConfiguration(JobConf job) {
		this.job = job;
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
	
	Class<?> getInputClass() {
		return job.getClass(Rdf2GoConfiguration.INPUT_CLASS_PROPERTY,
				Rdf2GoInputFormat.NullRdfWritable.class);
	}

	void setInputClass(Class<? extends Rdf2GoWritable> inputClass) {
		job.setClass(Rdf2GoConfiguration.INPUT_CLASS_PROPERTY, inputClass,
				Rdf2GoWritable.class);
	}

}
