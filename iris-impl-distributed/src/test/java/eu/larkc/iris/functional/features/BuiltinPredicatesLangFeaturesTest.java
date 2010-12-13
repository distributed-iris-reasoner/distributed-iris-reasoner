/**
 * 
 */
package eu.larkc.iris.functional.features;

/**
 * The following rules are expected to fail. They depend on the availability of
 * an equality predicate / built-in. This is an optional feature assigned to STI
 * right now where a mapping of IRIS / Datalog built-ins might be implementation
 * as a Bachelor thesis.
 * 
 * Also they are not complete and need to be augmented when the implementation of built-ins / datatypes is done.
 * 
 * @author Florian Fischer, fisf, 09-Dec-2010
 */
public class BuiltinPredicatesLangFeaturesTest extends LangFeaturesTest {

	public BuiltinPredicatesLangFeaturesTest(String string) {
		super(string);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	public void testIncompatibaleAssignments() throws Exception {
		program = "p(?X) :- ?X = 1, ?X = 2." + "?-p(?X).";
		parser.parse(program);
		rules = createRules();

		compile();
	}

	public void testCompatibaleAssignments() throws Exception {
		program = "p(?X) :- ?X = 2, ?X = 2." + "?-p(?X).";
		parser.parse(program);
		rules = createRules();

		compile();
	}

	/**
	 * Test lots of sequences of built-ins and ordinary predicates to check for
	 * errors in relational algebra expression evaluation.
	 * 
	 * @throws Exception
	 */
	public void testMultipleOrdinaryBuiltinSequences() throws Exception {
		program = "p1(?X) :- ?X = 1." + "p2(?X) :- ?X = 1, r(?X)."
				+ "p3(?X) :- r(?X), ?X = 1." + "p4(?X) :- ?X = 1, ?X = 2."
				+ "p5(?X) :- ?X = 2, ?X = 2.";
		parser.parse(program);
		rules = createRules();

		compile();
	}

	/**
	 * Test for recursion and built-in predicates.
	 * 
	 */
	public void testRecursiveRules() throws Exception {
		program = "sibling(?X,?Y) :- parent(?Z,?X), parent(?Z,?Y), ?X != ?Y."
				+ "cousin(?X,?Y) :- parent(?XP,?X), parent(?YP,?Y), sibling(?XP,?YP).\n"
				+ "cousin(?X,?Y) :- parent(?XP,?X), parent(?YP,?Y), cousin(?XP,?YP).\n"
				+

				"?- cousin(?X,?Y).\n";

		parser.parse(program);
		rules = createRules();

		compile();
	}

	/**
	 * Test for recursive rules.
	 */
	public void testRecursiveRules2() throws Exception {
		program = "sibling(?X,?Y) :- parent(?X, ?Z), parent(?Y, ?Z), ?X != ?Y."
				+ "cousin(?X,?Y)  :- parent(?X, ?Xp), parent(?Y, ?Yp), sibling(?Xp,?Yp)."
				+ "cousin(?X,?Y)  :- parent(?X, ?Xp), parent(?Y, ?Yp), cousin(?Xp,?Yp)."
				+

				"related(?X,?Y) :- sibling(?X, ?Y)."
				+ "related(?X,?Y) :- related(?X, ?Z), parent(?Y, ?Z)."
				+ "related(?X,?Y) :- related(?Z, ?Y), parent(?X, ?Z).";

		parser.parse(program);
		rules = createRules();

		compile();
	}
}
