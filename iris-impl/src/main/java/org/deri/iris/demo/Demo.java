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

import java.io.FileReader;
import java.io.IOException;

import org.deri.iris.Configuration;
import org.deri.iris.KnowledgeBaseFactory;
import org.deri.iris.evaluation.stratifiedbottomup.StratifiedBottomUpEvaluationStrategyFactory;
import org.deri.iris.evaluation.stratifiedbottomup.naive.NaiveEvaluatorFactory;
import org.deri.iris.evaluation.stratifiedbottomup.seminaive.SemiNaiveEvaluatorFactory;
import org.deri.iris.evaluation.wellfounded.WellFoundedEvaluationStrategyFactory;
import org.deri.iris.optimisations.magicsets.MagicSets;
import org.deri.iris.optimisations.rulefilter.RuleFilter;
import org.deri.iris.rules.safety.AugmentingRuleSafetyProcessor;
import org.deri.iris.rules.safety.StandardRuleSafetyProcessor;

/**
 * A command line demonstrator for IRIS.
 */
public class Demo
{
	public static final String WELL_FOUNDED = "well-founded";

	public static final String NAIVE = "naive";
	public static final String SEMI_NAIVE = "semi-naive";

	public static final String SAFE_RULES = "safe-rules";
	public static final String UNSAFE_RULES = "unsafe-rules";

	public static final String MAGIC_SETS = "magic-sets";

	public static final String TIMEOUT = "timeout";

	public static final String PROGRAM = "program";
	public static final String PROGRAM_FILE = "program-file";
	
	private static void usage()
	{
		String space = "    ";
		
		System.out.println(); 
		System.out.println( "Usage: java org.deri.iris.Demo <ARGUMENTS>" );
		System.out.println(); 
		System.out.println( "where <ARGUMENTS> is made up of:" ); 
		System.out.println( space + PROGRAM + "=<datalog program>" ); 
		System.out.println( space + PROGRAM_FILE+ "=<filename containing datalog program>" ); 
		System.out.println( space + TIMEOUT + "=<timeout in miliseconds> (default is to run forever)" ); 
		System.out.println( space + WELL_FOUNDED + " (to use the well-founded evaluation strategy)" ); 
		System.out.println( space + NAIVE + " (to use naive rule evaluation)" ); 
		System.out.println( space + SEMI_NAIVE + "* (to use semi-naive rule evaluation)" ); 
		System.out.println( space + SAFE_RULES + "* (to allow only safe rules)" ); 
		System.out.println( space + UNSAFE_RULES + " (to process unsafe rules)" ); 
		System.out.println( space + MAGIC_SETS + " (to use magic sets and rule-filtering optimisations)" ); 
		System.out.println( "(*=default)" );
		
		System.exit( 1 );
	}
	
	private static boolean startsWith( String argument, String token )
	{
		if( argument.length() < token.length() )
			return false;
		
		String start = argument.substring( 0, token.length() );
		
		return start.equalsIgnoreCase( token );
	}
	
	private static String getParameter( String argument )
	{
		int equals = argument.indexOf( '=' );
		
		if( equals >= 0 )
		{
			return argument.substring( equals + 1 );
		}
		
		return null;
	}
	
	private static final String loadFile( String filename ) throws IOException
	{
		FileReader r = new FileReader( filename );
		
		StringBuilder builder = new StringBuilder();
		
		int ch = -1;
		while( ( ch = r.read() ) >= 0 )
		{
			builder.append( (char) ch );
		}
		return builder.toString();
	}

	/**
	 * Entry point.
	 * @param args program evaluation_method
	 * @throws Exception 
	 */
	public static void main( String[] args )
	{
		String program = null;
		
		Configuration configuration = KnowledgeBaseFactory.getDefaultConfiguration();
		
		for( String argument : args )
		{
			if( startsWith( argument, PROGRAM_FILE ) )
			{
				String filename = getParameter( argument );
				try
				{
					program = loadFile( filename );
				}
				catch( Exception e )
				{
					System.out.println( "Unable to load input file '" + filename + "': " + e.getMessage() );
					System.exit( 2 );
				}
			}
			else if( startsWith( argument, PROGRAM ) )
				program = getParameter( argument );
			else if( startsWith( argument, TIMEOUT ) )
				configuration.evaluationTimeoutMilliseconds = Integer.parseInt( getParameter( argument ) );
			else if( startsWith( argument, WELL_FOUNDED ) )
				configuration.evaluationStrategyFactory = new WellFoundedEvaluationStrategyFactory();
			else if( startsWith( argument, NAIVE ) )
				configuration.evaluationStrategyFactory = new StratifiedBottomUpEvaluationStrategyFactory( new NaiveEvaluatorFactory() );
			else if( startsWith( argument, SEMI_NAIVE ) )
				configuration.evaluationStrategyFactory = new StratifiedBottomUpEvaluationStrategyFactory( new SemiNaiveEvaluatorFactory() );
			else if( startsWith( argument, SAFE_RULES ) )
				configuration.ruleSafetyProcessor = new StandardRuleSafetyProcessor();
			else if( startsWith( argument, UNSAFE_RULES ) )
				configuration.ruleSafetyProcessor = new AugmentingRuleSafetyProcessor();
			else if( startsWith( argument, MAGIC_SETS ) )
			{
				configuration.programOptmimisers.add( new RuleFilter() );
				configuration.programOptmimisers.add( new MagicSets() );
			}
			else
				usage();
		}
		
		if( program == null )
			usage();
		
		System.out.println(); 

		execute( program, configuration );
	}
	
	public static void execute( String program, Configuration configuration )
	{
		Thread t = new Thread( new ExecutionTask( program, configuration ), "Evaluation task" );

		t.setPriority( Thread.MIN_PRIORITY );
		t.start();
		
		try
		{
			t.join( configuration.evaluationTimeoutMilliseconds );
		}
		catch( InterruptedException e )
		{
		}
		
		if ( t.isAlive() )
		{
			t.stop();
			System.out.println( "Timeout exceeded: " + configuration.evaluationTimeoutMilliseconds + "ms" );
		}
	}
	
	static class ExecutionTask implements Runnable
	{
		ExecutionTask( String program, Configuration configuration )
		{
			this.program = program;
			this.configuration = configuration;
		}
		
//		@Override
        public void run()
        {
        	ProgramExecutor executor = new ProgramExecutor( program, configuration );
			System.out.println( executor.getOutput() );
        }
		
		private String program;
		private Configuration configuration;
	}
}
