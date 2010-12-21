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

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.deri.iris.Configuration;
import org.deri.iris.KnowledgeBaseFactory;
import org.deri.iris.evaluation.stratifiedbottomup.StratifiedBottomUpEvaluationStrategyFactory;
import org.deri.iris.evaluation.stratifiedbottomup.naive.NaiveEvaluatorFactory;
import org.deri.iris.evaluation.stratifiedbottomup.seminaive.SemiNaiveEvaluatorFactory;
import org.deri.iris.evaluation.wellfounded.WellFoundedEvaluationStrategyFactory;
import org.deri.iris.optimisations.magicsets.MagicSets;
import org.deri.iris.optimisations.rulefilter.RuleFilter;
import org.deri.iris.rules.safety.AugmentingRuleSafetyProcessor;

/**
 * A GUI version of the Demo application.
 */
public class DemoW
{
	public static final int FONT_SIZE = 12;
	public static final String NEW_LINE = System.getProperty( "line.separator" );

	/**
	 * Application entry point.
	 * @param args
	 */
	public static void main( String[] args )
	{
		// Set up the native look and feel
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch(Exception e)
		{
		}

		// Create the main window and show it.
		MainFrame mainFrame = new MainFrame();
		mainFrame.setSize( 800, 600 );
		mainFrame.setVisible( true );
	}
	
	/**
	 * The main application window
	 */
	public static class MainFrame extends JFrame implements ActionListener
	{
		/** The serialisation ID. */
        private static final long serialVersionUID = 1L;

        /**
         * Constructor
         */
		public MainFrame()
		{
			super( "IRIS - new" );

			setup();
		}

