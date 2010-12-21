/*
 * Integrated Rule Inference System (IRIS):
 * An extensible rule inference system for datalog with extensions.
 * 
 * Copyright (C) 2009 Semantic Technology Institute (STI) Innsbruck, 
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
package org.deri.iris.utils;

import java.util.Collection;
import java.util.Set;

import junit.framework.TestCase;

/**
 * Test for EquivalenceRelation.
 * 
 * @author Adrian Marte
 */
public class DisjointSetsTest extends TestCase {

	private static final String element1 = "foobar";
	private static final String element1Equiv1 = "f00b4r";
	private static final String element1Equiv2 = "Foobar";
	private static final String element1Equiv3 = "Foob4r";
	private static final String element1Equiv4 = "fOObAr";

	private static final String element2 = "Test";
	private static final String element2Equiv1 = "t3st";
	private static final String element2Equiv2 = "T3$t";
	private static final String element2Equiv3 = "te$T";
	private static final String element2Equiv4 = "TEST";

	private static final String element3 = "Gugu";
	private static final String element3Equiv1 = "GUGU";

	private static final String invalid = "Gigi";

	private DisjointSets<String> relation;

	public DisjointSetsTest(String name) {
		super(name);
	}

	/*
	 * (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		relation = new DisjointSets<String>();

		relation.putInSameSet(element1, element1Equiv1);
		relation.putInSameSet(element1, element1Equiv2);
		relation.putInSameSet(element1, element1Equiv3);
		relation.putInSameSet(element1, element1Equiv4);

		relation.putInSameSet(element2, element2Equiv1);
		relation.putInSameSet(element2, element2Equiv2);
		relation.putInSameSet(element2, element2Equiv3);
		relation.putInSameSet(element2, element2Equiv4);
	}

	/**
	 * Test method for
	 * {@link org.deri.iris.utils.DisjointSets#add(java.lang.Object)}.
	 */
	public void testAdd() {
		try {
			relation.add(element1);
		} catch (Exception e) {
			fail("An exception occured when adding elements to the relation.");
		}
	}

	/**
	 * Test method for
	 * {@link org.deri.iris.utils.DisjointSets#remove(java.lang.Object)}.
	 */
	public void testRemove() {
		relation.remove(element2);
		
		assertFalse(relation.areInSameSet(element2, element2Equiv1));
		
		relation.remove(element2Equiv1);
		relation.remove(element2Equiv2);
		relation.remove(element2Equiv3);
		relation.remove(element2Equiv4);
		
		assertEquals(1, relation.getNumberOfSets());
	}

	/**
	 * Test method for
	 * {@link org.deri.iris.utils.DisjointSets#areInSameSet(java.lang.Object, java.lang.Object)}
	 * .
	 */
	public void testAreInSameSet() {
		assertTrue(relation.areInSameSet(element1, element1Equiv1));
		assertTrue(relation.areInSameSet(element1, element1Equiv2));
		assertTrue(relation.areInSameSet(element1, element1Equiv3));
		assertTrue(relation.areInSameSet(element1, element1Equiv4));

		assertTrue(relation.areInSameSet(element2, element2Equiv1));
		assertTrue(relation.areInSameSet(element2, element2Equiv2));
		assertTrue(relation.areInSameSet(element2, element2Equiv3));
		assertTrue(relation.areInSameSet(element2, element2Equiv4));

		relation.putInSameSet(element1, element2);
		assertTrue(relation.areInSameSet(element1, element2Equiv2));
		assertTrue(relation.areInSameSet(element2, element1Equiv3));
		
		// Should return false here.
		assertFalse(relation.areInSameSet(element2, element3));

		relation.putInSameSet(element1, element3);
		assertTrue(relation.areInSameSet(element1, element2));
		assertTrue(relation.areInSameSet(element2, element3));

		// Check identity.
		assertTrue(relation.areInSameSet(element1, element1));
		assertTrue(relation.areInSameSet(element2, element2));
		assertTrue(relation.areInSameSet(element3, element3));
	}

	/**
	 * Test method for
	 * {@link org.deri.iris.utils.DisjointSets#find(java.lang.Object)}
	 * .
	 */
	public void testFind() {
		assertEquals(element1Equiv1, relation.find(element1));
		assertEquals(element1Equiv1, relation
				.find(element1Equiv4));

		assertEquals(element2Equiv1, relation.find(element2));
		assertEquals(element2Equiv1, relation
				.find(element2Equiv2));

		assertNull(relation.find(invalid));
	}

	/**
	 * Test method for
	 * {@link org.deri.iris.utils.DisjointSets#putInSameSet(java.lang.Object, java.lang.Object)}
	 * .
	 */
	public void testPutInSameSet() {
		relation.putInSameSet("test1", "test2");
		assertTrue(relation.areInSameSet("test1", "test2"));
	}

	/**
	 * Test method for
	 * {@link org.deri.iris.utils.DisjointSets#getSets()}.
	 */
	public void testGetSets() {
		Collection<Set<String>> classes = relation.getSets();

		int counter = 0;

		for (Set<String> equivalence : classes) {
			if (equivalence.contains(element1)) {
				counter++;
			}

			if (equivalence.contains(element1Equiv1)) {
				counter++;
			}

			if (equivalence.contains(element1Equiv2)) {
				counter++;
			}

			if (equivalence.contains(element1Equiv3)) {
				counter++;
			}

			if (equivalence.contains(element1Equiv4)) {
				counter++;
			}

			if (equivalence.contains(element2)) {
				counter++;
			}

			if (equivalence.contains(element2Equiv1)) {
				counter++;
			}

			if (equivalence.contains(element2Equiv2)) {
				counter++;
			}

			if (equivalence.contains(element2Equiv3)) {
				counter++;
			}

			if (equivalence.contains(element2Equiv4)) {
				counter++;
			}
		}

		assertEquals(
				"Not all elements are contained in the equivalence classses.",
				10, counter);
	}

	/**
	 * Test method for
	 * {@link org.deri.iris.utils.DisjointSets#getSetOf(java.lang.Object)}
	 * .
	 */
	public void testGetSetOf() {
		Set<String> classes = relation.getSetOf(element1);

		assertTrue(classes.contains(element1Equiv1));
		assertTrue(classes.contains(element1Equiv2));
		assertTrue(classes.contains(element1Equiv3));
		assertTrue(classes.contains(element1Equiv4));

		classes = relation.getSetOf(element2);

		assertTrue(classes.contains(element2Equiv1));
		assertTrue(classes.contains(element2Equiv2));
		assertTrue(classes.contains(element2Equiv3));
		assertTrue(classes.contains(element2Equiv4));
	}

	/**
	 * Test method for
	 * {@link org.deri.iris.utils.DisjointSets#getNumberOfSets()}
	 * .
	 */
	public void testGetNumberOfSets() {
		assertEquals(2, relation.getNumberOfSets());

		relation.putInSameSet(element3, element3Equiv1);
		assertEquals(3, relation.getNumberOfSets());

		relation.putInSameSet(element1, element2);
		assertEquals(2, relation.getNumberOfSets());

		relation.putInSameSet(element2Equiv1, element1Equiv1);
		assertEquals(2, relation.getNumberOfSets());

		relation.putInSameSet(element3, element1Equiv1);
		assertEquals(1, relation.getNumberOfSets());
	}

}
