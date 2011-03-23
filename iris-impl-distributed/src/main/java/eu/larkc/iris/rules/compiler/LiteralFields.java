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
package eu.larkc.iris.rules.compiler;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.hadoop.io.WritableComparable;
import org.deri.iris.api.basics.IAtom;
import org.deri.iris.api.basics.ILiteral;
import org.deri.iris.api.basics.IPredicate;
import org.deri.iris.api.builtins.IBuiltinAtom;
import org.deri.iris.api.terms.IConstructedTerm;
import org.deri.iris.api.terms.IStringTerm;
import org.deri.iris.api.terms.ITerm;
import org.deri.iris.api.terms.IVariable;
import org.deri.iris.api.terms.concrete.IIri;

import cascading.pipe.Each;
import cascading.pipe.Pipe;
import cascading.pipe.assembly.Rename;
import eu.larkc.iris.Configuration;
import eu.larkc.iris.evaluation.ConstantFilter;
import eu.larkc.iris.indexing.DistributedFileSystemManager;
import eu.larkc.iris.indexing.PredicateData;
import eu.larkc.iris.rules.compiler.RuleStreams.LiteralId;
import eu.larkc.iris.storage.IRIWritable;
import eu.larkc.iris.storage.WritableFactory;

/**
 * Stream containing only literal fields
 * 
 * @author valer
 *
 */
public class LiteralFields extends eu.larkc.iris.rules.compiler.PipeFields {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 6351207734635496829L;

	public static final String RIF_HAS_VALUE = "RIF_HAS_VALUE";
	
	private static final String PREDICATE_PREFIX = "P";
	private static final String VARIABLE_PREFIX = "V";
	private static final String CONSTANT_PREFIX = "C";
	
	private LiteralId id;
	
	public class TermId {
		private LiteralId literalId;
		private String prefix;
		private int index = -1;

		public TermId(LiteralId literalId, String prefix) {
			this.literalId = literalId;
			this.prefix = prefix;
		}
		
		public TermId(LiteralId literalId, String prefix, int index) {
			this(literalId, prefix);
			this.index = index;
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			if (index == -1) {
				return literalId + prefix;
			} else {
				return literalId + prefix + index;
			}
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof TermId)) {
				return false;
			}
			TermId termId = (TermId) obj;
			return new EqualsBuilder().append(literalId, termId.literalId)
				.append(prefix, termId.prefix).append(index, termId.index).isEquals();
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			return new HashCodeBuilder().append(literalId).append(prefix).append(index).hashCode();
		}
	}
	
	LiteralFields(Configuration configuration, Pipe mainPipe, LiteralId literalId, ILiteral literal) {
		this.id = literalId;
		this.count = calculateCount(configuration);
		IAtom atom = literal.getAtom();
		IPredicate predicate = atom.getPredicate();
		if (!predicate.getPredicateSymbol().equals(RIF_HAS_VALUE)) {
			add(new LiteralField(new TermId(literalId, PREDICATE_PREFIX), predicate));
		}
		for (int i = 0; i < atom.getTuple().size(); i++) {
			ITerm term = atom.getTuple().get(i);
			if (term instanceof IVariable) {
				add(new LiteralField(new TermId(literalId, VARIABLE_PREFIX, i), term));
			} else if (term instanceof IIri) {
				add(new LiteralField(new TermId(literalId, CONSTANT_PREFIX, i), term));
			} else if (term instanceof IStringTerm) {
				add(new LiteralField(new TermId(literalId, CONSTANT_PREFIX, i), term));
			}
		}
		
		if (mainPipe != null) {
			pipe = new Pipe(getId().toString(), mainPipe);
		} else {
			pipe = new Pipe(getId().toString());
		}
		//pipe = new Rename(pipe, new cascading.tuple.Fields(0, 1, 2), getFields());
		pipe = new Rename(pipe, cascading.tuple.Fields.ALL, getFields());
		
		pipe = filterConstants(pipe);
	}

	LiteralFields(Configuration configuration, LiteralId literalId, ILiteral literal) {
		this(configuration, null, literalId, literal);
	}

	public LiteralId getId() {
		return id;
	}
	
	/*
	 * Gets the record count for a predicate from indexed predicate storage
	 */
	private Long calculateCount(Configuration configuration) {
		DistributedFileSystemManager dfsm = new DistributedFileSystemManager(configuration);
		IPredicate predicate = this.getPredicate();
		if (predicate != null) {
			PredicateData predicateData = dfsm.getPredicateData(new IRIWritable(predicate));
			return predicateData.getCount();
		}
		return Long.MAX_VALUE;
	}

	public boolean fromBuiltInAtom() {
		return false;
	}
	
	/**
	 * Converts a built-in atom to a suitable cascading operation per the RIF
	 * specs. This is currently a stub implementation.
	 * 
	 * @param atom
	 */
	protected void processBuiltin(IBuiltinAtom atom) {

		boolean constructedTerms = false;
		for (ITerm term : atom.getTuple()) {
			if (term instanceof IConstructedTerm) {
				constructedTerms = true;
				break;
			}
		}
		
	
		if (constructedTerms) {
			// function symbol
			throw new NotImplementedException(
					"Function Symbols are not supported");
		} else {
			// ordinary built-in, those WILL be handled
			throw new NotImplementedException("Builtins not implemented yet");
		}
	}

	/* (non-Javadoc)
	 * @see eu.larkc.iris.rules.compiler.PipeFields#getPipe()
	 */
	@Override
	public Pipe getPipe() {
		return pipe;
	}

	/**
	 * This filters constants by providing in tuple streams according to the
	 * original rule defintion.
	 * 
	 * @param attachTo
	 * @param tuple
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected Pipe filterConstants(Pipe attachTo) {
		Map<String, WritableComparable> constantTerms = new HashMap<String, WritableComparable>();

		for (Field field : this) {
			if (field.getSource() instanceof IPredicate) {
				constantTerms.put(field.getName(), WritableFactory.fromPredicate((IPredicate) field.getSource())); //added one because of the predicate field
			} else if (field.getSource() instanceof ITerm) {
				ITerm term = (ITerm) field.getSource();
				// not a variable, we filter the tuples
				if (term.isGround()) {
					constantTerms.put(field.getName(), WritableFactory.fromTerm(term)); //added one because of the predicate field
				}
			}
		}

		// did we actually find at least one constant?
		if (!constantTerms.isEmpty()) {
			Pipe filter = new Each(attachTo, new ConstantFilter(constantTerms));
			return filter;
		}

		// nothing changed
		return attachTo;
	}

	/**
	 * Get the predicate literal for this stream.
	 * 
	 * @return the predicate or null if none found
	 */
	public IPredicate getPredicate() {
		for (Field field : this) {
			if (!(field instanceof LiteralField)) {
				continue;
			}
			LiteralField literalField = (LiteralField) field;
			if (literalField.getSource() instanceof IPredicate) {
				return (IPredicate) literalField.getSource();
			}
		}
		return null;
	}
}
