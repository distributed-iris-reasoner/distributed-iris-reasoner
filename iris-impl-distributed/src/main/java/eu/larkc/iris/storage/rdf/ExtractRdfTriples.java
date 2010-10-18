/**
 * 
 */
package eu.larkc.iris.storage.rdf;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.ModelFactory;
import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.model.Model;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author valer
 *
 */
public class ExtractRdfTriples {

	private static final Logger logger = LoggerFactory.getLogger(ExtractRdfTriples.class);
	
	public static void main(String[] args) throws RepositoryException, RDFParseException, IOException {
		//Repository repository = new SailRepository(new MemoryStore());
		//repository.initialize();
		RDF2Go.register("org.openrdf.rdf2go.RepositoryModelFactory");
		ModelFactory modelFactory = RDF2Go.getModelFactory();
		Model repositoryModel = modelFactory.createModel();
		//RepositoryModel repositoryModel = new RepositoryModel(repository);
		repositoryModel.open();
		//repository.initialize();
		
		//RepositoryConnection repConnection = repository.getConnection();
		//repConnection.add(new File("/home/valer/Projects/eu.larkc.reasoner/workspace/pariris/iris-impl-distributed/input/humans.rdf"), 
		//		"", //"http://www.know-center.at/ontologies/2009/2/software-project.owl", 
		//		RDFFormat.RDFXML, (Resource) null);
		
		//repConnection.commit();

		//TODO: this requires an additional import, seems not needed now, 
		//repositoryModel.readFrom(new FileInputStream(new File("/home/valer/Projects/eu.larkc.reasoner/workspace/pariris/iris-impl-distributed/input/humans.rdf")), 
		//				RDFFormat.RDFXML, ""); //"http://www.know-center.at/ontologies/2009/2/software-project.owl", (Resource) null);
		
		File outputFile = new File("/home/valer/Projects/eu.larkc.reasoner/workspace/pariris/iris-impl-distributed/output/humans.txt");
		FileOutputStream outputStream = new FileOutputStream(outputFile);
		OutputStreamWriter osw = new OutputStreamWriter(outputStream);
		
		ClosableIterator<org.ontoware.rdf2go.model.Statement> repResult = repositoryModel.iterator();
		while (repResult.hasNext()) {
			org.ontoware.rdf2go.model.Statement statement = repResult.next();
			logger.info(statement.getSubject() + " : " + statement.getPredicate() + " : " + statement.getObject());
			osw.write(statement.getSubject() + "\t" + statement.getPredicate() + "\t" + statement.getObject() + "\n");
		}
		osw.close();
		outputStream.close();
		
		/*
		Configuration config = new Configuration();
		FileSystem fs = FileSystem.get(config);
		Path path = new Path(DistributedReasoner.class.getResource("/").getPath() + "diseasedata");
		FSDataOutputStream outputStream = fs.create(path);

		RepositoryResult<Statement> repResult = repConnection.getStatements(null, null, null, false, (Resource) null);
		for (Statement statement : repResult.asList()) {
			logger.info(statement.getSubject() + " : " + statement.getPredicate() + " : " + statement.getObject());
			outputStream.writeChars(statement.getSubject() + " : " + statement.getPredicate() + " : " + statement.getObject());
		}
		*/
		
	}
	
}
