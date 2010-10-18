package eu.larkc.iris.storage.rdf.rdf2go;

import org.apache.hadoop.mapred.JobConf;
import org.ontoware.rdf2go.ModelFactory;
import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.model.Model;

public class Rdf2GoConfiguration {

	//I do not think a static variable is ok here, we could be in distributed environment
	private static Model model;
	
	public enum RDF_REPOSITORY_IMPLEMENTATION {
		SESAME
	}

	public static final String RDF_REPOSITORY_IMPLEMENTATION_PROPERTY = "mapred.rdf.repository.implementation";
	
	/** Class name implementing RdfRepositoryWritable which will hold input tuples */
	public static final String INPUT_CLASS_PROPERTY = "mapred.rdf.input.class";

	public static void configure(JobConf job, RDF_REPOSITORY_IMPLEMENTATION rdfRepositoryImplementation) {
		job.set(RDF_REPOSITORY_IMPLEMENTATION_PROPERTY, rdfRepositoryImplementation.name());
	}
	
	private JobConf job;
	
	Rdf2GoConfiguration(JobConf job) {
		this.job = job;
	}

	Model getModel() {
		if (model == null) {
			if (job.get(RDF_REPOSITORY_IMPLEMENTATION_PROPERTY).equals(RDF_REPOSITORY_IMPLEMENTATION.SESAME.name())) {
				//RDF2Go.register(new org.openrdf.rdf2go.RepositoryModelFactory());
				RDF2Go.register("org.openrdf.rdf2go.RepositoryModelFactory");
			}
			ModelFactory modelFactory = RDF2Go.getModelFactory();
			model = modelFactory.createModel();
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
