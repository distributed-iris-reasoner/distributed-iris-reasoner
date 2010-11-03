/**
 * 
 */
package eu.larkc.iris.storage;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.mapred.InputSplit;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.RecordReader;
import org.apache.hadoop.util.ReflectionUtils;
import org.deri.iris.api.basics.IAtom;

import eu.larkc.iris.storage.rdf.RdfStorage;

public class FactsRecordReader<T extends AtomRecord> implements RecordReader<LongWritable, T> {

	private Class<T> inputClass;
	private JobConf job;
	private InputSplit split;

	private long pos = 0;
	protected RdfStorage rdfStorage;

	public FactsRecordReader(InputSplit split, Class<T> inputClass, JobConf job) {
		this.inputClass = inputClass;
		this.split = split;
		this.job = job;
	}

	@Override
	public boolean next(LongWritable key, T value) throws IOException {
		IAtom atom = rdfStorage.next();
		if (atom == null) {
			return false;
		}
		value.read(rdfStorage, atom);
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
		if (split == null) {
			return 0;
		}
		return pos / (float) split.getLength();
	}

}
