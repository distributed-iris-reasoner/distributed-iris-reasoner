package eu.larkc.iris.storage.rdf;

import org.apache.hadoop.mapred.InputSplit;
import org.apache.hadoop.mapred.JobConf;

import eu.larkc.iris.storage.AtomRecord;
import eu.larkc.iris.storage.FactsRecordReader;

public class RdfFactsRecordReader<LongWritable, T extends AtomRecord> extends FactsRecordReader<T> {
	
	public RdfFactsRecordReader(InputSplit split, Class<T> inputClass, JobConf job) {
		super(split, inputClass, job);
		
		rdfStorage = new RdfStorage();
		rdfStorage.setModel(RdfFactsConfiguration.getModel(job));
	}
}
