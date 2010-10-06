/**
 * 
 */
package eu.larkc.reasoner;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.log4j.Logger;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;
import org.openrdf.sail.memory.MemoryStore;

/**
 * @author valer
 *
 */
public class ExtractRdfTriples {

	private static final Logger logger = Logger.getLogger(ExtractRdfTriples.class);
	
	public static void main(String[] args) throws RepositoryException, RDFParseException, IOException {
		Repository repository = new SailRepository(new MemoryStore());
		repository.initialize();
		
		RepositoryConnection repConnection = repository.getConnection();
		repConnection.add(new File("/home/valer/Projects/eu.larkc.reasoner/workspace/DistributedReasoner/input/disease.owl"), 
				"http://purl.org/obo/owl/", RDFFormat.RDFXML, (Resource) null);
		
		repConnection.commit();

		File outputFile = new File("/home/valer/Projects/eu.larkc.reasoner/workspace/DistributedReasoner/output/diseases_triples.txt");
		FileOutputStream outputStream = new FileOutputStream(outputFile);
		OutputStreamWriter osw = new OutputStreamWriter(outputStream);
		
		RepositoryResult<Statement> repResult = repConnection.getStatements(null, null, null, false, (Resource) null);
		for (Statement statement : repResult.asList()) {
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
