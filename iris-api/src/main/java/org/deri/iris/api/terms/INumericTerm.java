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
package org.deri.iris.api.terms;

import java.math.BigDecimal;

/**
 * <p>
 * An interface for representing a numeric term. A numeric term is a constant
 * term which represents a number.
 * </p>
 */
public interface INumericTerm extends IConcreteTerm {

	/**
	 * Returns the value of this numeric term represented as a BigDecimal.
	 * 
	 * @return The BigDecimal representing the value of this numeric term, or
	 *         <code>null</code> if this term represents "NaN", positive
	 *         infinity or negative infinity.
	 */
	public BigDecimal getValue();

	/**
	 * Returns <code>true</code> if this numeric term represents a "NaN" value,
	 * <code>false</code> otherwise.
	 * 
	 * @return <code>true</code> if this numeric term represents a "NaN" value,
	 *         <code>false</code> otherwise
	 */
	public boolean isNotANumber();

	/**
	 * Returns <code>true</code> if this numeric term represents positive
	 * infinity, <code>false</code> otherwise.
	 * 
	 * @return <code>true</code> if this numeric term represents positive
	 *         infinity, <code>false</code> otherwise.
	 */
	public boolean isPositiveInfinity();

	/**
	 * Returns <code>true</code> if this numeric term represents negative
	 * infinity, <code>false</code> otherwise.
	 * 
	 * @return <code>true</code> if this numeric term represents negative
	 *         infinity, <code>false</code> otherwise.
	 */
	public boolean isNegativeInfinity();
	
}
