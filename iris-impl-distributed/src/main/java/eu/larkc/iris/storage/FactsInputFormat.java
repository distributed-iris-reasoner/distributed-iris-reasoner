package eu.larkc.iris.storage;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.mapred.InputFormat;
import org.deri.iris.api.basics.IAtom;


public abstract class FactsInputFormat<T extends AtomRecord> implements
		InputFormat<LongWritable, T> {

	public static class NullAtomWritable extends AtomRecord {

		@Override
		public void read(FactsStorage storage, IAtom atom) {
		}

		@Override
		public void write(FactsStorage storage) {
		}
	}

}
