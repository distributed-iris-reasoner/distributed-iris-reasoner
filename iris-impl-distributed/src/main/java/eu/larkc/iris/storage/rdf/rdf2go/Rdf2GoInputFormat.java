package eu.larkc.iris.storage.rdf.rdf2go;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapred.InputFormat;
import org.apache.hadoop.mapred.InputSplit;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.RecordReader;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.util.ReflectionUtils;
import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Statement;

public class Rdf2GoInputFormat<T extends Rdf2GoWritable> implements
		InputFormat<LongWritable, T> {

	private Model model;

	/** A Class that does nothing, implementing RdfRepositoryWritable */
	public static class NullRdfWritable implements Rdf2GoWritable, Writable {
		@Override
		public void readFields(DataInput in) throws IOException {
		}

		@Override
		public void read(Statement statement) {
		}

		@Override
		public void write(DataOutput out) throws IOException {
		}

		@Override
		public void write(Model model) {
		}
	}


	protected class RdfRepositoryRecordReader implements
			RecordReader<LongWritable, T> {

		private Class<T> inputClass;
		private JobConf job;
		private Rdf2GoInputSplit split;

		private long pos = 0;
		ClosableIterator<Statement> iterator;

		public RdfRepositoryRecordReader(Rdf2GoInputSplit split,
				Class<T> inputClass, JobConf job) {
			this.inputClass = inputClass;
			this.split = split;
			this.job = job;

			this.iterator = Rdf2GoConfiguration.getModel(job).iterator();
		}

		@Override
		public boolean next(LongWritable key, T value) throws IOException {
			if (!this.iterator.hasNext()) {
				return false;
			}
			value.read(this.iterator.next());
			pos++;
			return true;
		}

		@Override
		public LongWritable createKey() {
			return new LongWritable();
		}

		@Override
		public T createValue() {
			return ReflectionUtils.newInstance(inputClass, job);
		}

		@Override
		public long getPos() {
			return pos;
		}

		@Override
		public void close() throws IOException {
		}

		@Override
		public float getProgress() throws IOException {
			if (model == null) {
				return 0;
			}
			return pos / (float) split.getLength();
		}

	}

	@Override
	public InputSplit[] getSplits(JobConf job, int numSplits)
			throws IOException {
		return new InputSplit[] { new Rdf2GoInputSplit(Rdf2GoConfiguration.getModel(job)) };
	}

	@Override
	public RecordReader<LongWritable, T> getRecordReader(InputSplit split,
			JobConf job, Reporter reporter) throws IOException {
		Class inputClass = job.getClass(Rdf2GoConfiguration.INPUT_CLASS_PROPERTY, Rdf2GoInputFormat.NullRdfWritable.class);
		return new RdfRepositoryRecordReader((Rdf2GoInputSplit) split,
				inputClass, job);
	}

}
