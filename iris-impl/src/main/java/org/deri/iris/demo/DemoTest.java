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
package org.deri.iris.demo;
//
//import junit.framework.TestCase;
//import org.deri.iris.KnowledgeBaseFactory;
//
//public class DemoTest extends TestCase
//{
//	public void testSimpleProgram() throws Exception
//	{
//    	String program = 
//		    "p(1)." +
//		    "p(2)." +
//		    
//		    "q(?X) :- p(?X)." +
//		    "?- q(?x).";
//    	
//    	helperExecuteProgram( program );
//	}
//
//	public void testEmptyProgram() throws Exception
//	{
//    	helperExecuteProgram( "" );
//	}
//	
//	public void testProgramWithoutQuery() throws Exception
//	{
//    	String program = 
//		    "p(1)." +
//		    "p(2)." +
//		    
//		    "q(?X) :- p(?X).";
//    
//    	helperExecuteProgram( program );
//	}
//
//	public void testProgramwithTwoQueries() throws Exception
//	{
//    	String program = 
//		    "p(1)." +
//		    "p(2)." +
//		    
//		    "q(?X) :- p(?X)." +
//		    "?- p(?x)." +
//    		"?- q(?x).";
//    	
//    	helperExecuteProgram( program );
//	}
//
//	private void helperExecuteProgram( String program ) throws Exception
//	{
//    	Demo.main( new String[]{ Demo.PROGRAM + "=" + program, Demo.UNSAFE_RULES, Demo.WELL_FOUNDED } );
//    	Demo.execute( program, KnowledgeBaseFactory.getDefaultConfiguration() );
//	}
//}
