/**
 * 
 */
package eu.larkc.iris.functional.features;


/** 
 * Tests basic features of predicate and propositional logic that can be expressed in Datalog.
 * 
 * @author Florian Fischer, fisf, 09-Dec-2010
 */
public class BasicLangFeaturesTest extends LangFeaturesTest {

	public BasicLangFeaturesTest(String string) {
		super(string);
	}
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	/**
	 * Test that a simple logic program of predicate logic.
	 * 
	 * @throws Exception 
	 */
	public void testPredicateLogic() throws Exception
	{
		
		program =
			"p( ?X, ?Y ) :- q( ?X, ?Y ), r( ?Y, ?Z ), s( ?X, ?Z )." +
			"?- p(?X, ?Y).";
		parser.parse(program);
		rules = createRules();
		
		compile();
	}
	
	/**
	 * Check that the compilation works with predicates with the same name, but different arities.
	 * @throws Exception 
	 */
	public void testPredicateWithSeveralArities() throws Exception
	{
		 program =
			"p( ?X, ?Y ) :- q( ?X, ?Y ), r( ?Y, ?Z ), s( ?X, ?Z )." +
			"p( ?X) :- q( ?X, ?Y ), r( ?Y, ?Z, ?K ), s( ?X).";
		
		parser.parse(program);
		rules = createRules();

		compile();
	}

	/**
	 * Test that datalog programs containing only propositional terms
	 * are correctly translated.
	 */
	public void testPropositionalLogic() throws Exception
	{
		program =
			"p :- a, b, c." +
			"?- p.";
		parser.parse(program);
		rules = createRules();

		compile();
	}	
}
