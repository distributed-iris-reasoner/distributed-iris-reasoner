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

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.mapred.InputFormat;
import org.deri.iris.api.basics.IAtom;


public abstract class FactsInputFormat<T extends AtomRecord> implements
		InputFormat<LongWritable, T> {

	public static class NullAtomWritable extends AtomRecord {

		@Override
		public void read(IAtom atom) {
		}

		@Override
		public void write(FactsStorage storage) {
		}
	}

}
