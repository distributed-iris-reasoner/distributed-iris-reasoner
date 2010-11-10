/**
 * 
 */
package eu.larkc.iris.functional;

import org.deri.iris.functional.Helper;

import junit.framework.TestCase;

/**
 * 
 * @history Oct 26, 2010, fisf, creation
 * @author florian
 */
public class DistributedBottumUpEvaluationStrategyTest extends TestCase {

	/**
	 * Test that a simple logic program of predicate logic is correctly
	 * evaluated.
	 * @throws Exception 
	 */
	public void testPredicateLogic() throws Exception
	{
		String program =
			"p( ?X, ?Y ) :- q( ?X, ?Y ), r( ?Y, ?Z ), s( ?X, ?Z )." +
			"q( 1, 2 )." +
			"r( 2, 3 )." +
			"s( 1, 3 )." +
			"?- p(?X, ?Y).";
		
		String expectedResults = "p( 1, 2 ).";
		Helper.evaluateWithAllStrategies( program, expectedResults );
	}
	
	/**
	 * Check that IRIS copes with predicates with the same name, but different arities.
	 * @throws Exception 
	 */
	public void testPredicateWithSeveralArities() throws Exception
	{
		String facts =
			"p(8,9,10,11)." +
			"p(12,13,14,15)." +
			"p(5,6,7)." +
			"p(6,6,8)." +
			"p(3,4)." +
			"p(1)." +
			"p(2)." +
			"p.";
		
		Helper.evaluateWithAllStrategies( facts + "?-p(?x,?y,?z,?a).", "p(8,9,10,11).p(12,13,14,15)." );
		
		Helper.evaluateWithAllStrategies( facts + "?-p(?x,?y,?z).", "p(5,6,7).p(6,6,8)." );
		Helper.evaluateWithAllStrategies( facts + "?-p(?x,?y).", "p(3,4)." );
		Helper.evaluateWithAllStrategies( facts + "?- p(?x).", "p(1).p(2)." );
		Helper.evaluateWithAllStrategies( facts + "?- p.", "p." );
	}

}
