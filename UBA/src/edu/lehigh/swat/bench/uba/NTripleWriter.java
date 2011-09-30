/**
 * 
 */
package edu.lehigh.swat.bench.uba;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

/**
 * @author valer
 *
 */
public class NTripleWriter implements Writer {

	PrintStream out = null;
	Generator generator;
	
	private String aboutId = null;
	
	public NTripleWriter(Generator generator) {
		this.generator = generator;
	}

	/* (non-Javadoc)
	 * @see edu.lehigh.swat.bench.uba.Writer#start()
	 */
	@Override
	public void start() {
	    try {
	      out = new PrintStream(new FileOutputStream("output.nt"));
	    }
	    catch (IOException e) {
	      System.out.println("Create file failure!");
	    }
	}

	/* (non-Javadoc)
	 * @see edu.lehigh.swat.bench.uba.Writer#end()
	 */
	@Override
	public void end() {
	    out.close();
	}

	/* (non-Javadoc)
	 * @see edu.lehigh.swat.bench.uba.Writer#startFile(java.lang.String)
	 */
	@Override
	public void startFile(String fileName) {}

	/* (non-Javadoc)
	 * @see edu.lehigh.swat.bench.uba.Writer#endFile()
	 */
	@Override
	public void endFile() {}

	/* (non-Javadoc)
	 * @see edu.lehigh.swat.bench.uba.Writer#startSection(int, java.lang.String)
	 */
	@Override
	public void startSection(int classType, String id) {
		aboutId = id;
		generator.startSectionCB(classType);
	    String s = "<" + id + "> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <" + generator.ontology + "#" + Generator.CLASS_TOKEN[classType] + "> .";
	    out.println(s);
	}

	/* (non-Javadoc)
	 * @see edu.lehigh.swat.bench.uba.Writer#startAboutSection(int, java.lang.String)
	 */
	@Override
	public void startAboutSection(int classType, String id) {
		aboutId = id;
		generator.startAboutSectionCB(classType);
		String s = "<" + id + "> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <" + generator.ontology + "#" + Generator.CLASS_TOKEN[classType] + "> .";
	    out.println(s);
	}

	/* (non-Javadoc)
	 * @see edu.lehigh.swat.bench.uba.Writer#endSection(int)
	 */
	@Override
	public void endSection(int classType) {
		aboutId = null;
	}

	/* (non-Javadoc)
	 * @see edu.lehigh.swat.bench.uba.Writer#addProperty(int, java.lang.String, boolean)
	 */
	@Override
	public void addProperty(int property, String value, boolean isResource) {
	    generator.addPropertyCB(property);

	    String s;
	    if (isResource) {
	      s = "<" + aboutId + "> <" + generator.ontology + "#" + Generator.PROP_TOKEN[property] + "> <" + value + "> .";
	    }
	    else { //literal
	      s = "<" + aboutId + "> <" + generator.ontology + "#" + Generator.PROP_TOKEN[property] + "> \"" + value + "\" .";
	    }

	    out.println(s);
	}

	/* (non-Javadoc)
	 * @see edu.lehigh.swat.bench.uba.Writer#addProperty(int, int, java.lang.String)
	 */
	@Override
	public void addProperty(int property, int valueClass, String valueId) {
	    generator.addPropertyCB(property);
	    generator.addValueClassCB(valueClass);

	    String s = "<" + aboutId + "> <" + generator.ontology + "#" + Generator.PROP_TOKEN[property] + "> <" + valueId + "> .";
	    
	    out.println(s);
	}

}
