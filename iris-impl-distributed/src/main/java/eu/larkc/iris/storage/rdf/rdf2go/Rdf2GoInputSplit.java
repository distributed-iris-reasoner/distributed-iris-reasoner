package eu.larkc.iris.storage.rdf.rdf2go;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.mapred.InputSplit;
import org.ontoware.rdf2go.model.Model;

public class Rdf2GoInputSplit implements InputSplit {

	private Model model;
	
	public Rdf2GoInputSplit() {
	}
	
	public Rdf2GoInputSplit(Model model) {
		this.model = model;
	}

	/** {@inheritDoc} */
	public String[] getLocations() throws IOException {
		// TODO Add a layer to enable SQL "sharding" and support locality
		return new String[] {};
	}

	/** @return The total row count in this split */
	public long getLength() throws IOException {
		return model.size();
	}

	/** {@inheritDoc} */
	public void readFields(DataInput input) throws IOException {
	}

	/** {@inheritDoc} */
	public void write(DataOutput output) throws IOException {
	}

}
