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

import org.deri.iris.api.basics.IAtom;

import cascading.pipe.Pipe;

/**
 * @author valer.roman@softgress.com
 *
 */
public class SubGoal {

	private IAtom atom;
	private Pipe pipe;
	
	public SubGoal(IAtom atom, Pipe pipe) {
		this.atom = atom;
		this.pipe = pipe;
	}
	
	/**
	 * @return the atom
	 */
	public IAtom getAtom() {
		return atom;
	}
	/**
	 * @return the pipe
	 */
	public Pipe getPipe() {
		return pipe;
	}
	
}