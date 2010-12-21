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
package org.deri.iris.performance;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.deri.iris.EvaluationException;
import org.deri.iris.KnowledgeBaseFactory;
import org.deri.iris.api.IKnowledgeBase;
import org.deri.iris.api.basics.IQuery;
import org.deri.iris.compiler.Parser;
import org.deri.iris.compiler.ParserException;

/**
 * <pre>
 * Rudimentary performance harness that works like this:
 * 1. The programs are loaded from the jar file, parsed
 *    and a knowledge base for each one is created.
 * 2. With 1 second gaps, each program is run once.
 * 3. Again, with 1 second gaps, the programs are run again,
 *    but this time they are timed.
 * 4. A report is output to stdout.
 * </pre>
 */
public class PerformanceHarness
{
	/**
	 * Entry point.
	 * @param args If any argument is 'show' then the contents of
	 * the test programs is added to the report.
	 */
	public static void main( String[] args )
	{
		boolean showPrograms = false;
		
		for( String arg : args )
		{
			if( arg.equalsIgnoreCase( "show" ) )
				showPrograms = true;
		}
		
		try
		{
			new PerformanceHarness( showPrograms );
		}
		catch( Exception e )
		{
			System.out.println( "Performance testing failed: " + e.getMessage() );
		}
	}
	
	/**
	 * A logic program. This class loads the program, parses it
	 * and initialises a knowledge base with it.
	 */
	class Program
	{
		/**
		 * Constructor.
		 * Load the file identified with 'filename',
		 * initialise a knowledge base object and
		 * store the query.
		 * @param filename The file containing the logic program.
		 * @throws IOException
		 * @throws ParserException
		 * @throws EvaluationException 
		 */
		Program( String filename ) throws IOException, ParserException, EvaluationException
		{
			mParser = new Parser();
			
			InputStream inputStream = getClass().getClassLoader().getResourceAsStream(filename);
			
			Reader r = new InputStreamReader(inputStream);
			
			StringBuilder builder = new StringBuilder();
			
			int ch = -1;
			while( ( ch = r.read() ) >= 0 )
			{
				builder.append( (char) ch );
			}
			mProgram = builder.toString();
			
			mParser.parse( mProgram );
		}
		
		/**
		 * Execute the query against the knowledge base and return the
		 * execution time in milliseconds.
		 * @return The time in milliseconds.
		 * @throws EvaluationException If the evaluation fails for any reason.
		 */
		long execute() throws EvaluationException
		{
			List<IQuery> queries = mParser.getQueries();
			
			if( queries.size() != 1 )
				throw new RuntimeException( "The input program must contain exactly one query." );
			
			IQuery query = queries.get( 0 );
			long elapsedTime = -System.currentTimeMillis();
			final IKnowledgeBase mKB = KnowledgeBaseFactory.createKnowledgeBase( mParser.getFacts(), mParser.getRules() );
			mKB.execute( query );
			elapsedTime += System.currentTimeMillis();

			return elapsedTime;
		}
		
		/**
		 * Get the human readable program.
		 * @return The logic program.
		 */
		String getProgram()
		{
			return mProgram;
		}
		
		/** The program in human-readable form. */
		private final String mProgram;
		
		private final Parser mParser;
		
		/** The knowledge base. */
//		private final IKnowledgeBase mKB;
		
		/** The query to run against the knowledge base. */
//		private final IQuery mQuery;
	}
	
	/**
	 * Constructor.
	 * All the work is done here!
	 * Create 'Program' objects for each logic program and run them once.
	 * Then with gaps (to let the garbage collector catch up) run them again,
	 * but this time record the execution times.
	 * Output the report to stdout.
	 * @param showPrograms true, to add the contents of the test programs to the report.
	 * @throws IOException
	 * @throws ParserException
	 * @throws EvaluationException
	 */
	public PerformanceHarness( boolean showPrograms ) throws IOException, ParserException, EvaluationException
	{
		// Ensure all the class files are loaded, memory allocated etc
		for( String filename : mProgramFilenames )
			mPrograms.add( new Program( filename ) );
		
		pause1();		
		for( Program program : mPrograms )
		{
			program.execute();
			pause1();
		}
		
		// Clear everything out
		mPrograms.clear();
		pause1();

		// Reload
		for( String filename : mProgramFilenames )
			mPrograms.add( new Program( filename ) );

		// Prepare the report header
		StringBuilder output = new StringBuilder();
		
		output.append( "IRIS Performance Harness" ).append( NEW_LINE );
		output.append( "========================" ).append( NEW_LINE );
		InetAddress localHost = InetAddress.getLocalHost();
		output.append( "At time:    " ).append( new Date() ).append( NEW_LINE );
		output.append( "On machine: " ).append( localHost.getHostName() ).append( " (" ).append( localHost ).append( ")" ).append( NEW_LINE );
		
		if( showPrograms )
		{
			for( int p = 0; p < mPrograms.size(); ++p )
			{
				pause(1);
				output.append( THIN_LINE );
				output.append( "Program " ).append( p ).append( " is:" ).append( NEW_LINE );
				output.append( mPrograms.get( p ).getProgram() ).append( NEW_LINE ).append( NEW_LINE );
			}
		}
		
		output.append( THICK_LINE );

		pause1();		
		for( int p = 0; p < mPrograms.size(); ++p )
		{
			output.append( "Program " ).append( p ).append( ": " ).append( mPrograms.get( p ).execute() ).append( NEW_LINE );
			pause1();
		}
		
		System.out.println( output.toString() );
	}
	
	/**
	 * Helper. Pause the calling thread for 1 second.
	 */
	private static void pause1()
	{
		pause( 1000 );
	}

	/**
	 * Pause the calling thread for the given time period.
	 * @param milliseconds The length of time to suspend the thread in milliseconds.
	 */
	private static void pause( int milliseconds )
	{
		try
		{
			Thread.sleep( milliseconds );
		}
		catch( InterruptedException e )
		{
		}
	}
	
	/** The list of programs. */
	private final List<Program> mPrograms = new ArrayList<Program>();
	
	/** Useful constant. */
	private static String NEW_LINE = "\r\n";

	/** Useful constant. */
	private static String THICK_LINE = "============================================================" + NEW_LINE;

	/** Useful constant. */
	private static String THIN_LINE = "============================================================" + NEW_LINE;

	/** The array of logic program files to load. */
	private static String[] mProgramFilenames =
	{
		"cartesian_product.txt",
		"local_stratification.txt",
		"multiplicative_congruent.txt",
		"transitive_closure.txt",
		"cartesian_product_with_negation.txt",
	};
}