		/**
		 * Create all the widgets, lay them out and create listeners.
		 */
		private void setup()
		{
			setLayout( new BorderLayout() );
			
			mProgram.setText( 
				"man('homer').\r\n" +
				"woman('marge').\r\n" +
				"hasSon('homer','bart').\r\n" +
				"isMale(?x) :- man(?x).\r\n" +
				"isFemale(?x) :- woman(?x).\r\n" +
				"isMale(?y) :- hasSon(?x,?y).\r\n" +
				"\r\n" +
				"?-isMale(?x)."
				);
			
			mProgram.setText( "p(h(?X)) :- q(?X)." );
			
			mProgram.setText(	"p(succ(?X), ?Y) :- p(?X,?Z), ?Z+1=?Y, ?Y < 7.\r\n" +
								"p(?X,?Y) :- ?X=1, ?Y=1.\r\n" +
								"?-p(?X,?Y).\r\n" );
			
			mProgram.setText(	
							"p('a'):-.\r\n" +
							"p(?X) :- r(?X), not s(?Y).\r\n" +
							"?- p(?X)." );


			mProgram.setText(
							"p(?X) :- a(?X), d(?X, ?Y)." + NEW_LINE +
							"d(?X, ?Y) :- not s(?X, ?Y), UNIVERSE(?X), UNIVERSE(?Y)." + NEW_LINE +
							"s(?U, ?U) :- UNIVERSE(?U)." + NEW_LINE +
							"a(1)." + NEW_LINE +
							NEW_LINE +
							"?- p(?X)." + NEW_LINE +
							NEW_LINE +
							"UNIVERSE(1)." + NEW_LINE +
							"UNIVERSE(2)." + NEW_LINE
							);
			
			mProgram.setText(
				"p(?x, ?y) :- not q(?x)." + NEW_LINE +
				"?- p(?x, ?y)."
			);
			
			mProgram.setText(
				"p(?x) :- t(?x, ?y, ?z), not p(?y), not p(?z)." + NEW_LINE +
				"p('b') :- not r('a')." + NEW_LINE +
				"t( 'a', 'a', 'b')." + NEW_LINE +
				"t( 'a', 'b', 'a')." + NEW_LINE +
				"?- p(?x)."
			);
			

			mProgram.setText(
				"p(0,0)." + NEW_LINE +
				"p( ?n1, succ( ?x ) ) :- p(?n, ?x), ?n + 1 = ?n1, ?n1 < 10." + NEW_LINE +
				"//?- p(?n, ?x )." + NEW_LINE +
	
				"even( succ( ?x ) ) :- ! even( ?x )." + NEW_LINE +
				"even( 0 )." + NEW_LINE +
				"?- even( ?x )."
			);

			mProgram.setText(
							"p(?x) :- t(?x, ?y, ?z), not p(?y), not p(?z)." + NEW_LINE +
							"p('b') :- not r('a')." + NEW_LINE +
							"t( 'a', 'a', 'b')." + NEW_LINE +
							"t( 'a', 'b', 'a')." + NEW_LINE +
							"?- p(?x)."
						);
			
			mProgram.setText(
				"p(?x) :- q(?x), ! r(?x)." + NEW_LINE +
				"q(?x) :- ! p(?x)." + NEW_LINE +
				"r('a')." + NEW_LINE +
				"?- r(?x)."
			);
			
			mProgram.setText(
							"p(?X,?Y) :- b(?X,?Y)." + NEW_LINE +
							"p(?X,?Y) :- b(?X,?U), p(?U,?Y)." + NEW_LINE +

							"e(?X,?Y) :- g(?X,?Y)." + NEW_LINE +
							"e(?X,?Y) :- g(?X,?U), e(?U,?Y)." + NEW_LINE +

							"a(?X,?Y) :- e(?X,?Y), not p(?X,?Y)." + NEW_LINE +

							"b(1,2)." + NEW_LINE +
							"b(2,1)." + NEW_LINE +
							"g(2,3)." + NEW_LINE +
							"g(3,2)." + NEW_LINE +
							"?- a(2,?Y)."
			);
			
			mProgram.setText(
							"triple(0,0,0,1)." + NEW_LINE +
							"triple(?n, ?x, ?y, ?z) :- triple(?n1, ?x1, ?y1, ?z1), ?n1 + 1 = ?n, ?n/100=?x, ?n%100=?y2, ?y2/10=?y, ?n%10=?zz, ?zz+1=?z, ?n < 1000." + NEW_LINE +
							NEW_LINE +
							"// get all those triples where a % n = b % n (definition of congruent)" + NEW_LINE +
							"congruent( ?a, ?b, ?n ) :- triple(?k, ?a, ?b, ?n ), ?a % ?n = ?amodn, ?b % ?n = ?bmodn, ?amodn = ?bmodn." + NEW_LINE +
							NEW_LINE +
							"// Proove that if a1 congruent a2 mod n and b1 congruent b2 mod n, then a1b1 congruent a2b2 mod n" + NEW_LINE +
							"mul( ?a1b1, ?a2b2, ?n ) :- congruent( ?a1, ?a2, ?n ), congruent( ?b1, ?b2, ?n ), ?a1*?b1=?a1b1, ?a2*?b2=?a2b2." + NEW_LINE +
							NEW_LINE +
							"// The multiplied triples where the congruency rule does not hold." + NEW_LINE +
							"exceptions_to_rule( ?x,?y,?n ) :- mul( ?x,?y,?n), ?x % ?n = ?xmodn, ?y % ?n = ?ymodn, ?xmodn != ?ymodn." + NEW_LINE +
							NEW_LINE +
							"// Ths should be empty if the congruency rule is correct and the reasoner behaves correctly." + NEW_LINE +
							"?-exceptions_to_rule( ?a1b1,?a2b2,?n )." + NEW_LINE
											);
			
			// Unsafe rule example
			mProgram.setText(
							"p( ?x ) :- a( ?x ), diff( ?x, ?y )." + NEW_LINE +
							"diff( ?x, ?y ) :- not same( ?x, ?y )." + NEW_LINE +
							"same( ?x, ?x ) :- ." + NEW_LINE +
							"a(1)." + NEW_LINE +
				
							"?- p(?x)."
						);
			
						
			// Unstratified example.
			mProgram.setText(
				"republican(?x) :- like_guns(?x), not hippy(?x)." + NEW_LINE +
				"democrat(?x) :- like_hippies(?x), not republican(?x)." + NEW_LINE +
				"hippy(?x) :- like_flowers(?x), not republican(?x), not democrat(?x)." + NEW_LINE +
				NEW_LINE +
				"like_flowers('a')." + NEW_LINE +
				"like_hippies('b')." + NEW_LINE +
				"like_guns('c')." + NEW_LINE +
				NEW_LINE +
				"?- republican(?x)." + NEW_LINE +
				"?- democrat(?x)." + NEW_LINE +
				"?- hippy(?x)."
			);
			
			mProgram.setText(
							"some_calc(?X,?Z) :- MULTIPLY(?X,?X,?Y), ADD(?Y,-1, ?Z). " + NEW_LINE +
							"?-some_calc(5,?Z)."
			);

			mProgram.setText(
							"p( _datetime( 1999, 12, 31, 23, 59, 59 ), _datetime( 1998, 11, 30, 22, 58, 58 ) )." + NEW_LINE +
							"p( _datetime( 2000, 1, 1, 0, 0, 0 ), _datetime( 1999, 12, 31, 23, 59, 0 ) )." + NEW_LINE +
							NEW_LINE +
							"?- p(?x,?y), ?x-?y=?z."
			);
							
			mRun.addActionListener( this );

			mAbort.addActionListener( this );
			mAbort.setEnabled( false );

			JScrollPane programScroller = new JScrollPane( mProgram );
			JScrollPane outputScroller = new JScrollPane( mOutput );
			
			Font f = new Font( "courier", Font.PLAIN, FONT_SIZE );
			mProgram.setFont( f );
			mOutput.setFont( f );

			JSplitPane mainSplitter = new JSplitPane( JSplitPane.VERTICAL_SPLIT, false, programScroller, outputScroller );

			getContentPane().add( mainSplitter, BorderLayout.CENTER );
			
			JPanel panel = new JPanel();
			panel.add( mStrategy );
			panel.add( mUnsafeRules );
			panel.add( mOptimise );
			panel.add( mRun );
			panel.add( mAbort );

			getContentPane().add( panel, BorderLayout.SOUTH );

			// Can't seem to make this happen before showinG, even with:
//			 mainSplitter.putClientProperty( JSplitPane.RESIZE_WEIGHT_PROPERTY, "0.5" );
//			mainSplitter.setDividerLocation( 0.5 );
			
			addWindowListener(
							new WindowAdapter()
							{
								public void windowClosing( WindowEvent e )
								{
									System.exit( 0 );
								}
							}
						);
			
		}

