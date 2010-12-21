/*
 * Integrated Rule Inference System (IRIS):
 * An extensible rule inference system for datalog with extensions.
 * 
 * Copyright (C) 2009 Semantic Technology Institute (STI) Innsbruck, 
 * University of Innsbruck, Technikerstrasse 21a, 6020 Innsbruck, Austria.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, 
 * MA  02110-1301, USA.
 */
package org.deri.iris.builtins.list;

import junit.framework.TestCase;

import org.deri.iris.api.basics.ITuple;
import org.deri.iris.api.terms.IConcreteTerm;
import org.deri.iris.api.terms.ITerm;
import org.deri.iris.api.terms.concrete.IList;
import org.deri.iris.factory.Factory;
import org.deri.iris.terms.concrete.IntTerm;

public abstract class AbstractListBuiltinTest extends TestCase {

	protected static final ITerm X = Factory.TERM.createVariable("X");

	protected static final ITerm Y = Factory.TERM.createVariable("Y");

	protected static final ITerm Z = Factory.TERM.createVariable("Z");

	protected static final IConcreteTerm ZERO = new IntTerm(0);
	
	protected static final IConcreteTerm ONE = new IntTerm(1);

	protected static final IConcreteTerm TWO = new IntTerm(2);

	protected static final IConcreteTerm THREE = new IntTerm(3);

	protected static final IConcreteTerm FOUR = new IntTerm(4);

	protected static final ITuple EMPTY_TUPLE = Factory.BASIC.createTuple();

	protected static final IList EMPTY_LIST = new org.deri.iris.terms.concrete.List();
	
	protected ITuple args = null;
	
	protected ITuple actual = null;

}
