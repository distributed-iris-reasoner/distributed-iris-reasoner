/*
 * Copyright 2010 Softgress - http://www.softgress.com/
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.larkc.iris.storage;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.mapred.InputSplit;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.RecordReader;
import org.apache.hadoop.util.ReflectionUtils;
import org.deri.iris.api.basics.IAtom;

public class FactsRecordReader<T extends AtomRecord> implements RecordReader<LongWritable, T> {

	private Class<T> inputClass;
	private JobConf job;
	private InputSplit split;

	private long pos = 0;
	protected FactsStorage factsStorage;

	public FactsRecordReader(InputSplit split, Class<T> inputClass, JobConf job) {
		this.inputClass = inputClass;
		this.split = split;
		this.job = job;
	}

	@Override
	public boolean next(LongWritable key, T value) throws IOException {
		IAtom atom = factsStorage.next();
		if (atom == null) {
			return false;
		}
		value.read(atom);
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
