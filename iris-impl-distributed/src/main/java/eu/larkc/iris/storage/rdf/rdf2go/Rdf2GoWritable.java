package eu.larkc.iris.storage.rdf.rdf2go;

import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Statement;

public interface Rdf2GoWritable {

	public void write(Model model);
	
	public void read(Statement statement);
	
}
