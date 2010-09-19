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
package org.deri.iris;

import java.lang.reflect.InvocationTargetException;

import junit.framework.Assert;

/**
 * <p>
 * Contains various tests for methods common to many classes. The results will
 * be checked with junit asserts.
 * </p>
 * <p>
 * $Id: ObjectTests.java,v 1.1 2007-07-17 10:12:55 poettler_ric Exp $
 * </p>
 * @author Richard Pöttler (richard dot poettler at deri dot org)
 * @version $Revision: 1.1 $
 */
public final class ObjectTests {

	private ObjectTests() {
		// prevent sublassing
	}

	/**
	 * Tests the <code>equals</code> method.
	 * 
	 * @param e0
	 *            a simple test object
	 * @param e1
	 *            test object which must be equal to e0
	 * @param ue0
	 *            test object which must be unequal to e0
	 * @throws NullPointerException
	 *             if any of the objects is <code>null</code>
	 */
	public static void runTestEquals(final Object e0, final Object e1,
			final Object ue0) {
		if ((e0 == null) || (e1 == null) || (ue0 == null)) {
			throw new NullPointerException("The objects must not be null");
		}

		Assert.assertEquals("A object must be equal to itself", e0, e0);
		Assert.assertEquals("The objects are equal", e0, e1);
		Assert.assertEquals("The objects are equal", e1, e0);
		Assert.assertFalse("The objects are unequal", e0.equals(ue0));
		Assert.assertFalse("The object must be unequal to null", e0
				.equals(null));
	}

	/**
	 * Tests the <code>clone</code> method.
	 * 
	 * @param o
	 *            the object which to try to clone
	 * @throws NullPointerException
	 *             if the object is <code>null</code>
	 */
	public static void runTestClone(final Object o) {
		if (o == null) {
			throw new NullPointerException("The object must not be null");
		}
		Assert.assertNotSame("Clone must not return the same object reference",
				o, runClone(o));
		Assert.assertEquals("Cloned objects must have the same classes", o
				.getClass(), runClone(o).getClass());
		Assert.assertEquals("Cloned objects must be equal", o, runClone(o));
	}

	/**
	 * Tests the <code>compareTo</code> method defined by the
	 * <code>Comparable</code> interface.
	 * 
	 * @param <T>
	 *            the type must extend <code>Comparable</code>
	 * @param basic
	 *            the basic value
	 * @param equal
	 *            a value which is equal to the basic one
	 * @param more
	 *            a value which is bigger than the basic one
	 * @param evenMore
	 *            a vale which is bigger than more
	 * @throws NullPointerException
	 *             if any of the objects is <code>null</code>
	 * @see Comparable
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Comparable> void runTestCompareTo(final T basic,
			final T equal, final T more, final T evenMore) {
		if ((basic == null) || (equal == null) || (more == null)
				|| (evenMore == null)) {
			throw new NullPointerException("The objects must not be null");
		}
		Assert.assertTrue("Something wrong with compareTo (" + basic
				+ " should be smaller than " + more + ")", basic
				.compareTo(more) < 0);
		Assert.assertTrue("Something wrong with compareTo (" + more
				+ " should be smaller than " + evenMore + ")", more
				.compareTo(evenMore) < 0);
		Assert.assertTrue("Something wrong with compareTo (" + basic
				+ " should be smaller than " + evenMore + ")", basic
				.compareTo(evenMore) < 0);

		Assert.assertTrue("Something wrong with compareTo (" + basic
				+ " should be equal to " + equal + ")",
				basic.compareTo(equal) == 0);

		Assert.assertTrue("Something wrong with compareTo (" + evenMore
				+ " should be greater than " + more + ")", evenMore
				.compareTo(more) > 0);
		Assert.assertTrue("Something wrong with compareTo (" + more
				+ " should be greater than " + basic + ")", more
				.compareTo(basic) > 0);
		Assert.assertTrue("Something wrong with compareTo (" + evenMore
				+ " should be greater than " + basic + ")", evenMore
				.compareTo(basic) > 0);
	}

	/**
	 * Tests the <code>compareTo</code> method defined by the
	 * <code>Comparable</code> interface.
	 * 
	 * @param <T>
	 *            the type must extend <code>Comparable</code>
	 * @param basic
	 *            the basic value
	 * @param equal
	 *            a value which is equal to the basic one
	 * @param more
	 *            a value which is bigger than the basic one
	 * @throws NullPointerException
	 *             if any of the objects is <code>null</code>
	 * @see Comparable
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Comparable> void runTestCompareTo(final T basic,
			final T equal, final T more) {
		if ((basic == null) || (equal == null) || (more == null)) {
			throw new NullPointerException("The objects must not be null");
		}
		Assert.assertTrue("Something wrong with compareTo", basic
				.compareTo(more) < 0);

		Assert.assertTrue("Something wrong with compareTo", basic
				.compareTo(equal) == 0);

		Assert.assertTrue("Something wrong with compareTo", more
				.compareTo(basic) > 0);
	}

	public static void runTestHashCode(final Object basic, final Object equal) {
		Assert.assertEquals(
				"Two equal object should produce the same hashCode", basic
						.hashCode(), equal.hashCode());
	}

	/**
	 * Helpermethod to clone an object, because the Object.clone() method is
	 * protected. This Method clones a object using reflection.
	 * 
	 * @param o
	 *            the object to clone
	 * @return the clone
	 * @throws NullPointerException
	 *             if the given object is <code>null</code>
	 */
	private static Object runClone(final Object o) {
		if (o == null) {
			throw new NullPointerException("The object must not be null");
		}
		Object clone = null;
		try {
			clone = o.getClass().getMethod("clone", (Class[]) null).invoke(o,
					(Object[]) null);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		return clone;
	}

}
