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

import org.deri.iris.api.terms.IVariable;

/**
 * @author valer
 *
 */
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
	
}
