/*
 * Integrated Rule Inference System (IRIS):
 * An extensible rule inference system for datalog with extensions.
 * 
 * Copyright (C) 2008 Semantic Technology Institute (STI) Innsbruck, 
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
package org.deri.iris.functional;

import junit.framework.TestCase;

public class WellFoundedSemanticsTest extends TestCase
{
	public void testKempSrivastavaStuckeyExample1() throws Exception
	{
		String program =
			"p(?x) :- t(?x, ?y, ?z), not p(?y), not p(?z)." +
			"p('b') :- not r('a')." +
			"t( 'a', 'a', 'b')." +
			"t( 'a', 'b', 'a')." +
			"?- p(?x).";
		
		Helper.evaluateWellFounded( program, "dummy('b')." );
	}

	public void testKempSrivastavaStuckeyExample2() throws Exception
	{
		String program =
			"p('a') :- q('a'), ! r('a')." +
			"q('a') :- ! p('a')." +
			"r('a').";
		
		Helper.evaluateWellFounded( program + "?- r(?x).", "dummy('a')." );
		Helper.evaluateWellFounded( program + "?- p(?x).", "" );
		Helper.evaluateWellFounded( program + "?- q(?x).", "dummy('a')." );
	}

	public void testKempSrivastavaStuckeyExample2WithVariables() throws Exception
	{
		String program =
			"p(?x) :- q(?x), ! r(?x)." +
			"q(?x) :- ! p(?x)." +
			"r('a').";
		
		Helper.evaluateWellFounded( program + "?- r(?x).", "dummy('a')." );
		Helper.evaluateWellFounded( program + "?- p(?x).", "" );
		Helper.evaluateWellFounded( program + "?- q(?x).", "dummy('a')." );
	}

	public void testKempSrivastavaStuckeyExample3() throws Exception
	{
		String program =
			"p('a') :- q('a'), ! r('a')." +
			"q('a') :- ! q('a')." +
			"r('a').";
		
		Helper.evaluateWellFounded( program + "?- r(?x).", "dummy('a')." );
		Helper.evaluateWellFounded( program + "?- p(?x).", "" );
		Helper.evaluateWellFounded( program + "?- q(?x).", "" );
	}
	
	public void testKempSrivastavaStuckeyExample3WithVariables() throws Exception
	{
		String program =
			"p(?x) :- q(?x), ! r(?x)." +
			"q(?x) :- ! q(?x)." +
			"r('a').";
		
		Helper.evaluateWellFounded( program + "?- r(?x).", "dummy('a')." );
		Helper.evaluateWellFounded( program + "?- p(?x).", "" );
		Helper.evaluateWellFounded( program + "?- q(?x).", "" );
	}
	
	// NB This is actually a stratified program.
	public void testGelderRossSchlipf_IntendedModelExample1() throws Exception
	{
		String program =
			"noise(?t) :- loaded(?t), shoots(?t)." +
			"loaded(0)." +
			"loaded(?t) :- succ( ?s, ?t ), loaded( ?s), not shoots( ?s )." +
			"shoots( ?t ) :- triggers( ?t )." +
			"triggers(1)." +
			"succ(0,1).";
		
		Helper.evaluateWellFounded( program + "?- loaded(?x).", "dummy(0).dummy(1)." );
		Helper.evaluateWellFounded( program + "?- shoots(?x).", "dummy(1)." );
		Helper.evaluateWellFounded( program + "?- noise(?x).", "dummy(1)." );
	}

	// NB This is also a stratified program.
	public void testGelderRossSchlipf_IntendedModelExample2() throws Exception
	{
		String program =
			"p(?x,?y) :- b(?x,?y)." +
			"p(?x,?y) :- b(?x,?u), p(?u,?y)." +
			"e(?x,?y) :- g(?x,?y)." +
			"e(?x,?y) :- g(?x,?u), e(?u,?y)." +

			"a(?x,?y) :- e(?x,?y), not p(?x,?y)." +

			"b(1,2).g(2,3)." +
			"b(2,1).g(3,2)." +
			"?- a(?x,?y).";
		
		Helper.evaluateWellFounded( program, "dummy(2,3).dummy(3,2).dummy(3,3)." );
	}
}
