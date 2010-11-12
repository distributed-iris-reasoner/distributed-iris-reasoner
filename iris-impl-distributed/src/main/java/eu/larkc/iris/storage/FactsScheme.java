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

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.OutputCollector;
import org.deri.iris.api.basics.IAtom;
import org.deri.iris.api.basics.ITuple;

import cascading.scheme.Scheme;
import cascading.tap.Tap;
import cascading.tap.TapException;
import cascading.tuple.Fields;
import cascading.tuple.Tuple;
import cascading.tuple.TupleEntry;

/**
 * Scheme used with a facts tap the source fields are the variable names of the
 * atom's tuple
 * 
 * @history 03.11.2010, creation
 * @author Valer Roman
 * 
 */
// TODO separate the implementation specific for rdf storages
public class FactsScheme extends Scheme {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 3622684910621818754L;

	private IAtom atom = null;

	IFactsConfiguration factsConfiguration = null;

	public FactsScheme() {
	}

	public FactsScheme(IAtom atom) {
		this.atom = atom;
		ITuple tuple = atom.getTuple();
		Fields sourceFields = new Fields();
		for (int i = 0; i < tuple.size(); i++) {
			Object value = tuple.get(i).getValue();
			// TODO check which types can the value have. also decide what field
			// name should we give for constants
			sourceFields = sourceFields.append(new Fields((String) value));
		}
		setSourceFields(sourceFields);
		setSinkFields(sourceFields); // TODO the sink fields I guess will be the
										// variables of the head of the rule
	}


	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (obj.getClass() != getClass()) {
			return false;
		}
		if (atom != null) {
			FactsScheme rhs = (FactsScheme) obj;
			return new EqualsBuilder().appendSuper(super.equals(obj)).append(
					atom, rhs.atom).isEquals();
		} else {
			return super.equals(obj);
		}
	}

	@Override
	public int hashCode() {
		if (atom != null) {
			return new HashCodeBuilder().appendSuper(super.hashCode()).append(atom.hashCode()).toHashCode();
		} else {
			return super.hashCode();
		}
	}

	@Override
	public void sourceInit(Tap tap, JobConf jobConf) throws IOException {
		factsConfiguration = FactsConfigurationFactory
				.getFactsConfiguration(jobConf);
		factsConfiguration.configureInput(jobConf);
	}

	@Override
	public void sinkInit(Tap tap, JobConf jobConf) throws IOException {
		if (atom != null)
			throw new TapException("cannot sink to this Scheme");

		factsConfiguration = FactsConfigurationFactory
				.getFactsConfiguration(jobConf);
		factsConfiguration.configureOutput(jobConf);
	}

	@Override
	public Tuple source(Object key, Object value) {
		Fields sourceFields = getSourceFields();
		Tuple tuple = ((AtomRecord) value).getTuple();
		// assert sourceFields.size() == tuple.size();
		return tuple;
	}

	@Override
	public void sink(TupleEntry tupleEntry, OutputCollector outputCollector)
			throws IOException {
		Tuple result = tupleEntry.selectTuple(getSinkFields());
		assert factsConfiguration != null;
		outputCollector.collect(factsConfiguration.newRecordInstance(result),
				null);
	}

}
