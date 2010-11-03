package eu.larkc.iris.storage;

import java.io.IOException;

import org.apache.hadoop.mapred.RecordWriter;
import org.apache.hadoop.mapred.Reporter;


/** A RecordWriter that writes the reduce output to a SQL table */
public class FactsRecordWriter<K extends AtomRecord, V> implements RecordWriter<K, V> {
	protected FactsStorage factsStorage;

	protected FactsRecordWriter() {
	}

	/** {@inheritDoc} */
	public void close(Reporter reporter) throws IOException {
	}

	/** {@inheritDoc} */
	public synchronized void write(K key, V value) throws IOException {
		//FIXME should never be null, but when I write to text file it is, I guess I 
		//should not set in the configuration the output when tap is not sink
		if (key == null) {
			return;
		}
		key.write(this.factsStorage);
	}
}
