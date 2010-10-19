package eu.larkc.iris.storage.rdf.rdf2go;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.mapred.InputSplit;

public class Rdf2GoInputSplit implements InputSplit {

	/** Default Constructor */
	public Rdf2GoInputSplit() {
	}

	/** {@inheritDoc} */
	public String[] getLocations() throws IOException {
		// TODO Add a layer to enable SQL "sharding" and support locality
		return new String[] {};
	}

	/** @return The index of the first row to select */
	public long getStart() {
		return 0;
	}

	/** @return The index of the last row to select */
	public long getEnd() {
		return 100;
	}

	/** @return The total row count in this split */
	public long getLength() throws IOException {
		return 100;
	}

	/** {@inheritDoc} */
	public void readFields(DataInput input) throws IOException {
	}

	/** {@inheritDoc} */
	public void write(DataOutput output) throws IOException {
	}

}
