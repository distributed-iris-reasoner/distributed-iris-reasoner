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

import cascading.pipe.Pipe;

/**
 * @author valer
 *
 */
public class PipeFielded {

	private Pipe pipe;
	private FieldsList fieldsList;
	
	public PipeFielded(Pipe pipe, FieldsList fieldsList) {
		this.pipe = pipe;
		this.fieldsList = fieldsList;
	}

	public Pipe getPipe() {
		return pipe;
	}

	public FieldsList getFieldsList() {
		return fieldsList;
	}
	
}
