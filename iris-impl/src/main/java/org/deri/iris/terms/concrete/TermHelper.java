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
package org.deri.iris.terms.concrete;

/**
 * <p>
 * Some helper methods for some operations on terms.
 * </p>
 * <p>
 * $Id: TermHelper.java,v 1.3 2007-10-09 20:29:38 bazbishop237 Exp $
 * </p>
 * 
 * @author richi
 * @version $Revision: 1.3 $
 * @date $Date: 2007-10-09 20:29:38 $
 */
class TermHelper {
	private TermHelper() {
		// prevent subclassing
	}

	/**
	 * Returns the double value from a <code>INumericTerm</code> <b>This
	 * method assumes that only numbers are stored in <code>INumericTerm</code>.</b>
	 * 
	 * @param n
	 *            the term
	 * @return the double value
	 * @throws NullPointerException
	 *             if the term is null
	 * @see Number
	 */
	/*static double getDouble(final INumericTerm n) {
		if (n == null) {
			throw new NullPointerException("The term must not be null");
		}
		// TODO: maybe instance check for Number
		return ((Number) n.getValue()).doubleValue();
	}*/

	/**
	 * Returns the float value from a <code>INumericTerm</code> <b>This method
	 * assumes that only numbers are stored in <code>INumericTerm</code>.</b>
	 * 
	 * @param n
	 *            the term
	 * @return the float value
	 * @throws NullPointerException
	 *             if the term is null
	 * @see Number
	 */
	/*static float getFloat(final INumericTerm n) {
		if (n == null) {
			throw new NullPointerException("The term must not be null");
		}
		// TODO: maybe instance check for Number
		return ((Number) n.getValue()).floatValue();
	}*/

	/**
	 * Returns the int value from a <code>INumericTerm</code> <b>This method
	 * assumes that only numbers are stored in <code>INumericTerm</code>.</b>
	 * 
	 * @param n
	 *            the term
	 * @return the int value
	 * @throws NullPointerException
	 *             if the term is null
	 * @see Number
	 */
	/*static int getInt(final INumericTerm n) {
		if (n == null) {
			throw new NullPointerException("The term must not be null");
		}
		return (int) getDouble(n);
	}*/
}
