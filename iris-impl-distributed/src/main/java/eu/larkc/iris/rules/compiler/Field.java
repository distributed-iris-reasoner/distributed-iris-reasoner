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
import org.deri.iris.api.terms.IVariable;

/**
 * Represents a field in the streams used to do the rule evaluation
 * 
 * @author valer.roman@softgress.com
 *
 */
@SuppressWarnings("rawtypes")
public class Field {

	private String name;
	protected Comparable source;

	public Field(String name, Comparable source) {
		this.name = name;
		this.source = source;
	}

	protected Field(Comparable source) {
		this.source = source;
	}

	public Comparable getSource() {
		return source;
	}

	public String getValue() {
		return source.toString();
	}
	
	public boolean isVariable() {
		return source instanceof IVariable;
	}

	public String getName() {
		return name;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Field)) {
			return false;
		}
		Field aField = (Field) obj;
		return new EqualsBuilder().append(name, aField.name).isEquals();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(name).hashCode();
	}
	
}
