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
import org.deri.iris.api.basics.IPredicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

	private static final Logger logger = LoggerFactory.getLogger(FactsScheme.class);
	
	private Class<? extends AtomRecord> inputClass = null;
	
	private String storageId;
	private IPredicate[] predicates = null;
	private IAtom atom = null;
	
	public FactsScheme(String storageId) {
		this.storageId = storageId;
	}

	public FactsScheme(String storageId, IPredicate... predicates) {
		this(storageId);
		this.predicates = predicates;		
		int arity = -1;
		for (IPredicate predicate : predicates) {
			int aArity = predicate.getArity();
			if (arity != -1 && arity != aArity) {
				throw new RuntimeException("Different arity predicates!");
			}
			arity = aArity;
		}
		Fields sourceFields = new Fields("PREDICATE");
		for (int i = 0; i < arity; i++) {
			sourceFields = sourceFields.append(new Fields("TERM" + (i+1)));
		}
		setSourceFields(sourceFields);
	}
	
	public FactsScheme(String storageId, IAtom atom) {
		this(storageId);
		this.atom = atom;

		setSourceFields(new Fields(0, 1, 2));
		//setSinkFields(sourceFields); // TODO the sink fields I guess will be the
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
					storageId, rhs.storageId).append(
					atom, rhs.atom).isEquals();
		} else if (predicates != null) {
			FactsScheme rhs = (FactsScheme) obj;
			return new EqualsBuilder().appendSuper(super.equals(obj)).append(
					storageId, rhs.storageId).append(
					predicates, rhs.predicates).isEquals();
		} else {
			FactsScheme rhs = (FactsScheme) obj;
			return new EqualsBuilder().appendSuper(super.equals(obj)).append(
					storageId, rhs.storageId).isEquals();
		}
	}

	@Override
	public int hashCode() {
		if (atom != null) {
			return new HashCodeBuilder().appendSuper(super.hashCode()).append(storageId.hashCode()).append(atom.hashCode()).toHashCode();
		} else if (predicates != null) {
			HashCodeBuilder hab = new HashCodeBuilder().appendSuper(super.hashCode()).append(storageId.hashCode());
			for (IPredicate predicate : predicates) {
				hab.append(predicate.hashCode());
			}
			return hab.toHashCode();
		} else {
			return super.hashCode();
		}
	}

	@Override
	public void sourceInit(Tap tap, JobConf jobConf) throws IOException {
		IFactsConfiguration factsConfiguration = FactsConfigurationFactory
				.getFactsConfiguration(jobConf);
		factsConfiguration.configureInput();
	}

	@Override
	public void sinkInit(Tap tap, JobConf jobConf) throws IOException {
		if (atom != null || predicates != null)
			throw new TapException("cannot sink to this Scheme");

		IFactsConfiguration factsConfiguration = FactsConfigurationFactory
				.getFactsConfiguration(jobConf);
		factsConfiguration.configureOutput();
		inputClass = factsConfiguration.getInputClass();
	}

	@Override
	public Tuple source(Object key, Object value) {
		//Fields sourceFields = getSourceFields();
		Tuple tuple = ((AtomRecord) value).getTuple();
		// assert sourceFields.size() == tuple.size();
		return tuple;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void sink(TupleEntry tupleEntry, OutputCollector outputCollector)
			throws IOException {
		Tuple result = tupleEntry.selectTuple(getSinkFields());
		//assert factsConfiguration != null;
		//AtomRecord atomRecord =
		
		logger.info("output : " + result);
		
		AtomRecord atomRecord = null;
		try {
			atomRecord = inputClass.newInstance();
		} catch (Exception e) {
			logger.error("exception", e);
			return;
		}
		atomRecord.setTuple(result);
		outputCollector.collect(atomRecord, null); //FIXME abstract RdfRecord class
	}

}