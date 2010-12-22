/**
 * 
 */
package org.deri.iris.terms.concrete;

import java.net.URI;

import org.deri.iris.api.terms.IConcreteTerm;
import org.deri.iris.api.terms.concrete.IList;
import org.deri.iris.terms.AbstractConcreteTermTest;

public class ListTest extends AbstractConcreteTermTest {

	@Override
	protected IConcreteTerm createBasic() {
		return new org.deri.iris.terms.concrete.List(new IntTerm(2),
				new IntTerm(3));
	}

	@Override
	protected String createBasicString() {
		return "[2,3]";
	}

	@Override
	protected IConcreteTerm createEqual() {
		return new org.deri.iris.terms.concrete.List(new IntTerm(2),
				new IntTerm(3));
	}

	@Override
	protected String createEqualString() {
		return "[2,3]";
	}

	@Override
	protected IConcreteTerm createGreater() {
		return new org.deri.iris.terms.concrete.List(
				new PlainLiteral("3", "de"), new IntTerm(3),
				new org.deri.iris.terms.concrete.List(new IntTerm(4)));
	}

	@Override
	protected String createGreaterString() {
		return "[3@de,3,[4]]";
	}

	@Override
	protected URI getDatatypeIRI() {
		return URI.create(IList.DATATYPE_URI);
	}

	public void testCompareTo2() {
		org.deri.iris.terms.concrete.List list_one = new org.deri.iris.terms.concrete.List(
				new IntTerm(1), new IntTerm(2));
		org.deri.iris.terms.concrete.List list_two = new org.deri.iris.terms.concrete.List(
				new IntTerm(2), new IntTerm(3));
		org.deri.iris.terms.concrete.List list_three = new org.deri.iris.terms.concrete.List(
				new IntTerm(1), new IntTerm(2), new IntTerm(3));

		assertFalse(list_one.equals(list_two));

		assertEquals(-1, list_one.compareTo(list_two));
		assertEquals(1, list_two.compareTo(list_one));

		assertEquals(-1, list_two.compareTo(list_three));
		assertEquals(1, list_three.compareTo(list_two));

		assertEquals(-1, list_one.compareTo(list_three));
		assertEquals(1, list_three.compareTo(list_one));

		assertEquals(0, list_one.compareTo(list_one));
		assertEquals(0, list_two.compareTo(list_two));
		assertEquals(0, list_three.compareTo(list_three));

		org.deri.iris.terms.concrete.List list_short = new org.deri.iris.terms.concrete.List(
				new ShortTerm((short) 1), new ShortTerm((short) 2));

		assertEquals(0, list_one.compareTo(list_short));
	}

	public void testListEquals2() {
		org.deri.iris.terms.concrete.List list_one = new org.deri.iris.terms.concrete.List(
				new IntTerm(1), new IntTerm(2));
		org.deri.iris.terms.concrete.List list_two = new org.deri.iris.terms.concrete.List(
				new IntTerm(2), list_one, new IntTerm(3));
		org.deri.iris.terms.concrete.List list_three = new org.deri.iris.terms.concrete.List(
				new IntTerm(1), new IntTerm(2), new IntTerm(3));
		org.deri.iris.terms.concrete.List list_four = new org.deri.iris.terms.concrete.List(
				new IntTerm(2), list_one, new IntTerm(3));

		assertTrue(list_one.equals(list_one));
		assertTrue(list_two.equals(list_two));
		assertTrue(list_three.equals(list_three));
		assertTrue(list_four.equals(list_four));
		assertTrue(list_two.equals(list_four));

		assertFalse(list_one.equals(list_two));
		assertFalse(list_two.equals(list_one));

		assertFalse(list_three.equals(list_one));
		assertFalse(list_one.equals(list_three));

		assertFalse(list_three.equals(list_two));
		assertFalse(list_two.equals(list_three));

	}

}
