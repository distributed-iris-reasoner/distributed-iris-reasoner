package eu.larkc.iris.storage.rdf;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.mapred.InputSplit;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.RecordReader;
import org.apache.hadoop.mapred.Reporter;
import org.ontoware.rdf2go.model.Model;

import eu.larkc.iris.storage.AtomRecord;
import eu.larkc.iris.storage.FactsInputFormat;

public class RdfInputFormat<T extends AtomRecord> extends FactsInputFormat<T> {

	private Model model;
	
	@Override
	public InputSplit[] getSplits(JobConf job, int numSplits)
			throws IOException {
		return new InputSplit[] { new RdfInputSplit(RdfFactsConfiguration.getModel(job)) };
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public RecordReader<LongWritable, T> getRecordReader(InputSplit split,
			JobConf job, Reporter reporter) throws IOException {
		Class inputClass = job.getClass(RdfFactsConfiguration.INPUT_CLASS_PROPERTY, FactsInputFormat.NullAtomWritable.class);
		return new RdfFactsRecordReader((RdfInputSplit) split, inputClass, job);
	}

}
