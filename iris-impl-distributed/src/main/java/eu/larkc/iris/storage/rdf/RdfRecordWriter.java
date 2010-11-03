package eu.larkc.iris.storage.rdf;

import org.ontoware.rdf2go.model.Model;

import eu.larkc.iris.storage.AtomRecord;
import eu.larkc.iris.storage.FactsRecordWriter;

/** A RecordWriter that writes the reduce output to a SQL table */
public class RdfRecordWriter<K extends AtomRecord, V> extends FactsRecordWriter<K, V> {
	
	protected RdfRecordWriter(Model model) {
		super();
		factsStorage = new RdfStorage();
		((RdfStorage) factsStorage).setModel(model);
	}
	
}
