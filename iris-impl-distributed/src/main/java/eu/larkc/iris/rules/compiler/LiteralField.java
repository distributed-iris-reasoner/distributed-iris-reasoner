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

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.deri.iris.api.basics.IPredicate;
import org.deri.iris.api.terms.IStringTerm;
import org.deri.iris.api.terms.IVariable;
import org.deri.iris.api.terms.concrete.IIri;

import eu.larkc.iris.rules.compiler.LiteralFields.TermId;

/**
 * Field used for streams created from Iris literals.
 * Their sources is the literal
 */
public class LiteralField extends Field {

	/**
	 * The id of the field
	 */
	TermId id;
	
	@SuppressWarnings("rawtypes")
	public LiteralField(TermId termId, Comparable source) {
		super(source);
		this.id = termId;
		
	}
	
	public String getValue() {
		if (source instanceof IPredicate) {
			return ((IPredicate) source).getPredicateSymbol();
		} else if (source instanceof IVariable) {
			return ((IVariable) source).getValue();
		} else if (source instanceof IIri) {
			return ((IIri) source).getValue();
		} else if (source instanceof IStringTerm) {
			return ((IStringTerm) source).getValue();
		}
		return super.getValue();
	}
	
	/* (non-Javadoc)
	 * @see eu.larkc.iris.rules.compiler.Field#getName()
	 */
	@Override
	public String getName() {
		return id.toString();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return id.toString();
	}

	/* (non-Javadoc)
	 * @see eu.larkc.iris.rules.compiler.Field#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof LiteralField)) {
			return false;
		}
		LiteralField aField = (LiteralField) obj;
		return new EqualsBuilder().append(id, aField.id).isEquals();
	}

	/* (non-Javadoc)
	 * @see eu.larkc.iris.rules.compiler.Field#hashCode()
	 */
	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(id).hashCode();
	}

}
