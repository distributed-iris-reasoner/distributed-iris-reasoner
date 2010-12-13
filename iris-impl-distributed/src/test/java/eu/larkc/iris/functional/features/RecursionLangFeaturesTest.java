/**
 * 
 */
package eu.larkc.iris.functional.features;

/**
 * Tests for various forms of recursion in Datalog.
 * 
 * @author Florian Fischer, fisf, 09-Dec-2010
 */
public class RecursionLangFeaturesTest extends LangFeaturesTest {

	public RecursionLangFeaturesTest(String string) {
		super(string);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	

	/**
	 * Test for recursive rules.
	 */
	public void testRecursiveRules3() throws Exception {
		program = "rsg(?X, ?Y) :- up(?X, ?W), rsg(?Q, ?W), down(?Q, ?Y)."
				+ "rsg(?X, ?Y) :- flat(?X, ?Y)." + "?- rsg(?X, ?Y).";

		parser.parse(program);
		rules = createRules();

		compile();
	}

	/**
	 * Same as testRecursiveRules4, but querying s(?X, ?Y) instead of s(1, ?Y).
	 * 
	 * @see testRecursiveRules4
	 */
	public void testRecursiveRules4b() throws Exception {
		program = "s(?X, ?Y) :- s(?X, ?Z), r(?Z, ?Y)."
				+ "s(?X, ?Y) :- r(?X, ?Y)." + "?- s(?X, ?Y).";
		parser.parse(program);
		rules = createRules();

		compile();
	}

	/**
	 * Test for rules with a cyclic dependency.
	 * 
	 * @throws Exception
	 *             If something goes very wrong.
	 */
	public void testRecursionBetweenRules() throws Exception {
		program = "path(?X, ?Y) :- edge(?X, ?Y)."
				+ "edge(?X, ?Y) :- path(?X, ?Y)."
				+ "path(?X, ?Y) :- edge(?X, ?Z), path(?Z, ?Y)."
				+ "?- path(?X, ?Y).";
		parser.parse(program);
		rules = createRules();

		compile();
	}
}