		private final JTextArea mProgram = new JTextArea();
		private final JTextArea mOutput = new JTextArea();
		
//		private final JComboBox mEvaluator = new JComboBox( new String[] { "Naive", "Semi-naive" } );
		private final JComboBox mStrategy = new JComboBox( new String[] { "Stratified (Semi-naive)", "Stratified (Naive)", "Well-founded" } );
		private final JCheckBox mUnsafeRules = new JCheckBox( "Unsafe-rules", false );
		private final JComboBox mOptimise = new JComboBox( new String[] { "none", "Magic Sets" } );
		
		private final JButton mRun = new JButton( "Evaluate" );
		private final JButton mAbort = new JButton( "Abort" );
		
		Thread mExecutionThread;
		
		public void actionPerformed( ActionEvent e )
        {
	        if( e.getSource() == mRun )
	        {
	        	run();
	        }
	        else if( e.getSource() == mAbort )
	        {
	        	abort();
	        }
        }

		/**
		 * Called when evaluation has finished.
		 * @param output The evaluation output 
		 */
		synchronized void setOutput( String output )
		{
			mRun.setEnabled( true );
			mAbort.setEnabled( false );

			mOutput.setText( output );
		}
		
		/**
		 * Notifier class that 'hops' the output from the evaluation thread to the UI thread.
		 */
		class NotifyOutput implements Runnable
		{
			NotifyOutput( String output )
			{
				mOutput = output;
			}
			
			public void run()
            {
	            setOutput( mOutput );
            }
			
			final String mOutput;
		}
		
		/**
		 * Starts the evaluation.
		 */
		synchronized void run()
		{
			mOutput.setText( "" );

			mRun.setEnabled( false );
			mAbort.setEnabled( true );
			
			String program = mProgram.getText();
			
			Configuration config = KnowledgeBaseFactory.getDefaultConfiguration();
			
			if( mUnsafeRules.isSelected() )
				config.ruleSafetyProcessor = new AugmentingRuleSafetyProcessor();
			
			switch( mStrategy.getSelectedIndex() )
			{
			default:
			case 0:
				config.evaluationStrategyFactory = new StratifiedBottomUpEvaluationStrategyFactory( new SemiNaiveEvaluatorFactory() );
				break;
				
			case 1:
				config.evaluationStrategyFactory = new StratifiedBottomUpEvaluationStrategyFactory( new NaiveEvaluatorFactory() );
				break;
				
			case 2:
				config.evaluationStrategyFactory = new WellFoundedEvaluationStrategyFactory();
				config.stratifiers.clear();
				break;
			}
			
			switch( mOptimise.getSelectedIndex() )
			{
			case 0:
				break;
				
			case 1:
				config.programOptmimisers.add( new RuleFilter() );
				config.programOptmimisers.add( new MagicSets() );
				break;
			}

			mExecutionThread = new Thread( new ExecutionTask( program, config ), "Evaluation task" );

			mExecutionThread.setPriority( Thread.MIN_PRIORITY );
			mExecutionThread.start();
		}
		
		/**
		 * Aborts the evaluation.
		 */
		synchronized void abort()
		{
			mRun.setEnabled( true );
			mAbort.setEnabled( false );

			// Not very nice, but hey, that's life.
			mExecutionThread.stop();
		}
		
		/**
		 * Runnable task for performing the evaluation.
		 */
		class ExecutionTask implements Runnable
		{
			ExecutionTask( String program, Configuration configuration )
			{
				this.program = program;
				this.configuration = configuration;
			}
			
//			@Override
	        public void run()
	        {
	        	ProgramExecutor executor = new ProgramExecutor( program, configuration );
				SwingUtilities.invokeLater( new NotifyOutput( executor.getOutput() ) );
	        }
			
			private final String program;
			private final Configuration configuration;
		}
	}
}
