package cascading.rdf.rdf2go;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapred.InputFormat;
import org.apache.hadoop.mapred.InputSplit;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.JobConfigurable;
import org.apache.hadoop.mapred.RecordReader;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.util.ReflectionUtils;
import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Statement;

public class Rdf2GoInputFormat<T extends Rdf2GoWritable> implements
		InputFormat<LongWritable, T>, JobConfigurable {

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

	protected class RdfRepositoryInputSplit implements InputSplit {

		/** Default Constructor */
		public RdfRepositoryInputSplit() {
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
			return model.size() - 1;
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

	protected class RdfRepositoryRecordReader implements
			RecordReader<LongWritable, T> {

		private Class<T> inputClass;
		private JobConf job;
		private RdfRepositoryInputSplit split;

		private long pos = 0;
		ClosableIterator<Statement> iterator;

		public RdfRepositoryRecordReader(RdfRepositoryInputSplit split,
				Class<T> inputClass, JobConf job) {
			this.inputClass = inputClass;
			this.split = split;
			this.job = job;

			this.iterator = model.iterator();
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
			return pos / (float) split.getLength();
		}

	}

	protected Rdf2GoConfiguration rdfRepositoryConf;

	@Override
	public void configure(JobConf job) {
		rdfRepositoryConf = new Rdf2GoConfiguration(job);
		model = rdfRepositoryConf.getModel();
	}

	@Override
	public InputSplit[] getSplits(JobConf job, int numSplits)
			throws IOException {
		return new InputSplit[] { new RdfRepositoryInputSplit() };
	}

	@Override
	public RecordReader<LongWritable, T> getRecordReader(InputSplit split,
			JobConf job, Reporter reporter) throws IOException {
		Class inputClass = rdfRepositoryConf.getInputClass();
		return new RdfRepositoryRecordReader((RdfRepositoryInputSplit) split,
				inputClass, job);
	}

	public static void setInput(JobConf job,
			Class<? extends Rdf2GoWritable> inputClass) {
		job.setInputFormat(Rdf2GoInputFormat.class);

		Rdf2GoConfiguration rdf2GoConf = new Rdf2GoConfiguration(job);

		rdf2GoConf.setInputClass(inputClass);

	}

}
