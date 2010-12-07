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
package eu.larkc.iris.rules;

import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.deri.iris.EvaluationException;
import org.deri.iris.api.basics.IRule;
import org.deri.iris.compiler.Parser;
import org.deri.iris.compiler.ParserException;

/**
 * Tests the {@link NonOptimizingRecursiveRulePreProcessor}
 * 
 * @author fisf, Florian Fischer
 */
public class NonOptimizingRecursiveRulePreProcessorTest extends TestCase {
	
	final String recursiveRule = "p(?X) :- a(?Y), q(?Y, ?Z), p(?Z).";
	final String nonRecursive = "p(?X, 1) :- a(?Y), q(?Y, ?X).";
	final String nonRecursive1 = "q(?X, 1) :- k(?Y), p(?Y, ?X).";
	final String nonRecursive2 = "p(?X) :- a(?Y), q(?Y, ?X)."; 
	final String totallyNotRecursive = "foo(?X) :- bar(?X), micky(?X).";
		
	private final NonOptimizingRecursiveRulePreProcessor processor = new NonOptimizingRecursiveRulePreProcessor();

	public static Test suite() {
		return new TestSuite(NonOptimizingRecursiveRulePreProcessorTest.class, NonOptimizingRecursiveRulePreProcessorTest.class.getSimpleName());
	}

	/**
	 * Tests basic process method.
	 * @throws EvaluationException 
	 */
	public void testBasicProcess() throws ParserException, EvaluationException {
						
		List<IRule> rules = parseRules(recursiveRule);		
		processor.process(rules);		
		
		rules = parseRules(nonRecursive);
		processor.process(rules);	
	}
	
	public void testSingleRuleRecursionDetection() throws ParserException, EvaluationException {
		List<IRule> rules = parseRules(recursiveRule);		
		processor.process(rules);	
		assertTrue(processor.getIsRecursive());
		
		rules = parseRules(nonRecursive);
		processor.process(rules);
		assertFalse(processor.getIsRecursive());		
	}
	
	public void testCombinedRuleRecursionDetection() throws ParserException, EvaluationException {
		String program = recursiveRule + "\n" + nonRecursive;
		
		List<IRule> rules = parseRules(program);		
		processor.process(rules);	
		assertTrue(processor.getIsRecursive());			
		
		program = nonRecursive + "\n" + nonRecursive;
		rules = parseRules(program);		
		processor.process(rules);	
		assertFalse(processor.getIsRecursive());			
	}
	
	public void testMultipleRuleRecursionDetection()  throws ParserException, EvaluationException {
		List<IRule> rules = parseRules(nonRecursive);		
		processor.process(rules);	
		assertFalse(processor.getIsRecursive());	
		
		rules = parseRules(nonRecursive1);		
		processor.process(rules);	
		assertFalse(processor.getIsRecursive());	
		
		String combinedRecursive = nonRecursive + "\n" + nonRecursive1;
		
		rules = parseRules(combinedRecursive);		
		processor.process(rules);	
		assertTrue(processor.getIsRecursive());	
	}
	
	public void testMultipleRuleRecursionDetectionWithDifferentArity()  throws ParserException, EvaluationException {
		
		String combinedRecursive = nonRecursive + "\n" + nonRecursive2;
		
		List<IRule> rules = parseRules(combinedRecursive);		
		processor.process(rules);	
		//FIXME (fisf) this intentionally fails for now. 
		//Building the graph uses equals() of IPredicate internally
		//This means that p(x) and p(x,y) are not treated as the same vertex in the graph / predicate
		//Needs some clarfication if this is the desired behavior (i.e. if evaluation handles it in the same way)
		//see _addRule(..) in PredicateGraph
		assertTrue(processor.getIsRecursive());
	}
	
	public void testSeparation()  throws ParserException, EvaluationException {
		
		String combinedRecursive = nonRecursive + "\n" + nonRecursive1 + "\n" + recursiveRule + "\n" + totallyNotRecursive;
		
		List<IRule> rules = parseRules(combinedRecursive);		
		processor.process(rules);	
		assertTrue(processor.getIsRecursive());		
		
		List<IRule> rule = processor.getNonrecursive();
		assertEquals(rule.size(), 1);
		
		List<IRule> rule2 = processor.getRecursive();
		assertEquals(rule2.size(), 3);		
	}
	
	
	

	/**
	 * Parses a program string
	 * 
	 * @param the program to parse
	 * @return parsed rules
	 */
	private static List<IRule> parseRules(final String program) throws ParserException {
		assert program != null: "The program must not be null";

		final Parser parser = new Parser();
		parser.parse(program);

		assert !parser.getRules().isEmpty(): "There must be at least one rule parsed";

		return parser.getRules();
	}
}
